package com.example.aservice;

import androidx.appcompat.app.AppCompatActivity;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AccessibilityManager mAccessibilityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAccessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);

        new Handler().postDelayed(() -> {
            boolean enabled = checkAccessibilityEnabled();
            if (enabled) {
                Toast.makeText(this, "服务已经启动", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "请启动服务...", Toast.LENGTH_LONG).show();
                goAccess();
            }
        }, 2000);
    }
    
    private boolean checkAccessibilityEnabled() {
        List<AccessibilityServiceInfo> accessibilityServices =
                mAccessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            if (info.getId().equals(AccessibilitySampleService.class.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 前往开启辅助服务界面
     */
    public void goAccess() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}