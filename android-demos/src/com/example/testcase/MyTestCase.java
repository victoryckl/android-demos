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
