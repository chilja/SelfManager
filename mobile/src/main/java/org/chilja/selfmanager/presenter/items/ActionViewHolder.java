package org.chilja.selfmanager.presenter.items;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.chilja.selfmanager.R;
import org.chilja.selfmanager.model.Action;
import org.chilja.selfmanager.presenter.edit.EditActivity;
import org.chilja.selfmanager.util.BitmapUtility;

import java.io.File;

/**
 * Created by chiljagossow on 8/23/15.
 */
public class ActionViewHolder extends RecyclerView.ViewHolder {

  private View mActionView;
  private TextView mName;
  private TextView mDueDate;
  private TextView mGoalName;
  private ImageView mGoalImage;
  private ImageView mButton;
  private Context mContext;

  private Action mAction;

  private int mIconChecked = R.drawable.ic_checked;
  private int mIconUnchecked = R.drawable.ic_unchecked;

  public ActionViewHolder(View view, Context context) {
    super(view);
    mContext = context;
    mActionView = view.findViewById(R.id.action);
    mName = (TextView) view.findViewById(R.id.action_name);
    mDueDate = (TextView) view.findViewById(R.id.action_due_date);
    mGoalName = (TextView) view.findViewById(R.id.goal_name);
    mButton = (ImageView) view.findViewById(R.id.action_button);
    mGoalImage = (ImageView) view.findViewById(R.id.goal_image);
  }

  void setAction(Action action) {
    mAction = action;
    mName.setText(action.getName());
    mDueDate.setText(action.getDisplayDate(action.getDueDate()));

    mButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mAction.changeStatus();
        setState(mAction.isDone());
      }
    });

    mActionView.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        final Intent intent = new Intent(mContext, EditActivity.class);
        intent.putExtra(EditActivity.ARG_ACTION, mAction);
        intent.putExtra(EditActivity.TYPE, EditActivity.EDIT_ACTION);
        mContext.startActivity(intent);
        return true;
      }
    });
  }

  void setState(boolean done) {
    if (done) {
      mButton.setImageResource(mIconChecked);
      mName.setPaintFlags(mName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    } else {
      mButton.setImageResource(mIconUnchecked);
      mName.setPaintFlags(mName.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
    }
  }

  void showGoalImage(File imageFile, final int id) {
    mGoalImage.setVisibility(View.VISIBLE);
    mGoalImage.setImageBitmap(null);
    mGoalImage.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        final Intent intent = new Intent(mContext, DetailActivity.class);
        intent.putExtra(DetailActivity.ARG_GOAL_ID, id);
        mContext.startActivity(intent);
        return true;
      }
    });
    int width = (int) mContext.getResources().getDimension(R.dimen.goal_image_width);
    BitmapUtility.loadBitmap(mContext, imageFile, mGoalImage, width, width);
  }

  void hideGoalImage() {
    if (mGoalImage != null) {
      mGoalImage.setImageBitmap(null);
      mGoalImage.setVisibility(View.GONE);
    }
  }

  void showGoalName(String name, final int id) {
    mGoalName.setVisibility(View.VISIBLE);
    mGoalName.setText(name);
    mGoalName.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        final Intent intent = new Intent(mContext, DetailActivity.class);
        intent.putExtra(DetailActivity.ARG_GOAL_ID, id);
        mContext.startActivity(intent);
        return true;
      }
    });

  }

  void hideGoalName() {
    if (mGoalName != null) {
      mGoalName.setVisibility(View.GONE);
    }
  }
}
