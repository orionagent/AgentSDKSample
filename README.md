# Agent SDK for APK V0.2.2文档

1. 概述
Agent SDK 是一个用于机器人交互的Android开发套件，提供了应用与机器人Agent服务进行通信的能力。SDK支持应用级和页面级的Agent开发，可以实现自然语言交互、动作规划和执行等功能。
SDK仓库地址：https://github.com/orionagent/agent-sdk
1.1 环境要求
1.1.1 开发环境
- Android SDK 版本: 最低支持API 26 (Android 8.0)
- JDK版本: Java 11
- Kotlin、Java语言开发
- 建议开发环境：
  - Android Studio：基础环境构建和调试
  - Cursor：提供AI编码能力
  
1.1.2 运行环境
AgentOS Product Version: V1.2.0.250515

1.2 快速开始
如果没有任何Android开发经验，请先下载安装Android Studio，然后下载我们提供的空项目，用Android Studio打开此空项目后再开始接下来的步骤。
1.2.1 配置仓库
如果你的gradle脚本使用的Groovy，那在项目根目录下会有一个settings.gradle文件，在此文件中以下位置添加maven配置（以下代码中加粗的部分）
dependencyResolutionManagement {
        repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
        repositories {
                mavenCentral()
                maven { url 'https://jitpack.io' }
        }
}
如果你的gradle脚本使用的是Kotlin，那么在项目根目录下会有一个settings.gradle.kts文件，在此文件中以下位置添加maven配置（以下代码中加粗的部分）
dependencyResolutionManagement {
        repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
        repositories {
                mavenCentral()
                maven { url = uri("https://jitpack.io") }
        }
}
1.2.2 添加依赖
如果你的gradle脚本使用的Groovy，那在项目根目录的app/目录下会有一个build.gradle文件，在此文件中以下位置添加maven配置（以下代码中加粗的部分）
dependencies {
        implementation 'com.github.orionagent:agent-sdk:0.2.2'
        
        // 以下是Android标准库，默认kotlin项目都会依赖，
        // 如果编译报未找到错误，再添加以下依赖库
        implementation 'androidx.core:core-ktx:1.13.1'
        implementation 'androidx.appcompat:appcompat:1.6.1'
}
如果你的gradle脚本使用的是Kotlin，那么在项目根目录的app/目录下会有一个build.gradle.kts文件，在此文件中以下位置添加maven配置（以下代码中加粗的部分）
dependencies {
        implementation("com.github.orionagent:agent-sdk:0.2.2")
        
        // 以下是Android标准库，默认kotlin项目都会依赖，
        // 如果编译报未找到错误，再添加以下依赖库
        implementation("androidx.core:core-ktx:1.13.1")
        implementation("androidx.appcompat:appcompat:1.6.1")
}
1.2.3 添加注册表
查看项目根目录的app/src/main目录下，是否包含assets目录，如果没有请先创建assets目录，然后在assets目录下创建actionRegistry.json文件，并在文件中添加以下配置
{
  "appId": "app_ebbd1e6e22d6499eb9c220daf095d465",
  "platform": "apk",
  "actionList": []
}
appId：Agent应用的appId，需在接待后台申请
platform：当前运行的平台，如：opk或apk
actionList：可以从外部调起的action（只能是app级），在注册表中声名的action需要在AppAgent的onExecuteAction方法中处理action的执行，注：如果不想对外暴露action，actionList可以设置为空数组[]

---
在这个项目中，我们将一起开发一个有个性、能感知情绪的虚拟助手。她不仅能和你对话，还能察觉你的情绪变化，并做出恰当回应——是的，她不再是冷冰冰的程序，而是一位会关心你感受的“豹姐姐”！
1.2.4 添加AppAgent
注意事项
1. 一个App中只能有一个AppAgent实例
在这里，我们为虚拟助手：豹姐姐，进行角色人设、角色目标等基础设定。
在项目的MainApplication的onCreate方法中添加以下代码（加粗部分），如果没有MainApplication.kt文件，请参考示例项目
package com.ainirobot.agent.sample

