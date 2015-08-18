package org.chilja.selfmanager.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;


/**
 * Created by chiljagossow on 6/17/15.
 */
public abstract class BaseProvider extends ContentProvider {

  public static final int ITEM = 100;
  public static final int ITEM_ID = 200;

  protected String mAuthority;

  protected String mTableName;

  protected String mId;

  protected String mTypeItem;
  protected String mTypeItemId;

  protected Uri mContentUri;

  protected SQLiteOpenHelper mDatabase;

  protected UriMatcher sURIMatcher = new UriMatcher(
          UriMatcher.NO_MATCH);

  {
    initialize();
    sURIMatcher.addURI(mAuthority, mTableName, ITEM);
    sURIMatcher.addURI(mAuthority, mTableName + "/#", ITEM_ID);
  }

  public abstract void initialize();

  protected abstract void setDatabase(Context context);

  public Uri getContentUri(int id) {
    return Uri.parse("content://" + mAuthority
            + "/" + mTableName + "/#" + id);
  }


  @Override
  public boolean onCreate() {
    setDatabase(getContext());
    return true;
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
    SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
    queryBuilder.setTables(mTableName);
    SQLiteDatabase db = mDatabase.getReadableDatabase();

    int uriType = sURIMatcher.match(uri);
    switch (uriType) {
      case ITEM_ID:
        queryBuilder.appendWhere(mId + "=" + uri.getLastPathSegment());
        break;
      case ITEM:
        break;
      default:
        throw new IllegalArgumentException("Unknown URI");
    }

    Cursor cursor = queryBuilder.query(db,
            projection, selection, selectionArgs, null, null, sortOrder);
    cursor.setNotificationUri(getContext().getContentResolver(), uri);
    return cursor;
  }

  @Override
  public String getType(Uri uri) {
    //MIME type
    int uriType = sURIMatcher.match(uri);
    switch (uriType) {
      case ITEM_ID:
        return mTypeItemId;
      case ITEM:
        return mTypeItem;
    }
    return null;
  }

  @Override
  public Uri insert(Uri uri, ContentValues initialValues) {

    int uriType = sURIMatcher.match(uri);
    SQLiteDatabase db;
    long rowId;

    ContentValues values;
    if (initialValues != null) {
      values = new ContentValues(initialValues);
    } else {
      values = new ContentValues();
    }

    switch (uriType) {
      case ITEM:
        db = mDatabase.getWritableDatabase();
        rowId = db.insert(mTableName, null, values);
        return ContentUris.withAppendedId(uri, rowId);

      default:
        throw new IllegalArgumentException("Unknown or Invalid URI " + uri);
    }
  }

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs) {
    SQLiteDatabase db = mDatabase.getWritableDatabase();
    String id = uri.getLastPathSegment();
    switch (sURIMatcher.match(uri)) {
      case ITEM:
        break;
      case ITEM_ID:
        if (TextUtils.isEmpty(selection)) {
          selection = mId + " = " + id;
        } else {
          selection = selection + " and " + mId + " = " + id;
        }
        break;
      default:
        throw new IllegalArgumentException("Unknown or Invalid URI " + uri);
    }

    int count = db.delete(mTableName, selection, selectionArgs);
    getContext().getContentResolver().notifyChange(uri, null);
    return count;
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    SQLiteDatabase db;
    int count;
    switch (sURIMatcher.match(uri)) {
      case ITEM:
        db = mDatabase.getWritableDatabase();
        count = db.update(mTableName, values, selection, selectionArgs);
        break;
      default:
        throw new IllegalArgumentException("Unknown URI " + uri);
    }

    getContext().getContentResolver().notifyChange(uri, null);
    return count;
  }

}
