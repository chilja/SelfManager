package org.chilja.selfmanager.presenter.edit;

import android.content.Context;

/**
 * Created by chiljagossow on 7/15/15.
 */
public interface Editable {
  String getContent();
  void setContent(String content);
  String getTitle();
  void setTitle(String title);
  boolean saveToDb(Context context);
  boolean deleteFromDb(Context context);
}
