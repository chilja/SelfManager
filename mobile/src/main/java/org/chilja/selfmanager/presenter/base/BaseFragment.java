package org.chilja.selfmanager.presenter.base;

/**
 * Created by chiljagossow on 6/6/15.
 * <p/>
 * Basic class for fragments.
 *
 * @author chiljagossow
 */

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;

import org.chilja.selfmanager.presenter.edit.EditActivity;

public abstract class BaseFragment extends Fragment implements Scrollable {

  protected boolean mIsResumed = false;
  protected boolean mIsAttached = false;

  protected String mTitle = "";

  protected int mDisplayWidth;
  protected int mDisplayHeight;

  protected int mFragmentId;

  protected OnScrollListener mListener;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mIsAttached = true;
    mDisplayWidth = getResources().getDisplayMetrics().widthPixels;
    mDisplayHeight = getResources().getDisplayMetrics().heightPixels;
  }

  @Override
  public void onResume() {
    super.onResume();
    mIsResumed = true;
  }

  @Override
  public void onPause() {
    super.onPause();
    mIsResumed = false;
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mIsAttached = false;
  }

  protected void refresh() {
  }

  public int getIconResId() {
    return 0;
  }

  public abstract String getTAG();

  public String getTitle() {
    return mTitle;
  }

  public void setTitle(String title) {
    mTitle = title;
  }

  public interface OnScrollListener {
    void onScrollY(int scrollY, int dy, int fragmentID);
  }

  public class ScrollListener extends RecyclerView.OnScrollListener {

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
      super.onScrolled(recyclerView, dx, dy);
      mListener.onScrollY(getScrollY(), dy, mFragmentId);
    }
  }

  public void startEditActivity(Activity activity, int type) {
    final Intent intent = new Intent(activity, EditActivity.class);
    intent.putExtra(EditActivity.TYPE, type);
    startActivity(intent);
  }
}
