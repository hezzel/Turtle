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
import turtle.interfaces.immutable.Command;
import turtle.interfaces.immutable.TurtleEvent;
import turtle.interfaces.CommandListener;
import turtle.interfaces.EventListener;
import turtle.EventBus;
import turtle.events.UserInputEvent;
import turtle.events.WarningEvent;
import turtle.commands.MudCommand;
import turtle.handlers.CommandParsingHandler;

public class CommandParsingHandlerTest {
  private class BoringListener implements EventListener, CommandListener {
    ArrayList<TurtleEvent> _events;
    ArrayList<Command> _commands;
    public BoringListener() {
      _events = new ArrayList<TurtleEvent>();
      _commands = new ArrayList<Command>();
    }
    public void eventOccurred(TurtleEvent.EventKind kind, TurtleEvent event) {
      _events.add(event);
      _commands.add(null);
    }
    public void commandGiven(Command.CommandKind kind, Command command) {
      _events.add(null);
      _commands.add(command);
    }
  }

  @Test
  public void testQueryCommand() {
    CommandParsingHandler handler = new CommandParsingHandler();
    assertEquals(handler.queryCommand("#heLLo"), "hello");
    assertEquals(handler.queryCommand("  #aa bb"), "aa");
    assertTrue(handler.queryCommand("aa #bb") == null);
  }

  @Test
  public void testWord() {
    CommandParsingHandler handler = new CommandParsingHandler();
    String txt = "  aaa b af;j b  cccd .";
    assertTrue(handler.word(txt, -1) == null);
    assertTrue(handler.word(txt, 0).equals("aaa"));
    assertTrue(handler.word(txt, 1).equals("b"));
    assertTrue(handler.word(txt, 2).equals("af;j"));
    assertTrue(handler.word(txt, 3).equals("b"));
    assertTrue(handler.word(txt, 4).equals("cccd"));
    assertTrue(handler.word(txt, 5).equals("."));
    assertTrue(handler.word(txt, 6).equals(""));
    assertTrue(handler.word(txt, 7).equals(""));
  }

  @Test
  public void testWordsFrom() {
    CommandParsingHandler handler = new CommandParsingHandler();
    String txt = "  aaa b af;j b  cccd .";
    assertTrue(handler.wordsFrom(txt, -1).equals(txt));
    assertTrue(handler.wordsFrom(txt, 0).equals("aaa b af;j b  cccd ."));
    assertTrue(handler.wordsFrom(txt, 1).equals("b af;j b  cccd ."));
    assertTrue(handler.wordsFrom(txt, 2).equals("af;j b  cccd ."));
    assertTrue(handler.wordsFrom(txt, 3).equals("b  cccd ."));
    assertTrue(handler.wordsFrom(txt, 4).equals("cccd ."));
    assertTrue(handler.wordsFrom(txt, 5).equals("."));
    assertTrue(handler.wordsFrom(txt, 6).equals(""));
    assertTrue(handler.wordsFrom(txt, 7).equals(""));
  }

  @Test
  public void testParseError() {
    CommandParsingHandler handler = new CommandParsingHandler();
    BoringListener listener = new BoringListener();
    EventBus.registerEventListener(listener);
    EventBus.registerCommandListener(listener);

    assertTrue(handler.parseError("Bing", "Bong") == null);
    assertTrue(listener._events.size() == 1);
    assertTrue(listener._events.get(0).queryEventKind() == TurtleEvent.EventKind.WARNING);
  }

  @Test
  public void testParseSeparator() {
    CommandParsingHandler handler = new CommandParsingHandler();
    BoringListener listener = new BoringListener();
    EventBus.registerEventListener(listener);
    EventBus.registerCommandListener(listener);

    String txt = "AAAAA;;  BBB ;;CD";
    TurtleEvent e = new UserInputEvent(txt);
    handler.eventOccurred(e.queryEventKind(), e);
    assertTrue(listener._commands.size() == 3);

    Command cmd = listener._commands.get(0);
    assertTrue(cmd.queryCommandKind() == Command.CommandKind.MUDCMD);
    assertTrue(((MudCommand)cmd).queryText().equals("AAAAA"));

    cmd = listener._commands.get(1);
    assertTrue(cmd.queryCommandKind() == Command.CommandKind.MUDCMD);
    assertTrue(((MudCommand)cmd).queryText().equals("  BBB "));
    
    cmd = listener._commands.get(2);
    assertTrue(cmd.queryCommandKind() == Command.CommandKind.MUDCMD);
    assertTrue(((MudCommand)cmd).queryText().equals("CD"));
  }

  @Test
  public void testUnknownCommand() {
    CommandParsingHandler handler = new CommandParsingHandler();
    BoringListener listener = new BoringListener();
    EventBus.registerEventListener(listener);
    EventBus.registerCommandListener(listener);

    String txt = "AAAAA;;  #BBB ;;CD";
    TurtleEvent e = new UserInputEvent(txt);
    handler.eventOccurred(e.queryEventKind(), e);
    assertTrue(listener._events.size() == 3);

    assertTrue(listener._events.get(0) == null);
    assertTrue(listener._events.get(1).queryEventKind() == TurtleEvent.EventKind.WARNING);
    assertTrue(listener._events.get(2) == null);
  }
}

