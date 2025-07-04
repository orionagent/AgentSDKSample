package com.example.agentrolejava;

import android.app.Application;
import android.os.Bundle;
import com.ainirobot.agent.AppAgent;
import com.ainirobot.agent.action.Action;
import com.ainirobot.agent.action.Actions;

public class MainApplication extends Application {
    private AppAgent appAgent;

    public AppAgent getAppAgent() {
        return appAgent;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        appAgent = new AppAgent(this) {
            @Override
            public void onCreate() {
                // 设定基础人设 - 为角色扮演做准备
                setPersona("你是一个专业的角色扮演助手，能够完全沉浸在不同角色中，展现各种人物的独特性格和特色。");
                // 设定角色目标
                // setObjective("通过自然的对话和合适的情绪表达，让用户感受到理解、陪伴与情感共鸣，从而提升交流的舒适感和信任感。");

                registerAction(Actions.SAY);
            }

            @Override
            public boolean onExecuteAction(Action action, Bundle params) {
                // 在此处处理静态注册的action，如果你不需要处理，请返回false，如果要自行处理且不需要后续处理，则返回true
                // 默认返回false
                return false;
            }
        };
    }
} 