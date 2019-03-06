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
 * The RGBAColour class represents a class given by red/green/blue values, along with an "alpha"
 * representing the transparency.  Such a Colour is not subject to (most) changes in settings,
 * and cannot be brightened or unbrightened.
 */
public class RGBAColour implements Colour {
  private Color _colorob;

  public RGBAColour(Color base) {
    _colorob = base;
  }

  public RGBAColour(int red, int green, int blue) {
    _colorob = new Color(red, green, blue);
  }

  public boolean equals(Object other) {
    if (other instanceof RGBAColour) return _colorob.equals(((RGBAColour)other)._colorob);
    return false;
  }

  public Color toJavaColor() {
    return _colorob;
  }

  private String hexify(int num) {
    String ret = Integer.toHexString(num);
    if (num < 16) ret = "0" + ret;
    return ret;
  }

  public String colourName() {
    return hexify(_colorob.getRed()) + hexify(_colorob.getGreen()) + hexify(_colorob.getBlue());
  }

  public Colour brightenedColour() {
    return this;
  }

  public Colour unbrightenedColour() {
    return this;
  }
}

