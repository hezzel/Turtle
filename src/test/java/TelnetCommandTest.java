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

import turtle.interfaces.immutable.TelnetCode;
import turtle.connection.telnet.*;

public class TelnetCommandTest {
  @Test
  public void testSingleTelnetCommandConstruction() {
    // we want to know that _any_ telnet command is recognised as a single command other than those
    // that are the start of other, handled commands; this guarantees that we cannot get stuck in
    // parsing an unknown telnet code
    TelnetCode code;
    code = new SingleTelnetCommand(139);
    code = new SingleTelnetCommand(255);
    code = new SingleTelnetCommand(-1);
    code = new SingleTelnetCommand(8);
  }

  @Test
  public void testSingleTelnetCommandReadSuccess() {
    // read from input
    ArrayList<Integer> list = new ArrayList<Integer>();
    list.add(TelnetCode.IAC);
    list.add(29);
    TelnetCode code = SingleTelnetCommand.readFromArrayList(list);
    // test that it is what we expect
    assertTrue(code != null);
    assertTrue(code.queryCommand() == 29);
    assertTrue(code.queryOption() == -1);
    assertTrue(code.querySubNegotiation() == null);
  }

  @Test
  public void testSingleTelnetCommandReadFailureTooShort() {
    // read from input
    ArrayList<Integer> list = new ArrayList<Integer>();
    list.add(TelnetCode.IAC);
    TelnetCode code = SingleTelnetCommand.readFromArrayList(list);
    assertTrue(code == null);
  }

  @Test(expected = java.lang.Error.class)
  public void testSingleTelnetCommandReadFailureTooLong() {
    // read from input
    ArrayList<Integer> list = new ArrayList<Integer>();
    list.add(TelnetCode.IAC);
    list.add(TelnetCode.GA);
    list.add(TelnetCode.SE);
    TelnetCode code = SingleTelnetCommand.readFromArrayList(list);
  }

  @Test
  public void testSingleTelnetCommandReadFailureWrongCommand() {
    // read from input
    ArrayList<Integer> list = new ArrayList<Integer>();
    list.add(TelnetCode.IAC);
    list.add(TelnetCode.WILL);
    TelnetCode code = SingleTelnetCommand.readFromArrayList(list);
    assertTrue(code == null);
  }

  @Test
  public void testSingleTelnetCommandComplete() {
    TelnetCode code = new SingleTelnetCommand(TelnetCode.GA);
    int[] parts = code.queryCompleteCode();
    assertTrue(parts.length == 2);
    assertTrue(parts[0] == TelnetCode.IAC);
    assertTrue(parts[1] == TelnetCode.GA);
  }

  @Test(expected = java.lang.Error.class)
  public void SupportTelnetCommandConstructionFailure() {
    TelnetCode code = new SupportTelnetCommand(TelnetCode.NOP, 89);
  }

  @Test
  public void SupportTelnetCommandReadSuccess() {
    ArrayList<Integer> list = new ArrayList<Integer>();
    list.add(TelnetCode.IAC);
    list.add(TelnetCode.DONT);
    list.add(89);
    TelnetCode code = SupportTelnetCommand.readFromArrayList(list);
    assertTrue(code != null);
  }

  @Test
  public void SupportTelnetCommandReadFailureTooShort() {
    ArrayList<Integer> list = new ArrayList<Integer>();
    list.add(TelnetCode.IAC);
    list.add(TelnetCode.DO);
    TelnetCode code = SupportTelnetCommand.readFromArrayList(list);
    assertTrue(code == null);
  }

  @Test(expected = java.lang.Error.class)
  public void SupportTelnetCommandFailureTooLong() {
    ArrayList<Integer> list = new ArrayList<Integer>();
    list.add(TelnetCode.IAC);
    list.add(TelnetCode.WONT);
    list.add(89);
    list.add(98);
    TelnetCode code = SupportTelnetCommand.readFromArrayList(list);
    assertTrue(code == null);
  }

  @Test
  public void SupportTelnetCommandReadFailureWrongCommand() {
    ArrayList<Integer> list = new ArrayList<Integer>();
    list.add(TelnetCode.IAC);
    list.add(TelnetCode.GA);
    list.add(89);
    TelnetCode code = SupportTelnetCommand.readFromArrayList(list);
    assertTrue(code == null);
  }

  @Test
  public void testSupportTelnetCommandComplete() {
    TelnetCode code = new SupportTelnetCommand(TelnetCode.WILL, 88);
    int[] parts = code.queryCompleteCode();
    assertTrue(parts.length == 3);
    assertTrue(parts[0] == TelnetCode.IAC);
    assertTrue(parts[1] == TelnetCode.WILL);
    assertTrue(parts[2] == 88);
  }

  @Test
  public void SubNegotiationTelnetCommandConstruction() {
    ArrayList<Integer> list = new ArrayList<Integer>();
    list.add(0);
    list.add(127);
    list.add(19);
    TelnetCode code = new SubNegotiationTelnetCommand(17, list);
    // was the data copied faithfully?
    int[] arr = code.querySubNegotiation();
    assertTrue(arr.length == 3);
    assertTrue(arr[0] == 0);
    assertTrue(arr[1] == 127);
    assertTrue(arr[2] == 19);
  }

  private ArrayList<Integer> constructGoodList(boolean includeIAC) {
    ArrayList<Integer> list = new ArrayList<Integer>();
    list.add(TelnetCode.IAC);
    list.add(TelnetCode.SB);
    list.add(17);
    list.add(97);
    list.add(200);
    list.add(4);
    list.add(77);
    if (includeIAC) list.add(TelnetCode.IAC);
    list.add(TelnetCode.SE);
    return list;
  }

  @Test
  public void SubNegotiationTelnetCommandReadSuccess() {
    TelnetCode code = SubNegotiationTelnetCommand.readFromArrayList(constructGoodList(true));
    assertTrue(code.queryCommand() == TelnetCode.SB);
    assertTrue(code.queryOption() == 17);
    int[] args = code.querySubNegotiation();
    assertTrue(args.length == 4);
    assertTrue(args[2] == 4);
  }

  @Test
  public void SubNegotiationTelnetCommandReadTolerant() {
    TelnetCode code = SubNegotiationTelnetCommand.readFromArrayList(constructGoodList(false));
    assertTrue(code.queryCommand() == TelnetCode.SB);
    assertTrue(code.queryOption() == 17);
    int[] args = code.querySubNegotiation();
    assertTrue(args.length == 4);
    assertTrue(args[3] == 77);
  }

  @Test
  public void testSubNegotiationTelnetCommandComplete() {
    ArrayList<Integer> data = new ArrayList<Integer>();
    data.add(1);
    data.add(12);
    TelnetCode code = new SubNegotiationTelnetCommand(100, data);
    int[] parts = code.queryCompleteCode();
    assertTrue(parts.length == 7);
    assertTrue(parts[0] == TelnetCode.IAC);
    assertTrue(parts[1] == TelnetCode.SB);
    assertTrue(parts[2] == 100);
    assertTrue(parts[3] == 1);
    assertTrue(parts[4] == 12);
    assertTrue(parts[5] == TelnetCode.IAC);
    assertTrue(parts[6] == TelnetCode.SE);
  }
}

