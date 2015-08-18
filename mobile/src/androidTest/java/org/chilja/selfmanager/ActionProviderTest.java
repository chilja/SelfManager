package org.chilja.selfmanager;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;

import org.chilja.selfmanager.db.GoalDatabase;
import org.chilja.selfmanager.model.Action;
import org.chilja.selfmanager.providers.ActionProvider;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by chiljagossow on 8/9/15.
 */
public class ActionProviderTest extends ProviderTestCase2<ActionProvider> {
  /**
   * Constructor.
   */
  public ActionProviderTest() {
    super(ActionProvider.class, ActionProvider.AUTHORITY);
  }

  private ContentResolver contentResolver;

  private Action mAction;

  private Uri mActionUri;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    contentResolver = getMockContentResolver();
    mAction = new Action();
    mAction.setName("Test action");
    mAction.setDueDate(new GregorianCalendar(2015, 11, 10));
    mAction.setGoalId(1);
    mActionUri = insertAction(mAction);
    int id = Integer.valueOf(mActionUri.getLastPathSegment());
    mAction.setId(id);
  }

  public void testRetrieveAction() {
    retrieveAction(mAction, mActionUri);
  }

  public void testUpdateAction() {
    mAction.setName("Updated");
    updateAction(mAction);
    retrieveAction(mAction, mActionUri);
  }

  private void deleteAction(Action action) {
    String selection = GoalDatabase.ActionEntry._ID + "=?";
    String[] selectionArgs = new String[]{Integer.valueOf(action.getId()).toString()};
    contentResolver.delete(ActionProvider.CONTENT_URI_ACTION, selection,
            selectionArgs);
  }

  private Uri insertAction(Action action) {
    ContentValues values = new ContentValues();
    values.put(GoalDatabase.ActionEntry.COL_TEXT, action.getName());
    values.put(GoalDatabase.ActionEntry.COL_GOAL_ID, action.getGoalId());
    Calendar endDate = action.getDueDate();
    if (endDate != null) {
      values.put(GoalDatabase.ActionEntry.COL_DUE_YEAR, endDate.get(Calendar.YEAR));
      values.put(GoalDatabase.ActionEntry.COL_DUE_MONTH, endDate.get(Calendar.MONTH));
      values.put(GoalDatabase.ActionEntry.COL_DUE_DAY, endDate.get(Calendar.DAY_OF_MONTH));
    }
    return contentResolver.insert(ActionProvider.CONTENT_URI_ACTION, values);
  }

  private int updateAction(Action action) {
    ContentValues values = new ContentValues();
    values.put(GoalDatabase.ActionEntry.COL_TEXT, action.getName());
    values.put(GoalDatabase.ActionEntry.COL_GOAL_ID, action.getGoalId());
    Calendar endDate = action.getDueDate();
    if (endDate != null) {
      values.put(GoalDatabase.ActionEntry.COL_DUE_YEAR, endDate.get(Calendar.YEAR));
      values.put(GoalDatabase.ActionEntry.COL_DUE_MONTH, endDate.get(Calendar.MONTH));
      values.put(GoalDatabase.ActionEntry.COL_DUE_DAY, endDate.get(Calendar.DAY_OF_MONTH));
    }
    String[] args = new String[]{Integer.valueOf(action.getId()).toString()};
    String selection = GoalDatabase.ActionEntry._ID + "=?";
    return contentResolver.update(ActionProvider.CONTENT_URI_ACTION, values, selection, args);
  }

  private void retrieveAction(Action action, Uri uri) {
    Cursor cursor = contentResolver.query(uri, null, null, null, null);
    assertEquals(1, cursor.getCount());
    assertTrue(cursor.moveToFirst());

    assertEquals(action.getName(),
            cursor.getString(cursor.getColumnIndex(GoalDatabase.ActionEntry.COL_TEXT)));
    assertEquals(10, cursor.getInt(cursor.getColumnIndex(GoalDatabase.ActionEntry.COL_DUE_DAY)));
    assertEquals(11, cursor.getInt(cursor.getColumnIndex(GoalDatabase.ActionEntry.COL_DUE_MONTH)));
    assertEquals(2015, cursor.getInt(cursor.getColumnIndex(GoalDatabase.ActionEntry.COL_DUE_YEAR)));
    assertEquals(1, cursor.getInt(cursor.getColumnIndex(GoalDatabase.ActionEntry.COL_GOAL_ID)));
    int id = cursor.getInt(cursor.getColumnIndex(GoalDatabase.ActionEntry._ID));
    action.setId(id);
    cursor.close();
  }

  public void tearDown() {
    deleteAction(mAction);
  }

}
