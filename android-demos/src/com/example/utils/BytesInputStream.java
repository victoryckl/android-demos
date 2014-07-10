package com.example.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;


public class BytesInputStream extends ByteArrayInputStream{
	public BytesInputStream(byte[] buf) {
		super(buf);
	}
	
	public BytesInputStream(byte[] buf, int offset, int length) {
        super(buf, offset, length);
	}

	public byte[] read(int length) throws IOException {
		byte[] buf = new byte[length];
		// Returns the number of bytes actually read or -1 if the end of the stream has been reached.
		int readlength = read(buf);
		if (readlength > 0) {
			return buf;
		}
		return null;
	}
	
	public int readInt() throws IOException {
		return EndianUtils.le.getInt(read(4), 0);
	}
	
	public int readShort() throws IOException {
		return EndianUtils.le.getShort(read(2), 0);
	}
	
	public int getPostion() {
		return pos;
	}
	
	public void setPostion(int pos) {
		this.pos = pos;
	}
}
