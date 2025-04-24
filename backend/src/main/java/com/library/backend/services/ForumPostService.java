package com.library.backend.services;

import com.library.backend.models.ForumPost;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;

/**
 * Service for handling forum posts
 * Note: This is a mock implementation as we don't have actual Discord API integration yet
 */
public class ForumPostService {
    private List<ForumPost> cachedPosts;
    private Random random;
    
    public ForumPostService() {
        this.cachedPosts = new ArrayList<>();
        this.random = new Random();
        // Initialize with mock data for development
        initializeMockData();
    }
    
    /**
     * Get recent forum posts
     * 
     * @param limit Maximum number of posts to return
     * @return List of recent forum posts
     */
    public List<ForumPost> getRecentPosts(int limit) {
        // In a real implementation, this would fetch from Discord API
        // For now, return mock data
        return cachedPosts.subList(0, Math.min(limit, cachedPosts.size()));
    }
    
    /**
     * Initialize mock data for development
     * In production, this would be replaced with actual API calls
     */
    private void initializeMockData() {
        // Create dates for different time periods
        LocalDateTime now = LocalDateTime.now();
        
        // Add some recent posts with different timestamps
        LocalDateTime time1 = now.minus(30, ChronoUnit.MINUTES);
        cachedPosts.add(new ForumPost("post1", "Tìm manga mới ra", 
                        "Có ai biết manga mới nào hay không? Tôi đang tìm một bộ mới để đọc.", 
                        "otaku123", "discussion", time1));
        
        LocalDateTime time2 = now.minus(2, ChronoUnit.HOURS);
        cachedPosts.add(new ForumPost("post2", "Review One Piece tập mới", 
                        "Tập mới của One Piece thật tuyệt vời! Ai đã đọc rồi?", 
                        "luffy_fan", "review", time2));
        
        LocalDateTime time3 = now.minus(5, ChronoUnit.HOURS);
        cachedPosts.add(new ForumPost("post3", "Truyện tranh yêu thích", 
                        "Bộ truyện tranh yêu thích của bạn là gì? Tôi thích Naruto và One Piece.", 
                        "manga_lover", "discussion", time3));
        
        LocalDateTime time4 = now.minus(1, ChronoUnit.DAYS);
        cachedPosts.add(new ForumPost("post4", "Thảo luận về Dragon Ball", 
                        "Dragon Ball có phải là một trong những bộ manga hay nhất mọi thời đại?", 
                        "goku4ever", "discussion", time4));
        
        LocalDateTime time5 = now.minus(2, ChronoUnit.DAYS);
        cachedPosts.add(new ForumPost("post5", "Đề xuất light novel", 
                        "Có ai có thể đề xuất một số light novel hay không? Tôi mới đọc được Sword Art Online và rất thích nó.", 
                        "light_reader", "recommendation", time5));
    }
    
    /**
     * Search forum posts by keyword in title or content
     * 
     * @param keyword Keyword to search for
     * @param limit Maximum number of results to return
     * @return List of matching forum posts
     */
    public List<ForumPost> searchPosts(String keyword, int limit) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String lowerKeyword = keyword.toLowerCase().trim();
        List<ForumPost> results = new ArrayList<>();
        
        for (ForumPost post : cachedPosts) {
            if ((post.getTitle() != null && post.getTitle().toLowerCase().contains(lowerKeyword)) || 
                (post.getContent() != null && post.getContent().toLowerCase().contains(lowerKeyword))) {
                results.add(post);
                if (results.size() >= limit) {
                    break;
                }
            }
        }
        
        return results;
    }
}