package com.svo.laohan.model;

import com.svo.laohan.model.dao.RankData;

import android.content.Context;

public class RankService {
	Context context;
	RankData rankData;
	public RankService(Context context) {
		this.context = context;
		rankData = new RankData(context);
	}
	public void update(int resId,String json) {
		if (rankData.update(resId, json) <= 0) {
			rankData.add(resId, json);
		}
	}
}
