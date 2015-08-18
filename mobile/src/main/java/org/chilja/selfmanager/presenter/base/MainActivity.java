package org.chilja.selfmanager.presenter.base;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import org.chilja.selfmanager.R;
import org.chilja.selfmanager.presenter.edit.EditActivity;
import org.chilja.selfmanager.presenter.items.ActionFragment;
import org.chilja.selfmanager.presenter.items.DetailActivity;
import org.chilja.selfmanager.presenter.items.GoalFragment;
import org.chilja.selfmanager.presenter.items.MissionFragment;
import org.chilja.selfmanager.presenter.items.WaitItemsFragment;

/**
 * Created by chiljagossow on 6/4/15.
 */
public class MainActivity extends BaseActivity implements
        GoalFragment.OnFragmentInteractionListener,
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        BaseFragment.OnScrollListener{

  public static final String TAG = "MainActivity";

  public static final int ACTION = 0;
  public static final int GOALS = 2;
  public static final int WAIT_ITEMS = 1;
  public static final int MISSION = 3;

  private static final int SIZE = 4;

  protected NavigationDrawerFragment mNavigationDrawerFragment;
  private GoalFragment mGoalFragment;
  private ActionFragment mActionFragment;
  private WaitItemsFragment mWaitItemsFragment;
  private MissionFragment mMissionFragment;

  public String getVisibleFragmentTAG() {
    return mVisibleFragmentTAG;
  }

  private String mVisibleFragmentTAG = ActionFragment.TAG;

  private static final String FRAGMENT_TAG = "fragment_tag";

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    setUp();
    setUpDrawer();

    mGoalFragment = (GoalFragment) getSupportFragmentManager().findFragmentByTag(
            GoalFragment.TAG);

    if (mGoalFragment == null) {
      mGoalFragment = GoalFragment.newInstance(mDisplayWidth);
    }

    mActionFragment = (ActionFragment) getSupportFragmentManager().findFragmentByTag(
            ActionFragment.TAG);

    if (mActionFragment == null) {
      mActionFragment = ActionFragment.newInstance();
    }

    mWaitItemsFragment = (WaitItemsFragment) getSupportFragmentManager().findFragmentByTag(
            WaitItemsFragment.TAG);

    if (mWaitItemsFragment == null) {
      mWaitItemsFragment = WaitItemsFragment.newInstance();
    }

    mMissionFragment = (MissionFragment) getSupportFragmentManager().findFragmentByTag(MissionFragment.TAG);
    if (mMissionFragment == null) {
      SharedPreferences sharedPref = getSharedPreferences(
              getString(R.string.preference_chief_aim_key), Context.MODE_PRIVATE);
      String defaultValue = getResources().getString(R.string.aim);
      String value = sharedPref.getString(getString(R.string.saved_chief_aim), defaultValue);
      mMissionFragment = MissionFragment.newInstance(value, "My Mission");
    }
  }

  protected void setUpDrawer() {
    setUpToolbar();

    mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentByTag(
            NavigationDrawerFragment.TAG);

    if (mNavigationDrawerFragment == null) {
      mNavigationDrawerFragment = new NavigationDrawerFragment();
      getSupportFragmentManager().beginTransaction()
              .replace(R.id.navigation_drawer, mNavigationDrawerFragment,
                      NavigationDrawerFragment.TAG)
              .commit();
    }

    // Set up the drawer.
    DrawerAdapter.MenuItem[] menuItems = new DrawerAdapter.MenuItem[SIZE];
    menuItems[ACTION] = new DrawerAdapter.MenuItem("Next Actions", R.drawable.ic_action);
    menuItems[GOALS] = new DrawerAdapter.MenuItem("Goals", R.drawable.ic_target);
    menuItems[WAIT_ITEMS] = new DrawerAdapter.MenuItem("Waiting for", R.drawable.ic_wait);
    menuItems[MISSION] = new DrawerAdapter.MenuItem("Mission", R.drawable.ic_goal);

    mNavigationDrawerFragment.setUp(this, R.id.navigation_drawer,
            (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar, menuItems);

    mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        mNavigationDrawerFragment.openDrawer();
      }
    });
    mToolbar.setNavigationIcon(R.drawable.ic_drawer);

  }


  @Override
  protected void onDestroy() {
    super.onDestroy();
    Log.i(TAG, "onDestroy");
  }

  @Override
  protected void onPause() {
    super.onPause();
    SharedPreferences preferences = getSharedPreferences();
    SharedPreferences.Editor editor = preferences.edit();
    editor.putString(FRAGMENT_TAG, mVisibleFragmentTAG);
    editor.commit();
  }

  @Override
  public void onResume() {
    super.onResume();
    SharedPreferences preferences = getSharedPreferences();
    mVisibleFragmentTAG = preferences.getString(FRAGMENT_TAG, mVisibleFragmentTAG);
    showFragment(mVisibleFragmentTAG);
  }

  private SharedPreferences getSharedPreferences() {
    return getSharedPreferences(getString(R.string.preference_main),
            Context.MODE_PRIVATE);
  }

  public void showFragment(String tag) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    BaseFragment fragment = null;
    mVisibleFragmentTAG = tag;
    switch (tag) {
      case ActionFragment.TAG:
        fragment = mActionFragment;
        break;
      case GoalFragment.TAG:
        fragment =  mGoalFragment;
        break;
      case WaitItemsFragment.TAG:
        fragment = mWaitItemsFragment;
        break;
      case MissionFragment.TAG:
        fragment = mMissionFragment;
        break;
    }
    if (fragment != null) {
      fragmentManager.beginTransaction()
              .replace(R.id.container, fragment, fragment.getTAG())
              .commit();
      getSupportActionBar().setTitle(fragment.getTitle());
      invalidateOptionsMenu();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
//    if (mNavigationDrawerFragment != null && mNavigationDrawerFragment.isDrawerOpen()) {
//      return true;
//    }
    switch (mVisibleFragmentTAG) {
      case ActionFragment.TAG:
        getMenuInflater().inflate(R.menu.actions, menu);
        return true;
      case GoalFragment.TAG:
        getMenuInflater().inflate(R.menu.goals, menu);
        return true;
      case MissionFragment.TAG:
        getMenuInflater().inflate(R.menu.mission, menu);
        return true;
    }
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public void onNavigationDrawerItemSelected(int position) {
    switch (position -1) {
      case ACTION:
        showFragment(ActionFragment.TAG);
        break;
      case GOALS:
        showFragment(GoalFragment.TAG);
        break;
      case WAIT_ITEMS:
        showFragment(WaitItemsFragment.TAG);
        break;
      case MISSION:
        showFragment(MissionFragment.TAG);
        break;
    }

  }

  @Override
  public void onGoalSelected(int id) {
    final Intent intent = new Intent(this, DetailActivity.class);
    intent.putExtra(DetailActivity.ARG_GOAL_ID, id);
    startActivity(intent);
  }

  public void onAddActionClick(View view) {
    startEditActivity(EditActivity.NEW_ACTION);
  }

  public void onAddGoalClick(View view) {
    startEditActivity(EditActivity.NEW_GOAL);
  }

  public void onAddWaitItemClick(View view) {
    startEditActivity(EditActivity.NEW_WAIT_ITEM);
  }

  @Override
  public void onScrollY(int scrollY, int dy, int fragmentID) {

  }
}
