package com.svo.laohan.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.svo.laohan.R;
import com.svo.laohan.model.dao.entity.FileEntity;
import com.svo.laohan.util.StringUtil;
import com.svo.laohan.util.TypeUtil;

public class FileAdapter extends BaseAdapter{
		private Context context;
		private List<FileEntity> entities;
		public FileAdapter(Context context, List<FileEntity> names) {
			this.context = context;
			this.entities = names;
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
		public View getView(int position, View view, ViewGroup parent) {
			ViewHolder holder;
			if (view == null) {
				holder = new ViewHolder();
				view = LayoutInflater.from(context).inflate(R.layout.res_file_item, null);
				holder.textView1 = (TextView) view.findViewById(R.id.textView1);
				holder.textView2 = (TextView) view.findViewById(R.id.textView2);
				holder.textView3 = (TextView) view.findViewById(R.id.textView3);
				holder.img = (ImageView) view.findViewById(R.id.img);
				view.setTag(holder);
			}else {
				holder = (ViewHolder) view.getTag();
			}
			FileEntity fileEntity = entities.get(position);
			String fileName = fileEntity.getFileName();
			if (fileName.endsWith("/")) {
				fileName = fileName.substring(0, fileName.length());
			}
			fileName = StringUtil.sepPath(fileName)[1];
			holder.textView1.setText(fileName);
			if (!fileEntity.isDir()) {
				holder.textView2.setText(StringUtil.sizeConvert(fileEntity.getSize()));
				int file_type = TypeUtil.getType(fileName.substring(fileName.lastIndexOf(".")+1));
				switch (file_type) {
				case TypeUtil.DOC:
					holder.img.setImageResource(R.drawable.type_doc);
					break;
				case TypeUtil.IMAGE:
					holder.img.setImageResource(R.drawable.type_img);
					break;
				case TypeUtil.GIF:
					holder.img.setImageResource(R.drawable.type_gif);
					break;
				case TypeUtil.AUDIO:
					holder.img.setImageResource(R.drawable.type_audio);
					break;
				case TypeUtil.VIDEO:
					holder.img.setImageResource(R.drawable.type_video);
					break;
				case TypeUtil.APK:
					holder.img.setImageResource(R.drawable.type_apk);
					break;
				default:
					break;
				}
			}else {
				holder.textView2.setText("");
				holder.img.setImageResource(R.drawable.filechooser_folder);
			}
			if (fileEntity.isDown()) {
				holder.textView3.setText("已下载");
			} else {
				holder.textView3.setText("");
			}
			return view;
		}
		private class ViewHolder{
			TextView textView1;
			TextView textView2;
			TextView textView3;
			ImageView img;
		}
	}