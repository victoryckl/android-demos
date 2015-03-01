package com.example.utils;

import java.nio.ByteOrder;
import java.nio.charset.Charset;

/**<pre>
 Little-endian
 Big-endian
 </pre>*/
public class EndianUtils {
	private EndianUtils() {}
	
	/**<pre> 
	 将按小端顺序排列的字节数组转为相应的类型数据
	Little-endian -> Big-endian   
	 </pre>*/
	
	public static class le {
		/**
		 * 将按小端顺序排列的字节数组转为一个int数据   
		 * @param array 按小端顺序排列的字节数组   
		 * @return int数据  
		 */
		public static int getInt(byte[] array, int start) {
			return peekInt(array, start, ByteOrder.LITTLE_ENDIAN);
		}
		
	    public static short getShort(byte[] array, int start) {
	    	return peekShort(array, start, ByteOrder.LITTLE_ENDIAN);
	    }
	    
	    public static byte getByte(byte[] array, int start) {
	    	byte b = (byte)(array[start] & 0xff);
	        return b;
	    }
	    
	    public static byte[] fromInt(int value) {
	    	return pokeInt(value, ByteOrder.LITTLE_ENDIAN);
	    }
	    
	    public static byte[] fromShort(short value) {
	    	return pokeShort(value, ByteOrder.LITTLE_ENDIAN);
	    }
	    
	    /**<pre>
		unicode -> charset(gbk...)
		Big-endian -> Little-endian
	    </pre>*/	    
	    public static byte[] fromChar(int c, Charset set) {
	    	// must add space char, or getBytes() will return 1 byte
	    	byte[] t = String.valueOf((char)c+" ").getBytes(set);
	    	return new byte[]{t[1], t[0]};
	    }

	    public static int charToShort(int c, Charset set) {
	    	return getShort(fromChar(c, set), 0);
	    }
	    
	    public static char getChar(byte[] array, int start, Charset set) {
	    	// char data: Little-endian -> Big-endian
	    	byte[] buf = new byte[]{array[1], array[0]};
	    	return new String(buf, 0, 2, set).charAt(0);
	    }
	}
	
	public static class be {
	    public static short getShort(byte[] array, int start) {
	    	return peekShort(array, start, ByteOrder.BIG_ENDIAN);
	    }
	}
	
	//------------------
    public static int peekInt(byte[] src, int offset, ByteOrder order) {
        if (order == ByteOrder.BIG_ENDIAN) {
            return (((src[offset++] & 0xff) << 24) |
                    ((src[offset++] & 0xff) << 16) |
                    ((src[offset++] & 0xff) <<  8) |
                    ((src[offset  ] & 0xff) <<  0));
        } else {
            return (((src[offset++] & 0xff) <<  0) |
                    ((src[offset++] & 0xff) <<  8) |
                    ((src[offset++] & 0xff) << 16) |
                    ((src[offset  ] & 0xff) << 24));
        }
    }

    public static long peekLong(byte[] src, int offset, ByteOrder order) {
        if (order == ByteOrder.BIG_ENDIAN) {
            int h = ((src[offset++] & 0xff) << 24) |
                    ((src[offset++] & 0xff) << 16) |
                    ((src[offset++] & 0xff) <<  8) |
                    ((src[offset++] & 0xff) <<  0);
            int l = ((src[offset++] & 0xff) << 24) |
                    ((src[offset++] & 0xff) << 16) |
                    ((src[offset++] & 0xff) <<  8) |
                    ((src[offset  ] & 0xff) <<  0);
            return (((long) h) << 32L) | ((long) l) & 0xffffffffL;
        } else {
            int l = ((src[offset++] & 0xff) <<  0) |
                    ((src[offset++] & 0xff) <<  8) |
                    ((src[offset++] & 0xff) << 16) |
                    ((src[offset++] & 0xff) << 24);
            int h = ((src[offset++] & 0xff) <<  0) |
                    ((src[offset++] & 0xff) <<  8) |
                    ((src[offset++] & 0xff) << 16) |
                    ((src[offset  ] & 0xff) << 24);
            return (((long) h) << 32L) | ((long) l) & 0xffffffffL;
        }
    }
    
    public static short peekShort(byte[] src, int offset, ByteOrder order) {
        if (order == ByteOrder.BIG_ENDIAN) {
            return (short) ((src[offset] << 8) | (src[offset + 1] & 0xff));
        } else {
            return (short) ((src[offset + 1] << 8) | (src[offset] & 0xff));
        }
    }

    public static void pokeInt(byte[] dst, int offset, int value, ByteOrder order) {
        if (order == ByteOrder.BIG_ENDIAN) {
            dst[offset++] = (byte) ((value >> 24) & 0xff);
            dst[offset++] = (byte) ((value >> 16) & 0xff);
            dst[offset++] = (byte) ((value >>  8) & 0xff);
            dst[offset  ] = (byte) ((value >>  0) & 0xff);
        } else {
            dst[offset++] = (byte) ((value >>  0) & 0xff);
            dst[offset++] = (byte) ((value >>  8) & 0xff);
            dst[offset++] = (byte) ((value >> 16) & 0xff);
            dst[offset  ] = (byte) ((value >> 24) & 0xff);
        }
    }
    
    public static void pokeLong(byte[] dst, int offset, long value, ByteOrder order) {
        if (order == ByteOrder.BIG_ENDIAN) {
            int i = (int) (value >> 32);
            dst[offset++] = (byte) ((i >> 24) & 0xff);
            dst[offset++] = (byte) ((i >> 16) & 0xff);
            dst[offset++] = (byte) ((i >>  8) & 0xff);
            dst[offset++] = (byte) ((i >>  0) & 0xff);
            i = (int) value;
            dst[offset++] = (byte) ((i >> 24) & 0xff);
            dst[offset++] = (byte) ((i >> 16) & 0xff);
            dst[offset++] = (byte) ((i >>  8) & 0xff);
            dst[offset  ] = (byte) ((i >>  0) & 0xff);
        } else {
            int i = (int) value;
            dst[offset++] = (byte) ((i >>  0) & 0xff);
            dst[offset++] = (byte) ((i >>  8) & 0xff);
            dst[offset++] = (byte) ((i >> 16) & 0xff);
            dst[offset++] = (byte) ((i >> 24) & 0xff);
            i = (int) (value >> 32);
            dst[offset++] = (byte) ((i >>  0) & 0xff);
            dst[offset++] = (byte) ((i >>  8) & 0xff);
            dst[offset++] = (byte) ((i >> 16) & 0xff);
            dst[offset  ] = (byte) ((i >> 24) & 0xff);
        }
    }

    public static void pokeShort(byte[] dst, int offset, short value, ByteOrder order) {
        if (order == ByteOrder.BIG_ENDIAN) {
            dst[offset++] = (byte) ((value >> 8) & 0xff);
            dst[offset  ] = (byte) ((value >> 0) & 0xff);
        } else {
            dst[offset++] = (byte) ((value >> 0) & 0xff);
            dst[offset  ] = (byte) ((value >> 8) & 0xff);
        }
    }
    
    //---------------
    public static byte[] pokeInt(int value, ByteOrder order) {
    	byte[] dst = new byte[4];
    	pokeInt(dst, 0, value, order);
    	return dst;
    }
    
    public static byte[] pokeLong(long value, ByteOrder order) {
    	byte[] dst = new byte[8];
    	pokeLong(dst, 0, value, order);
    	return dst;
    }

    public static byte[] pokeShort(short value, ByteOrder order) {
    	byte[] dst = new byte[2];
    	pokeInt(dst, 0, value, order);
    	return dst;
    }
}
