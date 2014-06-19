package com.svo.laohan.adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.svo.laohan.MyListActivity;
import com.svo.laohan.R;
import com.svo.laohan.util.StringUtil;
import com.svo.laohan.util.TypeUtil;

public class SearchAdapter extends BaseAdapter{
		private Context context;
		private JSONArray booksArr = new JSONArray();
		public SearchAdapter(Activity activity, JSONObject json) {
			try {
				booksArr=new JSONArray(json.optString("books")) ;
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.context = activity;
		}
		@Override
		public int getCount() {
			return booksArr.length();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public int getItemViewType(int position) {
			try {
				JSONObject object = booksArr.getJSONObject(position);
				int type = object.optInt("type");
				return type/100 == 1?0:1;
			} catch (JSONException e) {
				e.printStackTrace();
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
			JSONObject object = null;
			try {
				object = booksArr.getJSONObject(position);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (type == 0) {//多多
				if (view == null) {
					holder = new ViewHolder();
					view = (View) LayoutInflater.from(context).inflate(R.layout.search_file_item2, null);
					holder.name = (TextView) view.findViewById(R.id.name);
					holder.size = (TextView) view.findViewById(R.id.size);
					holder.img = (ImageView) view.findViewById(R.id.img);
					view.setTag(holder);
				}else {
					holder = (ViewHolder) view.getTag();
				}
				String author = object.optString("author");
				String resName = object.optString("resName");
				if (!TextUtils.isEmpty(author) && !TextUtils.isEmpty(resName)) {
					Spannable WordtoSpan = new SpannableString(object.optString("name")+"　("+author+" | "+resName+")");  
					WordtoSpan.setSpan(new AbsoluteSizeSpan(21), object.optString("name").length(), WordtoSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					holder.name.setText(WordtoSpan);
				} else {
					holder.name.setText(object.optString("name"));
				}
				holder.size.setText(object.optString("descrip"));
			} else {//百度云
				if (view == null) {
					holder = new ViewHolder();
					view = (View) LayoutInflater.from(context).inflate(R.layout.search_file_item, null);
					holder.name = (TextView) view.findViewById(R.id.name);
					holder.size = (TextView) view.findViewById(R.id.size);
					holder.img = (ImageView) view.findViewById(R.id.img);
					holder.vagaa = (ImageView) view.findViewById(R.id.vagaa);
					view.setTag(holder);
				} else {
					holder = (ViewHolder) view.getTag();
				}
//				final PCSCommonFileInfo pcsCommonFileInfo = list.get(position);
				final String downUrl = object.optString("htmlUrl");
				String suffix = "."+StringUtil.getSuffix(downUrl);//得到后缀名
				holder.name.setText(object.optString("name")+suffix);
//				holder.size.setText(StringUtil.sizeConvert(object.optLong("size")));
				holder.size.setVisibility(View.GONE);
				int file_type = TypeUtil.getType(downUrl.substring(downUrl.lastIndexOf(".")+1));
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
				holder.vagaa.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(context, MyListActivity.class);
						intent.putExtra("rePath", downUrl);
						context.startActivity(intent);
					}
				});
			}
			return view;
		}
		private class ViewHolder{
			TextView name;
			TextView size;
			ImageView vagaa;
			ImageView img;
		}
	}