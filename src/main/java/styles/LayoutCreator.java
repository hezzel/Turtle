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

import turtle.interfaces.immutable.Colour;
import turtle.interfaces.immutable.CharacterLayout;

/** This class gives easy access to both Colours and CharacterLayouts. */
public class LayoutCreator {
  /** Returns an ansi colour with the given ansi code (0-7) and brightness. */
  public static Colour getAnsiColour(int code, boolean bright) {
    return new AnsiColour(code, bright);
  }

  /**
   * Returns a representation of the default front/back colour (depending on whether front is
   * true), brighted if bright is set to true.
   */
  public static Colour getDefaultColour(boolean front, boolean bright) {
    return new DefaultColour(front, bright);
  }

  /** Returns the colour object for basic black, or bright black */
  public static Colour getBlack(boolean bright) {
    return getAnsiColour(AnsiColour.COL_BLACK, bright);
  }

  /** Returns the colour object for basic red, or bright red */
  public static Colour getRed(boolean bright) {
    return getAnsiColour(AnsiColour.COL_RED, bright);
  }

  /** Returns the colour object for basic green, or bright green */
  public static Colour getGreen(boolean bright) {
    return getAnsiColour(AnsiColour.COL_GREEN, bright);
  }

  /** Returns the colour object for basic yellow, or bright yellow */
  public static Colour getYellow(boolean bright) {
    return getAnsiColour(AnsiColour.COL_YELLOW, bright);
  }

  /** Returns the colour object for basic blue, or bright blue */
  public static Colour getBlue(boolean bright) {
    return getAnsiColour(AnsiColour.COL_BLUE, bright);
  }

  /** Returns the colour object for basic magenta, or bright magenta */
  public static Colour getMagenta(boolean bright) {
    return getAnsiColour(AnsiColour.COL_MAGENTA, bright);
  }

  /** Returns the colour object for basic cyan, or bright cyan */
  public static Colour getCyan(boolean bright) {
    return getAnsiColour(AnsiColour.COL_CYAN, bright);
  }

  /** Returns the colour object for basic white, or bright white */
  public static Colour getWhite(boolean bright) {
    return getAnsiColour(AnsiColour.COL_WHITE, bright);
  }

  /** Returns the colour object for the default foreground colour. */
  public static Colour getDefaultFront(boolean bright) {
    return getDefaultColour(true, bright);
  }

  /** Returns the colour object for the default background colour. */
  public static Colour getDefaultBack() {
    return getDefaultColour(false, false);
  }

  /**
   * Returns a character layout with the given foreground colour, the default background colour
   * and the given boldness.
   */
  public static CharacterLayout getSimpleColourLayout(Colour c, boolean bold) {
    int attribute = bold ? AttributeGroup.ATT_BOLD
                         : AttributeGroup.ATT_NORMAL;
    return new AttributeGroup(getDefaultBack(), c, attribute);
  }

  /**
   * Returns the attribute group with the given ansi foreground colour, which is brightened if bold
   * is true; in addition, the attribute group has a BOLD attribute in that case.
   */
  public static CharacterLayout getAnsiLayout(int ansicode, boolean bold) {
    Colour c = getAnsiColour(ansicode, bold);
    return getSimpleColourLayout(c, bold);
  }

  /**
   * Returns the attribute group with the default front- or background colour as the foreground
   * (front if front = true, and brighted if bold is true), the default background colour and no
   * attributes except perhaps boldness (if given).
   */
  public static CharacterLayout getForeBackLayout(boolean front, boolean bold) {
    Colour c = getDefaultColour(front, bold);
    return getSimpleColourLayout(c, bold);
  }

  public static CharacterLayout getDefaultLayout() {
    return getForeBackLayout(true, false);
  }
}

