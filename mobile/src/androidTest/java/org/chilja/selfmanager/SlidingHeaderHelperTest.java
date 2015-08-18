package org.chilja.selfmanager;

import junit.framework.TestCase;

import org.chilja.selfmanager.presenter.base.SlidingHeaderHelper;

/**
 * Created by chiljagossow on 8/17/15.
 */
public class SlidingHeaderHelperTest extends TestCase {
  public void testCalculateTranslationY() {
    int result = SlidingHeaderHelper.calculateTranslation(0, 240, 200);
    assertEquals(40, result);
  }
}
