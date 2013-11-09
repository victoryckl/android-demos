package com.example.utils;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.net.URL;
import java.nio.charset.Charset;

public class GuessCodepage {
	/**
	 * 利用第三方开源包cpdetector获取文件编码格式
	 * @param path
	 *            要判断文件编码格式的源文件的路径
	 */
	private static String getFileEncode(String path) {
		/*
		 * detector是探测器，它把探测任务交给具体的探测实现类的实例完成。
		 * cpDetector内置了一些常用的探测实现类，这些探测实现类的实例可以通过add方法 加进来，如ParsingDetector、
		 * JChardetFacade、ASCIIDetector、UnicodeDetector。
		 * detector按照“谁最先返回非空的探测结果，就以该结果为准”的原则返回探测到的
		 * 字符集编码。使用需要用到三个第三方JAR包：antlr.jar、chardet.jar和cpdetector.jar
		 * cpDetector是基于统计学原理的，不保证完全正确。
		 */
		CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
		addDetector(detector);
		
		java.nio.charset.Charset charset = null;
		File f = new File(path);
		try {
			charset = detector.detectCodepage(f.toURI().toURL());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (charset != null)
			return charset.name();
		else
			return null;
	}
	
	/**
	 * 获取URL对应的文件编码
	 * @param path
	 *            要判断文件编码格式的源文件的URL
	 */
	private static String getUrlEncode(URL url) {
		CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
		addDetector(detector);
		java.nio.charset.Charset charset = null;
		try {
			charset = detector.detectCodepage(url);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (charset != null)
			return charset.name();
		else
			return null;
	}
	
	/**
	 * 获取inputStream对应的文本编码
	 * @param inputStream
	 *          待测的文本输入流，必须支持mark
	 * @param length
	 * 			测量该流所需的读入字节数
	 */
	private static String getSteamEncode(InputStream inputStream, int length) {
		CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
		addDetector(detector);
		java.nio.charset.Charset charset = null;
		try {
			charset = detector.detectCodepage(inputStream, length);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (charset != null)
			return charset.name();
		else
			return null;
	}
	
	private static void addDetector(CodepageDetectorProxy detector) {
		/*
		 * ParsingDetector可用于检查HTML、XML等文件或字符流的编码,构造方法中的参数用于
		 * 指示是否显示探测过程的详细信息，为false不显示。
		 */
		detector.add(new ParsingDetector(false));
		/*
		 * JChardetFacade封装了由Mozilla组织提供的JChardet，它可以完成大多数文件的编码
		 * 测定。所以，一般有了这个探测器就可满足大多数项目的要求，如果你还不放心，可以
		 * 再多加几个探测器，比如下面的ASCIIDetector、UnicodeDetector等。
		 */
		detector.add(JChardetFacade.getInstance());// 用到antlr.jar、chardet.jar
		// ASCIIDetector用于ASCII编码测定
		detector.add(ASCIIDetector.getInstance());
		// UnicodeDetector用于Unicode家族编码的测定
		detector.add(UnicodeDetector.getInstance());
	}
	
	/*******************/
	private static final int BOM_SIZE = 4;
	
	private static String detectByHeadBytes(String path) throws IOException {
		FileInputStream fi = new FileInputStream(path);
		String charset = detectByHeadBytes(fi);
		fi.close();
		return charset;
	}
	
	private static String detectByHeadBytes(URL url) throws IOException {
		HttpUtils http = new HttpUtils();
		String charset = detectByHeadBytes(http.getInputStream(url));
		http.close();
		return charset;
	}
	
	private static String detectByHeadBytes(InputStream in) throws IOException {
		if (in == null) return null;
		
		PushbackInputStream internalIn = new PushbackInputStream(in, BOM_SIZE);
		String encoding = null;
		byte bom[] = new byte[BOM_SIZE];
		int n;
		n = internalIn.read(bom, 0, bom.length);

		if ((bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00)
				&& (bom[2] == (byte) 0xFE) && (bom[3] == (byte) 0xFF)) {
			encoding = "UTF-32BE";
		} else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)
				&& (bom[2] == (byte) 0x00) && (bom[3] == (byte) 0x00)) {
			encoding = "UTF-32LE";
		} else if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB)
				&& (bom[2] == (byte) 0xBF)) {
			encoding = "UTF-8";
		} else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF)) {
			encoding = "UTF-16BE";
		} else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)) {
			encoding = "UTF-16LE";
		} else {
			// Unicode BOM mark not found, unread all bytes
		}
		System.out.println("encoding="+encoding+", read=" + n);

		internalIn.unread(bom, 0, n);

		return encoding;
	}
	
	/********************************/
	public static String getCodepage(String path) {
		String charset = null;
		try {
			charset = detectByHeadBytes(path);
			if (charset != null) {
				return charset;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return getFileEncode(path);
	}
	
	public static String getCodepage(URL url) {
		String charset = null;
		try {
			charset = detectByHeadBytes(url);
			if (charset != null) {
				return charset;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return getUrlEncode(url);
	}
	
	public static String getCodepage(InputStream in) {
		String charset = null;
		try {
			charset = detectByHeadBytes(in);
			if (charset != null) {
				return charset;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			return getSteamEncode(in, in.available());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}