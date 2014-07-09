package com.example.sqlciphertest;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	
	private SQLiteDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		SQLiteDatabase.loadLibs(this);
		MyDatabaseHelper dbHelper = new MyDatabaseHelper(this, "demo.db", null, 1);
		db = dbHelper.getWritableDatabase("secret_key");
		Button addData = (Button) findViewById(R.id.add_data);
		Button queryData = (Button) findViewById(R.id.query_data);
		addData.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ContentValues values = new ContentValues();
				values.put("name", "达芬奇密码");
				values.put("pages", 566);
				db.insert("Book", null, values);
			}
		});
		queryData.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Cursor cursor = db.query("Book", null, null, null, null, null, null);
				if (cursor != null) {
					while (cursor.moveToNext()) {
						String name = cursor.getString(cursor.getColumnIndex("name"));
						int pages = cursor.getInt(cursor.getColumnIndex("pages"));
						Log.d("TAG", "book name is " + name);
						Log.d("TAG", "book pages is " + pages);
					}
				}
				cursor.close();
			}
		});
	}

}
