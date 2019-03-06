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

package turtle.styles;

import java.awt.Color;
import turtle.interfaces.immutable.Colour;

/**
 * A DefaultColour is either the default front or the default back; it may also be brightened.
 */
public class DefaultColour implements Colour {
  private int _code;
    /* 0: back, 1: front, 2: bright back, 3: bright front */

  /** The palette is used to transform default colours into a normal Color object. */
  private static Color _palette[] = new Color[] {
    new Color(0,   0,   0  ),
    new Color(205, 205, 205),
    new Color(102, 102, 102),
    new Color(255, 255, 255),
  };

  /**
   * Creates a colour to represent:
   * - the default background colour if front = false (brightened if bright is set to true)
   * - the default foreground colour if front = true (same).
   */
  public DefaultColour(boolean front, boolean bright) {
    _code = (bright ? 2 : 0) + (front ? 1 : 0);
  }

  public boolean equals(Object other) {
    if (other instanceof DefaultColour) return _code == ((DefaultColour)other)._code;
    return false;
  }

  public Color toJavaColor() {
    return _palette[_code];
  }

  public String colourName() {
    String[] names = { "defaultback", "defaultfront", "brightdefaultback", "brightdefaultfront" };
    return names[_code];
  }

  public Colour brightenedColour() {
    return new DefaultColour(_code % 2 == 1, true);
  }

  public Colour unbrightenedColour() {
    return new DefaultColour(_code % 2 == 1, false);
  }
}

