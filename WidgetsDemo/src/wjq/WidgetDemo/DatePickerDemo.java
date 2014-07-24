package wjq.WidgetDemo;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class DatePickerDemo extends Activity implements OnClickListener,OnDateSetListener,OnTimeSetListener,OnTimeChangedListener {
	private TextView mDateDisplay;
	private Button pickDate;
	private Button pickTime;
	private TimePicker timePicker;
	
	private int mYear;
	private int mMonth;
	private int mDay;
	private int mHour;
	private int mMinute;

	static final int TIME_DIALOG_ID = 0;
	static final int DATE_DIALOG_ID = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.datedisplay);
		mDateDisplay = (TextView) findViewById(R.id.dateDisplay);

		pickDate = (Button) findViewById(R.id.pickDate);
		pickDate.setOnClickListener(this);
		pickTime = (Button) findViewById(R.id.pickTime);
		pickTime.setOnClickListener(this);
		
		final Calendar calendar=Calendar.getInstance();
		mYear=calendar.get(Calendar.YEAR);
		mMonth=calendar.get(Calendar.MONTH);
		mDay=calendar.get(Calendar.DAY_OF_MONTH);
		mHour=calendar.get(Calendar.HOUR_OF_DAY);
		mMinute=calendar.get(Calendar.MINUTE);
		
		updateDisplay();
		
		timePicker=(TimePicker)findViewById(R.id.timePicker);
		timePicker.setCurrentHour(16);
		timePicker.setCurrentMinute(10);
		updateDisplay(16,10);
		timePicker.setOnTimeChangedListener(this);
	}

	/**
	 * 更新显示
	 */
	private void updateDisplay() {
		 mDateDisplay.setText(
				 new StringBuilder().append(mYear).append("年")
				 .append(mMonth + 1).append("月")
				 .append(mDay).append("日")
				 .append(pad(mHour)).append(":").append(pad(mMinute)));  
	}
	
    private void updateDisplay(int hourOfDay, int minute) {
        mDateDisplay.setText(
                    new StringBuilder()
                    .append(pad(hourOfDay)).append(":")
                    .append(pad(minute)));
    }

	/**
	 * 时间点格式
	 * @param i
	 * @return
	 */
	private static String pad(int i) {
		 if (i 	>= 10)
	            return String.valueOf(i);
	        else
	            return "0" + String.valueOf(i);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.pickDate) {
			showDialog(DATE_DIALOG_ID);
		}

		if (v.getId() == R.id.pickTime) {
			showDialog(TIME_DIALOG_ID);
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		 switch (id) {
         case TIME_DIALOG_ID:
             return new TimePickerDialog(this,
                     this, mHour, mMinute, false);
         case DATE_DIALOG_ID:
             return new DatePickerDialog(this,
            		 this,
                         mYear, mMonth, mDay);
     }
     return null;
	}
	
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case TIME_DIALOG_ID:
                ((TimePickerDialog) dialog).updateTime(mHour, mMinute);
                break;
            case DATE_DIALOG_ID:
                ((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
                break;
        }
    }  

	@Override
	 public void onDateSet(DatePicker view, int year, int monthOfYear,
             int dayOfMonth) {
         mYear = year;
         mMonth = monthOfYear;
         mDay = dayOfMonth;
         updateDisplay();
     }

	@Override
	 public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mHour = hourOfDay;
        mMinute = minute;
        updateDisplay();
    }

	@Override
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		updateDisplay(hourOfDay, minute);
	}

}
