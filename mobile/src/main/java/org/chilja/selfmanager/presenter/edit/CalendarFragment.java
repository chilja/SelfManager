package org.chilja.selfmanager.presenter.edit;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import org.chilja.selfmanager.R;
import org.chilja.selfmanager.presenter.base.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarFragment extends BaseFragment {

  private static final String TITLE = "Pick date";

  public CalendarFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @return A new instance of fragment CalendarFragment.
   */
  public static CalendarFragment newInstance() {
    CalendarFragment fragment = new CalendarFragment();
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment

    View view = inflater.inflate(R.layout.fragment_calendar, container, false);
    CalendarView calendar = (CalendarView) view.findViewById(R.id.calendar);
    initializeCalendar(calendar);

    return view;

  }

  public void initializeCalendar(CalendarView calendar) {
    // sets whether to show the week number.

    calendar.setShowWeekNumber(false);
    // sets the first day of week according to Calendar.
    // here we set Monday as the first day of the Calendar
    calendar.setFirstDayOfWeek(2);
    //The background color for the selected week.
    calendar.setSelectedWeekBackgroundColor(getResources().getColor(R.color.primary_light));
    //sets the color for the dates of an unfocused month.
    calendar.setUnfocusedMonthDateColor(getResources().getColor(R.color.white));
    //sets the color for the separator line between weeks.
    calendar.setWeekSeparatorLineColor(getResources().getColor(R.color.white));
    //sets the color for the vertical bar shown at the beginning and at the end of the selected date.
    calendar.setSelectedDateVerticalBar(R.color.accent);
    //sets the listener to be notified upon selected date change.
    calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
      //show the selected date as a toast
      @Override
      public void onSelectedDayChange(CalendarView view, int year, int month, int day) {
        Toast.makeText(getActivity(), day + "/" + month + "/" + year, Toast.LENGTH_LONG).show();
      }
    });
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  @Override
  public String getTAG() {
    return null;
  }

  @Override
  public String getTitle() {
    return TITLE;
  }

  @Override
  public int getScrollY() {
    return 0;
  }

  @Override
  public void scrollTo(int y) {

  }


}