import android.app.Application
import android.os.Bundle
import com.ainirobot.agent.AppAgent
import com.ainirobot.agent.action.Action

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        object : AppAgent(this) {
            
            override fun onCreate() {
                // 设定角色人设
                setPersona("你叫豹姐姐，是一位聪明、亲切又略带俏皮的虚拟助手。")
                // 设定角色目标
                setObjective("通过自然的对话和合适的情绪表达，让用户感受到理解、陪伴与情感共鸣，从而提升交流的舒适感和信任感。")
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
 到这一步，我们的 App 已经有了一个拥有“个性”的虚拟角色。接下来，我们要给她添加一些技能（Actoion），让她学会根据用户的情绪做出反应！
1.2.5 添加PageAgent
注意事项
1. 每个页面只能有一个PageAgent实例
示例：在此页面中添加三个Action分别表示感受到用户高兴、伤心、生气三种情绪，并根据不同情境跟用户对话
 设计思路：让助手“有情绪感知”
在日常交流中，人的情绪变化往往比语言更关键。本节我们为助手定义几个情绪感知型技能（Action）
暂时无法在飞书文档外展示此内容
你可以把它理解成：“当助手感知到用户的某种情绪（用户输入），就会下发一个对应的 Action，让虚拟助手用语音和表情做出回应”。

在MainActivity.kt中添加以下代码（代码中只添加了一个显示表情的Action，你要自己动手添加另外两个）
package com.ainirobot.agent.sample

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ainirobot.agent.AgentCore
import com.ainirobot.agent.PageAgent
import com.ainirobot.agent.action.Action
import com.ainirobot.agent.action.ActionExecutor
import com.ainirobot.agent.base.Parameter
import com.ainirobot.agent.base.ParameterType
import com.ainirobot.agent.coroutine.AOCoroutineScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 添加页面级Agent
        PageAgent(this)
            .registerAction(
                Action(
                    name = "com.agent.demo.SHOW_SMILE_FACE",
                    displayName = "笑",
                    desc = "响应用户的开心、满意或正面情绪",
                    parameters = listOf(
                        Parameter(
                            "sentence",
                            ParameterType.STRING,
                            "回复给用户的话",
                            true
                        )
                    ),
                    executor = object : ActionExecutor {

                        override fun onExecute(action: Action, params: Bundle?): Boolean {
                            AOCoroutineScope.launch {
                                // 展示笑脸
                                showFaceImage(R.drawable.ic_smile)
                                // 播放给用户说的话
                                params?.getString("sentence")?.let { AgentCore.ttsSync(it) }
                                // 播放完成后，及时上报Action的执行状态
                                action.notify(isTriggerFollowUp = false)
                            }
                            return true
                        }
                    }
                )
            )
    }
}
注意：在任何一个Action执行完成后都需要调用action的nofity()方法
现在你完成了一个能察觉你的情绪变化，并做出恰当回应的“豹姐姐”助手
1.2.6 完整示例
上边的QUICK START是为了方便接入，下面的示例项目也很简单，但添加了不同场景显示不同表情的功能，可能会更有趣一些，可以直接下载运行。

1.2.7 总结
开启一个基于Agent SDK的App ，主要有以下主要核心步骤。
1. 集成Agent SDK（配置仓库、添加依赖、创建注册表）
2. 设定App的人设、设定App级别的目标
3. 创建业务页面，并设置页面级的目标，上传页面信息
4. （很重要，这是你的应用和AgentOS交互的核心环节）基于App全局/特定页面，定义并注册Action到Agent SDK，可以同时注册多个Action。
  - 第一步：定义Action的名字，描述，参数及描述
  - 第二步：实现Action的处理逻辑
  - 第三步：通过语音触发，看看是否能顺利规划并且执行到你定义的Action

2. Action详解
2.1 什么是Action
AgentOS的核心是识别用户的意图执行合适的技能，而这个技能即为Action，如：用户问“我明天去深圳需要带伞吗？”，识别用户的意图是查询天气，那调用对应的查天气的技能（Action），比如：orion.agent.action.WEATHER
2.1.1 基础属性
Action下基础属性及描述如下：
package com.ainirobot.agent.action

class Action(
    /**
     * action全名，结构最好是公司域名+action简名，避免与其它app中的action冲突
     * action简名必须大写，示例：com.orion.action.WEATHER
     */
    name: String,
    /**
     * 当前应用的appId
     */
    appId: String,
    /**
     * action显示名称，可能被用于显示到UI界面上，可以是中文等
     */
    displayName: String,
    /**
     * action描述，用以让大模型理解应该在什么时间调用此action
     */
    desc: String,
    /**
     * 期望action在被规划出时携带的参数描述
     */
    parameters: List<Parameter>?,
    /**
     * action对应的执行器，当action规划完成后会回调此接口
     */
    @Transient
    var executor: ActionExecutor?
)
注：创建Action时需要清晰描述Action的各项属性，方便大模型理解Action的功能，能够更精确的选择合适的Action
2.1.2 Action参数
Action参数是想让大模型从用户的Query中抽取的核心内容，如：“我想查一下北京今天的天气”，那么可以从中抽取city和date两个字段。
参数描述对象的基本属性如下：
data class Parameter(
    /**
     * 参数名
     */
    val name: String,
    /**
     * 参数类型
     */
    val type: ParameterType,
    /**
     * 参数描述
     */
    val desc: String,
    /**
     * 是否是必要参数
     */
    val required: Boolean,
    /**
     * 当type为enum时，需要传此参数，作为枚举值选择的列表
     */
    var enumValues: List<String>? = null
)
注：参数的desc也要能精确反应此参数的定义，让大模型的理解更精准；而对于参数的name最好使用英文单词，多个单词间以下划线_连接；另外，name一定不要与Action对象或Parameter对象的属性名相同或类似，避免出现歧义，这非常重要！！！这非常重要！！！这非常重要！！！
2.2 Action注册
Action分为App级和Page级两种，区别在于其活跃的生命周期不同。
2.2.1 App级Action
App级Action即为全局Action，Action在整个应用运行（处于前台）期间都会被响应，如果应用退出或进入后台则不会被响应，App级的Action注册支持动态注册和静态注册两种。
动态注册
动态注册的App级Action，是为了在应用整个生命周期内响应用户的实时意图，在应用处于前台期间一直生效，应用退出或进入后台时则不再响应。需要在AppAgent的onCreate方法中调用注册，如以下示例，注册了一个退出的Action：
// 添加应用级Agent
object : AppAgent(this) {

