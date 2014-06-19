package com.svo.platform.picbrowser;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.svo.photoview.PhotoView;
import com.svo.photoview.PhotoViewAttacher.OnPhotoTapListener;
import com.svo.platform.R;
import com.svo.platform.gif.GifActivity;
import com.svo.platform.utils.PicUtil;
import com.svo.platform.utils.SvoToast;
import com.tencent.mm.sdk.platformtools.NetStatusUtil;

public class PicFragment extends Fragment implements OnPhotoTapListener,OnClickListener{
	public static PicFragment newInstance(String picPath) {
		PicFragment f = new PicFragment();
        Bundle args = new Bundle();
        args.putString("picPath", picPath);
        f.setArguments(args);
        return f;
    }
	private PhotoView imgView;
	private String picPath;
	private ProgressBar progressBar;
	private View view;
	private ImageView gifPlay;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		picPath = getArguments() != null ? getArguments().getString("picPath") : "";
		view = inflater.inflate(R.layout.pic_item, null);
		imgView = (PhotoView)(view.findViewById(R.id.imag));// 获取控件
		imgView.setOnPhotoTapListener(this);
		progressBar = (ProgressBar) view.findViewById(R.id.pic_progressbarloading);
		gifPlay = (ImageView) view.findViewById(R.id.gif_play);
		gifPlay.setOnClickListener(this);
		
		if (!NetStatusUtil.isConnected(getActivity()) && picPath.startsWith("http") && (PicUtil.getPicFile(picPath) == null || !PicUtil.getPicFile(picPath).exists())) {
			SvoToast.showHint(getActivity(), "网络不可用", Toast.LENGTH_SHORT);
		}else {
			progressBar.setVisibility(View.VISIBLE);
			PicUtil.displayImage(getActivity(), picPath, imgView,new SimpleImageLoadingListener(){
				@Override
				public void onLoadingComplete(String imageUri, View view,
						Bitmap loadedImage) {
					super.onLoadingComplete(imageUri, view, loadedImage);
					progressBar.setVisibility(View.GONE);
					if (imageUri.endsWith("gif") || imageUri.endsWith("GIF") ) {
						gifPlay.setVisibility(View.VISIBLE);
					}else {
						gifPlay.setVisibility(View.INVISIBLE);
					}
				}
				@Override
				public void onLoadingFailed(String imageUri, View view,
						FailReason failReason) {
					super.onLoadingFailed(imageUri, view, failReason);
					progressBar.setVisibility(View.GONE);
					if (getActivity() != null) {
						SvoToast.showHint(getActivity(), "图片加载失败", Toast.LENGTH_SHORT);
					}
				}
			});
		}
		return view;
	}
	@Override
	public void onPhotoTap(View view, float x, float y) {
		PicBrowseActivity pActivity = (PicBrowseActivity) getActivity();
		pActivity.hideBtm();
	}
	@Override
	public void onClick(View v) {
		String curPath = PicUtil.getPicFile(picPath).getAbsolutePath();
		Intent intent = new Intent(getActivity(), GifActivity.class);
		intent.putExtra("path", curPath);
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		try {
			BitmapDrawable bd = (BitmapDrawable) imgView.getDrawable();
			Bitmap bitmap = bd.getBitmap();
			if (bitmap != null && !bitmap.isRecycled()) {
				bitmap.recycle();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
