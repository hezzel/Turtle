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

import java.awt.event.KeyEvent;

import turtle.interfaces.immutable.TurtleEvent;
import turtle.interfaces.EventListener;
import turtle.interfaces.windowing.InputWindowComponent;
import turtle.EventBus;
import turtle.events.UserCommandEvent;
import turtle.windowing.InputHistory;
import turtle.windowing.InputWindow;

public class InputWindowTest {
  private class TestComponent implements InputWindowComponent {
    String _lastText;
    boolean _selected;

    public TestComponent() {
      _lastText = null;
      _selected = false;
    }

    public String getText() { return _lastText; }
    public void changeText(String txt) { _lastText = txt; }
    public void selectAll() { _selected = false; }
  }

  private class TestListener implements EventListener {
    int _count;
    String _lastCommand;

    public TestListener() {
      _count = 0;
      _lastCommand = null;
    }

    public boolean queryInterestedIn(TurtleEvent.EventKind kind) {
      return kind == TurtleEvent.EventKind.USERCMD;
    }

    public void eventOccurred(TurtleEvent event) {
      _count++;
      _lastCommand = ((UserCommandEvent)event).queryCommand();
    }
  }

  @Test
  public void testSendCommand() {
    InputHistory history = new InputHistory(3);
    TestComponent component = new TestComponent();
    InputWindow window = InputWindow.createTestWindow(component, history);
    TestListener listener = new TestListener();
    EventBus.registerEventListener(listener);

    component._lastText = "Hello ";
    window.componentChanged();
    component._lastText = "Hello world!";
    window.componentChanged();
    window.specialKeyEvent(KeyEvent.VK_ENTER);
    assertTrue(listener._count == 1);
    assertTrue(listener._lastCommand.equals("Hello world!"));
    assertTrue(history.queryCurrent() == null);
    assertTrue(history.browseUp().equals("Hello world!"));
  }

  @Test
  public void testBrowseHistory() {
    InputHistory history = new InputHistory(3);
    TestComponent component = new TestComponent();
    InputWindow window = InputWindow.createTestWindow(component, history);
    component._lastText = "AAA";
    window.specialKeyEvent(KeyEvent.VK_ENTER);
    component._lastText = "BBB";
    window.specialKeyEvent(KeyEvent.VK_ENTER);
    component._lastText = "CCC";
    window.specialKeyEvent(KeyEvent.VK_ENTER);
    assertTrue(history.queryCurrent() == null);
    assertTrue(component._lastText.equals("CCC"));
    window.specialKeyEvent(KeyEvent.VK_UP);
    assertTrue(component._lastText.equals("BBB"));
    window.specialKeyEvent(KeyEvent.VK_UP);
    assertTrue(component._lastText.equals("AAA"));
    window.specialKeyEvent(KeyEvent.VK_DOWN);
    assertTrue(component._lastText.equals("BBB"));
  }

  @Test
  public void testTypingWhileBrowsing() {
    // create components
    InputHistory history = new InputHistory(5);
    TestComponent component = new TestComponent();
    InputWindow window = InputWindow.createTestWindow(component, history);
    // set up a basic history
    component._lastText = "AAA";
    window.specialKeyEvent(KeyEvent.VK_ENTER);
    component._lastText = "BBB";
    window.specialKeyEvent(KeyEvent.VK_ENTER);
    component._lastText = "CCC";
    window.specialKeyEvent(KeyEvent.VK_ENTER);
    // browse back to AAA
    window.specialKeyEvent(KeyEvent.VK_UP);
    window.specialKeyEvent(KeyEvent.VK_UP);
    // change the window, then change it back for good measure
    component._lastText = "AAAx";
    window.componentChanged();
    component._lastText = "AAA";
    window.componentChanged();
    // we should have stopped browsing, but not yet added anything
    assertTrue(history.queryCurrent() == null);
    assertTrue(history.browseUp().equals("CCC"));
    history.resetBrowsing();
    // after a return, the altered item should be added
    component._lastText = "AAAy";
    window.specialKeyEvent(KeyEvent.VK_ENTER);
    assertTrue(component._lastText.equals("AAAy"));
    assertTrue(history.queryCurrent() == null);
    assertTrue(history.browseUp().equals("AAAy"));
    assertTrue(history.browseUp().equals("CCC"));
    assertTrue(history.browseUp().equals("BBB"));
    assertTrue(history.browseUp().equals("AAA"));
  }

