package org.chilja.selfmanager;

import android.content.Context;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.chilja.selfmanager.model.Action;
import org.chilja.selfmanager.model.Note;
import org.chilja.selfmanager.model.WaitItem;
import org.chilja.selfmanager.presenter.edit.EditActionFragment;
import org.chilja.selfmanager.presenter.edit.EditActivity;
import org.chilja.selfmanager.presenter.edit.EditFragment;
import org.chilja.selfmanager.presenter.edit.EditGoalFragment;
import org.chilja.selfmanager.presenter.edit.EditWaitItemFragment;
import org.chilja.selfmanager.presenter.edit.EditNoteFragment;

import org.robolectric.fakes.RoboMenuItem;

import java.util.GregorianCalendar;

/**
 * Created by chiljagossow on 8/5/15.
 */
public class EditActivityTest extends ActivityInstrumentationTestCase2<EditActivity> {
  private boolean mIsSaved;
  private boolean mIsDeleted;
  private EditActivity mActivity;
  private EditActionFragment mEditActionFragment;
  private ImageButton mSaveButton;
  public EditActivityTest() {
    super(EditActivity.class);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mActivity = getActivity();
    assertNotNull(mActivity);
  }

  public void testSaveButton() {
    mSaveButton = (ImageButton) mActivity.findViewById(R.id.button_save);
    assertNotNull("save button not found", mSaveButton);

    EditFragment fragment = new EditFragment() {
      @Override
      public void save(Context context) {
        mIsSaved = true;
      }

      @Override
      public void delete(Context context) {
        mIsDeleted = true;
      }

      @Override
      public String getTAG() {
        return null;
      }

      @Override
      public int getScrollY() {
        return 0;
      }

      @Override
      public void scrollTo(int y) {

      }
    };
    mActivity.setEditFragment(fragment);
    mActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        mSaveButton.performClick();
      }
    });
    getInstrumentation().waitForIdleSync();
    assertTrue(mIsSaved);
    MenuItem item = new RoboMenuItem(R.id.action_delete);
    mActivity.onOptionsItemSelected(item);
    assertTrue(mIsDeleted);
  }

  public void testNewGoalIntent() {
      final Intent intent = new Intent(mActivity.getApplicationContext(), EditActivity.class);
      intent.putExtra(EditActivity.TYPE, EditActivity.NEW_GOAL);
      EditFragment fragment = mActivity.getFragmentInstance(intent);
      assertNotNull("edit fragment is null", fragment);
      assertTrue("new goal fragment", fragment instanceof EditGoalFragment);
  }

  public void testEditGoalIntent() {
    final Intent intent = new Intent(mActivity.getApplicationContext(), EditActivity.class);
    intent.putExtra(EditActivity.TYPE, EditActivity.EDIT_GOAL);
    intent.putExtra(EditActivity.ARG_GOAL_ID, 1);
    EditFragment fragment = mActivity.getFragmentInstance(intent);
    assertNotNull("edit fragment is null", fragment);
    assertTrue("edit goal fragment", fragment instanceof EditGoalFragment);
  }

  public void testNewAction() {
    final Intent intent = new Intent(mActivity.getApplicationContext(), EditActivity.class);
    intent.putExtra(EditActivity.TYPE, EditActivity.NEW_ACTION);
    EditFragment fragment = mActivity.getFragmentInstance(intent);
    assertNotNull("edit fragment is null", fragment);
    assertTrue("new action fragment", fragment instanceof EditActionFragment);
    mEditActionFragment = (EditActionFragment) fragment;
    mActivity.setEditFragment(mEditActionFragment);
    assertNotNull("edit action fragment is null", mEditActionFragment);
    assertEquals("wrong title", mActivity.getString(R.string.title_new_action),
            mEditActionFragment.getTitle());
    mActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        mActivity.showFragment(mEditActionFragment);
      }
    });
    getInstrumentation().waitForIdleSync();
    assertNotNull("new action fragment is null", mActivity.getEditFragment());
    Action action = mEditActionFragment.getAction();
    assertNotNull("action is null", action);
  }

  public void testNewGoalActionIntent() {
    final Intent intent = new Intent(mActivity.getApplicationContext(), EditActivity.class);
    intent.putExtra(EditActivity.TYPE, EditActivity.NEW_GOAL_ACTION);
    EditFragment fragment = mActivity.getFragmentInstance(intent);
    assertNotNull("edit fragment is null", fragment);
    assertTrue("new goal action fragment", fragment instanceof EditActionFragment);
  }

  public void testEditActionIntent() {
    final Intent intent = new Intent(mActivity.getApplicationContext(), EditActivity.class);
    intent.putExtra(EditActivity.TYPE, EditActivity.EDIT_ACTION);
    intent.putExtra(EditActivity.ARG_ACTION, new Action("test"));
    EditFragment fragment = mActivity.getFragmentInstance(intent);
    assertNotNull("edit fragment is null", fragment);
    assertTrue("new action fragment", fragment instanceof EditActionFragment);
  }

  public void testNewWaitItemIntent() {
    WaitItem newItem = new WaitItem();
    String name = "test item name";
    String responsible = "test actor";
    newItem.setName(name);
    GregorianCalendar dueDate = new GregorianCalendar(2016, 3, 20);
    GregorianCalendar requestDate = new GregorianCalendar(2015, 1, 6);
    newItem.setDueDate(dueDate);
    newItem.setRequestDate(requestDate);
    newItem.setResponsible(responsible);
    final Intent intent = new Intent(mActivity.getApplicationContext(), EditActivity.class);
    intent.putExtra(EditActivity.TYPE, EditActivity.EDIT_WAIT_ITEM);
    intent.putExtra(EditActivity.ARG_WAIT_ITEM, newItem);
    EditFragment fragment = mActivity.getFragmentInstance(intent);
    assertNotNull("edit fragment is null", fragment);
    assertTrue("new action fragment", fragment instanceof EditWaitItemFragment);
    final EditWaitItemFragment editItemFragment = (EditWaitItemFragment) fragment;
    mActivity.setEditFragment(editItemFragment);
    assertEquals("wrong title", mActivity.getString(R.string.title_edit_wait_item),
            editItemFragment.getTitle());
    mActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        mActivity.showFragment(editItemFragment);
      }
    });
    getInstrumentation().waitForIdleSync();
    fragment = mActivity.getEditFragment();
    assertNotNull("new action fragment is null", fragment);
    assertTrue("wrong edit fragment", fragment instanceof EditWaitItemFragment);
    final EditWaitItemFragment itemFragment = (EditWaitItemFragment) fragment;
    EditText nameView = (EditText) mActivity.findViewById(R.id.name);
    assertNotNull(nameView);
    TextView dueDateView = (TextView) mActivity.findViewById(R.id.due_date);
    assertNotNull(dueDateView);
    EditText mResponsibleView = (EditText) mActivity.findViewById(R.id.responsible);
    assertNotNull(mResponsibleView);
    TextView mRequestDateView = (TextView) mActivity.findViewById(R.id.request_date);
    assertNotNull(mRequestDateView);
    WaitItem item = itemFragment.getWaitItem();
    assertNotNull("wait item is null", item);
    assertEquals("request date wrong", mRequestDateView.getText(), item.getDisplayDate(requestDate));
    assertEquals("due date wrong", dueDateView.getText(), item.getDisplayDate(dueDate));
  }

  public void testNewNoteIntent() {
    final Intent intent = new Intent(mActivity.getApplicationContext(), EditActivity.class);
    intent.putExtra(EditActivity.TYPE, EditActivity.NEW_NOTE);
    EditFragment fragment = mActivity.getFragmentInstance(intent);
    assertNotNull("edit fragment is null", fragment);
    assertTrue("new action fragment", fragment instanceof EditNoteFragment);
  }

  public void testEditNoteIntent() {
    final Intent intent = new Intent(mActivity.getApplicationContext(), EditActivity.class);
    intent.putExtra(EditActivity.TYPE, EditActivity.EDIT_NOTE);
    intent.putExtra(EditActivity.ARG_NOTE, new Note());
    EditFragment fragment = mActivity.getFragmentInstance(intent);
    assertNotNull("edit fragment is null", fragment);
    assertTrue("new action fragment", fragment instanceof EditNoteFragment);
  }



  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }
}
