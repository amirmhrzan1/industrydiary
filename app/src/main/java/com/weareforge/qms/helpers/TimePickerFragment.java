package com.weareforge.qms.helpers;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import com.weareforge.qms.fragments.IndustryEngagementFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by prajit on 2/16/16.
 */
public class TimePickerFragment implements  TimePickerDialog.OnTimeSetListener {

    private EditText editText;
    private Calendar myCalendar;
    private Context context;
    private String time;

    public TimePickerFragment(EditText editText, Context ctx,String selectedTime){
        this.editText = editText;
        this.myCalendar = Calendar.getInstance();
        this.context = ctx;
        this.time = selectedTime;

        int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = myCalendar.get(Calendar.MINUTE);
        new TimePickerDialog(this.context, this, hour, minute, true).show();

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // TODO Auto-generated method stub

        String time = hourOfDay + ":" + minute + ":00";
        SimpleDateFormat sdfs = new SimpleDateFormat("hh:mm aa");
        Date dt = new Date();
        dt.setHours(hourOfDay);
        dt.setMinutes(minute);
        this.editText.setText( sdfs.format(dt));
        IndustryEngagementFragment.selectedTime = time;


    }
}