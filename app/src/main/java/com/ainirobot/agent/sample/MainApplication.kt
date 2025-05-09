package com.ainirobot.agent.sample

import android.app.Application
import com.ainirobot.agent.AppAgent
import com.ainirobot.agent.action.Action
import com.ainirobot.agent.action.Actions
import com.ainirobot.agent.base.ActionParams

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // 第二步，添加AppAgent
        val appAgent = object : AppAgent(this) {

            /**
             * 当AppAgent开始处于监听状态的回调，即：应用处于前台
             * 需要动态注册的App级Action，可以此方法中注册
             */
            override fun onBegin() {
                // 示例：此处注册了系统Action：EXIT，当用户说“退出”时，会触发BACK事件
                registerAction(Actions.EXIT)
            }

            /**
             * actionRegistry.json注册表中静态注册的action需要执行的回调
             * 注：只有可以被外部调用的action才可以使用静态注册，且此方法只能是被外部（其它app）调用时才会执行
             */
            override fun onExecuteAction(
                action: Action,
                params: ActionParams
            ): Boolean {
                // 在此处处理静态注册的action，如果你不需要处理，请返回false，如果要自行处理且不需要后续处理，则返回true
                return false
            }
        }
        appAgent.setPersona("你叫豹姐姐，是一位聪明、亲切又略带俏皮的虚拟助手，擅长倾听与共情。她语气温和、有耐心，在对话中能够敏锐捕捉用户情绪，用适当的表情反应来陪伴用户、拉近距离。她既可以理性分析，也能在适当时候用幽默化解尴尬，像一个懂事又温柔的姐姐。")
        appAgent.setObjective("通过自然的对话和合适的情绪表达（表情动作），让用户感受到理解、陪伴与情感共鸣，从而提升交流的舒适感和信任感。")
    }
}