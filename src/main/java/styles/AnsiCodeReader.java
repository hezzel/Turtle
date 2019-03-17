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

import java.lang.NumberFormatException;
import java.util.ArrayList;
import turtle.interfaces.immutable.Colour;
import turtle.interfaces.immutable.LayoutedText;

/**
 * This class is used to match ansi, xterm256 and xterm16m escape codes with colour objects and
 * character layouts.
 * It expects to be given _all_ text from a given source, and parses escape codes within the
 * context of what has come before.
 */
public class AnsiCodeReader {
  private static final char ESC = 27;
  private AttributeGroup _currentAtts;
  private String _incompleteAnsi;

  public AnsiCodeReader() {
    _incompleteAnsi = "";
    _currentAtts = new AttributeGroup();
  }

  private int attributeByChar(char code) {
    if (code == '1') return AttributeGroup.ATT_BOLD;
    if (code == '2') return AttributeGroup.ATT_DIM;
    if (code == '3') return AttributeGroup.ATT_ITALIC;
    if (code == '4') return AttributeGroup.ATT_UNDERLINE;
    if (code == '5' || code == '6') return AttributeGroup.ATT_BLINK;
    if (code == '7') return AttributeGroup.ATT_REVERSE;
    if (code == '8') return AttributeGroup.ATT_INVISIBLE;
    if (code == '9') return AttributeGroup.ATT_STRIKETHROUGH;
    return AttributeGroup.ATT_NORMAL;
  }

  /** A single char indicates: set this attribute. */
  private AttributeGroup handleAttributeAddCommand(String cmd, AttributeGroup original) {
    int adding = attributeByChar(cmd.charAt(0));
    if (adding == AttributeGroup.ATT_BOLD) {
      Colour foreground = original.getFront().brightenedColour();
      original = original.replaceForegroundColour(foreground);
    }
    return original.addAttribute(attributeByChar(cmd.charAt(0)));
  }

  /**
   * 2X indicates: remove attribute X; in case of 22 we both turn off ATT_BOLD and ATT_DIM for
   * historical reasons.
   */
  private AttributeGroup handleAttributeDeleteCommand(String cmd, AttributeGroup original) {
    int removing = attributeByChar(cmd.charAt(1));
    if (removing == AttributeGroup.ATT_DIM) {
      original = original.delAttribute(AttributeGroup.ATT_DIM);
      removing = AttributeGroup.ATT_BOLD;
    }
    if (removing == AttributeGroup.ATT_BOLD) {
      Colour foreground = original.getFront().unbrightenedColour();
      original = original.replaceForegroundColour(foreground);
    }
    return original.delAttribute(attributeByChar(cmd.charAt(1)));
  }

  /** 3X indicates: set foreground colour X; 9X indicates the same, but brightens the colour. */
  private AttributeGroup handleFrontColourCommand(String cmd, AttributeGroup original) {
    char n = cmd.charAt(1);
    Colour c = original.getFront();
    boolean bold = original.queryBold() || cmd.charAt(0) == '9';
    if (n >= '0' && n <= '7') c = new AnsiColour(n - '0', bold);
    else if (n == '9') c = new DefaultColour(true, bold);
    return original.replaceForegroundColour(c);
  }

  /** 4X indicates: set background colour X; 10X indicates the same, but brightens the colour. */
  private AttributeGroup handleBackColourCommand(String cmd, AttributeGroup original) {
    boolean bold = cmd.charAt(0) == '1';
    char n = bold ? cmd.charAt(2) : cmd.charAt(1);
    Colour c = original.getBack();
    if (n >= '0' && n <= '7') c = new AnsiColour(n - '0', bold);
    else if (n == '9') c = new DefaultColour(false, bold);
    return original.replaceBackgroundColour(c);
  }

  /** 38;5;x indicates: set foreground colour to XTermColour(x). */
  private AttributeGroup handleXTermFrontColourCommand(String x, AttributeGroup original) {
    try {
      int num = Integer.parseInt(x);
      if (num >= 0 && num < 256) {
        Colour c = new XTermColour(num);
        if (original.queryBold()) c = c.brightenedColour();
        return original.replaceForegroundColour(c);
      }
    }
    catch (NumberFormatException e) {}

    return original;
  }

  /** 48;5;x indicates: set background colour to XTermColour(x). */
  private AttributeGroup handleXTermBackColourCommand(String x, AttributeGroup original) {
    try {
      int num = Integer.parseInt(x);
      if (num >= 0 && num < 256) {
        Colour c = new XTermColour(num);
        return original.replaceBackgroundColour(c);
      }
    }
    catch (NumberFormatException e) {}

    return original;
  }

