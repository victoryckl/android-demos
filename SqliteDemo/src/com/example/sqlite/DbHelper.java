package com.example.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
	private static final String TAG = DbHelper.class.getSimpleName();

	private static final String DATABASE_NAME = "demo.db";
	private static final int DATABASE_VERSION = 1;
	public static final String TABLE = "demo";
	
	//field
	public interface Field {
		String id = "_id";
		String dataStr = "dataStr";
		String dataInt = "dataInt";
		String dataBool = "dataBool";
	}
	
	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE IF NOT EXISTS "+TABLE +
				"("
				+ Field.id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ Field.dataStr + " CHAR(64), "
				+ Field.dataInt + " INTEGER, "
				+ Field.dataBool + " INTEGER)";
		Log.i(TAG, "onCreate(): " + sql);
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "DROP TABLE IF EXISTS "+TABLE;
		Log.i(TAG, "onUpgrade(): " + sql);
		db.execSQL(sql);
		onCreate(db);
	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		super.onDowngrade(db, oldVersion, newVersion);
		String sql = "DROP TABLE IF EXISTS "+TABLE;
		Log.i(TAG, "onDowngrade(): " + sql);
		db.execSQL(sql);
		onCreate(db);
	}
}
