package com.svo.laohan.model.dao;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.svo.laohan.model.dao.entity.FileEntity;

public class FileData {
	private DBHelper dbHelper;
//	private Context context;

	public FileData(Context context) {
//		this.context = context;
		dbHelper = DBHelper.getInstance(context);
	}
	/**
	 * 从File表中查询多条记录
	 * @param sql 
	 * @param selectionArgs
	 * @return
	 */
	public List<FileEntity> queryFiles(String sql,String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		List<FileEntity> entities = new LinkedList<FileEntity>();
		if (cursor == null) {
			return entities;
		}
		try {
			FileEntity entity = null;
			while (cursor.moveToNext()) {
				entity = new FileEntity();
				try {
					entity.set_id(cursor.getString(cursor.getColumnIndex("_id")));
					entity.setDir(cursor.getInt(cursor.getColumnIndex("isDir")) == 1);
					entity.setFileName(cursor.getString(cursor.getColumnIndex("fileName")));
					entity.setParDir(cursor.getString(cursor.getColumnIndex("parDir")));
					entity.setSize(cursor.getLong(cursor.getColumnIndex("size")));
					entity.setMtime(cursor.getLong(cursor.getColumnIndex("mtime")));
				} catch (Exception e) {
					Log.e("baidu", "已处理");
				}
				if (entity.isDir()) {
					entities.add(0,entity);
				} else {
					entities.add(entity);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (cursor != null) {
				cursor.close();
			}
		}
		return entities;
	}
	/**
	 * 向file表中插入多条数据
	 * @param list 
	 */
	public void insert(JSONArray arr) {
		for (int i = 0; i < arr.length(); i++) {
			try {
				insert(arr.getJSONObject(i));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 向file表中插入单条数据
	 * @param fileInfo
	 * @return
	 */
	public long insert(JSONObject object) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = getFileInfoValue(object);
		return db.insert("file", "blockList", values);
	}
	/**
	 * 根据PCSCommonFileInfo组织插入数据
	 * @param fileInfo
	 * @return
	 */
	private ContentValues getFileInfoValue(JSONObject object) {
		ContentValues values = new ContentValues();
		values.put("parDir", object.optString("parDir"));//带斜杠
		values.put("fileName", object.optString("name"));
		values.put("size", object.optInt("size"));
		values.put("isDir", object.optBoolean("isDir"));
		values.put("mtime", object.optLong("mtime"));
		return values;
	}
	/**
	 * 更新下载文件
	 * @param entity
	 * @return
	 */
	public int update(FileEntity entity) {
		ContentValues values = new ContentValues();
		values.put("localPath", entity.getLocalPath());
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		return db.update("down", values, "_id = ?", new String[]{entity.get_id()});
	}
	/**
	 * 删除某个远程目录下的文件信息
	 * @param rePath 远程文件目录
	 */
	public void delOldFile(String rePath) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete("file", "parDir like '%"+rePath+"%'", null);
	}
	/**
	 * 删除一条记录
	 * @param _id
	 * @return
	 */
	public int delOneFile(String _id) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		return db.delete("file", "_id = ?", new String[]{_id});
	}
	/**
	 * 删除
	 * @param rePath
	 * @return
	 */
	public long delDown(String rePath) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		return db.delete("down", "path=?", new String[]{rePath});
	}
	public void updateName(String _id, String name) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("update file set fileName = ? where _id = ?", new String[]{name,_id});
	}
}
