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

package turtle.interfaces.immutable;

/**
 * A TelnetCode represents a code that is received or sent over the telnet connection.
 * Codes have one of a few standard forms that should be handled in different ways following the
 * telnet protocol.
 */
public interface TelnetCode {
  /* The bytes that are part of a telnet requeest */
  public static final int SE   = 240;
  public static final int NOP  = 241;
  public static final int DAT  = 242;
  public static final int BRK  = 243;
  public static final int IP   = 244;
  public static final int AO   = 245;
  public static final int AYT  = 246;
  public static final int EC   = 247;
  public static final int EL   = 248;
  public static final int GA   = 249;
  public static final int SB   = 250;
  public static final int WILL = 251;
  public static final int WONT = 252;
  public static final int DO   = 253;
  public static final int DONT = 254;
  public static final int IAC  = 255;

  public String toString();
  
  /** For a command IAC <X> [...], this returns X. */
  public int queryCommand();

  /**
   * For a command IAC <X> <O>, where X is one of WILL, WONT, DO, DONT, this returns O.
   * For a command IAC SB <O> [...] SE, this also returns O.
   * For all other commands, it returns 0.
   */
  public int queryOption();

  /**
   * For a command IAC SB <O> [...] IAC SE or IAC SB <O> [...] SE, this returns [...].
   * For all other commands, null is returned.
   */
  public int[] querySubNegotiation();
}

