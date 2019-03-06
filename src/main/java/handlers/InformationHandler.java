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

import turtle.interfaces.immutable.TurtleEvent;
import turtle.interfaces.EventListener;
import turtle.interfaces.OutputTarget;
import turtle.styles.AnsiColour;
import turtle.styles.RGBAColour;
import turtle.styles.AttributeGroup;
import turtle.styles.ColourString;
import turtle.events.InformationEvent;
import turtle.events.MudTextEvent;
import turtle.events.UserCommandEvent;
import turtle.events.WarningEvent;

/**
 * The Information Handler listens for all kinds of events that require information to be printed
 * to the user; whether it is through InformationEvents or because there is text from the MUD or
 * anything that warrants informing the user about.
 * It is very possible that some of these events are additionally handled by other event listeners.
 */
public class InformationHandler implements EventListener {
  private OutputTarget _target;

  public InformationHandler(OutputTarget output) {
    _target = output;
  }

  public boolean queryInterestedIn(TurtleEvent.EventKind kind) {
    return kind == TurtleEvent.EventKind.USERCMD ||
           kind == TurtleEvent.EventKind.MUDTEXT ||
           kind == TurtleEvent.EventKind.INFORMATION ||
           kind == TurtleEvent.EventKind.WARNING;
  }

  public void eventOccurred(TurtleEvent event) {
    if (event.queryEventKind() == TurtleEvent.EventKind.USERCMD) {
      handleUserCommand((UserCommandEvent)event);
    }

    if (event.queryEventKind() == TurtleEvent.EventKind.MUDTEXT) {
      handleMudText((MudTextEvent)event);
    }

    if (event.queryEventKind() == TurtleEvent.EventKind.INFORMATION) {
      handleInformation((InformationEvent)event);
    }

    if (event.queryEventKind() == TurtleEvent.EventKind.WARNING) {
       handleWarning((WarningEvent)event);
    }
  }

  private void handleUserCommand(UserCommandEvent event) {
    RGBAColour colour = new RGBAColour(255, 255, 120);
    _target.print(new ColourString(event.queryCommand() + "\n", colour));
  }

  private void handleMudText(MudTextEvent event) {
    _target.print(new ColourString(event.queryText()));
  }

  private void handleInformation(InformationEvent event) {
    AnsiColour colour = new AnsiColour(AnsiColour.COL_GREEN, true);
    AttributeGroup ag = new AttributeGroup(colour, AttributeGroup.ATT_ITALIC);
    _target.print(new ColourString(event.queryText() + "\n", ag));
  }

  private void handleWarning(WarningEvent event) {
    AnsiColour colour = new AnsiColour(AnsiColour.COL_RED, true);
    AttributeGroup ag = new AttributeGroup(colour, AttributeGroup.ATT_BOLD);
    _target.print(new ColourString(event.queryText() + "\n", ag));
  }
}

