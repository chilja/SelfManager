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
import org.chilja.selfmanager.model.Goal;
import org.chilja.selfmanager.model.Note;
import org.chilja.selfmanager.resolvers.GoalResolver;
import org.chilja.selfmanager.presenter.base.BaseActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditNoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditNoteFragment extends EditItemFragment {

  public static final String TAG = "EditTextFragment";
  public static final String ARG_NOTE = "note";
  public static final String ARG_TITLE = "title";

  private EditText mEditText;
  private EditText mName;

  protected Note mNote;

  public Editable getEditableItem() {
    return mNote;
  }

  public void setNote(Note note) {
    this.mNote = note;
  }

  public EditNoteFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @return A new instance of fragment EditChiefAimFragment.
   */

  public static EditNoteFragment newInstance(Note note, String title) {
    EditNoteFragment fragment = new EditNoteFragment();
    Bundle args = new Bundle();
    args.putParcelable(ARG_NOTE, note);
    args.putString(ARG_TITLE, title);
    fragment.setArguments(args);
    fragment.setTitle(title);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle args = getArguments();
    if (args != null) {
      mNote = args.getParcelable(ARG_NOTE);
    }
    if (mNote == null)
      mNote = new Note("New note");
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_edit_note, container, false);
    mEditText = (EditText) view.findViewById(R.id.edit_text);
    mName = (EditText) view.findViewById(R.id.name);
    setUpSpinner(view, mNote);
    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
    mEditText.setText(mNote.getContent());
    mName.setText(mNote.getName());
  }

  public void save(Context context) {
    mNote.setText(mEditText.getText().toString());
    mNote.setName(mName.getText().toString());
    int id = ((Goal)mSpinner.getSelectedItem()).getId();
    mNote.setGoalId(id);
    mNote.saveToDb(context);
    NavUtils.navigateUpFromSameTask(getActivity());
  }

  @Override
  public void delete(Context context) {
    mNote.deleteFromDb(context);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    new GoalResolver(activity).getGoals(this);
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
