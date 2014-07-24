package wjq.WidgetDemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class SpinnerDemo extends Activity {
	  @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.spinnerpage);

	        Spinner s1 = (Spinner) findViewById(R.id.spinnercolor);
	        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	                this, R.array.colors, android.R.layout.simple_spinner_item);
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        s1.setAdapter(adapter);
	        s1.setOnItemSelectedListener(
	                new OnItemSelectedListener() {
	                    public void onItemSelected(
	                            AdapterView<?> parent, View view, int position, long id) {
	                        showToast("Spinner1: position=" + position + " id=" + id);
	                    }

	                    public void onNothingSelected(AdapterView<?> parent) {
	                        showToast("Spinner1: unselected");
	                    }
	                });

	        Spinner s2 = (Spinner) findViewById(R.id.spinnerplanet);
	        adapter = ArrayAdapter.createFromResource(this, R.array.planets,
	                android.R.layout.simple_spinner_item);
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        s2.setAdapter(adapter);
	        s2.setOnItemSelectedListener(
	                new OnItemSelectedListener() {
	                    public void onItemSelected(
	                            AdapterView<?> parent, View view, int position, long id) {
	                        showToast("Spinner2: position=" + position+1 + " id=" + id+1);
	                    }

	                    public void onNothingSelected(AdapterView<?> parent) {
	                        showToast("Spinner2: unselected");
	                    }
	                });
	    }
	  
	private void showToast(CharSequence msg) {
	        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	    }
}
