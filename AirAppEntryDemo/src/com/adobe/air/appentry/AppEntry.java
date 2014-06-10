package com.adobe.air.appentry;

import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.Date;

import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.adobe.air.ResourceIdMap;

import dalvik.system.DexClassLoader;

public class AppEntry extends Activity {
	private static final String LOG_TAG = "AppEntry";
	private static boolean sRuntimeClassesLoaded = false;
	private static DexClassLoader sDloader;
	private static Class<?> sAndroidActivityWrapperClass;
	private static Object sAndroidActivityWrapper = null;
	private static String RUNTIME_PACKAGE_ID = "com.adobe.air";
	private static final String RESOURCE_CLASS = "air.com.adobe.appentry.R";
	private static final String RESOURCE_TEXT_RUNTIME_REQUIRED = "string.text_runtime_required";
	private static final String RESOURCE_TITLE_ADOBE_AIR = "string.title_adobe_air";
	private static final String RESOURCE_BUTTON_INSTALL = "string.button_install";
	private static final String RESOURCE_BUTTON_EXIT = "string.button_exit";

	private void BroadcastIntent(String action, String data) {
		try {
			startActivity(Intent.parseUri(data, 0).setAction(action)
					.addFlags(268435456));
		} catch (URISyntaxException err) {
		} catch (ActivityNotFoundException err) {
		}
	}

	private void launchMarketPlaceForAIR() {
		String airDownloadURL = null;
		try {
			ActivityInfo info = getPackageManager().getActivityInfo(
					getComponentName(), 128);
			Bundle metadata = info.metaData;

			if (metadata != null) {
				airDownloadURL = (String) metadata.get("airDownloadURL");
			}

		} catch (PackageManager.NameNotFoundException e) {
		}

		String marketPlaceURL = airDownloadURL;
		if (marketPlaceURL == null) {
			marketPlaceURL = "market://details?id=" + RUNTIME_PACKAGE_ID;
		}
		try {
			BroadcastIntent("android.intent.action.VIEW", marketPlaceURL);
		} catch (Exception e) {
		}
	}

	private boolean isRuntimeInstalled() {
		PackageManager pkgMgr = getPackageManager();
		try {
			pkgMgr.getPackageInfo(RUNTIME_PACKAGE_ID, 256);
		} catch (PackageManager.NameNotFoundException nfe) {
			return false;
		}

		return true;
	}

	private boolean isRuntimeOnExternalStorage() {
		PackageManager pkgMgr = getPackageManager();
		try {
			ApplicationInfo appInfo = pkgMgr.getApplicationInfo(
					RUNTIME_PACKAGE_ID, 8192);
			if ((appInfo.flags & 0x40000) == 262144) {
				return true;
			}
		} catch (PackageManager.NameNotFoundException nfe) {
		}

		return false;
	}