  /*
  @Test
  public void testResendOldCommand() {
    // create components
    InputHistory history = new InputHistory(10);
    TestComponent component = new TestComponent();
    InputWindow window = InputWindow.createTestWindow(component, history);
    // set up a basic history
    component._lastText = "AAA";
    window.specialKeyEvent(KeyEvent.VK_ENTER);
    component._lastText = "BBB";
    window.specialKeyEvent(KeyEvent.VK_ENTER);
    component._lastText = "CCC";
    window.specialKeyEvent(KeyEvent.VK_ENTER);
    // resend CCC; this shouldn't add anything to history
    window.specialKeyEvent(KeyEvent.VK_ENTER);
    window.specialKeyEvent(KeyEvent.VK_UP);
    assertTrue(component._lastText.equals("BBB"));
    window.specialKeyEvent(KeyEvent.VK_DOWN);
    assertTrue(component._lastText.equals("CCC"));
    window.specialKeyEvent(KeyEvent.VK_DOWN);
    assertTrue(component._lastText.equals(""));
    // browse back to AAA and resend it; this should add to history
    window.specialKeyEvent(KeyEvent.VK_UP);
    System.out.println("Now we have: " + component._lastText);
    window.specialKeyEvent(KeyEvent.VK_UP);
    window.specialKeyEvent(KeyEvent.VK_UP);
    window.specialKeyEvent(KeyEvent.VK_ENTER);
    assertTrue(component._lastText.equals("AAA"));
    window.specialKeyEvent(KeyEvent.VK_UP);
    assertTrue(component._lastText.equals("CCC"));
    window.specialKeyEvent(KeyEvent.VK_UP);
    assertTrue(component._lastText.equals("BBB"));
    window.specialKeyEvent(KeyEvent.VK_UP);
    assertTrue(component._lastText.equals("AAA"));
  }
  */

  @Test
  public void testSaveByBrowseDown() {
    // create components
    InputHistory history = new InputHistory(10);
    TestComponent component = new TestComponent();
    InputWindow window = InputWindow.createTestWindow(component, history);
    TestListener listener = new TestListener();
    EventBus.registerEventListener(listener);
    // set up a basic history
    component._lastText = "AAA";
    window.specialKeyEvent(KeyEvent.VK_ENTER);
    component._lastText = "BBB";
    window.specialKeyEvent(KeyEvent.VK_ENTER);
    component._lastText = "CCC";
    window.specialKeyEvent(KeyEvent.VK_ENTER);
    // start typing something
    component._lastText = "Hi!";
    window.componentChanged();
    // now press the down key: this should save the last typed data to history
    // without sending anything
    window.specialKeyEvent(KeyEvent.VK_DOWN);
    assertTrue(listener._count == 3);
    assertTrue(history.queryCurrent() == null);
    assertTrue(component._lastText.equals(""));
    window.specialKeyEvent(KeyEvent.VK_UP);
    assertTrue(component._lastText.equals("Hi!"));
    window.specialKeyEvent(KeyEvent.VK_UP);
    assertTrue(component._lastText.equals("CCC"));
    window.specialKeyEvent(KeyEvent.VK_UP);
    assertTrue(component._lastText.equals("BBB"));
    window.specialKeyEvent(KeyEvent.VK_UP);
    assertTrue(component._lastText.equals("AAA"));
  }
}

