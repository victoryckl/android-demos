package com.example.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;


public class BytesInputStream extends ByteArrayInputStream {
	private Charset set = Charset.defaultCharset();
	
	public BytesInputStream(byte[] buf) {
		super(buf);
	}
	
	public BytesInputStream(byte[] buf, int offset, int length) {
        super(buf, offset, length);
	}

	public BytesInputStream(byte[] buf, Charset set) {
		super(buf);
		this.set = set;
	}
	
	public BytesInputStream(byte[] buf, int offset, int length, Charset set) {
		super(buf, offset, length);
		this.set = set;
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
	
	public short readShort() throws IOException {
		return EndianUtils.le.getShort(read(2), 0);
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
	
	public String readString(int length, Charset set) throws IOException {
		return new String(read(length), set).trim();
	}
	
	public int getPostion() {
		return pos;
	}
	
	public void setPostion(int pos) {
		if (pos >= this.count) {
			pos = this.count-1;
		} else if (pos < 0) {
			pos = 0;
		}
		this.pos = pos;
	}
}