	private void showDialog(int titleId, String text, int positiveButtonId,
			int negativeButtonId) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle(titleId);
		alertDialogBuilder.setMessage(text);
		alertDialogBuilder.setPositiveButton(positiveButtonId,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						AppEntry.this.launchMarketPlaceForAIR();
						System.exit(0);
					}
				});
		alertDialogBuilder.setNegativeButton(negativeButtonId,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						System.exit(0);
					}
				});
		alertDialogBuilder
				.setOnCancelListener(new DialogInterface.OnCancelListener() {
					public void onCancel(DialogInterface dialog) {
						System.exit(0);
					}
				});
		alertDialogBuilder.show();
	}

	private void showRuntimeNotInstalledDialog() {
		ResourceIdMap r = new ResourceIdMap("air.com.adobe.appentry.R");
		String text = getString(r.getId("string.text_runtime_required"))
				+ getString(r.getId("string.text_install_runtime"));
		showDialog(r.getId("string.title_adobe_air"), text,
				r.getId("string.button_install"), r.getId("string.button_exit"));
	}

	private void showRuntimeOnExternalStorageDialog() {
		ResourceIdMap r = new ResourceIdMap("air.com.adobe.appentry.R");
		String text = getString(r.getId("string.text_runtime_required"))
				+ getString(r.getId("string.text_runtime_on_external_storage"));
		showDialog(r.getId("string.title_adobe_air"), text,
				r.getId("string.button_install"), r.getId("string.button_exit"));
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Date t = new Date();
		long millis = t.getTime();
		Log.i("StartupTime1", ":" + millis);

		boolean hasCaptiveRuntime = loadCaptiveRuntimeClasses();
		if (!hasCaptiveRuntime) {
			if ((!sRuntimeClassesLoaded) && (!isRuntimeInstalled())) {
				if (isRuntimeOnExternalStorage())
					showRuntimeOnExternalStorageDialog();
				else
					showRuntimeNotInstalledDialog();
				return;
			}
			loadSharedRuntimeDex();
		}

		if (sRuntimeClassesLoaded) {
			createActivityWrapper(hasCaptiveRuntime);

			InvokeWrapperOnCreate();
		} else if (hasCaptiveRuntime) {
			KillSelf();
		} else {
			launchAIRService();
		}
	}

	private void launchAIRService() {
		boolean connected;
		try {
			Intent intent = new Intent("com.adobe.air.AIRServiceAction");
			intent.setClassName(RUNTIME_PACKAGE_ID, "com.adobe.air.AIRService");

			ServiceConnection conn = new ServiceConnection() {
				public void onServiceConnected(ComponentName name,
						IBinder service) {
					AppEntry.this.unbindService(this);

					AppEntry.this.loadSharedRuntimeDex();
					AppEntry.this.createActivityWrapper(false);

					if (AppEntry.sRuntimeClassesLoaded) {
						AppEntry.this.InvokeWrapperOnCreate();
					} else {
						//TODO: call which method ?
						//AppEntry.access$500();
						showRuntimeNotInstalledDialog();
					}
				}

				public void onServiceDisconnected(ComponentName name) {
				}
			};
			connected = bindService(intent, conn, 1);
		} catch (Exception e) {
		}
	}

	private void InvokeWrapperOnCreate() {
		try {
			Method method = sAndroidActivityWrapperClass.getMethod("onCreate",
					new Class[] { Activity.class, String.class });

			String xmlPath = "";
			String rootDirectory = "";
			String extraArgs = "-nodebug";
			Boolean isADL = new Boolean(false);
			Boolean isDebuggerMode = new Boolean(false);
			String[] args = { xmlPath, rootDirectory, extraArgs,
					isADL.toString(), isDebuggerMode.toString() };

			InvokeMethod(method, new Object[] { this, args });
		} catch (Exception e) {
		}
	}

	private Object InvokeMethod(Method method, Object[] args) {
		if (!sRuntimeClassesLoaded) {
			return null;
		}
		Object retval = null;
		try {
			if (args != null)
				retval = method.invoke(sAndroidActivityWrapper, args);
			else {
				retval = method.invoke(sAndroidActivityWrapper, new Object[0]);
			}
		} catch (Exception e) {
		}
		return retval;
	}

	private static void KillSelf() {
		Process.killProcess(Process.myPid());
	}

	public void onStart() {
		super.onStart();
	}

	public void onRestart() {
		super.onRestart();
		try {
			if (sRuntimeClassesLoaded) {
				Method method = sAndroidActivityWrapperClass.getMethod(
						"onRestart", new Class[0]);
				InvokeMethod(method, new Object[0]);
			}
		} catch (Exception e) {
		}
	}

	public void onPause() {
		super.onPause();
		try {
			if (sRuntimeClassesLoaded) {
				Method method = sAndroidActivityWrapperClass.getMethod(
						"onPause", new Class[0]);
				InvokeMethod(method, new Object[0]);
			}
		} catch (Exception e) {
		}
	}

	public void onResume() {
		super.onResume();
		try {
			if (sRuntimeClassesLoaded) {
				Method method = sAndroidActivityWrapperClass.getMethod(
						"onResume", new Class[0]);
				InvokeMethod(method, new Object[0]);
			}
		} catch (Exception e) {
		}
	}

	public void onStop() {
		super.onStop();
		try {
			Method method = sAndroidActivityWrapperClass.getMethod("onStop",
					new Class[0]);
			InvokeMethod(method, new Object[0]);
		} catch (Exception e) {
		}
	}

	public void onDestroy() {
		super.onDestroy();
		try {
			Method method = sAndroidActivityWrapperClass.getMethod("onDestroy",
					new Class[0]);
			InvokeMethod(method, new Object[0]);
		} catch (Exception e) {
		}
	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onConfigurationChanged",
					new Class[] { Configuration.class });
			InvokeMethod(method, new Object[] { newConfig });
		} catch (Exception e) {
		}
	}

	public void onLowMemory() {
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onLowMemory", new Class[0]);
			InvokeMethod(method, new Object[0]);
		} catch (Exception e) {
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (sRuntimeClassesLoaded) {
				Method method = sAndroidActivityWrapperClass.getMethod(
						"onActivityResult", new Class[] { Integer.TYPE,
								Integer.TYPE, Intent.class });
				InvokeMethod(
						method,
						new Object[] { Integer.valueOf(requestCode),
								Integer.valueOf(resultCode), data });
			}
		} catch (Exception e) {
		}
	}

	protected void onNewIntent(Intent aIntent) {
		Intent ii = aIntent;
		super.onNewIntent(ii);
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onNewIntent", new Class[] { Intent.class });
			InvokeMethod(method, new Object[] { ii });
		} catch (Exception e) {
		}
	}

	private boolean loadCaptiveRuntimeClasses() {
		boolean hasCaptiveRuntime = false;
		try {
			sAndroidActivityWrapperClass = Class
					.forName("com.adobe.air.AndroidActivityWrapper");
			hasCaptiveRuntime = true;
			if (sAndroidActivityWrapperClass != null)
				sRuntimeClassesLoaded = true;
		} catch (Exception e) {
		}

		return hasCaptiveRuntime;
	}

	private void loadSharedRuntimeDex() {
		try {
			if (!sRuntimeClassesLoaded) {
				Context con = createPackageContext(RUNTIME_PACKAGE_ID, 3);
				sDloader = new DexClassLoader(RUNTIME_PACKAGE_ID, getFilesDir()
						.getAbsolutePath(), null, con.getClassLoader());

				sAndroidActivityWrapperClass = sDloader
						.loadClass("com.adobe.air.AndroidActivityWrapper");
				if (sAndroidActivityWrapperClass != null)
					sRuntimeClassesLoaded = true;
			}
		} catch (Exception e) {
		}
	}

	private void createActivityWrapper(boolean hasCaptiveRuntime) {
		try {
			if (hasCaptiveRuntime) {
				Method methodCreateAndroidActivityWrapper = sAndroidActivityWrapperClass
						.getMethod("CreateAndroidActivityWrapper", 
								new Class[] {Activity.class, Boolean.class });
				sAndroidActivityWrapper = methodCreateAndroidActivityWrapper
						.invoke(null, new Object[] { this, Boolean.valueOf(hasCaptiveRuntime) });
			} else {
				Method methodCreateAndroidActivityWrapper = sAndroidActivityWrapperClass
						.getMethod("CreateAndroidActivityWrapper", new Class[] { Activity.class });
				sAndroidActivityWrapper = methodCreateAndroidActivityWrapper
						.invoke(null, new Object[] { this });
			}
		} catch (Exception e) {
		}
	}

	public void finishActivityFromChild(Activity child, int requestCode) {
		super.finishActivityFromChild(child, requestCode);
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"finishActivityFromChild", new Class[] { Activity.class,
							Integer.TYPE });
			InvokeMethod(method,
					new Object[] { child, Integer.valueOf(requestCode) });
		} catch (Exception e) {
		}
	}

	public void finishFromChild(Activity child) {
		super.finishFromChild(child);
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"finishFromChild", new Class[] { Activity.class });
			InvokeMethod(method, new Object[] { child });
		} catch (Exception e) {
		}
	}

	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onAttachedToWindow", new Class[0]);
			InvokeMethod(method, new Object[0]);
		} catch (Exception e) {
		}
	}

	public void onBackPressed() {
		super.onBackPressed();
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onBackPressed", new Class[0]);
			InvokeMethod(method, new Object[0]);
		} catch (Exception e) {
		}
	}

	public void onContentChanged() {
		super.onContentChanged();
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onContentChanged", new Class[0]);
			InvokeMethod(method, new Object[0]);
		} catch (Exception e) {
		}
	}

	public boolean onContextItemSelected(MenuItem item) {
		boolean retval = super.onContextItemSelected(item);
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onContextItemSelected", new Class[] { MenuItem.class,
							Boolean.TYPE });
			return ((Boolean) InvokeMethod(method,
					new Object[] { item, Boolean.valueOf(retval) }))
					.booleanValue();
		} catch (Exception e) {
		}
		return retval;
	}

	public void onContextMenuClosed(Menu menu) {
		super.onContextMenuClosed(menu);
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onContextMenuClosed", new Class[] { Menu.class });
			InvokeMethod(method, new Object[] { menu });
		} catch (Exception e) {
		}
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onCreateContextMenu", new Class[] { ContextMenu.class,
							View.class, ContextMenu.ContextMenuInfo.class });
			InvokeMethod(method, new Object[] { menu, v, menuInfo });
		} catch (Exception e) {
		}
	}

	public CharSequence onCreateDescription() {
		CharSequence retval = super.onCreateDescription();
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onCreateDescription", new Class[] { CharSequence.class });
			return (CharSequence) InvokeMethod(method, new Object[] { retval });
		} catch (Exception e) {
		}
		return retval;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		boolean retval = super.onCreateOptionsMenu(menu);
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onCreateOptionsMenu", new Class[] { Menu.class,
							Boolean.TYPE });
			return ((Boolean) InvokeMethod(method,
					new Object[] { menu, Boolean.valueOf(retval) }))
					.booleanValue();
		} catch (Exception e) {
		}
		return retval;
	}

	public boolean onCreatePanelMenu(int featureId, Menu menu) {
		boolean retval = super.onCreatePanelMenu(featureId, menu);
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onCreatePanelMenu", new Class[] { Integer.TYPE,
							Menu.class, Boolean.TYPE });
			return ((Boolean) InvokeMethod(
					method,
					new Object[] { Integer.valueOf(featureId), menu,
							Boolean.valueOf(retval) })).booleanValue();
		} catch (Exception e) {
		}
		return retval;
	}

	public View onCreatePanelView(int featureId) {
		View retval = super.onCreatePanelView(featureId);
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onCreatePanelView",
					new Class[] { Integer.TYPE, View.class });
			return (View) InvokeMethod(method,
					new Object[] { Integer.valueOf(featureId), retval });
		} catch (Exception e) {
		}
		return retval;
	}

	public boolean onCreateThumbnail(Bitmap outBitmap, Canvas canvas) {
		boolean retval = super.onCreateThumbnail(outBitmap, canvas);
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onCreateThumbnail", new Class[] { Bitmap.class,
							Canvas.class, Boolean.TYPE });
			return ((Boolean) InvokeMethod(method, new Object[] { outBitmap,
					canvas, Boolean.valueOf(retval) })).booleanValue();
		} catch (Exception e) {
		}
		return retval;
	}

	public View onCreateView(String name, Context context, AttributeSet attrs) {
		View retval = super.onCreateView(name, context, attrs);
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onCreateView", new Class[] { String.class, Context.class,
							AttributeSet.class, View.class });
			return (View) InvokeMethod(method, new Object[] { name, context,
					attrs, retval });
		} catch (Exception e) {
		}
		return retval;
	}

	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onDetachedFromWindow", new Class[0]);
			InvokeMethod(method, new Object[0]);
		} catch (Exception e) {
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean retval = super.onKeyDown(keyCode, event);
		try {
			Method method = sAndroidActivityWrapperClass.getMethod("onKeyDown",
					new Class[] { Integer.TYPE, KeyEvent.class, Boolean.TYPE });
			return ((Boolean) InvokeMethod(
					method,
					new Object[] { Integer.valueOf(keyCode), event,
							Boolean.valueOf(retval) })).booleanValue();
		} catch (Exception e) {
		}
		return retval;
	}

	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		boolean retval = super.onKeyLongPress(keyCode, event);
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onKeyLongPress", new Class[] { Integer.TYPE,
							KeyEvent.class, Boolean.TYPE });
			return ((Boolean) InvokeMethod(
					method,
					new Object[] { Integer.valueOf(keyCode), event,
							Boolean.valueOf(retval) })).booleanValue();
		} catch (Exception e) {
		}
		return retval;
	}

	public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
		boolean retval = super.onKeyMultiple(keyCode, repeatCount, event);
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onKeyMultiple", new Class[] { Integer.TYPE, Integer.TYPE,
							KeyEvent.class, Boolean.TYPE });
			return ((Boolean) InvokeMethod(
					method,
					new Object[] { Integer.valueOf(keyCode),
							Integer.valueOf(repeatCount), event,
							Boolean.valueOf(retval) })).booleanValue();
		} catch (Exception e) {
		}
		return retval;
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		boolean retval = super.onKeyUp(keyCode, event);
		try {
			Method method = sAndroidActivityWrapperClass.getMethod("onKeyUp",
					new Class[] { Integer.TYPE, KeyEvent.class, Boolean.TYPE });
			return ((Boolean) InvokeMethod(
					method,
					new Object[] { Integer.valueOf(keyCode), event,
							Boolean.valueOf(retval) })).booleanValue();
		} catch (Exception e) {
		}
		return retval;
	}

	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		boolean retval = super.onMenuItemSelected(featureId, item);
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onMenuItemSelected", new Class[] { Integer.TYPE,
							MenuItem.class, Boolean.TYPE });
			return ((Boolean) InvokeMethod(
					method,
					new Object[] { Integer.valueOf(featureId), item,
							Boolean.valueOf(retval) })).booleanValue();
		} catch (Exception e) {
		}
		return retval;
	}

	public boolean onMenuOpened(int featureId, Menu menu) {
		boolean retval = super.onMenuOpened(featureId, menu);
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onMenuOpened", new Class[] { Integer.TYPE, Menu.class,
							Boolean.TYPE });
			return ((Boolean) InvokeMethod(
					method,
					new Object[] { Integer.valueOf(featureId), menu,
							Boolean.valueOf(retval) })).booleanValue();
		} catch (Exception e) {
		}
		return retval;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		boolean retval = super.onOptionsItemSelected(item);
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onOptionsItemSelected", new Class[] { MenuItem.class,
							Boolean.TYPE });
			return ((Boolean) InvokeMethod(method,
					new Object[] { item, Boolean.valueOf(retval) }))
					.booleanValue();
		} catch (Exception e) {
		}
		return retval;
	}

	public void onOptionsMenuClosed(Menu menu) {
		super.onOptionsMenuClosed(menu);
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onOptionsMenuClosed", new Class[] { Menu.class });
			InvokeMethod(method, new Object[] { menu });
		} catch (Exception e) {
		}
	}

	public void onPanelClosed(int featureId, Menu menu) {
		super.onPanelClosed(featureId, menu);
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onPanelClosed", new Class[] { Integer.TYPE, Menu.class });
			InvokeMethod(method, new Object[] { Integer.valueOf(featureId),
					menu });
		} catch (Exception e) {
		}
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean retval = super.onPrepareOptionsMenu(menu);
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onPrepareOptionsMenu", new Class[] { Menu.class,
							Boolean.TYPE });
			return ((Boolean) InvokeMethod(method,
					new Object[] { menu, Boolean.valueOf(retval) }))
					.booleanValue();
		} catch (Exception e) {
		}
		return retval;
	}

	public boolean onPreparePanel(int featureId, View view, Menu menu) {
		boolean retval = super.onPreparePanel(featureId, view, menu);
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onPreparePanel", new Class[] { Integer.TYPE, View.class,
							Menu.class, Boolean.TYPE });
			return ((Boolean) InvokeMethod(method,
					new Object[] { Integer.valueOf(featureId), view, menu,
							Boolean.valueOf(retval) })).booleanValue();
		} catch (Exception e) {
		}
		return retval;
	}

	public Object onRetainNonConfigurationInstance() {
		Object retval = super.onRetainNonConfigurationInstance();
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onRetainNonConfigurationInstance",
					new Class[] { Object.class });
			return InvokeMethod(method, new Object[] { retval });
		} catch (Exception e) {
		}
		return retval;
	}

	public boolean onSearchRequested() {
		boolean retval = super.onSearchRequested();
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onSearchRequested", new Class[] { Boolean.TYPE });
			return ((Boolean) InvokeMethod(method,
					new Object[] { Boolean.valueOf(retval) })).booleanValue();
		} catch (Exception e) {
		}
		return retval;
	}

	public boolean onTouchEvent(MotionEvent event) {
		boolean retval = super.onTouchEvent(event);
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onTouchEvent", new Class[] { MotionEvent.class,
							Boolean.TYPE });
			return ((Boolean) InvokeMethod(method, new Object[] { event,
					Boolean.valueOf(retval) })).booleanValue();
		} catch (Exception e) {
		}
		return retval;
	}

	public boolean onTrackballEvent(MotionEvent event) {
		boolean retval = super.onTrackballEvent(event);
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onTrackballEvent", new Class[] { MotionEvent.class,
							Boolean.TYPE });
			return ((Boolean) InvokeMethod(method, new Object[] { event,
					Boolean.valueOf(retval) })).booleanValue();
		} catch (Exception e) {
		}

		return retval;
	}

	public void onUserInteraction() {
		super.onUserInteraction();
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onUserInteraction", new Class[0]);
			InvokeMethod(method, new Object[0]);
		} catch (Exception e) {
		}
	}

	public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
		super.onWindowAttributesChanged(params);
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onWindowAttributesChanged",
					new Class[] { WindowManager.LayoutParams.class });
			InvokeMethod(method, new Object[] { params });
		} catch (Exception e) {
		}
	}

	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onWindowFocusChanged", new Class[] { Boolean.TYPE });
			InvokeMethod(method, new Object[] { Boolean.valueOf(hasFocus) });
		} catch (Exception e) {
		}
	}

	protected void onApplyThemeResource(Resources.Theme theme, int resid,
			boolean first) {
		super.onApplyThemeResource(theme, resid, first);
		try {
			Method method = sAndroidActivityWrapperClass
					.getMethod("onApplyThemeResource", new Class[] {
							Resources.Theme.class, Integer.TYPE, Boolean.TYPE });
			InvokeMethod(method, new Object[] { theme, Integer.valueOf(resid),
					Boolean.valueOf(first) });
		} catch (Exception e) {
		}
	}

	protected void onChildTitleChanged(Activity childActivity,
			CharSequence title) {
		super.onChildTitleChanged(childActivity, title);
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onChildTitleChanged", new Class[] { Activity.class,
							CharSequence.class });
			InvokeMethod(method, new Object[] { childActivity, title });
		} catch (Exception e) {
		}
	}

	protected Dialog onCreateDialog(int id) {
		Dialog retval = super.onCreateDialog(id);
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onCreateDialog",
					new Class[] { Integer.TYPE, Dialog.class });
			return (Dialog) InvokeMethod(method,
					new Object[] { Integer.valueOf(id), retval });
		} catch (Exception e) {
		}
		return retval;
	}

	protected Dialog onCreateDialog(int id, Bundle args) {
		Dialog retval = super.onCreateDialog(id, args);
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onCreateDialog", new Class[] { Integer.TYPE, Bundle.class,
							Dialog.class });
			return (Dialog) InvokeMethod(method,
					new Object[] { Integer.valueOf(id), args, retval });
		} catch (Exception e) {
		}
		return retval;
	}

	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onPostCreate", new Class[] { Bundle.class });
			InvokeMethod(method, new Object[] { savedInstanceState });
		} catch (Exception e) {
		}
	}

	protected void onPostResume() {
		super.onPostResume();
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onPostResume", new Class[0]);
			InvokeMethod(method, new Object[0]);
		} catch (Exception e) {
		}
	}

	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		try {
			Method method = sAndroidActivityWrapperClass
					.getMethod("onPrepareDialog", new Class[] { R.id.class,
							Dialog.class });
			InvokeMethod(method, new Object[] { Integer.valueOf(id), dialog });
		} catch (Exception e) {
		}
	}

	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		super.onPrepareDialog(id, dialog, args);
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onPrepareDialog", new Class[] { R.id.class, Dialog.class,
							Bundle.class });
			InvokeMethod(method, new Object[] { Integer.valueOf(id), dialog,
					args });
		} catch (Exception e) {
		}
	}

	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onRestoreInstanceState", new Class[] { Bundle.class });
			InvokeMethod(method, new Object[] { savedInstanceState });
		} catch (Exception e) {
		}
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onSaveInstanceState", new Class[] { Bundle.class });
			InvokeMethod(method, new Object[] { outState });
		} catch (Exception e) {
		}
	}

	protected void onTitleChanged(CharSequence title, int color) {
		super.onTitleChanged(title, color);
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onTitleChanged", new Class[] { CharSequence.class,
							Integer.TYPE });
			InvokeMethod(method, new Object[] { title, Integer.valueOf(color) });
		} catch (Exception e) {
		}
	}

	protected void onUserLeaveHint() {
		super.onUserLeaveHint();
		try {
			Method method = sAndroidActivityWrapperClass.getMethod(
					"onUserLeaveHint", new Class[0]);
			InvokeMethod(method, new Object[0]);
		} catch (Exception e) {
		}
	}
}