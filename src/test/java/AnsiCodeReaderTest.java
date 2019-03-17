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

import java.util.ArrayList;
import turtle.styles.XTermColour;
import turtle.styles.AttributeGroup;
import turtle.styles.AnsiCodeReader;
import turtle.interfaces.immutable.Colour;
import turtle.interfaces.immutable.CharacterLayout;
import turtle.interfaces.immutable.LayoutedText;

public class AnsiCodeReaderTest {
  @Test
  public void testAnsiSplitting() {
    String esc = Character.toString((char)27);
    AnsiCodeReader reader = new AnsiCodeReader();
    String atext = "abc" + esc + "[0ma9" + esc + "[38;99m" + esc + "x" + esc;
    String btext = "[12;134701x17820" + esc + "[9";
    String ctext = "m";
    String dtext = "[7;3m";
    ArrayList<String> a = reader.splitText(atext);
    ArrayList<String> b = reader.splitText(btext);
    ArrayList<String> c = reader.splitText(ctext);
    ArrayList<String> d = reader.splitText(dtext);
    assertTrue(a.size() == 6);
    assertTrue(a.get(0).equals("abc"));
    assertTrue(a.get(1).equals(esc + "[0m"));
    assertTrue(a.get(2).equals("a9"));
    assertTrue(a.get(3).equals(esc + "[38;99m"));
    assertTrue(a.get(4).equals(esc));
    assertTrue(a.get(5).equals("x"));
    assertTrue(b.size() == 2);
    assertTrue(b.get(0).equals(esc + "[12;134701x"));
    assertTrue(b.get(1).equals("17820"));
    assertTrue(c.size() == 1);
    assertTrue(c.get(0).equals(esc + "[9m"));
    assertTrue(d.size() == 1);
    assertTrue(d.get(0).equals("[7;3m"));
  }

  @Test
  public void testPlainAnsiCodeReading() {
    String esc = Character.toString((char)27);
    AnsiCodeReader reader = new AnsiCodeReader();
    LayoutedText a = reader.parse("abc");
    assertTrue(a.numParts() == 1);
    assertTrue(a.getPart(0).equals("abc"));
    assertTrue(a.getStyle(0).equals(new AttributeGroup()));
    LayoutedText b = reader.parse("def");
    assertTrue(b.numParts() == 1);
    assertTrue(b.getPart(0).equals("def"));
    assertTrue(b.getStyle(0).equals(new AttributeGroup()));
  }

  @Test
  public void testAnsiFrontColourParsing() {
    String esc = Character.toString((char)27);
    AnsiCodeReader reader = new AnsiCodeReader();
    LayoutedText lay = reader.parse(esc + "[32mXXX");
    assertTrue(lay.numParts() == 1);
    assertTrue(lay.getPart(0).equals("XXX"));
    assertEquals(lay.getStyle(0).getFront().colourName(), "green");
    assertEquals(lay.getStyle(0).getBack().colourName(), "defaultback");
    assertFalse(lay.getStyle(0).queryBold());
  }

  @Test
  public void testBrightenedAnsiFrontColourParsing() {
    String esc = Character.toString((char)27);
    AnsiCodeReader reader = new AnsiCodeReader();
    LayoutedText lay = reader.parse(esc + "[92mXXX");
    assertTrue(lay.numParts() == 1);
    assertEquals(lay.getStyle(0).getFront().colourName(), "brightgreen");
    assertFalse(lay.getStyle(0).queryBold());
  }

  @Test
  public void testAnsiBackColourParsing() {
    String esc = Character.toString((char)27);
    AnsiCodeReader reader = new AnsiCodeReader();
    LayoutedText lay = reader.parse(esc + "[41mXXX");
    assertTrue(lay.numParts() == 1);
    assertEquals(lay.getStyle(0).getFront().colourName(), "defaultfront");
    assertEquals(lay.getStyle(0).getBack().colourName(), "red");
    assertFalse(lay.getStyle(0).queryItalic());
  }

