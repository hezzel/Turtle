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

package turtle.connection.telnet;

import java.util.ArrayList;
import turtle.interfaces.immutable.TelnetCode;

/**
 * A telnet command of the form IAC <X> <option>, where <X> is one of WILL, WONT, DO, DONT; such a
 * command indicates support for certain features from the client or the server.
 */
public class SupportTelnetCommand implements TelnetCode {
  private int _command;
  private int _option;

  public SupportTelnetCommand(int cmd, int option) {
    if (cmd != WILL && cmd != WONT && cmd != DO && cmd != DONT) {
      throw new Error("Using SupportTelnetCommand for something other than an option negotation.");
    }
    _command = cmd;
    _option = option;
  }

  /**
   * Tests whether the given arraylist represents a complete SupportTelnetCommand; if so, a
   * SupportTelnetCommand is returned; if not, null is returned.
   */
  public static TelnetCode readFromArrayList(ArrayList<Integer> list) {
    if (list.size() < 3) return null;
    int cmd = list.get(1);
    if (cmd != WILL && cmd != WONT && cmd != DO && cmd != DONT) return null;
    if (list.size() != 3) throw new Error("Telnet scanning not done char by char?");
    return new SupportTelnetCommand(cmd, list.get(2));
  }

  public int queryCommand() { return _command; }
  public int queryOption() { return _option; }
  public int[] querySubNegotiation() { return null; }

  public int[] queryCompleteCode() {
    return new int[] { TelnetCode.IAC, _command, _option };
  }
}

