package com.svo.laohan.fragment;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.waps.AppConnect;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.svo.laohan.R;
import com.svo.laohan.model.FileService;
import com.svo.laohan.model.Mjifen;
import com.svo.laohan.model.SearchService;
import com.svo.laohan.util.Constant;
import com.svo.laohan.util.FileUtil;
import com.svo.laohan.util.StringUtil;
import com.svo.platform.utils.HttpUtil;
import com.svo.platform.utils.SvoToast;
import com.tencent.mm.sdk.platformtools.NetStatusUtil;

public class Search extends Fragment implements View.OnClickListener, OnItemClickListener {
	private View root;
	private EditText search_et;
	private ListView listView;
	private SearchService searchService;
	private String initKey = "";
	private GridView gridView;
	private GridView gameGrid;
	private LinearLayout hotLine;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.search, null);
		search_et = (EditText) root.findViewById(R.id.search_et);
		hotLine = (LinearLayout) root.findViewById(R.id.hotLine);
		listView = (ListView) root.findViewById(R.id.search_list);
		gridView = (GridView) root.findViewById(R.id.grid);
		gameGrid = (GridView) root.findViewById(R.id.grid2);
		initGrid();
		root.findViewById(R.id.search_btn).setOnClickListener(this);
		AppConnect.getInstance(getActivity()).setAdBackColor(Color.argb(255, 120, 240, 120));
		AppConnect.getInstance(getActivity()).setAdForeColor(Color.YELLOW);
		LinearLayout miniLayout = (LinearLayout) root.findViewById(R.id.miniAdLinearLayout);
		AppConnect.getInstance(getActivity()).showMiniAd(getActivity(), miniLayout, 10); // 10
		return root;
	}

	private void initGrid() {
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String key = ((TextView)view).getText().toString();
				search_et.setText(key);
				search(key);
			}
		});
		gameGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				AppConnect.getInstance(getActivity()).showGameOffers(getActivity());
			}
		});
		final SharedPreferences preferences = getActivity().getSharedPreferences("weitu", 0);
		String hotKeys = preferences.getString("hotKeys", "");
		if (TextUtils.isEmpty(hotKeys)) {
			if (NetStatusUtil.isConnected(getActivity())) {
				hotLine.setVisibility(View.GONE);
				HttpUtil.get(Constant.gen+"HotSearch?type=hotKey", null, new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(String arg0) {
						super.onSuccess(arg0);
						preferences.edit().putString("hotKeys", arg0).commit();
						hotLine.setVisibility(View.VISIBLE);
						setGrid(arg0);
					}
				});
			} else {
				hotKeys = "读大学究竟读什么;富爸爸穷爸爸;7天女学馆;金瓶梅;诱惑与欲望;梦中缘;做你想做的人;绝对禁书;你为什么越忙越穷;狄仁杰断案传奇。抢话费斗地主;MM炸金花;欲仙;三国杀";
				setGrid(hotKeys);
			}
		} else {
			hotLine.setVisibility(View.VISIBLE);
			setGrid(hotKeys);
		}
		
	}

	protected void setGrid(String hotKeys) {
		if (TextUtils.isEmpty(hotKeys)) {
			return;
		}
		final String[] arr = hotKeys.split("。");
		gridView.setAdapter(new GridAdapter(arr[0].split(";")));
		gameGrid.setAdapter(new GridAdapter(arr[1].split(";")));
	}
	class GridAdapter extends BaseAdapter{
		String[] arrs;
		public GridAdapter(String[] arrs) {
			this.arrs = arrs;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(getActivity()).inflate(R.layout.search_grid_item, null);
			TextView btn = (TextView) convertView;
			btn.setText(arrs[position]);
			return convertView;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public int getCount() {
			return arrs.length;
		}
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Bundle bundle = getArguments();
		if (bundle != null) {
			initKey = bundle.getString("key");
		}
		if (!TextUtils.isEmpty(initKey)) {
			search_et.setText(initKey);
		}
		listView.setOnItemClickListener(this);
		if (searchService == null) {
			searchService = new SearchService(getActivity());
		}
	}

	/*
	 * @Override public void onResume() { super.onResume(); JSONObject list =
	 * searchService.getList(); if (list != null) { SearchAdapter adapter = new
	 * SearchAdapter(getActivity(), list); listView.setAdapter(adapter); } }
	 */
	@Override
	public void onClick(View v) {
		String searchKey = search_et.getText().toString().trim();
		search(searchKey);
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * @param key 
	 * 
	 */
	public void search(String searchKey) {
		if (TextUtils.isEmpty(searchKey)) {
			Toast.makeText(getActivity(), "搜索内容不可为空", Toast.LENGTH_LONG).show();
			hotLine.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
			return;
		}
		hotLine.setVisibility(View.GONE);
		listView.setVisibility(View.VISIBLE);
		searchService.search(searchKey, listView);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		JSONObject json = searchService.getList();
		JSONArray booksArr = new JSONArray();
		try {
			booksArr = new JSONArray(json.optString("books"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		final FileService fileService = new FileService(getActivity());
		int jifen = new Mjifen(getActivity()).getJifen();
		if (jifen == 5) {
			SvoToast.showHint(getActivity(), "您只剩下5个比特币了,快去免费赚取比特币吧", Toast.LENGTH_LONG);
		} else if (jifen == 0) {
			new Mjifen(getActivity()).showDialog();
			return;
		}
		final JSONObject jo = booksArr.optJSONObject(arg2);
		String downUrl = jo.optString("htmlUrl");
		String fileName = jo.optString("name")+"."+StringUtil.getSuffix(downUrl);
		boolean isExist = FileUtil.isFileExist(Constant.Save_path + fileName);
		if (isExist) {
			/*
			 * if (TypeUtil.getTypeBasePath(fileName) == TypeUtil.IMAGE ||
			 * TypeUtil.getTypeBasePath(fileName) == TypeUtil.GIF) { new
			 * FileService(getActivity()).browsePic(searchService.getList(),
			 * arg2); return; }
			 */
			new FileService(getActivity()).openFile(Constant.Save_path + fileName);
		} else {
			fileService.downFile(jo.optString("htmlUrl"),jo.optString("name"));
		}
	}
}
