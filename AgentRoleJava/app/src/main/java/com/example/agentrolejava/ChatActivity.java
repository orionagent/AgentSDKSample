package com.example.agentrolejava;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ainirobot.agent.AgentCore;
import com.ainirobot.agent.OnTranscribeListener;
import com.ainirobot.agent.PageAgent;
import com.ainirobot.agent.action.Actions;
import com.ainirobot.agent.OnAgentStatusChangedListener;
import com.ainirobot.agent.base.llm.LLMMessage;
import com.ainirobot.agent.base.llm.LLMConfig;
import com.ainirobot.agent.base.Transcription;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements ChatManager.ChatUpdateListener {
    private static final String TAG = "ChatActivity";
    
    private Role roleData;
    private PageAgent pageAgent;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private ImageView avatarImageView;
    private TextView roleNameTextView;
    private TextView statusTextView;
    private android.os.Handler animationHandler = new android.os.Handler();
    private Runnable animationRunnable;
    private int dotCount = 0;
    
    // 历史记录管理
    private final List<LLMMessage> conversationHistory = new ArrayList<>();
    private static final int MAX_HISTORY_SIZE = 10; // 最大保留10轮对话

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 获取传递过来的Role参数
        roleData = (com.example.agentrolejava.Role) getIntent().getParcelableExtra("role");
        if (roleData == null) {
            finish();
            return;
        }

        initViews();
        setupAgent();
        setupRecyclerView();
        
        // 注册ChatManager监听器
        ChatManager.getInstance().addListener(this);
    }

    private void initViews() {
        avatarImageView = findViewById(R.id.avatarImageView);
        roleNameTextView = findViewById(R.id.roleNameTextView);
        statusTextView = findViewById(R.id.statusTextView);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        
        // 设置角色信息
        avatarImageView.setImageResource(roleData.getAvatarRes());
        roleNameTextView.setText(roleData.getName());
        
        // 返回按钮
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter();
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);
    }

    private void setupAgent() {
        // 设置AppAgent的人设
        MainApplication app = (MainApplication) getApplicationContext();
        app.getAppAgent().setPersona(roleData.getPersona());
        app.getAppAgent().setObjective(roleData.getObjective());

        // 初始化PageAgent
        pageAgent = new PageAgent(this);
        pageAgent.blockAllActions();

        String roleInfo = roleData.getName() + "\n" + roleData.getPersona() + "\n" + roleData.getObjective();
        pageAgent.setObjective(roleInfo);
        
        AgentCore.INSTANCE.uploadInterfaceInfo(" ");
        Log.d(TAG, "Create UploadInterfaceInfo:");

        // 注册Action
        pageAgent.registerAction(Actions.SAY).registerAction(Actions.EXIT);

        pageAgent.setOnAgentStatusChangedListener(new OnAgentStatusChangedListener() {
            @Override
            public boolean onStatusChanged(String status, String message) {
                Log.d(TAG, "setOnAgentStatusChangedListener changed " + status + " " + message);
                // 更新状态到ChatManager
                ChatManager.getInstance().updateAgentStatus(status, message);
                Log.d(TAG, "Agent状态变化: " + status + ", 消息: " + message);
                return true;
            }
        });

        // 设置语音转写监听
        pageAgent.setOnTranscribeListener(new OnTranscribeListener() {
            @Override
            public boolean onASRResult(Transcription transcription) {
                if (!transcription.getText().isEmpty()) {
                    if (transcription.getFinal()) {
                        // 最终结果，添加到消息列表
                        ChatManager.getInstance().addMessage(transcription.getText(), true);
                        // 用户说话，流式请求LLM生成回复话术
                        generateRoleResponse(transcription.getText());
                    } else {
                        // 实时转写结果，更新临时消息
                        ChatManager.getInstance().updateTempMessage(transcription.getText(), true);
                    }
                }
                Log.d(TAG, "ASR结果: " + transcription.getText() + ", final: " + transcription.getFinal());
                return true;
            }

            @Override
            public boolean onTTSResult(Transcription transcription) {
                if (!transcription.getText().isEmpty()) {
                    if (transcription.getFinal()) {
                        // 最终结果，添加到消息列表
                        ChatManager.getInstance().addMessage(transcription.getText(), false);
                        // 机器人说话，将回复添加到历史记录
                        LLMMessage assistantMessage = new LLMMessage(com.ainirobot.agent.base.llm.Role.ASSISTANT, transcription.getText());
                        addToHistory(assistantMessage);
                        Log.d(TAG, "机器人回复已添加到历史记录: " + transcription.getText());
                    } else {
                        // 实时转写结果，更新临时消息
                        ChatManager.getInstance().updateTempMessage(transcription.getText(), false);
                    }
                }
                Log.d(TAG, "TTS结果: " + transcription.getText() + ", final: " + transcription.getFinal());
                return true;
            }
        });
    }

    private void generateRoleResponse(String userQuery) {
        AOExecutorScope.execute(() -> {
            try {
                // 构建包含历史记录的消息列表
                List<LLMMessage> messages = new ArrayList<>();
                
                // 添加系统提示词
                messages.add(new LLMMessage(
                    com.ainirobot.agent.base.llm.Role.SYSTEM,
                    "你现在扮演的角色是：" + roleData.getName() + "\n" +
                    "角色设定：" + roleData.getPersona() + "\n" +
                    "行为准则：" + roleData.getObjective() + "\n" +
                    "\n" +
                    "要求：\n" +
                    "1. 完全沉浸在角色中，展现角色特色\n" +
                    "2. 回复要自然流畅，富有情感\n" +
                    "3. 每次回复不超过50字\n" +
                    "4. 不要暴露是AI的身份\n" +
                    "5. 要有自己的态度和个性\n" +
                    "6. 保持对话的连贯性和上下文\n" +
                    "7. 说话要符合角色的语言风格和时代背景\n" +
                    "8. 根据之前的对话历史，保持角色的一致性和连贯性"
                ));
                
                // 添加历史对话记录
                synchronized (conversationHistory) {
                    messages.addAll(conversationHistory);
                }
                
                // 添加当前用户输入
                LLMMessage currentUserMessage = new LLMMessage(com.ainirobot.agent.base.llm.Role.USER, userQuery);
                messages.add(currentUserMessage);

                LLMConfig config = new LLMConfig(
                    0.8f,  // temperature - 增加一些随机性，让回复更有趣
                    100,   // maxTokens - 限制回复长度
                    6,     // timeout
                    false, // fileSearch
                    null   // businessInfo
                );

                // 先将用户输入添加到历史记录
                addToHistory(currentUserMessage);
                
                // 生成回复（流式播放，机器人的回复会在onTranscribe中获取到）
                AgentCore.INSTANCE.llm(messages, config, 20 * 1000, true, null);
                
                Log.d(TAG, "角色回复请求已发送，用户输入: " + userQuery);

            } catch (Exception e) {
                Log.e(TAG, "生成回复失败", e);
            }
        });
    }

    /**
     * 添加消息到历史记录，并管理历史记录大小
     */
    private void addToHistory(LLMMessage message) {
        synchronized (conversationHistory) {
            conversationHistory.add(message);
            Log.d(TAG, "历史记录：" + conversationHistory);
            
            // 如果历史记录超过最大限制，移除最早的对话（保留系统消息）
            while (conversationHistory.size() > MAX_HISTORY_SIZE * 2) { // *2 因为每轮对话包含用户和助手两条消息
                // 移除最早的一对用户-助手消息
                if (!conversationHistory.isEmpty() && conversationHistory.get(0).getRole() == com.ainirobot.agent.base.llm.Role.USER) {
                    conversationHistory.remove(0); // 移除用户消息
                    if (!conversationHistory.isEmpty() && conversationHistory.get(0).getRole() == com.ainirobot.agent.base.llm.Role.ASSISTANT) {
                        conversationHistory.remove(0); // 移除对应的助手消息
                    }
                } else if (!conversationHistory.isEmpty()) {
                    // 如果第一个不是USER消息，直接移除避免无限循环
                    conversationHistory.remove(0);
                } else {
                    // 如果列表为空，跳出循环
                    break;
                }
            }
            
            Log.d(TAG, "历史记录大小: " + conversationHistory.size());
        }
    }

    /**
     * 清空历史记录
     */
    private void clearHistory() {
        synchronized (conversationHistory) {
            conversationHistory.clear();
            Log.d(TAG, "历史记录已清空");
        }
    }

    /**
     * 生成初始对话（自我介绍）
     */
    private void generateInitialIntroduction() {
        AOExecutorScope.execute(() -> {
            try {
                String introQuery = "简短的自我介绍，不超过30字";
                
                // 构建消息列表
                List<LLMMessage> messages = new ArrayList<>();
                
                // 添加系统提示词
                messages.add(new LLMMessage(
                    com.ainirobot.agent.base.llm.Role.SYSTEM,
                    "你现在扮演的角色是：" + roleData.getName() + "\n" +
                    "角色设定：" + roleData.getPersona() + "\n" +
                    "行为准则：" + roleData.getObjective() + "\n" +
                    "\n" +
                    "现在需要你进行简短的自我介绍，要求：\n" +
                    "1. 完全沉浸在角色中，展现角色特色\n" +
                    "2. 自我介绍要自然亲切，不超过30字\n" +
                    "3. 要体现角色的个性和特点\n" +
                    "4. 不要暴露是AI的身份\n" +
                    "5. 要让用户感受到角色的魅力"
                ));
                
                // 添加用户请求
                LLMMessage userMessage = new LLMMessage(com.ainirobot.agent.base.llm.Role.USER, introQuery);
                messages.add(userMessage);

                LLMConfig config = new LLMConfig(
                    0.8f,  // temperature
                    80,    // maxTokens - 限制初始介绍的长度
                    6,     // timeout
                    false, // fileSearch
                    null   // businessInfo
                );

                // 将初始请求添加到历史记录
                addToHistory(userMessage);
                
                // 生成回复（流式播放，机器人的回复会在onTranscribe中获取到）
                AgentCore.INSTANCE.llm(messages, config, 20 * 1000, true, null);
                Log.d(TAG, "初始介绍请求已发送");

            } catch (Exception e) {
                Log.e(TAG, "生成初始介绍失败", e);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        
        // 上传角色信息
        AgentCore.INSTANCE.uploadInterfaceInfo("");
        Log.d(TAG, "onStart UploadInterfaceInfo:");

        // 清空当前界面聊天记录和历史记录
        ChatManager.getInstance().clearMessages();
        clearHistory(); // 清空LLM对话历史记录
        // 停止TTS和清理LLM上下文
        AgentCore.INSTANCE.stopTTS();
        AgentCore.INSTANCE.clearContext();

        // 触发初始对话
        AOExecutorScope.executeDelayed(() -> {
            if (!TextUtils.isEmpty(roleData.getName())) {
                generateInitialIntroduction();
            }
        }, 200);

        AgentCore.INSTANCE.setDisablePlan(true);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy stopTTS");
        // 清空聊天记录和历史记录
        ChatManager.getInstance().clearMessages();
        clearHistory(); // 清空LLM对话历史记录
        // 停止TTS和清理上下文
        AgentCore.INSTANCE.stopTTS();
        AgentCore.INSTANCE.clearContext();
        
        // 移除监听器
        ChatManager.getInstance().removeListener(this);
        
        // 停止状态动画
        stopStatusAnimation();

        super.onDestroy();
    }

    // ChatManager.ChatUpdateListener 实现
    @Override
    public void onMessagesUpdated() {
        runOnUiThread(() -> {
            chatAdapter.updateMessages(ChatManager.getInstance().getMessages());
            scrollToBottom();
        });
    }

    @Override
    public void onTempMessageUpdated() {
        runOnUiThread(() -> {
            chatAdapter.updateTempMessage(ChatManager.getInstance().getTempMessage());
            scrollToBottom();
        });
    }

    @Override
    public void onAgentStatusUpdated() {
        runOnUiThread(() -> {
            String status = ChatManager.getInstance().getAgentStatus();
            String statusText = null;
            
            if (status != null) {
                switch (status) {
                    case "listening":
                        statusText = "正在听";
                        break;
                    case "thinking":
                        statusText = "正在思考";
                        break;
                    case "processing":
                        statusText = "正在说";
                        break;
                    case "":
                        statusText = "";
                        break;
                    default:
                        statusText = null;
                        break;
                }
            }
            
            if (statusText != null && !statusText.isEmpty()) {
                startStatusAnimation(statusText);
                statusTextView.setVisibility(TextView.VISIBLE);
            } else {
                stopStatusAnimation();
                statusTextView.setVisibility(TextView.GONE);
            }
        });
    }

    private void startStatusAnimation(String baseText) {
        stopStatusAnimation(); // 先停止之前的动画
        
        animationRunnable = new Runnable() {
            @Override
            public void run() {
                String dots = "";
                for (int i = 0; i < dotCount; i++) {
                    dots += ".";
                }
                statusTextView.setText(baseText + dots);
                
                dotCount = (dotCount + 1) % 4; // 0, 1, 2, 3 循环
                animationHandler.postDelayed(this, 500); // 每500ms更新一次
            }
        };
        animationHandler.post(animationRunnable);
    }

    private void stopStatusAnimation() {
        if (animationRunnable != null) {
            animationHandler.removeCallbacks(animationRunnable);
            animationRunnable = null;
        }
        dotCount = 0;
    }

    private void scrollToBottom() {
        if (chatAdapter.getItemCount() > 0) {
            chatRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
        }
    }
} 