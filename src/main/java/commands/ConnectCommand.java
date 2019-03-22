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

package turtle.commands;

import turtle.interfaces.immutable.Command;

public class ConnectCommand implements Command {
  String _host;
  int _port;

  public ConnectCommand(String host, int port) {
    if (host == null) throw new Error("ConnectCommand given an empty host.");
    _host = host;
    _port = port;
  }

  public CommandKind queryCommandKind() {
    return CommandKind.CONNECTCMD;
  }

  public String queryHost() {
    return _host;
  }

  public int queryPort() {
    return _port;
  }
}
