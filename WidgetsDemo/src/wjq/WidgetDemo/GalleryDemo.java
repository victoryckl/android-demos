/**
 * 
 */
package wjq.WidgetDemo;

import android.R.layout;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

/**
 * @author º«“‰µƒ”¿∫„
 * 
 */
public class GalleryDemo extends Activity {
	private Gallery gallery;
	private Gallery gallery1;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallerypage);
		gallery = (Gallery) findViewById(R.id.gallery);
		gallery.setAdapter(new ImageAdapter(this));
		
		registerForContextMenu(gallery);
		
		 Cursor c = getContentResolver().query(People.CONTENT_URI, null, null, null, null);
	        startManagingCursor(c);
	        
	        SpinnerAdapter adapter = new SimpleCursorAdapter(this,
	        // Use a template that displays a text view
	                android.R.layout.simple_gallery_item,
	                // Give the cursor to the list adatper
	                c,
	                // Map the NAME column in the people database to...
	                new String[] {People.NAME},
	                // The "text1" view defined in the XML template
	                new int[] { android.R.id.text1 });

	        gallery1= (Gallery) findViewById(R.id.gallery1);
	        gallery1.setAdapter(adapter);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.add("Action");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		Toast.makeText(this, "Longpress: " + info.position, Toast.LENGTH_SHORT)
				.show();
		return true;
	}
	
	public class ImageAdapter extends BaseAdapter {
		int mGalleryItemBackground;
		private Context mContext;

		private Integer[] mImageIds = { R.drawable.b, R.drawable.c,
				R.drawable.d, R.drawable.f, R.drawable.g };

		public ImageAdapter(Context context) {
			mContext = context;

			TypedArray a = obtainStyledAttributes(R.styleable.Gallery1);
			mGalleryItemBackground = a.getResourceId(
					R.styleable.Gallery1_android_galleryItemBackground, 0);
			a.recycle();
		}

		@Override
		public int getCount() {
			return mImageIds.length;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(mContext);

			i.setImageResource(mImageIds[position]);
			i.setScaleType(ImageView.ScaleType.FIT_XY);
			i.setLayoutParams(new Gallery.LayoutParams(300, 400));

			// The preferred Gallery item background
			i.setBackgroundResource(mGalleryItemBackground);

			return i;
		}

	}


}
