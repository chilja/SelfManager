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
import android.widget.Toast;

import org.chilja.selfmanager.R;
import org.chilja.selfmanager.model.Action;
import org.chilja.selfmanager.model.Goal;
import org.chilja.selfmanager.resolvers.ActionResolver;
import org.chilja.selfmanager.resolvers.GoalResolver;
import org.chilja.selfmanager.presenter.base.BaseFragment;
import org.chilja.selfmanager.util.BitmapUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ActionFragment extends BaseFragment implements ActionResolver.ActionCallback, GoalResolver.GoalCallback {

  public static final String TAG = "ActionFragment";

  private RecyclerView mRecyclerView;
  private ArrayList<Action> mActions;
  private Map<Integer, Goal> mGoalMap = null;

  public ActionFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @return A new instance of fragment ActionFragment.
   */
  public static ActionFragment newInstance() {
    ActionFragment fragment = new ActionFragment();
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_action, container, false);
    mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
    mRecyclerView.setLayoutManager(layoutManager);
    setUpActionView(mActions, mGoalMap, getActivity());
    return view;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    new ActionResolver(activity).getActions(this);
    new GoalResolver(activity).getGoals(this);
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

  }

  @Override
  public void onStop() {
    super.onStop();
    if (mActions != null) {
      for (Action action : mActions) {
        if (action.isDone()) {
          action.delete(getActivity());
        }
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
    return "Next actions";
  }

  @Override
  public int getScrollY() {
    return 0;
  }

  @Override
  public void scrollTo(int y) {

  }

  @Override
  public void onActionsReturned(ArrayList<Action> actions) {
    mActions = actions;
    Collections.sort(mActions);
    setUpActionView(mActions, mGoalMap, getActivity());
  }

  @Override
  public void onGoalsReturned(ArrayList<Goal> goals) {
    mGoalMap = new HashMap<Integer, Goal>();
    if (goals != null) {
      for (Goal goal : goals) {
        mGoalMap.put(goal.getId(), goal);
      }
    }
    setUpActionView(mActions, mGoalMap, getActivity());
  }

  private void setUpActionView(ArrayList<Action> actions, Map goals, final Context context) {
    if (mRecyclerView != null && actions != null) {
      final ActionAdapter adapter = new ActionAdapter(actions, goals, context);
      mRecyclerView.setAdapter(adapter);
    }
  }
}