  /** 38;2;x;y;z indicates: set foreground colour to RGBAColour(x,y,z). */
  private AttributeGroup handleFullFrontColourCommand(String x, String y, String z,
                                                      AttributeGroup original) {
    try {
      int r = Integer.parseInt(x);
      int g = Integer.parseInt(y);
      int b = Integer.parseInt(z);
      if (r < 0 || r >= 256) return original;
      if (g < 0 || g >= 256) return original;
      if (b < 0 || b >= 256) return original;
      return original.replaceForegroundColour(new RGBAColour(r,g,b));
    }
    catch (NumberFormatException e) {
      return original;
    }
  }

  /** 48;2;x;y;z indicates: set background colour to RGBAColour(x,y,z). */
  private AttributeGroup handleFullBackColourCommand(String x, String y, String z,
                                                     AttributeGroup original) {
    try {
      int r = Integer.parseInt(x);
      int g = Integer.parseInt(y);
      int b = Integer.parseInt(z);
      if (r < 0 || r >= 256) return original;
      if (g < 0 || g >= 256) return original;
      if (b < 0 || b >= 256) return original;
      return original.replaceBackgroundColour(new RGBAColour(r,g,b));
    }
    catch (NumberFormatException e) {
      return original;
    }
  }

  /**
   * Split the given ansi code in a sequence of single codes.  The ansi sequence must already
   * be stripped from the leading <esc>. If the sequence if fundamentally broken, then null is
   * returned.
   */
  private String[] ansiCodeParts(String code) {
    if (code.length() <= 1 || code.charAt(1) != '[' || code.charAt(code.length()-1) != 'm') {
      return null;
    }
    return code.substring(2,code.length()-1).split(";");
  }

  /**
   * A single SGR ansi code may contain multiple commands, separated by semi-colons, but a
   * semi-colon is not necessarily the end of a command (it might also separate parts within a
   * specific kind of command).
   * Given the (semi-colon separated) parts of an SGR code as an array, and a position where a
   * single command starts, this function returns the first position in the array after the
   * command -- or -1 if the command is unsupported.
   */
  private int nextAnsiCode(String[] ansiparts, int pos) {
    // just ignore empty commands
    if (ansiparts[pos].length() == 0) return pos+1;
    // one-char commands never take parameters
    if (ansiparts[pos].length() == 1) return pos+1;
    // 21..29 disable attributes (no parameters)
    // 30..37 and 39 as well as 90..97 and 99: change foreground colour (no parameters);
    // 40..47 and 49: change background colour (no parameters)
    if (ansiparts[pos].length() == 2 && ansiparts[pos].charAt(1) != '8') {
      char c = ansiparts[pos].charAt(0);
      if (c == '2' || c == '3' || c == '4' || c == '9') return pos + 1;
    }
    // 100..107 and 109: change background colour (no parameters)
    if (ansiparts[pos].length() == 3 && ansiparts[pos].charAt(2) != '8') {
      if (ansiparts[pos].charAt(0) == '1' && ansiparts[pos].charAt(1) == '0') return pos + 1;
    }
    // 38;5;X and 48;5;X: change foreground colour (resp. background colour) to xterm256-value X
    // 38;2;R;G;B and 48;2;R;G;B: change foreground (resp. background) colour to rgb-value RGB
    if (ansiparts[pos].equals("38") || ansiparts[pos].equals("48")) {
      if (pos + 2 < ansiparts.length && ansiparts[pos+1].equals("5")) return pos + 3;
      if (pos + 4 < ansiparts.length && ansiparts[pos+1].equals("2")) return pos + 5;
    }
    return -1;
  }

  /**
   * Handles the SGR code sgrparts[start];...;sgrparts[end-1] by updating layout and returning the
   * result.
   * Note that this function assumes that nextAnsiCode has been used to approve the section that
   * is now being examined.
   */
  private AttributeGroup handleAnsiCommand(AttributeGroup layout, String[] sgrparts,
                                           int start, int end) {
    String cmd = sgrparts[start];
    if (cmd.equals("")) return layout;
    if (cmd.equals("0")) layout = new AttributeGroup();
    else if (cmd.length() == 1) layout = handleAttributeAddCommand(cmd, layout);
    else if (cmd.charAt(0) == '2') layout = handleAttributeDeleteCommand(cmd, layout);
    else if (end == start + 1) {
      if (cmd.charAt(0) == '3' || cmd.charAt(0) == '9')
        layout = handleFrontColourCommand(cmd, layout);
      if (cmd.charAt(0) == '4' || cmd.substring(0,2).equals("10"))
        layout = handleBackColourCommand(cmd, layout);
    }
    else if (cmd.equals("38") && sgrparts[start+1].equals("5"))
      layout = handleXTermFrontColourCommand(sgrparts[start+2], layout);
    else if (cmd.equals("48") && sgrparts[start+1].equals("5"))
      layout = handleXTermBackColourCommand(sgrparts[start+2], layout);
    else if (cmd.equals("38") && sgrparts[start+1].equals("2"))
      layout = handleFullFrontColourCommand(sgrparts[start+2], sgrparts[start+3],
                                            sgrparts[start+4], layout);
    else if (cmd.equals("48") && sgrparts[start+1].equals("5"))
      layout = handleFullBackColourCommand(sgrparts[start+2], sgrparts[start+3],
                                           sgrparts[start+4], layout);

    return layout;
  }

