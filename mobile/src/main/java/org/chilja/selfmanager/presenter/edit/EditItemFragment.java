package org.chilja.selfmanager.presenter.edit;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.chilja.selfmanager.R;
import org.chilja.selfmanager.model.Goal;
import org.chilja.selfmanager.model.Item;
import org.chilja.selfmanager.resolvers.GoalResolver;

import java.util.ArrayList;

/**
 * Created by chiljagossow on 8/10/15.
 */
public abstract class EditItemFragment extends EditFragment implements
        AdapterView.OnItemSelectedListener
        , GoalResolver.GoalCallback {

  protected Spinner mSpinner;
  private Item mItem;

  protected void setUpSpinner(View view, Item item) {
    mSpinner = (Spinner) view.findViewById(R.id.goals_spinner);
    mItem = item;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    new GoalResolver(activity).getGoals(this);
  }

  @Override
  public void onGoalsReturned(ArrayList<Goal> goals) {
    ArrayAdapter<Goal> adapter = new GoalArrayAdapter(getActivity(), goals);
    mSpinner.setAdapter(adapter);
    int id = mItem.getGoalId();
    if (id != 0) {
      for (int i = 0; i < goals.size(); i++) {
        if (goals.get(i).getId() == id) {
          mSpinner.setSelection(i);
          return;
        }
      }
    }
  }

  public void onItemSelected(AdapterView<?> parent, View view,
                             int pos, long id) {
    // An item was selected. You can retrieve the selected item using
    Goal goal = (Goal) parent.getItemAtPosition(pos);
    mItem.setGoalId(goal.getId());
  }

  public void onNothingSelected(AdapterView<?> parent) {
    // Another interface callback
  }

  class GoalArrayAdapter extends ArrayAdapter<Goal> {
    ArrayList<Goal> mGoals;

    public GoalArrayAdapter(Context context, ArrayList<Goal> goals) {
      super(context, R.layout.spinner_item);
      mGoals = goals;
    }

    private class ViewHolder {
      private TextView itemView;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
      ViewHolder viewHolder = null;
      if (convertView == null) {
        convertView = LayoutInflater.from(this.getContext())
                .inflate(R.layout.spinner_item, parent, false);

        viewHolder = new ViewHolder();
        viewHolder.itemView = (TextView) convertView.findViewById(R.id.goal_name);

        convertView.setTag(viewHolder);
      } else {
        viewHolder = (ViewHolder) convertView.getTag();
      }

      Goal item = getItem(position);
      if (item!= null) {
        // My layout has only one TextView
        // do whatever you want with your string and long
        viewHolder.itemView.setText(item.getName());
      }

      return convertView;
    }

    public int getCount () {
      return mGoals.size();
    }

    public Goal getItem (int position) {
      return mGoals.get(position);
    }

  }
}
