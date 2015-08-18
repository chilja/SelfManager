package org.chilja.selfmanager;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;


/**
 * Created by chiljagossow on 8/8/15.
 */
public class MyTestRunner extends RobolectricTestRunner {
  /**
   * Creates a runner to run {@code testClass}. Looks in your working directory for your AndroidManifest.xml file
   * and res directory by default. Use the {@link} annotation to configure.
   *
   * @param testClass the test class to be run
   * @throws InitializationError if junit says so
   */
  public MyTestRunner(Class<?> testClass) throws InitializationError {
    super(testClass);
  }

}

