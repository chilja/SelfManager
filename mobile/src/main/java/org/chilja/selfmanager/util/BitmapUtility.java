package org.chilja.selfmanager.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;

import org.chilja.selfmanager.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by chiljagossow on 6/2/15.
 */
public class BitmapUtility {

  /**
   * AsyncTask to retrieve images from resources.
   */
  private static class BitmapFromResourcesTask extends AsyncTask<Integer, Void, Bitmap> {

    private final ImageView imageView;
    private Integer resId = 0;
    private final int reqWidth;
    private final int reqHeight;
    private final Context context;

    private BitmapFromResourcesTask(Context context, ImageView imageView, int reqWidth,
                                    int reqHeight) {
      this.imageView = imageView;
      this.reqWidth = reqWidth;
      this.reqHeight = reqHeight;
      this.context = context;
      tasks.add(this);
    }

    @Override
    protected Bitmap doInBackground(Integer... params) {
      resId = params[0];
      return decodeSampledBitmapFromResource(context.getResources(), resId, reqWidth, reqHeight);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
      if (imageView != null) {
        imageView.setImageBitmap(bitmap);
      }
      addBitmapToMemoryCache(resId.toString() + "W" + reqWidth + "H" + reqHeight, bitmap);
      tasks.remove(this);
    }
  }

  /**
   * AsyncTask to retrieve images from resources.
   */
  private static class BitmapFromFileTask extends AsyncTask<File, Void, Bitmap> {

    private final ImageView mImageView;
    private File mFile;
    private final int reqWidth;
    private final int reqHeight;
    private final Context mContext;
    private final View[] mColoredViews;


    private BitmapFromFileTask(Context context, ImageView imageView, int reqWidth,
                                    int reqHeight) {
      this.mImageView = imageView;
      this.reqWidth = reqWidth;
      this.reqHeight = reqHeight;
      this.mContext = context;
      this.mColoredViews = null;
      tasks.add(this);
    }

    private BitmapFromFileTask(Context context, ImageView imageView, int reqWidth,
                               int reqHeight, View... coloredViews) {
      this.mImageView = imageView;
      this.reqWidth = reqWidth;
      this.reqHeight = reqHeight;
      this.mContext = context;
      this.mColoredViews = coloredViews;
    }

    @Override
    protected Bitmap doInBackground(File... params) {
      mFile = params[0];
      return decodeSampledBitmapFromFile(mFile, reqWidth, reqHeight);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
      if (mImageView != null) {
        mImageView.setImageBitmap(bitmap);
      }
      if (bitmap != null) {
        addBitmapToMemoryCache(mFile.getAbsolutePath().toString() + "W" + reqWidth + "H" + reqHeight, bitmap);
        colorBackground(bitmap, mColoredViews);
      }
      tasks.remove(this);
    }
  }

  /**
   * AsyncTask to retrieve images from URL.
   */
  private static class BitmapFromUrlTask extends AsyncTask<String, Void, Bitmap> {

    private final ImageView imageView;
    private final int reqWidth;
    private final int reqHeight;
    private URL url;
    private final boolean round;

    private BitmapFromUrlTask(ImageView imageView, int radius) {
      this.imageView = imageView;
      reqWidth = radius;
      reqHeight = radius;
      round = true;
      tasks.add(this);
    }

    private BitmapFromUrlTask(ImageView imageView, int reqWidth, int reqHeight) {
      this.imageView = imageView;
      this.reqWidth = reqWidth;
      this.reqHeight = reqHeight;
      round = false;
      tasks.add(this);
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
      if ((urls != null) && (urls.length > 0)) {
        try {
          url = new URL(urls[0].trim());
          return decodeSampledBitmapFromUrl(url, reqWidth, reqHeight);
        }
        catch (final Exception ex) {
          Log.e(TAG, "Could not load bitmap from" + url);
          return null;
        }
      }
      return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
      if (imageView != null) {
        final BitmapFromUrlTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
        if (this == bitmapWorkerTask) {
          if (round) {
            imageView.setImageBitmap(getCircleBitmap(bitmap, reqWidth));
          } else {
            imageView.setImageBitmap(bitmap);
          }
        }
      }
      addBitmapToMemoryCache(url.toString(), bitmap);
      tasks.remove(this);
    }
  }

