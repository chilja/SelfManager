package org.chilja.selfmanager;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

import org.chilja.selfmanager.presenter.edit.EditActivity;
import org.chilja.selfmanager.presenter.items.DetailActivity;

import org.junit.Test;

/**
 * Created by chiljagossow on 8/7/15.
 */
public class DetailActivityTest extends ActivityInstrumentationTestCase2<DetailActivity> {

  public DetailActivityTest() {
    super(DetailActivity.class);
  }

  @Test
  public void testGoalIdPersistedBetweenLaunches() {

    final int TEST_GOAL_ID = 12;
    final Intent intent = new Intent(getInstrumentation().getContext(), EditActivity.class);
    intent.putExtra(DetailActivity.ARG_GOAL_ID, TEST_GOAL_ID);
    setActivityIntent(intent);
    final DetailActivity activity = getActivity();

    activity.finish();
    setActivity(null);  // Required to force creation of a new activity

    // Relaunch the activity
    setActivityIntent(null);
    final DetailActivity activityRelaunched = getActivity();
    assertEquals(TEST_GOAL_ID, activityRelaunched.getGoalId());

  }
}
