package com.ainirobot.agent.sample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ainirobot.agent.AgentCore
import com.ainirobot.agent.OnTranscribeListener
import com.ainirobot.agent.PageAgent
import com.ainirobot.agent.action.Action
import com.ainirobot.agent.base.llm.LLMMessage
import com.ainirobot.agent.base.llm.LLMConfig
import com.ainirobot.agent.base.llm.Role as LLMRole
import com.ainirobot.agent.sample.ui.components.ChatManager
import com.ainirobot.agent.sample.ui.theme.AgentDemoTheme
import kotlinx.coroutines.launch
import android.text.TextUtils
import com.ainirobot.agent.coroutine.AOCoroutineScope
import com.ainirobot.agent.action.Actions
import com.ainirobot.agent.sample.ui.components.ChatScreen
import com.ainirobot.agent.sample.ui.components.Role
import com.ainirobot.agent.base.Transcription


class ChatActivity : ComponentActivity() {
    private lateinit var roleData: Role
    private lateinit var pageAgent: PageAgent
    
    // 添加历史记录管理
    private val conversationHistory = mutableListOf<LLMMessage>()
    private val maxHistorySize = 10 // 最大保留10轮对话

    private fun generateRoleResponse(userQuery: String) {
        AOCoroutineScope.launch {
            try {
                // 构建包含历史记录的消息列表
                val messages = mutableListOf<LLMMessage>()
                
                // 添加系统提示词
                messages.add(
                    LLMMessage(
                        LLMRole.SYSTEM,
                        """你现在扮演的角色是：${roleData.name}
                        |角色设定：${roleData.persona}
                        |行为准则：${roleData.objective}
                        |
                        |要求：
                        |1. 完全沉浸在角色中，展现角色特色
                        |2. 回复要自然流畅，富有情感
                        |3. 每次回复不超过50字
                        |4. 不要暴露是AI的身份
                        |5. 要有自己的态度和个性
                        |6. 保持对话的连贯性和上下文
                        |7. 说话要符合角色的语言风格和时代背景
                        |8. 根据之前的对话历史，保持角色的一致性和连贯性""".trimMargin()
                    )
                )
                
                // 添加历史对话记录
                messages.addAll(conversationHistory)
                
                // 添加当前用户输入
                val currentUserMessage = LLMMessage(LLMRole.USER, userQuery)
                messages.add(currentUserMessage)

                val config = LLMConfig(
                    temperature = 0.8f,  // 增加一些随机性，让回复更有趣
                    maxTokens = 100      // 限制回复长度
                )

                // 先将用户输入添加到历史记录
                addToHistory(currentUserMessage)
                
                // 生成回复（流式播放，机器人的回复会在onTranscribe中获取到）
                AgentCore.llmSync(messages, config, 20 * 1000)
                
                Log.d("ChatActivity", "角色回复请求已发送，用户输入: $userQuery")

            } catch (e: Exception) {
                Log.e("ChatActivity", "生成回复失败", e)
            }
        }
    }
    
    /**
     * 添加消息到历史记录，并管理历史记录大小
     */
    private fun addToHistory(message: LLMMessage) {
        conversationHistory.add(message)
        Log.d("ChatActivity", "历史记录：${conversationHistory}")
        
        // 如果历史记录超过最大限制，移除最早的对话（保留系统消息）
        while (conversationHistory.size > maxHistorySize * 2) { // *2 因为每轮对话包含用户和助手两条消息
            // 移除最早的一对用户-助手消息
            if (conversationHistory.isNotEmpty() && conversationHistory[0] != null && conversationHistory[0].role == LLMRole.USER) {
                conversationHistory.removeAt(0) // 移除用户消息
                if (conversationHistory.isNotEmpty() && conversationHistory[0] != null && conversationHistory[0].role == LLMRole.ASSISTANT) {
                    conversationHistory.removeAt(0) // 移除对应的助手消息
                }
            } else if (conversationHistory.isNotEmpty()) {
                // 如果第一个不是USER消息，直接移除避免无限循环
                conversationHistory.removeAt(0)
            } else {
                // 如果列表为空，跳出循环
                break
            }
        }
        
        Log.d("ChatActivity", "历史记录大小: ${conversationHistory.size}")
    }
    
    /**
     * 清空历史记录
     */
    private fun clearHistory() {
        conversationHistory.clear()
        Log.d("ChatActivity", "历史记录已清空")
    }
    
