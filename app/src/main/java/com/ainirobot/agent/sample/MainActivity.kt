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
    }

    private fun showFaceImage(resId: Int) {
        runOnUiThread {
            findViewById<ImageView>(R.id.face_view).setImageResource(resId)
        }
    }

    private fun handleAction(action: Action, params: Bundle?) {
        AOCoroutineScope.launch {
            // 播放给用户说的话
            params?.getString("sentence")?.let { AgentCore.ttsSync(it) }
            // 播放完成后，及时上报Action的执行状态
            action.notify()
        }
    }
}