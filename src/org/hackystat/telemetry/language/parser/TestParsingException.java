package org.hackystat.telemetry.language.parser;

import junit.framework.TestCase;

import org.hackystat.telemetry.language.parser.impl.TokenMgrError;

/**
 * Test suite for <code>ParsingException</code>.
 * 
 * @author (Cedric) Qin Zhang
 * @version $Id: TestParsingException.java,v 1.1.1.1 2005/10/20 23:56:49 johnson
 *          Exp $
 */
public class TestParsingException extends TestCase {

  /**
   * Test case.
   */
  public void testAll() {
    String defs = "streams MyStreamsA = {\"desc\", MyReducerA )};";
    // 1234567890A234567890<== encountered =, expecting (
    try {
      TelemetryLanguageParser.parse(defs);
      fail("An expected exception not raised.");
    }
    catch (ParsingException ex) {
      assertEquals(1, ex.getErrorLineNumber());
      assertEquals(20, ex.getErrorColumnNumber());
    }
  }

  /**
   * Tests whether we can extract line and column numbers from
   * <code>TokenMgrError</code>.
   */
  public void testWithTokenMgrError() {
    // test 1
    String msg = "  Lexical error at line 2, column 53. Encountered:...(12) ";
    TokenMgrError tokenError = new TokenMgrError(msg, 0); // 0 is Lexical Error
    ParsingException parsingException = new ParsingException(tokenError);
    assertEquals(2, parsingException.getErrorLineNumber());
    assertEquals(53, parsingException.getErrorColumnNumber());

    // test 2
    msg = "  Lexical error. Encountered:...(12) ";
    tokenError = new TokenMgrError(msg, 0); // 0 is Lexical Error
    parsingException = new ParsingException(tokenError);
    assertEquals(-1, parsingException.getErrorLineNumber());
    assertEquals(-1, parsingException.getErrorColumnNumber());
  }
}
