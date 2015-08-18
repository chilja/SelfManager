package org.chilja.selfmanager.presenter.items;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import org.chilja.selfmanager.R;
import org.chilja.selfmanager.presenter.base.BaseFragment;


/**
 * Created by chiljagossow on 6/7/15.
 */
public class MissionFragment extends BaseFragment {

  public static final String TAG = "MissionFragment";
  private static final String ARG_TEXT = "text";

  private TextView mContentView;
  private ScrollView mScrollView;

  public MissionFragment() {
  }

  /**
   * Returns a new instance of this fragment for the given section
   * number.
   */
  public static MissionFragment newInstance(String content, String title) {
    MissionFragment fragment = new MissionFragment();
    Bundle args = new Bundle();
    args.putString(ARG_TEXT, content);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_mission, container, false);
    mContentView = (TextView) view.findViewById(R.id.content);
    mScrollView = (ScrollView) view.findViewById(R.id.scroll_view);
    mContentView.setText(getArguments().getString(ARG_TEXT));
    return view;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mListener = (OnScrollListener) activity;
  }

  @Override
  public void onResume() {
    super.onResume();

    int scrollY = mScrollView.getScrollY(); //for verticalScrollView
    mListener.onScrollY(scrollY, 0, mFragmentId);
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  @Override
  public int getIconResId() {
    return 0;
  }

  @Override
  public String getTAG() {
    return TAG;
  }

  @Override
  public String getTitle() {
    return "Mission statement";
  }

  public int getScrollY() {
    if (mScrollView != null) {
      return mScrollView.getScrollY();
    }
    return 0;
  }

  public void scrollTo(int y) {
    if (mScrollView != null) {
      mScrollView.scrollTo(0, y);
    }
  }


}
