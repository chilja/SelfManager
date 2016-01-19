package org.chilja.selfmanager.model;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.chilja.selfmanager.resolvers.GoalResolver;
import org.chilja.selfmanager.util.BitmapUtility;

import java.io.File;
import java.io.FileOutputStream;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by chiljagossow on 6/29/15.
 */
public class Goal extends Item {

  private static final String TAG = "Goal";

  private static final String IMAGE_DIR = "imageDir";

  private String mMotivation;
  private String mDefinitionDone;
  private String mImage;
  private boolean mHasNextItem;

  public List<Action> getActionList() {
    return mActionList;
  }

  public void setActionList(List<Action> mActionList) {
    this.mActionList = mActionList;
  }

  public List<Event> getEventList() {
    return mEventList;
  }

  public void setEventList(List<Event> mEventList) {
    this.mEventList = mEventList;
  }

  public List<WaitItem> getWaitItemList() {
    return mWaitItemList;
  }

  public void setWaitItemList(List<WaitItem> mWaitItemList) {
    this.mWaitItemList = mWaitItemList;
  }

  private List<Action> mActionList;
  private List<Event> mEventList;
  private List<WaitItem> mWaitItemList;

  private static class SaveImageTask implements Runnable {

    private Activity mActivity;
    private File mImageFile;
    private Bitmap mBitmap;

    SaveImageTask(Activity activity, File file, Bitmap bitmap) {
      mActivity = activity;
      mImageFile = file;
      mBitmap = bitmap;
    }
    @Override
    public void run() {
      FileOutputStream fos = null;
      try {
        fos = new FileOutputStream(mImageFile);
        // Use the compress method on the BitMap object to write image to the OutputStream
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.close();
        mActivity.runOnUiThread(new Runnable() {
          public void run() {
            Toast.makeText(mActivity, "Image was saved", Toast.LENGTH_SHORT);
          }
        });

      } catch (Exception e) {
        Log.d(TAG, "error saving " + mImageFile.getAbsolutePath());
        Toast.makeText(mActivity, "Error while saving image", Toast.LENGTH_SHORT);
      }
    }
  }

  public void setHasNextItem(boolean hasNextItem) {
    mHasNextItem = hasNextItem;
  }

  public boolean hasNextItem() {
    return mHasNextItem;
  }

  public Goal(Parcel source){
    super(source.readString());
  /*
   * Reconstruct from the Parcel
   */

    mMotivation = source.readString();
    mDefinitionDone = source.readString();
    mDueDate = (GregorianCalendar) source.readSerializable();
    mImage = source.readString();
    mId = source.readInt();
  }

  public Goal(){
    super();
  }

  public Goal(String name){
    super(name);
  }

  public String getImage() {
    return mImage;
  }

  public void setImage(String image) {
    this.mImage = image;
  }

  public String getMotivation() {
    return mMotivation;
  }

  public void setMotivation(String motivation) {
    this.mMotivation = motivation;
  }

  public String createImageFileName() {
    return mName.replace(' ', '_');
  }

  public String getDefinitionDone() {
    return mDefinitionDone;
  }

  public void setDefinitionDone(String definitionDone) {
    this.mDefinitionDone = definitionDone;
  }

  public void saveBitmap(final Activity activity, final Bitmap bitmap){
    String fileName = createImageFileName();
    ContextWrapper cw = new ContextWrapper(activity);
    File directory = cw.getDir(IMAGE_DIR, Context.MODE_PRIVATE);
    // Create imageDir
    File file = new File(directory,fileName);
    setImage(file.getAbsolutePath());
    SaveImageTask task = new SaveImageTask(activity, file, bitmap);
    Thread thread = new Thread(task);
    thread.start();

  }

  public void loadBitmap(Context context, ImageView imageView, int width, int height) {
    if (mImage != null) {
      BitmapUtility.loadBitmap(context, new File(mImage), imageView, width, height);
    }
  }

  public void loadBitmap(Context context, ImageView imageView, View coloredView, int width, int height) {
    if (mImage != null) {
      BitmapUtility.loadBitmap(context, new File(mImage), imageView, width, height, coloredView);
    }
  }

  public void delete(Context context) {
    new GoalResolver(context).deleteGoal(mId);
  }

  public static void delete(Context context, int id) {
    new GoalResolver(context).deleteGoal(id);
  }

  public void save(Context context) {
    new GoalResolver(context).insertGoal(this);
  }

  public String toString() {
    return getName();
  }

  public int describeContents() {
    return 0;
  }


  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeString(mMotivation);
    dest.writeString(mDefinitionDone);
    dest.writeString(mImage);
  }

  class MyCreator implements Parcelable.Creator<Goal> {
    public Goal createFromParcel(Parcel source) {
      return new Goal(source);
    }
    public Goal[] newArray(int size) {
      return new Goal[size];
    }
  }

}
