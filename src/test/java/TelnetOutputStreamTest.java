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

import java.util.ArrayList;
import java.io.OutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import turtle.interfaces.immutable.TelnetCode;
import turtle.connection.TelnetOutputStream;
import turtle.connection.telnet.*;

public class TelnetOutputStreamTest {
  private class VariableStream extends OutputStream {
    private ArrayList<Integer> bytes;

    public VariableStream() {
      bytes = new ArrayList<Integer>();
    }

    public void write(int b) {
      bytes.add(b);
    }
  }

  private String makeString(ArrayList<Integer> list, int start, int end) {
    byte[] ret = new byte[end - start];
    for (int i = start; i < end; i++) ret[i-start] = list.get(i).byteValue();
    return new String(ret, Charset.forName("UTF-8"));
  }

  @Test
  public void testTelnetOutputStreamBasicText() throws IOException {
    VariableStream vstream = new VariableStream();
    TelnetOutputStream tstream = new TelnetOutputStream(vstream);
    tstream.sendCommand("∀x[hello");
    tstream.sendCommand("wórld(x)]!");
    ArrayList<String> xs = new ArrayList<String>();
    xs.add(" ");
    xs.add("λ-ε");
    tstream.sendCommands(xs);
    String written = makeString(vstream.bytes, 0, vstream.bytes.size());
    assertTrue(written.equals("∀x[hello\nwórld(x)]!\n \nλ-ε\n"));
  }

  @Test
  public void testTelnetOutputStreamBasicTelnet() throws IOException {
    VariableStream vstream = new VariableStream();
    TelnetOutputStream tstream = new TelnetOutputStream(vstream);
    tstream.sendTelnet(new SingleTelnetCommand(TelnetCode.NOP));
    ArrayList<TelnetCode> list = new ArrayList<TelnetCode>();
    list.add(new SupportTelnetCommand(TelnetCode.DO, 78));
    list.add(new SingleTelnetCommand(TelnetCode.GA));
    tstream.sendTelnetCodes(list);
    tstream.sendTelnet(new SubNegotiationTelnetCommand(78, new ArrayList<Integer>()));
    assertTrue(vstream.bytes.size() == 12);
    assertTrue(vstream.bytes.get(0) == (byte)TelnetCode.IAC);
    assertTrue(vstream.bytes.get(1) == (byte)TelnetCode.NOP);
    assertTrue(vstream.bytes.get(8) == (byte)TelnetCode.SB);
  }

  @Test
  public void testTelnetOutputStreamMixedMessages() throws IOException {
    VariableStream vstream = new VariableStream();
    TelnetOutputStream tstream = new TelnetOutputStream(vstream);
    tstream.sendTelnet(new SingleTelnetCommand(TelnetCode.NOP));
    tstream.sendCommand("∃∀∅");
    tstream.sendTelnet(new SupportTelnetCommand(TelnetCode.WILL, 12));
    int k = vstream.bytes.size()-3;
    assertTrue(makeString(vstream.bytes, 2, k).equals("∃∀∅\n"));
  }
}

