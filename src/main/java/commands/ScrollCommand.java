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

package turtle.commands;

import turtle.interfaces.immutable.Command;
import turtle.interfaces.CommandParser;

public class ScrollCommand implements Command {
  public enum Direction { UP, DOWN, TOGGLE };
  Direction _direction;

  public static Command parse(String text, CommandParser parser) {
    if (!parser.queryCommand(text).equals("scroll")) {
      return parser.parseError(text, "ERROR: ScrollCommand.parse called when command is [" +
                                     parser.queryCommand(text) + "]");
    }
    String direction = parser.wordsFrom(text, 1).toLowerCase();
    if (direction.equals("up")) return new ScrollCommand(Direction.UP);
    if (direction.equals("down")) return new ScrollCommand(Direction.DOWN);
    if (direction.equals("toggle")) return new ScrollCommand(Direction.TOGGLE);
      return parser.parseError(text, "Unexpected argument [" + direction + "]: expected " +
                                     "up, down or toggle.");
  }

  /** 1 = up, -1 = down, 0 = toggle scrolling */
  public ScrollCommand(Direction direction) {
    _direction = direction;
  }

  public CommandKind queryCommandKind() {
    return CommandKind.SCROLLCMD;
  }

  public Direction queryDirection() {
    return _direction;
  }
}
