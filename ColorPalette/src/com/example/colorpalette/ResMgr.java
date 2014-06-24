package com.example.colorpalette;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

public class ResMgr {
	private static final String TAG = ResMgr.class.getSimpleName();
	private Context ctx;
	private Resources res;
	private String pkg;
	private Field[] colorFields;
	
	public ResMgr(Context context) {
		ctx = context;
		res = ctx.getResources();
		pkg = ctx.getPackageName();
		
		colorFields = initType("color");
	}
	
	private Field[] initType(String type) {
		Class<?> c = null;
		Field[] fields = null;
		String clzName = pkg + ".R$"+type;
		Log.i(TAG, clzName);
		try {
			c = Class.forName(clzName);
			fields = c.getFields();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return fields;
	}
	
	public Field[] getColorFields() {
		return colorFields;
	}
	
	public static class ColorItem {
		String name;
		int color;
	}
	/*
	 * return List<ColorItem>
	 */
	public List<ColorItem> getColors() {
		List<ColorItem> colors = new ArrayList<ColorItem>();
		for (int i=0; i<colorFields.length; i++) {
			try {
				Field f = colorFields[i];
				ColorItem c = new ColorItem();
				c.name = f.getName();
				c.color = res.getColor(res.getIdentifier(c.name, "color", pkg));
				colors.add(c);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		return colors;
	}
}
