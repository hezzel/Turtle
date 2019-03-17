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

/** The XTermColour class represents one of the 256 colours in the xterm256 terminal setup. */
public class XTermColour implements Colour {
  private int _index;
  private Color _colorob;

  public XTermColour(int number) {
    if (number < 0 || number >= 256) {
      throw new Error("XTermColour created with illegal index: " + number);
    }
    _index = number;
    if (number < 16) _colorob = (new AnsiColour(number)).toJavaColor();
    else if (number < 232) setupColourfulColor(number);
    else setupGreyscaleColor(number);
  }

  /** Creates an XTerm-colour for R/G/B in (0,...,5) */
  public XTermColour(int r, int g, int b) {
    if (r < 0 || g < 0 || b < 0) throw new Error("XTermColour created with negative r/g/b");
    if (r > 5 || g > 5 || b > 5) throw new Error("XTermColour created with too large r/g/b");
    _index = 16 + r * 36 + 6 * g + b;
    setupColourfulColor(_index);
  }

  private void setupColourfulColor(int number) {
    number -= 16;
    int b = number % 6;
    if (b != 0) b = 55 + 40 * b;
    number /= 6;
    int g = number % 6;
    if (g != 0) g = 55 + 40 * g;
    int r = number / 6;
    if (r != 0) r = 55 + 40 * r;
    _colorob = new Color(r,g,b);
  }

  private void setupGreyscaleColor(int number) {
    int k = 8 + 10 * (number - 232);
    _colorob = new Color(k,k,k);
  }

  public boolean equals(Object other) {
    if (other instanceof XTermColour) return _index == ((XTermColour)other)._index;
    return false;
  }

  public Color toJavaColor() {
    return _colorob;
  }

  public String colourName() {
    String ret = "x";
    if (_index < 10) ret += "00";
    else if (_index < 100) ret += "0";
    ret += _index;
    return ret;
  }

  public Colour brightenedColour() {
    if (_index < 8) return new XTermColour(_index + 8);
    return this;
  }

  public Colour unbrightenedColour() {
    if (_index >= 8 && _index < 16) return new XTermColour(_index - 8);
    return this;
  }

  public String toString() {
    return colourName();
  }
}

