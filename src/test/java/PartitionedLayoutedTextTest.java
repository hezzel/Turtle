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
import turtle.styles.PartitionedLayoutedText;
import turtle.interfaces.immutable.CharacterLayout;
import turtle.interfaces.immutable.LayoutedText;

public class PartitionedLayoutedTextTest {
  private PartitionedLayoutedText incompleteText() {
    PartitionedLayoutedText txt = new PartitionedLayoutedText();
    AttributeGroup ag1 = new AttributeGroup(new XTermColour(30));
    AttributeGroup ag2 = new AttributeGroup(new XTermColour(112), new XTermColour(0),
                                            AttributeGroup.ATT_BOLD);
    AttributeGroup ag3 = new AttributeGroup();
    txt.append("Hello", ag1);
    txt.append(" ", ag3);
    txt.append("world", ag2);
    txt.append("!", ag3);
    return txt;
  }

  private PartitionedLayoutedText completedText() {
    PartitionedLayoutedText txt = incompleteText();
    txt.completeConstruction();
    return txt;
  }

  @Test(expected = java.lang.Error.class)
  public void testImmutableRequiredForNumParts() {
    int n = incompleteText().numParts();
  }

  @Test(expected = java.lang.Error.class)
  public void testImmutableRequiredForGetStyle() {
    CharacterLayout style = (new PartitionedLayoutedText()).getStyle(0);
  }

  @Test(expected = java.lang.Error.class)
  public void testImmutableRequiredForGetFullString() {
    String str = incompleteText().getFullString();
  }

  @Test(expected = java.lang.Error.class)
  public void testImmutableRequiredForIsEmpty() {
    boolean x = incompleteText().isEmpty();
  }

  @Test(expected = java.lang.Error.class)
  public void testMutableRequiredForAppending() {
    completedText().append("Bing!", new AttributeGroup());
  }

  @Test
  public void testSetupWorks() {
    PartitionedLayoutedText text = completedText();
    assertTrue(text.numParts() == 4);
    assertTrue(text.getFullString().equals("Hello world!"));
    assertFalse(text.isEmpty());
    assertTrue(text.getStyle(0).equals(new AttributeGroup(new XTermColour(30))));
    assertTrue(text.getPart(1).equals(" "));
    assertTrue(text.getPart(2).equals("world"));
    assertTrue(text.getStyle(3).equals(new AttributeGroup()));
  }
}

