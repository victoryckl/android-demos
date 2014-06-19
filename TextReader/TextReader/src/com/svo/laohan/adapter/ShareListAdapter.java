package com.svo.laohan.adapter;

import java.util.List;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.svo.laohan.R;
import com.svo.laohan.ShareList;
import com.svo.laohan.model.dao.entity.FileEntity;
import com.svo.laohan.util.StringUtil;
import com.svo.laohan.util.TypeUtil;

public class ShareListAdapter extends BaseAdapter {
	private Activity context;
	private List<FileEntity> entities;
	private ShareList shareFragment;
	private LinearLayout tmpLinear;
	public ShareListAdapter(Activity context, List<FileEntity> names,ShareList shareFragment) {
		this.context = context;
		this.entities = names;
		this.shareFragment = shareFragment;
	}

	@Override
	public int getCount() {
		return entities.size();
	}

	@Override
	public Object getItem(int position) {
		return entities.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public int getItemViewType(int position) {
		FileEntity fileEntity = entities.get(position);
		if (fileEntity.isDir()) {
			return 0;
		} else {
			return 1;
		}
	}
	@Override
	public int getViewTypeCount() {
		return 2;
	}
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder;
		int type = getItemViewType(position);
		if (view == null) {
			holder = new ViewHolder();
			if (type == 0) {
				view = (View) LayoutInflater.from(context).inflate(
						R.layout.res_file_item_dir, null);
			}else {
				view = (View) LayoutInflater.from(context).inflate(
						R.layout.res_file_item_sharelist, null);
				holder.textView2 = (TextView) view.findViewById(R.id.textView2);
				holder.img = (ImageView) view.findViewById(R.id.img);
				holder.linear = (LinearLayout) view.findViewById(R.id.linear1);
			}
			holder.typeImg = (ImageView) view.findViewById(R.id.type_img);
			holder.textView1 = (TextView) view.findViewById(R.id.textView1);
			view.setTag(holder);
		}else {
			holder = (ViewHolder) view.getTag();
		}
		FileEntity fileEntity = entities.get(position);
		String fileName = fileEntity.getFileName();
		if (fileName.endsWith("/")) {
			fileName = fileName.substring(0, fileName.length()-1);
		}
		fileName = StringUtil.sepPath(fileName)[1];
		holder.textView1.setText(fileName);
		if (fileEntity.isDir()) {
			holder.typeImg.setImageResource(R.drawable.filechooser_folder);
//			holder.img.setVisibility(View.GONE);
//			holder.textView2.setVisibility(View.GONE);
		} else {
			holder.img.setVisibility(View.VISIBLE);
			holder.img.setOnClickListener(new MyListener(fileEntity, holder.linear));
			int file_type = TypeUtil.getType(fileName.substring(fileName.lastIndexOf(".")+1));
			switch (file_type) {
			case TypeUtil.DOC:
				holder.typeImg.setImageResource(R.drawable.type_doc);
				holder.textView1.setText(fileName);
				break;
			case TypeUtil.IMAGE:
				holder.typeImg.setImageResource(R.drawable.type_img);
				holder.textView1.setText(fileName);
				break;
			case TypeUtil.GIF:
				holder.typeImg.setImageResource(R.drawable.type_gif);
				break;
			case TypeUtil.AUDIO:
				holder.typeImg.setImageResource(R.drawable.type_audio);
				break;
			case TypeUtil.VIDEO:
				holder.typeImg.setImageResource(R.drawable.type_video);
				break;
			case TypeUtil.APK:
				holder.typeImg.setImageResource(R.drawable.type_apk);
				break;
			default:
				break;
			}
			holder.textView2.setVisibility(View.VISIBLE);
			holder.textView2.setText(StringUtil.sizeConvert(fileEntity.getSize()));
		}
		return view;
	}
	private class ViewHolder{
		TextView textView1;
		TextView textView2;
		ImageView img;
		ImageView typeImg;
		LinearLayout linear;
	}
	private class MyListener implements View.OnClickListener {
		private FileEntity fileEntity;
		LinearLayout linear;

		public MyListener(FileEntity fileEntity, LinearLayout linear) {
			this.fileEntity = fileEntity;
			this.linear = linear;
		}

		@Override
		public void onClick(View view) {
			if (linear.getVisibility() == View.VISIBLE) {
				linear.setVisibility(View.GONE);
			} else {
				OnClickListener linearListener = new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						linear.setVisibility(View.GONE);
						shareFragment.operate(v.getId(),fileEntity);
					}
				};
				linear.findViewById(R.id.del).setOnClickListener(
						linearListener);
				linear.findViewById(R.id.rename).setOnClickListener(
						linearListener);
				if (fileEntity.isDir()) {
					linear.findViewById(R.id.down).setVisibility(View.GONE);
				} else {
					linear.findViewById(R.id.down).setOnClickListener(
							linearListener);
				}
				linear.findViewById(R.id.share).setOnClickListener(
						linearListener);
				if (tmpLinear != null) {
					tmpLinear.setVisibility(View.GONE);
				}
				linear.setVisibility(View.VISIBLE);
				tmpLinear = linear;
			}
		}
	}
}