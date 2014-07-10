package com.example.utils;


public class EndianUtils {
	private EndianUtils() {}
	
	/** 将按小端顺序排列的字节数组转为相应的类型数据   */
	public static class le {
		/**
		 * 将按小端顺序排列的字节数组转为一个int数据   
		 * @param array 按小端顺序排列的字节数组   
		 * @return int数据  
		 */
		public static int getInt(byte[] array, int start) {
	        int i = (int) 
	        		  ((array[start + 3] & 0xff) << 24)
	                | ((array[start + 2] & 0xff) << 16)
	                | ((array[start + 1] & 0xff) << 8)
	                | ( array[start + 0] & 0xff);
	        return i;
		}
		
	    public static int getShort(byte[] array, int start) {
	        int i = (int) 
	        		  ((array[start + 1] & 0xff) << 8)
	                | ( array[start + 0] & 0xff);
	        return i;
	    }
	    
	    public static int getByte(byte[] array, int start) {
	        int i = (int)(array[start] & 0xff);
	        return i;
	    }
	    
	    public static byte[] fromInt(int i) {
	    	byte[] buf = new byte[4];
	    	buf[3] = (byte) (i >>> 24);
	    	buf[2] = (byte) (i >>> 16);
	    	buf[1] = (byte) (i >>> 8);
	    	buf[0] = (byte) (i);
	    	return buf;
	    }
	    
	    public static byte[] fromShort(int s) {
	    	byte[] buf = new byte[2];
	    	buf[1] = (byte) (s >>> 8);
	    	buf[0] = (byte) (s);
	    	return buf;
	    }
	}
	
	public static class be {
		
	}
}
