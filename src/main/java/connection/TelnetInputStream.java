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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import turtle.interfaces.immutable.TelnetCode;
import turtle.connection.telnet.*;

/**
 * A TelnetInputStream is based on a given InputStream, but separates out all the telnet commands.
 * Care is taken to give the results in order, so that even if the server sends a telnet command
 * halfway through a line, this will lead to three events: first a text arriving event for the
 * first half, then a telnet event, then a text event for the last half.
 */
public class TelnetInputStream {
  private InputStream _source;
  private byte[] _textBuffer;
  private byte[] _connectionBuffer;
  private int _textBufferSize;
  private int _textBufferStart;
  private int _connectionBufferSize;
  private int _connectionBufferStart;
  private InternalBufferStream _ibs;
  private InputStreamReader _reader;
  private ArrayList<Integer> _partialTelnetCode;
  private TelnetCode _availableTelnetCode;
  private String _availableText;
  private static final int BUFFERSIZE = 1000;

  /**
   * StreamStatus gives the states that the telnet stream may be in: the stream may not be ready to
   * be read (NONE), may have a text string available (TEXT), or a telnet code available (TELNET);
   * alternatively, the connection may have ended (EOF).
   */
  public enum StreamStatus { NONE, TEXT, TELNET, EOF };

  private class OutOfBufferException extends IOException { }

  /**
   * For internal use only: this buffer stream is used to turn the internally buffered text into a
   * stream that an InputStreamReader can use to create a char stream.
   * Doing this rather than simply converting the byte sequence to a string is necessary because
   * the buffered text is never guaranteed to be complete, so may well end in the middle of a
   * unicode char; the InputStreamReader can deal with that, and wait with outputting the relevant
   * char until more of the byte stream is added to the buffer.
   */
  private class InternalBufferStream extends InputStream {
    public int read() throws IOException {
      if (_textBufferStart >= _textBufferSize) throw new OutOfBufferException();
      int c = _textBuffer[_textBufferStart];
      if (c < 0) c += 256;
      _textBufferStart++;
      return c;
    }
  }

  public TelnetInputStream(InputStream source) throws IOException {
    _source = source;
    _textBufferSize = 0;
    _textBufferStart = 0;
    _connectionBufferSize = 0;
    _connectionBufferStart = 0;
    _ibs = new InternalBufferStream();
    _textBuffer = new byte[BUFFERSIZE];
    _connectionBuffer = new byte[BUFFERSIZE];
    _reader = new InputStreamReader(_ibs, "UTF-8");
    _availableText = null;
    _availableTelnetCode = null;
    _partialTelnetCode = new ArrayList<Integer>();
  }

  /**
   * If _connectionBuffer has no active content in it, this function will read from the source
   * stream to fill it up again.  If there was already active content, then true is returned
   * without any reading.  If something was read, also true is returned; if the connection has
   * been closed, then false is returned. The only alternative is if an IOException occurs (for
   * example because the read times out).
   */
  private boolean fillConnectionBuffer() throws IOException {
    // if some unparsed text is already available, don't bother reading from the source for now
    if (_connectionBufferStart < _connectionBufferSize) return true;

    // okay, we have to read new text instead
    _connectionBufferSize = _source.read(_connectionBuffer);
    if (_connectionBufferSize == -1) return false;
    else {
      _connectionBufferStart = 0;
      return true;
    }
  }

  /**
   * If the relevant information in _textBuffer is in bytes i..j, then this data is shifted to
   * bytes 0..j-i-1 instead.
   * Thus, the unread portion of textbuffer is shifted to the start of the buffer.
   * Note that if there is no unread part of _textBuffer, this means that the buffer will be marked
   * as empty.
   */ 
  private void shiftTextBuffer() {
    if (_textBufferStart >= _textBufferSize) {
      _textBufferStart = 0;
      _textBufferSize = 0;
    }
    if (_textBufferStart != 0) {
      for (int i = _textBufferStart; i < _textBufferSize; i++) {
        _textBuffer[i-_textBufferStart] = _textBuffer[i];
      }
      _textBufferSize -= _textBufferStart;
      _textBufferStart = 0;
    }
  }

