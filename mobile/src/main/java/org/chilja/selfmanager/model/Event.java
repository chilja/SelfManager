package org.chilja.selfmanager.model;

import android.content.Context;

import org.chilja.selfmanager.resolvers.EventResolver;

import java.util.Calendar;

/**
 * Created by chiljagossow on 7/13/15.
 */
public class Event extends Item{

  protected long mEventId;

  public void setName(String name) {
    mName = name;
  }

  public Event(String text) {
    super(text);
  }

  public Event() {
    super("");
  }

  public Calendar getEndDate() {
    return mEndDate;
  }

  public void setEndDate(Calendar mEndDate) {
    this.mEndDate = mEndDate;
  }

  public String getDescription() {
    return mDescription;
  }

  public void setDescription(String mDescription) {
    this.mDescription = mDescription;
  }

  protected Calendar mEndDate;
  protected String mDescription;

  public long getEventId() {
    return mEventId;
  }

  public void setEventId(long mEventId) {
    this.mEventId = mEventId;
  }

  public void save(Context context) {
    new EventResolver(context).saveEvent(this);
  }

  public void delete(Context context) {
    new EventResolver(context).deleteEvent(this);
  }

  @Override
  public int describeContents() {
    return 0;
  }
}
