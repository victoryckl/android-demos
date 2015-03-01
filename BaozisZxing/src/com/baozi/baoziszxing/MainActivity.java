package com.baozi.baoziszxing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baozi.Zxing.CaptureActivity;
import com.example.baoziszxing.R;

/**
 * 
 * @author Baozi
 * @联系方式: 2221673069@qq.com
 * 
 * @描述: 1 project能扫描二维码和普通一维码 ;
 * 
 *      2 能从相册拿到二维码照片然后进行解析 --->注意 照片中的二维码 在拍摄的时候需要正对齐 否则会解析不出
 * 
 *      3 针对大部分中文解决乱码问题 , 但依旧会有部分编码格式会出现中文乱码 如解决请联系我 QQ:2221673069
 * 
 *      4 Zxing在使用过程中发现了新问题 :如果扫描的时候手机与二维码的角度超过30度左右的时候就解析不了 如解决请联系我
 *      QQ:2221673069
 * 
 *      谢谢大家 希望对大家有用
 */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.button1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,
						CaptureActivity.class);

				startActivityForResult(intent, 100);

			}
		});
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent data) {
		super.onActivityResult(arg0, arg1, data);

		/**
		 * 拿到解析完成的字符串
		 */
		if (data != null) {
			TextView text = (TextView) findViewById(R.id.textView1);
			text.setText(data.getStringExtra("result"));
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
