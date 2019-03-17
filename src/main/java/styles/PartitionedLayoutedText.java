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
import java.util.ArrayList;
import turtle.interfaces.immutable.Colour;
import turtle.interfaces.immutable.CharacterLayout;
import turtle.interfaces.immutable.LayoutedText;

/**
 * A Partitioned Layouted Text is the most general form of LayoutedText: a sequence of strings,
 * each equipped with its own character layout.
 *
 * Note that a PartitionedLayoutedText is *not* entirely immutable: the construction can take
 * multiple steps.  However, once construction has been completed, immutability is turned on and
 * the PLT can no longer be changed.
 */
public class PartitionedLayoutedText implements LayoutedText {
  private ArrayList<String> _parts;
  private ArrayList<CharacterLayout> _styles;
  private boolean _mutable;

  /** Called by all the LayoutedText functions, as LayoutedText is required to be immutable. */
  private void checkImmutability(String funname) {
    if (_mutable) {
      throw new Error("Method " + funname + " called on PartitionedLayoutedText " +
                      "before construction has been completed!");
    }
  }

  /**
   * Called by all the construction functions, as these can only be called when the text is
   * still mutable.
   */
  private void checkMutability(String funname) {
    if (!_mutable) {
      throw new Error("Method " + funname + " called on PartitionedLayoutedText " +
                      "when construction has already been completed!");
    }
  }

  /**
   * Creates an empty PartitionedLayoutedText, with no text.
   * Note that this PartitionedLayoutedText can NOT be used as a LayoutedText yet: all the
   * LayoutText methods will result in an Error.  Only when construction has been completed can
   * these functions be called.
   */
  public PartitionedLayoutedText() {
    _parts = new ArrayList<String>();
    _styles = new ArrayList<CharacterLayout>();
    _mutable = true;
  }

  /**
   * Marks the construction of the PartitionedLayoutedText as complete; the object is now
   * immutable, and can be used as a LayoutedText.
   */
  public void completeConstruction() {
    _mutable = false;
  }

  /**
   * As part of constructing the PartitionedLayoutedText (so before completeConstruction is called)
   * this method may be used to add another section to the current PartitionedLayoutedText.
   */
  public void append(String txt, CharacterLayout layout) {
    checkMutability("append");

    int last = _styles.size()-1;
    if (!_styles.isEmpty()) {
      if (_styles.get(last).equals(layout)) {
        _parts.set(last, _parts.get(last) + txt);
        return;
      }
      if (_parts.get(last).equals("")) {
        _parts.set(last, txt);
        _styles.set(last, layout);
        return;
      }
    }
    _parts.add(txt);
    _styles.add(layout);
  }

  /**
   * Splitting the string into parts based on the respective layouts, this method returns the
   * number of parts.  Each part has a uniform style and is as long a successive piece as possible
   * with that style.
   */
  public int numParts() {
    checkImmutability("numParts");

    return _parts.size();
  }

  /**
   * Splitting the string into parts based on the respective layouts, this method returns the
   * style of each part.  Each part has a uniform style and is as long a successive piece as
   * possible with that style.
   */
  public CharacterLayout getStyle(int part) {
    checkImmutability("getStyle");

    return _styles.get(part);
  }

  /**
   * Splitting the string into parts based on the respective attributes, this method returns the
   * part with the given index.  Each part has a uniform style and is as long a successive piece as
   * possible with that style.  The index must be in the range 0 ... numParts()-1
   */
  public String getPart(int part) {
    checkImmutability("getPart");

    return _parts.get(part);
  }

  /**
   * This returns the plain string corresponding to this colour string; all attributes are removed,
   * and no ansi sequence is added.
   */
  public String getFullString() {
    checkImmutability("getFullString");

    String ret = "";
    for (int i = 0; i < _parts.size(); i++) ret += _parts.get(i);
    return ret;
  }

  /** This returns whether this is the empty colour string (so whether there is no text). */
  public boolean isEmpty() {
    checkImmutability("isEmpty");

    return _parts.size() == 0 || (_parts.size() == 1 && _parts.get(0).length() == 0);
  }
}

