package org.chilja.selfmanager.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.chilja.selfmanager.resolvers.ActionResolver;

/**
 * Created by chiljagossow on 6/17/15.
 */
public class Action extends Item {

  public Action() {
    super();
  }

  public Action(String text){
    super(text);
  }

  public Action(int id) {
    super(id);
  }

  public Action(Parcel source) {
    super(source);
  }

  public void save(Context context) {
    ActionResolver resolver = new ActionResolver(context);
    resolver.saveAction(this);
  }

  public void delete(Context context) {
    ActionResolver resolver = new ActionResolver(context);
    resolver.deleteAction(this);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  public static final Parcelable.Creator CREATOR = new Parcelable.Creator<Action>() {

    @Override
    public Action createFromParcel(Parcel source) {
      return new Action(source);
    }

    @Override
    public Action[] newArray(int size) {
      return new Action[size];
    }
  };

}
