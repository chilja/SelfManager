package org.chilja.selfmanager.presenter.items;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.chilja.selfmanager.R;
import org.chilja.selfmanager.model.Event;
import org.chilja.selfmanager.model.Note;
import org.chilja.selfmanager.presenter.base.BaseActivity;
import org.chilja.selfmanager.presenter.edit.EditActionFragment;
import org.chilja.selfmanager.presenter.edit.EditActivity;
import org.chilja.selfmanager.presenter.edit.EditDetailActivity;

/**
 * Created by chiljagossow on 6/13/15.
 */
public class DetailActivity extends BaseActivity implements GoalDetailFragment.OnFragmentInteractionListener{

  public static final String ARG_GOAL_ID = "arg_goal_id";
  public static final String ARG_EVENT_ID = "arg_event_id";
  public static final int RC_EVENT = 100;

  private int mGoalId;
  private long mEventID;

  GoalDetailFragment mFragment;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail);

    setUp();
    setUpToolbar();

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    mGoalId = getPreferences().getInt(ARG_GOAL_ID, -1);

    if (getIntent() != null) {
      mGoalId = getIntent().getIntExtra(ARG_GOAL_ID, mGoalId);
    }

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.goal_detail, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    if (id == R.id.action_edit_goal) {
      final Intent intent = new Intent(this, EditActivity.class);
      intent.putExtra(EditActivity.TYPE, EditActivity.EDIT_GOAL);
      intent.putExtra(EditActivity.ARG_GOAL_ID, mGoalId );
      startActivity(intent);
      return true;
    }

    if (id == R.id.action_delete) {
      mFragment.delete(this);
      NavUtils.navigateUpFromSameTask(this);
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onStop() {
    saveIds();
    super.onStop();
  }

  private void saveIds() {
    SharedPreferences preferences = getPreferences();
    SharedPreferences.Editor editor = preferences.edit();
    editor.putLong(ARG_EVENT_ID, mEventID);
    editor.putInt(ARG_GOAL_ID, mGoalId);
    editor.commit();
  }

  public long getEventID() {
    return mEventID;
  }

  public int getGoalId() {
    return mGoalId;
  }

  @Override
  public void onResume() {
    super.onResume();
    showFragment();
    SharedPreferences preferences = getPreferences();
    mEventID = preferences.getLong(ARG_EVENT_ID, mEventID);
    long prev_id = getLastEventId(getContentResolver());

    // if prev_id == mEventId, means there is new events created
    // and we need to insert new events into local sqlite database.
    if (prev_id == mEventID) {
      // do database insert
      setEventId(mEventID, this);
      mEventID = 0;
    }

  }

  private SharedPreferences getPreferences() {
    return getSharedPreferences(getString(R.string.preference_main), Context.MODE_PRIVATE);
  }

  public void setEventId(long id, Context context) {
    Event event = new Event();
    event.setEventId(id);
    event.setGoalId(mGoalId);
    event.save(context);
  }

  private GoalDetailFragment showFragment() {
    FragmentManager fragmentManager = getSupportFragmentManager();
    mFragment = GoalDetailFragment.newInstance(mGoalId);
    fragmentManager.beginTransaction().replace(R.id.container, mFragment, mFragment.getTAG()).commit();
    return mFragment;
  }

  public void onAddActionClick(View view) {
    final Intent intent = new Intent(this, EditDetailActivity.class);
    intent.putExtra(EditActivity.TYPE, EditActivity.NEW_GOAL_ACTION);
    intent.putExtra(EditActionFragment.ARG_GOAL_ID, mGoalId);
    startActivity(intent);
  }

  public void onAddWaitItemClick(View view) {
    final Intent intent = new Intent(this, EditDetailActivity.class);
    intent.putExtra(EditActivity.TYPE, EditActivity.NEW_WAIT_ITEM);
    intent.putExtra(EditActionFragment.ARG_GOAL_ID, mGoalId);
    startActivity(intent);
  }

  public void onAddEventClick(View view) {
    mEventID = getNewEventId(getContentResolver());
    Intent intent = new Intent(Intent.ACTION_INSERT);
    intent.setData(CalendarContract.Events.CONTENT_URI);
    intent.putExtra(CalendarContract.Events._ID, mEventID);
    intent.putExtra("title", "Next action");
    startActivityForResult(intent, RC_EVENT);
  }

  public void onAddNoteClick(View view) {
    final Intent intent = new Intent(this, EditDetailActivity.class);
    intent.putExtra(EditActivity.TYPE, EditActivity.NEW_NOTE);
    intent.putExtra(EditActionFragment.ARG_GOAL_ID, mGoalId);
    startActivity(intent);
  }

  public static long getNewEventId(ContentResolver cr) {
    Cursor cursor = cr.query(CalendarContract.Events.CONTENT_URI, new String [] {"MAX(_id) as max_id"}, null, null, "_id");
    cursor.moveToFirst();
    long max_val = cursor.getLong(cursor.getColumnIndex("max_id"));
    return max_val+1;
  }

  public static long getLastEventId(ContentResolver cr) {
    Cursor cursor = cr.query(CalendarContract.Events.CONTENT_URI, new String [] {"MAX(_id) as max_id"}, null, null, "_id");
    cursor.moveToFirst();
    long max_val = cursor.getLong(cursor.getColumnIndex("max_id"));
    return max_val;
  }

  @Override
  public void onEditNote(Note note) {
    final Intent intent = new Intent(this, EditDetailActivity.class);
    intent.putExtra(EditActivity.TYPE, EditActivity.EDIT_NOTE);
    intent.putExtra(EditActivity.ARG_NOTE, note);
    startActivity(intent);
  }
}
