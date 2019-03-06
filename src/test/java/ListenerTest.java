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

import turtle.EventBus;
import turtle.interfaces.immutable.TurtleEvent;
import turtle.interfaces.EventListener;
import turtle.events.*;

public class ListenerTest {
  private class Listener implements EventListener {
    private int _called;
    private String _lastCommand;
    private boolean _allowAll;

    public Listener() { _called = 0; _lastCommand = ""; _allowAll = false; }

    public void setAllAllowed() {
      _allowAll = true;
    }

    public boolean queryInterestedIn(TurtleEvent.EventKind kind) {
      if (_allowAll) return true;
      else return kind == TurtleEvent.EventKind.USERCMD;
    }

    public void eventOccurred(TurtleEvent event) {
      _called++;
      if (event.queryEventKind() == TurtleEvent.EventKind.USERCMD) {
        _lastCommand = ((UserCommandEvent)event).queryCommand();
      }
    }

    public int queryCallCount() {
      return _called;
    }

    public String queryLastCommand() {
      return _lastCommand;
    }
  }

  @Test
  public void testListener() {
    Listener l = new Listener();

    EventBus.registerEventListener(l);
    EventBus.eventOccurred(new UserCommandEvent("X"));
    assertTrue(l.queryCallCount() == 1);
    EventBus.eventOccurred(new UserCommandEvent("Y"));
    assertTrue(l.queryCallCount() == 2);
    assertTrue(l.queryLastCommand().equals("Y"));
    EventBus.eventOccurred(new MudTextEvent("Z"));
    assertTrue(l.queryCallCount() == 2);
    assertTrue(l.queryLastCommand().equals("Y"));
    l.setAllAllowed();
    EventBus.eventOccurred(new MudTextEvent("A"));
    assertTrue(l.queryCallCount() == 3);
    assertTrue(l.queryLastCommand().equals("Y"));
    EventBus.eventOccurred(new UserCommandEvent("A"));
    assertTrue(l.queryCallCount() == 4);
    EventBus.eventOccurred(new UserCommandEvent("A"));
  }
}
