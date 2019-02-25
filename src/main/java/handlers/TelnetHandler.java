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

import java.util.ArrayList;
import turtle.interfaces.immutable.TelnetCode;
import turtle.interfaces.immutable.TurtleEvent;
import turtle.interfaces.EventListener;
import turtle.interfaces.TelnetSender;
import turtle.events.InformationEvent;
import turtle.events.TelnetEvent;
import turtle.connection.telnet.*;
import turtle.EventBus;

/** The Telnet Handler listens for telnet events and handles them as appropriate. */
public class TelnetHandler implements EventListener {
  private static final int TELOPT_TTYPE = 24;
  private static final int TELOPT_NAWS = 31;
  private static final int TELOPT_COMPRESS = 86;
  private static final int TELOPT_MXP = 91;
  private static final int TELOPT_ZMP = 93;
  private static final int TELQUAL_IS   =  0;
  private static final int TELQUAL_SEND =  1;

  private TelnetSender _sender;

  public TelnetHandler(TelnetSender sender) {
    _sender = sender;
  }

  public boolean queryInterestedIn(TurtleEvent.EventKind kind) {
    return kind == TurtleEvent.EventKind.TELNET;
  }

  private String telnetToString(TelnetCode code) {
    String ret = "IAC ";
    String[] commands = { "NOP", "DAT", "BRK", "IP", "AO", "AYT", "EC", "EL", "GA",
                          "SB", "DO", "DONT", "WILL", "WONT" };
    int cmd = code.queryCommand();
    if (cmd >= 241 && cmd <= 254) ret += commands[cmd - 241] + " ";
    else ret += cmd + " ";

    int option = code.queryOption();
    if (option == TELOPT_TTYPE) ret += "TTYPE ";
    else if (option == TELOPT_NAWS) ret += "NAWS";
    else if (option == TELOPT_COMPRESS) ret += "COMPRESS";
    else if (option == TELOPT_MXP) ret += "MXP";
    else if (option == TELOPT_ZMP) ret += "ZMP";
    else if (option != -1) ret += option + " ";

    int[] sub = code.querySubNegotiation();
    if (sub != null) {
      int start = 1;
      if (sub.length != 0) {
        if (sub[0] == TELQUAL_IS) ret += "IS ";
        else if (sub[0] == TELQUAL_SEND) ret += "SEND ";
        else start = 0;
      }
      for (int i = start; i < sub.length; i++) ret += sub[i] + " ";
      ret += "IAC SE";
    }
    return ret;
  }

  public void eventOccurred(TurtleEvent event) {
    TelnetEvent evt = (TelnetEvent)event;
    TelnetCode code = evt.queryTelnetCode();
    EventBus.eventOccurred(new InformationEvent("[Received telnet: "+ telnetToString(code) + "]"));
    int command = code.queryCommand();
    int option = code.queryOption();
    // here handle codes we actually want to do something with
    if (handleSupportedCommand(command, option, code.querySubNegotiation())) return;
    // remaining codes are not supported; inform the server of this
    if (command == TelnetCode.WILL) rejectWill(option);
    if (command == TelnetCode.WONT) acceptWont(option);
    if (command == TelnetCode.DO)   rejectDo(option);
  }

  private void send(TelnetCode code) {
    _sender.sendTelnet(code);
    EventBus.eventOccurred(new InformationEvent("[Sent telnet: " + telnetToString(code) + "]"));
  }

  private boolean handleSupportedCommand(int command, int option, int[] subnegotiation) {
    if (option == TELOPT_TTYPE) return handleTType(command, subnegotiation);
    return false;
  }

  private boolean handleTType(int command, int[] subn) {
    if (command == TelnetCode.DO) {
      send(new SupportTelnetCommand(TelnetCode.WILL, TELOPT_TTYPE));
      return true;
    }
    if (command == TelnetCode.SB && subn.length == 1 && subn[0] == TELQUAL_SEND) {
      byte[] parts = (new String("dumb")).getBytes();
      ArrayList<Integer> arr = new ArrayList<Integer>();
      arr.add(TELQUAL_IS);
      for (int i = 0; i < parts.length; i++) arr.add(parts[i] < 0 ? parts[i] + 256 : parts[i]);
      send(new SubNegotiationTelnetCommand(TELOPT_TTYPE, arr));
    }
    return false;
  }

  private void rejectWill(int option) {
    send(new SupportTelnetCommand(TelnetCode.DONT, option));
  }

  private void acceptWont(int option) {
    send(new SupportTelnetCommand(TelnetCode.DONT, option));
  }

  private void rejectDo(int option) {
    send(new SupportTelnetCommand(TelnetCode.WONT, option));
  }
}

