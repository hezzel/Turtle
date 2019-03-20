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

package turtle.windowing;

import javax.swing.*;
import java.awt.event.*;
import java.awt.Font;
import turtle.interfaces.windowing.InputWindowComponent;
import turtle.interfaces.windowing.InputWindowEventListener;
import turtle.EventBus;
import turtle.events.UserCommandEvent;

/** This class is a wrapper for the textbox at the bottom of Turtle, where users input text. */
public class InputWindow implements InputWindowEventListener {
  JComponent _component;
  InputWindowComponent _iwc;
  InputHistory _history;

  public InputWindow() {
    InputWindowTextField x = new InputWindowTextField(this);
      // takes care of nitty-gritty event handling
    _component = x;
    _iwc = x;
    setupHistory();
  }

  private InputWindow(InputWindowComponent iwc, InputHistory history) {
    _component = null;
    _iwc = iwc;
    _history = history;
  }

  /**
   * Exclusively used for unit testing!
   * Note that this will create an InputWindow without explicit copmonent, so queryComponent()
   * will return null.
   */
  public static InputWindow createTestWindow(InputWindowComponent comp, InputHistory hist) {
    return new InputWindow(comp, hist);
  }
 
  public void setFont(Font font) {
    _component.setFont(font);
  }

  public JComponent queryComponent() {
    return _component;
  }

  /** Called by the InputWindowComponent when a relevant key event occurs. */
  public void specialKeyEvent(int number) {
    if (number == KeyEvent.VK_ENTER) enterPressed();
    if (number == KeyEvent.VK_UP) historyBrowse(1);
    if (number == KeyEvent.VK_DOWN) historyBrowse(-1);
  }

  /** Called by the InputWindowComponent when the text in the underlying textfield has changed. */
  public void componentChanged() {
    resetHistoryBrowsing();
  }

  private void enterPressed() {
    String text = _iwc.getText();
    _iwc.selectAll();
    EventBus.eventOccurred(new UserCommandEvent(text));
    _history.addHistoryItem(text);
  }

  private void setupHistory() {
    _history = new InputHistory(100);
  }

  private void resetHistoryBrowsing() {
    _history.resetBrowsing();
  }

  private void historyBrowse(int direction) {
    // save current result in input history and immediately move to that point
    // (inputhistory will make sure not to add it if it's just the last item)
    if (_history.queryCurrent() == null) {
      String text = _iwc.getText();
      if (!text.equals("")) {
        _history.addHistoryItem(text);
        _history.browseUp();
      }
    }

    String browseText;
    if (direction > 0) {
      browseText = _history.browseUp();
    }
    else {
      browseText = _history.browseDown();
      if (browseText == null) _iwc.changeText("");
    }

    if (browseText != null) _iwc.changeText(browseText);
  }
}

