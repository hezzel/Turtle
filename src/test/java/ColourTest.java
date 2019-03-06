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

import org.junit.Test;
import static org.junit.Assert.*;

import java.awt.Color;
import turtle.styles.DefaultColour;
import turtle.styles.AnsiColour;
import turtle.styles.RGBAColour;
import turtle.styles.AttributeGroup;
import turtle.interfaces.immutable.Colour;
import turtle.interfaces.immutable.CharacterLayout;

public class ColourTest {
  @Test
  public void testDefaults() {
    Colour a = new DefaultColour(true, true);
    Colour b = new DefaultColour(false, false);
    Colour c = new DefaultColour(true, false);
    assertTrue(a.colourName().equals("brightdefaultfront"));
    assertTrue(b.colourName().equals("defaultback"));
    assertTrue(c.brightenedColour().equals(a));
  }

  @Test(expected = java.lang.Error.class)
  public void testIllegalAnsi() {
    Colour a = new AnsiColour(16);
  }

  @Test
  public void testAnsi() {
    Colour a = new AnsiColour(AnsiColour.COL_GREEN);
    Colour b = new AnsiColour(AnsiColour.COL_CYAN, true);
    Colour c = new AnsiColour(AnsiColour.COL_YELLOW, false);
    assertTrue(a.colourName().equals("green"));
    assertTrue(b.colourName().equals("brightcyan"));
    assertTrue(c.colourName().equals("yellow"));
    assertTrue(b.unbrightenedColour().colourName().equals("cyan"));
  }

  @Test(expected = java.lang.IllegalArgumentException.class)
  public void testIllegalRGBA() {
    Colour a = new RGBAColour(10, 100, 1000);
  }

  @Test
  public void testRGBA() {
    Colour a = new RGBAColour(10, 100, 200);
    Colour b = new RGBAColour(new Color(10, 100, 200));
    assertTrue(a.equals(b));
    assertTrue(a.colourName().equals("0a64c8"));
    assertTrue(b.brightenedColour().equals(a.unbrightenedColour()));
  }

  @Test
  public void testAttributeGroup() {
    Colour back = new AnsiColour(AnsiColour.COL_GREEN);
    Colour front = new AnsiColour(AnsiColour.COL_CYAN, true);
    CharacterLayout a = new AttributeGroup();
    CharacterLayout b = new AttributeGroup(new DefaultColour(true,false));
    CharacterLayout c = new AttributeGroup(back, front, AttributeGroup.ATT_ITALIC);
    AttributeGroup d = new AttributeGroup(c);
    AttributeGroup e = d.addAttribute(AttributeGroup.ATT_BOLD);
    CharacterLayout f = e.delAttribute(AttributeGroup.ATT_ITALIC);
    assertTrue(a.equals(b));
    assertTrue(c.equals(d));
    assertTrue(d.queryItalic());
    assertTrue(e.queryBold());
    assertTrue(e.queryItalic());
    assertFalse(f.queryItalic());
    assertFalse(d.queryBold());
    assertFalse(a.queryBlink());
    assertFalse(e.toString().equals(f.toString()));
    assertTrue(a.toString().equals(b.toString()));
  }
}
