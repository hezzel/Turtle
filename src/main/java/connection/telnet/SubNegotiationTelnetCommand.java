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
 * A telnet command of the form IAC SB <option> <data> IAC SE, which can be used to communicate all
 * kinds of things in more detail.
 */
public class SubNegotiationTelnetCommand implements TelnetCode {
  private int _option;
  private int[] _data;

  public SubNegotiationTelnetCommand(int option, ArrayList<Integer> data) {
    _option = option;
    _data = new int[data.size()];
    for (int i = 0; i < data.size(); i++) _data[i] = data.get(i);
  }

  private SubNegotiationTelnetCommand(int option, int[] data) {
    _option = option;
    _data = data;
  }

  /**
   * Tests whether the given arraylist represents a complete SubNegotiationTelnetCommand; if so, a
   * SubNegotiationTelnetCommand is returned; if not, null is returned.
   */
  public static TelnetCode readFromArrayList(ArrayList<Integer> list) {
    if (list.size() < 4) return null;
    // we allow both IAC SB <option> <data> IAC SE and IAC SB <option> <data> SE, because some
    // servers erroneously omit the final IAC in some cases
    // (if this turns out to be a problem, just require it after all)
    if (!list.get(1).equals(SB) || !list.get(list.size()-1).equals(SE)) return null;

    int length = list.size()-4;
    if (list.get(list.size()-2).equals(IAC)) length--;
    
    int[] data = new int[length];
    for (int i = 0; i < length; i++) data[i] = list.get(i+3);

    return new SubNegotiationTelnetCommand(list.get(2), data);
  }

  public int queryCommand() { return SB; }
  public int queryOption() { return _option; }

  public int[] querySubNegotiation() {
    // as this class is immutable, we should return a copy, so that querying classes cannot
    // manipulate our data!
    int[] ret = new int[_data.length];
    System.arraycopy(_data, 0, ret, 0, _data.length);
    return ret;
  }

  public int[] queryCompleteCode() {
    int[] ret = new int[_data.length+5];
    ret[0] = IAC;
    ret[1] = SB;
    ret[2] = _option;
    for (int i = 0; i < _data.length; i++) ret[i+3] = _data[i];
    ret[ret.length-2] = IAC;
    ret[ret.length-1] = SE;
    return ret;
  }
}

