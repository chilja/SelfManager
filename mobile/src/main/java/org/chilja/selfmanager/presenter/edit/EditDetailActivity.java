package org.chilja.selfmanager.presenter.edit;

import android.content.Intent;

import org.chilja.selfmanager.R;
import org.chilja.selfmanager.model.Note;

/**
 * Created by chiljagossow on 7/29/15.
 */
public class EditDetailActivity extends EditActivity {

  public EditFragment getFragmentInstance(Intent intent) {

    // Get intent, action and MIME type
    String action = intent.getAction();
    String type = intent.getType();

    if (Intent.ACTION_SEND.equals(action) && type != null) {
      if ("text/plain".equals(type)) {
        Note sharedNote = new Note();
        String sharedText = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
          sharedNote.setText(sharedText);
        }
        return EditNoteFragment.newInstance(sharedNote, getString(R.string.title_new_note));
      }
    }

    return super.getFragmentInstance(intent);

  }
}
