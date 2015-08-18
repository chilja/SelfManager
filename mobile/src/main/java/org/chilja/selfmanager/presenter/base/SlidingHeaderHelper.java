package org.chilja.selfmanager.presenter.base;

/**
 * Created by chiljagossow on 6/12/15.
 */

import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ScrollView;

public class SlidingHeaderHelper {

  public static final String TAG = "CharityDetailFragment";


  private static final String TRANSLATION = "translation";


  private int mMinTranslationY;
  private int mMaxTranslationY;

  // UI elements

  private ImageView mHeaderImageView;
  private View mHeaderView;
  private ScrollView mScrollView;


  public SlidingHeaderHelper(int minTranslationY, int maxTranslationY) {

    mMaxTranslationY = maxTranslationY;
    mMinTranslationY = minTranslationY;
  }

  public void setUp(View header, ImageView headerImage, ScrollView scrollView) {
    mHeaderImageView = headerImage;
    mHeaderView = header;
    mScrollView = scrollView;
    if (mScrollView != null) {
      mScrollView.getViewTreeObserver()
              .addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

                @Override
                public void onScrollChanged() {
                  translateHeader(calculateTranslation(mScrollView.getScrollY()));
                }
              });

      translateHeader(calculateTranslation(mScrollView.getScrollY()));
    }

  }


  private void translateHeader(int translation) {
    if (mHeaderView != null)
      mHeaderView.setTranslationY(translation);
    if (mHeaderImageView != null)
      mHeaderImageView.setTranslationY(translation);
  }

  static int calculateTranslation(int scrollY) {
    return -scrollY/2;
  }

  public static Integer calculateTranslation(int min, int max, int scrollY) {
    if ((max - scrollY) <= min) {
      return min;
    }
    // translate
    return max - scrollY;
  }


}
