package org.chilja.selfmanager.presenter.items;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.chilja.selfmanager.R;
import org.chilja.selfmanager.model.Action;
import org.chilja.selfmanager.model.Event;
import org.chilja.selfmanager.model.Goal;
import org.chilja.selfmanager.model.Item;
import org.chilja.selfmanager.model.WaitItem;
import org.chilja.selfmanager.resolvers.ActionResolver;
import org.chilja.selfmanager.resolvers.EventResolver;
import org.chilja.selfmanager.resolvers.GoalResolver;
import org.chilja.selfmanager.resolvers.WaitItemResolver;
import org.chilja.selfmanager.presenter.base.BaseFragment;
import org.chilja.selfmanager.presenter.edit.EditActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.sort;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GoalFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GoalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GoalFragment extends BaseFragment
        implements GoalResolver.GoalCallback, ActionResolver.ActionCallback,
        WaitItemResolver.WaitItemCallback,
        EventResolver.EventCallback{

  public static final String TAG = "GoalFragment";

  private static final String WIDTH = "width";
  private static final int NUM_COLUMNS = 2;
  // Identifies a particular Loader being used in this component
  private static final int URL_LOADER = 0;
  private GridView mGridView;
  private TextView mInspiration;
  private ArrayList<Goal> mGoals;
  private OnFragmentInteractionListener mListener;

  private boolean mActionsReturned;
  private boolean mWaitItemsReturned;
  private boolean mEventsReturned;

  public GoalFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @return A new instance of fragment ActionFragment.
   */
  public static GoalFragment newInstance(int width) {
    GoalFragment fragment = new GoalFragment();
    Bundle args = new Bundle();
    args.putInt(WIDTH, width);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_goal, container, false);
    mGridView = (GridView) view.findViewById(R.id.gridview);
    mGridView.setNumColumns(NUM_COLUMNS);
    mGridView.setAdapter(new ImageAdapter(getActivity(), mGoals, mDisplayWidth));
    mInspiration = (TextView) view.findViewById(R.id.inspiration);
    mInspiration.setText(R.string.inspiration_goals);
    return view;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      mListener = (OnFragmentInteractionListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(
              activity.toString() + " must implement OnFragmentInteractionListener");
    }

    GoalResolver resolver = new GoalResolver(activity);
    resolver.getGoals(this);
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
    return "Goals";
  }

  @Override
  public int getScrollY() {
    return 0;
  }

  @Override
  public void scrollTo(int y) {

  }

  @Override
  public void onGoalsReturned(ArrayList<Goal> goals) {
    if (goals != null) {
      mGoals = goals;
      sort(mGoals);
      new ActionResolver(getActivity()).getActions(this);
      new WaitItemResolver(getActivity()).getWaitItems(this);
      new EventResolver(getActivity()).getEvents(this);
    }
  }

  @Override
  public void onActionsReturned(ArrayList<Action> actions) {
    mActionsReturned = true;
    Map<Integer, Item> itemMap = new HashMap<Integer, Item>();
    for (Action action: actions) {
      if (action.getGoalId() != 0) {
        itemMap.put(action.getGoalId(), action);
      }
    }
    updateGoals(itemMap);
    updateView();
  }

  @Override
  public void onWaitItemsReturned(ArrayList<WaitItem> items) {
    mWaitItemsReturned = true;
    Map<Integer, Item> itemMap = new HashMap<Integer, Item>();
    for (WaitItem item: items) {
      if (item.getGoalId() != 0) {
        itemMap.put(item.getGoalId(), item);
      }
    }
    updateGoals(itemMap);
    updateView();
  }

  @Override
  public void onEventsReturned(ArrayList<Event> events) {
    mEventsReturned = true;
    Map<Integer, Item> itemMap = new HashMap<Integer, Item>();
    for (Event item: events) {
      itemMap.put(item.getGoalId(), item);
    }
    updateGoals(itemMap);
    updateView();
  }

  private void updateGoals(Map<Integer, Item> itemMap) {
    for (Goal goal: mGoals) {
      if (itemMap.containsKey(goal.getId())) {
        goal.setHasNextItem(true);
      }
    }
  }

  private void updateView() {
    if (mGridView != null && checkViewUpdate()) {
      mGridView.setAdapter(new ImageAdapter(getActivity(), mGoals, mDisplayWidth));
    }
  }

  private boolean checkViewUpdate() {
    return(mWaitItemsReturned && mEventsReturned && mActionsReturned);
  }

  public interface OnFragmentInteractionListener {
    void onGoalSelected(int id);
  }

  public class ImageAdapter extends BaseAdapter {
    private static final int sLayout = R.layout.goal_item_2;
    private Context mContext;
    private int mWidth;
    private ArrayList<Goal> mItems;

    public ImageAdapter(Context context, ArrayList<Goal> items, int width) {
      mWidth = width / NUM_COLUMNS;
      mItems = items;
      mContext = context;
    }

    public int getCount() {
      if (mItems == null) return 0;
      return mItems.size();
    }

    public Object getItem(int position) {
      return null;
    }

    public long getItemId(int position) {
      return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
      View view = getView(mItems.get(position), mContext, mWidth);

      return view;
    }

    public View getView(Goal goal, Context context, int width) {
      final View view = View.inflate(context, sLayout, null);
      ImageView imageView = (ImageView) view.findViewById(R.id.image);
      View coloredView = view.findViewById(R.id.colored_view);
      goal.loadBitmap(context, imageView, coloredView, width, width*2/3);
      TextView goalName =  (TextView) view.findViewById(R.id.goal_name);
      goalName.setText(goal.getName());
      TextView dueDate =  (TextView) view.findViewById(R.id.due_date);
      dueDate.setText(goal.getDisplayDate(goal.getDueDate()));
      view.setLayoutParams(new GridView.LayoutParams(width, width));

      ImageView nextItemIcon = (ImageView) view.findViewById(R.id.next_item_icon);
      if (goal.hasNextItem()) {
        nextItemIcon.setVisibility(View.GONE);
      } else {
        nextItemIcon.setVisibility(View.VISIBLE);
      }

      final int id = goal.getId();
      view.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          mListener.onGoalSelected(id);
        }
      });
      view.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
          final Intent intent = new Intent(getActivity(), EditActivity.class);
          intent.putExtra(EditActivity.TYPE, EditActivity.EDIT_GOAL);
          intent.putExtra(EditActivity.ARG_GOAL_ID, id);
          startActivity(intent);
          return true;
        }
      });
      return view;
    }
  }
}
