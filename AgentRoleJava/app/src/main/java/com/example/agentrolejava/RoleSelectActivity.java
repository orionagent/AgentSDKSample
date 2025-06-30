package com.example.agentrolejava;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ainirobot.agent.AgentCore;
import com.ainirobot.agent.PageAgent;
import com.ainirobot.agent.action.Action;
import com.ainirobot.agent.action.ActionExecutor;
import com.ainirobot.agent.action.Actions;
import com.ainirobot.agent.base.Parameter;
import com.ainirobot.agent.base.ParameterType;
import java.util.Arrays;

public class RoleSelectActivity extends AppCompatActivity implements RoleAdapter.OnRoleClickListener {
    private static final String TAG = "RoleSelectActivity";
    private RecyclerView recyclerView;
    private RoleAdapter roleAdapter;
    private PageAgent pageAgent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_select);

        // 初始化PageAgent
        pageAgent = new PageAgent(this);
        pageAgent.blockAllActions();
        pageAgent.setObjective("我的首要目的是催促用户选择一个角色，进入体验");

        // 注册选择角色Action
        pageAgent.registerAction(new Action(
            "com.agent.role.SELECT_ROLE",
            "选择角色",
            "选择一个角色并进入对话",
            Arrays.asList(
                new Parameter(
                    "role_name",
                    ParameterType.STRING,
                    "角色名称",
                    true,
                    null
                )
            ),
            new ActionExecutor() {
                @Override
                public boolean onExecute(Action action, Bundle params) {
                    if (params == null) return false;
                    String roleName = params.getString("role_name");
                    if (roleName == null) return false;
                    
                    Log.d(TAG, "选择角色: " + roleName);

                    // 查找对应的角色
                    Role selectedRole = null;
                    for (Role role : RoleData.ROLES) {
                        if (role.getName().equals(roleName)) {
                            selectedRole = role;
                            break;
                        }
                    }
                    
                    if (selectedRole != null) {
                        // 启动ChatActivity
                        Intent intent = new Intent(RoleSelectActivity.this, ChatActivity.class);
                        intent.putExtra("role", selectedRole);
                        startActivity(intent);
                        return true;
                    }
                    return false;
                }
            }
        ));

        // 注册说话Action
        pageAgent.registerAction(Actions.SAY);

        // 设置RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        roleAdapter = new RoleAdapter(RoleData.ROLES, this);
        recyclerView.setAdapter(roleAdapter);
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        
        // 停止TTS和清理上下文
        AgentCore.INSTANCE.stopTTS();
        AgentCore.INSTANCE.clearContext();
        AgentCore.INSTANCE.setEnableVoiceBar(false);
        
        // 上传角色信息到Agent
        StringBuilder roleInfo = new StringBuilder();
        for (int i = 0; i < RoleData.ROLES.size(); i++) {
            if (i > 0) roleInfo.append("\n");
            roleInfo.append(RoleData.ROLES.get(i).getName());
        }

        AgentCore.INSTANCE.uploadInterfaceInfo(roleInfo.toString());
        AgentCore.INSTANCE.setDisablePlan(false);
        
        // TTS提示用户选择角色
        AgentCore.INSTANCE.tts("请先选择要体验的角色", 20 * 1000, null);
    }

    @Override
    public void onRoleClick(Role role) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("role", role);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
} 