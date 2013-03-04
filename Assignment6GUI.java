/*---------------------------------------------------------------------------------------------
 * Programmer's name:			Jason Hellwig
 * Email address:				jasonhellwig@gmail.com 
 * Course:						CPSC 233j
 * Assignment number:			6
 * Due Date:					November 12th 2012 
 * Applet URL:					http://www.jasonhellwig.com/pong/
 *--------------------------------------------------------------------------------------------*/
package com.jason.assignment6;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.Timer;

public class Assignment6GUI extends JPanel 
{
	private static final long serialVersionUID = 1L;
	
	//game screen -GUI
	private JPanel displayPanel;
	private GamePanel display;
	public static final int X_SIZE = 950;
	public static final int Y_SIZE = 400;
	public static final int RADIUS = 15;
	private int x_pos;
	private int y_pos;	
	public static final int bgColorR = 51;
	public static final int bgColorG = 102;
	public static final int bgColorB = 0;
	public static final int ballColorR= 253;
	public static final int ballColorG= 253;
	public static final int ballColorB= 0;
	public static final int SPEEDFACTOR = 10;
	public static final int PADDLEWIDTH = 10;
	public static final int PADDLELENGTH = 80;
	public static final int PADDLE_VELOCITY = 50;
	public static final int paddleColorR = 0;
	public static final int paddleColorG = 0;
	public static final int paddleColorB = 0;
	public static final int MAX_WINS = 5;
	private int paddle1_base;
	private int paddle2_base;
	private boolean up1;
	private boolean up2;
	private boolean down1;
	private boolean down2;
	private int finish;
	private int winner;
	private int p1Score;
	private int p2Score;
	
	//game timer (controls the redrawing of each screen)
	private Timer drawTimer;
	private boolean started;
	private boolean firstStart;
	
	//draw frequency constant (controls redraw speed)
	public static final double DRAW_RATE = 60.0;
	public static final double DRAW_PERIOD = 1.0/DRAW_RATE;
	
	
	//control panel -GUI
	private JPanel controlPanel;	
	private JButton newGame;
	private JSlider speed;
	private JButton start;
	private JButton quit;
	private JButton pause;
	private JLabel speedl;
	private JTextField score;
	private JLabel scorel;
	private JPanel controlPanelLabels;
	public static final int PANEL_WIDTH = 950;
	public static final int PANEL_HEIGHT = 50;
	
	
	//reference to game object (controls game logic and variables)
	private PongGame game;
	
	//other references
	private JFrame parent;
	
	public Assignment6GUI(JFrame frame)
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		//set reference to parent frame
		parent = frame;
		
		//create game object
		game = new PongGame();
		finish = game.get_finish();
		x_pos = game.get_x();
		y_pos = game.get_y();
		paddle1_base = game.get_paddle1();
		paddle2_base = game.get_paddle2();
		winner = 0;
		p1Score = p2Score = 0;
		
		//create window heading
		add(Box.createVerticalStrut(20));
		add(new JLabel("Pong"));
		add(Box.createVerticalStrut(20));
		
		//create game display window
		display = new GamePanel();
		displayPanel = new JPanel();
		displayPanel.setLayout(new BoxLayout(displayPanel,BoxLayout.X_AXIS));
		displayPanel.add(Box.createHorizontalGlue());
		displayPanel.add(display);
		displayPanel.add(Box.createHorizontalGlue());
		display.setPreferredSize(new Dimension(X_SIZE,Y_SIZE));
		display.setMaximumSize(new Dimension(X_SIZE,Y_SIZE));
		display.setMinimumSize(new Dimension(X_SIZE,Y_SIZE));
		add(displayPanel);
		add(Box.createVerticalStrut(20));
		add(Box.createVerticalGlue());
		
		//create game control panel
		controlPanelLabels = new JPanel();
		controlPanelLabels.setLayout(new BoxLayout(controlPanelLabels,BoxLayout.X_AXIS));
		speedl = new JLabel("Speed");
		scorel = new JLabel("Score");
		controlPanelLabels.add(Box.createHorizontalStrut(120));
		controlPanelLabels.add(speedl);
		controlPanelLabels.add(Box.createHorizontalStrut(220));
		controlPanelLabels.add(scorel);
		controlPanelLabels.add(Box.createHorizontalStrut(250));
		add(controlPanelLabels);
		add(Box.createVerticalStrut(10));
		controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
		newGame = new JButton("New");
		newGame.addActionListener(new NewGameListener());
		start = new JButton("Go");
		start.addActionListener(new StartListener());
		pause = new JButton("Pause");
		pause.addActionListener(new PauseListener());
		quit = new JButton("Quit");
		quit.addActionListener(new QuitListener());
		score = new JTextField(5);
		score.setMaximumSize(new Dimension(30, 20));
		score.setMinimumSize(new Dimension(30, 20));
		score.setPreferredSize(new Dimension(30, 20));
		score.setText(p1Score + " - " + p2Score);
		score.setEditable(false);
		speed = new JSlider(0, 100, 10);
		speed.setPaintTicks(true);
		speed.setPaintLabels(true);
		speed.setMinorTickSpacing(2);
		speed.setMajorTickSpacing(20);
		speed.setMaximumSize(new Dimension(400,PANEL_HEIGHT));
		controlPanel.add(newGame);
		controlPanel.add(Box.createHorizontalStrut(50));
		controlPanel.add(speed);
		controlPanel.add(Box.createHorizontalStrut(20));
		controlPanel.add(score);
		controlPanel.add(Box.createHorizontalStrut(20));
		controlPanel.add(start);
		controlPanel.add(Box.createHorizontalStrut(20));
		controlPanel.add(pause);
		controlPanel.add(Box.createHorizontalStrut(50));
		controlPanel.add(quit);
		controlPanel.setPreferredSize(new Dimension(PANEL_WIDTH,PANEL_HEIGHT));
		controlPanel.setMaximumSize(new Dimension(PANEL_WIDTH,PANEL_HEIGHT));
		controlPanel.setMinimumSize(new Dimension(PANEL_WIDTH,PANEL_HEIGHT));
		add(controlPanel);
		add(Box.createVerticalStrut(800));
		add(Box.createVerticalGlue());
		
