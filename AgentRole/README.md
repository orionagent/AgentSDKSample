# AgentSDK 角色扮演Demo项目介绍

## 🔗 SDK文档链接

📚 **最新AgentOS SDK文档**: [https://github.com/orionagent/agentos-sdk](https://github.com/orionagent/agentos-sdk)

## 📖 项目背景

这是一个基于猎户星空 AgentOS SDK 开发的Android示例应用，专门展示**AI角色扮演对话**功能。该Demo演示了如何使用SDK创建一个能够扮演不同历史人物和虚拟角色，与用户进行深度对话交流的智能应用。

## 开发环境
Android SDK 版本: 最低支持API 26 (Android 8.0)

JDK版本: Java 11

Kotlin、Java语言开发

## 运行环境

AgentOS Product Version: V1.2.0.250515

## 🎯 Demo功能概述

### 核心交互流程
1. **角色选择** → 用户从预设角色列表中选择想要对话的角色
2. **AI角色扮演** → AI完全沉浸在选定角色中，展现独特人格
3. **智能对话** → 基于角色设定进行符合人物特色的深度交流
4. **语音交互** → 支持语音输入和语音输出的自然对话体验

### 预设角色介绍

| 角色名称 | 角色特色 | 对话风格 | 交流重点 |
|---------|---------|---------|---------|
| 🎭 **李白** | 盛唐诗仙，豪放不羁 | 诗意飘逸、才华横溢 | 诗词创作、人生哲理、酒文化 |
| 👩‍💼 **接待员小美** | 专业商务接待主管 | 温和亲切、优雅得体 | 商务礼仪、茶艺文化、服务理念 |
| 📚 **鲁迅** | 现代文学奠基人 | 犀利深刻、富有思辨 | 社会批判、文学创作、青年教育 |

## 🏗️ 项目架构

### 技术实现架构
```
MainApplication (应用级Agent)
├── 全局AI人设配置 ("豹姐姐"基础助手)
├── 系统Action注册 (SAY等)
└── 静态Action处理

RoleSelectActivity (角色选择页面)
├── 角色列表展示
├── 角色详情预览
└── 角色选择跳转

ChatActivity (对话页面)
├── 动态角色人设加载
├── 角色专属对话逻辑
├── 语音交互处理
└── 对话历史管理
```

### 文件结构说明
```
app/src/main/
├── java/com/ainirobot/agent/sample/
│   ├── MainActivity.kt              # 应用入口
│   ├── MainApplication.kt           # 应用级Agent配置
│   ├── RoleSelectActivity.kt        # 角色选择页面
│   ├── ChatActivity.kt              # 角色对话页面
│   └── ui/components/
│       ├── RoleData.kt              # 角色数据定义
│       ├── RoleSelectScreen.kt      # 角色选择UI组件
│       ├── ChatScreen.kt            # 对话界面UI组件
│       ├── ChatManager.kt           # 对话管理逻辑
│       └── ChatList.kt              # 对话列表组件
└── assets/
    └── actionRegistry.json          # Agent配置文件

```

## 💡 核心Agent实现解析

### 1. 应用级Agent配置 (MainApplication.kt)

```kotlin
class MainApplication : Application() {

    lateinit var appAgent: AppAgent

    override fun onCreate() {
        super.onCreate()

        appAgent = object : AppAgent(this@MainApplication) {
            override fun onCreate() {
                // 设定基础人设 - 为角色扮演做准备
                 setPersona("你是一个专业的角色扮演助手，能够完全沉浸在不同角色中，展现各种人物的独特性格和特色。")
                // 设定角色目标
                // setObjective("通过自然的对话和合适的情绪表达，让用户感受到理解、陪伴与情感共鸣，从而提升交流的舒适感和信任感。")

                registerAction(Actions.SAY)
            }

            override fun onExecuteAction(
                action: Action,
                params: Bundle?
            ): Boolean {
                // 在此处处理静态注册的action，如果你不需要处理，请返回false，如果要自行处理且不需要后续处理，则返回true
                // 默认返回false
                return false
            }
        }
    }
}
```

### 2. 页面级Agent动态角色切换 (ChatActivity.kt)

```kotlin
class ChatActivity : ComponentActivity() {
    private lateinit var roleData: Role
    private lateinit var pageAgent: PageAgent

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

        // 注册Action
        pageAgent.registerAction(Actions.SAY).registerAction(Actions.EXIT)
    }
}
```

### 3. Agent配置文件 (actionRegistry.json)

```json
{
  "appId": "app_43e38d01cfad05d3bb2d8ce3a66f7cc3",
  "platform": "apk",
  "actionList": []
}
```

### 4. Agent状态监听和语音交互

```kotlin
// 设置Agent状态监听
pageAgent.setOnAgentStatusChangedListener(object : OnAgentStatusChangedListener {
    override fun onStatusChanged(status: String, message: String?): Boolean {
        Log.d("ChatActivity", "setOnAgentStatusChangedListener changed $status $message")
        // 更新状态到ChatManager
        ChatManager.updateAgentStatus(status, message)
        return true
    }
})

// 设置语音转写监听
pageAgent.setOnTranscribeListener(object : OnTranscribeListener {
    override fun onASRResult(transcription: Transcription): Boolean {
        if (transcription.text.isNotEmpty()) {
            if (transcription.final) {
                // 最终结果，添加到消息列表
                ChatManager.addMessage(transcription.text, true)
                // 用户说话，流式请求LLM生成回复话术
                generateRoleResponse(transcription.text)
            } else {
                // 实时转写结果，更新临时消息
                ChatManager.updateTempMessage(transcription.text, true)
            }
        }
        return true
    }

    override fun onTTSResult(transcription: Transcription): Boolean {
        if (transcription.text.isNotEmpty()) {
            if (transcription.final) {
                // 最终结果，添加到消息列表
                ChatManager.addMessage(transcription.text, false)
                // 机器人说话，将回复添加到历史记录
                val assistantMessage = LLMMessage(LLMRole.ASSISTANT, transcription.text)
                addToHistory(assistantMessage)
            } else {
                // 实时转写结果，更新临时消息
                ChatManager.updateTempMessage(transcription.text, false)
            }
        }
        return true
    }
})
```

### 5. 角色对话生成逻辑

```kotlin
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
            AgentCore.llmSync(messages, config, 20 * 1000, isStreaming = true)

        } catch (e: Exception) {
            Log.e("ChatActivity", "生成回复失败", e)
        }
    }
}
```

### 6. AgentCore核心接口使用

```kotlin
// LLM大语言模型调用
AgentCore.llmSync(messages, config, 20 * 1000, isStreaming = true)

// TTS语音合成
AgentCore.tts("请先选择要体验的角色", timeoutMillis = 20 * 1000)
AgentCore.stopTTS()

// 上下文管理
AgentCore.clearContext()
AgentCore.uploadInterfaceInfo(roleInfo)

// Agent配置
AgentCore.isEnableVoiceBar = false
AgentCore.isDisablePlan = true
```

## 🎮 Demo使用方法

### 基本操作流程
1. **启动应用** - 打开角色扮演Demo应用
2. **选择角色** - 在角色选择页面点击想要对话的角色
3. **开始对话** - 通过语音或文字与AI角色进行交流
4. **深度交流** - 体验不同角色的独特对话风格和专业知识
5. **切换角色** - 返回角色选择页面体验其他角色

### 角色体验建议

**与李白对话：**
- 可以聊诗词创作、人生哲理、酒文化
- 体验诗仙的豪放不羁和才华横溢
- 感受盛唐时期的文化氛围

**与接待员小美对话：**
- 可以咨询商务礼仪、茶艺知识
- 体验专业的商务接待服务
- 感受温和亲切的服务态度

**与鲁迅对话：**
- 可以讨论文学创作、社会现象
- 体验文学大师的深邃思想
- 感受犀利而温暖的人文关怀

## 🔧 技术特色

### 1. 多角色动态切换
- 支持运行时动态加载不同角色配置
- 每个角色都有独立的人设和行为准则
- 保持角色一致性和沉浸感

### 2. 深度角色扮演
- 详细的角色背景和性格设定
- 符合历史人物特色的语言风格
- 丰富的专业知识和文化内涵

### 3. 智能对话管理
- 上下文记忆和对话连贯性
- 角色情感和态度的真实表达
- 自然流畅的语音交互体验

### 4. 可扩展架构
- 易于添加新的角色和人设
- 模块化的组件设计
- 灵活的配置管理系统

## 🚀 扩展开发

### 添加新角色
1. 在 `RoleData.kt` 中定义新角色
2. 添加角色头像资源到 `res/mipmap-xxxhdpi/`
3. 配置角色的 `persona` 和 `objective`
4. 测试角色对话效果

### 自定义功能
- 可以为特定角色添加专属Action
- 支持角色间的交互对话
- 可以集成更多多媒体交互功能

---

**🎭 开始你的角色扮演之旅吧！与历史名人面对面交流，体验跨越时空的对话魅力！**
