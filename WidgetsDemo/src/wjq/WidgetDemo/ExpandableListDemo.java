package wjq.WidgetDemo;

import android.app.ExpandableListActivity;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;

public class ExpandableListDemo extends ExpandableListActivity{
private ExpandableListAdapter adapter;
//private MyExpandableListAdapter MyAdapter=new MyExpandableListAdapter();

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		adapter=new MyExpandableListAdapter();
		setListAdapter(adapter);
		registerForContextMenu(getExpandableListView());
	}
	/* (non-Javadoc)
	 * @see android.app.ExpandableListActivity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("菜单");
		menu.add(0, 0, 0, "Action");
	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		boolean flag=false;
		// TODO Auto-generated method stub
		ExpandableListContextMenuInfo menuInfo=(ExpandableListContextMenuInfo)item.getMenuInfo();
		String title=((TextView)menuInfo.targetView).getText().toString();
		int type=ExpandableListView.getPackedPositionType(menuInfo.packedPosition);
		
		if (type==ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
		int	groupPos =ExpandableListView.getPackedPositionGroup(menuInfo.packedPosition);
		int	childPos =ExpandableListView.getPackedPositionChild(menuInfo.packedPosition);
		
		CharSequence str="您单击了"+title;
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
		Log.i("tag", "Run Hereing...");
		
		flag= true;
		}
		
		else
			 if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
	            int groupPos = ExpandableListView.getPackedPositionGroup(menuInfo.packedPosition); 
	            CharSequence cs="您单击了"+title;
	            Toast.makeText(this, cs, Toast.LENGTH_SHORT).show();
	            Log.i("tag", "Run Here...");
	            flag= true;
	        }
		return flag;
	}
	
	 public class MyExpandableListAdapter extends BaseExpandableListAdapter {
	        // Sample data set.  children[i] contains the children (String[]) for groups[i].
	        public String[] groups = { "我的好友", "新疆同学", "亲戚", "同事" };
	        public String[][] children = {
	                { "胡算林", "张俊峰", "王志军", "二人" },
	                { "李秀婷", "蔡乔", "别高", "余音" },
	                { "摊派新", "张爱明" },
	                { "马超", "司道光" }
	        };
	        
	        public Object getChild(int groupPosition, int childPosition) {
	            return children[groupPosition][childPosition];
	        }

	        public long getChildId(int groupPosition, int childPosition) {
	            return childPosition;
	        }

	        public int getChildrenCount(int groupPosition) {
	            return children[groupPosition].length;
	        }

	        public TextView getGenericView() {
	            // Layout parameters for the ExpandableListView
	            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
	                    ViewGroup.LayoutParams.MATCH_PARENT, 64);

	            TextView textView = new TextView(ExpandableListDemo.this);
	            textView.setLayoutParams(lp);
	            // Center the text vertically
	            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
	            // Set the text starting position
	            textView.setPadding(36, 0, 0, 0);
	            return textView;
	        }
	        
	        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
	                View convertView, ViewGroup parent) {
	            TextView textView = getGenericView();
	            textView.setText(getChild(groupPosition, childPosition).toString());
	            return textView;
	        }

	        public Object getGroup(int groupPosition) {
	            return groups[groupPosition];
	        }

	        public int getGroupCount() {
	            return groups.length;
	        }

	        public long getGroupId(int groupPosition) {
	            return groupPosition;
	        }

	        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
	                ViewGroup parent) {
	            TextView textView = getGenericView();
	            textView.setText(getGroup(groupPosition).toString());
	            return textView;
	        }

	        public boolean isChildSelectable(int groupPosition, int childPosition) {
	            return true;
	        }

	        public boolean hasStableIds() {
	            return true;
	        }

	    }

}
