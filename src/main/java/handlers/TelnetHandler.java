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

import java.nio.charset.StandardCharsets;
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
  private String _lastTtype;

  public TelnetHandler(TelnetSender sender) {
    _sender = sender;
    _lastTtype = null;
  }

  public boolean queryInterestedIn(TurtleEvent.EventKind kind) {
    return kind == TurtleEvent.EventKind.TELNET;
  }

  private void sendEvent(String kind, TelnetCode code) {
    String txt = "[" + kind + " telnet: " + telnetToString(code) + "]";
    InformationEvent event = new InformationEvent(txt, InformationEvent.InformationKind.TELNET);
    EventBus.eventOccurred(event);
  }

  public void eventOccurred(TurtleEvent event) {
    TelnetEvent evt = (TelnetEvent)event;
    TelnetCode code = evt.queryTelnetCode();
    sendEvent("Received", code);
    int command = code.queryCommand();
    int option = code.queryOption();
    // here handle codes we actually want to do something with
    if (handleSupportedCommand(command, option, code.querySubNegotiation())) return;
    // remaining codes are not supported; inform the server of this
    if (command == TelnetCode.WILL) rejectWill(option);
    if (command == TelnetCode.WONT) acceptWont(option);
    if (command == TelnetCode.DO)   rejectDo(option);
  }

  /**
   * Helper function for telnetCodeToString:
   * Returns a string representation of the given telnet command.
   */
  private String commandToString(int cmd) {
    String[] commands = { "NOP", "DAT", "BRK", "IP", "AO", "AYT", "EC", "EL", "GA",
                          "SB", "WILL", "WONT", "DO", "DONT" };
    if (cmd >= 241 && cmd <= 254) return commands[cmd - 241];
    else return "" + cmd;
  }

  /**
   * Helper function for telnetCodeToString:
   * Returns a string representation of the given telnet option.
   */
  private String optionToString(int option) {
    if (option == TELOPT_TTYPE) return "TTYPE";
    if (option == TELOPT_NAWS) return "NAWS";
    if (option == TELOPT_COMPRESS) return "COMPRESS";
    if (option == TELOPT_MXP) return "MXP";
    if (option == TELOPT_ZMP) return "ZMP";
    return "" + option;
  }

  /**
   * Helper function for telnetCodeToString:
   * Reads the given (partial) integer array as though it is a 0-separated list of strings, each
   * presented as a byte array that defines a UTF-8 string.
   */
  private ArrayList<String> makeStrings(int[] code, int start, int end) {
    ArrayList<String> ret = new ArrayList<String>();
    for (int p = start; p < end; p++) {
      if (code[p] == 0) continue;
      int q = p;
      while (q < end && code[q] != 0) q++;
      byte[] bs = new byte[q-p];
      for (int i = 0; i < bs.length; i++) bs[i] = (byte)code[p+i];
      ret.add(new String(bs, StandardCharsets.UTF_8));
      p = q;
    }
    return ret;
  }

  /**
   * Helper function for telnetCodeToString:
   * Translates *just* the subnegotiation part to a string, and ends with a space.
   */
  private String subNegotiationToString(int option, int[] subn) {
    if (subn.length == 0) return " ";

    int start = 1;
    String ret = "";
    if (subn[0] == TELQUAL_IS) ret += "IS ";
    else if (subn[0] == TELQUAL_SEND) ret += "SEND ";
    else start = 0;

    if ((option == TELOPT_TTYPE || option == TELOPT_ZMP) && subn[0] == TELQUAL_IS) {
      ArrayList<String> values = makeStrings(subn, 1, subn.length);
      for (int i = 0; i < values.size(); i++) {
        ret += "\"" + values.get(i) + "\" ";
      }
    }
    else {
      for (int i = start; i < subn.length; i++) ret += subn[i] + " ";
    }

    return ret;
  }

  /**
   * Returns a String representation of the given telnet code.
   * This tries to pretty-print, taking into account all information known by the handler about
   * specific kinds of telnet commands.
   */
  public String telnetToString(TelnetCode code) {
    String ret = "IAC " + commandToString(code.queryCommand());
    int option = code.queryOption();
    if (option != -1) ret += " " + optionToString(option);
    int[] subn = code.querySubNegotiation();
    if (subn != null) ret += " " + subNegotiationToString(option, subn) + "IAC SE";
    return ret;
  }

  private void send(TelnetCode code) {
    _sender.sendTelnet(code);
    sendEvent("Sent", code);
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

  private boolean handleSupportedCommand(int command, int option, int[] subnegotiation) {
    if (option == TELOPT_TTYPE) return handleTType(command, subnegotiation);
    return false;
  }

  /**
   * This function translates the given list to the sequence of integers that represents it, and
   * appends this sequence to arr.
   */
  private void addStringToIntList(String str, ArrayList<Integer> arr) {
    byte[] parts = str.getBytes();
    for (int i = 0; i < parts.length; i++) arr.add(parts[i] < 0 ? parts[i] + 256 : parts[i]);
  }

  /**
   * Following the standards, we should send our supported terminal types in order of preference,
   * until the server stops asking; however, if they keep asking and we're through, we should
   * repeat the last one.
   * If they keep asking even after that, we'll just restart the loop.
   */
  private void sendTTypeRequest() {
    String sendNow;

    if (_lastTtype == null) sendNow = "xterm16m";
    else if (_lastTtype.equals("xterm16m")) sendNow = "xterm256";
    else sendNow = "ansi";

    if (_lastTtype == null || !_lastTtype.equals(sendNow)) _lastTtype = sendNow;
    else _lastTtype = null;

    ArrayList<Integer> arr = new ArrayList<Integer>();
    arr.add(TELQUAL_IS);
    addStringToIntList(sendNow, arr);
    send(new SubNegotiationTelnetCommand(TELOPT_TTYPE, arr));
  }

  private boolean handleTType(int command, int[] subn) {
    if (command == TelnetCode.DO) {
      send(new SupportTelnetCommand(TelnetCode.WILL, TELOPT_TTYPE));
      return true;
    }
    if (command == TelnetCode.SB && subn.length == 1 && subn[0] == TELQUAL_SEND) {
      sendTTypeRequest();
      return true;
    }
    return false;
  }
}

