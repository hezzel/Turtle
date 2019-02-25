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

/** A telnet command without any parameters (so IAC <X>). */
public class SingleTelnetCommand implements TelnetCode {
  private int _command;

  public SingleTelnetCommand(int cmd) {
    _command = cmd;
  }

  /**
   * Tests whether the given arraylist represents a complete SingleTelnetCommand; if so, a
   * SingleTelnetCommand is returned; if not, null is returned.
   */
  public static TelnetCode readFromArrayList(ArrayList<Integer> list) {
    if (list.size() < 2) return null;
    int cmd = list.get(1);
    if (cmd != WILL && cmd != WONT && cmd != DO && cmd != DONT && cmd != SB) {
      if (list.size() != 2) throw new Error("Telnet scanning not done char by char?");
      return new SingleTelnetCommand(cmd);
    }
    else return null;
  }

   public String toString() {
    if (_command == NOP) return "IAC NOP";
    else if (_command == DAT) return "IAC DAT";
    else if (_command == BRK) return "IAC BRK";
    else if (_command == IP) return "IAC IP";
    else if (_command == AO) return "IAC AO";
    else if (_command == AYT) return "IAC AYT";
    else if (_command == EC) return "IAC EC";
    else if (_command == EL) return "IAC EL";
    else if (_command == GA) return "IAC GA";
    else return "IAC [unknown(" + _command + ")]";
  }
 
  public int queryCommand() { return _command; }
  public int queryOption() { return -1; }
  public int[] querySubNegotiation() { return null; }

  public int[] queryCompleteCode() {
    return new int[] { IAC, _command };
  }
}

