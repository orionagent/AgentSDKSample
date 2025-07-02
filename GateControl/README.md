# AgentOS SDK 门禁控制示例项目（模拟版）

[![Android API](https://img.shields.io/badge/API-26%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=26)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.8-blue.svg)](https://kotlinlang.org)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Simulation Mode](https://img.shields.io/badge/Mode-Simulation-orange.svg)](README.md#模拟模式说明)

这是一个基于 AgentOS SDK 开发的Android门禁控制示例应用，展示了如何使用语音交互来控制门禁系统。项目采用模拟模式运行，通过Toast提示演示完整的语音交互流程，非常适合学习和演示AgentOS SDK的核心功能。

## 🎯 项目简介

本项目演示了以下核心功能：
- **语音交互控制**：通过自然语言控制门禁开关
- **授权码验证**：语音识别4位数字授权码
- **模拟开门操作**：Toast提示模拟门禁控制效果
- **现代UI设计**：使用Jetpack Compose构建的现代化界面
- **代码规范化**：常量抽象和日志标准化

## 🛠 技术栈

- **开发语言**：Kotlin
- **UI框架**：Jetpack Compose
- **最低API**：Android API 26 (Android 8.0)
- **编译SDK**：Android API 35
- **核心依赖**：
  - AgentOS SDK 0.3.2-SNAPSHOT
  - Jetpack Compose BOM
  - Coroutines

## 🚀 快速开始

### 环境要求

- Android Studio Hedgehog | 2023.1.1 或更高版本
- JDK 11 或更高版本
- Android SDK API 26+

### 安装步骤

1. **克隆项目**
   ```bash
   git clone https://github.com/orionagent/agent-sdk-samples.git
   cd GateControl
   ```

2. **配置Maven仓库**
   项目已配置AgentOS SDK的Maven仓库，无需额外配置。

3. **同步项目**
   在Android Studio中打开项目，等待Gradle同步完成。

4. **运行项目**
   连接Android设备或启动模拟器，点击运行按钮。

## 📱 功能特性

### 语音交互功能
- 🎤 **语音识别**：支持"开门"、"开闸"、"打开门禁"等自然语言指令
- 🔢 **授权码识别**：自动识别用户说出的4位数字授权码
- 🔊 **语音反馈**：提供实时的语音状态反馈

### 门禁控制功能
- 🚪 **模拟开门**：Toast提示模拟开门控制过程
- 🔐 **权限验证**：验证授权码的有效性
- 📱 **手动控制**：支持触屏手动输入授权码
- 📋 **代码规范**：统一的常量管理和日志标准

### 用户界面
- 🎨 **现代设计**：基于Material Design 3的现代化界面
- 📱 **响应式布局**：适配不同屏幕尺寸
- 🌈 **主题支持**：支持系统深色模式

### 代码质量
- 📋 **统一常量管理**：所有配置集中在Constants.kt中管理
- 📝 **标准化日志**：统一使用LOG_TAG标签，便于调试过滤
- 🏗️ **清晰架构**：MainActivity专注UI，MainApplication处理Agent初始化
- 🔧 **易于配置**：通过修改常量即可快速调整应用行为
- 🎯 **模拟友好**：便于演示和学习的模拟模式设计

## 🏗 项目结构

```
app/src/main/
├── java/com/example/gatecontrol/
│   ├── MainActivity.kt              # 主Activity，UI和语音交互逻辑
│   ├── MainApplication.kt           # 应用初始化，AppAgent配置
│   ├── ui/theme/                    # UI主题配置
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   └── utils/
│       └── Constants.kt            # 统一常量管理
├── assets/
│   └── actionRegistry.json         # Agent动作注册配置
└── res/
    ├── values/strings.xml          # 字符串资源
    └── ...                         # 其他资源文件
```

## 🎯 核心实现

### 1. AppAgent初始化

```kotlin
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        object : AppAgent(this) {
            override fun onCreate() {
                setPersona("你是猎户星空的门卫安保人员...")
                setObjective("识别到访者的意图并要求提供授权码...")
            }
        }
    }
}
```

### 2. PageAgent语音指令处理

```kotlin
PageAgent(this)
    .registerAction(
        Action(
            name = "action.orionstar.OPEN_DOOR_WITH_AUTH_CODE",
            displayName = "语音控制开门",
            parameters = listOf(
                Parameter("auth_code", ParameterType.NUMBER_ARRAY, "4位数字授权码", true)
            ),
            executor = VoiceDoorControlExecutor()
        )
    )
```

### 3. 模拟开门操作

```kotlin
fun sendUdpCommand(hexData: String, ipAddress: String) {
    scope.launch {
        isLoading = true
        Log.d(Constants.LOG_TAG, "模拟发送UDP命令: $hexData 到 $ipAddress")
        
        // 显示开门中的Toast
        Toast.makeText(context, Constants.TOAST_DOOR_OPENING, Toast.LENGTH_SHORT).show()
        
        // 模拟网络延迟
        withContext(Dispatchers.IO) {
            kotlinx.coroutines.delay(1500)
        }
        
        // 显示成功的Toast
        Toast.makeText(context, Constants.TOAST_DOOR_OPENED, Toast.LENGTH_LONG).show()
        isLoading = false
    }
}
```

## ⚙️ 配置说明

### actionRegistry.json
配置Agent应用的基本信息和可用动作：

```json
{
  "appId": "app_gate_control_sample",
  "platform": "apk",
  "actionList": []
}
```

### 常量配置
在`Constants.kt`中管理所有应用常量：

```kotlin
object Constants {
    // 日志标签
    const val LOG_TAG = "GateControl"
    
    // 网络配置（模拟模式）
    const val SERVER_IP = "x.x.x.x"
    const val SERVER_PORT = 9999
    const val UDP_COMMAND_OPEN_DOOR = "01"
    
    // 授权码配置
    const val VALID_AUTH_CODE = "2025"
    const val AUTH_CODE_LENGTH = 4
    
    // Toast消息
    const val TOAST_DOOR_OPENING = "正在开门..."
    const val TOAST_DOOR_OPENED = "闸机已打开！"
    
    // TTS语音反馈
    const val TTS_AUTH_SUCCESS_OPENING = "授权码验证成功，正在开门"
    // ... 更多常量
}
```

## 🎭 模拟模式说明

本示例项目采用**模拟模式**运行，便于演示和学习AgentOS SDK的使用方法：

### 当前模拟功能
- ✅ **Toast提示**：使用Toast消息模拟开门操作反馈
- ✅ **延迟模拟**：1.5秒延迟模拟真实网络操作
- ✅ **日志记录**：完整的操作日志，便于调试
- ✅ **状态管理**：保持真实的UI状态变化

### 模拟流程
1. 用户输入授权码或语音控制
2. 系统验证授权码（默认：2025）
3. 显示"正在开门..."Toast
4. 模拟1.5秒网络延迟
5. 显示"闸机已打开！"成功Toast
6. 语音反馈确认操作完成

### 切换到生产模式
要启用真实的UDP门禁控制，需要：

1. **修改sendUdpCommand函数**（MainActivity.kt）：
```kotlin
// 将模拟代码替换为真实UDP发送
val socket = DatagramSocket()
val address = InetAddress.getByName(Constants.SERVER_IP)
val data = hexData.toByteArray(Charsets.UTF_8)
val packet = DatagramPacket(data, data.size, address, Constants.SERVER_PORT)
socket.send(packet)
socket.close()
```

2. **配置正确的服务器地址**（Constants.kt）：
```kotlin
const val SERVER_IP = "your.server.ip"
const val SERVER_PORT = 9999
```

3. **添加网络权限**（AndroidManifest.xml）：
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## 🔧 自定义配置

### 修改授权码
在`Constants.kt`中修改`VALID_AUTH_CODE`常量。

### 切换到真实UDP控制
如需使用真实UDP控制，可在`sendUdpCommand`函数中：
1. 移除Toast提示和延迟模拟
2. 恢复UDP socket发送逻辑
3. 在`Constants.kt`中配置正确的服务器地址

### 自定义语音指令
在`MainActivity.kt`的`initPageAgent()`方法中修改Action配置。

### 日志配置
所有日志统一使用`Constants.LOG_TAG`标签，便于调试和过滤。

## 🐛 常见问题

### Q: 编译失败，提示找不到AgentOS SDK
A: 确保已正确配置Maven仓库，检查网络连接是否正常。

### Q: 语音指令无响应
A: 确保设备支持语音输入，检查麦克风权限。

### Q: 模拟开门没有反应
A: 确保授权码输入正确（默认：2025），检查Toast权限。

### Q: 如何启用真实UDP控制
A: 修改`sendUdpCommand`函数，移除模拟逻辑并恢复UDP socket代码。

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🤝 贡献

欢迎提交 Issue 和 Pull Request 来改善这个项目。

## 📞 联系我们

- 官网：https://www.orionstar.com
- 邮箱：support@orionstar.com
- GitHub：https://github.com/orionagent/agentos-sdk

---

**注意**: 这是一个示例项目，仅用于演示AgentOS SDK的使用方法，请勿直接用于生产环境。 