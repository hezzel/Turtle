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

import java.awt.EventQueue;
import turtle.interfaces.immutable.TurtleEvent;
import turtle.interfaces.EventListener;
import turtle.interfaces.ConnectionListener;
import turtle.EventBus;
import turtle.events.InformationEvent;
import turtle.events.MudTextEvent;
import turtle.events.UserCommandEvent;
import turtle.events.WarningEvent;
import turtle.connection.Connection;

/**
 * The Connection Handler manages connections to a remote server.
 * This is all done asynchronously, but the rest of the program does not need to consider that.
 */
public class ConnectionHandler implements EventListener, ConnectionListener {
  Connection _connection;

  public ConnectionHandler() {
    _connection = null;
  }

  public void createConnection(String host, int port) {
    if (_connection == null) {
      _connection = new Connection(host, port, this);
    }
    else {
      String warning = "Cannot create a new connection when you are already connected.";
      EventBus.eventOccurred(new WarningEvent(warning));
    }
  }

  public boolean queryInterestedIn(TurtleEvent.EventKind kind) {
    return kind == TurtleEvent.EventKind.USERCMD;
  }

  public void eventOccurred(TurtleEvent event) {
    String cmd = ((UserCommandEvent)event).queryCommand();
    if (_connection != null) _connection.send(cmd);
  }

  /**
   * The Connection runs in a separate thread, so all the connection functions do too; we cannot
   * just put them on the event bus, or we risk concurrency issues all over the places.
   * Thus, this function makes sure that the relevant events are send on the standard event queue
   * where the rest of the program runs.
   */
  private void sendEventOnQueue(TurtleEvent event) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        EventBus.eventOccurred(event);
      }
    });
  }
  
  /** Sends an information event with the given text. */
  private void sendInformation(String txt) {
    sendEventOnQueue(new InformationEvent(txt));
  }

  /** Sends a warning event with the given text. */
  private void sendWarning(String txt) {
    sendEventOnQueue(new WarningEvent(txt));
  }

  /** Sends a "mud text arrived" event with the given text. */
  private void sendMudText(String txt) {
    sendEventOnQueue(new MudTextEvent(txt));
  }

  /** Called when connecting failed or the connection is broken improperly. */
  public void connectionFailed(String error) {
    if (_connection != null) sendWarning("Connection closed: " + error);
    _connection = null;
  }

  /** Called when the connection is closed without errors. */
  public void connectionClosed(boolean remote) {
    if (remote) sendInformation("The remote server has closed the connection.");
    _connection = null;
  }

  /** Called when the connection has successfully been established. */
  public void connectionEstablished(String host, String address, int port) {
    sendInformation("Connecting to " + address.toString() + " on port " + port + "...");
  }

  /** Called when an IP address is found. */
  public void connectionFoundAddress(String host, String address, int port) {
    sendInformation("Connection established.");
  }

  /** Called when the connection has acquired text from the server. */
  public void connectionReceivedText(String text) {
    sendMudText(text);
  }

  /** Called when the connection has encountered an exception that did not cause a disconnect. */
  public void connectionErrorOccurred(String explanation) {
    sendWarning(explanation);
  }
}

