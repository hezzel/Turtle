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

package turtle.connection;

import java.awt.EventQueue;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import turtle.interfaces.ConnectionListener;

public class Connection extends Thread {
  private String _host;
  private int _port;
  private Socket _socket;
  private TelnetStream _reader;
  private BufferedWriter _writer;
  private boolean _ended;
  private boolean _locked;
  private boolean _userClose;
  private ArrayList<String> _queued;
  private ConnectionListener _listener;

  /** Default constructor; sets up the class and immediately opens the connection. */
  public Connection(String host, int port, ConnectionListener listener) {
    _host = host;
    _port = port;
    _socket = null;
    _ended = false;
    _locked = false;
    _userClose = false;
    _queued = new ArrayList<String>();
    _listener = listener;
    start();
  }

  /**
   * This holds the main functionality to connect, starting the connection, and once established,
   * listening on the connection until either side closes it.
   * Do not call manually! It is public only because it needs to be for the threading code.
   */
  public void run() {
    _writer = null;
    _reader = null;

    verifyConnectionData();
    if (!_ended) createConnection();
    while (!_ended) {
      sendQueuedCommands();
      receiveMudText();
    }
    closeConnection();
  }

  private void verifyConnectionData() {
    if (_host == null || _host.equals("")) {
      _listener.connectionFailed("Asked to connect to an empty host.");
      _ended = true;
    }

    else if (_port <= 0 || _port > 65535) {
      _listener.connectionFailed("Invalid port: should be between 1 and 65535 " +
                                 "(given: " + _port + ")");
      _ended = true;
    }
  }

  private InetAddress lookupAddress() {
    InetAddress address;
    try { address = InetAddress.getByName(_host); }
    catch (UnknownHostException e) { address = null; }
    if (address == null) {
      _listener.connectionFailed("Unknown host: " + _host);
      _ended = true;
    }
    else _listener.connectionFoundAddress(_host, address.toString(), _port);
    return address;
  }

  private void connectToAddress(InetAddress address) {
    try {
      _socket = new Socket();
      _socket.connect(new InetSocketAddress(address, _port), 20000);
      _reader = new TelnetStream(_socket.getInputStream());
      _writer = new BufferedWriter(new OutputStreamWriter(_socket.getOutputStream(), "UTF-8"));
    }
    catch (IOException e) {
      _listener.connectionFailed("Could not connect to IP: " + e.getMessage());
      _ended = true;
    }
    if (!_ended)_listener.connectionEstablished(_host, address.toString(), _port);
  }

  private void setSocketTimeout() {
    try { _socket.setSoTimeout(250); }
    catch (SocketException e) {
      _listener.connectionFailed("Could not set socket timeout: " + e.getMessage());
      _ended = true;
    }
  }

  private void createConnection() {
    InetAddress address = lookupAddress();
    if (!_ended) connectToAddress(address);
    if (!_ended) setSocketTimeout();
  }
  
  private void sendQueuedCommands() {
    lock();
    try {
      for (int i = 0; i < _queued.size(); i++) {
        String text = _queued.get(i);
        _writer.write(text + "\n", 0, text.length() + 1); 
      }
      if (_queued.size() > 0) _writer.flush();
    }   
    catch (IOException e) { } 
    _queued.clear();
    unlock();
  }

  private void receiveMudText() {
    char[] buffer = new char[1000];
    try {
      TelnetStream.StreamStatus status =_reader.probeAvailableContent();
      if (status == TelnetStream.StreamStatus.TEXT) {
        String content = _reader.readString();
        _listener.connectionReceivedText(content);
      }
      else if (status == TelnetStream.StreamStatus.EOF) {
        _listener.connectionClosed(true);
        _ended = true;
      }
    } catch (IOException e) {
      _listener.connectionErrorOccurred("IO Exception in connection (" + e.getClass() + "): " +
                                        e.getMessage());
    }
  }

  private void closeConnection() {
    try {
      if (_writer != null) { _writer.close(); _writer = null; }
      if (_socket != null) { _socket.close(); _socket = null; }
    }
    catch (IOException e) { }
    if (_userClose) _listener.connectionClosed(false);
  }

  /**
   * Lock the variable for the send queue.
   * This is needed because the send functionality is called from the event queue, while the actual
   * sending happens in the Connection thread.
   */
  private synchronized void lock() {
    while (_locked) {
      try { wait(); }
      catch (InterruptedException e) {}
    }
    _locked = true;
  }

  /** Unlock the variable for the send queue. */
  private synchronized void unlock() {
    _locked = false;
    notify();
  }

  /**
   * Call this from other thread to send the given text over the current connection.  If the
   * connection is not open yet, it will be sent once the connection is established (or never, if
   * that doesn't happen).
   */
  public void send(String text) {
    lock();
    _queued.add(text);
    unlock();
  }

  /**
   * Call this from another thread to close the connection.
   * It may take some time (say, half a second) before this actually happens.  If no connection
   * is yet established, the thread won't die until it has, or until it has realised that
   * establishing a connection 's not going to happen.
   */
  public void disconnect() {
    _ended = true;
    _userClose = true;
  }
}
