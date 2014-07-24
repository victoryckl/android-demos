package wjq.WidgetDemo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.MultiAutoCompleteTextView;

public class AutoCompleteDemo extends Activity {
	private AutoCompleteTextView edit;
	private MultiAutoCompleteTextView edit1;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.autocompletepage);
		edit = (AutoCompleteTextView) findViewById(R.id.edit);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, strs);
		edit.setAdapter(adapter);

		edit1 = (MultiAutoCompleteTextView) findViewById(R.id.edit1);

		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, strs);
		edit1.setAdapter(adapter1);
		edit1.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
		
	}

	private String[] strs = new String[] { "wujianqiang", "zhangmeijing", "lixiuting",
			"mazhaotong", "wujianxin", "wujianzhen",
			"machao"
			 };

}
