package org.chilja.selfmanager.presenter.base;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import org.chilja.selfmanager.R;

/**
 * Created by chiljagossow on 6/8/15.
 */
public class HeaderAnimationHelper {

  private static final String TAG = "HeaderAnimationHelper";

  private int mToolbarHeight;
  private int mTabHeight;
  private Integer mMaxTabTranslationY;
  private Integer mMinTabTranslationY;
  private int mMaxTitleTranslationY;
  private Toolbar mToolbar;
  private View mTabsBackground;
  private ImageView mHeaderImage;
  private View mTabs;
  private View mTitle;

  public HeaderAnimationHelper(Context context, Toolbar toolbar, View tabs, View tabsBackground, ImageView headerImage, View title) {
    mToolbar = toolbar;
    mTabsBackground = tabsBackground;
    mTabs = tabs;
    mHeaderImage = headerImage;
    mTitle = title;
    initTranslationYValues(context);
  }

  private void initTranslationYValues(Context context) {
    mTabHeight = context.getResources().getDimensionPixelSize(R.dimen.tab_height);
    mToolbarHeight = context.getResources().getDimensionPixelSize(R.dimen.toolbar_height);
    mMaxTabTranslationY = context.getResources().getDimensionPixelSize(R.dimen.max_translation);
    mMaxTitleTranslationY = context.getResources()
            .getDimensionPixelSize(R.dimen.max_translation_title);
    mMinTabTranslationY = 0;
  }

  public void animateHeaderTransition(int scrollY) {

    // tab animations
    int tabTranslationY = calculateTabTranslationY(scrollY, true);
    animateTabsTranslation(scrollY, tabTranslationY);
    updateToolbar(tabTranslationY);
  }

  private Integer calculateTabTranslationY(int scrollY, boolean showTabs) {
    int minTranslation = -mTabHeight;
    if (showTabs) {
      // scrolling up -> show tabs
      minTranslation = mMinTabTranslationY;
    }
    if ((mMaxTabTranslationY + scrollY) <= minTranslation) {
      return minTranslation;
    }
    if ((mMaxTabTranslationY + scrollY) < mMinTabTranslationY) {
      // show
      return mMinTabTranslationY;
    }
    // translate
    return mMaxTabTranslationY + scrollY;
  }

  private void animateTabsTranslation(int scrollY, int tabTranslationY) {
    cancelTabsAnimation();
    Log.d(TAG, "animate tabs to " + tabTranslationY);
    animateYTranslation(mTabs, tabTranslationY, 300);
    animateYTranslation(mTabsBackground, (tabTranslationY - mMaxTabTranslationY), 300);
    animateYTranslation(mHeaderImage, (scrollY * 2), 300);
    // tabs background alpha
    float alpha = calculateAlpha(tabTranslationY);
    mTabsBackground.animate().alpha(alpha).setDuration(300).start();
  }

  private void updateToolbar(int tabTranslationY) {
    // show toolbar if tab translation leaves sufficient room for tool bar
    float translationY = mToolbar.getTranslationY();
    if (tabTranslationY >= mToolbarHeight) {
      if (translationY <= mMinTabTranslationY) {
        // show
        animateYTranslation(mToolbar, 0, 100);
      }
    } else {
      // hide
      if (translationY != -mToolbarHeight) {
        if (tabTranslationY == mMinTabTranslationY) {
          // animation would be too slow
          if (mToolbar.getAnimation() != null) {
            mToolbar.getAnimation().cancel();
          }
          mToolbar.setTranslationY(-mToolbarHeight);
        } else {
          animateYTranslation(mToolbar, -mToolbarHeight, 30);
        }
      }
    }
  }

  private void cancelTabsAnimation() {
    if (mTabs.getAnimation() != null) {
      mTabs.getAnimation().cancel();
    }
    if (mTabsBackground.getAnimation() != null) {
      mTabsBackground.getAnimation().cancel();
    }
    if (mHeaderImage.getAnimation() != null) {
      mHeaderImage.getAnimation().cancel();
    }
  }

  private void animateYTranslation(final View view, final int y, int duration) {

    if (view.getAnimation() != null) {
      return;
    }

    Animator.AnimatorListener listener = new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {
      }

      @Override
      public void onAnimationEnd(Animator animation) {
        // make sure the animation ends in the correct end mPosition
        Log.d(TAG, "end animation on " + y);
//        view.setTranslationY(y);
      }

      @Override
      public void onAnimationCancel(Animator animation) {
//        view.setTranslationY(y);
      }

      @Override
      public void onAnimationRepeat(Animator animation) {
      }

    };

    view.animate()
            .translationY(y)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .setDuration(duration)
//            .setListener(listener)
            .start();

  }

  private float calculateAlpha(int tabTranslationY) {
    // tabs background alpha
    float alpha = 2 * (float) (mMaxTabTranslationY - tabTranslationY) / (mMaxTabTranslationY - mMinTabTranslationY);
    if (alpha > 1) {
      return 1;
    }
    if (alpha < 0) {
      return 0;
    }
    return alpha;
  }

  public void onPageScrolled(Integer scrollY, int dy) {
    boolean showTabs = false;
    if (dy < 0) {
      showTabs = true;
    }
    int previousTabTranslation = (int) mTabs.getTranslationY();
    int tabTranslationY = calculateTabTranslationY(scrollY, showTabs);
    updateTabsBackground(tabTranslationY);
    updateToolbar(tabTranslationY);
    if (((previousTabTranslation == mMinTabTranslationY) && (tabTranslationY == -mTabHeight)) ||
            ((previousTabTranslation == -mTabHeight) && (tabTranslationY == mMinTabTranslationY))) {
      // show or hide tabs
      translateTabs(scrollY, tabTranslationY);
//      animateTabsTranslation(scrollY, tabTranslationY);
    } else if (previousTabTranslation != tabTranslationY) {
      // translation has changed
      translateTabs(scrollY, tabTranslationY);
    }
  }

  /**
   *
   */
  private void updateTabsBackground(int tabTranslationY) {
    // animate container background
    float alpha = calculateAlpha(tabTranslationY);
    mTabsBackground.setAlpha(alpha);
    if (alpha == 1) {
      mTabsBackground.setElevation(4.0F);
    } else {
      mTabsBackground.setElevation(0.0F);
    }
    mTitle.setAlpha(1 - alpha);
  }

  private void translateTabs(int scrollY, int tabTranslationY) {
    cancelTabsAnimation();
    Log.d(TAG, "translate tabs to " +  tabTranslationY);
    mTabs.setTranslationY(tabTranslationY);
    mTabsBackground.setTranslationY(tabTranslationY - mMaxTabTranslationY);
    mHeaderImage.setTranslationY(scrollY * 2);
    mTitle.setTranslationY(scrollY + mMaxTitleTranslationY);
  }
}
