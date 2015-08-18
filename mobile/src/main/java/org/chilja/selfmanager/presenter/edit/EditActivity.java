package org.chilja.selfmanager.presenter.edit;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Outline;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageButton;

import org.chilja.selfmanager.R;
import org.chilja.selfmanager.model.Action;
import org.chilja.selfmanager.model.Mission;
import org.chilja.selfmanager.model.Note;
import org.chilja.selfmanager.model.WaitItem;
import org.chilja.selfmanager.presenter.base.BaseActivity;

/**
 * Created by chiljagossow on 6/13/15.
 */
public class EditActivity extends BaseActivity
        implements EditFragment.OnFragmentInteractionListener, DatePickerFragment.OnFragmentInteractionListener,
EditWaitItemFragment.OnFragmentInteractionListener{

  public static final String TAG = "EditActivity";

  private static final String MISSION_TAG = "Mission";

  public static final String TYPE = "edit_entity_type";

  public static final String ARG_GOAL_ID = "arg_goal_id";
  public static final String ARG_ACTION = "arg_action";
  public static final String ARG_NOTE = "arg_note";
  public static final String ARG_WAIT_ITEM = "arg_wait_item";

  public static final int MISSION = 1;
  public static final int NEW_GOAL = 2;
  public static final int EDIT_GOAL = 3;
  public static final int NEW_ACTION = 4;
  public static final int NEW_GOAL_ACTION = 5;
  public static final int EDIT_ACTION = 6;
  public static final int NEW_WAIT_ITEM = 7;
  public static final int EDIT_WAIT_ITEM = 8;
  public static final int NEW_NOTE = 9;
  public static final int EDIT_NOTE = 10;

  public EditFragment getEditFragment() {
    return mEditFragment;
  }

  public void setEditFragment(EditFragment editFragment) {
    this.mEditFragment = editFragment;
  }

  private EditFragment mEditFragment;
  private ImageButton mSaveButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit);

    setUp();
    setUpToolbar();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    mSaveButton = (ImageButton) findViewById(R.id.button_save);
    mSaveButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mEditFragment.save(EditActivity.this);
      }
    });
    mSaveButton.setClipToOutline(true);
    final int buttonDiameter = getResources().getDimensionPixelSize(R.dimen.diameter);
    mSaveButton.setOutlineProvider(new ViewOutlineProvider() {
      @TargetApi(Build.VERSION_CODES.LOLLIPOP)
      @Override
      public void getOutline(View view, Outline outline) {
        outline.setOval(0, 0, buttonDiameter, buttonDiameter);
      }
    });
  }

  @Override
  public void onResume() {
    super.onResume();
    mEditFragment = getFragmentInstance(getIntent());
    showFragment(mEditFragment);
  }

  public void showFragment(EditFragment fragment) {
    if (fragment != null) {
      FragmentManager fragmentManager = getSupportFragmentManager();
      fragmentManager.beginTransaction()
              .replace(R.id.container, fragment, fragment.getTAG())
              .commit();
      getSupportActionBar().setTitle(fragment.getTitle());
    }
  }

  public EditFragment getFragmentInstance(Intent intent) {
    if (intent != null) {

      FragmentManager fragmentManager = getSupportFragmentManager();
      int goalId = intent.getIntExtra(ARG_GOAL_ID, 0);
      int type = intent.getIntExtra(TYPE, 0);
      EditFragment fragment;

      switch (type) {

        case MISSION:
          fragment = (EditFragment) fragmentManager.findFragmentByTag(MISSION_TAG);
          if (fragment == null)
            fragment = EditTextFragment.newInstance(new Mission(this), getString(R.string.title_edit_mission));
          break;

        case NEW_GOAL:
          fragment = (EditFragment) fragmentManager.findFragmentByTag(EditGoalFragment.TAG);
          if (fragment == null) {
            fragment = EditGoalFragment.newInstance(getString(R.string.title_new_goal));
          }
          break;

        case EDIT_GOAL:
          fragment = (EditFragment) fragmentManager.findFragmentByTag(EditGoalFragment.TAG);
          if (fragment == null) {
            fragment = EditGoalFragment.newInstance(goalId, getString(R.string.title_edit_goal));
          }
          break;

        case NEW_ACTION:
          fragment = (EditFragment) fragmentManager.findFragmentByTag(EditActionFragment.TAG);
          if (fragment == null)
            fragment = EditActionFragment.newInstance(getString(R.string.title_new_action));
          break;

        case EDIT_ACTION:
          Action action = intent.getParcelableExtra(ARG_ACTION);
          fragment = EditActionFragment.newInstance(action, getString(R.string.title_edit_action));
          break;

        case NEW_GOAL_ACTION:
          fragment = EditActionFragment.newInstance(goalId, getString(R.string.title_new_action));
          break;

        case NEW_WAIT_ITEM:
          WaitItem newItem = new WaitItem();
          newItem.setGoalId(goalId);
                  fragment = EditWaitItemFragment.newInstance(newItem,
                  getString(R.string.title_edit_wait_item));
          break;

        case EDIT_WAIT_ITEM:
          WaitItem item = intent.getParcelableExtra(ARG_WAIT_ITEM);
          fragment = EditWaitItemFragment.newInstance(item,
                  getString(R.string.title_edit_wait_item));
          break;

        case NEW_NOTE:
          Note note = new Note();
          note.setGoalId(goalId);
          fragment = EditNoteFragment.newInstance(note, getString(R.string.title_new_note));
          break;

        case EDIT_NOTE:
          Note editNote = intent.getParcelableExtra(ARG_NOTE);
          fragment = EditNoteFragment.newInstance(editNote, getString(R.string.title_edit_note));
          break;

        default:
          fragment = null;

      }
      return fragment;
    }
    return null;
  }

  public void showDatePickerDialog(int type) {
    DialogFragment newFragment = DatePickerFragment.newInstance(type);
    String tag = "datePicker";
    newFragment.show(getSupportFragmentManager(), tag);
  }

  @Override
  public void onEndDatePickerCall(View view) {
    showDatePickerDialog(DatePickerFragment.DUE_DATE);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.edit_activity, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    if (id == R.id.action_delete) {
      mEditFragment.delete(this);
      NavUtils.navigateUpFromSameTask(this);
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onDueDateSet(int year, int month, int day) {
    mEditFragment.setDueDate(year, month, day);
  }

  @Override
  public void onRequestDateSet(int year, int month, int day) {
    mEditFragment.setRequestDate(year, month, day);
  }


  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    mEditFragment.onActivityResult(requestCode, resultCode, data);
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onRequestDatePickerCall(View view) {
    showDatePickerDialog(DatePickerFragment.REQUEST_DATE);
  }
}
