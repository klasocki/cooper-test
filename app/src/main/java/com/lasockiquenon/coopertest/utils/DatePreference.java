package com.lasockiquenon.coopertest.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;

//import com.example.coopertest.util.timereminder.R;

import androidx.preference.DialogPreference;

import com.lasockiquenon.coopertest.R;

/**
 * A dialog preference that shown calendar in the dialog.
 *
 * Saves a string value.
 */
public class DatePreference extends DialogPreference {

    private String mDateValue;

    public DatePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(Object defaultValue) {
        setDate(getPersistedString((String) defaultValue));
    }

    /**
     * Gets the date as a string from the current data storage.
     *
     * @return string representation of the date.
     */
    public String getDate() {
        return mDateValue;
    }

    /**
     * Saves the date as a string in the current data storage.
     *
     * @param text string representation of the date to save.
     */
    public void setDate(String text) {
        final boolean wasBlocking = shouldDisableDependents();

        mDateValue = text;

        persistString(text);

        final boolean isBlocking = shouldDisableDependents();
        if (isBlocking != wasBlocking) {
            notifyDependencyChange(isBlocking);
        }

        notifyChanged();
    }

    /**
     * A simple {@link androidx.preference.Preference.SummaryProvider} implementation for an
     * {@link DatePreference}. If no value has been set, the summary displayed will be 'Not
     * set', otherwise the summary displayed will be the value set for this preference.
     */
    public static final class SimpleSummaryProvider implements SummaryProvider<DatePreference> {

        private static SimpleSummaryProvider sSimpleSummaryProvider;

        private SimpleSummaryProvider() {}

        /**
         * Retrieve a singleton instance of this simple
         * {@link androidx.preference.Preference.SummaryProvider} implementation.
         *
         * @return a singleton instance of this simple
         * {@link androidx.preference.Preference.SummaryProvider} implementation
         */
        public static SimpleSummaryProvider getInstance() {
            if (sSimpleSummaryProvider == null) {
                sSimpleSummaryProvider = new SimpleSummaryProvider();
            }
            return sSimpleSummaryProvider;
        }

        @Override
        public CharSequence provideSummary(DatePreference preference) {
            if (TextUtils.isEmpty(preference.getDate())) {
                return (preference.getContext().getString(R.string.not_set));
            } else {
                return preference.getDate();
            }
        }
    }
}
