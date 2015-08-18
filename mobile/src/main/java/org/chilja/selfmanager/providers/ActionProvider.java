package org.chilja.selfmanager.providers;

import android.content.Context;
import android.net.Uri;

import org.chilja.selfmanager.db.GoalDatabase;

/**
 * Created by chiljagossow on 6/17/15.
 */
public class ActionProvider extends BaseProvider {

  public static final String AUTHORITY = "org.chilja.selfmanager.providers.ActionProvider";

  public static final Uri CONTENT_URI_ACTION = Uri.parse("content://" + AUTHORITY
          + "/" + GoalDatabase.ActionEntry.TABLE_NAME);

  @Override
  public void initialize() {

    mAuthority = AUTHORITY;

    mTableName = GoalDatabase.ActionEntry.TABLE_NAME;
    mId = GoalDatabase.ActionEntry._ID;

    mTypeItem = "?";
    mTypeItemId = "?";

    mContentUri = CONTENT_URI_ACTION;
  }

  @Override
  protected void setDatabase(Context context) {
    mDatabase = GoalDatabase.getInstance(context);
  }


}
