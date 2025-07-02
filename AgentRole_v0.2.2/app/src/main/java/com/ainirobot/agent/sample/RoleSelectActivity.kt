package com.ainirobot.agent.sample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ainirobot.agent.AgentCore
import com.ainirobot.agent.PageAgent
import com.ainirobot.agent.action.Action
import com.ainirobot.agent.action.ActionExecutor
import com.ainirobot.agent.action.Actions
import com.ainirobot.agent.base.Parameter
import com.ainirobot.agent.base.ParameterType
import com.ainirobot.agent.sample.ui.components.RoleSelectScreen
import com.ainirobot.agent.sample.ui.components.roles
import com.ainirobot.agent.sample.ui.theme.AgentDemoTheme

class RoleSelectActivity : ComponentActivity() {
    private lateinit var pageAgent: PageAgent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化PageAgent
        pageAgent = PageAgent(this)
        pageAgent.blockAllActions()
        pageAgent.setObjective("我的首要目的是催促用户选择一个角色，进入体验")

        // 注册选择角色Action
        pageAgent.registerAction(
            Action(
                "com.agent.role.SELECT_ROLE",
                "选择角色",
                "选择一个角色并进入对话",
                parameters = listOf(
                    Parameter(
                        "role_name",
                        ParameterType.STRING,
                        "角色名称",
                        true
                    )
                ),
                executor = object : ActionExecutor {
                    override fun onExecute(action: Action, params: Bundle?): Boolean {
                        val roleName = params?.getString("role_name") ?: return false
                        Log.d("RoleSelectActivity", "选择角色: $roleName")

                        // 查找对应的角色
                        val selectedRole = roles.find { it.name == roleName }
                        if (selectedRole != null) {
                            // 启动ChatActivity
                            val intent = Intent(this@RoleSelectActivity, ChatActivity::class.java)
                            intent.putExtra("role", selectedRole)
                            startActivity(intent)
                            return true
                        }
                        return false
                    }
                }
            )
        )

        // 注册说话Action
        pageAgent.registerAction(Actions.SAY)

        // 设置UI
        setContent {
            AgentDemoTheme {
                RoleSelectScreen(
                    roles = roles,
                    onRoleSelected = { role ->
                        val intent = Intent(this@RoleSelectActivity, ChatActivity::class.java)
                        intent.putExtra("role", role)
                        startActivity(intent)
                    }
                )
            }
        }
    }
    
    override fun onStart() {
        super.onStart()
        AgentCore.stopTTS()
        AgentCore.clearContext()
        // 上传角色信息到Agent
        val roleInfo = roles.joinToString("\n") {
            "${it.name}"
        }

        AgentCore.uploadInterfaceInfo(roleInfo)
        AgentCore.tts("请先选择要体验的角色", timeoutMillis = 20 * 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
