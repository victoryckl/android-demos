package com.example.testcase;

import java.io.File;

import android.os.Environment;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.utils.FileUtils;

public class MyTestCase extends AndroidTestCase {
	private static final String TAG = "MyTestCase";

	private File createFile(File file) {
		String c = ""+System.currentTimeMillis()+"\n";
		FileUtils.writeFile(file, c.getBytes());
		return file;
	}
	
	private void testRename(File f1, File f2) {
		if (f1.renameTo(f2)) {
			Log.i(TAG, "rename ok:"+f1.getAbsolutePath()+" => " +f2.getAbsolutePath());
		} else {
			Log.i(TAG, "rename error:"+f1.getAbsolutePath()+" => " +f2.getAbsolutePath());
		}
	}
	
	public void testRename() {
		String path3 = "/sdcard/.cybertron/res/0/0/0/0/__res__53414e074e2a4e3a4ec04e484e4b4e0d89814e7154034e1c897f.fmv";
		String path2 = "/sdcard/教材/其他/其他啊/什么啊.fmv";
		String path1 = "/sdcard/test/11.fmv";

		File f = new File("/sdcard/22");
		String path4 = getContext().getCacheDir()+"/22.key";
		
		String data = getContext().getFilesDir().getAbsolutePath();
		String sd1 = Environment.getExternalStorageDirectory().getAbsolutePath();
		String sd2 = "/storage/external_storage/sdcard1";
		
		File f1 = createFile(new File(data, "data-1.txt"));
		File f2 = new File(data, "data-2.txt");
		testRename(f1, f2);
		
		f1 = createFile(new File(sd1, "sd1-1.txt"));
		f2 = new File(sd1, "sd1-2.txt");
		testRename(f1, f2);
		
		f1 = createFile(new File(sd2, "sd2-1.txt"));
		f2 = new File(sd2, "sd2-2.txt");
		testRename(f1, f2);
		
		f1 = createFile(new File(sd1, "sd1-1.txt"));
		f2 = new File(sd2, "sd2-3.txt");
		testRename(f1, f2);
		
		f1 = createFile(new File(sd1, "sd1-1.txt"));
		f2 = new File(data, "data-3.txt");
		testRename(f1, f2);
	}
}
