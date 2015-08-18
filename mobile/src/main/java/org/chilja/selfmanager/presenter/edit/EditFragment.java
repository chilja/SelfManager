package org.chilja.selfmanager.presenter.edit;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import org.chilja.selfmanager.presenter.base.BaseFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by chiljagossow on 6/20/15.
 */
public abstract class EditFragment extends BaseFragment {

  protected Calendar mDueDate = Calendar.getInstance();
  protected Calendar mRequestDate = Calendar.getInstance();
  protected SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


  public abstract void save(Context context);
  public abstract void delete(Context context);
  public void setDueDate(int year, int month, int day) {
    mDueDate = new GregorianCalendar(year, month, day);
  }
  public void setRequestDate(int year, int month, int day) {
    mRequestDate = new GregorianCalendar(year, month, day);
  }
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
  }

  public interface OnFragmentInteractionListener {
    void onEndDatePickerCall(View view);
  }
}
