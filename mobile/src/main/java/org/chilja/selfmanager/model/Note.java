package org.chilja.selfmanager.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.chilja.selfmanager.resolvers.NoteResolver;
import org.chilja.selfmanager.presenter.edit.Editable;

/**
 * Created by chiljagossow on 7/15/15.
 */
public class Note extends Item implements Editable, Parcelable {



  private String mText;

  public Note(String name) {
    super(name);
  }

  public Note() {

  }

  public String getText() {
    return mText;
  }

  public void setText(String text) {
    this.mText = text;
  }

  @Override
  public String getContent() {
    return getText();
  }

  @Override
  public void setContent(String text) {
    setText(text);
  }

  @Override
  public String getTitle() {
    return getTitle();
  }

  @Override
  public void setTitle(String title) {

  }

  @Override
  public boolean saveToDb(Context context) {
    save(context);
    return true;
  }

  @Override
  public boolean deleteFromDb(Context context) {
    delete(context);
    return true;
  }


  @Override
  public void save(Context context) {
    new NoteResolver(context).insertOrUpdateNote(this);
  }

  @Override
  public void delete(Context context) {
    new NoteResolver(context).deleteNote(this);
  }

  // Parcelable implementation
  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeString(mText);
  }

  public Note(Parcel source) {
    super(source);
    mText = source.readString();
  }

  public static final Parcelable.Creator CREATOR = new Parcelable.Creator<Note>() {

    @Override
    public Note createFromParcel(Parcel source) {
      return new Note(source);
    }

    @Override
    public Note[] newArray(int size) {
      return new Note[size];
    }
  };
}
