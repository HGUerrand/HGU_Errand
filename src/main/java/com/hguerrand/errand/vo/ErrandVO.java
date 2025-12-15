package com.hguerrand.errand.vo;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ErrandVO {
    private int id;
    private String title;
    private int reward;
    private String from;
    private String to;
    private String time;
    private String status;
    private String hashtags;          // DB 원본: "#급함,#가벼움"
    private LocalDateTime createdAt;  // DB 원본
    private int requesterId;

    // JSP에서 ${e.hashtags}로 돌리고 싶으면 "가짜 getter" 제공
    public List<String> getHashtags() {
        if (hashtags == null || hashtags.trim().isEmpty()) return Collections.emptyList();
        return Arrays.asList(hashtags.split("\\s*,\\s*"));
    }

    // getters/setters (필수)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getReward() { return reward; }
    public void setReward(int reward) { this.reward = reward; }

    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }

    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getHashtagsRaw() { return hashtags; }
    public void setHashtagsRaw(String hashtags) { this.hashtags = hashtags; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public int getRequesterId() { return requesterId; }
    public void setRequesterId(int requesterId) { this.requesterId = requesterId; }
}