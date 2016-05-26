package com.hammersmith.onlineapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.Calendar;

/**
 * Created by Thuon on 5/25/2016.
 */
public class DateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    EditText editText;
    public DateDialog(View view){
        editText = (EditText)view;
    }
    public Dialog onCreateDialog(Bundle saveInstanceState){
        final Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        return new DatePickerDialog(getActivity(),this,year,month,day);
    }
    @Override
    public void onDateSet(android.widget.DatePicker datePicker, int year, int month, int day) {
        String date = year +"-"+(month+1)+"-"+day;
        editText.setText(date);
    }
}
