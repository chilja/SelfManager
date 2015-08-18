package org.chilja.selfmanager.presenter.edit;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.chilja.selfmanager.R;
import org.chilja.selfmanager.presenter.base.BaseActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditTextFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditTextFragment extends EditFragment {

  public static final String TAG = "EditTextFragment";

  private EditText mEditText;
  private EditText mName;

  protected Editable mEditableItem;

  public Editable getEditableItem() {
    return mEditableItem;
  }

  public void setEditableItem(Editable mEditableItem) {
    this.mEditableItem = mEditableItem;
  }

  public EditTextFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @return A new instance of fragment EditChiefAimFragment.
   */

  public static EditTextFragment newInstance(Editable editable, String title) {
    EditTextFragment fragment = new EditTextFragment();
    fragment.setEditableItem(editable);
    fragment.setTitle(title);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_edit_text, container, false);
    mEditText = (EditText) view.findViewById(R.id.edit_text);
    mName = (EditText) view.findViewById(R.id.name);
    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
    mEditText.setText(mEditableItem.getContent());
  }

  public void save(Context context) {
    mEditableItem.setContent(mEditText.getText().toString());
    mEditableItem.setTitle(mName.getText().toString());
    mEditableItem.saveToDb(context);
    NavUtils.navigateUpFromSameTask(getActivity());
  }

  @Override
  public void delete(Context context) {
    mEditableItem.deleteFromDb(context);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (activity instanceof BaseActivity)
      ((BaseActivity)activity).getSupportActionBar().setTitle(getTitle());
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  @Override
  public String getTAG() {
    return TAG;
  }

  @Override
  public int getScrollY() {
    return 0;
  }

  @Override
  public void scrollTo(int y) {

  }
}
