package com.library.anishelf.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Lớp tiện ích để gỡ lỗi thời gian chạy của ứng dụng.
 * Lớp này cho phép ghi log ra cả terminal và file log.
 * Sử dụng cơ chế Singleton để đảm bảo chỉ có một instance trong toàn ứng dụng.
 */
public class RuntimeDebugUtil {
    // Hằng số cho các level log
    public static final int LEVEL_DEBUG = 0;
    public static final int LEVEL_INFO = 1;
    public static final int LEVEL_WARNING = 2;
    public static final int LEVEL_ERROR = 3;
    
    // Hằng số cho đường dẫn file log
    private static final String LOG_DIRECTORY = "logs";
    private static final String DEFAULT_LOG_FILE = "application.log";

    // Instance singleton
    private static RuntimeDebugUtil instance;
    
    // Level log hiện tại, mặc định là DEBUG (log tất cả)
    private int currentLogLevel = LEVEL_DEBUG;
    
    // Đường dẫn đến file log
    private String logFilePath;
    
    // Flag để kiểm soát việc ghi ra file
    private boolean logToFile = true;
    
    // Flag để kiểm soát việc ghi ra terminal
    private boolean logToConsole = true;
    
    // Format thời gian cho log
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    // Queue và luồng xử lý log bất đồng bộ
    private BlockingQueue<String> logQueue = new LinkedBlockingQueue<>();
    private Thread loggerThread;
    private boolean isRunning = true;

