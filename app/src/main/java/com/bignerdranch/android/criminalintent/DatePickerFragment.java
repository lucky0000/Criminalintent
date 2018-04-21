package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends android.support.v4.app.DialogFragment {
    private static final String ARG_DATE = "date";
    public static final String EXTRA_DATE = "com.bignerdranch.android.criminalintent.date";
    private DatePicker mDatePicker;

    private void sendResult(int resultCode, Date date) {
        if (getTargetFragment() == null)
            return;
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }


    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setArguments(args);
        return datePickerFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        return super.onCreateDialog(savedInstanceState);
        View diglogDate = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);

        Date date = (Date) getArguments().getSerializable(ARG_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        mDatePicker = (DatePicker) diglogDate.findViewById(R.id.dialog_date_picker);
        mDatePicker.init(year, month, day, null);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.date_picker_title)
                .setView(diglogDate)
                .setPositiveButton(android.R.string.ok, (d, w) -> {
                    int y = mDatePicker.getYear();
                    int m = mDatePicker.getMonth();
                    int d1 = mDatePicker.getDayOfMonth();
                    Date newDate = new GregorianCalendar(y, m, d1).getTime();
                    sendResult(Activity.RESULT_OK, newDate);

                })
                .create();

    }
}
