package org.chilja.selfmanager;

import android.test.ActivityInstrumentationTestCase2;

import org.chilja.selfmanager.presenter.base.MainActivity;
import org.chilja.selfmanager.presenter.items.GoalFragment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by chiljagossow on 7/23/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21)
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

  public MainActivityTest() {
    super(MainActivity.class);
  }

  public void setUp() {
    MainActivity activity = getActivity();
    assertNotNull(activity);
  }

  @Test
  public void testFragmentPersistedBetweenLaunches() {

    final String TEST_VISIBLE_FRAGMENT = GoalFragment.TAG;
    final MainActivity activity = getActivity();

    activity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        activity.showFragment(TEST_VISIBLE_FRAGMENT);
      }
    });

    activity.finish();
    setActivity(null);  // Required to force creation of a new activity

    // Relaunch the activity
    final MainActivity activityRelaunched = this.getActivity();
    assertEquals(TEST_VISIBLE_FRAGMENT, activityRelaunched.getVisibleFragmentTAG());

  }

}
