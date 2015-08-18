package org.chilja.selfmanager.presenter.edit;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.chilja.selfmanager.R;
import org.chilja.selfmanager.model.WaitItem;

import java.util.GregorianCalendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditWaitItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditWaitItemFragment extends EditItemFragment {

  public static final String TAG = "EditWaitItemFragment";

  public static final String ARG_WAIT_ITEM = "arg_wait_item";

  private TextView mDueDateView;
  private EditText mName;
  private TextView mRequestDateView;
  private EditText mResponsible;

  public WaitItem getWaitItem() {
    return mWaitItem;
  }

  private WaitItem mWaitItem;


  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @return A new instance of fragment EditGoalFragment.
   */

  public static EditWaitItemFragment newInstance(String title) {
    EditWaitItemFragment fragment = new EditWaitItemFragment();
    fragment.mTitle = title;
    return fragment;
  }

  public static EditWaitItemFragment newInstance(WaitItem item, String title) {
    EditWaitItemFragment fragment = newInstance(title);
    Bundle args = new Bundle();
    args.putParcelable(ARG_WAIT_ITEM, item);
    fragment.setArguments(args);
    return fragment;
  }

  public EditWaitItemFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_edit_wait_item, container, false);
    mName = (EditText) view.findViewById(R.id.name);
    mDueDateView = (TextView) view.findViewById(R.id.due_date);
    mResponsible = (EditText) view.findViewById(R.id.responsible);
    mRequestDateView = (TextView) view.findViewById(R.id.request_date);
    Bundle args = getArguments();
    if (args != null) {
      mWaitItem = args.getParcelable(ARG_WAIT_ITEM);
      if (mWaitItem  == null) {
        mWaitItem  = new WaitItem();
      } else {
        mName.setText(mWaitItem .getName());
        mResponsible.setText(mWaitItem.getResponsible());
      }
    } else {
      mWaitItem  = new WaitItem();
    }
    setDueDateView(mWaitItem);
    setRequestDateView(mWaitItem);
    setUpSpinner(view, mWaitItem);
    return view;
  }

  private void setDueDateView(WaitItem item) {
    mDueDateView.setText(item.getDisplayDate(item.getDueDate()));
  }

  public void setDueDate(int year, int month, int day) {
    super.setDueDate(year, month, day);
    mWaitItem.setDueDate(new GregorianCalendar(year, month, day));
    setDueDateView(mWaitItem);
  }

  private void setRequestDateView(WaitItem item) {
    mRequestDateView.setText(item.getDisplayDate(item.getRequestDate()));;
  }

  public void setRequestDate(int year, int month, int day) {
    super.setRequestDate(year, month, day);
    mWaitItem.setRequestDate(new GregorianCalendar(year, month, day));
    setRequestDateView(mWaitItem);
  }

  @Override
  public void onDetach() {
    super.onDetach();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }

  @Override
  public String getTAG() {
    return TAG;
  }

  @Override
  public String getTitle() {
    return mTitle;
  }

  @Override
  public void save(Context context) {
    String name = mName.getText().toString();
    if (name != null &&  name != "") {
      mWaitItem.setResponsible(mResponsible.getText().toString());
      mWaitItem.setName(name);
      mWaitItem.save(context);
      NavUtils.navigateUpFromSameTask(getActivity());
    } else {
      Toast.makeText(context, "Please enter a name", Toast.LENGTH_SHORT);
    }
  }

  @Override
  public void delete(Context context) {
    mWaitItem.delete(context);
  }

  @Override
  public int getScrollY() {
    return 0;
  }

  @Override
  public void scrollTo(int y) {

  }

  public interface OnFragmentInteractionListener {
    void onRequestDatePickerCall(View view);
  }

}
