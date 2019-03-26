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
import turtle.interfaces.immutable.LayoutedText;
import turtle.interfaces.EventListener;
import turtle.interfaces.OutputTarget;
import turtle.styles.AnsiColour;
import turtle.styles.DefaultColour;
import turtle.styles.RGBAColour;
import turtle.styles.AttributeGroup;
import turtle.styles.AnsiCodeReader;
import turtle.styles.ColourString;
import turtle.events.InformationEvent;
import turtle.events.MudTextEvent;
import turtle.events.UserInputEvent;
import turtle.events.WarningEvent;

/**
 * The Information Handler listens for all kinds of events that require information to be printed
 * to the user; whether it is through InformationEvents or because there is text from the MUD or
 * anything that warrants informing the user about.
 * It is very possible that some of these events are additionally handled by other event listeners.
 */
public class InformationHandler implements EventListener {
  private OutputTarget _target;
  private AnsiCodeReader _ansireader;

  public InformationHandler(OutputTarget output) {
    _target = output;
    _ansireader = new AnsiCodeReader();
  }

  public void eventOccurred(TurtleEvent.EventKind kind, TurtleEvent event) {
    if (kind == TurtleEvent.EventKind.USERINPUT) {
      handleUserInput((UserInputEvent)event);
    }

    if (kind == TurtleEvent.EventKind.MUDTEXT) {
      handleMudText((MudTextEvent)event);
    }

    if (kind == TurtleEvent.EventKind.INFORMATION) {
      handleInformation((InformationEvent)event);
    }

    if (kind == TurtleEvent.EventKind.WARNING) {
       handleWarning((WarningEvent)event);
    }
  }

  private void handleUserInput(UserInputEvent event) {
    RGBAColour colour = new RGBAColour(255, 255, 120);
    _target.print(new ColourString(event.queryCommand() + "\n", colour));
  }

  private void handleMudText(MudTextEvent event) {
    LayoutedText txt = _ansireader.parse(event.queryText());
    _target.print(txt);
  }

  private AttributeGroup queryInformationAttributes(InformationEvent event) {
    if (event.queryInformationKind() == InformationEvent.InformationKind.TELNET) {
      return new AttributeGroup(new DefaultColour(false, true));
    }
    // default -- for instance FEEDBACK
    AnsiColour colour = new AnsiColour(AnsiColour.COL_GREEN, true);
    return new AttributeGroup(colour, AttributeGroup.ATT_ITALIC);
  }

  private void handleInformation(InformationEvent event) {
    _target.print(new ColourString(event.queryText() + "\n", queryInformationAttributes(event)));
  }

  private void handleWarning(WarningEvent event) {
    AnsiColour colour = new AnsiColour(AnsiColour.COL_RED, true);
    AttributeGroup ag = new AttributeGroup(colour, AttributeGroup.ATT_BOLD);
    _target.print(new ColourString(event.queryText() + "\n", ag));
  }
}

