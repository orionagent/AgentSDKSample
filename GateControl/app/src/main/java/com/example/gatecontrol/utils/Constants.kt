package com.example.gatecontrol.utils

/**
 * 门禁控制应用常量定义
 */
object Constants {
    
    // 日志标签
    const val LOG_TAG = "GateControl"
    
    // 网络配置
    const val SERVER_IP = "10.60.232.139"
    const val SERVER_PORT = 9999
    const val UDP_COMMAND_OPEN_DOOR = "0109"
    
    // 授权码配置
    const val VALID_AUTH_CODE = "2025"
    const val AUTH_CODE_LENGTH = 4
    
    // Agent配置
    const val ACTION_NAME = "action.orionstar.OPEN_DOOR_WITH_AUTH_CODE"
    const val ACTION_DISPLAY_NAME = "语音控制开门"
    const val ACTION_DESCRIPTION = "当用户说开门、开闸、打开门禁等意图时触发,并且要求用户提供授权码"
    const val PARAMETER_AUTH_CODE = "auth_code"
    const val PARAMETER_DESCRIPTION = "4位数字授权码"
    
    // Agent人设配置
    const val AGENT_PERSONA = "你是猎户星空的门卫安保人员，你负责对进出人员进行身份查验、帮助开门"
    const val AGENT_OBJECTIVE = "你需要识别到访者的意图，如需开门进入的人员，要求提供4位的授权码用于身份校验，只有授权码正确才放行。"
    
    // TTS语音反馈文本
    const val TTS_VERIFYING_AUTH_CODE = "正在验证授权码。"
    const val TTS_AUTH_SUCCESS_OPENING = "授权码验证成功，正在开门"
    const val TTS_AUTH_FAILED_RETRY = "授权码错误，请重新说出正确的4位授权码"
    const val TTS_INVALID_FORMAT = "授权码格式错误，请重新说出4位数字"
    const val TTS_PROVIDE_AUTH_CODE = "请提供4位有效的授权码"
    const val TTS_OPEN_SUCCESS = "开门成功"
    const val TTS_OPEN_FAILED = "开门失败，请重试"
    
    // Toast消息文本
    const val TOAST_DOOR_OPENING = "正在开门..."
    const val TOAST_DOOR_OPENED = "闸机已打开！"
    const val TOAST_SIMULATION_MODE = "模拟模式：闸机控制指令已发送"
} 