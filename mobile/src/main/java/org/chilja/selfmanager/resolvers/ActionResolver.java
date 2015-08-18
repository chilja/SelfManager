package org.chilja.selfmanager.resolvers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import org.chilja.selfmanager.db.GoalDatabase.ActionEntry;
import org.chilja.selfmanager.model.Action;
import org.chilja.selfmanager.providers.ActionProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by chiljagossow on 8/11/15.
 */
public class ActionResolver {

  private Context mContext;

  private final String[] mProjection = {
          ActionEntry._ID,
          ActionEntry.COL_TEXT,
          ActionEntry.COL_GOAL_ID,
          ActionEntry.COL_DUE_DAY,
          ActionEntry.COL_DUE_MONTH,
          ActionEntry.COL_DUE_YEAR
  };

  public ActionResolver(Context context) {
    mContext = context;
  }

  public void getActions(ActionResolver.ActionCallback callback) {
    ActionTask task = new ActionTask(callback);
    task.execute();
  }

  public void getActions(ActionResolver.ActionCallback callback, int goalId) {
    ActionTask task = new ActionTask(callback);
    task.execute(goalId);
  }

  public void saveAction(final Action action) {
    Runnable task = new Runnable() {
      @Override
      public void run() {
        insertOrUpdateAction(action);
      }
    };
    Thread thread = new Thread(task);
    thread.start();
  }

  public void deleteAction(final Action action) {
    Runnable task = new Runnable() {
      @Override
      public void run() {
      String selection = ActionEntry._ID + "=?";
      String[] selectionArgs = new String[]{Integer.valueOf(action.getId()).toString()};
      mContext.getContentResolver().delete(ActionProvider.CONTENT_URI_ACTION, selection,
              selectionArgs);
      }
    };
    Thread thread = new Thread(task);
    thread.start();
  }

  private ArrayList<Action> getActions(int goalId) {
    String[] selectionArgs = new String[]{Integer.valueOf(goalId).toString()};
    String selection = ActionEntry.COL_GOAL_ID + "=?";
    Cursor cursor = mContext.getContentResolver().query(
            ActionProvider.CONTENT_URI_ACTION, mProjection, selection, selectionArgs, null);
    return getActions(cursor);
  }

  private ArrayList<Action> getActions() {
    Cursor cursor = mContext.getContentResolver().query(ActionProvider.CONTENT_URI_ACTION, mProjection, null, null, null);
    return getActions(cursor);
  }

  private ArrayList<Action> getActions(Cursor cursor) {
    ArrayList<Action> actions = new ArrayList<Action>();
    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      Action action = readCursor(cursor);
      actions.add(action);
      cursor.moveToNext();
    }
    return actions;
  }

  private void insertOrUpdateAction(Action action) {
    ContentValues values = new ContentValues();
    values.put(ActionEntry.COL_TEXT, action.getName());
    values.put(ActionEntry.COL_GOAL_ID, action.getGoalId());
    Calendar endDate = action.getDueDate();
    if (endDate != null) {
      values.put(ActionEntry.COL_DUE_YEAR, endDate.get(Calendar.YEAR));
      values.put(ActionEntry.COL_DUE_MONTH, endDate.get(Calendar.MONTH));
      values.put(ActionEntry.COL_DUE_DAY, endDate.get(Calendar.DAY_OF_MONTH));
    }
    if (action.getId() == 0) {
      mContext.getContentResolver().insert(ActionProvider.CONTENT_URI_ACTION, values);
    } else {
      String[] args = new String[]{Integer.valueOf(action.getId()).toString()};
      String selection = ActionEntry._ID + "=?";
      mContext.getContentResolver().update(ActionProvider.CONTENT_URI_ACTION, values,
              selection, args);
    }
  }

  private Action readCursor(Cursor cursor) {
    Action action = null;
    try {
      action = new Action(cursor.getString(cursor.getColumnIndexOrThrow(
              ActionEntry.COL_TEXT)));
      action.setId(cursor.getInt(cursor.getColumnIndexOrThrow(ActionEntry._ID)));
      action.setGoalId(cursor.getInt(cursor.getColumnIndexOrThrow(ActionEntry.COL_GOAL_ID)));
      int endYear = cursor.getInt(cursor.getColumnIndex(ActionEntry.COL_DUE_YEAR));
      int endMonth = cursor.getInt(cursor.getColumnIndex(ActionEntry.COL_DUE_MONTH));
      int endDay = cursor.getInt(cursor.getColumnIndex(ActionEntry.COL_DUE_DAY));
      action.setDueDate(new GregorianCalendar(endYear, endMonth, endDay));
    } catch (android.database.CursorIndexOutOfBoundsException e) {
      // no acton found - that is ok
    }
    return action;
  }

  public interface ActionCallback {
    void onActionsReturned(ArrayList<Action> actions);
  }

  private class ActionTask extends AsyncTask<Integer, Void, ArrayList<Action>> {
    private ActionCallback mCallback;

    ActionTask(ActionCallback callback) {
      mCallback = callback;
    }
    @Override
    protected ArrayList<Action> doInBackground(Integer... params) {
      if (params != null && params.length > 0) {
        return getActions(params[0]);
      }
      return getActions();
    }

    @Override
    protected void onPostExecute(ArrayList<Action> actions) {
      mCallback.onActionsReturned(actions);
    }
  }
}
