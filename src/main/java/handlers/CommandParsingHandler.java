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
import turtle.interfaces.EventListener;
import turtle.EventBus;
import turtle.events.UserInputEvent;
import turtle.events.CommandEvent;
import turtle.commands.*;

/**
 * The Command Parsing Handler listens for UserInput events and parses the corresponding command
 * into a TurtleCommand, which can be executed by the relevant handler.
 */
public class CommandParsingHandler implements EventListener {
  public boolean queryInterestedIn(TurtleEvent.EventKind kind) {
    return kind == TurtleEvent.EventKind.USERINPUT;
  }

  public void eventOccurred(TurtleEvent event) {
    UserInputEvent e = (UserInputEvent)event;
    String txt = e.queryCommand();
    Command cmd = parseInput(txt);
    if (cmd != null) EventBus.eventOccurred(new CommandEvent(cmd));
  }

  private Command parseInput(String text) {
    return new MudCommand(text);
  }
}
