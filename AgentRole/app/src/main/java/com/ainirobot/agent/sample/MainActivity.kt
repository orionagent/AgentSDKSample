package com.ainirobot.agent.sample

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.ainirobot.agent.sample.ui.components.*
import com.ainirobot.agent.sample.ui.components.ChatManager
import com.ainirobot.agent.sample.ui.components.roles
import androidx.compose.ui.platform.LocalContext
import com.ainirobot.agent.AgentCore
import com.ainirobot.agent.sample.ui.theme.AgentDemoTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AgentDemoTheme {
                val context = LocalContext.current
                RoleSelectScreen(
                    roles = roles,
                    onRoleSelected = { role ->
                        val appAgent = (context.applicationContext as MainApplication).appAgent
                        appAgent.setPersona(role.persona)
                        appAgent.setObjective(role.objective)
                        ChatManager.clearMessages()
                        
                        // 启动ChatActivity
                        val intent = Intent(context, ChatActivity::class.java)
                        intent.putExtra("role", role)
                        context.startActivity(intent)
                    }
                )
            }
        }
        AgentCore.isEnableVoiceBar = false
    }
}