  @Test
  public void testBrightenedAnsiBackColourParsing() {
    String esc = Character.toString((char)27);
    AnsiCodeReader reader = new AnsiCodeReader();
    LayoutedText lay = reader.parse(esc + "[109mXXX");
    assertTrue(lay.numParts() == 1);
    assertEquals(lay.getStyle(0).getBack().colourName(), "brightdefaultback");
    assertFalse(lay.getStyle(0).queryBold());
  }

  @Test
  public void testAttributeParsing() {
    String esc = Character.toString((char)27);
    AnsiCodeReader reader = new AnsiCodeReader();
    LayoutedText lay = reader.parse(esc + "[3mXXX");
    assertTrue(lay.numParts() == 1);
    assertTrue(lay.getStyle(0).queryItalic());
    assertEquals(lay.getStyle(0).getFront().colourName(), "defaultfront");
    assertEquals(lay.getStyle(0).getBack().colourName(), "defaultback");
  }

  @Test
  public void testCombinedAnsiParsing() {
    String esc = Character.toString((char)27);
    AnsiCodeReader reader = new AnsiCodeReader();
    LayoutedText lay = reader.parse(esc + "[34;1;45mXXX");
    assertTrue(lay.numParts() == 1);
    assertTrue(lay.getStyle(0).queryBold());
    assertFalse(lay.getStyle(0).queryItalic());
    assertEquals(lay.getStyle(0).getFront().colourName(), "brightblue");
    assertEquals(lay.getStyle(0).getBack().colourName(), "magenta");
  }

  @Test
  public void testXTermReading() {
    String esc = Character.toString((char)27);
    AnsiCodeReader reader = new AnsiCodeReader();
    LayoutedText lay = reader.parse(esc + "[48;5;200;38;5;19mBing");
    assertTrue(lay.numParts() == 1);
    assertTrue(lay.getStyle(0).getFront().equals(new XTermColour(19)));
    assertTrue(lay.getStyle(0).getBack().equals(new XTermColour(200)));
  }

  @Test
  public void testBoldenedXTermReading() {
    String esc = Character.toString((char)27);
    AnsiCodeReader reader = new AnsiCodeReader();
    LayoutedText lay = reader.parse(esc + "[48;5;200;1;38;5;4mBing");
    assertTrue(lay.numParts() == 1);
    assertEquals(lay.getStyle(0).getFront(), new XTermColour(12));
    assertEquals(lay.getStyle(0).getBack(), new XTermColour(200));
    assertTrue(lay.getStyle(0).queryBold());
  }

  @Test
  public void combineTwoParts() {
    String esc = Character.toString((char)27);
    AnsiCodeReader reader = new AnsiCodeReader();
    LayoutedText lay = reader.parse(esc + "[1;43mXXX" + esc + "[3;4;31mYYY");
    assertTrue(lay.numParts() == 2);
    assertTrue(lay.getPart(0).equals("XXX"));
    assertTrue(lay.getPart(1).equals("YYY"));
    assertEquals(lay.getStyle(0).getFront().colourName(), "brightdefaultfront");
    assertEquals(lay.getStyle(0).getBack().colourName(), "yellow");
    assertTrue(lay.getStyle(0).queryBold());
    assertFalse(lay.getStyle(0).queryItalic());
    assertFalse(lay.getStyle(0).queryUnderline());
    assertEquals(lay.getStyle(1).getFront().colourName(), "brightred");
    assertEquals(lay.getStyle(1).getBack().colourName(), "yellow");
    assertTrue(lay.getStyle(1).queryBold());
    assertTrue(lay.getStyle(1).queryItalic());
    assertTrue(lay.getStyle(1).queryUnderline());
  }

