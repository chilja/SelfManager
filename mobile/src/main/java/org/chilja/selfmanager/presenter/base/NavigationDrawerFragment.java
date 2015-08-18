package org.chilja.selfmanager.presenter.base;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.chilja.selfmanager.R;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment implements DrawerAdapter.OnClickListener {

  /**
   * Remember the mPosition of the selected item.
   */
  private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
  /**
   * Per the design guidelines, you should show the drawer on launch until the user manually
   * expands it. This shared preference tracks this.
   */
  private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
  public static String TAG = "NavigationDrawerFragment";
  RecyclerView.LayoutManager mLayoutManager;
  ActionBarDrawerToggle mDrawerToggle;
  /**
   * A pointer to the current callbacks instance (the Activity).
   */
  private NavigationDrawerCallbacks mCallbacks;
  private DrawerLayout mDrawerLayout;
  private RecyclerView mRecyclerView;
  private View mFragmentContainerView;
  private int mCurrentSelectedPosition = 2;
  private boolean mFromSavedInstanceState;
  private boolean mUserLearnedDrawer;
  private DrawerAdapter.MenuItem[] mMenuItems;

  public NavigationDrawerFragment() {
  }

  public void openDrawer() {
    mDrawerLayout.openDrawer(GravityCompat.START);

  }

  /**
   * Users of this fragment must call this method to set up the navigation drawer interactions.
   *
   * @param fragmentId   The android:id of this fragment in its activity's layout.
   * @param drawerLayout The DrawerLayout containing this fragment's UI.
   */
  public void setUp(Activity activity, int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar, DrawerAdapter.MenuItem[] menuItems) {
    mMenuItems = menuItems;
    mFragmentContainerView = activity.findViewById(fragmentId);
    mDrawerLayout = drawerLayout;

    // set a custom shadow that overlays the actions content when the drawer opens
    mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
    // set up the drawer's list view with items and click listener

    // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
    // per the navigation drawer design guidelines.
    if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
      //      mDrawerLayout.openDrawer(mFragmentContainerView);
    }

    mDrawerToggle = new ActionBarDrawerToggle(activity, mDrawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

      @Override
      public void onDrawerOpened(View drawerView) {
        super.onDrawerOpened(drawerView);
      }

      @Override
      public void onDrawerClosed(View drawerView) {
        super.onDrawerClosed(drawerView);
      }
    };

    mDrawerLayout.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
    mDrawerToggle.syncState();

  }

  private void selectItem(int position) {
    mCurrentSelectedPosition = position;

    if (mDrawerLayout != null) {
      mDrawerLayout.closeDrawer(mFragmentContainerView);
    }
    if (mCallbacks != null) {
      mCallbacks.onNavigationDrawerItemSelected(position);
    }
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      mCallbacks = (NavigationDrawerCallbacks) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Read in the flag indicating whether or not the user has demonstrated awareness of the
    // drawer. See PREF_USER_LEARNED_DRAWER for details.
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
    mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

    if (savedInstanceState != null) {
      mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
      mFromSavedInstanceState = true;
    }

    // Select either the default item (0) or the last selected item.
    //selectItem(mCurrentSelectedPosition);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

    mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
    mRecyclerView.setHasFixedSize(true);

    mRecyclerView.setAdapter(new DrawerAdapter(mMenuItems, this));

    mLayoutManager = new LinearLayoutManager(getActivity());

    mRecyclerView.setLayoutManager(mLayoutManager);

    return mRecyclerView;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    // Indicate that this fragment would like to influence the set of actions in the action bar.
    setHasOptionsMenu(true);
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mCallbacks = null;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    // If the drawer is open, show the global app actions in the action bar. See also
    // showGlobalContextActionBar, which controls the top-left area of the action bar.
    if (mDrawerLayout != null && isDrawerOpen()) {
      inflater.inflate(R.menu.global, menu);
      return;
    }
    super.onCreateOptionsMenu(menu, inflater);
  }

  public boolean isDrawerOpen() {
    return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onClick(int position) {
    closeDrawer();
    mCallbacks.onNavigationDrawerItemSelected(position);
  }

  public void closeDrawer() {
    mDrawerLayout.closeDrawer(GravityCompat.START);

  }


  /**
   * Callbacks interface that all activities using this fragment must implement.
   */
  public static interface NavigationDrawerCallbacks {
    /**
     * Called when an item in the navigation drawer is selected.
     */
    void onNavigationDrawerItemSelected(int position);
  }
}
