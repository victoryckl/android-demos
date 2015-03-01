package com.example.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;


public class BytesOutputStream extends ByteArrayOutputStream{
	private Charset set = Charset.defaultCharset();
	
    public BytesOutputStream() {
        super();
    }

    public BytesOutputStream(int size) {
    	super(size);
    }
    
    public BytesOutputStream(Charset set) {
    	super();
    	this.set = set;
    }
    
    public BytesOutputStream(int size, Charset set) {
    	super(size);
    	this.set = set;
    }
    
	public int writeInt(int value) throws IOException {
		byte[] buf = EndianUtils.le.fromInt(value);
		write(buf);
		return buf.length;
	}
	
	public int writeShort(short value) throws IOException {
		byte[] buf = EndianUtils.le.fromShort(value);
		write(buf);
		return buf.length;
	}
	
	public int writeChar(char value) throws IOException {
		return writeChar(value, set);
	}
	
	public int writeChar(char value, Charset set) throws IOException {
		String str = String.valueOf(value+" ");
		byte[] buf = str.getBytes(set);
		write(buf, 0, 2);
		return 2;
	}
	
	public int writeString(String str) throws IOException {
		return writeString(str, set);
	}
	
	public int writeString(String str, Charset set) throws IOException {
		byte[] buf = str.getBytes(set);
		write(buf);
		return buf.length;
	}
}
