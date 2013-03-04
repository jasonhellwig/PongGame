/*---------------------------------------------------------------------------------------------
 * Programmer's name:			Jason Hellwig
 * Email address:				jasonhellwig@gmail.com 
 * Course:						CPSC 233j
 * Assignment number:			6
 * Due Date:					November 12th 2012 
 * Applet URL:					http://www.jasonhellwig.com/pong/
 *--------------------------------------------------------------------------------------------*/
package com.jason.assignment6;

import java.applet.Applet;

import javax.swing.JFrame;

public class Assignment6Applet extends Applet 
{
	private static final long serialVersionUID = 1L;
	private JFrame GUIFrame;
	
	public void init()
	{
		GUIFrame = new JFrame("Pong by Jason Hellwig");
		GUIFrame.getContentPane().add(new Assignment6GUI(GUIFrame));
		GUIFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		GUIFrame.setSize(1000, 600);
		GUIFrame.setVisible(true);	
		GUIFrame.requestFocus();
	}

}
