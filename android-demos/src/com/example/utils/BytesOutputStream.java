package com.example.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class BytesOutputStream extends ByteArrayOutputStream{
    public BytesOutputStream() {
        super();
    }

    public BytesOutputStream(int size) {
    	super(size);
    }
    
	public int writeInt(int i) throws IOException {
		byte[] buf = EndianUtils.le.fromInt(i);
		write(buf);
		return buf.length;
	}
	
	public int writeShort(int s) throws IOException {
		byte[] buf = EndianUtils.le.fromShort(s);
		write(buf);
		return buf.length;
	}
}
