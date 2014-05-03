package com.example.syncdemo.authenticator;

import com.example.syncdemo.AccountInfo;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class AuthenticatorService extends Service {
	private static final String TAG = AuthenticatorService.class.getSimpleName();
	
	private Authenticator mAuthenticator;
	
	@Override
	public void onCreate() {
		Log.i(TAG, "uid:" + Binder.getCallingUid());
		mAuthenticator = new Authenticator(this);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "onBind(), "+intent);
//		mHandler.sendEmptyMessageDelayed(0, 1000);
		return mAuthenticator.getIBinder();
	}
	
	//-------------------------------------------------
	// Instance fields
	private Account mAccount;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			mAccount = CreateSyncAccount(getApplicationContext());
		};
	};
	
	/**
	 * Create a new dummy account for the sync adapter
	 * 
	 * @param context
	 *            The application context
	 */
	public static Account CreateSyncAccount(Context context) {
		Log.i(TAG, "CreateSyncAccount(), uid:" + Binder.getCallingUid());
		
		// Create the account type and default account
		Account newAccount = new Account(AccountInfo.ACCOUNT, AccountInfo.ACCOUNT_TYPE);
		// Get an instance of the Android account manager
		AccountManager accountManager = (AccountManager) context
				.getSystemService(ACCOUNT_SERVICE);
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
