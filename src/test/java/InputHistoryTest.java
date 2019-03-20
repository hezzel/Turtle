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

import turtle.windowing.InputHistory;

public class InputHistoryTest {
  @Test
  public void testBasics() {
    InputHistory history = new InputHistory(5);
    history.addHistoryItem("Bing");
    history.addHistoryItem("Bang");
    history.addHistoryItem("Bong");
    assertTrue(history.queryCurrent() == null);
    assertTrue(history.browseUp().equals("Bong"));
    assertTrue(history.queryCurrent().equals("Bong"));
    assertTrue(history.browseUp().equals("Bang"));
    assertTrue(history.queryCurrent().equals("Bang"));
    assertTrue(history.browseUp().equals("Bing"));
    assertTrue(history.queryCurrent().equals("Bing"));
    assertTrue(history.browseUp().equals(""));
    assertTrue(history.queryCurrent().equals(""));
  }

  @Test
  public void testScrollOff() {
    InputHistory history = new InputHistory(3);
    history.addHistoryItem("Bing");
    history.addHistoryItem("Bang");
    history.addHistoryItem("Bong");
    history.addHistoryItem("Beng");
    assertTrue(history.queryCurrent() == null);
    assertTrue(history.browseUp().equals("Beng"));
    assertTrue(history.browseUp().equals("Bong"));
    assertTrue(history.browseUp().equals("Bang"));
    assertTrue(history.browseUp() == null);
    assertTrue(history.browseUp() == null);
    assertTrue(history.queryCurrent().equals("Bang"));
    assertTrue(history.browseDown().equals("Bong"));
  }

  @Test
  public void testRepetition() {
    InputHistory history = new InputHistory(4);
    history.addHistoryItem("Bing");
    history.addHistoryItem("Bing");
    history.addHistoryItem("Bang");
    history.addHistoryItem("");
    history.addHistoryItem("Bang");
    history.addHistoryItem("Bong");
    assertTrue(history.queryCurrent() == null);
    assertTrue(history.browseUp().equals("Bong"));
    assertTrue(history.browseUp().equals("Bang"));
    assertTrue(history.browseUp().equals("Bing"));
    assertTrue(history.browseUp().equals(""));
  }

  @Test
  public void testAddingWhileBrowsing() {
    InputHistory history = new InputHistory(3);
    history.addHistoryItem("Bing");
    history.browseUp();
    history.addHistoryItem("Bang");
    assertTrue(history.queryCurrent() == null);
    assertTrue(history.browseUp().equals("Bang"));
    assertTrue(history.browseUp().equals("Bing"));
  }

  @Test
  public void testBrowseFullyDown() {
    InputHistory history = new InputHistory(4);
    history.addHistoryItem("Bing");
    history.addHistoryItem("Bang");
    history.addHistoryItem("Bong");
    assertTrue(history.queryCurrent() == null);
    assertTrue(history.browseUp().equals("Bong"));
    assertTrue(history.browseUp().equals("Bang"));
    assertTrue(history.browseDown().equals("Bong"));
    assertTrue(history.browseDown() == null);
    assertTrue(history.browseUp().equals("Bong"));
  }
}