  /**
   * Starting from the given AttributeGroup, adapts it for the given ansi escape sequence and
   * returns the result (leaving the original intact).
   */
  private AttributeGroup adaptWithAnsi(AttributeGroup original, String ansi) {
    String[] parts = ansiCodeParts(ansi);
    AttributeGroup layout = new AttributeGroup(original);
    if (parts == null) return layout;

    for (int i = 0; i < parts.length; ) {
      int nextpos = nextAnsiCode(parts, i);
      if (nextpos == -1) return layout;
      layout = handleAnsiCommand(layout, parts, i, nextpos);
      i = nextpos;
    }

    return layout;
  }

  /**
   * Returns the position of the first char after the escape code that is started on text[startpos]
   * (where we assume that text.charAt(startpos) = ESC).
   */
  private int findAnsiEnd(String text, int startpos) {
    if (text.length() <= startpos) throw new Error("findAnsiEnd given illegal startpos");
    if (text.length() == startpos + 1) return -1;   // isolated ESC, code is probably incomplete
    if (text.charAt(startpos + 1) != '[') return startpos + 1; // unknown code, just return the ESC
    for (int k = startpos + 2; k < text.length(); k++) {
      char c = text.charAt(k);
      if (c != ';' && (c < '0' || c > '9')) return k+1;
    }
    return -1;
  }
  
  /**
   * Given that text.charAt(pos) = ESC, this method reads until the end of the sequence, stores the
   * result into values, and returns the position of the first char after the ansi sequence.
   * If the ansi sequence is not complete, then nothing is added to values, but instead the
   * incomplete sequence is stored into _incompleteAnsi.  In this case, text.length() is returned.
   */
  private int readAnsiSequence(String text, int pos, ArrayList<String> values) {
    int n = findAnsiEnd(text, pos);
    if (n == -1) {
      _incompleteAnsi = text.substring(pos);
      return text.length();
    }
    else {
      values.add(text.substring(pos, n));
      return n;
    }
  }

  /**
   * Reads text[pos..n-1] into a string that is added onto the end of values, where n is the first
   * position in text with text[n] = ESC; if no such position exists, then n = text.length().
   * The return value is n.
   */
  private int readText(String text, int pos, ArrayList<String> values) {
    int n = text.indexOf(ESC, pos);
    if (n == -1) n = text.length();
    values.add(text.substring(pos, n));
    return n;
  }

  /**
   * This method splits text into a sequence of strings where each part either (a) is an ansi
   * escape sequence, or (b) does not contain any ansi codes.
   * The _incompleteAnsi string is quietly put before the given text, and if the text ends in the
   * middle of an ansi sequence, that part is stored in _incompleteAnsi and not returned.
   */
  public ArrayList<String> splitText(String text) {
    ArrayList<String> ret = new ArrayList<String>();
    text = _incompleteAnsi + text;
    _incompleteAnsi = "";
    for (int pos = 0; pos < text.length(); ) {
      if (text.charAt(pos) == ESC) pos = readAnsiSequence(text, pos, ret);
      else pos = readText(text, pos, ret);
    }
    return ret;
  }

  /**
   * This is the main method of the AnsiCodeReader: it takes the given string -- which may contain
   * ansi colour codes -- and parses it into a LayoutedText.
   * This function takes into account previous calls to parse, treating text as though it is
   * appended to the end of previous texts.  Thus, a string with no ansi codes in it at all may end
   * up coloured if the text that was parsed before it was coloured and did not end on a reset.
   */
  public LayoutedText parse(String text) {
    ArrayList<String> parts = splitText(text);
    PartitionedLayoutedText ret = new PartitionedLayoutedText();
    for (int i = 0; i < parts.size(); i++) {
      String part = parts.get(i);
      if (part.equals("")) continue;
      if (part.charAt(0) == ESC) _currentAtts = adaptWithAnsi(_currentAtts, part);
      else ret.append(part, _currentAtts);
    }
    ret.completeConstruction();
    return ret;
  }
}

