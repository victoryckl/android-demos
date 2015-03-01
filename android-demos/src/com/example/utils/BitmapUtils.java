package com.example.utils;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class BitmapUtils {
	
	public static byte[] toByteArray(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}
	
	public static byte[] toByteArray(Context ctx, int id) {
		Bitmap bitmap = ((BitmapDrawable) ctx.getResources().getDrawable(id)).getBitmap();
		return toByteArray(bitmap);
	}
	
	public static byte[] toByteArray(Drawable drawable) {
		Bitmap bitmap = toBitmap(drawable);
		return toByteArray(bitmap);
	}
	
	public static Bitmap toBitmap(byte[] data) {
		return BitmapFactory.decodeByteArray(data, 0, data.length);
	}
	
	public static Bitmap toBitmap(Drawable drawable) {
		return ((BitmapDrawable)drawable).getBitmap();
	}
	
	public static Drawable toDrawable(Context ctx, Bitmap bitmap) {
		return new BitmapDrawable(ctx.getResources(), bitmap); 
	}
	
	public static Drawable toDrawable(Context ctx, byte[] data) {
		Bitmap bitmap = toBitmap(data);
		return toDrawable(ctx, bitmap);
	}
	
	public static Drawable toDrawable(Context ctx, int id) {
		return ctx.getResources().getDrawable(id);
	}
}
