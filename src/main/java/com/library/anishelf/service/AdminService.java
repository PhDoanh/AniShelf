package com.library.anishelf.service;

import com.library.anishelf.controller.CustomerAlter;
import com.library.anishelf.dao.*;
import com.library.anishelf.model.*;
import com.library.anishelf.model.enums.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminService implements ServiceHandler {
    private String operationType;
    private Object payload;
    private Member memberResponse;
    private Book bookResponse;
    private BookDAO bookDAO;
    private BookItem bookItemResponse;
    
    public AdminService(String operationType, Object payload) {
        this.operationType = operationType;
        this.payload = payload;
        bookDAO = BookDAO.getInstance();
    }

    public Member getMemberResponse() {
        return memberResponse;
    }

    public Book getBookResponse() {
        return bookResponse;
    }

    public BookItem getBookItemResponse() {
        return bookItemResponse;
    }

    
    public boolean handleRequest() {
        try {
            switch (operationType) {
                case "add":
                    if (payload instanceof Book) {
                        BookDAO.getInstance().insert((Book) payload);
                        return true;
                    } else if (payload instanceof Member) {
                        MemberDAO.getInstance().insert((Member) payload);
                        return true;
                    } else if (payload instanceof BookReservation) {
                        BookReservationDAO.getInstance().insert((BookReservation) payload);
                        return true;
                    } else if (payload instanceof BookIssue) {
                            Map<String, Object> findCriteria = new HashMap<>();
                            findCriteria.put("BookReservationStatus", BookReservationStatus.WAITING);
                            findCriteria.put("member_ID", ((BookIssue) payload).getMember().getPerson().getId());
                            findCriteria.put("barcode", ((BookIssue) payload).getBookItem().getBookBarcode());
                            List<BookReservation> bookReservationsList = BookReservationDAO.getInstance().findByCriteria(findCriteria);
                            if (bookReservationsList.size() > 0) {
                                BookIssue newBookIssue = new BookIssue(((BookIssue) payload).getMember(),bookReservationsList.getFirst().getBookItem(),((BookIssue) payload).getIssueDate(),((BookIssue) payload).getDueDate());
                                BookIssueDAO.getInstance().insert(newBookIssue);
                                bookReservationsList.getFirst().setReservationStatus(BookReservationStatus.COMPLETED);
                                BookReservationDAO.getInstance().updateEntity(bookReservationsList.getFirst());
                                return true;
                            } else  if(BookDAO.getInstance().findById(((BookIssue) payload).getBookItem().getIsbn()).getstatus() == BookStatus.AVAILABLE)  {
                                BookIssueDAO.getInstance().insert((BookIssue) payload);
                                return true;
                            } else {
                                CustomerAlter.showMessage("Đã hết sách :<");
                                return false;
                            }
                    }
                    return false;
                case "delete":
                    if (payload instanceof Book) {
                        BookDAO.getInstance().deleteEntity((Book) payload);
                        return true;
                    } else
                    if (payload instanceof Member) {
                        MemberDAO.getInstance().deleteEntity((Member) payload);
                        return true;
                    } else if(payload instanceof BookReservation) {
                        BookItem bookItem = ((BookReservation) payload).getBookItem();
                        bookItem.setBookItemStatus(BookItemStatus.AVAILABLE);
                        BookItemDAO.getInstance().updateEntity(bookItem);
                        BookReservation bookReservation = (BookReservation) payload;
                        bookReservation.setReservationStatus(BookReservationStatus.CANCELED);
                        BookReservationDAO.getInstance().updateEntity(bookReservation);
                        BookReservationDAO.getInstance().deleteEntity((BookReservation) payload);
                        return true;
                    } else if(payload instanceof BookIssue) {
                        BookItem bookItem = ((BookIssue) payload).getBookItem();
                        bookItem.setBookItemStatus(BookItemStatus.AVAILABLE);
                        BookItemDAO.getInstance().updateEntity(bookItem);
                        BookIssueDAO.getInstance().deleteEntity((BookIssue) payload);
                        return true;
                    }
                    return false;
                case "edit":
                    if (payload instanceof Book) {
                        Book book = (Book) payload;
                        bookDAO.updateEntity((Book) payload);
                        return true;
                    } else if (payload instanceof Member) {
                        MemberDAO.getInstance().updateEntity((Member) payload);
                        return true;
                    } else if(payload instanceof BookIssue) {
                        BookIssueDAO.getInstance().updateEntity((BookIssue) payload);
                        return true;
                    } else if(payload instanceof BookReservation) {
                        BookReservationDAO.getInstance().updateEntity((BookReservation) payload);
                        return true;
                    }
                    
                    return false;
                case "block":
                    if (payload instanceof Member) {
                        Member member = (Member) payload;
                        member.setStatus(AccountStatus.CLOSED);
                    }
                    return true;
                case "unblock":
                    if (payload instanceof Member) {
                        Member member = (Member) payload;
                        member.setStatus(AccountStatus.ACTIVE);
                    }
                    return true;
                case "find":
                    if (payload instanceof Member) {
                        Member member = (Member) payload;
                        this.memberResponse = MemberDAO.getInstance().findById(member.getAccId());
                    }
                    if (payload instanceof BookItem) {
                        BookItem bookItem = (BookItem) payload;
                        this.bookItemResponse = BookItemDAO.getInstance().findById(bookItem.getBookBarcode());
                    }
                    return true;
                default:
                    return false;
            }
        } catch (
                SQLException e) {
            System.out.println("Lỗi AdminService:" + e.getMessage());
            CustomerAlter.showAlter(e.getMessage());
            return false; 
        }
    }
}
