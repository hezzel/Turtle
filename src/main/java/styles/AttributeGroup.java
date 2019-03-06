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

import javax.swing.JTextPane;
import javax.swing.text.*;
import turtle.interfaces.immutable.Colour;
import turtle.interfaces.immutable.CharacterLayout;

/**
 * The AttributeGroup class combines a front- and background-colour with various text attributes
 * like boldness or underlining.
 */
public class AttributeGroup implements CharacterLayout {
  public static final int ATT_NORMAL        = 0;
  public static final int ATT_BOLD          = 2;
  public static final int ATT_ITALIC        = 4;
  public static final int ATT_UNDERLINE     = 8;
  public static final int ATT_REVERSE       = 16; 
  public static final int ATT_BLINK         = 32; 
  public static final int ATT_DIM           = 64; 
  public static final int ATT_INVISIBLE     = 128;
  public static final int ATT_STRIKETHROUGH = 256;

  private Colour _back;
  private Colour _front;
  private int _attributes; // all attributes are saved in a single int, using binary or

  /** Create an AttributeGroup with default back- and foreground colour, and no attributes. */
  public AttributeGroup() {
    _back = new DefaultColour(false, false);
    _front = new DefaultColour(true, false);
    _attributes = ATT_NORMAL;
  }

  /**
   * Create an AttributeGroup with default background colour, no attributes, and the given
   * foreground colour.
   */
  public AttributeGroup(Colour f) {
    _back = new DefaultColour(false, false);
    _attributes = ATT_NORMAL;
    _front = f;
  }

  /**
   * Create an AttributeGroup with default background colour and the given foreground colour and
   * attributes (one of the ATT_choices).
   */
  public AttributeGroup(Colour f, int atts) {
    _back = new DefaultColour(false, false);
    _attributes = atts;
    _front = f;
  }

  /**
   * Create an AttributeGroup with background colour b, foreground colour f, and attributes atts,
   * where atts should be one of the ATT_ constants in the class.
   */
  public AttributeGroup(Colour b, Colour f, int atts) {
    _back = b;
    _front = f;
    _attributes = atts;
  }

  /** Creates an AttributeGroup which corresponds to the reference. */
  public AttributeGroup(CharacterLayout reference) {
    _back = reference.getBack();
    _front = reference.getFront();
    _attributes = 0;
    if (reference.queryBold()) _attributes |= ATT_BOLD;
    if (reference.queryDim()) _attributes |= ATT_DIM;
    if (reference.queryItalic()) _attributes |= ATT_ITALIC;
    if (reference.queryUnderline()) _attributes |= ATT_UNDERLINE;
    if (reference.queryBlink()) _attributes |= ATT_BLINK;
    if (reference.queryReverse()) _attributes |= ATT_REVERSE;
    if (reference.queryInvisible()) _attributes |= ATT_INVISIBLE;
    if (reference.queryStrikethrough()) _attributes |= ATT_STRIKETHROUGH;
  }

  public Colour getFront() {
    return _front;
  }
  
  public Colour getBack() {
    return _back;
  }

  private boolean hasAttribute(int attribute) {
    return (_attributes & attribute) == attribute;
  }

  public boolean queryBold() { return hasAttribute(ATT_BOLD); }
  public boolean queryDim() { return hasAttribute(ATT_DIM); }
  public boolean queryItalic() { return hasAttribute(ATT_ITALIC); }
  public boolean queryUnderline() { return hasAttribute(ATT_UNDERLINE); }
  public boolean queryBlink() { return hasAttribute(ATT_BLINK); }
  public boolean queryReverse() { return hasAttribute(ATT_REVERSE); }
  public boolean queryInvisible() { return hasAttribute(ATT_INVISIBLE); }
  public boolean queryStrikethrough() { return hasAttribute(ATT_STRIKETHROUGH); }

  /**
   * Returns a AttributeGroup that copies the current one, but with the given attribute added (one
   * of the ATT_ constants).
   */
  public AttributeGroup addAttribute(int attribute) {
    return new AttributeGroup(_back, _front, _attributes | attribute);
  }

  /**
   * Returns a AttributeGroup that copies the current one, but with the given attribute removed
   * (one of the ATT_ constants).
   */
  public AttributeGroup delAttribute(int attribute) {
    return new AttributeGroup(_back, _front, _attributes & (511 - attribute));
  }

  /**
   * Returns a AttributeGroup that copies the current one, but with the background colour replaced
   * by colour.
   */
  public AttributeGroup replaceBackgroundColour(Colour colour) {
    return new AttributeGroup(colour, _front, _attributes);
  }

  /**
   * Returns a AttributeGroup that copies the current one, but with the foreground colour replaced
   * by colour.
   */
  public AttributeGroup replaceForegroundColour(Colour colour) {
    return new AttributeGroup(_back, colour, _attributes);
  }

  /** Returns a string uniquely describing this group. */
  private String styleName() {
    return _back.colourName() + "." + _front.colourName() + "." + Integer.toString(_attributes);
  }

  /** Adjusts the given Style with the values for this attribute group */
  private void setupStyle(Style style) {
    StyleConstants.setForeground(style, _front.toJavaColor());
    StyleConstants.setBackground(style, _back.toJavaColor());
    StyleConstants.setBold(style, queryBold());
    StyleConstants.setItalic(style, queryItalic());
    StyleConstants.setUnderline(style, queryUnderline());
    StyleConstants.setStrikeThrough(style, queryStrikethrough());
  }

  /**
   * Returns a (java-internal) Style object representing the current attribute group.
   * The given JTextPane is needed to obtain an initial style object.
   */
  public Style getStyle(JTextPane ref) {
    String name = styleName();
    Style ret = ref.getStyle(name);
    if (ret == null) {
      ret = ref.addStyle(name, null);
      setupStyle(ret);
    }
    return ret;
  }

  /** Returns a string uniquely describing this attribute group. */
  public String toString() {
    return styleName();
  }

  public boolean equals(Object other) {
    if (!(other instanceof CharacterLayout)) return false;
    CharacterLayout c = (CharacterLayout)other;
    if (!c.getBack().equals(_back)) return false;
    if (!c.getFront().equals(_front)) return false;
    AttributeGroup group;
    if (c instanceof AttributeGroup) group = (AttributeGroup)c;
    else group = new AttributeGroup(c);
    return _attributes == group._attributes;
  }
}

