package com.example.agentrolejava;

public class ChatMessage {
    private String content;
    private boolean isUser;
    private boolean isTemp;

    public ChatMessage(String content, boolean isUser) {
        this.content = content;
        this.isUser = isUser;
        this.isTemp = false;
    }

    public ChatMessage(String content, boolean isUser, boolean isTemp) {
        this.content = content;
        this.isUser = isUser;
        this.isTemp = isTemp;
    }

    // Getters
    public String getContent() {
        return content;
    }

    public boolean isUser() {
        return isUser;
    }

    public boolean isTemp() {
        return isTemp;
    }

    // Setters
    public void setContent(String content) {
        this.content = content;
    }

    public void setUser(boolean user) {
        isUser = user;
    }

    public void setTemp(boolean temp) {
        isTemp = temp;
    }
} 