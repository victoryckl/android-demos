package com.example.accounttest;

import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.example.accountdemo.Constants;

public class AccountTestActivity extends Activity {
	private static final String TAG = AccountTestActivity.class.getSimpleName();

	private AccountManager mAccountManager;
	private Account[] mAccounts;
	
	private TextView mTvInfo, mTvStatus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_test);
		
		init();
	}

	private void init() {
		mTvInfo = (TextView) findViewById(R.id.tv_info);
		mTvStatus = (TextView) findViewById(R.id.tv_status);
		
		findViewById(R.id.btn_login).setOnClickListener(mClickListener);
		
		mAccountManager = AccountManager.get(getApplicationContext());
		Account[] accounts = mAccountManager.getAccounts();
		if (accounts == null || accounts.length <= 0) {
			mTvInfo.setText("no account");
		} else {
			StringBuffer sb = new StringBuffer();
			for (Account account:accounts) {
				sb.append(account.name+'-'+account.type+'\n');
			}
			mTvInfo.setText(sb.toString());
		}
	}
	
	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_login:
				login();
				break;
			default:
				break;
			}
		}
	};
	
	private void login() {
		Bundle options = new Bundle();
		mAccounts = mAccountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
		
		Account account = null;
		
		if (mAccounts == null || mAccounts.length <= 0) {
			account = new Account("name", Constants.ACCOUNT_TYPE);
		} else {
			account = mAccounts[0];
		}
		mAccountManager.getAuthToken(
			account,                        // Account retrieved using getAccountsByType()
			Constants.AUTHTOKEN_TYPE,       // Auth scope
			options,                        // Authenticator-specific options
			this,                           // Your activity
			new OnTokenAcquired(),          // Callback called when a token is successfully acquired
			null);    // Callback called if an error occurs
	}

	private class OnTokenAcquired implements AccountManagerCallback<Bundle> {
	    @Override
	    public void run(AccountManagerFuture<Bundle> result) {
	        // Get the result of the operation from the AccountManagerFuture.
	        Bundle bundle;
			try {
				bundle = result.getResult();
				
				// The token is a named value in the bundle. The name of the value
				// is stored in the constant AccountManager.KEY_AUTHTOKEN.
				String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
				mTvStatus.setText(bundle.toString());
				if (TextUtils.isEmpty(token)) {
					Intent launch = (Intent) bundle.get(AccountManager.KEY_INTENT);
					if (launch != null) {
						startActivityForResult(launch, 0);
					}
				}
			} catch (OperationCanceledException e) {
				e.printStackTrace();
			} catch (AuthenticatorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "request:"+requestCode+", result:"+resultCode+", "+data.toString());
	}
}
