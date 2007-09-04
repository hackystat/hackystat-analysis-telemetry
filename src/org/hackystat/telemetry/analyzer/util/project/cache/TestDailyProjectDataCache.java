package org.hackystat.telemetry.analyzer.util.project.cache;

import junit.framework.TestCase;

/**
 * Test the DailyProjectDataCache to ensure that the cache is saving and deleting entries correctly.
 * 
 * Note that the remaining tests of the DailyProjectDataCache mechanism have been moved to the 
 * TestDailyProjectFileMetric class. This is because we need an actually DailyProjectData instance
 * in order to do the remaining tests, and we don't want to introduce cycles in our dependency
 * graph by importing the DailyProjectFileMetric class into this package. 
 * 
 * @author Hongbing Kou, Philip Johnson
 * @version $Id: TestDailyProjectDataCache.java,v 1.1.1.1 2005/10/20 23:56:36 johnson Exp $
 */
public class TestDailyProjectDataCache extends TestCase {

  /** Test that the cache is a singleton and is enabled. */
  public void testProjectCacheInstance() {
    DailyProjectDataCache cache1 = DailyProjectDataCache.getInstance();
    DailyProjectDataCache cache2 = DailyProjectDataCache.getInstance();
    assertSame("Check whether cache is singleton", cache1, cache2);
   }
  
  
  /**
   * Test that the cache is returning previously generated instances, and that changes to a 
   * Project result in the cache being refreshed.
   * 
   * @throws Exception If any error on test.
   */
  

}
