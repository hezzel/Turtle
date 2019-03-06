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

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import turtle.interfaces.immutable.TelnetCode;
import turtle.interfaces.immutable.TurtleEvent;
import turtle.interfaces.EventListener;
import turtle.interfaces.TelnetSender;
import turtle.EventBus;
import turtle.connection.telnet.*;
import turtle.events.InformationEvent;
import turtle.events.TelnetEvent;
import turtle.handlers.TelnetHandler;

public class TelnetHandlerTest {
  private class Listener implements EventListener {
    private TurtleEvent _last;

    public Listener() { _last = null; }

    public boolean queryInterestedIn(TurtleEvent.EventKind kind) { return true; }
    public void eventOccurred(TurtleEvent event) { _last = event; }
    public TurtleEvent queryLast() { return _last; }
  }

  private class Sender implements TelnetSender {
    private TelnetCode _last;

    public Sender() { _last = null; }
    public void sendTelnet(TelnetCode code) { _last = code; }
    public TelnetCode queryLast() { return _last; }
  }
  
  private Listener _listener;
  private Sender _sender;
  private TelnetHandler _handler;

  public TelnetHandlerTest() {
    _listener = new Listener();
    _sender = new Sender();
    _handler = new TelnetHandler(_sender);
    EventBus.registerEventListener(_listener);
  }

  @Test
  public void testInformation() {
    TelnetCode code = new SingleTelnetCommand(TelnetCode.NOP);
    _handler.eventOccurred(new TelnetEvent(code));
    TurtleEvent last = _listener.queryLast();
    assertTrue(last != null);
    assertTrue(last instanceof InformationEvent);
    assertTrue(((InformationEvent)last).queryText().equals("[Received telnet: IAC NOP]"));
  }

  @Test
  public void testPrinting() {
    TelnetCode code1 = new SingleTelnetCommand(TelnetCode.AYT);
    TelnetCode code2 = new SupportTelnetCommand(TelnetCode.WILL, 86);
    TelnetCode code3 = new SupportTelnetCommand(TelnetCode.DONT, 93);
    assertTrue(_handler.telnetToString(code1).equals("IAC AYT"));
    assertTrue(_handler.telnetToString(code2).equals("IAC WILL COMPRESS"));
    assertTrue(_handler.telnetToString(code3).equals("IAC DONT ZMP"));

    ArrayList<Integer> subn = new ArrayList<Integer>();
    subn.add(0);
    subn.add((int)'a');
    subn.add((int)'b');
    subn.add((int)'c');
    TelnetCode code4 = new SubNegotiationTelnetCommand(24, subn);
    assertTrue(_handler.telnetToString(code4).equals("IAC SB TTYPE IS \"abc\" IAC SE"));

    TelnetCode code5 = new SubNegotiationTelnetCommand(25, subn);
    assertTrue(_handler.telnetToString(code5).equals("IAC SB 25 IS 97 98 99 IAC SE"));

    subn = new ArrayList<Integer>();
    subn.add(0);
    subn.add((int)'a');
    subn.add((int)'b');
    subn.add((int)'c');
    subn.add(0);
    subn.add((int)'x');
    subn.add((int)'y');
    subn.add(0);
    TelnetCode code6 = new SubNegotiationTelnetCommand(93, subn);
    assertTrue(_handler.telnetToString(code6).equals("IAC SB ZMP IS \"abc\" \"xy\" IAC SE"));
  }

  @Test
  public void testTTypeSupported() {
    TelnetCode code = new SupportTelnetCommand(TelnetCode.DO, 24);
    _handler.eventOccurred(new TelnetEvent(code));
    TelnetCode result = _sender.queryLast();
    assertTrue(result.queryCommand() == TelnetCode.WILL);
    assertTrue(result.queryOption() == 24);
  }

  @Test
  public void testTTypeLoop() {
    ArrayList<Integer> arr = new ArrayList<Integer>();
    arr.add(1);
    TelnetCode request = new SubNegotiationTelnetCommand(24, arr);
    String resultRepresentation;

    _handler.eventOccurred(new TelnetEvent(request));
    resultRepresentation = _handler.telnetToString(_sender.queryLast());
    assertTrue(resultRepresentation.equals("IAC SB TTYPE IS \"xterm16m\" IAC SE"));

    _handler.eventOccurred(new TelnetEvent(request));
    resultRepresentation = _handler.telnetToString(_sender.queryLast());
    assertTrue(resultRepresentation.equals("IAC SB TTYPE IS \"xterm256\" IAC SE"));

    _handler.eventOccurred(new TelnetEvent(request));
    resultRepresentation = _handler.telnetToString(_sender.queryLast());
    assertTrue(resultRepresentation.equals("IAC SB TTYPE IS \"ansi\" IAC SE"));

    _handler.eventOccurred(new TelnetEvent(request));
    resultRepresentation = _handler.telnetToString(_sender.queryLast());
    assertTrue(resultRepresentation.equals("IAC SB TTYPE IS \"ansi\" IAC SE"));

    _handler.eventOccurred(new TelnetEvent(request));
    resultRepresentation = _handler.telnetToString(_sender.queryLast());
    assertTrue(resultRepresentation.equals("IAC SB TTYPE IS \"xterm16m\" IAC SE"));

    _handler.eventOccurred(new TelnetEvent(request));
    resultRepresentation = _handler.telnetToString(_sender.queryLast());
    assertTrue(resultRepresentation.equals("IAC SB TTYPE IS \"xterm256\" IAC SE"));
  }
}

