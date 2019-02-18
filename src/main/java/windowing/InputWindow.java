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
import turtle.EventHandler;
import turtle.events.EventUserCommand;

/**
 * This class represents the textbox at the bottom of Turtle, where users input text.
 */
public class InputWindow {
  JTextField _component;

  public InputWindow() {
    _component = new JTextField();
    _component.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "enterpressed");
    _component.getActionMap().put("enterpressed", new AbstractAction() {
        public void actionPerformed(ActionEvent tf) {
          enterPressed();
        }
      });
  }

  private void enterPressed() {
    String text = _component.getText();
    _component.setText("");
    EventHandler.eventOccurred(new EventUserCommand(text));
  }

  public JComponent queryComponent() {
    return _component;
  }
}

