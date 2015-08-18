package org.chilja.selfmanager;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;

import org.chilja.selfmanager.db.GoalDatabase;
import org.chilja.selfmanager.model.Note;
import org.chilja.selfmanager.providers.NoteProvider;

/**
 * Created by chiljagossow on 8/9/15.
 */
public class NoteProviderTest extends ProviderTestCase2<NoteProvider> {
  /**
   * Constructor.
   */
  public NoteProviderTest() {
    super(NoteProvider.class, NoteProvider.AUTHORITY);
  }

  private ContentResolver contentResolver;

  private Note mNote;

  private Uri mNoteUri;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    contentResolver = getMockContentResolver();
    mNote = new Note();
    mNote.setName("Test");
    mNote.setText("text");
    mNote.setGoalId(1);
    mNoteUri = insert(mNote);
    int id = Integer.valueOf(mNoteUri.getLastPathSegment());
    mNote.setId(id);
  }

  public void testRetrieve() {
    retrieve(mNote, mNoteUri);
  }

  public void testUpdate() {
    mNote.setName("Updated");
    update(mNote);
    retrieve(mNote, mNoteUri);
  }

  private void delete(Note note) {
    String selection = GoalDatabase.NoteEntry._ID + "=?";
    String[] selectionArgs = new String[]{Integer.valueOf(note.getId()).toString()};
    contentResolver.delete(NoteProvider.CONTENT_URI_NOTE, selection,
            selectionArgs);
  }

  private Uri insert(Note note) {
    ContentValues values = new ContentValues();
    values.put(GoalDatabase.NoteEntry.COL_NAME, note.getName());
    values.put(GoalDatabase.NoteEntry.COL_TEXT, note.getText());
    values.put(GoalDatabase.NoteEntry.COL_GOAL_ID, note.getGoalId());
    return contentResolver.insert(NoteProvider.CONTENT_URI_NOTE, values);
  }

  private int update(Note note) {
    ContentValues values = new ContentValues();
    values.put(GoalDatabase.NoteEntry.COL_NAME, note.getName());
    values.put(GoalDatabase.NoteEntry.COL_TEXT, note.getText());
    values.put(GoalDatabase.NoteEntry.COL_GOAL_ID, note.getGoalId());
    String[] args = new String[]{Integer.valueOf(note.getId()).toString()};
    String selection = GoalDatabase.NoteEntry._ID + "=?";
    return contentResolver.update(NoteProvider.CONTENT_URI_NOTE, values, selection, args);
  }

  private void retrieve(Note note, Uri uri) {
    Cursor cursor = contentResolver.query(uri, null, null, null, null);
    assertEquals(1, cursor.getCount());
    assertTrue(cursor.moveToFirst());

    assertEquals(note.getName(),
            cursor.getString(cursor.getColumnIndex(GoalDatabase.NoteEntry.COL_NAME)));
    assertEquals(note.getGoalId(), cursor.getInt(cursor.getColumnIndex(GoalDatabase.NoteEntry.COL_GOAL_ID)));
    assertEquals(note.getText(), cursor.getString(cursor.getColumnIndex(GoalDatabase.NoteEntry.COL_TEXT)));
    int id = cursor.getInt(cursor.getColumnIndex(GoalDatabase.NoteEntry._ID));
    note.setId(id);
    cursor.close();
  }

  public void tearDown() {
    delete(mNote);
  }

}
