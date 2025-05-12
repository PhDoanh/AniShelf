package com.library.anishelf.util.config;

import java.io.*;

/**
 * The type Usr config util.
 */
public class UsrConfigUtil {
    private static UsrConfigUtil instance;
    private static final String DEFAULT_CONFIG_PATH = "/config/usr_config.txt";
    private String filePath;

    private UsrConfigUtil() {
        // File trong thư mục người dùng để lưu cấu hình cá nhân
        filePath = System.getProperty("user.home") + "/.anishelf/config/usr_config.txt";

        // Đảm bảo thư mục tồn tại
        File configDir = new File(System.getProperty("user.home") + "/.anishelf/config");
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        // Kiểm tra xem file cấu hình đã tồn tại chưa
        File configFile = new File(filePath);
        if (!configFile.exists()) {
            // Sao chép từ file mẫu trong dự án
            copyDefaultConfig(configFile);
        }
    }

    private void copyDefaultConfig(File targetFile) {
        try {
            InputStream defaultConfig = getClass().getResourceAsStream(DEFAULT_CONFIG_PATH);

            if (defaultConfig != null) {
                System.out.println("Đã tìm thấy file mẫu trong resources: " + DEFAULT_CONFIG_PATH);

                // Tạo file đích nếu chưa tồn tại
                if (!targetFile.exists()) {
                    targetFile.createNewFile();
                }

                // Sao chép nội dung từ file mẫu sang file đích
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(defaultConfig));
                     BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile))) {

                    String line;
                    boolean hasContent = false;

                    while ((line = reader.readLine()) != null) {
                        writer.write(line);
                        writer.newLine();
                        hasContent = true;
                    }

                    // Nếu file mẫu không có nội dung, thêm một nội dung mặc định
                    if (!hasContent) {
                        System.out.println("File mẫu rỗng, tạo nội dung mặc định");
                        // Tạo một nội dung mẫu nếu file không có dữ liệu
                        writer.write("default light");
                        writer.newLine();
                    }
                }

                System.out.println("Đã sao chép thành công file cấu hình từ mẫu tới: " + targetFile.getAbsolutePath());
            } else {
                System.err.println("Không tìm thấy file mẫu trong resources: " + DEFAULT_CONFIG_PATH);

                // Tạo file với nội dung mặc định nếu không tìm thấy file mẫu
                targetFile.createNewFile();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile))) {
                    writer.write("default light");
                    writer.newLine();
                }

                System.out.println("Đã tạo file cấu hình mặc định tại: " + targetFile.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi tạo file cấu hình: " + e.getMessage());
            e.printStackTrace();

            // Thử tạo file trống với nội dung mặc định
            try {
                targetFile.createNewFile();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile))) {
                    writer.write("default light");
                    writer.newLine();
                }
                System.out.println("Đã tạo file cấu hình mặc định sau lỗi tại: " + targetFile.getAbsolutePath());
            } catch (IOException ex) {
                System.err.println("Không thể tạo file cấu hình sau lỗi: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static UsrConfigUtil getInstance() {
        if (instance == null) {
            instance = new UsrConfigUtil();
        }
        return instance;
    }

    /**
     * Write user info to file.
     *
     * @param id    the id
     * @param color the color
     */
    public void writeUserInfoToFile(String id, String color) {
        UsrInfo user = new UsrInfo(id, color);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            // Ghi thông tin user vào file
            writer.write(user.toFileString());
            writer.newLine(); // Thêm dòng mới
            System.out.println("Dữ liệu đã được ghi vào file!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Find user by id usr info.
     *
     * @param searchId the search id
     * @return the usr info
     */
    public UsrInfo findUserById(String searchId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length >= 2) {
                    String id = parts[0];
                    String color = parts[1];

                    UsrInfo user = new UsrInfo(id, color);

                    if (user.matchesId(searchId)) {
                        return user;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading from the file.");
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Update user info.
     *
     * @param updatedUser the updated user
     */
    public void updateUserInfo(UsrInfo updatedUser) {
        try {
            // Đọc tất cả thông tin cũ vào bộ nhớ
            StringBuilder fileContent = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            boolean userFound = false;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length >= 1) {
                    String id = parts[0];

                    // Nếu ID trùng với người dùng cần cập nhật, thay đổi thông tin
                    if (id.equals(updatedUser.getId())) {
                        fileContent.append(updatedUser.toFileString()).append("\n");
                        userFound = true;
                    } else {
                        fileContent.append(line).append("\n");
                    }
                } else {
                    // Nếu dòng không hợp lệ, vẫn giữ nguyên
                    fileContent.append(line).append("\n");
                }
            }
            reader.close();

            // Nếu không tìm thấy user, thêm mới
            if (!userFound) {
                fileContent.append(updatedUser.toFileString()).append("\n");
            }

            // Ghi lại tất cả nội dung đã cập nhật vào file
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(fileContent.toString());
            writer.close();

        } catch (IOException e) {
            System.out.println("An error occurred while updating the file.");
            e.printStackTrace();
        }
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        //UsrConfigUtil.getInstance().writeUserInfoToFile("123","hehe");
        System.out.println(UsrConfigUtil.getInstance().findUserById("123").getColor());
        UsrConfigUtil.getInstance().updateUserInfo(new UsrInfo("123", "rea"));
        System.out.println(UsrConfigUtil.getInstance().findUserById("123").getColor());
    }
}