    /**
     * AppAgent初始化的回调
     * 需要动态注册的App级Action，可以此方法中注册
     */
    override fun onCreate() {
        // 动态注册Action
        // 示例：此处注册了系统Action：EXIT，当用户说“退出”时，会触发BACK事件
        registerAction(Actions.EXIT)
    }
}
动态注册支持注册在当前应用中新创建的私有Action，也支持注册外部的Action，如：系统Action或者其它AgentOS App（集成AgentSDK的应用）中静态注册的Action。
以下示例，注册一个打开天气App中的打开天气首页Action：
// 注册一个查天气的Action，前提是你已经安装了包含此Action的AgentOS应用（必须是在注册表中静态注册才可以）
registerAction(
    Action("com.agent.tool.WEATHER_HOME").also { 
        it.executor = object : ActionExecutor {
            
            override fun onExecute(action: Action, params: Bundle?): Boolean {
                // 如果你不需要处理，请返回false，如果要自行处理且不需要后续处理，则返回true
                // 此处只打印了一个日志，所以返回false，结果就是会调起天气App查询天气
                // 如果你想自己查天气，那么就需要以此处调用自己的查天气接口，并返回true即可
                println("用户刚查了天气")
                return false
            }
        }
    }
)

静态注册
首先，只有App级的Action才可以静态注册，静态注册的Action是为了向外部提供调起当前应用的入口，默认并不会在当前App运行期间生效，如果想要在当前App中也生效，需要再次动态注册即可。
静态注册必须在actionRegistry.json注册表中配置，并添加详细的参数描述等。
以下示例，是在天气App中向外部提供一个可以打开天气首页查天气的Action：
{
  "appId": "app_43e38d01cfad05d3bb2d8ce3a66f7aa2",
  "platform": "apk",
  "actionList": [
    {
      "name": "com.agent.tool.WEATHER_HOME",
      "displayName": "打开查询天气的首页",
      "desc": "当用户想要询问天气时，显示天气情况",
      "parameters": [
        {
          "name": "city",
          "type": "string",
          "desc": "要查询哪个城市的天气",
          "required": false
        },
        {
          "name": "date",
          "type": "string",
          "desc": "要查询什么日期的天气",
          "required": false
        }
      ]
    }
  ]
}
注：required为false，表示参数可以为空，如果为空时需要执行端自行处理，如：使用当前定位的城市，时间默认为今天等。

静态注册的Action，最终的执行器是在AppAgent的onExecuteAction方法中，如果对外公开了多个Action，则需要通过actionName判断不同的Action并分别处理。
以下还是天气App为例，我们在上一步中，已经在天气App的注册表中添加了com.agent.tool.WEATHER_HOME的Action，那天气App中AgentAgent的onExecuteAction方法必须处理此Action。
以下示例
object : AppAgent(this) {
    /**
     * actionRegistry.json注册表中静态注册的action需要执行的回调
     * 注：只有可以被外部调用的action才可以使用静态注册，且此方法只能是被外部（其它app）调用时才会执行
     */
    override fun onExecuteAction(
        action: Action,
        params: Bundle?
    ): Boolean {
        return when (action.name) {
            "com.agent.tool.WEATHER_HOME" -> {
                // 打开天气首页
                startWeatherHomePage(action, params)
                true
            }
            else -> false
        }
    }
}
注：简单来说，静态注册的Action为了让外部调用，动态注册的Action为了让当前应用内部调用
2.2.2 Page级Action
Page级的Action需要在页面（Activity或Fragment）初始化时声名，且只在当前页面对用户可见时生效，当页面退出或者被其它页面覆盖则不再生效
动态注册
因为Page级Action只在当前页面生效，所以它只能动态注册，不能在注册表中注册，即不能向外部提供接口
以下示例定义了三个Action，根据用户的情绪显示三种不同的表情
在Activity的onCreate方法中创建
PageAgent(this)
    .blockAction("com.xxx.yyy.TTT") // 排除指定Action
    .blockActions( // 排除指定Action列表
        listOf(
            "com.xxx.yyy.TTT",
            "com.xxx.yyy.RRR",
        )
    )
    .blockAllActions() // 排除所有Action
    .registerAction(
        Action(
            name = "com.agent.demo.SHOW_SMILE_FACE",
            displayName = "笑",
            desc = "响应用户的开心、满意或正面情绪",
            parameters = listOf(
                Parameter(
                    "sentence",
                    ParameterType.STRING,
                    "回复给用户的话",
                    true
                )
            ),
            executor = object : ActionExecutor {

                override fun onExecute(action: Action, params: Bundle?): Boolean {
                    showFaceImage(R.drawable.ic_smile)
                    handleAction(action, params)
                    return true
                }
            }
        )
    )
    .registerAction(
        Action(
            name = "com.agent.demo.SHOW_CRY_FACE",
            displayName = "哭",
            desc = "响应用户的难过、失落或求助情绪",
            parameters = listOf(
                Parameter(
                    "sentence",
                    ParameterType.STRING,
                    "回复给用户的话，给于安慰",
                    true
                )
            ),
            executor = object : ActionExecutor {

                override fun onExecute(action: Action, params: Bundle?): Boolean {
                    showFaceImage(R.drawable.ic_cry)
                    handleAction(action, params)
                    return true
                }
            }
        )
    )
    .registerAction(
        Action(
            name = "com.agent.demo.SHOW_ANGRY_FACE",
            displayName = "生气",
            desc = "响应用户的愤怒、不满或投诉情绪",
            parameters = listOf(
                Parameter(
                    "sentence",
                    ParameterType.STRING,
                    "回复给用户的话，尽可能消除用户的负面情绪",
                    true
                )
            ),
            executor = object : ActionExecutor {

                override fun onExecute(action: Action, params: Bundle?): Boolean {
                    showFaceImage(R.drawable.ic_angry)
                    handleAction(action, params)
                    return true
                }
            }
        )
    )
