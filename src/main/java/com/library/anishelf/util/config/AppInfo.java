package com.library.anishelf.util.config;

public class AppInfo {
    private int volume;
    private String Mode;
    private String Password;

    public AppInfo(String Mode, int volume, String Password) {
        this.Mode = Mode;
        this.volume = volume;
        this.Password = Password;
    }
    public int getVolume() {
        return volume;
    }
    public void setVolume(int volume) {
        this.volume = volume;
    }
    public String getMode() {
        return Mode;
    }
    public void setMode(String Mode) {
        this.Mode = Mode;
    }
    public String getPassword() {
        return Password;
    }
    public void setPassword(String Password) {
        this.Password = Password;
    }

    public String toText() {
        return String.format("%s\n%d\n%s\n", Mode, volume, Password);
    }

    public static AppInfo fromText(String text) {
        if (text == null || text.trim().isEmpty()) {
            // Trả về cấu hình mặc định nếu text là null hoặc rỗng
            return new AppInfo("light", 50, "");
        }
        
        try {
            String[] parts = text.split("\n");
            if (parts.length < 3) {
                // Không đủ thông tin, trả về cấu hình mặc định
                return new AppInfo("light", 50, "");
            }
            
            String mode = parts[0];
            int volume = Integer.parseInt(parts[1]);
            String Password = parts[2];
            return new AppInfo(mode, volume, Password);
        } catch (NumberFormatException e) {
            // Xử lý lỗi chuyển đổi số nguyên
            return new AppInfo("light", 50, "");
        } catch (Exception e) {
            // Xử lý các lỗi khác có thể xảy ra
            return new AppInfo("light", 50, "");
        }
    }
}
