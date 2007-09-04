package org.hackystat.telemetry.analyzer.util.selector.interval;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.hackystat.telemetry.analyzer.util.Week;

/**
 * Provides iterator over week interval.
 * 
 * @author Hongbing Kou
 * @version $Id: WeekIterator.java,v 1.1.1.1 2005/10/20 23:56:40 johnson Exp $
 */
public class WeekIterator implements Iterator {
  /** Start week. */
  private Week startWeek;
  /** End week. */
  private Week endWeek;
  /** Current week. */
  private Week currentWeek;
   
  /**
   * Creates an iterator.
   * 
   * @param weekInterval Week interval.
   */
  WeekIterator(WeekInterval weekInterval) {
    this.startWeek = weekInterval.getStartWeek();
    this.endWeek = weekInterval.getEndWeek();
    this.currentWeek = this.startWeek.dec();
  }
  
  /**
   * Required for iterator().  It will throw UnSupportedMethodException.
   */
  public void remove() {
    throw new UnsupportedOperationException("remove() is not supported by week iterator.");
  }

  /**
   * Whether it is still inside the day interval.
   * 
   * @return True if it is still in the interval.
   */
  public boolean hasNext() {
    return this.currentWeek.compareTo(this.endWeek) < 0;
  }

  /**
   * Gets the next day.
   * 
   * @return Next day.
   */
  public Object next() {    
    this.currentWeek = this.currentWeek.inc();
    
    if (this.currentWeek.compareTo(this.endWeek) > 0) {
      throw new NoSuchElementException("Reaches the end of week interval already.");
    }
    
    return this.currentWeek;
  }
}
