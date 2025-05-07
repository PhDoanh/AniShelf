package com.library.anishelf.util.config;

import java.io.*;

public class AppConfigUtil {
    private static final String CONFIG_PATH = System.getProperty("user.home") + "/.anishelf/config/app_config.txt";

    public AppConfigUtil() {
        // Đảm bảo thư mục tồn tại
        File configDir = new File(System.getProperty("user.home") + "/.anishelf/config");
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
    }

    public void saveSettings(AppInfo info) {
        File configFile = new File(CONFIG_PATH);
        
        try {
            // Tạo thư mục nếu chưa tồn tại
            configFile.getParentFile().mkdirs();
            
            // Ghi dữ liệu
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
                writer.write(info.toText());
            }
            
            System.out.println("Settings saved successfully!");
        } catch (IOException e) {
            System.err.println("Error saving settings: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public AppInfo loadSettings() {
        File configFile = new File(CONFIG_PATH);
        
        if (!configFile.exists()) {
            // Tạo file mặc định nếu chưa tồn tại
            AppInfo defaultSettings = new AppInfo("Normal", 50, "chua co");
            saveSettings(defaultSettings);
            return defaultSettings;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            
            return AppInfo.fromText(content.toString());
        } catch (IOException e) {
            System.err.println("Error loading settings: " + e.getMessage());
            // Trả về cấu hình mặc định trong trường hợp lỗi
            return new AppInfo("Normal", 50, "chua co");
        }
    }
}
