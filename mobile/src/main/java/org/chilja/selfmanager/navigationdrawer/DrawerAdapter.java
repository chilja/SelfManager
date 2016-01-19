package org.chilja.selfmanager.navigationdrawer;

/**
 * Created by chiljagossow on 6/3/15.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.chilja.selfmanager.R;

/**
 * Created by hp1 on 28-12-2014.
 */
public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {

  private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
  // IF the view under inflation and population is header or Item
  private static final int TYPE_ITEM = 1;

  private MenuItem[] mMenuItems;

  private Context context;

  private OnClickListener mListener;

  private int mDrawableResId;

  DrawerAdapter(MenuItem[] items, OnClickListener listener, int drawableResId) {
    mMenuItems = items;
    mListener = listener;
    mDrawableResId = drawableResId;
  }


  // Creating a WaitItemViewHolder which extends the RecyclerView View Holder
  // WaitItemViewHolder are used to to store the inflated views in order to recycle them

  @Override
  public DrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    this.context = parent.getContext();

    View view = null;

    if (viewType == TYPE_ITEM) {
      view = LayoutInflater.from(parent.getContext())
              .inflate(R.layout.drawer_item_row, parent, false); //Inflating the layout
    } else if (viewType == TYPE_HEADER) {
      view = LayoutInflater.from(parent.getContext())
              .inflate(R.layout.drawer_header, parent, false); //Inflating the layout
    }

    ViewHolder vhItem = new ViewHolder(view, viewType, context,
            mListener);
    return vhItem;

  }

  //Next we override a method which is called when the item in a row is needed to be displayed, here the int mPosition
  // Tells us item at which mPosition is being constructed to be displayed and the holder id of the holder object tell us
  // which view type is being created 1 for item row
  @Override
  public void onBindViewHolder(DrawerAdapter.ViewHolder holder, int position) {
    holder.position = position;
    if (holder.holderId == 1) {                              // as the list view is going to be called after the header view so we decrement the
      // mPosition by 1 and pass it to the holder while setting the text and image
      holder.textView.setText(
              mMenuItems[position - 1].title); // Setting the Text with the array of our Titles
      holder.imageView.setImageResource(
              mMenuItems[position - 1].icon);// Settimg the image with array of our icons
    } else {

      BitmapUtility.loadBitmap(context, mDrawableResId, holder.headerImageView, 320, 200);

    }
  }

  //Below first we ovverride the method onCreateViewHolder which is called when the ActionViewHolder is
  //Created, In this method we inflate the item_row.xml layout if the viewType is Type_ITEM or else we inflate header.xml
  // if the viewType is TYPE_HEADER
  // and pass it to the view holder

  // With the following method we check what type of view is being passed
  @Override
  public int getItemViewType(int position) {
    if (isPositionHeader(position)) {
      return TYPE_HEADER;
    }

    return TYPE_ITEM;
  }

  // This method returns the number of items present in the list
  @Override
  public int getItemCount() {
    return mMenuItems.length + 1; // the number of items in the list will be +1 the titles including the header view.
  }

  private boolean isPositionHeader(int position) {
    return position == 0;
  }


  interface OnClickListener {
    public void onClick(int position);
  }

  public static class MenuItem {
    String title;
    int icon;

    MenuItem(String title, int icon) {
      this.title = title;
      this.icon = icon;
    }
  }

  public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    int holderId;

    TextView textView;
    ImageView imageView;
    ImageView headerImageView;
    int position;
    Context context;
    OnClickListener listener;


    public ViewHolder(View itemView, int ViewType, Context context, final OnClickListener listener) {
      super(itemView);
      this.context = context;
      this.listener = listener;
      itemView.setClickable(true);
      itemView.setOnClickListener(this);

      if (ViewType == TYPE_ITEM) {
        textView = (TextView) itemView.findViewById(
                R.id.rowText);
        imageView = (ImageView) itemView.findViewById(
                R.id.rowIcon);// Creating ImageView object with the id of ImageView from item_row.xml
        holderId = 1;                                               // setting holder id as 1 as the object being populated are of type item row
      } else {

        headerImageView = (ImageView) itemView.findViewById(R.id.image);
        holderId = 0;                                                // Setting holder id = 0 as the object being populated are of type header view
      }
    }

    @Override
    public void onClick(View v) {
      listener.onClick(position);
    }

  }

}
