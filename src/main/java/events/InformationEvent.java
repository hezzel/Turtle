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

package turtle.events;

import turtle.interfaces.immutable.TurtleEvent;

/**
 * This class represents the event that some information is available that needs to be printed to
 * the user.
 */
public class InformationEvent implements TurtleEvent {
  public enum InformationKind { FEEDBACK, TELNET };
  private String _text;
  private InformationKind _kind;

  /** Creates an InformationEvent with kind "FEEDBACK". */
  public InformationEvent(String txt) {
    if (txt == null) throw new Error("Cannot initialise InformationEvent with null!");
    _text = txt;
    _kind = InformationKind.FEEDBACK;
  }

  public InformationEvent(String txt, InformationKind kind) {
    if (txt == null) throw new Error("Cannot initialise InformationEvent with null!");
    _text = txt;
    _kind = kind;
  }

  public EventKind queryEventKind() {
    return EventKind.INFORMATION;
  }

  public InformationKind queryInformationKind() {
    return _kind;
  }

  public String queryText() {
    return _text;
  }
}