Action过滤
如果你在App级注册了N个全局的Action，但在当前页面上不想规划其中一个或多个全局Action，那么可以通过以下方式排除掉指定的全局Action
过滤指定的一个Action
// 过滤掉指定的全局Action，参数为Action的name
pageAgent.blockAction("com.xxx.yyy.TTT")
过滤指定的多个Action
// 过滤的Action列表
pageAgent.blockActions(
    listOf(
        "com.xxx.yyy.TTT",
        "com.xxx.yyy.RRR",
    )
)
过滤所有Action
// 过滤掉所有在AppAgent中注册的全局Action
// 仅当前页面注册的Action生效
pageAgent.blockAllActions()
2.3 Action执行
Action执行上面已经说的很详细，此处只是为了再强调一下
2.3.1 执行回调
动态注册Action的具体执行，需要为Action对象设置一个执行器，在执行器中添加你要执行的代码，如：
Action(
    name = "com.agent.demo.SHOW_ANGRY_FACE",
    displayName = "生气",
    desc = "响应用户的愤怒、不满或投诉情绪",
    parameters = listOf(
        Parameter(
            "sentence",
            ParameterType.STRING,
            "回复给用户的话，尽可能消除用户的负面情绪",
            true
        )
    ),
    executor = object : ActionExecutor {

        override fun onExecute(action: Action, params: Bundle?): Boolean {
            showFaceImage(R.drawable.ic_angry)
            handleAction(action, params)
            return true
        }
    }
)
静态注册Action的具体执行，需要在AppAgent的onExecuteAction方法中实现，如：
override fun onExecuteAction(
    action: Action,
    params: Bundle?
): Boolean {
    return false
}
注：不管是哪种注册方式，执行的回调都带有一个Boolean的返回值，表示你是否已经处理过此Action，如果你不处理，请返回false，如果要自定义处理且不需要后续处理，则返回true
2.3.2 执行结果
这非常重要，必不可少！这非常重要，必不可少！这非常重要，必不可少！这非常重要，必不可少！这非常重要，必不可少！这非常重要，必不可少！这非常重要，必不可少！这非常重要，必不可少！这非常重要，必不可少！！！
1. 首先，任何Action的执行回调方法中都不能执行耗时操作。
2. 其次，如果你要处理一个Action，除了在执行的回调方法返回值返回true之外，还需要在Action执行完成后手动调用action的成员方法nofity()把执行状态或结果同步给系统，具体的时机用户可以自行定义，如：页面加载完成、天气播报完成、到达一个目的地等。
3. 最后，执行的回调方法默认都是子线程。
notify是Action类的成员方法，说明如下：
package com.ainirobot.agent.action

/**
 * Action执行完成后需要同步执行结果
 *
 * @param result Action的执行结果
 * @param isTriggerFollowUp 在Action执行完成后主动引导用户进行下一步操作，默认开启
 */
fun notify(
    result: ActionResult = ActionResult(ActionStatus.SUCCEEDED),
    isTriggerFollowUp: Boolean = true
)
2.4 系统内置Action
系统Action是系统内置的功能Action，包含部分系统的功能、指令和应用等，系统Action的namespace是orion.agent.action。
系统Action并不是都由系统实现了执行逻辑，有些还是需要用户自行处理逻辑，只是系统内置了这些Action的定义而已，如：orion.agent.action.CLICK（点击事件）
1. 系统处理的Action
package com.ainirobot.agent.action

object Actions {
    /**
     * 调整系统音量
     */
    const val SET_VOLUME = "orion.agent.action.SET_VOLUME"
    /**
     * 机器人兜底对话
     */
    const val SAY = "orion.agent.action.SAY"
    /**
     * 取消
     */
    const val CANCEL = "orion.agent.action.CANCEL"
    /**
     * 返回
     */
    const val BACK = "orion.agent.action.BACK"
    /**
     * 退出
     */
    const val EXIT = "orion.agent.action.EXIT"
    /**
     * 知识库问答
     */
    const val KNOWLEDGE_QA = "orion.agent.action.KNOWLEDGE_QA"
    /**
     * 对用户说一句欢迎或者欢送语
     */
    const val GENERATE_MESSAGE = "orion.agent.action.GENERATE_MESSAGE"
    /**
     * 调整机器人速度
     */
    const val ADJUST_SPEED = "orion.agent.action.ADJUST_SPEED"
}
注：CANCEL、BACK和EXIT默认处理为模拟点击Back键
2. 需用户处理的Action
package com.ainirobot.agent.action

object Actions {
    /**
     * 确定
     */
    const val CONFIRM = "orion.agent.action.CONFIRM"
    /**
     * 点击
     */
    const val CLICK = "orion.agent.action.CLICK"
}
3. 核心功能接口
1. 麦克风开关
介绍
- 默认只要集成了AgentSDK的应用，在应用进入前台时会自动打开麦克风，应用退出或进入后台会自动关闭麦克风，当然你也可以自行开启和关闭
应用场景
- 需要手动闭麦的场景。
- 播放音视频时防止干扰时。

import com.ainirobot.agent.AgentCore

// 设置麦克风静音状态
AgentCore.isMicrophoneMuted = true // 静音
AgentCore.isMicrophoneMuted = false // 取消静音
2. 获取ASR和TTS的结果
介绍
- 通过设置setOnTranscribeListener监听系统回调的方式，获取到对话交互的内容。
应用场景
- 如果你的应用想获取到ASR识别/TTS播报的内容，可以通过以下方法。

