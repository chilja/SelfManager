package org.chilja.selfmanager.resolvers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import org.chilja.selfmanager.db.GoalDatabase;
import org.chilja.selfmanager.model.Goal;
import org.chilja.selfmanager.providers.GoalProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by chiljagossow on 8/13/15.
 */
public class GoalResolver {

  private Context mContext;

  private final String[] mProjection = {
          GoalDatabase.GoalEntry._ID,
          GoalDatabase.GoalEntry.NAME,
          GoalDatabase.GoalEntry.IMAGE,
          GoalDatabase.GoalEntry.DUE_DAY,
          GoalDatabase.GoalEntry.DUE_MONTH,
          GoalDatabase.GoalEntry.DUE_YEAR,
          GoalDatabase.GoalEntry.MOTIVATION,
          GoalDatabase.GoalEntry.DEFINITION_DONE
  };

  public GoalResolver(Context context) {
    mContext = context;
  }

  public void getGoals(GoalCallback callback) {
    GoalTask task = new GoalTask(callback);
    task.execute();
  }

  public void getGoal(GoalCallback callback, int id) {
    GoalTask task = new GoalTask(callback);
    task.execute(id);
  }

  public void insertGoal(final Goal goal) {
    Runnable task = new Runnable() {
      @Override
      public void run() {
        insertOrUpdateGoal(goal);
      }
    };
    Thread thread = new Thread(task);
    thread.start();
  }

  public void deleteGoal(final int id) {
    Runnable task = new Runnable() {
      @Override
      public void run() {
        String selection = GoalDatabase.GoalEntry._ID + "=?";
        String[] selectionArgs = new String[]{Integer.valueOf(id).toString()};
        mContext.getContentResolver().delete(GoalProvider.CONTENT_URI, selection, selectionArgs);
      }
    };
    Thread thread = new Thread(task);
    thread.start();

  }

  private Goal getGoal(int id) {
    String[] selectionArgs = new String[]{Integer.valueOf(id).toString()};
    String selection = GoalDatabase.GoalEntry._ID + "=?";
    Cursor cursor = mContext.getContentResolver().query(
            GoalProvider.CONTENT_URI, mProjection, selection, selectionArgs, null);
    ArrayList<Goal> goals = getGoals(cursor);
    if (goals != null && goals.size()>0) {
      return goals.get(0);
    }
    return null;
  }

  private ArrayList<Goal> getGoals() {
    Cursor cursor = mContext.getContentResolver().query(GoalProvider.CONTENT_URI,
            mProjection, null, null, null);
    return getGoals(cursor);
  }

  private ArrayList<Goal> getGoals(Cursor cursor) {
    ArrayList<Goal> goals = new ArrayList<Goal>();
    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      Goal goal = readCursor(cursor);
      goals.add(goal);
      cursor.moveToNext();
    }
    return goals;
  }

  private void insertOrUpdateGoal(Goal goal) {
    ContentValues values = new ContentValues();

    values.put(GoalDatabase.GoalEntry.NAME, goal.getName());
    values.put(GoalDatabase.GoalEntry.MOTIVATION, goal.getMotivation());
    values.put(GoalDatabase.GoalEntry.DEFINITION_DONE, goal.getDefinitionDone());
    values.put(GoalDatabase.GoalEntry.IMAGE, goal.getImage());

    Calendar endDate = goal.getDueDate();
    values.put(GoalDatabase.GoalEntry.DUE_YEAR, endDate.get(Calendar.YEAR));
    values.put(GoalDatabase.GoalEntry.DUE_MONTH, endDate.get(Calendar.MONTH));
    values.put(GoalDatabase.GoalEntry.DUE_DAY, endDate.get(Calendar.DAY_OF_MONTH));

    if (goal.getId() == 0) {
      mContext.getContentResolver().insert(GoalProvider.CONTENT_URI, values);
    } else {
      String[] args = new String[]{Integer.valueOf(goal.getId()).toString()};
      String selection = GoalDatabase.GoalEntry._ID + "=?";
      mContext.getContentResolver().update(GoalProvider.CONTENT_URI, values, selection,
              args);
    }
  }

  private Goal readCursor(Cursor c) {
    Goal goal = new Goal(c.getString(c.getColumnIndex(GoalDatabase.GoalEntry.NAME)));
    goal.setId(c.getInt(c.getColumnIndex(GoalDatabase.GoalEntry._ID)));
    goal.setMotivation(c.getString(c.getColumnIndex(GoalDatabase.GoalEntry.MOTIVATION)));
    goal.setDefinitionDone(c.getString(c.getColumnIndex(GoalDatabase.GoalEntry.DEFINITION_DONE)));
    int endYear = c.getInt(c.getColumnIndex(GoalDatabase.GoalEntry.DUE_YEAR));
    int endMonth = c.getInt(c.getColumnIndex(GoalDatabase.GoalEntry.DUE_MONTH));
    int endDay = c.getInt(c.getColumnIndex(GoalDatabase.GoalEntry.DUE_DAY));
    goal.setDueDate(new GregorianCalendar(endYear, endMonth, endDay));
    goal.setImage(c.getString(c.getColumnIndex(GoalDatabase.GoalEntry.IMAGE)));
    return goal;
  }

  private class GoalTask extends AsyncTask<Integer, Void, ArrayList<Goal>> {
    private GoalCallback mCallback;

    GoalTask(GoalCallback callback) {
      mCallback = callback;
    }
    @Override
    protected ArrayList<Goal> doInBackground(Integer... params) {
      if (params != null && params.length > 0) {
        ArrayList<Goal> goals = new ArrayList<Goal>();
        goals.add(getGoal(params[0]));
        return goals;
      }
      return getGoals();
    }

    @Override
    protected void onPostExecute(ArrayList<Goal> goals) {
      mCallback.onGoalsReturned(goals);
    }
  }

  public interface GoalCallback {
    void onGoalsReturned(ArrayList<Goal> goals);
  }
}
