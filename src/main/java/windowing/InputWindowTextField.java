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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.*;
import turtle.interfaces.windowing.InputWindowComponent;
import turtle.interfaces.windowing.InputWindowEventListener;

/**
 * This class represents the textbox at the bottom of Turtle, where users input text.
 * Aside from being a textfield, the class has little functionality; all relevant events are
 * passed on to the listening object.
 */
public class InputWindowTextField extends JTextField implements InputWindowComponent {
  private InputWindowEventListener _listener;
  private boolean _allowChangeEvents;

  public InputWindowTextField(InputWindowEventListener l) {
    super();
    _listener = l;
    _allowChangeEvents = true;

    setupDocumentListener();
    setupKeyMappings();
  }

  public void changeText(String text) {
    _allowChangeEvents = false;
    setText(text);
    _allowChangeEvents = true;
  }

  public void selectAll() {
    select(0, getText().length());
  }

  private void reportChange() {
    if (_allowChangeEvents) _listener.componentChanged();
  }

  private void setupDocumentListener() {
    getDocument().addDocumentListener(new DocumentListener() {
        public void changedUpdate(DocumentEvent e) { reportChange(); }
        public void removeUpdate(DocumentEvent e) { reportChange(); }
        public void insertUpdate(DocumentEvent e) { reportChange(); }
      });
  }

  private class KeyAction extends AbstractAction {
    int number;
    public KeyAction(int num) { number = num; }
    public void actionPerformed(ActionEvent tf) {
      _listener.specialKeyEvent(number);
    }
  }

  private void setupKey(String stroke, int code) {
    getInputMap().put(KeyStroke.getKeyStroke(stroke), "action " + code);
    getActionMap().put("action " + code, new KeyAction(code));
  }
 
  private void setupKeyMappings() {
    setupKey("ENTER", KeyEvent.VK_ENTER);
    setupKey("UP", KeyEvent.VK_UP);
    setupKey("KP_UP", KeyEvent.VK_UP);
    setupKey("DOWN", KeyEvent.VK_DOWN);
    setupKey("KP_DOWN", KeyEvent.VK_DOWN);
  }
}

