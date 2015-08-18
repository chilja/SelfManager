package org.chilja.selfmanager;

import android.test.AndroidTestCase;

import org.chilja.selfmanager.model.Note;
import org.chilja.selfmanager.resolvers.NoteResolver;

import java.util.ArrayList;

/**
 * Created by chiljagossow on 8/14/15.
 */
public class NoteResolverTest extends AndroidTestCase implements NoteResolver.NoteCallback{
  private Note mNote;
  private NoteResolver mResolver;
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mNote = new Note();
    mNote.setName("Test");
    mNote.setText("text");
    mNote.setGoalId(1);
    mResolver = new NoteResolver(getContext());
    mResolver.insertOrUpdate(mNote);
  }

  public void testRetrieve() {
    ArrayList<Note> items = mResolver.getNotes(mNote.getGoalId());
    checkItems(items);
  }

  private void checkItems(ArrayList<Note> items) {
    assertNotNull(items);
    assertTrue(items.size() > 0);
    boolean found = false;
    for (Note note: items) {
      if (note.getGoalId() == mNote.getGoalId()) {
        if (note.getName().equals(mNote.getName()) && note.getText().equals(mNote.getText())) {
          found = true;
          break;
        }
      }
    }
    assertTrue(found);
  }

  @Override
  public void onNotesReturned(ArrayList<Note> items) {
    checkItems(items);
  }
}
