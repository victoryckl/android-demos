package com.svo.laohan.model.dao.entity;

import java.io.Serializable;

public class FileEntity implements Serializable{
	private static final long serialVersionUID = -5587059749627418622L;
	private String _id;
	private String fs_id;
	private String path;
	private String parDir;
	private long mtime;
	private long size;
	private boolean isDir;
	private String type;
	private String suffix;
	private String localPath;
	private boolean isDown;
	private String fileName;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getFs_id() {
		return fs_id;
	}
	public void setFs_id(String fs_id) {
		this.fs_id = fs_id;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getParDir() {
		return parDir;
	}
	public void setParDir(String parDir) {
		this.parDir = parDir;
	}
	public long getMtime() {
		return mtime;
	}
	public void setMtime(long mtime) {
		this.mtime = mtime;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public boolean isDir() {
		return isDir;
	}
	public void setDir(boolean isDir) {
		this.isDir = isDir;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	public String getLocalPath() {
		return localPath;
	}
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
	public boolean isDown() {
		return isDown;
	}
	public void setDown(boolean isDown) {
		this.isDown = isDown;
	}
	
}
