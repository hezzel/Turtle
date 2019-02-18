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

package turtle;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import static javax.swing.GroupLayout.Alignment.*;

/**
 * This class represents the main frame of Turtle.
 * Its responsibility is to show the appropriate windows to the user.
 */
public class TurtleFrame extends JFrame {
  /** Helper function for the constructor. */
  private void setupDefaultSize() {
    setState(JFrame.NORMAL);
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Dimension dimension = toolkit.getScreenSize();
    dimension.setSize(dimension.getWidth() * 3 / 4, dimension.getHeight() * 3 / 4);
    setPreferredSize(dimension);
    setExtendedState(JFrame.MAXIMIZED_BOTH);
  }

  /** Helper function for the constructor. */
  private void setupDefaultLayout() {
    JTextPane output = new JTextPane();
    JTextField input = new JTextField();

    JPanel panel = new JPanel();
    GroupLayout layout = new GroupLayout(panel);
    panel.setLayout(layout);
    layout.setAutoCreateGaps(true);
    layout.setAutoCreateContainerGaps(true);

    layout.setHorizontalGroup(layout.createParallelGroup(LEADING)
      .addComponent(output)
      .addComponent(input)
    );  
    
    layout.setVerticalGroup(layout.createSequentialGroup()
      .addComponent(output)
      .addComponent(input, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                    GroupLayout.PREFERRED_SIZE)
    );  

    add(panel);
  }

  /** Helper function for the constructor. */
  private void setupWindowClosing() {
    // handle window closing
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        quit();
      }
    });
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
  }

  /** Action for when the user indicates a desire to quit. */
  public void quit() {
    dispose();
    System.exit(0);
  }

  public TurtleFrame() {
    setupDefaultSize();
    setupDefaultLayout();

    setupWindowClosing();

    // finalise
    setTitle("Turtle");
    pack();
    setVisible(true);
  }
}