  /**
   * This method moves text from _connectionBuffer to _textBuffer, until _connectionBuffer runs
   * out, _textBuffer is full, or an IAC is encountered in _connectionBuffer.
   * Note that this means that the text is removed from _connectionBuffer (by increasing
   * _connectionBufferStart).
   */
  private void moveNonTelnetFromConnectionToTextBuffer() {
    for (; _connectionBufferStart < _connectionBufferSize && _textBufferSize < BUFFERSIZE;
           _connectionBufferStart++, _textBufferSize++) {
      byte c = _connectionBuffer[_connectionBufferStart];
      if (c == -1) break; // IAC
      _textBuffer[_textBufferSize] = c;
    }
  }

  /**
   * This function parses _textBuffer into a string.
   * It is possible that _textBuffer does not end at a complete utf-8 codepoint.  In that case, the
   * string up to the last character is returned, and the incomplete character is left in the
   * buffer for later completing.
   */
  private StreamStatus readTextBufferToString() throws IOException {
    char[] charbuffer = new char[BUFFERSIZE];
    int len = _reader.read(charbuffer);
    if (len == -1) return StreamStatus.EOF;
    if (len == 0) return StreamStatus.NONE;
    _availableText = new String(charbuffer, 0, len);
    return StreamStatus.TEXT;
  }

  /**
   * This function reads as many bytes from the connection buffer into the text buffer as
   * possible (without breaking up telnet codes), and parses the result into a string,
   * stored as _availableText.
   * @return true if a String of length at least 1 was found.
   */
  private StreamStatus readTextFromConnectionBuffer() throws IOException {
    shiftTextBuffer();
    moveNonTelnetFromConnectionToTextBuffer();
    return readTextBufferToString();
  }

  /**
   * This function tests whether _partialTelnetCode actually represents a complete telnet code,
   * and if so, turns it into a TelnetCode class.  If not, null is returned instead.
   */
  private TelnetCode tryPartialToCompleteTelnetCode() {
    TelnetCode ret;
    ret = SingleTelnetCommand.readFromArrayList(_partialTelnetCode);
    if (ret == null) ret = SupportTelnetCommand.readFromArrayList(_partialTelnetCode);
    if (ret == null) ret = SubNegotiationTelnetCommand.readFromArrayList(_partialTelnetCode);
    return ret;
  }

  private StreamStatus readRemainingTelnetCode() {
    // read one character at a time, so we don't accidentally overshoot the telnet code
    while (_connectionBufferStart < _connectionBufferSize) {
      int b = _connectionBuffer[_connectionBufferStart];
      if (b < 0) b += 256;
      _partialTelnetCode.add(b);
      _connectionBufferStart++;

      TelnetCode code = tryPartialToCompleteTelnetCode();
      if (code != null) {
        _availableTelnetCode = code;
        _partialTelnetCode.clear();
        return StreamStatus.TELNET;
      }
    }
    return StreamStatus.NONE;
  }

  /**
   * This function reads from the source stream and parses the result in strings and telnet codes.
   * The return value tells you what the next thing to be read is.
   * It is allowed to call this function multiple times without reading the available text or
   * telnet code; in that case, the string or code that was ready for reading will be forgotten.
   */
  public StreamStatus probeAvailableContent() throws IOException {
    try { if (!fillConnectionBuffer()) return StreamStatus.EOF; }
    catch (SocketTimeoutException e) { return StreamStatus.NONE; }
    if (_connectionBufferStart >= _connectionBufferSize) return StreamStatus.NONE;

    if (_partialTelnetCode.size() > 0 || _connectionBuffer[_connectionBufferStart] == -1) {
      return readRemainingTelnetCode();
    }
    else {
      return readTextFromConnectionBuffer();
    }
  }

  public String readString() {
    return _availableText;
  }

  public TelnetCode readTelnetCode() {
    return _availableTelnetCode;
  }

  public void close() throws IOException {
    _source.close();
    _reader.close();
  }
}

