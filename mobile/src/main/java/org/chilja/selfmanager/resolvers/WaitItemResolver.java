package org.chilja.selfmanager.resolvers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import org.chilja.selfmanager.db.GoalDatabase;
import org.chilja.selfmanager.model.WaitItem;
import org.chilja.selfmanager.providers.WaitItemProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by chiljagossow on 8/13/15.
 */
public class WaitItemResolver {

  private Context mContext;

  private final String[] mProjection = {
          GoalDatabase.WaitItemEntry._ID,
          GoalDatabase.WaitItemEntry.COL_TEXT,
          GoalDatabase.WaitItemEntry.COL_GOAL_ID,
          GoalDatabase.WaitItemEntry.COL_DUE_DAY,
          GoalDatabase.WaitItemEntry.COL_DUE_MONTH,
          GoalDatabase.WaitItemEntry.COL_DUE_YEAR,
          GoalDatabase.WaitItemEntry.COL_REQUEST_DAY,
          GoalDatabase.WaitItemEntry.COL_REQUEST_MONTH,
          GoalDatabase.WaitItemEntry.COL_REQUEST_YEAR,
          GoalDatabase.WaitItemEntry.COL_RESPONSIBLE
  };

  public WaitItemResolver(Context context) {
    mContext = context;
  }

  public void saveWaitItem(final WaitItem item) {
    Runnable task = new Runnable() {
      @Override
      public void run() {
        insertItem(item);
      }
    };
    new Thread(task).start();
  }

  public void deleteWaitItem(final WaitItem item) {
    Runnable task = new Runnable() {
      @Override
      public void run() {
        String selection = GoalDatabase.WaitItemEntry._ID + "=?";
        String[] selectionArgs = new String[]{Integer.valueOf(item.getId()).toString()};
        mContext.getContentResolver().delete(WaitItemProvider.CONTENT_URI, selection,
                selectionArgs);
      }
    };
    Thread thread = new Thread(task);
    thread.start();
  }

  public void getWaitItems(WaitItemCallback callback) {
    WaitItemTask task = new WaitItemTask(callback);
    task.execute();
  }

  public void getWaitItems(WaitItemCallback callback, int goalId) {
    WaitItemTask task = new WaitItemTask(callback);
    task.execute(goalId);
  }

  private ArrayList<WaitItem> getItems(int goalId) {
    String[] selectionArgs = new String[]{Integer.valueOf(goalId).toString()};
    String selection = GoalDatabase.WaitItemEntry.COL_GOAL_ID + "=?";
    Cursor cursor = mContext.getContentResolver().query(
            WaitItemProvider.CONTENT_URI, mProjection, selection, selectionArgs, null);
    return getItems(cursor);
  }

  private ArrayList<WaitItem> getItems() {
    Cursor cursor = mContext.getContentResolver().query(
            WaitItemProvider.CONTENT_URI, mProjection, null, null, null);
    return getItems(cursor);
  }

  private ArrayList<WaitItem> getItems(Cursor cursor) {
    ArrayList<WaitItem> items = new ArrayList<WaitItem>();
    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      WaitItem item = readCursor(cursor);
      items.add(item);
      cursor.moveToNext();
    }
    return items;
  }

  private void insertItem(WaitItem waitItem) {
    ContentValues values = new ContentValues();
    values.put(GoalDatabase.WaitItemEntry.COL_TEXT, waitItem.getName());
    values.put(GoalDatabase.WaitItemEntry.COL_GOAL_ID, waitItem.getGoalId());
    values.put(GoalDatabase.WaitItemEntry.COL_RESPONSIBLE, waitItem.getResponsible());
    Calendar dueDate = waitItem.getDueDate();
    if (dueDate != null) {
      values.put(GoalDatabase.WaitItemEntry.COL_DUE_YEAR, dueDate.get(Calendar.YEAR));
      values.put(GoalDatabase.WaitItemEntry.COL_DUE_MONTH, dueDate.get(Calendar.MONTH));
      values.put(GoalDatabase.WaitItemEntry.COL_DUE_DAY, dueDate.get(Calendar.DAY_OF_MONTH));
    }
    Calendar requestDate = waitItem.getRequestDate();
    if (requestDate != null) {
      values.put(GoalDatabase.WaitItemEntry.COL_REQUEST_YEAR, requestDate.get(Calendar.YEAR));
      values.put(GoalDatabase.WaitItemEntry.COL_REQUEST_MONTH, requestDate.get(Calendar.MONTH));
      values.put(GoalDatabase.WaitItemEntry.COL_REQUEST_DAY, requestDate.get(Calendar.DAY_OF_MONTH));
    }

    if (waitItem.getId() == 0) {
      mContext.getContentResolver().insert(WaitItemProvider.CONTENT_URI, values);
    } else {
      String selection = GoalDatabase.WaitItemEntry._ID + "=?";
      String[] args = new String[]{Integer.valueOf(waitItem.getId()).toString()};
      mContext.getContentResolver().update(WaitItemProvider.CONTENT_URI, values, selection,
              args);

    }
  }

  protected WaitItem readCursor(Cursor cursor) {
    WaitItem item = null;
    try {
      item = new WaitItem(cursor.getString(cursor.getColumnIndexOrThrow(
              GoalDatabase.WaitItemEntry.COL_TEXT)));
      item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(GoalDatabase.WaitItemEntry._ID)));
      item.setGoalId(cursor.getInt(
              cursor.getColumnIndexOrThrow(GoalDatabase.WaitItemEntry.COL_GOAL_ID)));
      item.setResponsible(cursor.getString(cursor.getColumnIndexOrThrow(
              GoalDatabase.WaitItemEntry.COL_RESPONSIBLE)));
      int dueYear = cursor.getInt(cursor.getColumnIndex(GoalDatabase.WaitItemEntry.COL_DUE_YEAR));
      int dueMonth = cursor.getInt(cursor.getColumnIndex(GoalDatabase.WaitItemEntry.COL_DUE_MONTH));
      int dueDay = cursor.getInt(cursor.getColumnIndex(GoalDatabase.WaitItemEntry.COL_DUE_DAY));
      item.setDueDate(new GregorianCalendar(dueYear, dueMonth, dueDay));
      int requestYear = cursor.getInt(cursor.getColumnIndex(GoalDatabase.WaitItemEntry.COL_REQUEST_YEAR));
      int requestMonth = cursor.getInt(cursor.getColumnIndex(GoalDatabase.WaitItemEntry.COL_REQUEST_MONTH));
      int requestDay = cursor.getInt(cursor.getColumnIndex(GoalDatabase.WaitItemEntry.COL_REQUEST_DAY));
      item.setRequestDate(new GregorianCalendar(requestYear, requestMonth, requestDay));
    } catch (android.database.CursorIndexOutOfBoundsException e) {
      // nothing found - that is ok
    }
    return item;
  }

  public interface WaitItemCallback {
    void onWaitItemsReturned(ArrayList<WaitItem> items);
  }

  private class WaitItemTask extends AsyncTask<Integer, Void, ArrayList<WaitItem>> {
    private WaitItemCallback mCallback;

    WaitItemTask(WaitItemCallback callback) {
      mCallback = callback;
    }
    @Override
    protected ArrayList<WaitItem> doInBackground(Integer... params) {
      if (params != null && params.length > 0) {
        return getItems(params[0]);
      }
      return getItems();
    }

    @Override
    protected void onPostExecute(ArrayList<WaitItem> waitItems) {
      mCallback.onWaitItemsReturned(waitItems);
    }
  }

}
