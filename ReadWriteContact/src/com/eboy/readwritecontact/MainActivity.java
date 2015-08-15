package com.eboy.readwritecontact;

import java.util.ArrayList;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private EditText etName, etPhone;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        etName = (EditText) findViewById(R.id.et_name);
        etPhone = (EditText) findViewById(R.id.et_phone);
        findViewById(R.id.btn_add).setOnClickListener(this);
    }

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_add:
			savePhone();
			break;
		default:
			break;
		}
	}
	
	private void savePhone() {
		String name = etName.getText().toString();
		String phone = etPhone.getText().toString();
		
		if (TextUtils.isEmpty(name)) {
			showToast(R.string.input_name);
			return ;
		}
		if (TextUtils.isEmpty(phone)) {
			showToast(R.string.input_phone);
			return ;
		}
		
		try {
			addContact(name, phone);
			showToast(R.string.save_ok);
		} catch (Exception e) {
			e.printStackTrace();
			showToast(R.string.save_failed);
		}
	}
	
	private void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
	
	private void showToast(int strId) {
		Toast.makeText(this, strId, Toast.LENGTH_SHORT).show();
	}
	
	//添加联系人，使用事务
	public void addContact(String name, String phone) throws Exception {
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		ContentResolver resolver = getContentResolver();
		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
		ContentProviderOperation op1 = ContentProviderOperation.newInsert(uri)
			.withValue("account_name", null)
			.build();
		operations.add(op1);
		
		uri = Uri.parse("content://com.android.contacts/data");
		ContentProviderOperation op2 = ContentProviderOperation.newInsert(uri)
			.withValueBackReference("raw_contact_id", 0)
			.withValue("mimetype", "vnd.android.cursor.item/name")
			.withValue("data2", name)
			.build();
		operations.add(op2);
		
		ContentProviderOperation op3 = ContentProviderOperation.newInsert(uri)
			.withValueBackReference("raw_contact_id", 0)
			.withValue("mimetype", "vnd.android.cursor.item/phone_v2")
			.withValue("data1", phone)			
			.build();
		operations.add(op3);
		
		resolver.applyBatch("com.android.contacts", operations);
	}
}
