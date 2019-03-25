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

import javax.swing.UIManager;
import javax.swing.JFrame;
import turtle.windowing.TurtleFrame;
import turtle.handlers.*;

public class Turtle {
  private static void setupListeners(TurtleFrame frame) {
    InformationHandler infh = new InformationHandler(frame);
    EventBus.registerEventListener(infh);
    ConnectionHandler conh = new ConnectionHandler();
    EventBus.registerEventListener(conh);
    TelnetHandler telh = new TelnetHandler(conh);
    EventBus.registerEventListener(telh);
    CommandParsingHandler cph = new CommandParsingHandler();
    EventBus.registerEventListener(cph);
  }

  public static void main(String[] args) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          UIManager.setLookAndFeel(
             UIManager.getSystemLookAndFeelClassName());

        } catch (Exception ex) {
          ex.printStackTrace();
        }
        TurtleFrame frame = new TurtleFrame();
        setupListeners(frame);
        frame.setVisible(true);
      }
    });
  }
}

