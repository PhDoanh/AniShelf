package com.library.anishelf.controller;

import com.library.anishelf.dao.BookItemDAO;
import com.library.anishelf.dao.BookReservationDAO;
import com.library.anishelf.model.BookIssue;
import com.library.anishelf.model.BookItem;
import com.library.anishelf.model.BookReservation;
import com.library.anishelf.model.enums.BookItemStatus;
import com.library.anishelf.model.enums.BookReservationStatus;
import com.library.anishelf.service.BookService;
import com.library.anishelf.util.SceneManagerUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class ReservedBorrowedHistoryPageController implements Initializable {
    @FXML
    private VBox contentBox;
    @FXML
    private HBox borrowingHBox;
    @FXML
    private HBox borrowedHBox;
    @FXML
    private HBox reservedHBox;

    private SceneManagerUtil sceneManagerUtil = SceneManagerUtil.getInstance();

    private List<BookReservation> bookReservationList = new ArrayList<>();
    private List<BookIssue> bookBorrowingList = new ArrayList<>();
    private List<BookIssue> bookBorrowedList = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            bookReservationList = BookService.getInstance().getPendingReservedBooks();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            bookBorrowedList = BookService.getInstance().getReturnedBooks();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        try {
            bookBorrowingList = BookService.getInstance().getCurrentlyBorrowedBooks();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < bookBorrowingList.size(); i++) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/view/VerticalTypeBookCard.fxml"));
                VBox cardBox = fxmlLoader.load();
                VerticalTypeBookCardController cardController = fxmlLoader.getController();
                cardController.setData(bookBorrowingList.get(i).getBookItem());
                borrowingHBox.getChildren().add(cardBox);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < bookReservationList.size(); i++) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/view/VerticalTypeBookCard.fxml"));
                VBox cardBox = fxmlLoader.load();
                VerticalTypeBookCardController cardController = fxmlLoader.getController();
                cardController.setData(bookReservationList.get(i).getBookItem());
                cardController.setReservedBook(this,bookReservationList.get(i).getBookItem());
                reservedHBox.getChildren().add(cardBox);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < bookBorrowedList.size(); i++) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/view/VerticalTypeBookCard.fxml"));
                VBox cardBox = fxmlLoader.load();
                VerticalTypeBookCardController cardController = fxmlLoader.getController();
                cardController.setData(bookBorrowedList.get(i).getBookItem());
                borrowedHBox.getChildren().add(cardBox);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * thêm sách đặt trước vào vị trí.
     * @param bookReservation sách đặt trước
     * @throws IOException ném ngoại lệ
     */
    public void addReservedBook(BookReservation bookReservation) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/VerticalTypeBookCard.fxml"));
        VBox cardBox = fxmlLoader.load();
        VerticalTypeBookCardController cardController = fxmlLoader.getController();
        cardController.setData(bookReservation.getBookItem());
        bookReservationList.add(bookReservation);
        cardController.setReservedBook(this,bookReservation.getBookItem());
        System.out.println("add and size =" + bookReservationList.size());
        reservedHBox.getChildren().add(cardBox);
    }

    /**
     * xoá sách đặt trước.
     * @param bookItem sách
     * @param vBox hộp chứa sách
     * @throws IOException ngoại lệ
     */
    public void deleteBookReserved(BookItem bookItem,VBox vBox) throws IOException {
        if(!CustomerAlter.showAlter("Bạn huỷ đặt trước sách này?")) {
            return;
        }
        Map<String,Object> criteria = new HashMap<>();
        criteria.put("member_ID", NavigationBarController.getMember().getPerson().getId());
        criteria.put("barcode",bookItem.getBookBarcode());
        criteria.put("BookReservationStatus",BookReservationStatus.WAITING);

        int index = findBookReserved(bookItem.getBookBarcode());

        try {
            List<BookReservation> bookReservations = BookReservationDAO.getInstance().findByCriteria(criteria);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if(index!=-1) {
            try {
                List<BookReservation> bookReservations = BookReservationDAO.getInstance().findByCriteria(criteria);
                if(bookReservations.size()>0) {
                    bookItem.setBookItemStatus(BookItemStatus.AVAILABLE);
                    BookItemDAO.getInstance().updateEntity(bookItem);
                    BookReservation bookReservation = BookReservationDAO.getInstance().findById(bookReservations.getFirst().getId());
                    bookReservation.setReservationStatus(BookReservationStatus.CANCELED);
                    BookReservationDAO.getInstance().updateEntity(bookReservation);
                    bookReservationList.remove(index);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        reservedHBox.getChildren().remove(vBox);
    }

    /**
     * tìm sách theo barCode
     * @param barCode barCode
     * @return sách
     */
    private int findBookReserved(long barCode) {
        try {
            bookReservationList = BookService.getInstance().getPendingReservedBooks();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0;i<bookReservationList.size();i++) {
            if (bookReservationList.get(i).getBookItem().getBookBarcode() == barCode) {
                return i;
            }
        }
        return -1;
    }
}
