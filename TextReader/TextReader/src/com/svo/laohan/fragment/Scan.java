package com.svo.laohan.fragment;

import java.util.ArrayList;

import org.json.JSONArray;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.svo.laohan.MyListActivity;
import com.svo.laohan.R;
import com.svo.laohan.adapter.ResAdapter;
import com.svo.laohan.model.FileService;
import com.svo.laohan.model.ResService;
import com.svo.laohan.model.UploadService;
import com.svo.laohan.model.dao.DBHelper;
import com.svo.laohan.model.dao.FileData;
import com.svo.laohan.util.NetStateUtil;
import com.svo.platform.widget.PushRefreshListView;

public class Scan extends SherlockFragment {
	private View root;
	private PushRefreshListView listView;
	private ArrayList<String> names;
	private String curPath = "/";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if (args != null) {
			curPath = args.getString("curPath");
		}/*else {
			curPath = "/网友共享/"+new UploadService(getActivity()).getUserId()+"/";
		}*/
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.scan, null);
		listView = (PushRefreshListView) root.findViewById(R.id.listView1);
		names = DBHelper.getInstance(getActivity()).getRes(curPath);
		ResAdapter adapter = new ResAdapter(getActivity(),names);
		listView.setAdapter(adapter);
		if (names.size() < 1) {
			load();
		}
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent(getActivity(), MyListActivity.class);
				intent.putExtra("rePath", names.get(arg2-1));
				startActivity(intent);
			}
		});
		/** 下拉监听 */
		listView.setonRefreshListener(new PushRefreshListView.OnRefreshListener() {
			public void onRefresh() {
				load();
			}
		});
		return root;
	}
	private void load() {
		new AsyncTask<Void, Void, Boolean>() {
    		private ProgressDialog progressDialog;
    		protected void onPreExecute() {
    			if (names.size() < 1) {
    				progressDialog = ProgressDialog.show(getActivity(), null, "加载中...", true, true);
    				progressDialog.setCanceledOnTouchOutside(false);
    			}
    		}
    		@Override
    		protected Boolean doInBackground(Void... params) {
    			boolean flag = new FileService(getActivity()).updateRes(curPath);
    			return flag;
    		}
    		protected void onPostExecute(Boolean result) {
    			if (progressDialog != null) {
    				progressDialog.dismiss();
				}
    			listView.onRefreshComplete();
    			if (result) {
    				names = DBHelper.getInstance(getActivity()).getRes(curPath);
    				ResAdapter adapter = new ResAdapter(getActivity(),names);
    				listView.setAdapter(adapter);
				}else {
					if (getActivity() != null) {
						Toast.makeText(getActivity(), "已是最新", Toast.LENGTH_SHORT).show();
					}
				}
    		}
		}.execute();
	}
	
}
