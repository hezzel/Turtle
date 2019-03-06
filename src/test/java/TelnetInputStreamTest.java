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

import java.io.InputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import turtle.interfaces.immutable.TelnetCode;
import turtle.connection.TelnetInputStream;

public class TelnetInputStreamTest {
  private class VariableStream extends InputStream {
    private byte[] bytes;
    private int counter;
    private boolean endAfter = false;

    private int toPositive(byte b) {
      if (b < 0) return b + 256;
      else return b;
    }

    public int read() throws IOException {
      if (counter < bytes.length) return toPositive(bytes[counter++]);
      else if (endAfter && counter == bytes.length) { return -1; }
      else throw new SocketTimeoutException();
    }
  }

  @Test
  public void testTelnetInputStreamBasicText() throws IOException {
    VariableStream vstream = new VariableStream();
    vstream.bytes = "Hello world!".getBytes(Charset.forName("UTF-8"));
    vstream.counter = 0;
    TelnetInputStream tstream = new TelnetInputStream(vstream);
    assertTrue(tstream.probeAvailableContent() == TelnetInputStream.StreamStatus.TEXT);
    assertTrue(tstream.readString().equals("Hello world!"));
    assertTrue(tstream.probeAvailableContent() == TelnetInputStream.StreamStatus.NONE);
    assertTrue(tstream.readString().equals("Hello world!"));
    vstream.bytes = "∀x ∈ Φ [x ≠ 3]".getBytes(Charset.forName("UTF-8"));
    vstream.counter = 0;
    assertTrue(tstream.probeAvailableContent() == TelnetInputStream.StreamStatus.TEXT);
    assertTrue(tstream.readString().equals("∀x ∈ Φ [x ≠ 3]"));
  }

  @Test
  public void testTelnetInputStreamPartialUTF() throws IOException {
    // first the stream will receive two complete characters, and part of a third
    VariableStream vstream = new VariableStream();
    byte[] vbytes = "é∃∀".getBytes(Charset.forName("UTF-8"));
    vstream.bytes = new byte[vbytes.length-1];
    for (int i = 0; i < vstream.bytes.length; i++) vstream.bytes[i] = vbytes[i];
    vstream.counter = 0;
    TelnetInputStream tstream = new TelnetInputStream(vstream);
    assertTrue(tstream.probeAvailableContent() == TelnetInputStream.StreamStatus.TEXT);
    assertTrue(tstream.readString().equals("é∃"));
    assertTrue(tstream.probeAvailableContent() == TelnetInputStream.StreamStatus.NONE);
    // now it receives the remainder and three further characters
    byte[] wbytes = "∅ λ".getBytes(Charset.forName("UTF-8"));
    vstream.bytes = new byte[wbytes.length+1];
    vstream.bytes[0] = vbytes[vbytes.length-1];
    vstream.counter = 0;
    for (int i = 0; i < wbytes.length; i++) vstream.bytes[i+1] = wbytes[i];
    assertTrue(tstream.probeAvailableContent() == TelnetInputStream.StreamStatus.TEXT);
    assertTrue(tstream.readString().equals("∀∅ λ"));
  }

  @Test
  public void testTelnetInputStreamTelnetCode() throws IOException {
    VariableStream vstream = new VariableStream();
    vstream.bytes = new byte[9];
    vstream.bytes[0] = (byte)TelnetCode.IAC;
    vstream.bytes[1] = (byte)TelnetCode.WILL;
    vstream.bytes[2] = 87;
    vstream.bytes[3] = (byte)TelnetCode.IAC;
    vstream.bytes[4] = (byte)TelnetCode.SB;
    vstream.bytes[5] = 87;
    vstream.bytes[6] = 0;
    vstream.bytes[7] = (byte)TelnetCode.IAC;
    vstream.bytes[8] = (byte)TelnetCode.SE;
    vstream.counter = 0;
    TelnetInputStream tstream = new TelnetInputStream(vstream);
    assertTrue(tstream.probeAvailableContent() == TelnetInputStream.StreamStatus.TELNET);
    TelnetCode code = tstream.readTelnetCode();
    assertTrue(code.queryCommand() == TelnetCode.WILL);
    assertTrue(code.queryOption() == 87);
    assertTrue(tstream.probeAvailableContent() == TelnetInputStream.StreamStatus.TELNET);
    code = tstream.readTelnetCode();
    assertTrue(code.queryCommand() == TelnetCode.SB);
    assertTrue(code.queryOption() == 87);
    int[] subneg = code.querySubNegotiation();
    assertTrue(subneg != null);
    assertTrue(subneg.length == 1);
    assertTrue(subneg[0] == 0);
  }

  @Test
  public void testBrokenUpTelnetCode() throws IOException {
    VariableStream vstream = new VariableStream();
    vstream.bytes = new byte[3];
    vstream.bytes[0] = (byte)TelnetCode.IAC;
    vstream.bytes[1] = (byte)TelnetCode.SB;
    vstream.bytes[2] = 87;
    vstream.counter = 0;
    TelnetInputStream tstream = new TelnetInputStream(vstream);
    assertTrue(tstream.probeAvailableContent() == TelnetInputStream.StreamStatus.NONE);
    vstream.bytes = new byte[4];
    vstream.bytes[0] = 100;
    vstream.bytes[1] = (byte)TelnetCode.IAC;
    vstream.bytes[2] = (byte)TelnetCode.SE;
    vstream.bytes[3] = 98;
    vstream.counter = 0;
    assertTrue(tstream.probeAvailableContent() == TelnetInputStream.StreamStatus.TELNET);
    TelnetCode code = tstream.readTelnetCode();
    assertTrue(code.queryCommand() == TelnetCode.SB);
    assertTrue(code.queryOption() == 87);
    int[] subneg = code.querySubNegotiation();
    assertTrue(subneg.length == 1);
  }

  @Test
  public void testTelnetBetweenText() throws IOException {
    VariableStream vstream = new VariableStream();
    vstream.bytes = new byte[3];
    byte[] abytes = "é∃∀".getBytes(Charset.forName("UTF-8"));
    byte[] bbytes = "∅ λ".getBytes(Charset.forName("UTF-8"));

    vstream.bytes = new byte[abytes.length+bbytes.length+3];
    int v = 0;
    for (int i = 0; i < abytes.length-1; i++) vstream.bytes[v++] = abytes[i];
    vstream.bytes[v++] = (byte)TelnetCode.IAC;
    vstream.bytes[v++] = (byte)TelnetCode.WILL;
    vstream.bytes[v++] = 87;
    vstream.bytes[v++] = abytes[abytes.length-1];
    for (int i = 0; i < bbytes.length; i++) vstream.bytes[v++] = bbytes[i];
    vstream.counter = 0;

    TelnetInputStream tstream = new TelnetInputStream(vstream);
    assertTrue(tstream.probeAvailableContent() == TelnetInputStream.StreamStatus.TEXT);
    assertTrue(tstream.readString().equals("é∃"));
    assertTrue(tstream.probeAvailableContent() == TelnetInputStream.StreamStatus.TELNET);
    assertTrue(tstream.readTelnetCode().queryCommand() == TelnetCode.WILL);
    assertTrue(tstream.probeAvailableContent() == TelnetInputStream.StreamStatus.TEXT);
    assertTrue(tstream.readString().equals("∀∅ λ"));
  }
}

