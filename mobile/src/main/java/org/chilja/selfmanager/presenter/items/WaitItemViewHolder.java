package org.chilja.selfmanager.presenter.items;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.chilja.selfmanager.R;
import org.chilja.selfmanager.model.WaitItem;
import org.chilja.selfmanager.presenter.edit.EditActivity;
import org.chilja.selfmanager.util.BitmapUtility;

import java.io.File;

/**
 * Created by chiljagossow on 8/23/15.
 */
public class WaitItemViewHolder extends RecyclerView.ViewHolder {

  private Context mContext;

  private WaitItem mWaitItem;

  private View mView;
  private TextView mName;
  private TextView mDueDate;
  private TextView mGoalName;
  private ImageView mGoalImage;
  private ImageView mButton;

  private TextView mResponsible;
  private TextView mRequestDate;

  private int mIconChecked = R.drawable.ic_checked;
  private int mIconUnchecked = R.drawable.ic_unchecked;

  public WaitItemViewHolder(View view, Context context) {
    super(view);
    mContext = context;
    mView = view.findViewById(R.id.wait);
    mName = (TextView) view.findViewById(R.id.wait_name);
    mDueDate = (TextView) view.findViewById(R.id.wait_due_date);
    mGoalName = (TextView) view.findViewById(R.id.goal_name);
    mButton = (ImageView) view.findViewById(R.id.wait_button);
    mGoalImage = (ImageView) view.findViewById(R.id.goal_image);
    mResponsible = (TextView) view.findViewById(R.id.responsible);
    mRequestDate = (TextView) view.findViewById(R.id.request_date);
  }

  void setWaitItem(WaitItem item) {
    mWaitItem = item;
    mName.setText(item.getName());
    mDueDate.setText(item.getDisplayDate(item.getDueDate()));
    mResponsible.setText(item.getResponsible());
    mRequestDate.setText(item.getDisplayDate(item.getRequestDate()));
    mButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mWaitItem.changeStatus();
        setState(mWaitItem.isDone());
      }
    });

    mView.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        final Intent intent = new Intent(mContext, EditActivity.class);
        intent.putExtra(EditActivity.ARG_WAIT_ITEM, mWaitItem);
        intent.putExtra(EditActivity.TYPE, EditActivity.EDIT_WAIT_ITEM);
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
    if (mGoalImage != null)
      mGoalImage.setImageBitmap(null);
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
    if (mGoalName != null)
      mGoalName.setVisibility(View.GONE);
  }
}
