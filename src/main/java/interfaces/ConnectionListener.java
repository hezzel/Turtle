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

package turtle.interfaces;

import turtle.interfaces.immutable.TelnetCode;

/**
 * A ConnectionListener is a class that is interested in a given connection.
 * The listener is responsible for taking care of responding in the right thread.
 */
public interface ConnectionListener {
  /** Used if the connection runs into errors and is broken non-neatly. */
  public void connectionFailed(String error);

  /**
   * Used if the connection is ended without errors either the server (remote = true) or by request
   * command (if remote = false).
   */
  public void connectionClosed(boolean remote);

  /** Used when the connection has successfully been established. */
  public void connectionEstablished(String host, String address, int port);

  /** Used when an IP address is found. */
  public void connectionFoundAddress(String host, String address, int port);

  /** Used when the connection has received text from the server. */
  public void connectionReceivedText(String text);

  /** Used when the connection has received a telnet code from the server. */
  public void connectionReceivedTelnet(TelnetCode code);

  /** Used when an unexpected error occurs that has not necessarily closed the connection. */
  public void connectionErrorOccurred(String explanation);
}

