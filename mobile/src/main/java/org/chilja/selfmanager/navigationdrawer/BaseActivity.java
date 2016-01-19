package org.chilja.selfmanager.navigationdrawer;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.chilja.selfmanager.R;

public abstract class BaseActivity extends AppCompatActivity {

  protected NavigationDrawerFragment mNavigationDrawerFragment;

  public Toolbar getToolbar() {
    return mToolbar;
  }

  /**
   * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
   */

  protected Toolbar mToolbar;
  protected int mDisplayWidth;
  protected int mDisplayHeight;

  protected void setUp() {
    mDisplayWidth = getResources().getDisplayMetrics().widthPixels;
    mDisplayHeight = getResources().getDisplayMetrics().heightPixels;
  }

  public Toolbar setUpToolbar(int resId) {
    mToolbar = (Toolbar) findViewById(resId);
    setSupportActionBar(mToolbar);
    mToolbar.setNavigationIcon(R.drawable.ic_drawer);
    return mToolbar;
  }

  public void startBrowserActivity(String uri) {
    if ((uri != null) && (uri != "")) {
      uri = uri.trim();
      final Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setData(Uri.parse(uri));
      startActivity(intent);
    }
  }

  public void startActivity(Class<?> cls) {
    final Intent intent = new Intent(this, cls);
    startActivity(intent);
  }

  protected void setUpDrawer(DrawerAdapter.MenuItem[] menuItems, int drawerResId, int layoutResId) {

    mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentByTag(
            NavigationDrawerFragment.TAG);

    if (mNavigationDrawerFragment == null) {
      mNavigationDrawerFragment = new NavigationDrawerFragment();
      getSupportFragmentManager().beginTransaction()
              .replace(drawerResId, mNavigationDrawerFragment,
                      NavigationDrawerFragment.TAG)
              .commit();
    }

    // Set up the drawer.
//    DrawerAdapter.MenuItem[] menuItems = new DrawerAdapter.MenuItem[4];
//    menuItems[ACTION] = new DrawerAdapter.MenuItem("Next Actions", R.drawable.ic_action);
//    menuItems[GOALS] = new DrawerAdapter.MenuItem("Goals", R.drawable.ic_target);
//    menuItems[WAIT_ITEMS] = new DrawerAdapter.MenuItem("Waiting for", R.drawable.ic_wait);
//    menuItems[MISSION] = new DrawerAdapter.MenuItem("Mission", R.drawable.ic_goal);

    mNavigationDrawerFragment.setUp(this, drawerResId, (DrawerLayout) findViewById(layoutResId),
            mToolbar, menuItems);

    mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        mNavigationDrawerFragment.openDrawer();
      }
    });
  }
}
