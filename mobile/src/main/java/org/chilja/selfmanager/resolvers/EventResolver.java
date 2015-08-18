package org.chilja.selfmanager.resolvers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import org.chilja.selfmanager.db.GoalDatabase;
import org.chilja.selfmanager.model.Event;
import org.chilja.selfmanager.providers.EventProvider;

import java.util.ArrayList;

/**
 * Created by chiljagossow on 8/13/15.
 */
public class EventResolver {
  private Context mContext;
  private final String[] mProjection = {
          GoalDatabase.EventEntry._ID,
          GoalDatabase.EventEntry.COL_EVENT_ID,
          GoalDatabase.EventEntry.COL_GOAL_ID
  };

  public EventResolver(Context context) {
    mContext = context;
  }

  public void getEvents(EventCallback callback) {
    EventTask task = new EventTask(callback);
    task.execute();
  }

  public void getEvents(EventCallback callback, int goalId) {
    EventTask task = new EventTask(callback);
    task.execute(goalId);
  }

  public void saveEvent(final Event event) {
    Runnable task = new Runnable() {
      @Override
      public void run() {
        insertEvent(event);
      }
    };
    Thread thread = new Thread(task);
    thread.start();
  }

  public void deleteEvent(final Event event) {
    Runnable task = new Runnable() {
      @Override
      public void run() {
        String selection = GoalDatabase.EventEntry._ID + "=?";
        String[] selectionArgs = new String[]{Integer.valueOf(event.getId()).toString()};
        mContext.getContentResolver().delete(EventProvider.CONTENT_URI_EVENT, selection,
                selectionArgs);
      }
    };
    Thread thread = new Thread(task);
    thread.start();
  }

  private ArrayList<Event> getEvents(int goalId) {
    String[] selectionArgs = new String[]{Integer.valueOf(goalId).toString()};
    String selection = GoalDatabase.EventEntry.COL_GOAL_ID + "=?";
    Cursor cursor = mContext.getContentResolver().query(
            EventProvider.CONTENT_URI_EVENT, mProjection, selection, selectionArgs, null);
    return getEvents(cursor);
  }

  private ArrayList<Event> getEvents() {
    Cursor cursor = mContext.getContentResolver().query(
            EventProvider.CONTENT_URI_EVENT, mProjection, null, null, null);
    return getEvents(cursor);
  }

  private ArrayList<Event> getEvents(Cursor cursor) {
    ArrayList<Event> events = new ArrayList<Event>();
    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      Event event = readCursor(cursor);
      events.add(event);
      cursor.moveToNext();
    }
    return events;
  }

  private void insertEvent(Event event) {
    ContentValues values = new ContentValues();
    values.put(GoalDatabase.EventEntry.COL_EVENT_ID, event.getEventId());
    values.put(GoalDatabase.EventEntry.COL_GOAL_ID, event.getGoalId());
    mContext.getContentResolver().insert(EventProvider.CONTENT_URI_EVENT, values);
  }

  private Event readCursor(Cursor cursor) {
    Event event = new Event();
    try {
      event.setId(cursor.getInt(cursor.getColumnIndexOrThrow(GoalDatabase.EventEntry._ID)));
      event.setEventId(
              cursor.getInt(cursor.getColumnIndexOrThrow(GoalDatabase.EventEntry.COL_EVENT_ID)));
      event.setGoalId(
              cursor.getInt(cursor.getColumnIndexOrThrow(GoalDatabase.EventEntry.COL_GOAL_ID)));
    } catch (android.database.CursorIndexOutOfBoundsException e) {
      // no event found - that is ok
    }
    return event;
  }

  private class EventTask extends AsyncTask<Integer, Void, ArrayList<Event>> {
    private EventCallback mCallback;

    EventTask(EventCallback callback) {
      mCallback = callback;
    }
    @Override
    protected ArrayList<Event> doInBackground(Integer... params) {
      if (params != null && params.length > 0) {
        return getEvents(params[0]);
      }
      return getEvents();
    }

    @Override
    protected void onPostExecute(ArrayList<Event> events) {
      mCallback.onEventsReturned(events);
    }
  }

  public interface EventCallback {
    void onEventsReturned(ArrayList<Event> events);
  }

}