    /**
     * 生成初始对话（自我介绍）
     */
    private fun generateInitialIntroduction() {
        AOCoroutineScope.launch {
            try {
                val introQuery = "简短的自我介绍，不超过30字"
                
                // 构建消息列表
                val messages = mutableListOf<LLMMessage>()
                
                // 添加系统提示词
                messages.add(
                    LLMMessage(
                        LLMRole.SYSTEM,
                        """你现在扮演的角色是：${roleData.name}
                        |角色设定：${roleData.persona}
                        |行为准则：${roleData.objective}
                        |
                        |现在需要你进行简短的自我介绍，要求：
                        |1. 完全沉浸在角色中，展现角色特色
                        |2. 自我介绍要自然亲切，不超过30字
                        |3. 要体现角色的个性和特点
                        |4. 不要暴露是AI的身份
                        |5. 要让用户感受到角色的魅力""".trimMargin()
                    )
                )
                
                // 添加用户请求
                val userMessage = LLMMessage(LLMRole.USER, introQuery)
                messages.add(userMessage)

                val config = LLMConfig(
                    temperature = 0.8f,
                    maxTokens = 80  // 限制初始介绍的长度
                )

                // 将初始请求添加到历史记录
                addToHistory(userMessage)
                
                // 生成回复（流式播放，机器人的回复会在onTranscribe中获取到）
                AgentCore.llmSync(messages, config, 20 * 1000)
                Log.d("ChatActivity", "初始介绍请求已发送")

            } catch (e: Exception) {
                Log.e("ChatActivity", "生成初始介绍失败", e)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 获取传递过来的Role参数
        roleData = intent.getParcelableExtra("role")!!

        // 设置AppAgent的人设
        val appAgent = (applicationContext as MainApplication).appAgent
        appAgent.setPersona(roleData.persona)
        appAgent.setObjective(roleData.objective)

        // 初始化PageAgent
        pageAgent = PageAgent(this)
        pageAgent.blockAllActions()

        val roleInfo = roleData.name + "\n" + roleData.persona + "\n" + roleData.objective
        pageAgent.setObjective(roleInfo)
        
        AgentCore.uploadInterfaceInfo(" ")
        Log.d("CreateChatScreen", "Create UploadInterfaceInfo:")

        // 注册Action
        pageAgent.registerAction(Actions.SAY).registerAction(Actions.EXIT)

        // 设置语音转写监听
        pageAgent.setOnTranscribeListener(object : OnTranscribeListener {
            override fun onTranscribe(transcription: Transcription): Boolean {
                if (transcription.text.isNotEmpty()) {
                    // 直接使用Transcription内置的isUserSpeaking属性
                    val isUserSpeaking = transcription.isUserSpeaking
                    
                    Log.d(
                        "ChatActivity",
                        "转写结果 - 文本: '${transcription.text}', speaker: '${transcription.speaker}', final: ${transcription.final}, isUserSpeaking: $isUserSpeaking"
                    )
                    
                    if (transcription.final) {
                        // 最终结果，添加到消息列表
                        ChatManager.addMessage(transcription.text, isUserSpeaking)
                        
                        if (isUserSpeaking) {
                            // 用户说话，流式请求LLM生成回复话术
                            generateRoleResponse(transcription.text)
                            Log.d("ChatActivity", "用户输入已处理: ${transcription.text}")
                        } else {
                            // 机器人说话，将回复添加到历史记录
                            val assistantMessage = LLMMessage(LLMRole.ASSISTANT, transcription.text)
                            addToHistory(assistantMessage)
                            Log.d("ChatActivity", "机器人回复已添加到历史记录: ${transcription.text}")
                        }
                    } else {
                        // 实时转写结果，更新临时消息
                        ChatManager.updateTempMessage(transcription.text, isUserSpeaking)
                    }
                }
                
                return true
            }
        })

        // 设置UI
        setContent {
            AgentDemoTheme {
                ChatScreen(
                    roleData = roleData,
                    onBackClick = { this@ChatActivity.finish() }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        
        // 上传角色信息
        AgentCore.uploadInterfaceInfo("")
        Log.d("onStart", "onStart UploadInterfaceInfo:")

        // 清空当前界面聊天记录和历史记录
        ChatManager.clearMessages()
        clearHistory() // 清空LLM对话历史记录
        // 停止TTS和清理LLM上下文
        AgentCore.stopTTS()
        AgentCore.clearContext()


        // 触发初始对话
        AOCoroutineScope.launch {
            kotlinx.coroutines.delay(200)
            if (!TextUtils.isEmpty(roleData.name)) {
                generateInitialIntroduction()
            }
        }

    }

    override fun onDestroy() {
        Log.d("ChatActivity", "onDestroy stopTTS")
        // 清空聊天记录和历史记录
        ChatManager.clearMessages()
        clearHistory() // 清空LLM对话历史记录
        // 停止TTS和清理上下文
        AgentCore.stopTTS()
        AgentCore.clearContext()

        super.onDestroy()
    }
} 