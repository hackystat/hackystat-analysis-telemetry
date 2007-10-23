package org.hackystat.telemetry.analyzer.language.ast;

/**
 * Text position indiating start and end location of telemetry definition.
 * 
 * @author (Cedric) Qin ZHANG
 * @version $Id$
 */
public class TextPosition {

  /**
   * BeginLine and beginColumn describe the position of the first character
   * of the text; endLine and endColumn describe the position of the
   * last character of the text.
   */
  private int beginLine, beginColumn, endLine, endColumn;
  
  /**
   * Constructs this instance.
   * 
   * @param beginLine The row position of the first character of the text.
   * @param beginColumn The column position of the first character of the text.
   * @param endLine The row position of the last character of the text.
   * @param endColumn The column position of the last character of the text.
   */
  public TextPosition(int beginLine, int beginColumn, int endLine, int endColumn) {
    this.beginLine = beginLine;
    this.beginColumn = beginColumn;
    this.endLine = endLine;
    this.endColumn = endColumn;
  }

  /**
   * Gets the row position of the first character of the text.
   * @return The row position of the first character of the text.
   */
  public int getBeginLine() {
    return this.beginLine;
  }

  /**
   * Gets the column position of the first character of the text.
   * @return The column position of the first character of the text.
   */
  public int getBeginColumn() {
    return this.beginColumn;
  }

  /**
   * Gets the row position of the last character of the text.
   * @return The row position of the last character of the text.
   */
  public int getEndLine() {
    return this.endLine;
  }

  /**
   * Gets the column position of the last character of the text.
   * @return The column position of the last character of the text.
   */
  public int getEndColumn() {
    return this.endColumn;
  }
 
  /**
   * Gets string representation of this instance.
   * @return The string representation.
   */
  @Override
  public String toString() {
    return "TextPosition: (" + this.beginLine + ", " + this.beginColumn + ") - ("
      + this.endLine + ", " + this.endColumn + ")";
  }
}