		//create draw timer
		drawTimer = new Timer((int)(DRAW_PERIOD*1000), new ClockListener());
		started = false;
		firstStart = true;
		
		//create key listeners
		start.addKeyListener(new KeyBoardListener());
		start.setFocusable(true);
	}
	
	private class GamePanel extends JPanel
	{
		private static final long serialVersionUID = 1L;

		public void paintComponent(Graphics g) 
		{
			super.paintComponents(g);			
			g.setColor(new Color(bgColorR,bgColorG,bgColorB));
			g.fillRect(0, 0, X_SIZE, Y_SIZE);
			g.setColor(new Color(ballColorR,ballColorG,ballColorB));
			int diam = RADIUS*2;			
			int circle_x = x_pos-RADIUS;
			int circle_y = y_pos-RADIUS;
			g.fillOval(circle_x, circle_y,diam,diam);
			g.setColor(new Color(paddleColorR,paddleColorG,paddleColorB));
			int paddle1_x = 0;
			int paddle1_y = paddle1_base;
			int paddle2_x = X_SIZE-PADDLEWIDTH;
			int paddle2_y = paddle2_base;
			g.fillRect(paddle1_x, paddle1_y, PADDLEWIDTH, PADDLELENGTH);
			g.fillRect(paddle2_x, paddle2_y, PADDLEWIDTH, PADDLELENGTH);
			if (winner == 1)
			{
				g.setFont(new Font("sansserif", Font.BOLD, 32));
				g.drawString("Player 1 Wins!", X_SIZE/2-80, Y_SIZE/2);				
			}
			if (winner == 2)
			{
				g.setFont(new Font("sansserif", Font.BOLD, 32));
				g.drawString("Player 2 Wins!", X_SIZE/2-80, Y_SIZE/2);
		
			}
		}
	}
	
	private class ClockListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			finish = game.run_cylce(up1,down1,up2,down2);
			if (finish > 0)
			{
				drawTimer.stop();
				if (finish == 1)
					p1Score++;
				else p2Score++;
				score.setText(p1Score + " - " + p2Score);
				game.reset();
				finish = game.get_finish();
				if (p1Score > MAX_WINS-1)
					winner = 1;
				else if (p2Score > MAX_WINS-1)
					winner = 2;
				started = false;
				firstStart = true;
			}
			if (winner == 0)
			{
				x_pos = game.get_x();
				y_pos = game.get_y();
				paddle1_base = game.get_paddle1();
				paddle2_base = game.get_paddle2();
			}
				display.repaint();
		}		
	}
	
	private class NewGameListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{	
			try
			{
				drawTimer.stop();
			}
			catch (NullPointerException ex){}			
						
			//reset game values
			game.reset();
			finish = game.get_finish();
			x_pos = game.get_x();
			y_pos = game.get_y();
			paddle1_base = game.get_paddle1();
			paddle2_base = game.get_paddle2();
			winner = 0;
			p1Score = p2Score = 0;
			score.setText(p1Score + " - " + p2Score);
			
			//create draw timer
			drawTimer = new Timer((int)(DRAW_PERIOD*1000), new ClockListener());
			started = false;
			firstStart = true;
			
			//reset the display
			display.repaint();
			
			//unlock velocity inputs
			speed.setEnabled(true);
		}
		
	}
	
	private class StartListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			//start game
			if (winner == 0)
			{
				if (finish == 0)
				{
					if (!started)
					{
						if (firstStart)
						{
							//disable speed inputs
							speed.setEnabled(false);
							
							//get velocity value
							double velocity = speed.getValue();					
							
							//initialize game values
							game.initialize(velocity, DRAW_PERIOD);	
						}
						
						//start draw timer
						started = true;
						firstStart = false;
						drawTimer.start();
					}				
				}
			}
		}
	}
	
	private class QuitListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			parent.dispose();
		}
	}
	
	private class KeyBoardListener implements KeyListener
	{

		public void keyPressed(KeyEvent e) 
		{
			if (e.getKeyCode() == KeyEvent.VK_W)
				down1 = true;
			if (e.getKeyCode() == KeyEvent.VK_S)
				up1 = true;
			if (e.getKeyCode() == KeyEvent.VK_UP)
				down2 = true;
			if (e.getKeyCode() == KeyEvent.VK_DOWN)
				up2 = true;
		}

		public void keyReleased(KeyEvent e) 
		{
			if (e.getKeyCode() == KeyEvent.VK_W)
				down1 = false;
			if (e.getKeyCode() == KeyEvent.VK_S)
				up1 = false;
			if (e.getKeyCode() == KeyEvent.VK_UP)
				down2 = false;
			if (e.getKeyCode() == KeyEvent.VK_DOWN)
				up2 = false;			
		}

		public void keyTyped(KeyEvent e) 
		{		}
		
	}
	
	private class PauseListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			drawTimer.stop();
			started = false;
		}		
	}
}
