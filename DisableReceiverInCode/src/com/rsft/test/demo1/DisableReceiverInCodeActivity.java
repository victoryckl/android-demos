package com.rsft.test.demo1;

import java.util.List;
import java.util.Map;

import com.cybertron.account.util.PackageUtils;
import com.rsft.test.demo1.R;

import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class DisableReceiverInCodeActivity extends Activity {
	private PackageManager pm;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        pm = getPackageManager();

        final CheckBox bootCheck = (CheckBox) findViewById(R.id.boot_complete_checkbox);
        final ComponentName cm = new ComponentName("com.rsft.test.demo1", "com.rsft.test.demo1.MyReceiver");
        int state = pm.getComponentEnabledSetting(cm);
        if (state != PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                && state != PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER) {
            bootCheck.setChecked(true);
        }
        bootCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                int newState = bootCheck.isChecked() ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                        : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
                pm.setComponentEnabledSetting(cm, newState, PackageManager.DONT_KILL_APP);
            }
        });
        
        listBootStartApp();
        
        initViews();
    }
    
	private void listBootStartApp() {
    	new Thread() {
    		@Override
    		public void run() {
    			List<ApplicationInfo> infos = PackageUtils.listApps(getPackageManager(), 0);
    			PackageUtils.printAppInfos(pm, infos);
    			PackageUtils.printBootStartReceivers(pm);
    			
    			List<ResolveInfo> comps = PackageUtils.listBootReceivers(pm);
    			Map<String, List<String>> map = PackageUtils.arrangeByPackage(comps);
    			PackageUtils.printMap(map);
    		}
    	}.start();
    }
	
	private CheckBox mCbApp, mCbComp;
	private EditText mEtAppPkg, mEtCompPkg, mEtCompCls;
	private void initViews() {
		mEtAppPkg = (EditText) findViewById(R.id.et_app_pacakge);
		mEtCompPkg = (EditText) findViewById(R.id.et_comp_package);
		mEtCompCls = (EditText) findViewById(R.id.et_comp_cls);
		
		mCbApp  = (CheckBox) findViewById(R.id.cp_enable_app);
		mCbComp = (CheckBox) findViewById(R.id.cp_enable_component);
		
		mCbApp.setOnCheckedChangeListener(mChangeListener);
		mCbComp.setOnCheckedChangeListener(mChangeListener);
		
		mCbApp.setChecked(isAppEnable());
		mCbComp.setChecked(isCompEnable());
	}
	
	private OnCheckedChangeListener mChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			switch (buttonView.getId()) {
			case R.id.cp_enable_app:
				setAppEnable(isChecked);
				break;
			case R.id.cp_enable_component:
				setCompEnable(isChecked);
				break;
			default:
				break;
			}
		}
	};
	
	private void setAppEnable(boolean isChecked) {
		String pkg = mEtAppPkg.getText().toString();
		PackageUtils.setAppEnable(pm, pkg, isChecked);
	}
	
	private void setCompEnable(boolean isChecked) {
		String pkg = mEtCompPkg.getText().toString();
		String cls = mEtCompCls.getText().toString();
		PackageUtils.setComponentEnable(pm, pkg, cls, isChecked);
	}
	
	private boolean isAppEnable() {
		String pkg = mEtAppPkg.getText().toString();
		return PackageUtils.isAppEnable(pm, pkg);
	}
	
	private boolean isCompEnable() {
		String pkg = mEtCompPkg.getText().toString();
		String cls = mEtCompCls.getText().toString();
		return PackageUtils.isComponentEnable(pm, pkg, cls);
	}
}
