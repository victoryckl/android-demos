package com.example.aidl;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
public class Book implements Parcelable {

    private static final String TAG = "Book";
	private String bookName;
    private int bookPrice;

    public Book(){
    	Log.i(TAG, "Book()");
    }

    public Book(Parcel parcel){
    	Log.i(TAG, "Book(Parcel)");
        bookName = parcel.readString();
        bookPrice = parcel.readInt();
    }

    public String getBookName() {
        return bookName;
    }
    public void setBookName(String bookName) {
        this.bookName = bookName;
    }
    public int getBookPrice() {
        return bookPrice;
    }
    public void setBookPrice(int bookPrice) {
        this.bookPrice = bookPrice;
    }

    public int describeContents() {
    	Log.i(TAG, "describeContents()");
        return 0;
    }
    public void writeToParcel(Parcel parcel, int flags) {
    	Log.i(TAG, "writeToParcel()");
        parcel.writeString(bookName);
        parcel.writeInt(bookPrice);
    }

    public static final Parcelable.Creator<Book> CREATOR = new Creator<Book>() {
        public Book createFromParcel(Parcel source) {

            return new Book(source);
        }
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
}