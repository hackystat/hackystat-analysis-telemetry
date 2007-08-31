package org.hackystat.telemetry.language.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hackystat.telemetry.language.TelemetryLanguageException;
import org.hackystat.telemetry.language.parser.impl.ParseException;
import org.hackystat.telemetry.language.parser.impl.Token;
import org.hackystat.telemetry.language.parser.impl.TokenMgrError;

/**
 * Exception for telemetry query parsing related problems.
 * 
 * @author (Cedric) Qin Zhang
 * @version $Id$
 */
public class ParsingException extends TelemetryLanguageException {

  private static final long serialVersionUID = 1L;
  private int lineNumber = -1;
  private int colNumber = -1;

  /** Pattern for TokenMgrError message. */
  private static Pattern TOKEN_MGR_ERROR_MSG_PATTERN 
      = Pattern.compile("Lexical error at line \\d+, column \\d+. ");

  /** Pattern for one or more digits. */
  private static Pattern DIGITS_PATTERN = Pattern.compile("\\d+");
  
  /**
   * Constructs this parsing exception. Only use this constructor if you don't
   * know where the parser encounters the error.
   * 
   * @param message The error message.
   */
  public ParsingException(String message) {
    super(message);
  }

  /**
   * Constructs this parsing exception.
   * 
   * @param message The error message.
   * @param errorLineNumber The line (1-indexed) where the parser encounters the error.
   * @param errorColumnNumber The column (1-indexed) where the parser encounters the error.
   */
  public ParsingException(String message, int errorLineNumber, int errorColumnNumber) {
    super(message);
    this.lineNumber = errorLineNumber;
    this.colNumber = errorColumnNumber;
  }

  /**
   * Constructs this parsing exception. <p/> <b>Important: </b> If the exception
   * passed in is of type <code>ParseException</code>, then there is special
   * logic to extract error line number and column number. This works with the
   * ParseException code generated with JavaCC 3.1. I am not sure whether it
   * will work correctly with other versions of JavaCC, since it uses some
   * internal knowledge of the parser to get the line number and column number
   * where the parse error occurs.
   * 
   * @param exception The exception to be wrapped.
   */
  public ParsingException(Throwable exception) {
    super(exception.getMessage());

    // JavaCC specific stuff, works with JavaCC 3.1 and 4.0, not sure about other versions.
    if (exception instanceof ParseException) {
      ParseException parseException = (ParseException) exception;
      if (parseException.currentToken != null) {
        try {
          // I am not too sure about JavaCC internal implementation, so I put the following code
          // in a try block, just in case I get some null pointer error.
          Token nextToken = parseException.currentToken.next;
          this.lineNumber = nextToken.beginLine;
          this.colNumber = nextToken.beginColumn;
        }
        catch (Exception ex) {
          this.lineNumber = -1;
          this.colNumber = -1;
        }
      }
    }
    else if (exception instanceof TokenMgrError) {
      int[] lineAndColumnNumber = this.parseTokenMgrErrorMessage((TokenMgrError) exception);
      this.lineNumber = lineAndColumnNumber[0];
      this.colNumber = lineAndColumnNumber[1];
    }
  }

  /**
   * Parses the message in <code>TokenMgrError</code> object, and try to
   * extract line number and col number;
   * 
   * @param tokenMgrError An instance of <code>TokenMgrError</code>.
   * 
   * @return An integer array of 2 elements, the first is line number, the
   *         second is column number. Note that if there is any error extracting
   *         the information, then the returned integer array is {-1, -1}.
   */
  private int[] parseTokenMgrErrorMessage(TokenMgrError tokenMgrError) {
    // If you call TokenMgrError.getMessage(), it returns a string like:
    // "Lexical error at line 2, column 53. Other stuff..."
    // I am trying to extract line number and column number, this is the only way.
    int[] lineAndColumnNumber = new int[] { -1, -1 };
    try {
      String tokenMgrErrorMsg = tokenMgrError.getMessage();
      Matcher tokenMgrErrorMsgMatcher = TOKEN_MGR_ERROR_MSG_PATTERN.matcher(tokenMgrErrorMsg);
      if (tokenMgrErrorMsgMatcher.find()) {
        Matcher digitsMatcher = DIGITS_PATTERN.matcher(tokenMgrErrorMsgMatcher.group());
        if (digitsMatcher.find()) {
          String strLineNumber = digitsMatcher.group();
          if (digitsMatcher.find()) {
            String strColNumber = digitsMatcher.group();
            lineAndColumnNumber[0] = Integer.parseInt(strLineNumber);
            lineAndColumnNumber[1] = Integer.parseInt(strColNumber);
          }
        }
      }
    }
    catch (Exception e) {
      lineAndColumnNumber[0] = -1;
      lineAndColumnNumber[1] = -1;
    }
    return lineAndColumnNumber;
  }

  /**
   * Gets the line number (1-indexed) where the parser encounters the error.
   * 
   * @return The line number, or a non-positive number indication such information 
   *         is not available.
   */
  public int getErrorLineNumber() {
    return this.lineNumber;
  }

  /**
   * Gets the column number (1-indexed) where the parser encounters the error.
   * 
   * @return The column number, or a non-positive number indication such information 
   *         is not available.
   */
  public int getErrorColumnNumber() {
    return this.colNumber;
  }
}
