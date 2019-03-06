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

/** An AnsiColour is one of the 8 basic colours, or one of the 8 brightened basic colours. */
public class AnsiColour implements Colour {
  public static final int COL_BLACK      = 0;
  public static final int COL_RED        = 1;
  public static final int COL_GREEN      = 2;
  public static final int COL_YELLOW     = 3;
  public static final int COL_BLUE       = 4;
  public static final int COL_MAGENTA    = 5;
  public static final int COL_CYAN       = 6;
  public static final int COL_WHITE      = 7;

  private int _code;

  /** The palette is used to transform the colour codes into a normal Color object. */
  private static Color _palette[] = new Color[] {
    new Color(0,   0,   0  ),
    new Color(192, 0,   0  ),
    new Color(0,   192, 0  ),
    new Color(192, 192, 0  ),
    new Color(0,   0,   192),
    new Color(192, 0,   192),
    new Color(0,   192, 192),
    new Color(205, 205, 205),
    new Color(102, 102, 102),
    new Color(255, 0,   0  ),
    new Color(0,   255, 0  ),
    new Color(255, 255, 0  ),
    new Color(0,   0,   255),
    new Color(255, 0,   255),
    new Color(0,   255, 255),
    new Color(255, 255, 255),
  };

  public AnsiColour(int ansicode) {
    if (ansicode < 0 || ansicode >= 16) throw new Error("Illegal ansicode: " + ansicode);
    _code = ansicode;
  }

  public AnsiColour(int ansicode, boolean bright) {
    if (ansicode < 0 || ansicode >= 16) throw new Error("Illegal ansicode: " + ansicode);
    _code = ansicode + (bright ? 8 : 0);
  }

  public boolean equals(Object other) {
    if (other instanceof AnsiColour) return _code == ((AnsiColour)other)._code;
    return false;
  }

  public Color toJavaColor() {
    return _palette[_code];
  }

  public String colourName() {
    String[] names = { "black", "red", "green", "yellow", "blue", "magenta", "cyan", "white" };
    if (_code < 8) return names[_code];
    else return "bright" + names[_code-8];
  }

  public Colour brightenedColour() {
    if (_code < 8) return new AnsiColour(_code, true);
    else return this;
  }

  public Colour unbrightenedColour() {
    if (_code >= 8) return new AnsiColour(_code - 8, false);
    else return this;
  }
}

