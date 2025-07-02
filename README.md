# AgentSDK Android Demo项目

这个仓库包含了基于猎户星空 AgentOS SDK 开发的Android示例应用，展示了SDK在不同场景下的应用能力。

## 🔗 SDK文档链接

📚 **最新AgentOS SDK文档**: [https://github.com/orionagent/agentos-sdk](https://github.com/orionagent/agentos-sdk)

## 📱 Demo项目介绍

### 🎭 AgentRole - 角色扮演Demo
**AI角色扮演对话应用**

展示如何使用AgentOS SDK创建能够扮演不同历史人物和虚拟角色的智能对话应用。用户可以选择不同角色（李白、鲁迅、接待员等），与AI进行深度的角色扮演对话体验。

**核心功能：**
- 多角色动态切换
- 深度角色扮演对话
- 智能语音交互
- 角色人设管理

👉 **[查看详细文档](./AgentRole/README.md)**

---
> **🚨 Java版本实现**  
> 📁 **[AgentRoleJava](./AgentRoleJava/)** 目录提供相同功能的Java实现版本
---

### 😊 EmotiBot - 情感交互Demo
**情感识别与表情反馈应用**

展示如何使用AgentOS SDK创建能够理解用户情绪并做出相应表情反应的智能交互应用。通过语音输入识别用户情感状态，并显示对应的表情图标和语音回应。

**核心功能：**
- 情感智能识别
- 表情动态反馈
- 语音温暖回应
- 多种情感场景支持

👉 **[查看详细文档](./EmotiBot/README.md)**

### 🚪 GateControl - 门禁控制Demo
**语音门禁控制应用（模拟版）**

展示如何使用AgentOS SDK创建语音控制门禁系统的智能应用。通过自然语言指令和授权码验证，实现语音开门控制功能。采用模拟模式运行，通过Toast提示演示完整交互流程。

**核心功能：**
- 语音指令识别（"开门"、"开闸"等）
- 4位数字授权码语音识别
- 模拟门禁控制操作
- 现代化UI设计（Jetpack Compose）
- 统一常量管理和日志标准

👉 **[查看详细文档](./GateControl/README.md)**

---

## 📋 开发环境要求

- **Android SDK**: 最低支持API 26 (Android 8.0)
- **JDK版本**: Java 11
- **开发语言**: Kotlin、Java
- **UI框架**: Jetpack Compose（部分项目）
- **运行环境**: AgentOS Product Version V1.2.0.250515

## 🛠 项目技术栈

| 项目 | 开发语言 | UI框架 | 特色功能 |
|------|---------|--------|----------|
| **AgentRole** | Kotlin | Jetpack Compose | 角色扮演对话 |
| **AgentRoleJava** | Java | 传统View | Java版本实现 |
| **EmotiBot** | Kotlin | 传统View | 情感识别反馈 |
| **GateControl** | Kotlin | Jetpack Compose | 语音门禁控制 |

---

**🤖 开始探索AgentOS SDK的强大能力吧！** 