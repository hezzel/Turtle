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

import java.awt.Color;

/**
 * The Colour class represents, unsurprisingly, a colour.
 * This could be a colour given by a code (whose exact RGB representation may be decided by user
 * settings or some well-chosen defaults) or just an RGB value.
 */
public interface Colour {
  /** Return a java.awt.Color representation of the Colour object. */
  Color toJavaColor();

  /** Return a unique string representation for the Colour object. */
  String colourName();

  /**
   * Some kinds of Colours change when mixed with the STANDOUT attribute; others remain unaltered.
   * This function returns the result of adding STANDOUT.
   */
  Colour brightenedColour();

  /**
   * Some kinds of Colours change when mixed with the STANDOUT attribute; others remain unaltered.
   * This function returns the result of removing STANDOUT.
   */
  Colour unbrightenedColour();
}

