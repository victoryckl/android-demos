package com.svo.platform.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.svo.platform.R;

public class LoadDialog extends Dialog {
	private TextView loadingText;
	private RotateImageView rotateImageView;

	public LoadDialog(Context paramContext) {
		super(paramContext);
	}

	public LoadDialog(Context paramContext, int paramInt) {
		super(paramContext, paramInt);
	}

	public void dismiss() {
		super.dismiss();
	}

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		View view = LayoutInflater.from(getContext()).inflate(
				R.layout.load_dialog, null);
		setContentView(view);
		Window window = getWindow();
		window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		this.rotateImageView = ((RotateImageView) view
				.findViewById(R.id.loading_progress));
		this.loadingText = ((TextView) findViewById(R.id.loading_text));
	}

	protected void onStop() {
		super.onStop();
	}

	public void show() {
		super.show();
		this.rotateImageView.rotate();
	}

	public void show(String paramString) {
		show();
		this.loadingText.setText(paramString + "");
	}
}