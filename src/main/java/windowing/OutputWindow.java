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

import java.awt.Color;
import javax.swing.*;
import turtle.EventHandler;
import turtle.interfaces.TurtleEvent;
import turtle.interfaces.EventListener;
import turtle.events.EventUserCommand;

/**
 * This class represents the main window of Turtle, where text is printed to the user.
 */
public class OutputWindow {
  private JTextPane _textpane;
  private JScrollPane _scrollpane;

  public OutputWindow() {
    // set up the text field
    _textpane = new JTextPane();
    //_textpane.setBackground(Color.BLACK);
    _textpane.setEditable(false);
    // make it scrollable
    _scrollpane = new JScrollPane(_textpane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                  JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    EventHandler.registerEventListener(new EventListener() {
      public boolean queryInterestedIn(TurtleEvent.EventKind kind) {
        return kind == TurtleEvent.EventKind.USERCMD;
      }

      public void eventOccurred(TurtleEvent event) {
        String txt = ((EventUserCommand)event).queryCommand();
        _textpane.setText(txt);
      }
    });
  }

  public JComponent queryComponent() {
    return _scrollpane;
  }
}

