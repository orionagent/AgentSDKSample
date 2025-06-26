# AgentSDK 情感交互Demo项目介绍

## 🔗 SDK文档链接

📚 **最新AgentOS SDK文档**: [https://github.com/orionagent/agentos-sdk](https://github.com/orionagent/agentos-sdk)

## 📖 项目背景

这是一个基于猎户星空 AgentOS SDK 开发的Android示例应用，专门展示**情感识别与表情反馈**功能。该Demo演示了如何使用SDK创建一个能够理解用户情绪并做出相应表情反应的智能交互应用。

## 开发环境
Android SDK 版本: 最低支持API 26 (Android 8.0)

JDK版本: Java 11

Kotlin、Java语言开发

## 运行环境

AgentOS Product Version: V1.2.0.250515


## 🎯 Demo功能概述

### 核心交互流程
1. **用户语音输入** → 用户通过语音表达情感
2. **情绪智能识别** → AI分析用户的情绪状态  
3. **表情动态反馈** → 应用显示对应的表情图标
4. **语音温暖回应** → AI助手给出贴心的语音回复

### 支持的情感场景

| 情感类型 | 触发词示例 | 表情反馈 | 回应特点 |
|---------|-----------|---------|---------|
| 😊 **开心** | "我很高兴"、"太棒了"、"很满意" | 笑脸图标 | 积极鼓励、分享喜悦 |
| 😢 **难过** | "心情不好"、"很失落"、"难受" | 哭脸图标 | 温暖安慰、情感支持 |
| 😠 **生气** | "太气人了"、"很愤怒"、"讨厌" | 生气图标 | 理解共情、情绪疏导 |

## 🏗️ 项目架构

### 技术实现架构
```
MainApplication (应用级Agent)
├── 全局AI人设配置 ("豹姐姐"助手)
├── 系统Action注册 (EXIT等)
└── 静态Action处理

MainActivity (页面级Agent)  
├── 三个情感Action注册
│   ├── SHOW_SMILE_FACE (开心表情)
│   ├── SHOW_CRY_FACE (难过表情)  
│   └── SHOW_ANGRY_FACE (生气表情)
├── 表情图标显示逻辑
└── TTS语音播放处理
```

### 文件结构说明
```
app/src/main/
├── java/com/ainirobot/agent/sample/
│   ├── MainActivity.kt          # 主界面 - 实现页面级Agent和表情Action
│   └── MainApplication.kt       # 应用入口 - 配置应用级Agent和AI人设
├── assets/
│   └── actionRegistry.json      # Agent配置 - 定义应用ID和平台信息
└── AndroidManifest.xml         # 应用配置
```

## 💡 核心实现解析

### 1. AI助手人设配置 (MainApplication.kt)

```kotlin
// 设定AI助手的角色人设
setPersona("""
    你叫豹姐姐，是一位聪明、亲切又略带俏皮的虚拟助手，
    擅长倾听与共情。你在对话中能够敏锐捕捉用户情绪，
    用适当的表情反应来陪伴用户。
""")

// 设定对话风格
setStyle("语气温和、有耐心")

// 设定任务目标  
setObjective("""
    根据与用户的对话展示不同的表情，以响应用户的情绪，
    让用户感受到理解、陪伴与情感共鸣，从而提升交流的舒适感和信任感。
""")
```

### 2. 情感Action实现 (MainActivity.kt)

每个情感Action都遵循相同的实现模式：

```kotlin
Action(
    name = "com.agent.demo.SHOW_SMILE_FACE",  // Action唯一标识
    displayName = "笑",                        // 显示名称
    desc = "响应用户的开心、满意或正面情绪",      // 功能描述(供AI理解)
    parameters = listOf(
        Parameter("sentence", ParameterType.STRING, "回复给用户的话", true)
    ),
    executor = object : ActionExecutor {
        override fun onExecute(action: Action, params: Bundle?): Boolean {
            showFaceImage(R.drawable.ic_smile)  // 显示对应表情
            handleAction(action, params)        // 处理TTS播放
            return true
        }
    }
)
```

### 3. 统一的Action处理逻辑

```kotlin
private fun handleAction(action: Action, params: Bundle?) {
    AOCoroutineScope.launch {
        // 播放AI回复的语音
        params?.getString("sentence")?.let { 
            AgentCore.ttsSync(it) 
        }
        // 通知Action执行完成
        action.notify(isTriggerFollowUp = false)
    }
}
```

## 🎮 Demo使用方法

### 基本操作流程
1. **启动应用** - 打开Demo应用，自动初始化Agent服务
2. **开始对话** - 通过语音说出你的感受
3. **观察反馈** - 查看屏幕上的表情变化和听取语音回复
4. **继续交互** - 尝试不同的情绪表达，体验多样化反馈

### 测试用例示例

**测试开心情绪：**
- 用户说："我今天心情特别好！"
- 预期效果：显示😊笑脸 + 播放积极回应

**测试难过情绪：**  
- 用户说："我最近很难过..."
- 预期效果：显示😢哭脸 + 播放安慰话语

**测试生气情绪：**
- 用户说："这件事太让我生气了！"  
- 预期效果：显示😠生气表情 + 播放理解回应

