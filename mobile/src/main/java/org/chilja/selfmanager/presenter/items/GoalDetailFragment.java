package org.chilja.selfmanager.presenter.items;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.chilja.selfmanager.R;
import org.chilja.selfmanager.model.Action;
import org.chilja.selfmanager.presenter.base.SlidingHeaderHelper;
import org.chilja.selfmanager.resolvers.ActionResolver;
import org.chilja.selfmanager.resolvers.CalendarEventResolver;
import org.chilja.selfmanager.model.Event;
import org.chilja.selfmanager.model.Goal;
import org.chilja.selfmanager.model.Note;
import org.chilja.selfmanager.model.WaitItem;
import org.chilja.selfmanager.resolvers.EventResolver;
import org.chilja.selfmanager.resolvers.GoalResolver;
import org.chilja.selfmanager.resolvers.NoteResolver;
import org.chilja.selfmanager.db.DatabaseInteractionListener;
import org.chilja.selfmanager.resolvers.WaitItemResolver;
import org.chilja.selfmanager.presenter.base.BaseFragment;
import org.chilja.selfmanager.util.BitmapUtility;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GoalDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GoalDetailFragment extends BaseFragment implements
        GoalResolver.GoalCallback
        , ActionResolver.ActionCallback
        , WaitItemResolver.WaitItemCallback
        , EventResolver.EventCallback
        , NoteResolver.NoteCallback
        , DatabaseInteractionListener {

  //TODO show all items
  // TODO: 8/17/15  set items done
  private static final String ARG_GOAL_ID = "arg_goal_id";

  private OnFragmentInteractionListener mListener;

  private int mGoalId;
  private Goal mGoal;
  private RelativeLayout mGoalItemView;
  private ImageView mImageView;
  private int mImageHeight;

  private TextView mGoalName;
  private TextView mMotivation;
  private TextView mDefinitionDone;
  private TextView mDueDate;


  //action
  private View mNextActionData;
  private TextView mDueDateAction;
  private TextView mActionName;
  private ArrayList<Action> mActions;

  //wait item
  private View mWaitItemData;
  private ArrayList<WaitItem> mWaitItems;
  private TextView mDueDateWaitItem;
  private TextView mWaitItemName;
  private TextView mResponsible;
  private TextView mRequestDate;

  // event
  private ArrayList<Event> mEvents;
  private View mEventData;
  private ImageButton mEventButton;
  private TextView mEventTitle;
  private TextView mEventStartDate;

  // notes
  private View mNoteData;
  private ListView mNotesList;
  private ArrayList<Note> mNotes;


  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @param goalId Parameter 1.
   * @return A new instance of fragment GoalDetailFragment.
   */
  public static GoalDetailFragment newInstance(int goalId) {
    GoalDetailFragment fragment = new GoalDetailFragment();
    Bundle args = new Bundle();
    args.putInt(ARG_GOAL_ID, goalId);
    fragment.setArguments(args);
    return fragment;
  }

  public GoalDetailFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {

    if (getArguments() != null) {
      mGoalId = getArguments().getInt(ARG_GOAL_ID);
    } else {
      SharedPreferences preferences = getActivity().getSharedPreferences(
              getString(R.string.preference_main), Context.MODE_PRIVATE);
      mGoalId = preferences.getInt(ARG_GOAL_ID, mGoalId);
    }

    GoalResolver gdm = new GoalResolver(getActivity());
    gdm.getGoal(this, mGoalId);

    super.onCreate(savedInstanceState);

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_goal_detail, container, false);
    mGoalItemView = (RelativeLayout)view.findViewById(R.id.goal_item);
    mImageView = (ImageView) view.findViewById(R.id.image);
    mGoalName =  (TextView) view.findViewById(R.id.goal_name);
    mDueDate =  (TextView) view.findViewById(R.id.due_date);
    mMotivation =  (TextView) view.findViewById(R.id.motivation);
    mDefinitionDone =  (TextView) view.findViewById(R.id.definiton_done);

    FloatingActionButton actionButton = (FloatingActionButton) view.findViewById(R.id.new_action_button);
    actionButton.setSize(FloatingActionButton.SIZE_MINI);
    FloatingActionButton eventButton = (FloatingActionButton) view.findViewById(R.id.new_event_button);
    eventButton.setSize(FloatingActionButton.SIZE_MINI);
    FloatingActionButton noteButton = (FloatingActionButton) view.findViewById(R.id.new_note_button);
    noteButton.setSize(FloatingActionButton.SIZE_MINI);
    FloatingActionButton waitItemButton = (FloatingActionButton) view.findViewById(R.id.new_wait_button);
    waitItemButton.setSize(FloatingActionButton.SIZE_MINI);

    mNextActionData = view.findViewById(R.id.next_action_data);
    mActionName =  (TextView) view.findViewById(R.id.next_action);
    mDueDateAction =  (TextView) view.findViewById(R.id.next_action_due_date);
    mRequestDate =  (TextView) view.findViewById(R.id.request_date);
    mResponsible = (TextView) view.findViewById(R.id.responsible);

    mWaitItemData = view.findViewById(R.id.wait_item_data);
    mWaitItemName  =  (TextView) view.findViewById(R.id.wait_item);
    mDueDateWaitItem =  (TextView) view.findViewById(R.id.wait_item_due_date);

    mEventData = view.findViewById(R.id.event_data);
    mEventButton = (ImageButton) view.findViewById(R.id.event_button);
    mEventTitle  =  (TextView) view.findViewById(R.id.event_title);
    mEventStartDate =  (TextView) view.findViewById(R.id.event_start_date);

    mNoteData = view.findViewById(R.id.note_data);
    mNotesList = (ListView) view.findViewById(R.id.notes_list);

    View placeholder = view.findViewById(R.id.placeholder);

    float ratio = .75F;
    mImageHeight = (int) (mDisplayWidth * ratio);
    mGoalItemView.setLayoutParams(new FrameLayout.LayoutParams(mDisplayWidth, mImageHeight));
    placeholder.setLayoutParams(new LinearLayout.LayoutParams(mDisplayWidth, mImageHeight));

    ImageView headerImage = (ImageView) view.findViewById(R.id.image);
    ScrollView scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
    new SlidingHeaderHelper(-mImageHeight, 0).setUp(null, headerImage, scrollView);

    return view;
  }

  /**** Method for Setting the Height of the ListView dynamically.
   **** Hack to fix the issue of not showing all the items of the ListView
   **** when placed inside a ScrollView  ****/
  public static void setListViewHeightBasedOnChildren(ListView listView) {
    ListAdapter listAdapter = listView.getAdapter();
    if (listAdapter == null)
      return;

    int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
            View.MeasureSpec.UNSPECIFIED);
    int totalHeight = 0;
    View view = null;
    for (int i = 0; i < listAdapter.getCount(); i++) {
      view = listAdapter.getView(i, view, listView);
      view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
      view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
      totalHeight += view.getMeasuredHeight();
    }
    ViewGroup.LayoutParams params = listView.getLayoutParams();
    params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
    listView.setLayoutParams(params);
    listView.requestLayout();
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
  }

  @Override
  public void onResume() {
    super.onResume();
    setGoalData(mGoal);
    setActionData(mActions);
    setWaitItemData(mWaitItems);
    setEventData(mEvents);
    setNotesData(mNotes);
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt(ARG_GOAL_ID, mGoalId);
  }

  @Override
  public void onStop() {
    super.onStop();
    SharedPreferences preferences = getActivity().getSharedPreferences(
            getString(R.string.preference_main), Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = preferences.edit();
    editor.putInt(ARG_GOAL_ID, mGoalId);
    editor.commit();
  }

  @Override
  public String getTAG() {
    return null;
  }

  @Override
  public String getTitle() {
    return "";
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
    if (goals != null && goals.get(0) != null) {
      mGoal = goals.get(0);
      setGoalData(mGoal);
      new ActionResolver(getActivity()).getActions(this, mGoal.getId());
      new WaitItemResolver(getActivity()).getWaitItems(this, mGoal.getId());
      new EventResolver(getActivity()).getEvents(this, mGoal.getId());
      new NoteResolver(getActivity()).getNotes(this, mGoal.getId());
    }
  }

  private void setGoalData(Goal goal) {
    if (mIsAttached && mIsResumed && goal != null) {

      if (goal.getImage() != null) {
        BitmapUtility.loadBitmap(getActivity(), new File(goal.getImage()), mImageView,
                mDisplayWidth, mImageHeight);
      }

      mGoalName.setText(goal.getName());
      mMotivation.setText(goal.getMotivation());
      mDefinitionDone.setText(goal.getDefinitionDone());
      mDueDate.setText(goal.getDisplayDate(goal.getDueDate()));
    }
  }

  @Override
  public void onActionsReturned(ArrayList<Action> actions) {
    if (actions != null && actions.size() > 0) {
      Collections.sort(actions);
    }
    mActions = actions;
    setActionData(actions);
  }

  private void setActionData(ArrayList<Action> actions) {

    if (mIsAttached && mIsResumed ) {
      Action action = null;
      if (actions != null && actions.size() > 0) {
        action = actions.get(0);
      }
      if (action != null) {
        mActionName.setText(action.getName());
        mDueDateAction.setText(action.getDisplayDate(action.getDueDate()));
        mNextActionData.setVisibility(View.VISIBLE);
      } else {
        mNextActionData.setVisibility(View.GONE);
      }
    }
  }

  @Override
  public void onWaitItemsReturned(ArrayList<WaitItem> items) {
    if (items != null && items.size() > 0) {
      Collections.sort(items);
    }
    mWaitItems = items;
    setWaitItemData(items);
  }

  private void setWaitItemData(ArrayList<WaitItem> items) {

    if (mIsAttached && mIsResumed ) {
      WaitItem item = null;
      if (items != null && items.size() > 0) {
        item = items.get(0);
      }
      if (item != null) {
        mWaitItemName.setText(item.getName());
        mDueDateWaitItem.setText(item.getDisplayDate(item.getDueDate()));
        mResponsible.setText(item.getResponsible());
        mRequestDate.setText(item.getDisplayDate(item.getRequestDate()));
        mWaitItemData.setVisibility(View.VISIBLE);
      } else {
        mWaitItemData.setVisibility(View.GONE);
      }
    }
  }

  @Override
  public void onEventsReturned(ArrayList<Event> events) {
    if (events != null && events.size() > 0) {
      Collections.sort(events);
    }
    mEvents = events;
    setEventData(events);
  }

  private void setEventData(ArrayList<Event> events) {
    if (mIsAttached && mIsResumed) {
      Event event = null;
      if (events != null && events.size() > 0) {
        event = events.get(0);
      }
      if (event != null) {
        CalendarEventResolver cer = new CalendarEventResolver(getActivity());
        cer.readCalendarEvent(event);
        Calendar endDate = event.getEndDate();
        if (endDate != null && new GregorianCalendar().getTime().before(endDate.getTime())) {
          final long eventID = event.getEventId();
          mEventData.setVisibility(View.VISIBLE);
          mEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
              Intent intent = new Intent(Intent.ACTION_VIEW).setData(uri);
              startActivity(intent);
            }
          });
          mEventStartDate.setText(event.getDisplayDate(event.getDueDate()));
          mEventTitle.setText(event.getName());
          mEventData.setVisibility(View.VISIBLE);
        } else {
          event.delete(getActivity());
          mEventData.setVisibility(View.GONE);
        }
      } else {
        mEventData.setVisibility(View.GONE);
      }
    }
  }

  @Override
  public void onNotesReturned(ArrayList<Note> items) {
    mNotes = items;
    setNotesData(items);
  }

  void setNotesData(ArrayList<Note> items) {
    if (mIsAttached && mIsResumed ) {
      if (items != null && items.size() > 0) {
        NotesArrayAdapter adapter = new NotesArrayAdapter(getActivity(), items);
        mNotesList.setAdapter(adapter);
        mNoteData.setVisibility(View.VISIBLE);
        setListViewHeightBasedOnChildren(mNotesList);
      } else {
        mNoteData.setVisibility(View.GONE);
      }
    }
  }

  @Override
  public boolean save(Context context) {
    return false;
  }

  @Override
  public boolean delete(Context context) {
    mGoal.delete(context);
    if (mNotes != null) {
      for (Note note : mNotes) {
        note.delete(context);
      }
    }
    if (mActions != null) {
      for (Action action : mActions) {
        action.delete(context);
      }
    }
    if (mWaitItems != null) {
      for (WaitItem item : mWaitItems) {
        item.delete(context);
      }
    }
    if (mEvents != null) {
      for (Event event : mEvents) {
        event.delete(context);
      }
    }
    Resources res = getResources();
    String text = String.format(res.getString(R.string.goal_deleted), mGoal.getName());
    Toast.makeText(context, text, Toast.LENGTH_SHORT);
    return true;
  }

  class NotesArrayAdapter extends BaseAdapter {
    ArrayList<Note> mNotes;
    private Context mContext;

    public NotesArrayAdapter(Context context, ArrayList<Note> notes) {
      mNotes = notes;
      mContext = context;
    }

    private class ViewHolder {
      private TextView nameView;
      private TextView textView;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
      ViewHolder viewHolder = null;
      if (convertView == null) {
        convertView = View.inflate(mContext, R.layout.note, null);

        viewHolder = new ViewHolder();
        viewHolder.nameView = (TextView) convertView.findViewById(R.id.name);
        viewHolder.textView = (TextView) convertView.findViewById(R.id.text);

        convertView.setTag(viewHolder);
      } else {
        viewHolder = (ViewHolder) convertView.getTag();
      }

      final Note note = getItem(position);
      if (note!= null) {
        if (note.getName() != null && !note.getName().isEmpty()) {
          viewHolder.nameView.setText(note.getName());
        } else {
          viewHolder.nameView.setVisibility(View.GONE);
        }
        viewHolder.textView.setText(note.getText());
      }

      convertView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          mListener.onEditNote(note);
        }
      });

      return convertView;
    }

    public int getCount () {
      Log.i(getTAG(), "size " + mNotes.size());
      return mNotes.size();
    }

    public long getItemId(int position) {
      return mNotes.get(position).getId();
    }

    public Note getItem (int position) {
      return mNotes.get(position);
    }

  }

  interface OnFragmentInteractionListener{
    void onEditNote(Note note);
  }
}
