package com.svo.laohan.model.dao;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.svo.laohan.model.dao.entity.FileEntity;

public class DBHelper extends SQLiteOpenHelper{
	private static final int VERSION = 9;
	private static DBHelper dbHelper;
    private DBHelper(Context context) {
        super(context, "laohan.db", null, VERSION);
    }
    public static DBHelper getInstance(Context context) {
		if (dbHelper == null) {
			dbHelper = new DBHelper(context);
		}
		return dbHelper;
	}
	private String sql1 = "CREATE TABLE file(_id INTEGER PRIMARY KEY AUTOINCREMENT,fileName Text,parDir Text,size INTEGER,isDir Text,mtime Text)";
	private String sql2 = "CREATE TABLE IF NOT EXISTS  down(path Text unique ,localPath Text);";
	private String sql3 = "CREATE TABLE IF NOT EXISTS  rank(resId INTEGER PRIMARY KEY,json Text)";
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(sql1);
		db.execSQL(sql2);
		db.execSQL(sql3);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		switch (oldVersion) {
		case 2:
		case 3:
		case 5:
			db.execSQL("drop table if exists file");
			db.execSQL("drop table if exists res");
			db.execSQL("drop table if exists zires");
			db.execSQL("drop table if exists zifile");
		case 6:
			db.execSQL("drop table if exists file");
			db.execSQL(sql1);
			db.execSQL(sql2);
		case 7:
			db.execSQL(sql3);
		case 8:
			db.execSQL("drop table if exists file");
			db.execSQL("CREATE TABLE file(_id INTEGER PRIMARY KEY AUTOINCREMENT,fileName Text,parDir Text,size INTEGER,isDir Text,mtime Text)");
		}
	}
	public void addRes(ArrayList<String> resNames) {
		SQLiteDatabase database = getWritableDatabase();
		String sql = "delete from res";
		database.beginTransaction();
		database.execSQL(sql);
		for (String resName : resNames) {
			sql = "insert into res(resName) values(?)";
			database.execSQL(sql, new String[]{resName});
		}
		database.setTransactionSuccessful();
		database.endTransaction();
		database.close();
	}
	/**
	 * 查询某目录下的子目录
	 * @param path
	 * @return
	 */
	public ArrayList<String> getRes(String path) {
		SQLiteDatabase database = getWritableDatabase();
		String sql = "select fileName from file where parDir in (?,?) order by mtime desc";
		Cursor cursor = database.rawQuery(sql, new String[]{path,path+"/"});
		ArrayList<String> names = new ArrayList<String>();
		String name;
		while (cursor.moveToNext()) {
			name = cursor.getString(cursor.getColumnIndex("fileName"));
			names.add(name);
		}
		cursor.close();
		return names;
	}
	/**
	 * 得到某个类别下的文件名称
	 * @param zh 某个类别的中文名字
	 * @return
	 */
	public ArrayList<FileEntity> getFiles(String path) {
		SQLiteDatabase database = getWritableDatabase();
		String sql = "select * from file where parDir in (?,?) order by by mtime";
		Cursor cursor = database.rawQuery(sql, new String[]{path,path+"/"});
		ArrayList<FileEntity> entities = new ArrayList<FileEntity>();
		FileEntity entity;
		while (cursor.moveToNext()) {
			entity = new FileEntity();
			if ("yes".equals(cursor.getString(cursor.getColumnIndex("down")))) {
				entity.setDown(true);
			} else {
				entity.setDown(false);
			}
			entities.add(entity);
		}
		cursor.close();
		database.close();
		return entities;
	}
}
