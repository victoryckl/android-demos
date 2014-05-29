package com.example.syncdemo;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;

public class SyncDemoActivity extends Activity {
	private static final String TAG = SyncDemoActivity.class.getSimpleName();

	// Instance fields
	private Account mAccount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sync_demo);
		// Create the dummy account
//		mAccount = CreateSyncAccount(this);
	}

	/**
	 * Create a new dummy account for the sync adapter
	 * 
	 * @param context
	 *            The application context
	 */
	public static Account CreateSyncAccount(Context context) {
		Log.i(TAG, "uid:" + Binder.getCallingUid());
		
		// Create the account type and default account
		Account newAccount = new Account(Constants.ACCOUNT, Constants.ACCOUNT_TYPE);
		// Get an instance of the Android account manager
		AccountManager accountManager = 
				(AccountManager) context.getSystemService(ACCOUNT_SERVICE);
		/*
		 * Add the account and account type, no password or user data If
		 * successful, return the Account object, otherwise report an error.
		 */
		if (accountManager.addAccountExplicitly(newAccount, null, null)) {
			/*
			 * If you don't set android:syncable="true" in in your <provider>
			 * element in the manifest, then call context.setIsSyncable(account,
			 * AUTHORITY, 1) here.
			 */
			Log.i(TAG, "addAccountExplicitly(), success");
		} else {
			/*
			 * The account exists or some other error occurred. Log this, report
			 * it, or handle it internally.
			 */
			Log.i(TAG, "addAccountExplicitly(), failed");
		}
		return newAccount;
	}
}
