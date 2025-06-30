package com.example.agentrolejava;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatManager {
    private static ChatManager instance;
    private List<ChatMessage> messages;
    private ChatMessage tempMessage;
    private String agentStatus;
    private String agentStatusMessage;
    private List<ChatUpdateListener> listeners;

    public interface ChatUpdateListener {
        void onMessagesUpdated();
        void onTempMessageUpdated();
        void onAgentStatusUpdated();
    }

    private ChatManager() {
        messages = new ArrayList<>();
        listeners = new CopyOnWriteArrayList<>();
    }

    public static synchronized ChatManager getInstance() {
        if (instance == null) {
            instance = new ChatManager();
        }
        return instance;
    }

    public void addListener(ChatUpdateListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ChatUpdateListener listener) {
        listeners.remove(listener);
    }

    /**
     * 添加消息
     */
    public void addMessage(String content, boolean isFromUser) {
        android.util.Log.d("ChatManager", "添加消息: " + content + ", 来自用户: " + isFromUser);
        // 清除临时消息
        tempMessage = null;
        // 添加正式消息
        messages.add(new ChatMessage(content, isFromUser));
        android.util.Log.d("ChatManager", "消息列表大小: " + messages.size());
        notifyMessagesUpdated();
        notifyTempMessageUpdated();
    }

    /**
     * 更新临时消息
     */
    public void updateTempMessage(String content, boolean isFromUser) {
        if (content != null && !content.isEmpty()) {
            tempMessage = new ChatMessage(content, isFromUser, true);
        } else {
            tempMessage = null;
        }
        notifyTempMessageUpdated();
    }

    /**
     * 更新Agent状态
     */
    public void updateAgentStatus(String status, String message) {
        this.agentStatus = status;
        this.agentStatusMessage = message;
        notifyAgentStatusUpdated();
    }

    /**
     * 清空消息列表
     */
    public void clearMessages() {
        messages.clear();
        tempMessage = null;
        agentStatus = null;
        agentStatusMessage = null;
        notifyMessagesUpdated();
        notifyTempMessageUpdated();
        notifyAgentStatusUpdated();
    }

    // Getters
    public List<ChatMessage> getMessages() {
        return new ArrayList<>(messages);
    }

    public ChatMessage getTempMessage() {
        return tempMessage;
    }

    public String getAgentStatus() {
        return agentStatus;
    }

    public String getAgentStatusMessage() {
        return agentStatusMessage;
    }

    // Notify methods
    private void notifyMessagesUpdated() {
        for (ChatUpdateListener listener : listeners) {
            listener.onMessagesUpdated();
        }
    }

    private void notifyTempMessageUpdated() {
        for (ChatUpdateListener listener : listeners) {
            listener.onTempMessageUpdated();
        }
    }

    private void notifyAgentStatusUpdated() {
        for (ChatUpdateListener listener : listeners) {
            listener.onAgentStatusUpdated();
        }
    }
} 