package org.hackystat.telemetry.reducer.util;

import org.hackystat.telemetry.reducer.TelemetryReducerException;

/**
 * Utility function for handling reducer options.
 * 
 * @author (Cedric) Qin Zhang
 * @version $Id$.
 */
public class ReducerOptionUtility {

  /**
   * Parses boolean reduction option.
   * 
   * @param optionIndex The 0-based option position.
   * @param optionString The option string to be parsed.
   * 
   * @return True or false.
   * 
   * @throws TelemetryReducerException If the option string does not represent a boolean value.
   */
  public static boolean parseBooleanOption(int optionIndex, String optionString) 
      throws TelemetryReducerException {
    if ("true".equalsIgnoreCase(optionString) || "yes".equalsIgnoreCase(optionString)) {
      return true; 
    }
    else if ("false".equalsIgnoreCase(optionString) || "no".equalsIgnoreCase(optionString)) {
      return false;
    }
    else {
      throw new TelemetryReducerException("Parameter " + (optionIndex + 1) 
                                   + " must indicate a boolean value.");
    }
  }
  
  /**
   * Parses mode option.
   * 
   * @param optionIndex The 0-based option position.
   * @param modes An array of acceptable strings.
   * @param modeString The option string to be parsed.
   * 
   * @return The index into modes where modes[index] == modeString (case insensitive).
   * 
   * @throws TelemetryReducerException If no match can be found.
   */
  public static int parseModeOption(int optionIndex, String[] modes, String modeString) 
      throws TelemetryReducerException {
    for (int i = 0; i < modes.length; i++) {
      if (modes[i].equalsIgnoreCase(modeString)) {
        return i;
      }
    }
    //none matching
    StringBuffer buffer = new StringBuffer(32);
    buffer.append("Parameter ").append(optionIndex + 1).append(" must be one of ");
    for (int i = 0; i < modes.length; i++) {
      buffer.append("'").append(modes[i]).append("'");
      buffer.append(i == modes.length - 1 ? "." : ", ");
    }
    throw new TelemetryReducerException(buffer.toString());
  }
}
