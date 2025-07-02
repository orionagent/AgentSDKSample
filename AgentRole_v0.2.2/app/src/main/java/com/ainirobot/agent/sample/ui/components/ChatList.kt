package com.ainirobot.agent.sample.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 消息数据类
data class ChatMessage(
    val content: String,
    val isUser: Boolean
)

// 对话列表组件
@Composable
fun ChatList(
    modifier: Modifier = Modifier,
    messages: List<ChatMessage> = emptyList()
) {
    val listState = rememberLazyListState()
    val tempMessage = ChatManager.tempMessage.value

    // 每次消息变化自动滚动到底部
    LaunchedEffect(messages.size, tempMessage) {
        if (messages.isNotEmpty() || tempMessage != null) {
            listState.animateScrollToItem(messages.size + (if (tempMessage != null) 1 else 0) - 1)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(messages) { message ->
            ChatMessageItem(message = message)
        }
        
        // 显示临时消息
        tempMessage?.let { message ->
            item {
                ChatMessageItem(
                    message = message,
                    isTemp = true
                )
            }
        }
    }
}

// 单个消息项组件
@Composable
private fun ChatMessageItem(
    message: ChatMessage,
    isTemp: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(
                    color = when {
                        isTemp -> if (message.isUser) Color(0xFFE3F2FD).copy(alpha = 0.7f)
                                 else Color(0xFFE8F5E9).copy(alpha = 0.7f)
                        else -> if (message.isUser) Color(0xFFE3F2FD) else Color(0xFFE8F5E9)
                    },
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = if (isTemp) "${message.content}..." else message.content,
                fontSize = 16.sp,
                color = if (isTemp) Color.Gray else Color.Black,
                textAlign = if (message.isUser) TextAlign.Start else TextAlign.End
            )
        }
    }
} 