PageAgent(this).setOnTranscribeListener(object : OnTranscribeListener {
        override fun onTranscribe(transcription: Transcription): Boolean {
            // ASR/TTS 内容    
            val text = transcription.text
            
            // 当前的发言人
            val speaker = transcription.speaker
            
            // 是否是用户在说话。true为ASR结果，false为TTS结果
            val userSpeaking = transcription.isUserSpeaking
            
            // 是否是最终的结果（为false时，代表时中间流式结果）
            val finalPack = transcription.final
            
            return true
        }
    })
- setOnTranscribeListener是AppAgent和PageAgent的成员方法。

获取ASR/TTS 内容  
- transcription.text 是文本内容。

判断是ASR还是TTS内容
- 通过transcription.isUserSpeaking判断，true时为ASR结果，false为TTS结果。

判断是流式结果还是最终结果
- 通过transcription.final判断，true为最终结果，false为中间结果。

onTranscribe 回调函数返回值的设定
- 返回true时，代表你告知系统你消费了此次结果，系统将不再把字幕显示在底部的字幕条上。
- （建议）返回false时，将不影响后续系统对于ASR/TTS结果的分发。

注意：onTranscribe 回调是在子线程中。
3. 播放TTS/停止播放TTS
介绍
- 主动调用系统的接口，合成指定文本为音频，并自动进行播报
应用场景
- 需要在一些业务流程中，驱动机器人说出某一句话时，比如应用启动时，让机器人播报“欢迎光临”，就可以主动调用。

播报TTS
import com.ainirobot.agent.AgentCore

/**
 * TTS接口，同步调用
 * 注：此接口需在协程中调用
 *
 * @param text 要播放的文本
 * @param timeoutMillis 超时时间，单位毫秒
 *
 * @return 返回1表示成功，返回0表示失败
 */
suspend fun ttsSync(text: String, timeoutMillis: Long = 180000): Int {
    return this.appAgent?.api?.ttsSync(text, timeoutMillis) ?: 0
}
import com.ainirobot.agent.AgentCore

/**
 * TTS接口，异步调用，返回状态通过TaskCallback回调
 *
 * @param text 要播放的文本
 * @param timeoutMillis 超时时间，单位毫秒
 * @param callback 回调，status=1表示播放成功，status=0表示播放失败
 */
fun tts(
    text: String,
    timeoutMillis: Long = 180000,
    callback: TaskCallback? = null
) {
    this.appAgent?.api?.tts(text, timeoutMillis, callback)
}
停止播报
import com.ainirobot.agent.AgentCore

/**
 * 强制打断TTS播放
 */
fun stopTTS() {
    this.appAgent?.api?.stopTTS()
}
4. 大模型接口
介绍
- 通过大模型接口，会跳过AgentOS的意图理解和任务规划的环节，直接和原生的大模型进行对话。但会失去所有AgentOS拥有的功能特性和系统级能力。
应用场景
- 非必要不推荐直接调用，只当有一些特殊场景，不得不直接请求大模型时，可以选择。

- 同步接口调用
import com.ainirobot.agent.AgentCore

/**
 * 大模型接口，同步调用
 * 注：此接口需在协程中调用
 *
 * @param messages 大模型chat message
 * @param config 大模型配置
 * @param timeoutMillis 超时时间，单位毫秒
 *
 * @return 返回1表示成功，返回0表示失败
 */
suspend fun llmSync(
    messages: List<LLMMessage>,
    config: LLMConfig,
    timeoutMillis: Long = 180000
): Int {
    return this.appAgent?.api?.llmSync(messages, config, timeoutMillis) ?: 0
}
- 异步接口调用
import com.ainirobot.agent.AgentCore

/**
 * 大模型接口，异步调用，返回状态通过TaskCallback回调
 *
 * @param messages 大模型chat message
 * @param config 大模型配置
 * @param timeoutMillis 超时时间，单位毫秒
 * @param callback 回调，status=1表示播放成功，status=0表示播放失败
 */
fun llm(
    messages: List<LLMMessage>,
    config: LLMConfig,
    timeoutMillis: Long = 180000,
    callback: TaskCallback? = null
) {
    this.appAgent?.api?.llm(messages, config, timeoutMillis, callback)
}
5. 文本指令
- 介绍
  - 当你需要在没有用户语音交互的时候希望触发大模型的规划和执行时，推荐使用。
- 应用场景
  - 用户手动点击一个页面的按钮“确定”，等效于用户说了“确定”，通过QueryByText即可。
  - 比如应用启动页面，在用户开始交互之前，主动去跟用户交互，可以通过QueryByText去驱动。
import com.ainirobot.agent.AgentCore

/**
 * 通过文本形式的用户问题触发大模型规划Action
 *
 * @param text 用户问题的文本，如：今天天气怎么样？
 */
fun query(text: String) {
    this.appAgent?.api?.query(text)
}

6. 感知信息上报
- 介绍
  - 当你需要上传一些你的应用的场景的感知信息，辅助AgentOS去理解和规划你的任务时，建议调用。
- 应用场景
  - 比如你的屏幕上有很多的信息，你希望AgentOS感知到用户看到的信息（比如用户可以问，我想看看第3个），你可以把屏幕的信息整理成一定的格式上报，比如当前任务进展、比如数据清单。
import com.ainirobot.agent.AgentCore

/**
 * 上传页面信息，方便大模型理解当前页面的内容
 *
 * @param interfaceInfo 页面信息描述，最好带有页面组件的层次结构，但内容不宜过长
 */
fun uploadInterfaceInfo(interfaceInfo: String) {
    this.appAgent?.api?.uploadInterfaceInfo(interfaceInfo)
}

