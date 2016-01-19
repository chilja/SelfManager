package org.chilja.selfmanager.presenter.items;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.chilja.selfmanager.R;
import org.chilja.selfmanager.model.Goal;
import org.chilja.selfmanager.model.WaitItem;
import org.chilja.selfmanager.resolvers.GoalResolver;
import org.chilja.selfmanager.resolvers.WaitItemResolver;
import org.chilja.selfmanager.presenter.base.BaseFragment;
import org.chilja.selfmanager.util.BitmapUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WaitItemsFragment extends BaseFragment
        implements WaitItemResolver.WaitItemCallback, GoalResolver.GoalCallback {

  public static final String TAG = "WaitItemFragment";

  private RecyclerView mRecyclerView;
  private ArrayList<WaitItem> mWaitItems = new ArrayList<WaitItem>();
  private Map<Integer, Goal> mGoalMap = null;

  public WaitItemsFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @return A new instance of fragment ActionFragment.
   */
  public static WaitItemsFragment newInstance() {
    WaitItemsFragment fragment = new WaitItemsFragment();
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_wait_item, container, false);
    mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
    mRecyclerView.setLayoutManager(layoutManager);
    return view;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    new WaitItemResolver(activity).getWaitItems(this);
    new GoalResolver(activity).getGoals(this);
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

  }

  @Override
  public void onStop() {
    super.onStop();
    for (WaitItem item: mWaitItems) {
      if (item.isDone()) {
        item.delete(getActivity());
      }
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  @Override
  public String getTAG() {
    return null;
  }

  @Override
  public String getTitle() {
    return "Waiting for";
  }

  @Override
  public int getScrollY() {
    return 0;
  }

  @Override
  public void scrollTo(int y) {

  }

  @Override
  public void onWaitItemsReturned(ArrayList<WaitItem> items) {
    mWaitItems = items;
    Collections.sort(mWaitItems);
    setUpRecyclerView(mWaitItems, mGoalMap, getActivity());
  }

  @Override
  public void onGoalsReturned(ArrayList<Goal> goals) {
    mGoalMap = new HashMap<Integer, Goal>();
    if (goals != null) {
      for (Goal goal : goals) {
        mGoalMap.put(goal.getId(), goal);
      }
    }
    setUpRecyclerView(mWaitItems, mGoalMap, getActivity());
  }

  private void setUpRecyclerView(ArrayList<WaitItem> items, Map goals, Context context) {
    if (mRecyclerView != null && items != null && goals != null) {
      mRecyclerView.setAdapter(new WaitItemAdapter(items, goals, context));
    }
  }

}
