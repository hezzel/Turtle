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

package turtle;

import java.util.ArrayList;
import turtle.interfaces.TurtleEvent;
import turtle.interfaces.EventListener;

/**
 * The Event Bus is told about all events, and passes them on to all listening objects.
 * The Event Bus itself does not act on any event, and does not consider which listeners it
 * passes the information on to.
 */
public class EventBus {
  private static ArrayList<EventListener> _listeners = new ArrayList<EventListener>();

  public static void eventOccurred(TurtleEvent event) {
    TurtleEvent.EventKind kind = event.queryEventKind();
    for (int i = 0; i < _listeners.size(); i++) {
      if (_listeners.get(i).queryInterestedIn(kind)) {
        _listeners.get(i).eventOccurred(event);
      }
    }
  }

  /**
   * Register a new event listeners.
   * Listeners that are already registered are ignored; they will not be notified twice on the
   * same event.  Order of registration should not be considered indicative of calling order when
   * an event occurs.
   */
  public static void registerEventListener(EventListener el) {
    if (!_listeners.contains(el)) _listeners.add(el);
  }

  public static void removeEventListener(EventListener el) {
    _listeners.remove(el);
  }
}

