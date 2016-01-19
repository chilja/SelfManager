package org.chilja.selfmanager.model;

import android.content.Context;
import android.content.SharedPreferences;

import org.chilja.selfmanager.R;
import org.chilja.selfmanager.presenter.edit.Editable;

/**
 * Created by chiljagossow on 7/15/15.
 */
public class Mission implements Editable {

  private SharedPreferences mSharedPref;
  private String mKey;
  private String mContent;
  private String mDefaultContent;


  public Mission(Context context) {
    mSharedPref = context.getSharedPreferences(
            context.getString(R.string.preference_chief_aim_key), Context.MODE_PRIVATE);
    mKey = context.getString(R.string.saved_chief_aim);
    mDefaultContent = context.getResources().getString(R.string.aim);
  }

  @Override
  public String getContent() {
    mContent = mSharedPref.getString(mKey, mDefaultContent);
    return mContent;
  }

  @Override
  public void setContent(String content) {
    mContent = content;
  }

  @Override
  public String getTitle() {
    return "Chief Aim";
  }

  @Override
  public void setTitle(String title) {

  }

  @Override
  public boolean saveToDb(Context context) {
    SharedPreferences.Editor editor = mSharedPref.edit();
    editor.putString(mKey, mContent);
    editor.commit();
    return true;
  }

  @Override
  public boolean deleteFromDb(Context context) {
    return false;
  }
}
