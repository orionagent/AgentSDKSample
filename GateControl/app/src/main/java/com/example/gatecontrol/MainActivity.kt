package com.example.gatecontrol

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import android.content.res.Configuration
import com.ainirobot.agent.AgentCore
import com.example.gatecontrol.ui.theme.GateControlTheme
import com.ainirobot.agent.PageAgent
import com.ainirobot.agent.action.Action
import com.ainirobot.agent.action.ActionExecutor
import com.ainirobot.agent.base.Parameter
import com.ainirobot.agent.base.ParameterType
import com.ainirobot.agent.coroutine.AOCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import com.example.gatecontrol.utils.Constants

class MainActivity : ComponentActivity() {
    companion object {
        // 用于语音交互时控制UI状态的全局变量
        var voiceAuthCode: String? = null
        var showVoiceAuthDialog: Boolean = false
        var onVoiceAuthCallback: ((Boolean) -> Unit)? = null
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // 初始化PageAgent，添加语音指令处理能力
        initPageAgent()
        
        setContent {
            GateControlTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GateControlScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
    
    private fun initPageAgent() {
        PageAgent(this)
            .setPersona(Constants.AGENT_PERSONA)
            .setObjective(Constants.AGENT_OBJECTIVE)
            .registerAction(
                Action(
                    name = Constants.ACTION_NAME,
                    displayName = Constants.ACTION_DISPLAY_NAME,
                    desc = Constants.ACTION_DESCRIPTION,
                    parameters = listOf(
                        Parameter(
                            Constants.PARAMETER_AUTH_CODE,
                            ParameterType.NUMBER_ARRAY,
                            Constants.PARAMETER_DESCRIPTION,
                            true
                        )
                    ),
                    executor = object : ActionExecutor {
                        override fun onExecute(action: Action, params: Bundle?): Boolean {
                            handleVoiceDoorControl(action, params)  // 可以沿用手机应用逻辑，执行授权码校验并开门操作
                            return true
                        }
                    }
                )
            )
    }
    
    private fun handleVoiceDoorControl(action: Action, params: Bundle?) {
        Log.d(Constants.LOG_TAG, "处理语音开门控制, params: $params")

        // 尝试获取NUMBER_ARRAY类型的授权码，SDK返回的是ArrayList
        val authCodeList = params?.get(Constants.PARAMETER_AUTH_CODE) as? ArrayList<*>
        
        if (authCodeList != null && authCodeList.size == Constants.AUTH_CODE_LENGTH) {
            try {
                // 将ArrayList转换为数字字符串
                val authCode = authCodeList.joinToString("") { it.toString() }
                Log.d(Constants.LOG_TAG, "收到授权码列表: $authCodeList, 转换为: $authCode")
                
                // 设置语音授权码并显示弹窗
                voiceAuthCode = authCode
                showVoiceAuthDialog = true
                onVoiceAuthCallback = { success ->
                    // 清理状态
                    voiceAuthCode = null
                    showVoiceAuthDialog = false
                    onVoiceAuthCallback = null
                    
                    if (success) {
                        // 验证成功，语音反馈
                        AOCoroutineScope.launch {
                            AgentCore.tts(Constants.TTS_AUTH_SUCCESS_OPENING)
                            action.notify(isTriggerFollowUp = false)
                        }
                    } else {
                        // 验证失败，语音反馈
                        AOCoroutineScope.launch {
                            AgentCore.tts(Constants.TTS_AUTH_FAILED_RETRY)
                            action.notify()
                        }
                    }
                }
                
                AOCoroutineScope.launch {
                    AgentCore.tts(Constants.TTS_VERIFYING_AUTH_CODE)
                }
                
            } catch (e: Exception) {
                Log.e(Constants.LOG_TAG, "解析授权码失败", e)
                AOCoroutineScope.launch {
                    AgentCore.tts(Constants.TTS_INVALID_FORMAT)
                    action.notify(isTriggerFollowUp = false)
                }
            }
        } else {
            // 需要用户提供授权码
            Log.d(Constants.LOG_TAG, "未收到有效的${Constants.AUTH_CODE_LENGTH}位授权码，收到: $authCodeList")
            AOCoroutineScope.launch {
                AgentCore.tts(Constants.TTS_PROVIDE_AUTH_CODE)
                action.notify(isTriggerFollowUp = false)
            }
        }
    }
    
    private fun verifyAuthCodeAndOpenDoor(code: String, action: Action) {
        Log.d(Constants.LOG_TAG, "验证授权码: $code")
        
        if (code == Constants.VALID_AUTH_CODE) {
            Log.d(Constants.LOG_TAG, "授权码验证成功，执行开门操作")
            // 这里可以调用原有的开门逻辑
            AOCoroutineScope.launch {
                try {
                    AgentCore.tts(Constants.TTS_AUTH_SUCCESS_OPENING)
                    
                    // 模拟开门操作
                    withContext(Dispatchers.IO) {
                        kotlinx.coroutines.delay(1500) // 模拟网络延迟
                    }
                    
                    Log.d(Constants.LOG_TAG, "模拟开门命令执行成功")
                    AgentCore.tts(Constants.TTS_OPEN_SUCCESS)
                    action.notify(isTriggerFollowUp = false)
                } catch (e: Exception) {
                    Log.e(Constants.LOG_TAG, "模拟开门操作失败", e)
                    AgentCore.tts(Constants.TTS_OPEN_FAILED)
                    action.notify()
                }
            }
        } else {
            Log.d(Constants.LOG_TAG, "授权码验证失败: $code")
            AOCoroutineScope.launch {
                AgentCore.tts(Constants.TTS_AUTH_FAILED_RETRY)
                action.notify()
            }
        }
    }
}

@Composable
fun GateControlScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    var isLoading by remember { mutableStateOf(false) }
    var showAuthDialog by remember { mutableStateOf(false) }
    var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    var statusMessage by remember { mutableStateOf("") }
    var showStatus by remember { mutableStateOf(false) }
    var prefilledAuthCode by remember { mutableStateOf("") }
    
    // 监听语音交互状态变化
    LaunchedEffect(Unit) {
        while (true) {
            if (MainActivity.showVoiceAuthDialog) {
                prefilledAuthCode = MainActivity.voiceAuthCode ?: ""
                showAuthDialog = true
                MainActivity.showVoiceAuthDialog = false
            }
            kotlinx.coroutines.delay(100) // 每100ms检查一次
        }
    }

    // 验证授权码的函数
    fun verifyCode(code: String): Boolean {
        // 这里可以替换为实际的验证逻辑
        // 目前假设正确的授权码是 "2025"
        return code == Constants.VALID_AUTH_CODE
    }

    fun sendUdpCommand(hexData: String, ipAddress: String) {
        scope.launch {
            isLoading = true
            Log.d(Constants.LOG_TAG, "模拟发送UDP命令: $hexData 到 $ipAddress")
            
            try {
                // 显示开门中的Toast
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context, 
                        Constants.TOAST_DOOR_OPENING, 
                        Toast.LENGTH_SHORT
                    ).show()
                }
                
                // 模拟网络延迟
                withContext(Dispatchers.IO) {
                    kotlinx.coroutines.delay(1500) // 1.5秒延迟模拟网络操作
                }
                
                Log.d(Constants.LOG_TAG, "模拟UDP命令执行完成")
                
                // 显示成功的Toast
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context, 
                        Constants.TOAST_DOOR_OPENED, 
                        Toast.LENGTH_LONG
                    ).show()
                }
                
            } catch (e: Exception) {
                Log.e(Constants.LOG_TAG, "模拟操作异常: ${e.javaClass.simpleName}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context, 
                        "操作失败: ${e.message}", 
                        Toast.LENGTH_LONG
                    ).show()
                }
            } finally {
                isLoading = false
            }
        }
    }

    // 处理开门操作，需要先验证授权码
    fun handleDoorAction(action: () -> Unit) {
        pendingAction = action
        showAuthDialog = true
    }

    // 根据屏幕方向选择不同的布局
    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            LandscapeLayout(
                modifier = modifier,
                isLoading = isLoading,
                showStatus = showStatus,
                statusMessage = statusMessage,
                onDoorAction = { handleDoorAction { sendUdpCommand(Constants.UDP_COMMAND_OPEN_DOOR, Constants.SERVER_IP) } }
            )
        }
        else -> {
            PortraitLayout(
                modifier = modifier,
                isLoading = isLoading,
                showStatus = showStatus,
                statusMessage = statusMessage,
                onDoorAction = { handleDoorAction { sendUdpCommand(Constants.UDP_COMMAND_OPEN_DOOR, Constants.SERVER_IP) } }
            )
        }
    }

    // 授权码验证对话框
    if (showAuthDialog) {
        AuthCodeDialog(
            prefilledCode = prefilledAuthCode,
            isVoiceTriggered = MainActivity.onVoiceAuthCallback != null,
            onVerify = { code ->
                val isValid = verifyCode(code)
                
                // 如果是语音交互触发的对话框，调用语音回调
                if (MainActivity.onVoiceAuthCallback != null) {
                    MainActivity.onVoiceAuthCallback?.invoke(isValid)
                    showAuthDialog = false
                    prefilledAuthCode = ""
                    
                    // 语音交互也需要处理验证结果的UI状态
                    if (isValid) {
                        statusMessage = context.getString(com.example.gatecontrol.R.string.verification_success)
                        showStatus = true
                        // 语音交互也执行开门操作
                        sendUdpCommand(Constants.UDP_COMMAND_OPEN_DOOR, Constants.SERVER_IP)
                        // 3秒后隐藏状态消息
                        scope.launch {
                            kotlinx.coroutines.delay(3000)
                            showStatus = false
                        }
                    } else {
                        statusMessage = context.getString(com.example.gatecontrol.R.string.verification_failed)
                        showStatus = true
                        // 5秒后隐藏状态消息
                        scope.launch {
                            kotlinx.coroutines.delay(5000)
                            showStatus = false
                        }
                    }
                } else {
                    // 普通触摸交互
                    if (isValid) {
                        showAuthDialog = false
                        statusMessage = context.getString(com.example.gatecontrol.R.string.verification_success)
                        showStatus = true
                        // 执行待处理的操作
                        pendingAction?.invoke()
                        pendingAction = null
                        // 3秒后隐藏状态消息
                        scope.launch {
                            kotlinx.coroutines.delay(3000)
                            showStatus = false
                        }
                    } else {
                        statusMessage = context.getString(com.example.gatecontrol.R.string.verification_failed)
                        showStatus = true
                        showAuthDialog = false
                        // 5秒后隐藏状态消息
                        scope.launch {
                            kotlinx.coroutines.delay(5000)
                            showStatus = false
                        }
                    }
                    pendingAction = null
                }
            },
            onCancel = {
                showAuthDialog = false
                prefilledAuthCode = ""
                pendingAction = null
                // 如果是语音交互，也要清理回调
                if (MainActivity.onVoiceAuthCallback != null) {
                    MainActivity.onVoiceAuthCallback?.invoke(false)
                    MainActivity.onVoiceAuthCallback = null
                    MainActivity.voiceAuthCode = null
                }
            }
        )
    }
}

