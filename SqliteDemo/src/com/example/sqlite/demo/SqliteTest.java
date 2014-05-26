package com.example.sqlite.demo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.sqlite.DbHelper;
import com.example.sqlite.DbHelper.Field;

public class SqliteTest extends AndroidTestCase {
	private static final String TAG = SqliteTest.class.getSimpleName();
	private static final String TABLE = DbHelper.TABLE;
	
	private DbHelper helper;
	private SQLiteDatabase db;
	
	private void init() {
		helper = new DbHelper(getContext());
	}
	
	public void insert() {
		init();
		
		db = helper.getWritableDatabase();
		
		ContentValues v = new ContentValues();
		v.put(Field.dataStr, "This is a String...2");
		v.put(Field.dataInt, 3322);
		v.put(Field.dataBool, false);
		
		long rowId = db.insert(TABLE, null, v);
		Log.i(TAG, "rowId: " + rowId);
	}
	
	public void query() {
		init();
		
		db = helper.getReadableDatabase();
		
		String selection = null;
		String[] args = null;
		
		Cursor c = db.query(TABLE, null, selection, args, null, null, null);
		if (c != null) {
			if (c.getCount() > 0) {
				c.moveToFirst();
				do {
					String dataStr = c.getString(c.getColumnIndex(Field.dataStr));
					int dataint = c.getInt(c.getColumnIndex(Field.dataInt));
					boolean dataBool = c.getInt(c.getColumnIndex(Field.dataBool)) != 0 ? true : false;
					Log.i(TAG, "dataStr: "+dataStr);
					Log.i(TAG, "dataint: "+dataint);
					Log.i(TAG, "dataBool: "+dataBool);
				} while (c.moveToNext());
			}
			c.close();
		}
	}
}
