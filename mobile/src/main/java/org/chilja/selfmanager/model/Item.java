package org.chilja.selfmanager.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by chiljagossow on 6/17/15.
 */
public abstract class Item implements Comparable<Item>, Parcelable {

  protected int mId;
  protected int mGoalId;
  protected String mName;
  protected boolean mDone;
  protected Calendar mDueDate = new GregorianCalendar();

  public Item(String name){
    mName = name;
  }

  public Item(int id){
    setId(id);
  }

  public Item(){

  }

  public int getId() {
    return mId;
  }

  public void setId(int id) {
    mId = id;
  }

  public int getGoalId() {
    return mGoalId;
  }

  public void setGoalId(int goalId) {
    mGoalId = goalId;
  }

  public String getName() {
    return mName;
  }

  public void setName(String name) {
    mName = name;
  }

  public Calendar getDueDate() {
    return mDueDate;
  }

  public void setDueDate(Calendar endDate) {
    this.mDueDate = endDate;
  }

  public boolean isDone() {
    return mDone;
  }

  public void setDone(boolean done) {
    mDone = done;
  }

  public abstract void save(Context context);

  public abstract void delete(Context context) ;

  public boolean changeStatus() {
    if (!isDone()) {
      setDone(true);
    } else {
      setDone(false);
    }
    return isDone();
  }


  public String getDisplayDate(Calendar date) {
    if (date != null)
      return getRelativeDate(date, GregorianCalendar.getInstance());
    return "as soon as possible";
  }

  public static String getRelativeDate(Calendar date1, Calendar date2) {
    return (String) DateUtils.getRelativeTimeSpanString(date1.getTime().getTime(),
            date2.getTime().getTime(), DateUtils.DAY_IN_MILLIS);
  }

  @Override
  public int compareTo(Item action) {
      if (getDueDate() == null) {
        return -1;
      }
      if (action.getDueDate() == null) {
        return 1;
      }
      if (getDueDate().getTime().getTime() - action.getDueDate().getTime().getTime() < 0) {
        return -1;
      }
      if (getDueDate().getTime().getTime() - action.getDueDate().getTime().getTime() > 0) {
        return 1;
      }
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(mName);
    dest.writeSerializable(mDueDate);
    dest.writeInt(mId);
    dest.writeInt(mGoalId);
    dest.writeBooleanArray(new boolean[] {mDone});
  }

  public Item(Parcel source){

  /*
   * Reconstruct from the Parcel
   */
    this(source.readString());
    mDueDate = (GregorianCalendar) source.readSerializable();
    mId = source.readInt();
    mGoalId = source.readInt();
    boolean[] boolArray = new boolean[1];
    source.readBooleanArray(boolArray);
    mDone = boolArray[0];
  }

}
