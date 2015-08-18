package org.chilja.selfmanager.providers;

import android.content.Context;
import android.net.Uri;

import org.chilja.selfmanager.db.GoalDatabase;

/**
 * Created by chiljagossow on 6/17/15.
 */
public class EventProvider extends BaseProvider {

  public static final String AUTHORITY = "org.chilja.selfmanager.providers.EventProvider";

  public static final Uri CONTENT_URI_EVENT = Uri.parse("content://" + AUTHORITY
          + "/" + GoalDatabase.EventEntry.TABLE_NAME);

  @Override
  public void initialize() {

    mAuthority = AUTHORITY;

    mTableName = GoalDatabase.EventEntry.TABLE_NAME;
    mId = GoalDatabase.EventEntry._ID;

    mTypeItem = "?";
    mTypeItemId = "?";

    mContentUri = CONTENT_URI_EVENT;
  }

  @Override
  protected void setDatabase(Context context) {
    mDatabase = GoalDatabase.getInstance(context);
  }


}