  @Test
  public void testReset() {
    String esc = Character.toString((char)27);
    AnsiCodeReader reader = new AnsiCodeReader();
    LayoutedText lay = reader.parse(esc + "[1;43mXXX" + esc + "[0mYYY");
    assertTrue(lay.numParts() == 2);
    assertEquals(lay.getStyle(1).getFront().colourName(), "defaultfront");
    assertEquals(lay.getStyle(1).getBack().colourName(), "defaultback");
    assertFalse(lay.getStyle(1).queryBold());
    assertFalse(lay.getStyle(1).queryBlink());
  }

  @Test
  public void testSemiReset() {
    String esc = Character.toString((char)27);
    AnsiCodeReader reader = new AnsiCodeReader();
    LayoutedText lay = reader.parse(esc + "[1;43mXXX" + esc + "[39;49mYYY");
    assertTrue(lay.numParts() == 2);
    assertEquals(lay.getStyle(1).getFront().colourName(), "brightdefaultfront");
    assertEquals(lay.getStyle(1).getBack().colourName(), "defaultback");
    assertTrue(lay.getStyle(1).queryBold());
  }

  @Test
  public void testEqualAppend() {
    // verifies that if you append something with the same style, it becomes
    // one part
    String esc = Character.toString((char)27);
    AnsiCodeReader reader = new AnsiCodeReader();
    String txt = "XXX" + esc + "[3;31mYYY" + esc + "[0m" + esc + "[3;31mZZZ";
    LayoutedText lay = reader.parse(txt);
    assertTrue(lay.numParts() == 2);
    assertTrue(lay.getPart(1).equals("YYYZZZ"));
    assertTrue(lay.getStyle(1).queryItalic());
    assertFalse(lay.getStyle(1).queryBold());
    assertTrue(lay.getStyle(1).getFront().colourName().equals("red"));
    assertTrue(lay.getStyle(1).getBack().colourName().equals("defaultback"));
  }

  @Test
  public void testIncompleteReading() {
    String esc = Character.toString((char)27);
    AnsiCodeReader reader = new AnsiCodeReader();
    String txt = "XXX" + esc + "[32;3;1;47mY32;m" + esc + "[0mZZZ" + esc + "[";
    LayoutedText lay = reader.parse(txt);
    assertTrue(lay.numParts() == 3);
    String a = lay.getPart(0);
    String b = lay.getPart(1);
    String c = lay.getPart(2);
    assertTrue(a.equals("XXX"));
    assertTrue(b.equals("Y32;m"));
    assertTrue(c.equals("ZZZ"));
    CharacterLayout d = lay.getStyle(0);
    CharacterLayout e = lay.getStyle(1);
    CharacterLayout f = lay.getStyle(2);
    assertTrue(d.equals(f));
    assertTrue(d.equals(new AttributeGroup()));
    assertTrue(e.getFront().colourName().equals("brightgreen"));
    assertTrue(e.getBack().colourName().equals("white"));
    assertTrue(e.queryBold());
    assertTrue(e.queryItalic());
    assertFalse(e.queryReverse());
  }

  @Test
  public void testContinueParsing() {
    String esc = Character.toString((char)27);
    AnsiCodeReader reader = new AnsiCodeReader();
    LayoutedText a = reader.parse(esc + "[4;31mHello " + esc + "[48;5");
    LayoutedText b = reader.parse(";112;1mworld!" + esc + "[0m");
    assertTrue(a.numParts() == 1);
    assertTrue(b.numParts() == 1);
    assertEquals(a.getPart(0), "Hello ");
    assertEquals(b.getPart(0), "world!");
    CharacterLayout acl = a.getStyle(0);
    CharacterLayout bcl = b.getStyle(0);
    assertEquals(acl.getFront().colourName(), "red");
    assertTrue(acl.queryUnderline());
    assertFalse(acl.queryBold());
    assertEquals(bcl.getFront().colourName(), "brightred");
    assertEquals(bcl.getBack(), new XTermColour(112));
    assertTrue(bcl.queryUnderline());
    assertTrue(bcl.queryBold());
  }
}

