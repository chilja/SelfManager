package org.chilja.selfmanager.presenter.items;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.chilja.selfmanager.R;
import org.chilja.selfmanager.model.Action;
import org.chilja.selfmanager.model.Goal;
import org.chilja.selfmanager.resolvers.ActionResolver;
import org.chilja.selfmanager.resolvers.GoalResolver;
import org.chilja.selfmanager.presenter.base.BaseFragment;
import org.chilja.selfmanager.presenter.edit.EditActivity;
import org.chilja.selfmanager.util.BitmapUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ActionFragment extends BaseFragment implements ActionResolver.ActionCallback, GoalResolver.GoalCallback {

  public static final String TAG = "ActionFragment";

  private RecyclerView mRecyclerView;
  private TextView mInspiration;
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
    mInspiration = (TextView) view.findViewById(R.id.inspiration);
    mInspiration.setText(R.string.inspiration_actions);
    setUpActionView(mActions, mGoalMap, getActivity());
    return view;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    GoalResolver dbManager = new GoalResolver(activity);
    new ActionResolver(activity).getActions(this);
    dbManager.getGoals(this);
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

  private void setUpActionView(ArrayList<Action> actions, Map goals, Context context) {
    if (mRecyclerView != null && actions != null) {
      mRecyclerView.setAdapter(new ActionAdapter(actions, context));
    }
  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    private View mView;
    private TextView mTextView;
    private TextView mDueDate;
    private TextView mGoalName;
    private ImageView mGoalImage;
    private ImageView mButton;

    private int mColorDone;
    private int mColorUndone;
    private int mIconChecked = R.drawable.ic_checked;
    private int mIconUnchecked = R.drawable.ic_unchecked;

    public ViewHolder(View view, Context context) {
      super(view);
      mView = view.findViewById(R.id.item_view);
      mTextView = (TextView) view.findViewById(R.id.text);
      mDueDate = (TextView) view.findViewById(R.id.due_date);
      mGoalName = (TextView) view.findViewById(R.id.goal_name);
      mButton = (ImageView) view.findViewById(R.id.button);
      mGoalImage = (ImageView) view.findViewById(R.id.goal_image);
      mColorDone = context.getResources().getColor(R.color.divider);
      mColorUndone = context.getResources().getColor(R.color.primary_light);
    }

    private void setState(boolean done) {
      if (done) {
        mButton.setImageResource(mIconChecked);
        mView.setBackgroundColor(mColorDone);
      } else {
        mButton.setImageResource(mIconUnchecked);
        mView.setBackgroundColor(mColorUndone);
      }
    }

    private void showGoalImage(final Context context, File imageFile, final int id) {
      mGoalImage.setVisibility(View.VISIBLE);
      mGoalImage.setImageBitmap(null);
      mGoalImage.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
          final Intent intent = new Intent(context, DetailActivity.class);
          intent.putExtra(DetailActivity.ARG_GOAL_ID, id);
          context.startActivity(intent);
          return true;
        }
      });
      BitmapUtility.loadBitmap(context, imageFile, mGoalImage, 100,100);
    }

    private void hideGoalImage() {
      mGoalImage.setImageBitmap(null);
    }

    private void showGoalName(final Context context, String name, final int id) {
      mGoalName.setVisibility(View.VISIBLE);
      mGoalName.setText(name);
      mGoalName.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
          final Intent intent = new Intent(context, DetailActivity.class);
          intent.putExtra(DetailActivity.ARG_GOAL_ID, id);
          context.startActivity(intent);
          return true;
        }
      });

    }

    private void hideGoalName() {
      mGoalName.setVisibility(View.GONE);
    }
  }

  class ActionAdapter extends RecyclerView.Adapter<ViewHolder> {
    private ArrayList<Action> mItems;
    private Context mContext;

    public ActionAdapter(ArrayList<Action> items, Context context) {
      mItems = items;
      mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      final View view = View.inflate(parent.getContext(), R.layout.action_item, null);
      return new ViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
      final Action action = mItems.get(position);
      holder.mTextView.setText(action.getName());
      holder.mDueDate.setText(action.getDisplayDate(action.getDueDate()));

      Goal goal = null;
      if (mGoalMap != null) {
        goal = mGoalMap.get(action.getGoalId());
      }
      if (goal != null) {
        if (goal.getImage() != null) {
          holder.showGoalImage(getActivity(), new File(goal.getImage()), goal.getId());
          holder.hideGoalName();
        } else {
          holder.hideGoalImage();
          holder.showGoalName(getActivity(), goal.getName(), goal.getId());
        }
      } else {
        holder.hideGoalName();
        holder.hideGoalImage();
      }

      holder.mButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          action.changeStatus();
          holder.setState(action.isDone());
        }
      });

      holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
          final Intent intent = new Intent(mContext, EditActivity.class);
          intent.putExtra(EditActivity.ARG_ACTION, action);
          intent.putExtra(EditActivity.TYPE, EditActivity.EDIT_ACTION);
          mContext.startActivity(intent);
          return true;
        }
      });

      holder.setState(action.isDone());;
    }

    public long getItemId(int position) {
      final Action action = mItems.get(position);
      return action.getId();
    }

    @Override
    public int getItemCount() {
      return mItems.size();
    }
  }
}
