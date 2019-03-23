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

package turtle.interfaces;

/**
 * A CommandParser offers string analysis functionality to parse strings that represent commands.
 */
public interface CommandParser {
  /** 
   * This parses the given text as a sequence of words, and returns number [num], or the empty
   * string if there are too few words.
   * This is 0-based, and ignores double spaces and indenting at the start.
   * For example, word("    a  b c", 1) returns "b" and word("a  b c", 3) returns the empty string.
   *
   * Note that a returned word will always have positive length, unless num is greater than or
   * equal to the number of words in the line.
   *
   * @see wordsFrom
   */
  public String word(String command, int num);

  /** 
   * This parses the given text as a sequence of words, and returns everything starting from the
   * word with index [num]. If there are not so many words, then the empty string is returned.
   *
   * As in the word function, the index is 0-based and words are non-empty space-separated
   * substrings of [command].  For example, wordsFrom("a  b c", 0) = "a  b c",
   * wordsFrom("a  b c", 1) = "a  b c" and wordsFrom("a  b c", 3) = wordsFrom("a  b c", 4) = "".
   *
   * @see word
   */
  public String wordsFrom(String command, int num);
}

