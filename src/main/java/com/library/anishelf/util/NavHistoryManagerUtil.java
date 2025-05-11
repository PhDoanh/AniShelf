package com.library.anishelf.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Quản lý lịch sử điều hướng để hỗ trợ chức năng Back/Next
 */
public class NavHistoryManagerUtil {
    
    private static NavHistoryManagerUtil instance;
    private List<String> history;
    private int currentIndex;
    
    /**
     * Khởi tạo lịch sử điều hướng
     */
    private NavHistoryManagerUtil() {
        history = new ArrayList<>();
        currentIndex = -1;
    }
    
    /**
     * Lấy đối tượng quản lý lịch sử điều hướng (Singleton)
     * @return Đối tượng NavHistoryManagerUtil
     */
    public static NavHistoryManagerUtil getInstance() {
        if (instance == null) {
            instance = new NavHistoryManagerUtil();
        }
        return instance;
    }
    
    /**
     * Thêm một trang vào lịch sử điều hướng
     * @param pagePath Đường dẫn đến trang
     */
    public void addToHistory(String pagePath) {
        // Xóa tất cả các trang phía trước nếu người dùng đã di chuyển lùi
        if (currentIndex < history.size() - 1) {
            history = new ArrayList<>(history.subList(0, currentIndex + 1));
        }
        
        // Nếu trang hiện tại giống trang cuối cùng trong lịch sử, không thêm vào
        if (!history.isEmpty() && history.get(currentIndex).equals(pagePath)) {
            return;
        }
        
        history.add(pagePath);
        currentIndex = history.size() - 1;
    }
    
    /**
     * Kiểm tra xem có thể di chuyển lùi không
     * @return true nếu có thể lùi, false nếu không
     */
    public boolean canGoBack() {
        return currentIndex > 0;
    }
    
    /**
     * Kiểm tra xem có thể di chuyển tiến không
     * @return true nếu có thể tiến, false nếu không
     */
    public boolean canGoForward() {
        return currentIndex < history.size() - 1;
    }
    
    /**
     * Di chuyển lùi một trang
     * @return Đường dẫn đến trang trước
     */
    public String goBack() {
        if (canGoBack()) {
            currentIndex--;
            return history.get(currentIndex);
        }
        return null;
    }
    
    /**
     * Di chuyển tiến một trang
     * @return Đường dẫn đến trang kế tiếp
     */
    public String goForward() {
        if (canGoForward()) {
            currentIndex++;
            return history.get(currentIndex);
        }
        return null;
    }
    
    /**
     * Xóa lịch sử điều hướng
     */
    public void clearHistory() {
        history.clear();
        currentIndex = -1;
    }
    
    /**
     * Lấy trang hiện tại
     * @return Đường dẫn đến trang hiện tại
     */
    public String getCurrentPage() {
        if (currentIndex >= 0 && currentIndex < history.size()) {
            return history.get(currentIndex);
        }
        return null;
    }
    
    /**
     * Lấy toàn bộ lịch sử điều hướng
     * @return Danh sách các trang đã điều hướng
     */
    public List<String> getHistory() {
        return new ArrayList<>(history);
    }
}