7. 清空对话历史
介绍
- 清空上下文对话历史，防止之前的对话干扰到新的对话。
应用场景
- 比如应用重置了对话的内容，用户更换了一个话题等等


import com.ainirobot.agent.AgentCore

/**
 * 清空大模型对话上下文记录
 */
fun clearContext() {
    this.appAgent?.api?.clearContext()
}

4. 进阶功能
注解实现Action动态注册
如果觉得手动注册麻烦，那么我们还提供了更简单便捷的注册方式，只需要在Application、Activity或Fragment中添加成员方法并添加注解，那么SDK会在运行时自动识别这些Action并注册
App级动态注册
在应用内使用注解方式自动注册时，需要使用AppAgent(Application)构造方法，然后在Application中创建成员方法，最后添加注解即可
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // AppAgent初始化
        object : AppAgent(this@MainApplication) {

            override fun onCreate() {
                // 设定角色人设
                setPersona("你叫豹姐姐，是一位聪明、亲切又略带俏皮的虚拟助手。")
                // 设定角色目标
                setObjective("通过自然的对话和合适的情绪表达，让用户感受到理解、陪伴与情感共鸣，从而提升交流的舒适感和信任感。")
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

    @AgentAction(
        name = "com.agent.demo.SHOW_SMILE_FACE",
        displayName = "笑",
        desc = "响应用户的开心、满意或正面情绪"
    )
    private fun showSmileFace(
        action: Action,
        @ActionParameter(
            name = "sentence",
            desc = "回复给用户的话"
        )
        sentence: String
    ): Boolean {
        AOCoroutineScope.launch {
            // 播放给用户说的话
            AgentCore.ttsSync(sentence)
            // 播放完成后，及时上报Action的执行状态
            action.notify(isTriggerFollowUp = false)
        }
        return true
    }
}
Page级动态注册
在页面内部使用注解方式自动注册时，需要使用PageAgent(Activity)或PageAgent(Fragment)构造方法，然后在对应的Activity或Fragment中创建成员方法，最后添加注解即可
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // PageAgent初始化
        PageAgent(this)
    }

    @AgentAction(
        name = "com.agent.demo.SHOW_SMILE_FACE",
        displayName = "笑",
        desc = "响应用户的开心、满意或正面情绪"
    )
    private fun showSmileFace(
        action: Action,
        @ActionParameter(
            name = "sentence",
            desc = "回复给用户的话"
        )
        sentence: String
    ): Boolean {
        AOCoroutineScope.launch {
            // 播放给用户说的话
            AgentCore.ttsSync(sentence)
            // 播放完成后，及时上报Action的执行状态
            action.notify(isTriggerFollowUp = false)
        }
        return true
    }
}
注解类说明
注解类有两个：AgentAction和ActionParameter
AgentAction
AgentAction是作用于成员方法上，用来标记该方法是一个Action
package com.ainirobot.agent.annotations

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class AgentAction(
    /**
     * Action的名称
     */
    val name: String,
    /**
     * Action的描述
     */
    val desc: String,
    /**
     * Action的显示名称
     */
    val displayName: String
)
ActionParameter
ActionParameter是作用于方法参数上，用来标记Action的参数
package com.ainirobot.agent.annotations

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ActionParameter(
    /**
     * 参数名
     */
    val name: String,
    /**
     * 参数描述
     */
    val desc: String,
    /**
     * 是否是必要参数
     */
    val required: Boolean = true,
    /**
     * 限制参数的value只能从指定的值中选择
     */
    val enumValues: Array<String> = []
)
5. 项目源码
模版项目
https://github.com/orionagent/AgentSDKSampleEmpty
示例项目
https://github.com/orionagent/AgentSDKSample

6. 技术支持
如有任何问题，请联系技术支持团队。

TODO：可以在github项目下提交issue。@裴彦礼 







---
附录1
1. Robot SDK接入指引
什么情况下需要Robot SDK？
当目前通过AgentSDK 封装的系统级Action无法很好的满足你的应用需求时，您也可以依旧使用更加底层的Robot SDK进行项目的集成和开发。
例如：自定义系统的导航能力、调用系统的相机、判断定位状态等等

RobotSDK 提供了丰富的机器人业务 API 和能力组件，涵盖视觉识别、基础运动控制、地图导航等多项核心功能，全面支持机器人智能化应用的开发与实现。

1.1 你需要知道的概念
- 应用授权：只有当app成功连接上RobotOS，并且app界面在前端显示时，app才能成功被授权使用sdk，当app界面退到后台，app当即被挂起。
- 应用被挂起：机器人使用中会遇到一些系统事件，比如急停，低电，OTA，硬件异常等，当发生这些系统事件时，RobotOS会接管业务，这时前台的业务apk就会被挂起，收到onsuspend事件，业务apk也不再具有使用api的能力
- 应用恢复挂起：对应挂起事件，当系统事件消失后，RobotOS会把业务控制权交还给当前app，当前apk恢复使用RobotApi的能力

