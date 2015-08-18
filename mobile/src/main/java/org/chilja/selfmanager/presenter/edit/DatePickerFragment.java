package org.chilja.selfmanager.presenter.edit;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by chiljagossow on 6/20/15.
 */
public class DatePickerFragment extends DialogFragment
          implements DatePickerDialog.OnDateSetListener {

  public static final String DATE_TYPE = "date_type";
  public static final int DUE_DATE = 1;
  public static final int REQUEST_DATE = 2;

  private OnFragmentInteractionListener mListener;

  public static DatePickerFragment newInstance(int type) {
    DatePickerFragment fragment = new DatePickerFragment();
    Bundle args = new Bundle();
    args.putInt(DATE_TYPE, type);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      mListener = (OnFragmentInteractionListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(
              activity.toString() + " must implement OnFragmentInteractionListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    // Use the current date as the default date in the picker
    final Calendar c = Calendar.getInstance();
    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.MONTH);
    int day = c.get(Calendar.DAY_OF_MONTH);

    // Create a new instance of DatePickerFragment and return it
    return new DatePickerDialog(getActivity(), this, year, month, day);
  }

  public void onDateSet(DatePicker view, int year, int month, int day) {
    // Do something with the date chosen by the user
    int type = 0;
    if (getArguments() != null) {
      type = getArguments().getInt(DATE_TYPE);
      switch (type) {
        case DatePickerFragment.DUE_DATE:
          mListener.onDueDateSet(year, month, day);
          break;
        case DatePickerFragment.REQUEST_DATE:
          mListener.onRequestDateSet(year, month, day);
          break;
      }
    }
  }

  interface OnFragmentInteractionListener {
    void onDueDateSet(int year, int month, int day);
    void onRequestDateSet(int year, int month, int day);
  }
}