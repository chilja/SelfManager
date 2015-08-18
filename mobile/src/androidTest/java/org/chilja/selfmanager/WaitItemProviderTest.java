package org.chilja.selfmanager;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;

import org.chilja.selfmanager.db.GoalDatabase;
import org.chilja.selfmanager.model.WaitItem;
import org.chilja.selfmanager.providers.WaitItemProvider;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by chiljagossow on 8/9/15.
 */
public class WaitItemProviderTest extends ProviderTestCase2<WaitItemProvider> {
  /**
   * Constructor.
   */
  public WaitItemProviderTest() {
    super(WaitItemProvider.class, WaitItemProvider.AUTHORITY);
  }

  private ContentResolver contentResolver;

  private WaitItem mWaitItem;

  private Uri mUri;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    contentResolver = getMockContentResolver();
    mWaitItem = new WaitItem();
    mWaitItem.setName("Test wait item");
    mWaitItem.setDueDate(new GregorianCalendar(2015, 11, 10));
    mWaitItem.setRequestDate(new GregorianCalendar(2015, 1, 1));
    mWaitItem.setGoalId(1);
    mWaitItem.setResponsible("me");
    mUri = insert(mWaitItem);
    assertNotNull(mUri);
    int id = Integer.valueOf(mUri.getLastPathSegment());
    mWaitItem.setId(id);
  }

  public void testRetrieve() {
    retrieve(mWaitItem, mUri);
  }

  public void testUpdate() {
    mWaitItem.setName("Updated");
    update(mWaitItem);
    retrieve(mWaitItem, mUri);
  }

  private void delete(WaitItem item) {
    String selection = GoalDatabase.WaitItemEntry._ID + "=?";
    String[] selectionArgs = new String[]{Integer.valueOf(item.getId()).toString()};
    contentResolver.delete(WaitItemProvider.CONTENT_URI, selection,
            selectionArgs);
  }

  private Uri insert(WaitItem item) {
    ContentValues values = new ContentValues();
    values.put(GoalDatabase.WaitItemEntry.COL_TEXT, item.getName());
    values.put(GoalDatabase.WaitItemEntry.COL_RESPONSIBLE, item.getResponsible());
    values.put(GoalDatabase.WaitItemEntry.COL_GOAL_ID, item.getGoalId());
    Calendar endDate = item.getDueDate();
    if (endDate != null) {
      values.put(GoalDatabase.WaitItemEntry.COL_DUE_YEAR, endDate.get(Calendar.YEAR));
      values.put(GoalDatabase.WaitItemEntry.COL_DUE_MONTH, endDate.get(Calendar.MONTH));
      values.put(GoalDatabase.WaitItemEntry.COL_DUE_DAY, endDate.get(Calendar.DAY_OF_MONTH));
    }
    Calendar requestDate = item.getRequestDate();
    if (requestDate != null) {
      values.put(GoalDatabase.WaitItemEntry.COL_REQUEST_YEAR, requestDate.get(Calendar.YEAR));
      values.put(GoalDatabase.WaitItemEntry.COL_REQUEST_MONTH, requestDate.get(Calendar.MONTH));
      values.put(GoalDatabase.WaitItemEntry.COL_REQUEST_DAY, requestDate.get(Calendar.DAY_OF_MONTH));
    }
    return contentResolver.insert(WaitItemProvider.CONTENT_URI, values);
  }

  private int update(WaitItem item) {
    ContentValues values = new ContentValues();
    values.put(GoalDatabase.WaitItemEntry.COL_TEXT, item.getName());
    values.put(GoalDatabase.WaitItemEntry.COL_GOAL_ID, item.getGoalId());
    values.put(GoalDatabase.WaitItemEntry.COL_RESPONSIBLE, item.getResponsible());
    Calendar endDate = item.getDueDate();
    if (endDate != null) {
      values.put(GoalDatabase.WaitItemEntry.COL_DUE_YEAR, endDate.get(Calendar.YEAR));
      values.put(GoalDatabase.WaitItemEntry.COL_DUE_MONTH, endDate.get(Calendar.MONTH));
      values.put(GoalDatabase.WaitItemEntry.COL_DUE_DAY, endDate.get(Calendar.DAY_OF_MONTH));
    }
    Calendar requestDate = item.getRequestDate();
    if (requestDate != null) {
      values.put(GoalDatabase.WaitItemEntry.COL_REQUEST_YEAR, requestDate.get(Calendar.YEAR));
      values.put(GoalDatabase.WaitItemEntry.COL_REQUEST_MONTH, requestDate.get(Calendar.MONTH));
      values.put(GoalDatabase.WaitItemEntry.COL_REQUEST_DAY, requestDate.get(Calendar.DAY_OF_MONTH));
    }
    String[] args = new String[]{Integer.valueOf(item.getId()).toString()};
    String selection = GoalDatabase.WaitItemEntry._ID + "=?";
    return contentResolver.update(WaitItemProvider.CONTENT_URI, values, selection, args);
  }

  private void retrieve(WaitItem item, Uri uri) {
    Cursor cursor = contentResolver.query(uri, null, null, null, null);
    assertEquals(1, cursor.getCount());
    assertTrue(cursor.moveToFirst());

    assertEquals(item.getName(),
            cursor.getString(cursor.getColumnIndex(GoalDatabase.WaitItemEntry.COL_TEXT)));
    assertEquals(item.getResponsible(),
            cursor.getString(cursor.getColumnIndex(GoalDatabase.WaitItemEntry.COL_RESPONSIBLE)));
    assertEquals(item.getDueDate().get(Calendar.DAY_OF_MONTH),
            cursor.getInt(cursor.getColumnIndex(GoalDatabase.WaitItemEntry.COL_DUE_DAY)));
    assertEquals(
            item.getDueDate().get(Calendar.MONTH),
            cursor.getInt(cursor.getColumnIndex(GoalDatabase.WaitItemEntry.COL_DUE_MONTH)));
    assertEquals(
            item.getDueDate().get(Calendar.YEAR),
            cursor.getInt(cursor.getColumnIndex(GoalDatabase.WaitItemEntry.COL_DUE_YEAR)));
    assertEquals(
            item.getRequestDate().get(Calendar.DAY_OF_MONTH),
            cursor.getInt(cursor.getColumnIndex(GoalDatabase.WaitItemEntry.COL_REQUEST_DAY)));
    assertEquals(
            item.getRequestDate().get(Calendar.MONTH),
            cursor.getInt(cursor.getColumnIndex(GoalDatabase.WaitItemEntry.COL_REQUEST_MONTH)));
    assertEquals(
            item.getRequestDate().get(Calendar.YEAR),
            cursor.getInt(cursor.getColumnIndex(GoalDatabase.WaitItemEntry.COL_REQUEST_YEAR)));
    assertEquals(
            item.getGoalId(),
            cursor.getInt(cursor.getColumnIndex(GoalDatabase.WaitItemEntry.COL_GOAL_ID)));
    int id = cursor.getInt(cursor.getColumnIndex(GoalDatabase.WaitItemEntry._ID));
    item.setId(id);
    cursor.close();
  }

  public void tearDown() {
    delete(mWaitItem);
  }

}
