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

import java.util.ArrayList;

/**
 * This class represents a string, along with layout for each of the characters.
 * The layout consists of the foreground colour, background colour and a number of attributes.
 */
public interface LayoutedText {
  /**
   * Splitting the string into parts based on the respective attributes, this returns the number of
   * parts.
   * Each part has a uniform style and is as long a successive piece as possible with that style.
   */
  public int numParts();

  /**
   * Splitting the string into parts based on the respective attributes, this returns the part with
   * the given index.
   * The index must be in the range 0 ... numParts()-1
   */
  public String getPart(int part);

  /**
   * Splitting the string into parts based on the respective attributes, this returns the style of
   * each part.
   * The index must be in the range 0 ... numParts()-1
   */
  public CharacterLayout getStyle(int part);

  /**
   * This returns the plain text underlying this structure; all attributes and colours are removed.
   */
  public String getFullString();

  /**
   * This method returns whether the current text is empty.
   * Equivalent to getFullString().equals("")
   */
  public boolean isEmpty();
}

