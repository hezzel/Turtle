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

package turtle.interfaces.windowing;

import javax.swing.KeyStroke;

public interface InputWindowComponent {
  /** Returns the text that is currently in the underlying component. */
  String getText();

  /** Changes the text in the component without generating an event. */
  void changeText(String text);

  /** Selects all text in the component. */
  void selectAll();

  /**
   * Marks the given keystroke as one that any InputWindowEventListeners listening to the current
   * component may wish to be informed of.
   */
  void registerSignificantKeystroke(KeyStroke k);

  /**
   * Marks the given keystroke as one that any InputWindowEventListeners listening to the current
   * component do not need to be informed of.
   */
  void deregisterSignificantKeystroke(KeyStroke k);
}

