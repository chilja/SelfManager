package org.chilja.selfmanager.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.chilja.selfmanager.resolvers.WaitItemResolver;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by chiljagossow on 6/17/15.
 */
public class WaitItem extends Item{

  public String getResponsible() {
    return mResponsible;
  }

  public void setResponsible(String responsible) {
    this.mResponsible = responsible;
  }

  private String mResponsible;
  private Calendar mRequestDate = new GregorianCalendar();


  public Calendar getRequestDate() {
    return mRequestDate;
  }

  public void setRequestDate(Calendar requestDate) {
    this.mRequestDate = requestDate;
  }

  public WaitItem(){
    super();
  }

  public WaitItem(String text){
    super(text);
  }

  public WaitItem(Parcel source) {
    super(source);
    mResponsible = source.readString();
    mRequestDate = (GregorianCalendar) source.readSerializable();
  }

  public void save(Context context) {
    new WaitItemResolver(context).saveWaitItem(this);
  }

  public void delete(Context context) {
    new WaitItemResolver(context).deleteWaitItem(this);
  }
  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeString(mResponsible);
    dest.writeSerializable(mRequestDate);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  public static final Parcelable.Creator CREATOR = new Parcelable.Creator<WaitItem>() {

    @Override
    public WaitItem createFromParcel(Parcel source) {
      return new WaitItem(source);
    }

    @Override
    public WaitItem[] newArray(int size) {
      return new WaitItem[size];
    }
  };
}
