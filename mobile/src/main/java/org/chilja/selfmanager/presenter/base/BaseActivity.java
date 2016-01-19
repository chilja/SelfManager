package org.chilja.selfmanager.presenter.base;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.chilja.selfmanager.R;
import org.chilja.selfmanager.presenter.edit.EditActivity;


public abstract class BaseActivity extends AppCompatActivity {


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

  public Toolbar setUpToolbar() {
    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(mToolbar);
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



  public void startEditActivity(int type) {
    final Intent intent = new Intent(this, EditActivity.class);
    intent.putExtra(EditActivity.TYPE, type);
    startActivity(intent);
  }

}