1.2 准备工作
开启开发者模式
出厂版本默认ADB关闭，只有通过以下方式可临时开启：
1. 在任何时候（包括自检异常），单指下拉>>狂点多次时间区域
暂时无法在飞书文档外展示此内容
2. 弹出动态密码输入页，该页面显示系统日期时间，动态密码的获取查看【查询动态密码】部分
- 动态密码输入正确：跳转至步骤三，可以进行adb设置。
- 动态密码输入错误：清空输入内容，停留在当前页面。
[图片]
3. 当“启用调试”被打开后，显示第二个菜单“持久调试”。注意“启动调试”在重启后会恢复默认
- “持久调试”菜单默认不显示，只有“启用调试”开启后，才显示。
- “持久调试”显示后，默认是未开启状态，需要手动开启。
- 当再次关闭“启用调试”开关后，“持久调试”自动设置为禁用，且菜单隐藏。
- 以上设置重启后才能生效
[图片]
为了方便开发者，还提供“打开MIMI”，“开启系统导航栏”，“打开设置”，三个附带的快捷功能。
查询动态密码
提供SN号，联系您的售前/售后技术支持

如何正确的启动apk
启动APK时，为确保APK被机器人底盘正确的授权并使用机器人功能，请使用RobotOS Home调起（参见下面视频），如果想开机即启动某个apk，在设置中设置“开机启动apk“即可。
暂时无法在飞书文档外展示此内容


1.3 SDK集成
Manifest.xml
<activity android:name=".MainActivity">
    <intent-filter>
        <action android:name="android.intent.action.MAIN"/>
        <category android:name="android.intent.category.LAUNCHER"/>
     </intent-filter>
     
     App如果需要在开机后默认启动进入，需要在Manifest中配置
     <intent-filter>
         <action android:name="action.orionstar.default.app" />
         <category android:name="android.intent.category.DEFAULT" />
     </intent-filter>
     
</activity>
权限清单
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="com.ainirobot.coreservice.robotSettingProvider" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
服务初始化
RobotApi.getInstance().connectServer(this, new ApiListener() {
    @Override
    public void handleApiDisabled() {
    }
    @Override
    public void handleApiConnected() {
        //Server已连接，设置接收请求的回调，包含语音指令、系统事件等
        RobotApi.getInstance().setCallback(new ModuleCallback());
    }
    @Override
    public void handleApiDisconnected() {
        //连接已断开
    }
});

public class ModuleCallback extends ModuleCallbackApi {
    @Override
    public boolean onSendRequest(int reqId, String reqType, String reqText, String reqParam)
            throws RemoteException {
        //接收语音指令,
        //reqTyp : 语音指令类型
        //reqText : 语音识别内容
        //reqParam : 语音指令参数
        return true;
    }
    @Override 
    public void onRecovery() 
            throws RemoteException {
           //控制权恢复，收到该事件后，重新恢复对机器人的控制

    }
    @Override 
    public void onSuspend() 
            throws RemoteException {
           //控制权被系统剥夺，收到该事件后，所有Api调用无效

    }

1.4 核心API
1.4.1 地图及位置
判断当前是否已定位
RobotApi.getInstance().isRobotEstimate(reqId, new CommandListener() {
    @Override
    public void onResult(int result, String message) {
        if (!"true".equals(message)) {
            //当前未定位
        } else {
            //当前已定位
        }
    }
});

设置定位（设置机器人初始坐标点）
try {
    JSONObject params = new JSONObject();
    //x坐标
    params.put(Definition.JSON_NAVI_POSITION_X, x);
    //y坐标
    params.put(Definition.JSON_NAVI_POSITION_Y, y);
    //z坐标
    params.put(Definition.JSON_NAVI_POSITION_THETA, theta);
    RobotApi.getInstance().setPoseEstimate(reqId, params.toString(), new CommandListener() {
        @Override
        public void onResult(int result, String message) {
            if ("succeed".equals(message)) {
                //定位成功
            }
        }
    });
} catch (JSONException e) {
    e.printStackTrace();
}
获取当前地图所有位置点
RobotApi.getInstance().getPlaceList(reqId, new CommandListener() {
    @Override
    public void onResult(int result, String message) {
        try {
            JSONArray jsonArray = new JSONArray(message);
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                //常用
                json.getString("name"); //位置名称
                json.getDouble("x"); //x坐标
                json.getDouble("y"); //y坐标
                //不常用
                json.getDouble("theta"); //面朝方向
                json.getString("id"); //位置id
                json.getLong("time");//更新时间
                json.getInt("status"); //0:正常区域，可以到 1:禁行区，不可以到 2:地图外，不可以到
            }
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }
    }
});

