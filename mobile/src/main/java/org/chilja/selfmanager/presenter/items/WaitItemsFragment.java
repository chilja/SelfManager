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
import org.chilja.selfmanager.model.Goal;
import org.chilja.selfmanager.model.WaitItem;
import org.chilja.selfmanager.resolvers.GoalResolver;
import org.chilja.selfmanager.resolvers.WaitItemResolver;
import org.chilja.selfmanager.presenter.base.BaseFragment;
import org.chilja.selfmanager.presenter.edit.EditActivity;
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
    setUpRecyclerView(mWaitItems, mGoalMap, getActivity() );
  }

  private void setUpRecyclerView(ArrayList<WaitItem> items, Map goals, Context context) {
    if (mRecyclerView != null && items != null && goals != null) {
      mRecyclerView.setAdapter(new RecyclerViewAdapter(items, context));
    }
  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    private View mView;
    private TextView mTextView;
    private TextView mDueDate;
    private TextView mGoalName;
    private ImageView mGoalImage;
    private ImageView mButton;

    private TextView mResponsible;
    private TextView mRequestDate;

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
      mResponsible = (TextView) view.findViewById(R.id.responsible);
      mRequestDate = (TextView) view.findViewById(R.id.request_date);
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
      mGoalImage.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
          final Intent intent = new Intent(context, DetailActivity.class);
          intent.putExtra(DetailActivity.ARG_GOAL_ID, id);
          context.startActivity(intent);
          return true;
        }
      });
      BitmapUtility.loadBitmap(context, imageFile, mGoalImage, 100, 100);
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

  class RecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder> {
    private ArrayList<WaitItem> mItems;
    private Context mContext;

    public RecyclerViewAdapter(ArrayList<WaitItem> items, Context context) {
      mItems = items;
      mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      final View view = View.inflate(parent.getContext(), R.layout.wait_item, null);
      return new ViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
      final WaitItem item = mItems.get(position);
      holder.mTextView.setText(item.getName());
      holder.mDueDate.setText(item.getDisplayDate(item.getDueDate()));
      holder.mResponsible.setText(item.getResponsible());
      holder.mRequestDate.setText(item.getDisplayDate(item.getRequestDate()));

      if (item.getGoalId() != 0) {
        final Goal goal = mGoalMap.get(item.getGoalId());
        if (goal != null) {
          if (goal.getImage() != null) {
            holder.showGoalImage(getActivity(), new File(goal.getImage()), goal.getId());
            holder.hideGoalName();
          } else {
            holder.hideGoalImage();
            holder.showGoalName(getActivity(), goal.getName(), goal.getId());
          }
        }
      } else {
        holder.hideGoalName();
        holder.hideGoalImage();
      }

      holder.mButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          item.changeStatus();
          holder.setState(item.isDone());
        }
      });

      holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
          final Intent intent = new Intent(mContext, EditActivity.class);
          intent.putExtra(EditActivity.ARG_WAIT_ITEM, item);
          intent.putExtra(EditActivity.TYPE, EditActivity.EDIT_WAIT_ITEM);
          mContext.startActivity(intent);
          return true;
        }
      });

      holder.setState(item.isDone());;
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

}
