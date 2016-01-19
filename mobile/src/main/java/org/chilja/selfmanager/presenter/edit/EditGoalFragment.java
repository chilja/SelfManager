package org.chilja.selfmanager.presenter.edit;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.chilja.selfmanager.R;
import org.chilja.selfmanager.model.Goal;
import org.chilja.selfmanager.resolvers.GoalResolver;
import org.chilja.selfmanager.util.BitmapUtility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditGoalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditGoalFragment extends EditFragment implements GoalResolver.GoalCallback {

  public static final String TAG = "EditGoalFragment";
  private static final int PICK_IMAGE = 100;
  public static final String ARG_GOAL_ID = "arg_goal_id";
  public static final String ARG_TITLE = "arg_title";

  private TextView mDueDateView;
  private EditText mGoalName;
  private EditText mMotivation;
  private EditText mDefinitionDone;
  private ImageView mImageView;

  private Bitmap mGoalBitmap;
  private String mGoalImagePath;
  private int mGoalId;
  private Goal mGoal;


  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @return A new instance of fragment EditGoalFragment.
   */

  public static EditGoalFragment newInstance(String title) {
    EditGoalFragment fragment = new EditGoalFragment();
    Bundle args = new Bundle();
    args.putString(ARG_TITLE, title);
    fragment.setArguments(args);
    fragment.setTitle(title);
    return fragment;
  }

  public static EditGoalFragment newInstance(int goalId, String title) {
    EditGoalFragment fragment = new EditGoalFragment();
    Bundle args = new Bundle();
    args.putInt(ARG_GOAL_ID, goalId);
    args.putString(ARG_TITLE, title);
    fragment.setArguments(args);
    fragment.setTitle(title);
    return fragment;
  }

  public EditGoalFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_edit_goal, container, false);
    mGoalName = (EditText) view.findViewById(R.id.goal_name);
    mMotivation = (EditText) view.findViewById(R.id.motivation);
    mDefinitionDone = (EditText) view.findViewById(R.id.definition_done);
    mImageView = (ImageView) view.findViewById(R.id.image);
    mImageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onPickImageRequest();
      }
    });
    mDueDateView = (TextView) view.findViewById(R.id.end_date);

    setEndDateView();

    return view;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (getArguments() != null) {
      mTitle = getArguments().getString(ARG_TITLE);
      mGoalId = getArguments().getInt(ARG_GOAL_ID);
      if (mGoalId != 0) {
        new GoalResolver(activity).getGoal(this, mGoalId);
      }
      else {
        mGoal = new Goal();
        setGoalData(mGoal);
      }
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    setGoalData(mGoal);
  }

  private void setGoalData(Goal goal) {
    if (mIsAttached && mIsResumed && goal != null) {
      if (mGoalBitmap != null) {
        mImageView.setImageBitmap(mGoalBitmap);
        Log.d(TAG, "image bitmap");
      } else if (goal.getImage() != null){
        BitmapUtility.loadBitmap(getActivity(), new File(goal.getImage()), mImageView, mDisplayWidth,
                mDisplayWidth * 9 / 16);
        Log.d(TAG, "image from file");
      }
      mGoalName.setText(goal.getName());
      mMotivation.setText(goal.getMotivation());
      mDefinitionDone.setText(goal.getDefinitionDone());
      mDueDateView.setText(goal.getDisplayDate(goal.getDueDate()));
      mGoalImagePath = goal.getImage();
      mDueDate = goal.getDueDate();
    }
  }

  private void setEndDateView() {
    mDueDateView.setText(mDateFormat.format(mDueDate.getTime()));
  }

  public void setDueDate(int year, int month, int day) {
    super.setDueDate(year, month, day);
    setEndDateView();
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.i(TAG, "result for code " + requestCode + " " + resultCode);
    if (requestCode == PICK_IMAGE ) {
      if (resultCode != Activity.RESULT_OK || data == null) {
        Toast.makeText(getActivity(), "No image returned", Toast.LENGTH_SHORT);
        return;
      }
      try {
        InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
        mGoalBitmap = BitmapFactory.decodeStream(inputStream);
        mImageView.setImageBitmap(mGoalBitmap);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
        Log.e(TAG, "file not found");
      }
    }
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

    if (checkObligatoryFields(context)) {
      Goal goal = new Goal(mGoalName.getText().toString());
      goal.setId(mGoalId);
      goal.setDueDate(mDueDate);
      goal.setMotivation(mMotivation.getText().toString());
      goal.setDefinitionDone(mDefinitionDone.getText().toString());
      goal.setImage(mGoalImagePath);

      if (mGoalBitmap != null) {
        goal.saveBitmap(getActivity(), mGoalBitmap);
      }
      goal.save(context);
      NavUtils.navigateUpFromSameTask(getActivity());
    }
  }

  private boolean checkObligatoryFields(Context context) {
    if (mGoalName.getText().toString().isEmpty()) {
      Toast.makeText(context, "Please enter a name", Toast.LENGTH_SHORT).show();
      return false;
    }
    if (mMotivation.getText().toString().isEmpty()) {
      Toast.makeText(context, "Please enter a motivation", Toast.LENGTH_SHORT).show();
      return false;
    }
    if (mDefinitionDone.getText().toString().isEmpty()){
      Toast.makeText(context, "Please enter a definition of done", Toast.LENGTH_SHORT).show();
      return false;
    }
    return true;
  }

  @Override
  public void delete(Context context) {
    if (mGoal != null) {
      mGoal.delete(context);
    }
    NavUtils.navigateUpFromSameTask(getActivity());
  }

  @Override
  public int getScrollY() {
    return 0;
  }

  @Override
  public void scrollTo(int y) {

  }

  public void onPickImageRequest() {
    Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
    getIntent.setType("image/*");

    Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    pickIntent.setType("image/*");

    Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

    startActivityForResult(chooserIntent, PICK_IMAGE);
  }

  @Override
  public void onGoalsReturned(ArrayList<Goal> goals) {
    if (goals != null && goals.size() > 0){
      mGoal = goals.get(0);
      setGoalData(mGoal);
    }
  }

}
