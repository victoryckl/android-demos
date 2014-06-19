package com.svo.laohan.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.waps.AppConnect;
import cn.waps.UpdatePointsNotifier;

import com.actionbarsherlock.app.SherlockFragment;
import com.svo.laohan.JifenActivity;
import com.svo.laohan.R;
import com.svo.laohan.ShareList;
import com.svo.laohan.adapter.ShareAdapter;
import com.svo.laohan.model.Mjifen;
import com.svo.platform.utils.SvoToast;

public class Share extends SherlockFragment implements OnItemClickListener,OnClickListener{
	private View root;
	private ListView listView;
	private TextView jifenTv;
	private TextView vipTv;
	Mjifen mjifen;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.share_fragment, null);
		listView = (ListView) root.findViewById(R.id.list);
		listView.setOnItemClickListener(this);
		jifenTv = (TextView) root.findViewById(R.id.jifen);
		vipTv = (TextView) root.findViewById(R.id.vip);
		vipTv.setTextColor(Color.RED);
		mjifen = new Mjifen(getActivity());
		jifenTv.append(mjifen.getJifen()+" 个");
		if (mjifen.isVip()) {
			vipTv.setText("(VIP)");
		}else {
			vipTv.setText("(非VIP)");
		}
		root.findViewById(R.id.getjifen).setOnClickListener(this);
		return root;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ShareAdapter adapter = new ShareAdapter(getActivity());
		listView.setAdapter(adapter);
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(getActivity(), ShareList.class);
		intent.putExtra("index", position+1);
		getActivity().startActivityForResult(intent, 11);
	}
	@Override
	public void onClick(View v) { //赚取积分
		startActivityForResult(new Intent(getActivity(), JifenActivity.class),123);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 123) {
			AppConnect.getInstance(getActivity()).getPoints(new UpdatePointsNotifier() {
				
				@Override
				public void getUpdatePointsFailed(String arg0) {
					if (getActivity() != null) {
						SvoToast.showHint(getActivity(), "获取比特币失败", Toast.LENGTH_SHORT);
					}
				}
				
				@Override
				public void getUpdatePoints(String arg0, final int arg1) {
					if (getActivity() != null) {
						mjifen.saveJifen(arg1);
						jifenTv.post(new Runnable() {
							
							@Override
							public void run() {
								jifenTv.setText("我的比特币："+arg1+" 个");
								if (mjifen.isVip()) {
									vipTv.setText("(VIP)");
								}else {
									vipTv.setText("(非VIP)");
								}
							}
						});
					}
				}
			});
		}
	}
}
