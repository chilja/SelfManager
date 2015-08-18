package org.chilja.selfmanager.db;

import android.content.Context;

/**
 * Created by chiljagossow on 8/7/15.
 */
public interface DatabaseInteractionListener {
  boolean save(Context context);
  boolean delete(Context context);
}
