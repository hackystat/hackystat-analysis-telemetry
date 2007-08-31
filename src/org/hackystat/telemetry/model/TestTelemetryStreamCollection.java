package org.hackystat.telemetry.model;

import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

/**
 * Test suite for <code>TelemetryStreamCollection</code>.
 * 
 * @author (Cedric) Qin Zhang
 * @version $Id$
 */
public class TestTelemetryStreamCollection extends TestCase {

  /**
   * Test case.
   * 
   * @throws Exception If test fails.
   */
  public void testAll() throws Exception {
    TelemetryStream stream0 = new TelemetryStream(null);
    TelemetryStream stream0b = new TelemetryStream(null);
    TelemetryStream stream1 = new TelemetryStream("stream1");
    TelemetryStream stream1b = new TelemetryStream("stream1");
    TelemetryStream stream2 = new TelemetryStream("stream2");

    TelemetryStreamCollection streams = new TelemetryStreamCollection("Test", null, null);
    assertEquals("Test", streams.getName());
    assertNull(streams.getProject());
    assertNull(streams.getInterval());
    assertEquals(0, streams.getTelemetryStreams().size());

    streams.add(stream0);
    streams.add(stream1);
    assertEquals(2, streams.getTelemetryStreams().size());
    try {
      streams.add(stream0b);
      fail("Should not allow add stream with same tag.");
    }
    catch (TelemetryDataModelException ex) {
      // expected.
    }
    assertEquals(2, streams.getTelemetryStreams().size());
    try {
      streams.add(stream1b);
      fail("Should not allow add stream with same tag.");
    }
    catch (TelemetryDataModelException ex) {
      // expected.
    }
    assertEquals(2, streams.getTelemetryStreams().size());
    streams.add(stream2);

    assertEquals(3, streams.getTelemetryStreams().size());
    assertSame(stream0, streams.get(null));
    assertSame(stream1, streams.get("stream1"));
    assertSame(stream2, streams.get("stream2"));
  }
  
  /**
   * Asserts equality for two telemetry stream collection objects, except their names.
   * 
   * @param streamCollection1 Telemetry stream collection 1.
   * @param streamCollection2 Telemetry stream collection 2.
   * 
   * @throws Exception If the two stream collections are not the same.
   */
  public static void assertEqualsIgnoreName(TelemetryStreamCollection streamCollection1, 
                           TelemetryStreamCollection streamCollection2) throws Exception {
    //assertEquals(streamCollection1.getName(), streamCollection2.getName());
    assertEquals(streamCollection1.getProject(), streamCollection2.getProject());
    assertEquals(streamCollection1.getInterval(), streamCollection2.getInterval());
    
    Collection streams1 = streamCollection1.getTelemetryStreams();
    assertEquals(streams1.size(), streamCollection2.getTelemetryStreams().size());
    
    for (Iterator i = streams1.iterator(); i.hasNext(); ) {
      TelemetryStream streamFrom1 = (TelemetryStream) i.next();
      Object tag = streamFrom1.getTag();
      TelemetryStream streamFrom2 = streamCollection2.get(tag);
      if (streamFrom2 == null) {
        throw new Exception("Unable to find TelemetryStream '" + streamFrom1.getTag() 
            + "' from TelemetryStreamCollection '" + streamCollection2.getName() + "'.");
      }
      TestTelemetryStream.assertEquals(streamFrom1, streamFrom2);
    }
  }
}
