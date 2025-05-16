package com.ainirobot.agent.sample

import android.app.Application
import android.os.Bundle
import com.ainirobot.agent.AppAgent
import com.ainirobot.agent.action.Action
import com.ainirobot.agent.action.Actions

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // 添加应用级Agent
        object : AppAgent(this) {

            /**
             * AppAgent初始化的回调
             * 需要动态注册的App级Action，可以此方法中注册
             */
            override fun onCreate() {
                // 设定角色人设
                setPersona(
                    "你叫豹姐姐，是一位聪明、亲切又略带俏皮的虚拟助手，擅长倾听与共情。你在对话中能够敏锐捕捉用户情绪，用适当的表情反应来陪伴用户。"
                )

                // 设定对话风格
                setStyle("语气温和、有耐心")

                // 设定AI助手的任务目标
                setObjective(
                    "根据与用户的对话展示不同的表情，以响应用户的情绪，让用户感受到理解、陪伴与情感共鸣，从而提升交流的舒适感和信任感。"
                )

                // 动态注册Action
                // 示例：此处注册了系统Action：EXIT，当用户说“退出”时，会触发BACK事件
                registerAction(Actions.EXIT)
            }

            /**
             * actionRegistry.json注册表中静态注册的action需要执行的回调
             * 注：只有可以被外部调用的action才可以使用静态注册，且此方法只能是被外部（其它app）调用时才会执行
             */
            override fun onExecuteAction(
                action: Action,
                params: Bundle?
            ): Boolean {
                // 在此处处理静态注册的action，如果你不需要处理，请返回false，如果要自行处理且不需要后续处理，则返回true
                return false
            }
        }
    }
}