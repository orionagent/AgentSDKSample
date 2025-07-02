package com.ainirobot.agent.sample.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp


@Composable
fun ChatScreen(
    roleData: Role,
    onBackClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 顶部栏
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { 
                    // 点击返回按钮时直接清理并退出
                    onBackClick()
                }) {
                    Icon(Icons.Default.ArrowBack, "返回")
                }
                Image(
                    painter = painterResource(id = roleData.avatarRes),
                    contentDescription = roleData.name,
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    roleData.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
                // 显示Agent状态
                ChatManager.agentStatus.value?.let { status ->

                    val statusText = when (status) {
                        "listening" -> "正在听"
                        "thinking" -> "正在思考"
                        "processing" -> "正在说"
                        "" -> ""
                        else -> null
                    }
                    statusText?.let {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary,
                            )
                            AnimatedDots(
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            )
                        }
                    }
                }
            }
            // 聊天消息列表
            ChatList(
                modifier = Modifier.weight(1f),
                messages = ChatManager.messages
            )
        }
    }
}