1.4.2 机器人导航
RobotApi.getInstance().startNavigation(reqId, destName, coordinateDeviation, time, object : ActionListener() {
    override fun onResult(status: Int, response: String) {
        when (status) {
            Definition.RESULT_OK -> {
                if ("true" == response) {
                    // 导航成功
                } else {
                    // 导航失败
                }
            }
        }
    }

    override fun onError(errorCode: Int, errorString: String) {
        when (errorCode) {
            Definition.ERROR_NOT_ESTIMATE -> {
                // 当前未定位
            }

            Definition.ERROR_IN_DESTINATION -> {
                // 当前机器人已经在目的地范围内
            }

            Definition.ERROR_DESTINATION_NOT_EXIST -> {
                // 导航目的地不存在
            }

            Definition.ERROR_DESTINATION_CAN_NOT_ARRAIVE -> {
                // 避障超时，目的地不能到达，超时时间通过参数设置
            }

            Definition.ACTION_RESPONSE_ALREADY_RUN -> {
                // 当前接口已经调用，请先停止，才能再次调用
            }

            Definition.ACTION_RESPONSE_REQUEST_RES_ERROR -> {
                // 已经有需要控制底盘的接口调用，请先停止，才能继续调用
            }
        }
    }

    override fun onStatusUpdate(status: Int, data: String) {
        when (status) {
            Definition.STATUS_NAVI_AVOID -> {
                // 当前路线已经被障碍物堵死
            }

            Definition.STATUS_NAVI_AVOID_END -> {
                // 障碍物已移除
            }
        }
    }
})
- 参数描述
  - destName：导航目的地名称（必须先通过setLocation设置）
  - pose：导航目的地坐标点
  - obsDistance：最大避障距离，距离目标的障碍物小于该值时，机器人停止，取值大于 0，默认 0.75，单位米。
  - coordinateDeviation：目的地范围，如果距离目的地在该范围内，则认为已到达，建议设置为0.2，单位为米。
  - time：避障超时时间，如果该时间内机器人的移动距离不超过0.1m，则导航失败，单位毫秒，建议30*1000。
  - linearSpeed：导航线速度，范围：0.1 ~ 0.85 m/s 默认值：0.7 m/s。
  - angularSpeed：导航角速度，范围：0.4 ~ 1.4 m/s 默认值：1.2 m/s 最终导航速度是结合线速度和角速度换算后得到，不同的线速度和角速度对导航运动方式有影响，建议线速度和角速度保持一定规律：angularSpeed = 0.4 + (linearSpeed – 0.1) / 3 * 4
  - isAdjustAngle：是否适应导航结束时朝向的角度。如传false，则归正到点位设置时的角度
  - destinationRange：目标点无法到达时，距离目标点多少距离即认为导航成功
  - wheelOverCurrentRetryCount：导航过程中轮子堵转尝试次数
  - multipleWaitTime：导航过程中如遇多机等待
  - priority：默认取 0 即可，取值范围0~30，值越大优先级越高；一般用于多个机器人在导航时候避让使用
  - linearAcceleration：导航线加速度，范围：0.4 ~ 0.8 m/s2 默认值：0.7 m/s2。
  - angularAcceleration：导航角加速度，范围：0.4 ~ 0.9 m/s2 默认值：0.8 m/s2 最终导航角加速度速度是通过线速度
  - 换算后得到，不同的线加速度和角加速度对导航运动方式有影响，建议线加速度和角加速度保持一定规律：angularAcceleration= (linearSpeed / 0.8)

1.4.3 直线运动
CommandListener motionListener = new CommandListener() {
    @Override
    public void onResult(int result, String message) {
        if ("succeed".equals(message)) {
            //调用成功
        } else {
            //调用失败
        }
    }
};

RobotApi.getInstance().goForward(reqId, speed, motionListener);
RobotApi.getInstance().goForward(reqId, speed, distance, motionListener);
RobotApi.getInstance().goForward(reqId, speed, distance, avoid, motionListener);
RobotApi.getInstance().goBackward(reqId, speed, motionListener);
RobotApi.getInstance().goBackward(reqId, speed, distance, motionListener);

1.4.4 视觉能力
Person主要信息
private int id; //人脸本地识别id，此id可用于焦点跟随等。
    private double distance; //距离
    private double faceAngleX; //人脸x轴角度
    private double faceAngleY; //人脸y轴角度
    private int headSpeed; //当前机器人头部转动速度
    private long latency; //数据延迟
    private int facewidth; //人脸宽度
    private int faceheight; //人脸高度
    private int faceX; //人脸x坐标
    private int faceY; //人脸y坐标
    private int bodyX; //身体x坐标
    private int bodyY; //身体y坐标
    private int bodywidth; //身体宽度
    private int bodyheight; //身体高度
    private double angleInView; //人相对于机器人头部的角度
    private String quality; //检查质量参数
    private int age; //年龄（云端注册后会返回估算值）
    private String gender; //性别（按照国家规定，此项必须授权注册后才会返回结果）
    private int glasses; //是否戴眼睛
    private String remoteFaceId; //如果已注册，注册的人脸远端id
    private String faceRegisterTime; //注册时间
//更多Person信息，可参见SDK中com.ainirobot.coreservice.client.listener.Person类定义
获取检测到人脸的人员列表
//获取机器人视野内所有有人脸信息的人员列表
List<Person> personList = PersonApi.getInstance().getAllFaceList();
//获取机器人视野内1m范围内所有有人脸信息的人员列表
List personList = PersonApi.getInstance().getAllFaceList(1);
1.4.5 调用摄像头
拍照
val takePictureStatus = RobotApi.getInstance().takePicture(
    (System.currentTimeMillis() % 1000000).toInt(),
    "",
    object : CommandListener() {
        override fun onResult(result: Int, message: String?, extraData: String?) {
            KLog.d("takePicture result: $result, message: $message, extraData: $extraData", "ImageUtil")
        }
    }
)

1.4.6 其他
- 因为系统级API数量非常多，详情可以参考如下文档：RobotSDK完整文档
https://doc.orionstar.com/blog/knowledge-base/%e7%ae%80%e4%bb%8b/#undefined

1.5 获取机器人原始日志
1. 用此命令查看机器人上所有保存好的日志。
adb shell
cd /sdcard/logs/offlineLogs/821/
ls -l

2. 退出adb shell后，使用adb pull取出需要时间段的日志。
adb pull /sdcard/logs/offlineLogs/821/logcat.log-2020-05-22-11-00-07-062.tgz



---
附录2
1. Doc for cursor
如果你熟悉并使用类Cursor的AI编程工具，可以把该文档内容放入项目中辅助Cursor的理解。
ApiDoc For CursorAI

2. Agent SDK的介绍
如果你还不太了解AgentOS和AgentSDK的工作机制，可以了解一下以下内容
开启 AgentOS之门：AgentSDK 快速概览

