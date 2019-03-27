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

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Rectangle;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import turtle.interfaces.immutable.CharacterLayout;
import turtle.interfaces.immutable.LayoutedText;

/**
 * This class represents the main window of Turtle, where text is printed to the user.
 * The window has in-built scrolling functionality, that can be called from the outside.
 */
public class OutputWindow {
  private JTextPane _textpane;
  private JScrollPane _scrollpane;
  private boolean _shouldScrollToBottom;
  private int _savedScrollPosition;

  public OutputWindow() {
    // set up the text field
    _textpane = new JTextPane();
    _textpane.setBackground(Color.BLACK);
    _textpane.setEditable(false);
    // make it scrollable
    _scrollpane = new JScrollPane(_textpane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                  JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    // but: make it only scrolled when told to!
    DefaultCaret caret = (DefaultCaret) _textpane.getCaret();
    caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
    Document document = _textpane.getDocument();
    document.addDocumentListener(new ScrollingDocumentListener());
    _shouldScrollToBottom = false;
    _savedScrollPosition = -1;
  }

  public void setFont(Font font) {
    _textpane.setFont(font);
  }

  public void addText(LayoutedText text) {
    for (int i = 0; i < text.numParts(); i++) {
      CharacterLayout ag = text.getStyle(i);
      String part = text.getPart(i);
      Style style = ag.getStyle(_textpane);
      StyledDocument doc = _textpane.getStyledDocument();
      try { doc.insertString(doc.getLength(), part, style); }
      catch (BadLocationException e) { } 
    }
  }

  public JComponent queryComponent() {
    return _scrollpane;
  }

  /** Scrolls the GUI component for this window up one page. */
  public void scrollUp() {
    JScrollBar bar = _scrollpane.getVerticalScrollBar();
    int pos = bar.getValue();
    int height = _scrollpane.getHeight();
    int newpos = pos - (height * 9 / 10);
    if (newpos < 0) newpos = 0;
    bar.setValue(newpos);

    // set this to avoid a delayed scrollDown() hitting after the scrollUp has triggered:
    _shouldScrollToBottom = false;
  }

  /** Scrolls the GUI component for this window down one page. */
  public void scrollDown() {
    JScrollBar bar = _scrollpane.getVerticalScrollBar();
    int pos = bar.getValue();
    int height = _scrollpane.getHeight();
    int newpos = pos + (height * 9 / 10);
    bar.setValue(newpos);
  }

  public void scrollToggle() {
    JScrollBar bar = _scrollpane.getVerticalScrollBar();
    if (!isScrollBarFullyExtended(bar)) {
      _savedScrollPosition = bar.getValue();
      _shouldScrollToBottom = true;
      scrollToBottom();
    }
    else if (_savedScrollPosition != -1) {
      bar.setValue(_savedScrollPosition);
      _shouldScrollToBottom = false;
    }
  }

  /**
   * Scrolls the GUI component for this window down as far as it goes, so all new text is visible
   * (and newer text will be made visible when it arrives).
   */
  private void scrollToBottom() {
    if (_shouldScrollToBottom) {
      Rectangle visibleRect = _textpane.getVisibleRect();
      visibleRect.y = _textpane.getHeight() - visibleRect.height;
      _textpane.scrollRectToVisible(visibleRect);
      _shouldScrollToBottom = false;
    }
  }

  /**
   * Returns whether the GUI component for this window is currently scrolled down completely (or
   * almost completely).
   * This is relevant because the window will generally scroll to the bottom when new text appears,
   * but should not do so when the window is scrolled up!
   */
  private boolean isScrollBarFullyExtended(JScrollBar bar) {
    BoundedRangeModel model = bar.getModel();
    int height = _textpane.getFontMetrics(_textpane.getFont()).getHeight();
    return (model.getExtent() + model.getValue()) >= model.getMaximum() - height - 1;
  }

  /**
   * This document listener, which is attached to the text pane on creation, makes sure that the
   * window is scrolled to the bottom whenever new text is added, except if it was already scrolled
   * up.
   */
  private class ScrollingDocumentListener implements DocumentListener {
    public void changedUpdate(DocumentEvent e) { maybeScroll(); }
    public void insertUpdate(DocumentEvent e) { maybeScroll(); }
    public void removeUpdate(DocumentEvent e) { maybeScroll(); }

    /**
     * Called by any of the update functions to handle the scrolling functionality: if the window
     * is currently at bottom scroll, push a "scrollToBottom" on the event queue (so it is done
     * only when the update has actually completely gone through), and otheerwise doesn't do
     * anything (the window will not scroll down automatically).
     */
    private void maybeScroll() {
      JScrollBar scrollBar = _scrollpane.getVerticalScrollBar();
      boolean scrollBarAtBottom = isScrollBarFullyExtended(scrollBar);
      if (scrollBarAtBottom) {
        _shouldScrollToBottom = true;
        EventQueue.invokeLater(new Runnable() {
          public void run() {
            EventQueue.invokeLater(new Runnable() {
              public void run() {
                scrollToBottom();
              }
            });
          }
        });
      }
    }
  }
}

