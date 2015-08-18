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

import org.chilja.selfmanager.R;
import org.chilja.selfmanager.model.Action;
import org.chilja.selfmanager.model.Goal;

import java.util.GregorianCalendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditActionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditActionFragment extends EditItemFragment {

  public static final String TAG = "EditActionFragment";

  public static final String ARG_GOAL_ID = "arg_goal_id";
  public static final String ARG_ACTION = "arg_action";

  private TextView mEndDateView;
  private EditText mName;


  private Action mAction;


  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @return A new instance of fragment EditGoalFragment.
   */

  public static EditActionFragment newInstance(String title) {
    EditActionFragment fragment = new EditActionFragment();
    fragment.setTitle(title);
    return fragment;
  }

  public static EditActionFragment newInstance(int goalId, String title) {
    EditActionFragment fragment = new EditActionFragment();
    Bundle args = new Bundle();
    args.putInt(ARG_GOAL_ID, goalId);
    fragment.setArguments(args);
    fragment.setTitle(title);
    return fragment;
  }

  public static EditActionFragment newInstance(Action action, String title) {
    EditActionFragment fragment = new EditActionFragment();
    Bundle args = new Bundle();
    args.putParcelable(ARG_ACTION, action);
    fragment.setArguments(args);
    fragment.setTitle(title);
    return fragment;
  }

  public EditActionFragment() {
    // Required empty public constructor
  }

  public Action getAction() {
    return mAction;
  }


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_edit_action, container, false);
    mName = (EditText) view.findViewById(R.id.name);
    mEndDateView = (TextView) view.findViewById(R.id.end_date);
    Bundle args = getArguments();
    if (args != null) {
      mAction = args.getParcelable(ARG_ACTION);
      if (mAction == null) {
        mAction = new Action();
        mAction.setGoalId(args.getInt(ARG_GOAL_ID));
      } else {
        mName.setText(mAction.getName());
      }
    } else {
      mAction = new Action();
    }

    setEndDateView(mAction);

    setUpSpinner(view, mAction);

    return view;
  }

  private void setEndDateView(Action action) {
    mEndDateView.setText(action.getDisplayDate(action.getDueDate()));
  }

  public void setDueDate(int year, int month, int day) {
    super.setDueDate(year, month, day);
    mAction.setDueDate(new GregorianCalendar(year, month, day));
    setEndDateView(mAction);
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
    if (mAction !=null) {
      mAction.setName(mName.getText().toString());
      int id = ((Goal)mSpinner.getSelectedItem()).getId();
      mAction.setGoalId(id);
      mAction.save(context);
    }
    NavUtils.navigateUpFromSameTask(getActivity());
  }

  @Override
  public void delete(Context context) {

  }

  @Override
  public int getScrollY() {
    return 0;
  }

  @Override
  public void scrollTo(int y) {

  }

}
