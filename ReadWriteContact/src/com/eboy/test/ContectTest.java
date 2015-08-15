package com.eboy.test;

import java.util.ArrayList;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

public class ContectTest extends AndroidTestCase {
	
	private static final String TAG = "TestContact";
	
	//查询所有联系人的姓名，电话，邮箱
	public void TestContact() throws Exception {		
		Uri uri = Uri.parse("content://com.android.contacts/contacts");
		ContentResolver resolver = getContext().getContentResolver();
		Cursor cursor = resolver.query(uri, new String[]{"_id"}, null, null, null);
		while (cursor.moveToNext()) {
			int contractID = cursor.getInt(0);
			StringBuilder sb = new StringBuilder("contractID=");
			sb.append(contractID);
			uri = Uri.parse("content://com.android.contacts/contacts/" + contractID + "/data");
			Cursor cursor1 = resolver.query(uri, new String[]{"mimetype", "data1", "data2"}, null, null, null);
			while (cursor1.moveToNext()) {
				String data1 = cursor1.getString(cursor1.getColumnIndex("data1"));
				String mimeType = cursor1.getString(cursor1.getColumnIndex("mimetype"));
				if ("vnd.android.cursor.item/name".equals(mimeType)) { //是姓名
					sb.append(",name=" + data1);
				} else if ("vnd.android.cursor.item/email_v2".equals(mimeType)) { //邮箱
					sb.append(",email=" + data1);
				} else if ("vnd.android.cursor.item/phone_v2".equals(mimeType)) { //手机
					sb.append(",phone=" + data1);
				}				
			}
			cursor1.close();
			Log.i(TAG, sb.toString());
		}
		cursor.close();
	}
	
	//查询指定电话的联系人姓名，邮箱
	public void testContactNameByNumber() throws Exception {
		String number = "18052369652";
		Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/" + number);
		ContentResolver resolver = getContext().getContentResolver();
		Cursor cursor = resolver.query(uri, new String[]{"display_name"}, null, null, null);
		if (cursor.moveToFirst()) {
			String name = cursor.getString(0);
			Log.i(TAG, name);
		}
		cursor.close();
	}
	
	//添加联系人，使用事务
	public void testAddContact() throws Exception {
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		ContentResolver resolver = getContext().getContentResolver();
		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
		ContentProviderOperation op1 = ContentProviderOperation.newInsert(uri)
			.withValue("account_name", null)
			.build();
		operations.add(op1);
		
		uri = Uri.parse("content://com.android.contacts/data");
		ContentProviderOperation op2 = ContentProviderOperation.newInsert(uri)
			.withValueBackReference("raw_contact_id", 0)
			.withValue("mimetype", "vnd.android.cursor.item/name")
			.withValue("data2", "0老刘")
			.build();
		operations.add(op2);
		
		ContentProviderOperation op3 = ContentProviderOperation.newInsert(uri)
			.withValueBackReference("raw_contact_id", 0)
			.withValue("mimetype", "vnd.android.cursor.item/phone_v2")
			.withValue("data1", "18938038622")			
			.build();
		operations.add(op3);
		
		ContentProviderOperation op4 = ContentProviderOperation.newInsert(uri)
		.withValueBackReference("raw_contact_id", 0)
		.withValue("mimetype", "vnd.android.cursor.item/email_v2")
		.withValue("data1", "asdfasfad@163.com")			
		.build();
	operations.add(op4);
		
		resolver.applyBatch("com.android.contacts", operations);
	}
	
}
