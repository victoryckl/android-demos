package wjq.WidgetDemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Main extends Activity implements OnClickListener{
	private Button btnAnim;
	private Button btnactv;
	private Button btnchro;
	private Button btndate;
	private Button btnelv;
	private Button btngallery;
	private Button btngrid;
	private Button btnimgs;
	private Button btnrb;
	private Button btnsb;
	private Button btnpb;
	private Button btnsp;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        btnAnim=(Button)findViewById(R.id.btnanim);
        btnAnim.setOnClickListener(this);
        btnactv=(Button)findViewById(R.id.btnactv);
        btnactv.setOnClickListener(this);
        btnchro=(Button)findViewById(R.id.btnchronometer);
        btnchro.setOnClickListener(this);
        btndate=(Button)findViewById(R.id.btndate);
        btndate.setOnClickListener(this);
        btnelv=(Button)findViewById(R.id.btnelv);
        btnelv.setOnClickListener(this);
        btngallery=(Button)findViewById(R.id.btngallery);
        btngallery.setOnClickListener(this);
        btngrid=(Button)findViewById(R.id.btngrid);
        btngrid.setOnClickListener(this);
        btnimgs=(Button)findViewById(R.id.btnimgs);
        btnimgs.setOnClickListener(this);
        btnrb=(Button)findViewById(R.id.btnrb);
        btnrb.setOnClickListener(this);
        btnsb=(Button)findViewById(R.id.btnsb);
        btnsb.setOnClickListener(this);
        btnpb=(Button)findViewById(R.id.btnpb);
        btnpb.setOnClickListener(this);
        btnsp=(Button)findViewById(R.id.btnsp);
        btnsp.setOnClickListener(this);
    }
	@Override
	public void onClick(View v) {
		if (v.getId()==R.id.btnanim) {
			startActivity(new Intent(this,AnimationDemo.class));
			return;
		}
		
		if (v.getId()==R.id.btnactv) {
			startActivity(new Intent(this,AutoCompleteDemo.class));
			return;
		}
		
		if (v.getId()==R.id.btnchronometer) {
			startActivity(new Intent(this,ChronometerDemo.class));
			return;
		}
		
		if (v.getId()==R.id.btndate) {
			startActivity(new Intent(this,DatePickerDemo.class));
			return;
		}
		
		if (v.getId()==R.id.btnelv) {
			startActivity(new Intent(this,ExpandableListDemo.class));
			return;
		}
		
		if (v.getId()==R.id.btnelv) {
			startActivity(new Intent(this,ExpandableListDemo.class));
			return;
		}
		
		if (v.getId()==R.id.btngallery) {
			startActivity(new Intent(this,GalleryDemo.class));
			return;
		}
		
		if (v.getId()==R.id.btngrid) {
			startActivity(new Intent(this,GridDemo.class));
			return;
		}
		
		if (v.getId()==R.id.btnimgs) {
			startActivity(new Intent(this,ImageSwitcherDemo.class));
			return;
		}
		
		if (v.getId()==R.id.btnrb) {
			startActivity(new Intent(this,RatingBarDemo.class));
			return;
		}
		
		if (v.getId()==R.id.btnsb) {
			startActivity(new Intent(this,SeekBarDemo.class));
			return;
		}
		
		if (v.getId()==R.id.btnpb) {
			startActivity(new Intent(this,ProgressBarDemo.class));
			return;
		}
		
		if (v.getId()==R.id.btnsp) {
			startActivity(new Intent(this,SpinnerDemo.class));
			return;
		}
	}
}