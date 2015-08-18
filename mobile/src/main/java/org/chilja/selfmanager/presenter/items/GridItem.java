package org.chilja.selfmanager.presenter.items;

import android.content.Context;
import android.view.View;

/**
 * Created by chiljagossow on 6/17/15.
 */
public interface GridItem {
  int TEXT = 0;
  int IMAGE = 1;

  View getView(Context context, int width);
}
