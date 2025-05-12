package com.library.anishelf.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.library.anishelf.util.RuntimeDebugUtil;
import javafx.scene.image.Image;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Lớp tiện ích quản lý cache sử dụng thư viện Caffeine.
 * Cung cấp các phương thức tạo và quản lý cache cho toàn bộ ứng dụng.
 */
public class CacheManagerUtil {
    private static final RuntimeDebugUtil logger = RuntimeDebugUtil.getInstance();
    private static final String TAG = "CacheManagerUtil";

    // Cache cho hình ảnh, sử dụng Caffeine thay cho MySQL LRUCache
    private static final Cache<String, Image> imageCache = Caffeine.newBuilder()
            .maximumSize(200)  // Giữ nguyên kích thước như ImageCache ban đầu
            .expireAfterAccess(Duration.ofMinutes(30))
            .recordStats()
            .build();

    /**
     * Lấy instance của image cache
     *
     * @return Cache instance cho hình ảnh
     */
    public static Cache<String, Image> getImageCache() {
        return imageCache;
    }

    /**
     * Lấy hình ảnh từ cache
     *
     * @param key Đường dẫn hình ảnh
     * @return Hình ảnh nếu có trong cache, null nếu không có
     */
    public static Image getImageFromCache(String key) {
        return imageCache.getIfPresent(key);
    }

    /**
     * Lưu hình ảnh vào cache
     *
     * @param key   Đường dẫn hình ảnh
     * @param image Đối tượng hình ảnh cần lưu
     */
    public static void putImageToCache(String key, Image image) {
        imageCache.put(key, image);
    }

    /**
     * Xóa toàn bộ cache hình ảnh
     */
    public static void clearImageCache() {
        imageCache.invalidateAll();
        logger.debug(TAG, "Đã xóa toàn bộ cache hình ảnh");
    }

    /**
     * Tạo một cache mới với kích thước tối đa
     *
     * @param <K>         Kiểu dữ liệu của khóa
     * @param <V>         Kiểu dữ liệu của giá trị
     * @param maximumSize Kích thước tối đa của cache
     * @return Cache đã được cấu hình
     */
    public static <K, V> Cache<K, V> buildCache(int maximumSize) {
        logger.debug(TAG, "Tạo cache mới với kích thước: " + maximumSize);
        return Caffeine.newBuilder()
                .maximumSize(maximumSize)
                .build();
    }

    /**
     * Tạo một cache mới với kích thước tối đa và thời gian hết hạn
     *
     * @param <K>               Kiểu dữ liệu của khóa
     * @param <V>               Kiểu dữ liệu của giá trị
     * @param maximumSize       Kích thước tối đa của cache
     * @param expireAfterAccess Thời gian hết hạn sau khi truy cập (phút)
     * @return Cache đã được cấu hình
     */
    public static <K, V> Cache<K, V> buildCache(int maximumSize, int expireAfterAccess) {
        logger.debug(TAG, "Tạo cache mới với kích thước: " + maximumSize +
                ", thời gian hết hạn: " + expireAfterAccess + " phút");
        return Caffeine.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterAccess(Duration.ofMinutes(expireAfterAccess))
                .build();
    }

    /**
     * Tạo một cache với cấu hình nâng cao
     *
     * @param <K>               Kiểu dữ liệu của khóa
     * @param <V>               Kiểu dữ liệu của giá trị
     * @param maximumSize       Kích thước tối đa của cache
     * @param expireAfterAccess Thời gian hết hạn sau khi truy cập
     * @param expireAfterWrite  Thời gian hết hạn sau khi ghi
     * @param timeUnit          Đơn vị thời gian
     * @return Cache đã được cấu hình
     */
    public static <K, V> Cache<K, V> buildAdvancedCache(
            int maximumSize,
            long expireAfterAccess,
            long expireAfterWrite,
            TimeUnit timeUnit) {

        logger.debug(TAG, "Tạo cache nâng cao với kích thước: " + maximumSize);

        return Caffeine.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterAccess(expireAfterAccess, timeUnit)
                .expireAfterWrite(expireAfterWrite, timeUnit)
                .recordStats()
                .build();
    }

    /**
     * Lấy thống kê cơ bản về cache
     *
     * @param <K>   Kiểu dữ liệu của khóa
     * @param <V>   Kiểu dữ liệu của giá trị
     * @param cache Cache cần lấy thống kê
     * @return Chuỗi thông tin thống kê
     */
    public static <K, V> String getCacheStats(Cache<K, V> cache) {
        long estimatedSize = cache.estimatedSize();
        return "Kích thước ước tính: " + estimatedSize;
    }
}