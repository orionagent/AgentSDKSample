package com.example.agentrolejava;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<ChatMessage> messages = new ArrayList<>();
    private ChatMessage tempMessage;

    public void updateMessages(List<ChatMessage> newMessages) {
        this.messages.clear();
        this.messages.addAll(newMessages);
        notifyDataSetChanged();
    }

    public void updateTempMessage(ChatMessage tempMessage) {
        this.tempMessage = tempMessage;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        if (position < messages.size()) {
            // 正常消息
            ChatMessage message = messages.get(position);
            holder.bind(message);
        } else {
            // 临时消息
            if (tempMessage != null) {
                holder.bind(tempMessage);
            }
        }
    }

    @Override
    public int getItemCount() {
        int count = messages.size();
        if (tempMessage != null) {
            count++;
        }
        return count;
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        private TextView userMessageTextView;
        private TextView assistantMessageTextView;
        private View userMessageContainer;
        private View assistantMessageContainer;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            userMessageTextView = itemView.findViewById(R.id.messageTextView);
            assistantMessageTextView = itemView.findViewById(R.id.assistantMessageTextView);
            userMessageContainer = itemView.findViewById(R.id.userMessageContainer);
            assistantMessageContainer = itemView.findViewById(R.id.assistantMessageContainer);
        }

        public void bind(ChatMessage message) {
            String displayText = message.getContent();
            if (message.isTemp()) {
                displayText += "...";
            }
            
            // 设置消息显示样式
            if (message.isUser()) {
                // 用户消息显示在右侧
                userMessageTextView.setText(displayText);
                userMessageContainer.setVisibility(View.VISIBLE);
                assistantMessageContainer.setVisibility(View.GONE);
                
                // 设置透明度（临时消息半透明）
                float alpha = message.isTemp() ? 0.7f : 1.0f;
                userMessageContainer.setAlpha(alpha);
            } else {
                // 助手消息显示在左侧
                assistantMessageTextView.setText(displayText);
                userMessageContainer.setVisibility(View.GONE);
                assistantMessageContainer.setVisibility(View.VISIBLE);
                
                // 设置透明度（临时消息半透明）
                float alpha = message.isTemp() ? 0.7f : 1.0f;
                assistantMessageContainer.setAlpha(alpha);
            }
        }
    }
} 