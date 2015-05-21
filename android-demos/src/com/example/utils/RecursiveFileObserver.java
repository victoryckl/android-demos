package com.example.utils;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import android.os.FileObserver;
import android.text.TextUtils;
import android.util.Log;

public class RecursiveFileObserver extends FileObserver {

    private static final String TAG = "RecursiveFileObserver";
    private static final boolean DEBUG = true;

	public static int WATCH_MARK = ALL_EVENTS&(~(CLOSE_NOWRITE|ACCESS|ATTRIB|OPEN|MODIFY));
//    public static int WATCH_MARK = CREATE | DELETE | CLOSE_WRITE
//    		| DELETE_SELF | MOVE_SELF | MOVED_FROM | MOVED_TO;
    
    public static int DIR_MARK = 0x40000000; 

    Map<String, FileObserver> mObsMap;
    String mPath;
    int mMask;

    public RecursiveFileObserver(String path) {
        this(path, WATCH_MARK);
    }

    public RecursiveFileObserver(String path, int mask) {
        super(path, mask);
        mPath = path;
        mMask = mask;
    }

    @Override
    public void startWatching() {
        if (mObsMap != null)
            return;

        TimeStat ts = new TimeStat();
        
        mObsMap = new HashMap<String, FileObserver>();
        Stack<String> stack = new Stack<String> ();
        stack.push(mPath);

        while (!stack.isEmpty()) {
            String parent = (String)stack.pop();
            mObsMap.put(parent, new SingleFileObserver(parent, mMask));
            File path = new File(parent);
            File[] files = path.listFiles();
            if (null == files)
                continue;
            for (File f : files) {
                if (f.isDirectory() && !f.getName().equals(".")
                        && !f.getName().equals("..")) {
                    stack.push(f.getPath());
                }
            }
        }

        if (MapUtils.isEmpty(mObsMap))
        	return ;
        
        Collection<FileObserver> obs = mObsMap.values();
        Iterator<FileObserver> iter = obs.iterator();
        while (iter.hasNext()) {
        	iter.next().startWatching();
		}
        ts.updateTimeMillis("RecursiveFileObserver,startWatching()");
    }

    private boolean isStoping = false;
    @Override
    public void stopWatching() {
        if (MapUtils.isEmpty(mObsMap))
            return;

        isStoping = true;
        TimeStat ts = new TimeStat();
        
        Collection<FileObserver> obs = mObsMap.values();
        Iterator<FileObserver> iter = obs.iterator();
        while (iter.hasNext()) {
        	iter.next().stopWatching();
		}
        
        mObsMap.clear();
        mObsMap = null;
        ts.updateTimeMillis("RecursiveFileObserver,stopWatching()");
        isStoping = false;
    }
    
    @Override
    public void onEvent(int event, String path) {
    	if (MapUtils.isEmpty(mObsMap))
    		return ;
    	if ((DIR_MARK & event) != 0) {
    		onDirEvent(event, path);
    	} else {
    		onFileEvent(event, path);
    	}
    }
    
    public void onDirEvent(int event, String path) {
    	int dirEvent = event&(~DIR_MARK);
        switch (dirEvent) {
        case ACCESS:
            logi("DIR ACCESS: " + path);
            break;
        case ATTRIB:
            logi("DIR ATTRIB: " + path);
            break;
        case CLOSE_NOWRITE:
            logi("DIR CLOSE_NOWRITE: " + path);
            break;
        case CLOSE_WRITE:
            logi("DIR CLOSE_WRITE: " + path);
            break;
        case CREATE:
            logi("DIR CREATE: " + path);
            appendDirObserver(path);
            break;
        case DELETE:
            logi("DIR DELETE: " + path);
            break;
        case DELETE_SELF:
            logi("DIR DELETE_SELF: " + path);
            break;
        case MODIFY:
            logi("DIR MODIFY: " + path);
            break;
        case MOVE_SELF:
            logi("DIR MOVE_SELF: " + path);
            break;
        case MOVED_FROM:
            logi("DIR MOVED_FROM: " + path);
            break;
        case MOVED_TO:
            logi("DIR MOVED_TO: " + path);
            break;
        case OPEN:
            logi("DIR OPEN: " + path);
            break;
        default:
            logi("DIR DEFAULT(0x"+Integer.toHexString(event)+" => " + Integer.toHexString(dirEvent) + " : " + path);
            break;
        }
    }
    
    public void onFileEvent(int event, String path) {
        switch (event) {
        case ACCESS:
            logi("FILE ACCESS: " + path);
            break;
        case ATTRIB:
            logi("FILE ATTRIB: " + path);
            break;
        case CLOSE_NOWRITE:
            logi("FILE CLOSE_NOWRITE: " + path);
            break;
        case CLOSE_WRITE:
            logi("FILE CLOSE_WRITE: " + path);
            break;
        case CREATE:
            //logi("FILE CREATE: " + path);
            break;
        case DELETE:
            logi("FILE DELETE: " + path);
            break;
        case DELETE_SELF:
            logi("FILE DELETE_SELF: " + path);
            break;
        case MODIFY:
            logi("FILE MODIFY: " + path);
            break;
        case MOVE_SELF:
            logi("FILE MOVE_SELF: " + path);
            break;
        case MOVED_FROM:
            logi("FILE MOVED_FROM: " + path);
            break;
        case MOVED_TO:
            logi("FILE MOVED_TO: " + path);
            break;
        case OPEN:
            logi("FILE OPEN: " + path);
            break;
        case 0x8000://停止监听
        	if (!isStoping) {
	        	mObsMap.remove(path);
	        	logi("FILE map remove: "+path);
        	}
        	break;
        default:
            logi("FILE DEFAULT(0x" + Integer.toHexString(event) + " : " + path);
            break;
        }
    }
    
    private void appendDirObserver(String path) {
    	if (MapUtils.isEmpty(mObsMap))
    		return ;
    	if (mObsMap.containsKey(path))
    		return ;
    	
    	FileObserver fob = new SingleFileObserver(path, mMask);
    	fob.startWatching();
    	mObsMap.put(path, fob);
    	logi("start watching: "+ path);
    }
    
    /**
     * Monitor single directory and dispatch all events to its parent, with full
     * path.
     */
    class SingleFileObserver extends FileObserver {
        String mPath;

        public SingleFileObserver(String path) {
            this(path, WATCH_MARK);
            mPath = path;
        }

        public SingleFileObserver(String path, int mask) {
            super(path, mask);
            mPath = path;
        }

        @Override
        public void onEvent(int event, String path) {
        	String newPath = mPath;
        	if (!TextUtils.isEmpty(path)) {
        		newPath += "/"+path;
        	}
            RecursiveFileObserver.this.onEvent(event, newPath);
        }
    }
    
    private void logi(String msg) {
    	if (DEBUG) {
    		Log.i(TAG, msg);
    	}
    }
    
    private void loge(String msg) {
    	Log.e(TAG, msg);
    }
}
