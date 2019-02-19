/*
 * This Java source file was generated by the Gradle 'init' task.
 */
import org.junit.Test;
import static org.junit.Assert.*;

import turtle.EventBus;
import turtle.interfaces.TurtleEvent;
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
