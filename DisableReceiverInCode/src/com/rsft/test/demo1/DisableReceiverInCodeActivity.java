package com.rsft.test.demo1;

import com.rsft.test.demo1.R;

import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class DisableReceiverInCodeActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final CheckBox bootCheck = (CheckBox) findViewById(R.id.boot_complete_checkbox);
        final ComponentName cm = new ComponentName("com.rsft.test.demo1", "com.rsft.test.demo1.MyReceiver");
        final PackageManager pm = getPackageManager();
        int state = pm.getComponentEnabledSetting(cm);
        if (state != PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                && state != PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER) {
            bootCheck.setChecked(true);
        }
        bootCheck
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                            boolean isChecked) {
                        int newState = bootCheck.isChecked() ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                                : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
                        pm.setComponentEnabledSetting(cm, newState,
                                PackageManager.DONT_KILL_APP);
                    }
                });
    }
}