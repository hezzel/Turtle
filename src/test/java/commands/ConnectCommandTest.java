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

import turtle.interfaces.immutable.Command;
import turtle.interfaces.CommandParser;
import turtle.commands.ConnectCommand;
import turtle.handlers.CommandParsingHandler;

public class ConnectCommandTest {
  private class TestCommandParser implements CommandParser {
    int _warningCount;
    String _lastWarning;
    CommandParser _delegate;

    public TestCommandParser() {
      _warningCount = 0;
      _lastWarning = null;
      _delegate = new CommandParsingHandler();
    }
    
    public String queryCommand(String text) { return _delegate.queryCommand(text); }
    public String word(String command, int num) { return _delegate.word(command, num); }
    public String wordsFrom(String command, int num) { return _delegate.wordsFrom(command,num); }
    public Command parseError(String text, String warning) {
      _lastWarning = text;
      _warningCount++;
      return null;
    }
  }

  @Test
  public void testBadCommand() {
    TestCommandParser parser = new TestCommandParser();
    String text = "#conect";
    assertTrue(ConnectCommand.parse(text, parser) == null);
    assertTrue(parser._warningCount == 1);
    assertTrue(parser._lastWarning.equals(text));
  }

  @Test
  public void testMissingHost() {
    TestCommandParser parser = new TestCommandParser();
    String text = "#connect";
    assertTrue(ConnectCommand.parse(text, parser) == null);
    assertTrue(parser._warningCount == 1);
    assertTrue(parser._lastWarning.equals(text));
  }

  @Test
  public void testNonNumericPort() {
    TestCommandParser parser = new TestCommandParser();
    String text = "#connect localhost host";
    assertTrue(ConnectCommand.parse(text, parser) == null);
    assertTrue(parser._warningCount == 1);
    assertTrue(parser._lastWarning.equals(text));
  }

  @Test
  public void testTooManyArguments() {
    TestCommandParser parser = new TestCommandParser();
    String text = "#connect localhost 4242 -s";
    assertTrue(ConnectCommand.parse(text, parser) == null);
    assertTrue(parser._warningCount == 1);
    assertTrue(parser._lastWarning.equals(text));
  }

  @Test
  public void testCorrectUsage() {
    TestCommandParser parser = new TestCommandParser();
    String text = "#connect localhost 4242";
    Command cmd = ConnectCommand.parse(text, parser);
    assertTrue(parser._warningCount == 0);
    assertTrue(cmd != null);
    assertTrue(cmd.queryCommandKind() == Command.CommandKind.CONNECTCMD);
    assertTrue(((ConnectCommand)cmd).queryHost().equals("localhost"));
    assertTrue(((ConnectCommand)cmd).queryPort() == 4242);
  }

  @Test
  public void testMissingPortOkay() {
    TestCommandParser parser = new TestCommandParser();
    String text = "#connect localhost";
    Command cmd = ConnectCommand.parse(text, parser);
    assertTrue(parser._warningCount == 0);
    assertTrue(cmd != null);
    assertTrue(cmd.queryCommandKind() == Command.CommandKind.CONNECTCMD);
    assertTrue(((ConnectCommand)cmd).queryHost().equals("localhost"));
    assertTrue(((ConnectCommand)cmd).queryPort() == 23);
  }
}

