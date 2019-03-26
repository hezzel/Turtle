/**************************************************************************************************
*   Turtle Mud Client                                                                             *
*   Copyright (C) 2019 Cynthia Kop                                                                *
*                                                                                                 *
*   This program is protected under the GNU GPL (See COPYING).                                    *
*                                                                                                 *
*   This program is free software; you can redistribute it and/or modify  it under the terms of   *
*   the GNU General Public License as published by the Free Software Foundation; either version   *
*   2 of the License, or (at your option) any later version.                                      *
*                                                                                                 *
*   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;     *
*   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     *
*   See the GNU General Public License for more details.                                          *
*                                                                                                 *
*   You should have received a copy of the GNU General Public License along with this program;    *
*   if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA   *
*   02111-1307  USA                                                                               *
**************************************************************************************************/

package turtle.handlers;

import turtle.interfaces.immutable.Command;
import turtle.interfaces.immutable.TurtleEvent;
import turtle.interfaces.CommandParser;
import turtle.interfaces.EventListener;
import turtle.EventBus;
import turtle.events.CommandEvent;
import turtle.events.UserInputEvent;
import turtle.events.WarningEvent;
import turtle.commands.*;

/**
 * The Command Parsing Handler listens for UserInput events and parses the corresponding command
 * into a TurtleCommand, which can be executed by the relevant handler.
 */
public class CommandParsingHandler implements CommandParser, EventListener {
  private static final char TURTLECHAR = '#';
  private static final String SEPARATOR = ";;";

  public void eventOccurred(TurtleEvent.EventKind kind, TurtleEvent event) {
    if (kind != TurtleEvent.EventKind.USERINPUT) return;
    UserInputEvent e = (UserInputEvent)event;
    execute(e.queryCommand());
  }

  /**
   * Executes a user command, which may contain the SEPARATOR (so can lead to one or more Command
   * events if no error occurs).
   */
  private void execute(String txt) {
    while (txt != null) {
      int k = txt.indexOf(SEPARATOR);
      String cmd;
      if (k == -1) { cmd = txt; txt = null; }
      else { cmd = txt.substring(0, k); txt = txt.substring(k + SEPARATOR.length()); }
      Command command = parseSingleCommand(cmd);
      if (command != null) EventBus.eventOccurred(new CommandEvent(command));
    }
  }

  /** Parses a text without separators into a single Command. */
  private Command parseSingleCommand(String text) {
    String cmd = queryCommand(text);
    if (cmd == null) return new MudCommand(text);

    if (cmd.equals("connect")) return ConnectCommand.parse(text, this);
    if (cmd.equals("scroll")) return ScrollCommand.parse(text, this);

    EventBus.eventOccurred(new WarningEvent("Unknown Turtle command: " + text));
    return null;
  }

  public String queryCommand(String text) {
    String cmd = word(text, 0);
    if (cmd.equals("") || cmd.charAt(0) != TURTLECHAR) return null;
    else return cmd.substring(1).toLowerCase();
  }

  public String word(String command, int num) {
    int i, j, len = command.length();
    if (num < 0) return null;
    for (i = 0; i < len && command.charAt(i) == ' '; i++);
    for (j = i; j < len && command.charAt(j) != ' '; j++);
    if (i == len) return "";
    if (j == len) {
      if (num == 0) return command.substring(i);
      else return "";
    }
    if (num <= 0) return command.substring(i, j);
    if (j == len-1) return "";
    return word(command.substring(j+1), num-1);
  }

  public String wordsFrom(String command, int num) {
    if (num < 0) return command;
    int i, j, len = command.length();
    for (i = 0; i < len && command.charAt(i) == ' '; i++);
    if (num == 0) return command.substring(i);
    for (j = i; j < len && command.charAt(j) != ' '; j++);
    if (j >= len-1) return ""; 
    return wordsFrom(command.substring(j+1), num-1);
  }

  public Command parseError(String text, String warning) {
    EventBus.eventOccurred(new WarningEvent("Parsing error: " + warning));
    return null;
  }
}

