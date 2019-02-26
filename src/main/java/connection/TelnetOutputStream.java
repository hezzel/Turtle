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

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import turtle.interfaces.immutable.TelnetCode;

/**
 * The TelnetOutputStream is based on a given OutputStream, and writes both user commands and
 * telnet codes to it.
 */
public class TelnetOutputStream {
  private BufferedWriter _commandWriter;
  private BufferedOutputStream _telnetWriter;
  
  public TelnetOutputStream(OutputStream target) throws IOException {
    _commandWriter = new BufferedWriter(new OutputStreamWriter(target, "UTF-8"));
    _telnetWriter = new BufferedOutputStream(target);
  }

  private void sendSingleLine(String text) throws IOException {
    _commandWriter.write(text + "\n", 0, text.length() + 1);
  }

  private void sendSingleTelnet(TelnetCode code) throws IOException {
    int[] parts = code.queryCompleteCode();
    for (int j = 0; j < parts.length; j++) {
      _telnetWriter.write(parts[j]);
    }
  }

  /** Sends a single line over the output stream; a newline is added to the end. */
  public void sendCommand(String text) throws IOException {
    sendSingleLine(text);
    _commandWriter.flush();
  }

  /** Sends 0 or more lines over the output stream, adding newlines after each. */
  public void sendCommands(ArrayList<String> texts) throws IOException {
    for (int i = 0; i < texts.size(); i++) {
      sendSingleLine(texts.get(i));
    }
    if (texts.size() > 0) _commandWriter.flush();
  }

  /** Sends a single telnet code over the output stream. */
  public void sendTelnet(TelnetCode code) throws IOException {
    sendSingleTelnet(code);
    _telnetWriter.flush();
  }

  /** Sends 0 or more telnet codes over the output stream. */
  public void sendTelnetCodes(ArrayList<TelnetCode> codes) throws IOException {
    for (int i = 0; i < codes.size(); i++) {
      sendSingleTelnet(codes.get(i));
    }
    if (codes.size() > 0) _telnetWriter.flush();
  }

  public void close() throws IOException {
    _commandWriter.close();
    _telnetWriter.close();
  }
}

