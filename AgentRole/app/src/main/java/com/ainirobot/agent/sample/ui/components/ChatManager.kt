package com.ainirobot.agent.sample.ui.components

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

/**
 * 聊天管理器，用于管理聊天消息
 */
object ChatManager {
    // 消息列表
    val messages = mutableStateListOf<ChatMessage>()
    
    // 临时消息，用于显示正在转写的文本
    val tempMessage = mutableStateOf<ChatMessage?>(null)

    // Agent状态
    val agentStatus = mutableStateOf<String?>(null)
    val agentStatusMessage = mutableStateOf<String?>(null)

    /**
     * 添加消息
     * @param content 消息内容
     * @param isFromUser 是否来自用户
     */
    fun addMessage(content: String, isFromUser: Boolean) {
        // 清除临时消息
        tempMessage.value = null
        // 添加正式消息
        messages.add(ChatMessage(content = content, isUser = isFromUser))
    }

    /**
     * 更新临时消息
     * @param content 临时消息内容
     * @param isFromUser 是否来自用户
     */
    fun updateTempMessage(content: String, isFromUser: Boolean) {
        if (content.isNotEmpty()) {
            tempMessage.value = ChatMessage(content = content, isUser = isFromUser)
        } else {
            tempMessage.value = null
        }
    }

    /**
     * 更新Agent状态
     * @param status 状态
     * @param message 状态消息
     */
    fun updateAgentStatus(status: String?, message: String?) {
        agentStatus.value = status
        agentStatusMessage.value = message
    }

    // 清空消息列表
    fun clearMessages() {
        messages.clear()
        tempMessage.value = null
        agentStatus.value = null
        agentStatusMessage.value = null
    }
} 