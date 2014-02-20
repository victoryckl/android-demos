package org.join.wfs;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.join.wfs.server.WebService;
import org.join.wfs.util.CopyUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class WFSActivity extends Activity implements OnCheckedChangeListener {

	private ToggleButton toggleBtn;
	private TextView urlText;

	private Intent intent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initViews();
		initFiles();

		intent = new Intent(this, WebService.class);
	}

	private void initViews() {
		toggleBtn = (ToggleButton) findViewById(R.id.toggleBtn);
		toggleBtn.setOnCheckedChangeListener(this);
		urlText = (TextView) findViewById(R.id.urlText);
	}

	private void initFiles() {
		new CopyUtil(this).assetsCopy();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			String ip = getLocalIpAddress();
			if (ip == null) {
				Toast.makeText(this, R.string.msg_net_off, Toast.LENGTH_SHORT)
						.show();
				urlText.setText("");
			} else {
				startService(intent);
				urlText.setText("http://" + ip + ":" + WebService.PORT + "/");
			}
		} else {
			stopService(intent);
			urlText.setText("");
		}
	}

	/** 获取当前IP地址 */
	private String getLocalIpAddress() {
		try {
			// 遍历网络接口
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				// 遍历IP地址
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					// 非回传地址时返回
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void onBackPressed() {
		if (intent != null) {
			stopService(intent);
		}
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}