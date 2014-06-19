package com.svo.laohan.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class RankData {
	private DBHelper dbHelper;
//	private Context context;

	public RankData(Context context) {
//		this.context = context;
		dbHelper = DBHelper.getInstance(context);
	}
	/**
	 * 更新记录
	 * @param resId
	 * @param json
	 * @return 返回受影响的行数
	 */
	public int update(int resId,String json){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("json", json);
		return db.update("rank", values , "resId = ?", new String[]{resId+""});
	}
	public long add(int resId,String json){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("json", json);
		values.put("resId", resId);
		return db.insert("rank", "json", values);
	}
	public String query(int resId){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String sql = "select json from rank where resId = ?";
		Cursor cursor = db.rawQuery(sql, new String[]{resId+""});
		try {
			if (cursor.moveToNext()) {
				return cursor.getString(cursor.getColumnIndex("json"));
			}
		}finally{
			cursor.close();
		}
		return "";
	}
}