  /**
   * BitmapDrawable with reference to the async action_item loading the bitmap.
   *
   * @author chiljagossow
   *
   */
  static class AsyncDrawable extends BitmapDrawable {
    private final WeakReference<BitmapFromUrlTask> asyncTaskReference;

    private AsyncDrawable(Resources res, Bitmap bitmap, BitmapFromUrlTask task) {
      super(res, bitmap);
      asyncTaskReference = new WeakReference<BitmapFromUrlTask>(task);
    }

    private BitmapFromUrlTask getAsyncTask() {
      return asyncTaskReference.get();
    }
  }

  private static final String TAG = "BitmapUtility";

  private static ArrayList<AsyncTask> tasks = new ArrayList<AsyncTask>();

  private static LruCache<String, Bitmap> memoryCache;

  private static TreeMap<String, ImageView> requestedBitmaps = new TreeMap<String, ImageView>();

  static {

    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    final int cacheSize = maxMemory / 8;

    memoryCache = new LruCache<String, Bitmap>(cacheSize) {
      @Override
      protected int sizeOf(String key, Bitmap bitmap) {
        return bitmap.getByteCount() / 1024;
      }
    };
  }

  /**
   * Transforms a bitmap into a circle shape.
   *
   * @param bitmap
   * @param diameterPixels
   * @return bitmap as circle shape
   */
  public static Bitmap getCircleBitmap(Bitmap bitmap, int diameterPixels) {
    if (bitmap != null) {
      final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
              Bitmap.Config.ARGB_8888);
      final Canvas canvas = new Canvas(output);

      final int color = 0xff424242;
      final Paint paint = new Paint();
      final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
      final RectF rectF = new RectF(rect);
      final float roundPx = diameterPixels;

      paint.setAntiAlias(true);
      canvas.drawARGB(0, 0, 0, 0);
      paint.setColor(color);
      canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

      paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
      canvas.drawBitmap(bitmap, rect, rect, paint);

      return output;
    }
    return null;
  }

  /**
   * Loads bitmap from resources.
   *
   * @param context
   *          context to access the recourses
   * @param resId
   *          resource id of the bitmap to be loaded
   * @param imageView
   *          mImageView the bitmap will be attached to
   * @param reqWidth
   *          width in pixels
   * @param reqHeight
   *          height in pixels
   */
  public static void loadBitmap(Context context, Integer resId, ImageView imageView,
                                int reqWidth, int reqHeight) {
    // check cache
    // use width and height as part of key to enforce reloading when resizing
    final Bitmap bitmap = getBitmapFromMemCache(resId.toString() + "W" + reqWidth + "H" + reqHeight);
    if (bitmap != null) {
      imageView.setImageBitmap(bitmap);
      return;
    }
    final BitmapFromResourcesTask task = new BitmapFromResourcesTask(context, imageView, reqWidth,
            reqHeight);

    task.execute(resId);
  }

  /**
   * Loads bitmap from resources.
   *
   * @param context
   *          context to access the recourses
   * @param file
   *          resource id of the bitmap to be loaded
   * @param imageView
   *          mImageView the bitmap will be attached to
   * @param reqWidth
   *          width in pixels
   * @param reqHeight
   *          height in pixels
   */
  public static void loadBitmap(Context context, File file, ImageView imageView,
                                int reqWidth, int reqHeight) {
    // check cache
    // use width and height as part of key to enforce relaoding when resizing
    final Bitmap bitmap = getBitmapFromMemCache(file.getAbsolutePath().toString() + "W" + reqWidth + "H" + reqHeight);
    if (bitmap != null) {
      imageView.setImageBitmap(bitmap);
      return;
    }
    final BitmapFromFileTask task = new BitmapFromFileTask(context, imageView, reqWidth,
            reqHeight);

    task.execute(file);
  }

  public static void loadBitmap(Context context, File file, ImageView imageView,
                                int reqWidth, int reqHeight, View... colorViews) {
    // check cache
    // use width and height as part of key to enforce relaoding when resizing
    final Bitmap bitmap = getBitmapFromMemCache(file.getAbsolutePath().toString() + "W" + reqWidth + "H" + reqHeight);
    if (bitmap != null) {
      imageView.setImageBitmap(bitmap);
      colorBackground(bitmap, colorViews);
      return;
    }
    final BitmapFromFileTask task = new BitmapFromFileTask(context, imageView, reqWidth,
            reqHeight, colorViews);

    task.execute(file);
  }

  private static void colorBackground(Bitmap bitmap, View[] colorViews) {
    Palette palette = Palette.generate(bitmap);
    if (colorViews != null) {
      for (int i = 0; i < colorViews.length; i++) {
        colorBackgroundDark(colorViews[i], palette);
      }
    }
  }

  private static void colorBackgroundDark(View view, Palette palette) {
    int color = palette.getDarkVibrantColor(R.color.primary);
    view.setBackgroundColor(color);
  }

  private static void colorBackground(View view, Palette palette) {
    int color = palette.getVibrantColor(R.color.primary);
    view.setBackgroundColor(color);
  }


  /**
   * Cancels all background tasks that haven't completed. Should be called from
   * activity's onDestroy() method.
   */
  public static void onDestroy() {
    if (!tasks.isEmpty()) {
      for (final AsyncTask task : tasks) {
        task.cancel(true);
      }
      tasks.clear();
    }
    Log.i(TAG, "All async tasks cancelled.");
  }

  private static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
    if (getBitmapFromMemCache(key) == null) {
      if (memoryCache != null) {
        memoryCache.put(key, bitmap);
      }
    }
  }

  private static int calculateSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

    final int height = options.outHeight;
    final int width = options.outWidth;
    int sampleSize = 1;

    if ((height > reqHeight) || (width > reqWidth)) {

      final int halfHeight = height / 2;
      final int halfWidth = width / 2;

      // Calculate the largest value that is a power of 2 and keeps
      // both height and width larger than the requested height and width.
      while (((halfHeight / sampleSize) > reqHeight) && ((halfWidth / sampleSize) > reqWidth)) {
        sampleSize *= 2;
      }
    }
    return sampleSize;
  }

  private static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth,
                                                        int reqHeight) {

    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeResource(res, resId, options);

    options.inSampleSize = calculateSampleSize(options, reqWidth, reqHeight);

    options.inJustDecodeBounds = false;
    return BitmapFactory.decodeResource(res, resId, options);
  }

  private static Bitmap decodeSampledBitmapFromFile(File file, int reqWidth,
                                                        int reqHeight) {

    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(file.getAbsolutePath(), options);

    options.inSampleSize = calculateSampleSize(options, reqWidth, reqHeight);

    options.inJustDecodeBounds = false;
    return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
  }

  private static Bitmap decodeSampledBitmapFromUrl(URL url, int reqWidth, int reqHeight)
          throws IOException {
    URLConnection connection;
    Bitmap bitmap = null;
    InputStream inputStream = null;
    try {
      connection = url.openConnection();

      inputStream = new BufferedInputStream(connection.getInputStream());

      final BitmapFactory.Options options = new BitmapFactory.Options();
      options.inJustDecodeBounds = true;
      BitmapFactory.decodeStream(inputStream, null, options);
      inputStream.close();

      options.inSampleSize = calculateSampleSize(options, reqWidth, reqHeight);

      options.inJustDecodeBounds = false;
      connection = url.openConnection();
      inputStream = new BufferedInputStream(connection.getInputStream());
      bitmap = BitmapFactory.decodeStream(inputStream, null, options);
    }
    catch (IOException e) {
      Log.e(TAG, e.toString());
    }
    finally {
      if (inputStream != null) {
        inputStream.close();
      }
    }
    return bitmap;
  }

  private static Bitmap getBitmapFromMemCache(String key) {
    return memoryCache.get(key);
  }

  private static BitmapFromUrlTask getBitmapWorkerTask(ImageView imageView) {
    if (imageView != null) {
      final Drawable drawable = imageView.getDrawable();
      if (drawable instanceof AsyncDrawable) {
        final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
        return asyncDrawable.getAsyncTask();
      }
    }
    return null;
  }
}

