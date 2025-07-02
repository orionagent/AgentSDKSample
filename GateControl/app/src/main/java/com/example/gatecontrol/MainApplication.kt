package com.example.gatecontrol

import android.app.Application
import android.os.Bundle
import android.util.Log
import com.ainirobot.agent.AppAgent
import com.ainirobot.agent.action.Action
import com.example.gatecontrol.utils.Constants

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        object : AppAgent(this) {
            
            override fun onCreate() {
                Log.d(Constants.LOG_TAG, "AgentOS SDK 初始化")
                // 设定角色人设
                setPersona("你是一位专业的门禁管理助手，负责帮助用户进行门禁控制。")
                // 设定角色目标
                setObjective("通过自然的语音交互，帮助用户安全便捷地进行门禁管理，包括开门、验证授权码等操作。")
            }

            override fun onExecuteAction(
                action: Action,
                params: Bundle?
            ): Boolean {
                return false
            }
        }
    }
} 