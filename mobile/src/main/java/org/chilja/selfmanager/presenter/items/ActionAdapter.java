package org.chilja.selfmanager.presenter.items;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import org.chilja.selfmanager.R;
import org.chilja.selfmanager.model.Action;
import org.chilja.selfmanager.model.Goal;
import org.chilja.selfmanager.presenter.edit.EditActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by chiljagossow on 8/20/15.
 */
class ActionAdapter extends RecyclerView.Adapter<ActionViewHolder> {
  private ArrayList<Action> mItems;
  private Context mContext;
  private Map<Integer, Goal> mGoalMap;
  private boolean mShowGoal;

  public ActionAdapter(ArrayList<Action> items, Map<Integer, Goal> goalMap, Context context) {
    mItems = items;
    mContext = context;
    mGoalMap = goalMap;
    mShowGoal = true;
  }

  public ActionAdapter(ArrayList<Action> items, Context context) {
    mItems = items;
    mContext = context;
  }

  @Override
  public ActionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = null;
    if (mShowGoal) {
      view = View.inflate(parent.getContext(), R.layout.action_item, null);
    } else {
      view = View.inflate(parent.getContext(), R.layout.action, null);
    }
    return new ActionViewHolder(view, parent.getContext());
  }

  @Override
  public void onBindViewHolder(final ActionViewHolder holder, int position) {
    final Action action = mItems.get(position);

    Goal goal = null;
    if (mGoalMap != null) {
      goal = mGoalMap.get(action.getGoalId());
    }
    if (goal != null) {
      if (goal.getImage() != null) {
        holder.showGoalImage(new File(goal.getImage()), goal.getId());
        holder.hideGoalName();
      } else {
        holder.hideGoalImage();
        holder.showGoalName(goal.getName(), goal.getId());
      }
    } else {
      holder.hideGoalName();
      holder.hideGoalImage();
    }

    holder.setAction(action);

    holder.setState(action.isDone());
  }

  public long getItemId(int position) {
    final Action action = mItems.get(position);
    return action.getId();
  }

  @Override
  public int getItemCount() {
    return mItems.size();
  }

  public void remove(int position) {
    mItems.remove(position);
    notifyItemRemoved(position);
    notifyItemRangeChanged(position, mItems.size());
  }

}
