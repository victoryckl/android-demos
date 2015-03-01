package com.example.widgets;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * ¹Ì¶¨Ç°×ºµÄ±à¼­Æ÷
 */
public class PrefixEditText extends EditText {
//	private static final String TAG = PrefixEditText.class.getSimpleName();
	
	public PrefixEditText(Context context) {
		super(context);
		init();
	}

	public PrefixEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PrefixEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
		setFilters(new InputFilter[]{filter});
	}
	
	private String prefix = "X-Man:";
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	@Override
	public Editable getText() {
		Editable et = super.getText();
		if (prefix == null) {
			return et;
		}
		if (et != null && et.length() > 0) {
			if (!et.toString().startsWith(prefix)) {
				et.insert(0, prefix+"\n");
			}
		}
		return et;
	}
	
	private InputFilter filter = new InputFilter() {
		@Override
		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {
//			Log.i(TAG, "filter: " + source + ", "+start+", "+end+", "+dest+", "+dstart+", "+dend);
			if (prefix == null) {
				return source;
			}
	
			if (dest != null && 
				dest.toString().startsWith(prefix)) {
				if (dend-dstart == dest.length()) {//clear
					return source;
				}
				if (dstart <= prefix.length()) {
					return dest.subSequence(dstart, dend);
				}
			}
			return source;
		}
	};
}
