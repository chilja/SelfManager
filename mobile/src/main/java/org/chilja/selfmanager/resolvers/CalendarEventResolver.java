package org.chilja.selfmanager.resolvers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import org.chilja.selfmanager.model.Event;

import java.util.Calendar;

/**
 * Created by chiljagossow on 7/14/15.
 */
public class CalendarEventResolver {
  Context mContext;

  public CalendarEventResolver(Context context) {
    mContext = context;
  }

    public void readCalendarEvent(Event event) {
      String selection = CalendarContract.Events._ID + " =?";
      String[] selectionArgs = {Long.valueOf(event.getEventId()).toString()};
      String orderBy = null;
      Cursor cursor = mContext.getContentResolver()
              .query(
                      Uri.parse("content://com.android.calendar/events"),
                      new String[] { "calendar_id", "title", "description",
                              "dtstart", "dtend", "eventLocation" }, selection,
                      selectionArgs, orderBy);
      cursor.moveToFirst();
      if (cursor.getCount()>0) {
        event.setName(cursor.getString(1));
        event.setDueDate(getCalendar(Long.parseLong(cursor.getString(3))));
        event.setEndDate(getCalendar(Long.parseLong(cursor.getString(4))));
        event.setDescription(cursor.getString(2));
      }
    }

    public static Calendar getCalendar(long milliSeconds) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(milliSeconds);
      return calendar;
    }

  }
