package com.library.anishelf.service;

import com.library.anishelf.model.ForumComment;
import com.library.anishelf.model.ForumPost;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for interacting with Discord forum channels via JDA
 */
public class DiscordForumService {

    private static DiscordForumService instance;
    private JDA jda;
    private String guildId;
    private String forumChannelId;
    private boolean isConnected = false;

    // Cache để tránh gọi API liên tục
    private Map<String, ForumPost> postCache = new HashMap<>();
    private Map<String, List<ForumComment>> commentCache = new HashMap<>();
    
    private DiscordForumService() { }

    /**
     * Singleton instance getter
     */
    public static synchronized DiscordForumService getInstance() {
        if (instance == null) {
            instance = new DiscordForumService();
        }
        return instance;
    }

    /**
     * Kết nối đến Discord API với token và thiết lập guild/channel ID
     * @param token Discord Bot token
     * @param guildId ID của guild (server)
     * @param forumChannelId ID của forum channel
     * @return true nếu kết nối thành công
     */
    public boolean connect(String token, String guildId, String forumChannelId) {
        try {
            this.guildId = guildId;
            this.forumChannelId = forumChannelId;
            
            jda = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS)
                    .build();
            
            // Đợi JDA kết nối hoàn tất
            jda.awaitReady();
            isConnected = true;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            isConnected = false;
            return false;
        }
    }

    /**
     * Kiểm tra trạng thái kết nối
     */
    public boolean isConnected() {
        return isConnected && jda != null && jda.getStatus().isInit();
    }

    /**
     * Lấy danh sách bài đăng từ forum
     * @param limit Số lượng bài đăng tối đa muốn lấy
     * @return Danh sách các bài đăng
     */
    public List<ForumPost> getForumPosts(int limit) {
        if (!isConnected()) return Collections.emptyList();
        
        try {
            Guild guild = jda.getGuildById(guildId);
            if (guild == null) return Collections.emptyList();
            
            ForumChannel forumChannel = guild.getForumChannelById(forumChannelId);
            if (forumChannel == null) return Collections.emptyList();
            
            List<ForumPost> posts = new ArrayList<>();
            
            // Lấy danh sách active threads trong forum
            List<ThreadChannel> threads = new ArrayList<>(forumChannel.getThreadChannels());
            
            // Sắp xếp theo thời gian tạo, mới nhất lên đầu
            threads.sort(Comparator.comparing(ThreadChannel::getTimeCreated).reversed());
            
            // Giới hạn số lượng
            if (threads.size() > limit) {
                threads = threads.subList(0, limit);
            }
            
            for (ThreadChannel thread : threads) {
                // Kiểm tra cache trước khi lấy từ API
                if (postCache.containsKey(thread.getId())) {
                    posts.add(postCache.get(thread.getId()));
                    continue;
                }
                
                try {
                    // Lấy tin nhắn đầu tiên trong thread (bài đăng gốc)
                    // Sửa cách lấy tin nhắn từ thread theo API mới của JDA 5.0.0-beta.20
                    List<Message> messages = thread.getHistory().retrievePast(1).complete();
                    
                    if (!messages.isEmpty()) {
                        Message firstMessage = messages.get(0);
                        
                        // Convert từ Discord message sang model ForumPost
                        ForumPost post = createForumPostFromMessage(firstMessage, thread);
                        
                        // Cache lại kết quả
                        postCache.put(thread.getId(), post);
                        posts.add(post);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            return posts;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Lấy danh sách các bài đăng được ghim
     * @return Danh sách các bài ghim
     */
    public List<ForumPost> getPinnedPosts() {
        List<ForumPost> allPosts = getForumPosts(50); // Lấy một số lượng đủ lớn
        return allPosts.stream()
                .filter(ForumPost::isPinned)
                .collect(Collectors.toList());
    }

    /**
     * Lấy bài đăng theo ID
     * @param postId ID của bài đăng
     * @return Đối tượng ForumPost nếu tìm thấy, null nếu không
     */
    public ForumPost getForumPostById(String postId) {
        if (!isConnected()) return null;
        
        // Kiểm tra cache trước
        if (postCache.containsKey(postId)) {
            return postCache.get(postId);
        }
        
        try {
            Guild guild = jda.getGuildById(guildId);
            if (guild == null) return null;
            
            ThreadChannel thread = guild.getThreadChannelById(postId);
            if (thread == null) return null;
            
            // Lấy tin nhắn đầu tiên trong thread sử dụng API mới của JDA
            List<Message> messages = thread.getHistory().retrievePast(1).complete();
            
            if (!messages.isEmpty()) {
                Message firstMessage = messages.get(0);
                
                // Convert từ Discord message sang model ForumPost
                ForumPost post = createForumPostFromMessage(firstMessage, thread);
                
                // Cache lại kết quả
                postCache.put(thread.getId(), post);
                return post;
            }
            
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lấy danh sách bình luận của một bài đăng
     * @param postId ID của bài đăng
     * @return Danh sách bình luận
     */
    public List<ForumComment> getCommentsForPost(String postId) {
        if (!isConnected()) return Collections.emptyList();
        
        // Kiểm tra cache trước
        if (commentCache.containsKey(postId)) {
            return commentCache.get(postId);
        }
        
        try {
            Guild guild = jda.getGuildById(guildId);
            if (guild == null) return Collections.emptyList();
            
            ThreadChannel thread = guild.getThreadChannelById(postId);
            if (thread == null) return Collections.emptyList();
            
            List<ForumComment> comments = new ArrayList<>();
            
            // Sử dụng API mới để lấy tin nhắn (tối đa 50 tin nhắn)
            List<Message> messages = thread.getHistory().retrievePast(50).complete();
            
            // Bỏ qua message đầu tiên (đó là bài đăng gốc)
            if (messages.size() > 1) {
                for (int i = 1; i < messages.size(); i++) {
                    Message message = messages.get(i);
                    
                    // Convert tin nhắn thành object ForumComment
                    ForumComment comment = new ForumComment(
                            message.getId(),
                            message.getContentRaw(),
                            message.getAuthor().getName(),
                            message.getAuthor().getEffectiveAvatarUrl(),
                            convertToLocalDateTime(message.getTimeCreated()),
                            postId
                    );
                    
                    // Đếm reactions như số lượt "like"
                    comment.setLikeCount(message.getReactions().size());
                    
                    comments.add(comment);
                }
            }
            
            // Cache lại kết quả
            commentCache.put(postId, comments);
            
            return comments;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /**
     * Tìm kiếm bài đăng theo tiêu đề hoặc tag
     * @param query Chuỗi tìm kiếm
     * @return Danh sách bài đăng phù hợp
     */
    public List<ForumPost> searchPosts(String query) {
        if (query == null || query.isEmpty()) return Collections.emptyList();
        
        List<ForumPost> allPosts = getForumPosts(100); // Lấy số lượng đủ lớn để tìm kiếm
        
        String lowerCaseQuery = query.toLowerCase();
        
        return allPosts.stream()
                .filter(post -> 
                    post.getTitle().toLowerCase().contains(lowerCaseQuery) || 
                    post.getTags().stream().anyMatch(tag -> tag.toLowerCase().contains(lowerCaseQuery))
                )
                .collect(Collectors.toList());
    }
    
    /**
     * Thêm view cho bài đăng
     * @param postId ID của bài đăng
     */
    public void incrementViewCount(String postId) {
        if (postCache.containsKey(postId)) {
            ForumPost post = postCache.get(postId);
            post.setViewCount(post.getViewCount() + 1);
        }
    }
    
    /**
     * Convert từ OffsetDateTime của JDA sang LocalDateTime của Java
     */
    private LocalDateTime convertToLocalDateTime(OffsetDateTime offsetDateTime) {
        return offsetDateTime.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }
    
    /**
     * Tạo đối tượng ForumPost từ Message và ThreadChannel
     */
    private ForumPost createForumPostFromMessage(Message message, ThreadChannel thread) {
        ForumPost post = new ForumPost(
                thread.getId(),
                thread.getName(),
                message.getContentRaw(),
                message.getAuthor().getName(),
                message.getAuthor().getEffectiveAvatarUrl(),
                convertToLocalDateTime(message.getTimeCreated())
        );
        
        // Thiết lập trạng thái ghim
        post.setPinned(message.isPinned());
        
        // Lấy số lượt xem (members count của thread - 1 cho bot)
        int memberCount = thread.getMembers().size();
        post.setViewCount(Math.max(0, memberCount - 1));
        
        // Lấy tags từ các applied tags của forum thread
        thread.getAppliedTags().forEach(tag -> post.getTags().add(tag.getName()));
        
        // Lấy số lượng bình luận (số tin nhắn - 1 cho bài đăng gốc)
        post.setCommentCount(Math.max(0, thread.getMessageCount() - 1));
        
        return post;
    }
    
    /**
     * Đóng kết nối và xóa tài nguyên
     */
    public void disconnect() {
        if (jda != null) {
            jda.shutdown();
            isConnected = false;
        }
    }
}