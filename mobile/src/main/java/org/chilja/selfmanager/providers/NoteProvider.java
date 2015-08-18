package org.chilja.selfmanager.providers;

import android.content.Context;
import android.net.Uri;

import org.chilja.selfmanager.db.GoalDatabase;

/**
 * Created by chiljagossow on 6/17/15.
 */
public class NoteProvider extends BaseProvider {

  public static final String AUTHORITY = "org.chilja.selfmanager.providers.NoteProvider";

  public static final Uri CONTENT_URI_NOTE = Uri.parse("content://" + AUTHORITY
          + "/" + GoalDatabase.NoteEntry.TABLE_NAME);

  @Override
  public void initialize() {

    mAuthority = AUTHORITY;

    mTableName = GoalDatabase.NoteEntry.TABLE_NAME;
    mId = GoalDatabase.NoteEntry._ID;

    mTypeItem = "?";
    mTypeItemId = "?";

    mContentUri = CONTENT_URI_NOTE;
  }

  @Override
  protected void setDatabase(Context context) {
    mDatabase = GoalDatabase.getInstance(context);
  }


}
