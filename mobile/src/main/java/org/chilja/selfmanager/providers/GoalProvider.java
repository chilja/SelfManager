package org.chilja.selfmanager.providers;

import android.content.Context;
import android.net.Uri;

import org.chilja.selfmanager.db.GoalDatabase;

/**
 * Created by chiljagossow on 6/17/15.
 */
public class GoalProvider extends BaseProvider {

  public static final String AUTHORITY = "org.chilja.selfmanager.providers.GoalProvider";

  public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
          + "/" + GoalDatabase.GoalEntry.TABLE_NAME);

  @Override
  public void initialize() {

    mAuthority = AUTHORITY;

    mTableName = GoalDatabase.GoalEntry.TABLE_NAME;
    mId = GoalDatabase.GoalEntry._ID;

    mTypeItem = "?";
    mTypeItemId = "?";

    mContentUri = CONTENT_URI;
  }

  @Override
  protected void setDatabase(Context context) {
    mDatabase = GoalDatabase.getInstance(context);
  }


}