@Composable
fun PortraitLayout(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    showStatus: Boolean,
    statusMessage: String,
    onDoorAction: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.mipmap.orionstar),
            contentDescription = "OrionStar Logo",
            modifier = Modifier
                .size(300.dp)
                .padding(bottom = 24.dp)
        )
        
        Text(
            text = stringResource(id = com.example.gatecontrol.R.string.app_name),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        // 状态显示区域
        if (showStatus) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (statusMessage.contains("失败")) MaterialTheme.colorScheme.errorContainer
                    else MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = statusMessage,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (statusMessage.contains("失败")) MaterialTheme.colorScheme.onErrorContainer
                    else MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Button(
            onClick = onDoorAction,
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp).padding(horizontal = 32.dp),
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(id = com.example.gatecontrol.R.string.sending_command))
            } else {
                Text(
                    text = stringResource(id = com.example.gatecontrol.R.string.open_door),
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun LandscapeLayout(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    showStatus: Boolean,
    statusMessage: String,
    onDoorAction: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧：Logo
        Image(
            painter = painterResource(id = R.mipmap.orionstar),
            contentDescription = "OrionStar Logo",
            modifier = Modifier
                .size(300.dp)
                .weight(1f)
        )
        
        // 右侧：控制区域
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = com.example.gatecontrol.R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // 状态显示区域
            if (showStatus) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (statusMessage.contains("失败")) MaterialTheme.colorScheme.errorContainer
                        else MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = statusMessage,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (statusMessage.contains("失败")) MaterialTheme.colorScheme.onErrorContainer
                        else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Button(
                onClick = onDoorAction,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(id = com.example.gatecontrol.R.string.sending_command))
                } else {
                    Text(
                        text = stringResource(id = com.example.gatecontrol.R.string.open_door),
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AuthCodeDialog(
    prefilledCode: String = "",
    isVoiceTriggered: Boolean = false,
    onVerify: (String) -> Unit,
    onCancel: () -> Unit
) {
    var authCode by remember(prefilledCode) { mutableStateOf(prefilledCode) }
    var isVerifying by remember { mutableStateOf(false) }
    var countdown by remember { mutableStateOf(0) }
    
    // 如果是语音触发且有预填充授权码，延迟1秒自动验证
    LaunchedEffect(isVoiceTriggered, prefilledCode) {
        if (isVoiceTriggered && prefilledCode.isNotEmpty() && prefilledCode.length == 4) {
            // 倒计时显示
            for (i in 1 downTo 1) {
                countdown = i
                kotlinx.coroutines.delay(1000)
            }
            countdown = 0
            if (!isVerifying) {
                isVerifying = true
                onVerify(authCode)
            }
        }
    }

    Dialog(onDismissRequest = onCancel) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isVoiceTriggered && countdown > 0) {
                        "语音识别授权码，即将自动验证"
                    } else {
                        stringResource(id = com.example.gatecontrol.R.string.auth_code_required)
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = authCode,
                    onValueChange = { newValue ->
                        // 限制只能输入数字，最多4位
                        if (newValue.all { it.isDigit() } && newValue.length <= 4) {
                            authCode = newValue
                        }
                    },
                    label = { Text(stringResource(id = com.example.gatecontrol.R.string.enter_auth_code)) },
                    placeholder = { Text(stringResource(id = com.example.gatecontrol.R.string.auth_code_hint)) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(id = com.example.gatecontrol.R.string.cancel))
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = {
                            if (authCode.length == 4) {
                                isVerifying = true
                                onVerify(authCode)
                            }
                        },
                        enabled = authCode.length == 4 && !isVerifying,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (isVerifying) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(stringResource(id = com.example.gatecontrol.R.string.verify))
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GateControlScreenPreview() {
    GateControlTheme {
        GateControlScreen()
    }
}