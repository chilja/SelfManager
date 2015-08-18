package org.chilja.selfmanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by chiljagossow on 6/17/15.
 */
public class GoalDatabase extends SQLiteOpenHelper {

  private static final String TAG = "GoalDatabase";
  private static final int DB_VERSION = 1;
  private static final String DB_NAME = "goal_data";

  private static GoalDatabase sInstance;

  public static GoalDatabase getInstance(Context context) {
    if (sInstance == null) {
      sInstance = new GoalDatabase(context);
    }
    return sInstance;
  }

  private GoalDatabase(Context context) {
    super(context, DB_NAME, null, DB_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(CREATE_TABLE_GOAL);
    db.execSQL(CREATE_TABLE_ACTION);
    db.execSQL(CREATE_TABLE_WAIT_ITEM);
    db.execSQL(CREATE_TABLE_EVENT);
    db.execSQL(CREATE_TABLE_NOTE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL(SQL_DELETE_ENTRIES_GOAL);
    db.execSQL(SQL_DELETE_ENTRIES_ACTION);
    db.execSQL(SQL_DELETE_ENTRIES_WAIT_ITEM);
    db.execSQL(SQL_DELETE_ENTRIES_EVENT);
    db.execSQL(SQL_DELETE_ENTRIES_NOTE);
    onCreate(db);
  }

  /* Inner class that defines the table contents */
  public static abstract class GoalEntry implements BaseColumns {
    public static final String TABLE_NAME = "goal";
    public static final String NAME = "name";
    public static final String IMAGE = "image";
    public static final String DUE_YEAR = "due_year";
    public static final String DUE_MONTH = "due_month";
    public static final String DUE_DAY = "due_day";
    public static final String MOTIVATION = "motivation";
    public static final String DEFINITION_DONE = "definition_done";
  }

  public static final String CREATE_TABLE_GOAL = "create table " + GoalEntry.TABLE_NAME
          + " (" + GoalEntry._ID + " integer primary key autoincrement, "
          + GoalEntry.NAME + " text not null, "
          + GoalEntry.DUE_YEAR + " integer, "
          + GoalEntry.DUE_MONTH + " integer, "
          + GoalEntry.DUE_DAY + " integer, "
          + GoalEntry.MOTIVATION + " text, "
          + GoalEntry.DEFINITION_DONE + " text, "
          + GoalEntry.IMAGE + " text"
          + ");"
          ;

  public static final String SQL_DELETE_ENTRIES_GOAL =
          "DROP TABLE IF EXISTS " + GoalEntry.TABLE_NAME;

  /* Inner class that defines the table contents */
  public static abstract class ActionEntry implements BaseColumns {
    public static final String TABLE_NAME = "action";
    public static final String COL_TEXT = "text";
    public static final String COL_GOAL_ID = "goal_id";
    public static final String COL_DUE_YEAR = "due_year";
    public static final String COL_DUE_MONTH = "due_month";
    public static final String COL_DUE_DAY = "due_day";
  }

  public static final String CREATE_TABLE_ACTION = "create table " + ActionEntry.TABLE_NAME
          + " (" + ActionEntry._ID + " integer primary key autoincrement, "
          + ActionEntry.COL_TEXT
          + " text not null,"
          + ActionEntry.COL_GOAL_ID
          + " integer,"
          + ActionEntry.COL_DUE_YEAR
          + " integer,"
          + ActionEntry.COL_DUE_MONTH
          + " integer,"
          + ActionEntry.COL_DUE_DAY
          + " integer);" ;

  public static final String SQL_DELETE_ENTRIES_ACTION =
          "DROP TABLE IF EXISTS " + ActionEntry.TABLE_NAME;

  /* Inner class that defines the table contents */
  public static abstract class WaitItemEntry implements BaseColumns {
    public static final String TABLE_NAME = "wait_item";
    public static final String COL_TEXT = "text";
    public static final String COL_RESPONSIBLE = "responsible";
    public static final String COL_GOAL_ID = "goal_id";
    public static final String COL_DUE_YEAR = "due_year";
    public static final String COL_DUE_MONTH = "due_month";
    public static final String COL_DUE_DAY = "due_day";
    public static final String COL_REQUEST_YEAR = "request_year";
    public static final String COL_REQUEST_MONTH = "request_month";
    public static final String COL_REQUEST_DAY = "request_day";
  }

  public static final String SQL_DELETE_ENTRIES_WAIT_ITEM =
          "DROP TABLE IF EXISTS " + GoalDatabase.WaitItemEntry.TABLE_NAME;


  public static final String CREATE_TABLE_WAIT_ITEM = "create table " + GoalDatabase.WaitItemEntry.TABLE_NAME
          + " (" + GoalDatabase.WaitItemEntry._ID + " integer primary key autoincrement, "
          + GoalDatabase.WaitItemEntry.COL_TEXT
          + " text not null,"
          + GoalDatabase.WaitItemEntry.COL_RESPONSIBLE
          + " text not null,"
          + GoalDatabase.WaitItemEntry.COL_GOAL_ID
          + " integer,"
          + GoalDatabase.WaitItemEntry.COL_DUE_YEAR
          + " integer,"
          + GoalDatabase.WaitItemEntry.COL_DUE_MONTH
          + " integer,"
          + GoalDatabase.WaitItemEntry.COL_DUE_DAY
          + " integer,"
          + GoalDatabase.WaitItemEntry.COL_REQUEST_YEAR
          + " integer,"
          + GoalDatabase.WaitItemEntry.COL_REQUEST_MONTH
          + " integer,"
          + GoalDatabase.WaitItemEntry.COL_REQUEST_DAY
          + " integer);";


  /* Inner class that defines the table contents */
  public static abstract class EventEntry implements BaseColumns {
    public static final String TABLE_NAME = "event";
    public static final String COL_EVENT_ID = "event_id";
    public static final String COL_GOAL_ID = "goal_id";
  }

  public static final String CREATE_TABLE_EVENT = "create table " + EventEntry.TABLE_NAME
          + " (" + EventEntry._ID + " integer primary key autoincrement, "
          + EventEntry.COL_EVENT_ID
          + " integer,"
          + EventEntry.COL_GOAL_ID
          + " integer);" ;

  public static final String SQL_DELETE_ENTRIES_EVENT =
          "DROP TABLE IF EXISTS " + EventEntry.TABLE_NAME;

  /* Inner class that defines the table contents */
  public static abstract class NoteEntry implements BaseColumns {
    public static final String TABLE_NAME = "note";
    public static final String COL_NAME = "name";
    public static final String COL_TEXT = "text";
    public static final String COL_GOAL_ID = "goal_id";
  }

  public static final String CREATE_TABLE_NOTE = "create table " + NoteEntry.TABLE_NAME
          + " (" + NoteEntry._ID + " integer primary key autoincrement, "
          + NoteEntry.COL_NAME
          + " text_not_null,"
          + NoteEntry.COL_TEXT
          + " text_not_null,"
          + NoteEntry.COL_GOAL_ID
          + " integer not null);" ;

  public static final String SQL_DELETE_ENTRIES_NOTE =
          "DROP TABLE IF EXISTS " + NoteEntry.TABLE_NAME;
}
