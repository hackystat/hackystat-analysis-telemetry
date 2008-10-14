package org.hackystat.telemetry.service.prefetch;

import java.util.Date;
import java.util.Timer;
import javax.xml.datatype.XMLGregorianCalendar;
import org.hackystat.utilities.tstamp.Tstamp;


/**
 * Provides a timer task that runs each day at the specified number of minutes past midnight, 
 * and then invokes the run() method of all of the tasks associated with its TimerTaskManager. 
 *
 * @author    Philip M. Johnson
 */
public class DailyTimer {
  /** Milliseconds in a day. */
  private long millisecondsInADay = (1000 * 60 * 60 * 24);
  /** Task manager. */
  private Date triggerTime;

  /**
   * Creates a new DailyTimer, which runs each day at minutesPastMidnight.
   * @param minutesPastMidnight The time when the tasks in the associated TimerTaskManager run.
   * @param prefetchTask The PrefetchTask associated with this timer. 
   */
  public DailyTimer (int minutesPastMidnight, PrefetchTask prefetchTask) {
    Timer timer = new Timer(true);
    this.triggerTime = this.getTomorrowTriggerTime(minutesPastMidnight); 
    timer.schedule(prefetchTask, this.triggerTime, this.millisecondsInADay);
  }

  /**
   * Create a date corresponding to tomorrow's time at which this DailyTimer will be triggered.
   * @param minutesPastMidnight The number of minutes past midnight for this trigger.
   * @return   The required tomorrow's time.
   */
  private Date getTomorrowTriggerTime(int minutesPastMidnight) {
    XMLGregorianCalendar tstamp = Tstamp.makeTimestamp();
    tstamp = Tstamp.incrementDays(tstamp, 1);
    tstamp.setHour(0);
    tstamp.setMinute(0);
    tstamp.setSecond(0);
    long tstampLong = tstamp.toGregorianCalendar().getTimeInMillis();
    tstampLong += minutesPastMidnight * 60L * 1000L;
    return new Date(tstampLong);
  }

  /**
   * Returns the Date indicating the time this task is next scheduled to run.
   * @return The trigger time. 
   */
  public Date getTriggerTime() {
    return new Date(this.triggerTime.getTime());
  }
}

