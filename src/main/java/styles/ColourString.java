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
import turtle.interfaces.immutable.CharacterLayout;
import turtle.interfaces.immutable.LayoutedText;

/**
 * A ColourString is a layoutedtext with a uniform CharacterLayout (typically a single colour).
 */
public class ColourString implements LayoutedText {
  private String _text;
  private CharacterLayout _layout;

  /** Creates a ColourString in the default colour. */
  public ColourString(String str) {
    _text = str;
    _layout = new AttributeGroup();
  }

  /** Creates a ColourString with the given (foreground) colour. */
  public ColourString(String str, Colour col) {
    _text = str;
    _layout = new AttributeGroup(col);
  }

  /** Creates a ColourString with the given character layout. */
  public ColourString(String str, CharacterLayout layout) {
    _text = str;
    _layout = layout;
  }

  public int numParts() {
    return 1;
  }

  public String getPart(int part) {
    if (part == 0) return _text;
    else throw new Error("Asking for part " + part + " in ColourString.");
  }

  public CharacterLayout getStyle(int part) {
    if (part == 0) return _layout;
    else throw new Error("Asking for style of part " + part + " in ColourString.");
  }

  public String getFullString() {
    return _text;
  }

  public boolean isEmpty() {
    return _text.equals("");
  }
}

