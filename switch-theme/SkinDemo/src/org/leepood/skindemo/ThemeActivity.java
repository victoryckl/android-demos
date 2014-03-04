package org.leepood.skindemo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class ThemeActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener{
	private static final String TAG = ThemeActivity.class.getSimpleName();
	private SharedPreferences sp;
	private OnThemeChangedListener  listener;
	
	public interface OnThemeChangedListener {
		public void onChanged(String newThemePackageName);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp=this.getSharedPreferences("config",Context.MODE_WORLD_WRITEABLE);
		sp.registerOnSharedPreferenceChangeListener(this);
	}
	
	public void setOnThemeChangedListener(OnThemeChangedListener listener) {
		this.listener=listener;
	}
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		Log.i(TAG, "onSharedPreferenceChanged()");
		if(key.equals("themePackage")) {
			listener.onChanged(sp.getString("themePackage", ""));
		}
	}
}