    /**
     * Constructor riêng để thực hiện Singleton.
     * Khởi tạo đường dẫn file log và bắt đầu luồng logger.
     */
    private RuntimeDebugUtil() {
        // Tạo thư mục logs nếu chưa tồn tại
        try {
            File logDir = new File(LOG_DIRECTORY);
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            
            // Khởi tạo đường dẫn file log
            logFilePath = LOG_DIRECTORY + File.separator + DEFAULT_LOG_FILE;
            
            // Khởi tạo luồng xử lý log
            startLoggerThread();
        } catch (Exception e) {
            System.err.println("Không thể khởi tạo RuntimeDebugUtil: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Bắt đầu luồng xử lý log bất đồng bộ
     */
    private void startLoggerThread() {
        loggerThread = new Thread(() -> {
            while (isRunning) {
                try {
                    String logMessage = logQueue.take(); // Chờ cho đến khi có log message
                    writeLogToFile(logMessage);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("Lỗi trong luồng logger: " + e.getMessage());
                }
            }
        });
        loggerThread.setDaemon(true); // Để luồng tự động kết thúc khi ứng dụng đóng
        loggerThread.start();
    }

    /**
     * Lấy instance của RuntimeDebugUtil (Singleton pattern)
     * 
     * @return Instance duy nhất của RuntimeDebugUtil
     */
    public static synchronized RuntimeDebugUtil getInstance() {
        if (instance == null) {
            instance = new RuntimeDebugUtil();
        }
        return instance;
    }

    /**
     * Thiết lập level log tối thiểu sẽ được hiển thị
     * 
     * @param level Level log (DEBUG, INFO, WARNING, ERROR)
     */
    public void setLogLevel(int level) {
        if (level >= LEVEL_DEBUG && level <= LEVEL_ERROR) {
            this.currentLogLevel = level;
        } else {
            warning("setLogLevel", "Level log không hợp lệ: " + level + ", sử dụng mặc định: DEBUG");
        }
    }

    /**
     * Bật/tắt ghi log ra file
     * 
     * @param enable true để bật, false để tắt
     */
    public void enableFileLogging(boolean enable) {
        this.logToFile = enable;
    }

    /**
     * Bật/tắt hiển thị log trên terminal
     * 
     * @param enable true để bật, false để tắt
     */
    public void enableConsoleLogging(boolean enable) {
        this.logToConsole = enable;
    }

    /**
     * Đặt đường dẫn tùy chỉnh cho file log
     * 
     * @param filePath Đường dẫn đến file log
     */
    public void setLogFile(String filePath) {
        if (filePath != null && !filePath.trim().isEmpty()) {
            this.logFilePath = filePath;
        } else {
            warning("setLogFile", "Đường dẫn file không hợp lệ, sử dụng mặc định: " + this.logFilePath);
        }
    }

    /**
     * Ghi log debug
     * 
     * @param tag Tag để phân loại log
     * @param message Nội dung log
     */
    public void debug(String tag, String message) {
        log(LEVEL_DEBUG, tag, message);
    }

    /**
     * Ghi log thông tin
     * 
     * @param tag Tag để phân loại log
     * @param message Nội dung log
     */
    public void info(String tag, String message) {
        log(LEVEL_INFO, tag, message);
    }

    /**
     * Ghi log cảnh báo
     * 
     * @param tag Tag để phân loại log
     * @param message Nội dung log
     */
    public void warning(String tag, String message) {
        log(LEVEL_WARNING, tag, message);
    }

    /**
     * Ghi log lỗi
     * 
     * @param tag Tag để phân loại log
     * @param message Nội dung log
     */
    public void error(String tag, String message) {
        log(LEVEL_ERROR, tag, message);
    }

    /**
     * Ghi log lỗi với exception
     * 
     * @param tag Tag để phân loại log
     * @param message Nội dung log
     * @param e Exception cần ghi lại
     */
    public void error(String tag, String message, Throwable e) {
        StringBuilder sb = new StringBuilder(message);
        sb.append("\nStacktrace: ");
        
        // Chuyển đổi stacktrace thành chuỗi
        StackTraceElement[] stackTrace = e.getStackTrace();
        for (StackTraceElement element : stackTrace) {
            sb.append("\n\tat ").append(element.toString());
        }
        
        log(LEVEL_ERROR, tag, sb.toString());
    }

    /**
     * Phương thức chung để ghi log
     * 
     * @param level Level của log
     * @param tag Tag để phân loại log
     * @param message Nội dung log
     */
    private void log(int level, String tag, String message) {
        // Nếu level không đủ điều kiện thì bỏ qua
        if (level < currentLogLevel) {
            return;
        }

        // Tạo chuỗi log có định dạng
        String levelStr;
        switch (level) {
            case LEVEL_DEBUG:
                levelStr = "DEBUG";
                break;
            case LEVEL_INFO:
                levelStr = "INFO";
                break;
            case LEVEL_WARNING:
                levelStr = "WARNING";
                break;
            case LEVEL_ERROR:
                levelStr = "ERROR";
                break;
            default:
                levelStr = "UNKNOWN";
        }

        String timestamp = dateFormat.format(new Date());
        String logMessage = String.format("%s [%s] %s: %s", timestamp, levelStr, tag, message);

        // Hiển thị log trên terminal nếu được bật
        if (logToConsole) {
            if (level == LEVEL_ERROR) {
                System.err.println(logMessage);
            } else {
                System.out.println(logMessage);
            }
        }

        // Thêm vào queue để ghi ra file bất đồng bộ
        if (logToFile) {
            try {
                logQueue.put(logMessage);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Không thể thêm log vào queue: " + e.getMessage());
            }
        }
    }

    /**
     * Ghi log vào file
     * 
     * @param logMessage Nội dung log cần ghi
     */
    private void writeLogToFile(String logMessage) {
        try (FileWriter fw = new FileWriter(logFilePath, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(logMessage);
        } catch (IOException e) {
            System.err.println("Lỗi khi ghi vào file log: " + e.getMessage());
        }
    }

    /**
     * Đóng và giải phóng tài nguyên khi kết thúc ứng dụng
     */
    public void shutdown() {
        // Dừng luồng xử lý log
        isRunning = false;
        loggerThread.interrupt();
        
        // Xử lý các log còn lại trong queue
        while (!logQueue.isEmpty()) {
            try {
                String logMessage = logQueue.take();
                writeLogToFile(logMessage);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    /**
     * Xóa file log hiện tại và tạo file mới
     */
    public void clearLogs() {
        try {
            File logFile = new File(logFilePath);
            if (logFile.exists()) {
                logFile.delete();
                logFile.createNewFile();
                info("RuntimeDebugUtil", "File log đã được xóa và tạo mới");
            }
        } catch (IOException e) {
            error("RuntimeDebugUtil", "Không thể xóa file log", e);
        }
    }
    
    /**
     * Tạo file log mới với timestamp
     */
    public void createNewLogWithTimestamp() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String newLogPath = LOG_DIRECTORY + File.separator + "application_" + timestamp + ".log";
        info("RuntimeDebugUtil", "Tạo file log mới: " + newLogPath);
        setLogFile(newLogPath);
    }
}