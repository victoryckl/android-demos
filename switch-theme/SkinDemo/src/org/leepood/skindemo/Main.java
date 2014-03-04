package org.leepood.skindemo;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends ThemeActivity {
	private static final String TAG = Main.class.getSimpleName();

	private ListView listview;
	private Context c;
	private Handler mHandler;
	private ProgressDialog pDialog;
	private SkinAdapter adapter;
	private SharedPreferences sp;

	static final int MESSAGE_SEARCHED_SKIN = 0;
	static final int MESSAGE_SEARCHING_SKIN = MESSAGE_SEARCHED_SKIN + 1;
	static final int MESSAGE_SEARCHED_SKIN_FOR_NONTHING = MESSAGE_SEARCHING_SKIN + 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		init();
		pDialog.show();
		new Thread(serachSkin).start();
	}

	private void init() {
		c = this;
		this.setOnThemeChangedListener(new OnThemeChangedListener() {
			public void onChanged(String newThemePackageName) {
				System.out.println("自动调用该方法啦");
				setStyle(newThemePackageName);
			}
		});

		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MESSAGE_SEARCHED_SKIN:
					ArrayList<PackageInfo> skins = (ArrayList<PackageInfo>) msg.obj;// 获取skins
					adapter = new SkinAdapter(c, skins);
					listview.setAdapter(adapter);
					Toast.makeText(c, "查找到已经安装的皮肤", 1).show();
					pDialog.dismiss();
					setStyle(sp.getString("themePackage", skins.get(0).packageName));
					break;
				case MESSAGE_SEARCHED_SKIN_FOR_NONTHING:
					Toast.makeText(c, "未查找到任何皮肤", 1).show();
					pDialog.dismiss();
				}
			}
		};
		
		sp = this.getSharedPreferences("config", Context.MODE_WORLD_WRITEABLE);

		listview = (ListView) findViewById(R.id.list);
		listview.setItemsCanFocus(false);
		listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		pDialog = new ProgressDialog(this);
		pDialog.setMessage("正在查找已经安装的皮肤");

		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, 
					View view, int position, long id) {
				SkinAdapter sadp = (SkinAdapter) listview.getAdapter();
				ArrayList<PackageInfo> datas = sadp.getDatas();
				String name = datas.get(position).packageName;
				sp.edit().putString("themePackage", name).commit();
			}
		});
	}

	private Runnable serachSkin = new Runnable() {
		public void run() {
			PackageManager manager = c.getPackageManager();
			List<PackageInfo> packages = manager
					.getInstalledPackages(PackageManager.PERMISSION_GRANTED);
			ArrayList<PackageInfo> skins = new ArrayList<PackageInfo>();
			for (PackageInfo info : packages) {
				if (info.packageName.startsWith("org.leepood.skin.")) {
					skins.add(info);
				}
			}
			if (skins.size() > 0) {
				Message msg = mHandler.obtainMessage();
				msg.obj = skins;
				msg.what = MESSAGE_SEARCHED_SKIN;
				mHandler.sendMessage(msg);
			} else {
				mHandler.sendEmptyMessage(MESSAGE_SEARCHED_SKIN_FOR_NONTHING);
			}
		}
	};

	private class SkinAdapter extends BaseAdapter {

		LayoutInflater mInflater;
		ArrayList<PackageInfo> datas;
		PackageManager manager;

		public SkinAdapter(Context c, ArrayList<PackageInfo> datas) {
			this.datas = datas;
			mInflater = LayoutInflater.from(c);
			manager = c.getPackageManager();
		}

		public int getCount() {
			return datas.size();
		}

		public Object getItem(int position) {
			return datas.get(position);
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.skin_item, null);
			}
			ImageView icon = (ImageView) convertView
					.findViewById(R.id.skin_icon);
			TextView skin_name = (TextView) convertView
					.findViewById(R.id.skin_name);
			PackageInfo info = datas.get(position);
			icon.setImageDrawable(info.applicationInfo.loadIcon(manager));
			skin_name.setText(info.applicationInfo.loadLabel(manager));
			return convertView;
		}
		
		public ArrayList<PackageInfo> getDatas() {
			return datas;
		}
	}
	
	private void setStyle(String newThemePackageName) {
		try {
			Context themeContext = Main.this.createPackageContext(
					newThemePackageName, CONTEXT_IGNORE_SECURITY);
			Resources res = themeContext.getResources();
			setControlsStyle(res);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void setControlsStyle(Resources res) {
		Log.i(TAG, "setControlsStyle()");
		listview.setBackgroundColor(res.getColor(R.color.ListView_bg));
	}
}
