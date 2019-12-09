package com.example.coopertest;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.preference.PreferenceDialogFragmentCompat;

public class DatePreferenceDialogFragment extends PreferenceDialogFragmentCompat {



    private int mLastYear;
    private int mLastMonth;
    private int mLastDay;
    private DatePicker mDatePicker;

    public static DatePreferenceDialogFragment newInstance(String key) {
        final DatePreferenceDialogFragment
                fragment = new DatePreferenceDialogFragment();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String dateValue = getDatePreference().getDate();

        if (dateValue == null || dateValue.isEmpty()) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            dateValue = df.format(calendar.getTime());
        }

        mLastYear = getYear(dateValue);
        mLastMonth = getMonth(dateValue);
        mLastDay = getDay(dateValue);
    }

    @Override
    protected View onCreateDialogView(Context context) {
        mDatePicker = new DatePicker(getContext());
        // Show spinner dialog for old APIs.
        mDatePicker.setCalendarViewShown(false);

        return mDatePicker;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        mDatePicker.updateDate(mLastYear, mLastMonth - 1, mLastDay);
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            mLastYear = mDatePicker.getYear();
            mLastMonth = mDatePicker.getMonth() + 1;
            mLastDay = mDatePicker.getDayOfMonth();

            String dateVal = String.valueOf(mLastYear) + "-"
                    + String.valueOf(mLastMonth) + "-"
                    + String.valueOf(mLastDay);

            final DatePreference preference = getDatePreference();
            if (preference.callChangeListener(dateVal)) {
                preference.setDate(dateVal);
            }
        }
    }

    private DatePreference getDatePreference() {
        return (DatePreference) getPreference();
    }

    private int getYear(String dateString) {
        String[] datePieces = dateString.split("-");
        return (Integer.parseInt(datePieces[0]));
    }

    private int getMonth(String dateString) {
        String[] datePieces = dateString.split("-");
        return (Integer.parseInt(datePieces[1]));
    }

    private int getDay(String dateString) {
        String[] datePieces = dateString.split("-");
        return (Integer.parseInt(datePieces[2]));
    }
}
