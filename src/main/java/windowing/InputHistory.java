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

package turtle.windowing;

/**
 * This class keeps track of the last <size> strings entered into an input window, and allows the
 * user to browse through these commands.
 */
public class InputHistory {
  private String[] _history;
  private int _offset;
  private int _current;

  /**
   * Create a history "array".
   * @param size The number of strings that should be remembered.
   * If more strings than this number are stored, the older ones are scrolled off.
   * The history is set as not currently being browsed.
   */
  public InputHistory(int size) {
    _history = new String[size];
    for (int i = 0; i < size; i++) _history[i] = "";
    _offset = 0;
    _current = -1;
  }

  /**
   * Adds the given string to the end of the history (scrolling off the top) and marks the window
   * as not currently being browsed.
   * Note that a repetition of the most recent element will not actually be added, nor will an
   * empty element.
   */
  public void addHistoryItem(String item) {
    if (item != null && !item.equals("") && !item.equals(_history[_offset])) {
      _offset = (_offset == 0) ? (_history.length-1) : (_offset-1);
      _history[_offset] = item;
    }
    _current = -1;
  }

  /**
   * Goes up into the history (towards a less recently added item) and returns the history item at
   * the browsed position.
   * If browsing up further is not possible, then null is returned instead, and the browsing
   * position remains at the oldest available history item.
   */
  public String browseUp() {
    if (_current == -1) _current = _offset;
    else {
      _current = (_current + 1) % _history.length;
      if (_current == _offset) {
        _current = (_current + _history.length - 1) % _history.length;
        return null;
      }
    }
    return _history[_current];
  }

  /**
   * Goes down in the history list (towards a more recently added item) and returns the history
   * item at the browsed position.
   * If browsing down further is not possible, then null is returned instead.  In this case,
   * browsing is reset.
   */
  public String browseDown() {
    if (_current == -1) return null;
    else {
      if (_current == _offset) {
        _current = -1;
        return null;
      }
      else {
        _current = (_current + _history.length - 1) % _history.length;
        return _history[_current];
      }
    }
  }

  /** Returns the current history item if we are in browsing mode, or null if not. */
  public String queryCurrent() {
    if (_current == -1) return null;
    return _history[_current];
  }

  /** Takes the history out of browsing status (regardless of whether it was already there). */
  public void resetBrowsing() {
    _current = -1;
  }
}

