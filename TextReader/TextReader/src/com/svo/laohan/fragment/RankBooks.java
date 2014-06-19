package com.svo.laohan.fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.svo.laohan.BookDetail;
import com.svo.laohan.R;
import com.svo.laohan.model.RankService;
import com.svo.laohan.model.dao.RankData;
import com.svo.platform.utils.HttpUtil;
import com.svo.platform.utils.SvoToast;
import com.svo.platform.widget.PushRefreshListView;
import com.svo.platform.widget.PushRefreshListView.OnRefreshListener;
import com.tencent.mm.sdk.platformtools.NetStatusUtil;

public final class RankBooks extends Fragment implements OnItemClickListener, OnRefreshListener {
    private static final String KEY_CONTENT = "TestFragment:Content";
    
    public static RankBooks newInstance(JSONObject jsonObject, String resName) {
        RankBooks fragment = new RankBooks();
        fragment.mContent = jsonObject.toString();
        fragment.resName = resName;
        return fragment;
    }

    private String mContent = "";
    private String resName = "";
    MyAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }
    }
    //初始化数据
    private void initData(JSONArray jsonArray) {
    	adapter = new MyAdapter(getActivity(),jsonArray);
		listView.setAdapter(adapter);
	}
    private View root;
    private PushRefreshListView listView;
    private ProgressBar progressBar;
    int resId;//资源ID
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.rank_list, null);
		listView = (PushRefreshListView) root.findViewById(R.id.list);
		listView.setonRefreshListener(this);
		progressBar = (ProgressBar) root.findViewById(R.id.progressBar);
		listView.setOnItemClickListener(this);
		try {
			JSONObject jo = new JSONObject(mContent);
			resId = jo.optInt("id");
			String json = new RankData(getActivity()).query(resId);
			if (TextUtils.isEmpty(json)) {
				progressBar.setVisibility(View.VISIBLE);
				reqBooks();
			} else {
				initData(new JSONArray(json));
				if (progressBar != null) {
					progressBar.setVisibility(View.GONE);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return root;
    }
	/**
	 * 
	 */
	private void reqBooks() {
		if (!NetStatusUtil.isConnected(getActivity())) {
			SvoToast.showHint(getActivity(), "网络不可用", Toast.LENGTH_LONG);
			if (progressBar != null) {
				progressBar.setVisibility(View.GONE);
			}
		}else {
			HttpUtil.get("http://2.dubinwei.duapp.com/Rank?resId="+resId, null, new JsonHttpResponseHandler(){
				@Override
				public void onStart() {
					super.onStart();
				}
				@Override
				public void onSuccess(JSONArray arg0) {
					super.onSuccess(arg0);
					if (arg0 != null && arg0.length() > 0) {
						new RankService(getActivity()).update(resId, arg0.toString());
					}
					if (progressBar != null) {
						progressBar.setVisibility(View.GONE);
					}
					if (listView != null) {
						listView.onRefreshComplete();
					}
					initData(arg0);
				}
				@Override
				public void onFailure(Throwable arg0) {
					if (progressBar != null) {
						progressBar.setVisibility(View.GONE);
					}
					if (listView != null) {
						listView.onRefreshComplete();
					}
					SvoToast.showHint(getActivity(), "请求数据失败", Toast.LENGTH_LONG);
				}
			});
		}
	}
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (adapter != null) {
			JSONObject jo = (JSONObject) adapter.getItem(position-1);
			Intent intent = new  Intent(getActivity(), BookDetail.class);
			intent.putExtra("json", jo.toString());
			intent.putExtra("resName", resName);
			startActivity(intent);
		}
	}
	@Override
	public void onRefresh() {
		reqBooks();
	}
}
