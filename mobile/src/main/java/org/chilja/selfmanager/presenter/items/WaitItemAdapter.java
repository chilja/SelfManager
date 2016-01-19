package org.chilja.selfmanager.presenter.items;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import org.chilja.selfmanager.R;
import org.chilja.selfmanager.model.Goal;
import org.chilja.selfmanager.model.WaitItem;
import org.chilja.selfmanager.presenter.edit.EditActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by chiljagossow on 8/23/15.
 */
public class WaitItemAdapter extends RecyclerView.Adapter<WaitItemViewHolder> {
  private ArrayList<WaitItem> mItems;
  private Context mContext;
  private Map<Integer, Goal> mGoalMap;
  private boolean mShowGoal;

  public WaitItemAdapter(ArrayList<WaitItem> items, Context context) {
    mItems = items;
    mContext = context;
  }

  public WaitItemAdapter(ArrayList<WaitItem> items, Map<Integer, Goal> goalMap, Context context) {
    mItems = items;
    mContext = context;
    mGoalMap = goalMap;
    mShowGoal = true;
  }

  @Override
  public WaitItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final View view = View.inflate(parent.getContext(), R.layout.wait_item, null);
    return new WaitItemViewHolder(view, parent.getContext());
  }

  @Override
  public void onBindViewHolder(final WaitItemViewHolder holder, int position) {
    final WaitItem item = mItems.get(position);

    Goal goal = null;
    if (mGoalMap != null) {
      goal = mGoalMap.get(item.getGoalId());
    }
    if (mShowGoal && goal != null) {
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

    holder.setWaitItem(item);
    holder.setState(item.isDone());

  }

  public long getItemId(int position) {
    final WaitItem item = mItems.get(position);
    return item.getId();
  }

  @Override
  public int getItemCount() {
    return mItems.size();
  }
}
