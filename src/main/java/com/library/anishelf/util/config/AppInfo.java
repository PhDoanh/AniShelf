package com.library.anishelf.util.config;

/**
 * The type App info.
 */
public class AppInfo {
    private int volume;
    private String Mode;
    private String Password;

    /**
     * Instantiates a new App info.
     *
     * @param Mode     the mode
     * @param volume   the volume
     * @param Password the password
     */
    public AppInfo(String Mode, int volume, String Password) {
        this.Mode = Mode;
        this.volume = volume;
        this.Password = Password;
    }

    /**
     * Gets volume.
     *
     * @return the volume
     */
    public int getVolume() {
        return volume;
    }

    /**
     * Sets volume.
     *
     * @param volume the volume
     */
    public void setVolume(int volume) {
        this.volume = volume;
    }

    /**
     * Gets mode.
     *
     * @return the mode
     */
    public String getMode() {
        return Mode;
    }

    /**
     * Sets mode.
     *
     * @param Mode the mode
     */
    public void setMode(String Mode) {
        this.Mode = Mode;
    }

    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() {
        return Password;
    }

    /**
     * Sets password.
     *
     * @param Password the password
     */
    public void setPassword(String Password) {
        this.Password = Password;
    }

    /**
     * To text string.
     *
     * @return the string
     */
    public String toText() {
        return String.format("%s\n%d\n%s\n", Mode, volume, Password);
    }

    /**
     * From text app info.
     *
     * @param text the text
     * @return the app info
     */
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
