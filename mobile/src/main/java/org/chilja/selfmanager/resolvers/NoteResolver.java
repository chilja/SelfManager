package org.chilja.selfmanager.resolvers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import org.chilja.selfmanager.db.GoalDatabase;
import org.chilja.selfmanager.model.Note;
import org.chilja.selfmanager.providers.NoteProvider;

import java.util.ArrayList;

/**
 * Created by chiljagossow on 8/13/15.
 */
public class NoteResolver {
  private Context mContext;
  private final String[] mProjection = {
          GoalDatabase.NoteEntry._ID,
          GoalDatabase.NoteEntry.COL_NAME,
          GoalDatabase.NoteEntry.COL_TEXT,
          GoalDatabase.NoteEntry.COL_GOAL_ID
  };

  public NoteResolver(Context context) {
    mContext = context;
  }

  public void getNotes(NoteCallback callback, int goalId) {
    NoteTask task = new NoteTask(callback);
    task.execute(goalId);
  }

  public void insertOrUpdateNote(final Note note) {
    Runnable task = new Runnable() {
      @Override
      public void run() {
        insertOrUpdate(note);
      }
    };
    Thread thread = new Thread(task);
    thread.start();
  }

  public void deleteNote(final Note note) {
    Runnable task = new Runnable() {
      @Override
      public void run() {
        String selection = GoalDatabase.NoteEntry._ID + "=?";
        String[] selectionArgs = new String[]{Integer.valueOf(note.getId()).toString()};
        mContext.getContentResolver().delete(NoteProvider.CONTENT_URI_NOTE, selection,
                selectionArgs);
      }
    };
    Thread thread = new Thread(task);
    thread.start();
  }

  public ArrayList<Note> getNotes(int goalId) {
    String selection = GoalDatabase.NoteEntry.COL_GOAL_ID + "=?";
    String[] selectionArgs = new String[]{Integer.valueOf(goalId).toString()};
    Cursor cursor = mContext.getContentResolver().query(NoteProvider.CONTENT_URI_NOTE,
            mProjection, selection, selectionArgs, null);
    return getNotes(cursor);
  }

  private ArrayList<Note> getNotes() {
    Cursor cursor = mContext.getContentResolver().query(NoteProvider.CONTENT_URI_NOTE,
            mProjection, null, null, null);
    return getNotes(cursor);
  }

  private ArrayList<Note> getNotes(Cursor cursor) {
    ArrayList<Note> notes = new ArrayList<Note>();
    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      Note note = readCursor(cursor);
      notes.add(note);
      cursor.moveToNext();
    }
    return notes;
  }

  public void insertOrUpdate(Note note) {
    ContentValues values = new ContentValues();
    values.put(GoalDatabase.NoteEntry.COL_NAME, note.getName());
    values.put(GoalDatabase.NoteEntry.COL_TEXT, note.getText());
    values.put(GoalDatabase.NoteEntry.COL_GOAL_ID, note.getGoalId());


    if (note.getId() == 0) {
      mContext.getContentResolver().insert(NoteProvider.CONTENT_URI_NOTE, values);
    } else {

      String selection = GoalDatabase.NoteEntry._ID + "=?";
      String[] args = new String[]{Integer.valueOf(note.getId()).toString()};
      mContext.getContentResolver().update(NoteProvider.CONTENT_URI_NOTE, values,
              selection, args);
    }

  }

  private Note readCursor(Cursor cursor) {
    Note note = null;
    try {
      note = new Note(cursor.getString(cursor.getColumnIndexOrThrow(GoalDatabase.NoteEntry.COL_NAME)));
      note.setId(cursor.getInt(cursor.getColumnIndexOrThrow(GoalDatabase.NoteEntry._ID)));
      note.setText(cursor.getString(cursor.getColumnIndexOrThrow(GoalDatabase.NoteEntry.COL_TEXT)));
      note.setGoalId(cursor.getInt(cursor.getColumnIndexOrThrow(GoalDatabase.NoteEntry.COL_GOAL_ID)));
    } catch (android.database.CursorIndexOutOfBoundsException e) {
      // no event found - that is ok
    }
    return note;
  }

  private class NoteTask extends AsyncTask<Integer, Void, ArrayList<Note>> {
    private NoteCallback mCallback;

    NoteTask(NoteCallback callback) {
      mCallback = callback;
    }
    @Override
    protected ArrayList<Note> doInBackground(Integer... params) {
      if (params != null && params.length > 0) {
        return getNotes(params[0]);
      }
      return getNotes();
    }

    @Override
    protected void onPostExecute(ArrayList<Note> notes) {
      mCallback.onNotesReturned(notes);
    }
  }

  public interface NoteCallback {
    void onNotesReturned(ArrayList<Note> items);
  }
}
