package com.example.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;

public class RandomReader {
	private RandomAccessFile raf;
	private Charset set = Charset.defaultCharset();
	
	public RandomReader(String path) throws FileNotFoundException {
		this(path, Charset.defaultCharset());
	}
	
	public RandomReader(String path, Charset set) throws FileNotFoundException {
		raf = new RandomAccessFile(path, "r");
		this.set = set;
	}
	
	public void close() {
		try {
			if (raf != null) raf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//----------------
	public void seek(long offset) throws IOException {
		raf.seek(offset);
	}
	
	public int skipBytes(int count) throws IOException {
		return raf.skipBytes(count);
	}

	public long length() throws IOException {
		return raf.length();
	}
	
	public int read() throws IOException {
		return raf.read();
	}

	public int read(byte[] buffer) throws IOException {
		return raf.read(buffer);
	}
	
	public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
		return raf.read(buffer, byteOffset, byteCount);
	}
	
	//------------------
	public byte[] read(int length) throws IOException {
		byte[] buf = new byte[length];
		int readlength = read(buf);
		if (readlength > 0) {
			return buf;
		}
		return null;
	}
	
	public int readInt() throws IOException {
		return EndianUtils.le.getInt(read(4), 0);
	}
	
	public short readShort() throws IOException {
		return (short) EndianUtils.le.getShort(read(2), 0);
	}
	
	public char readChar() throws IOException {
		return readChar(set);
	}
	
	public char readChar(Charset set) throws IOException {
		return new String(read(2), set).trim().charAt(0);
	}
	
	public String readString(int length) throws IOException {
		return readString(length, set);
	}
	
	public String readString(int length, Charset set)
			throws IOException {
		return new String(read(length), set).trim();
	}

	//---------------------
	public byte[] readData(long offset) throws IOException {
		byte[] intBuf = new byte[4];
		seek(offset);
		read(intBuf);
		int length = EndianUtils.le.getInt(intBuf, 0);
		
		return readData(offset+4, length);
	}
	
	public byte[] readData(long offset, int length) throws IOException {
		byte[] buf = new byte[length];
		seek(offset);
		if (read(buf) <= 0) {
			buf = null;
		}
		return buf;
	}
	
	//--------------------
	public int readInt(long offset) throws IOException  {
		seek(offset);
		return readInt();
	}
	
	public short readShort(long offset) throws IOException {
		seek(offset);
		return readShort();
	}
	
	public char readChar(long offset) throws IOException {
		seek(offset);
		return readChar(set);
	}
	
	public char readChar(long offset, Charset set) throws IOException {
		seek(offset);
		return new String(read(2), set).trim().charAt(0);
	}
	
	public String readString(long offset, int length)
			throws IOException {
		seek(offset);
		return readString(length, set);
	}
	
	public String readString(long offset, int length, Charset set)
			throws IOException {
		seek(offset);
		return new String(read(length), set).trim();
	}
	
	public String readDataString(long offset) 
			throws IOException {
		return readDataString(offset, set);
	}
	
	public String readDataString(long offset, Charset set) 
			throws IOException {
		int length = readInt(offset);
		return readString(offset+4, length, set);
	}
}
