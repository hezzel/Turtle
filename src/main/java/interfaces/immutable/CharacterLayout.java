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

import javax.swing.JTextPane;
import javax.swing.text.Style;

/**
 * This class represents the collection of layout properties which a character (in a LayoutedText)
 * can have.
 * This includes things like foreground colour, background colour, bold and italic properties.
 */
public interface CharacterLayout {
  Colour getFront();
  Colour getBack();
  boolean queryBold();
  boolean queryItalic();
  boolean queryUnderline();
  boolean queryBlink();
  boolean queryReverse();
  boolean queryDim();
  boolean queryInvisible();
  boolean queryStrikethrough();

  /**
   * Returns a (java-internal) Style object representing this layout.
   * The given JTextPane should be the text pane in which the style object
   * will be used.
   */
  public Style getStyle(JTextPane ref);
}

