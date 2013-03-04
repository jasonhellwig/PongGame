/*---------------------------------------------------------------------------------------------
 * Programmer's name:			Jason Hellwig
 * Email address:				jasonhellwig@gmail.com 
 * Course:						CPSC 233j
 * Assignment number:			6
 * Due Date:					November 12th 2012 
 * Applet URL:					http://www.jasonhellwig.com/pong/
 *--------------------------------------------------------------------------------------------*/
package com.jason.assignment6;

import java.util.Random;

public class PongGame
{
	private Ball gameBall;
	private double period;
	private Paddle player1; //left side
	private Paddle player2; //right side
	private final int WIDTH = Assignment6GUI.PADDLEWIDTH;
	private int gameState;
	private int paddleSpeed;
	
	/* -methods- */
	
	//constructor
	public PongGame()
	{
		gameBall = new Ball();
		player1 = new Paddle();
		player2 = new Paddle();
		reset();
	}
	
	//reset
	public void reset()
	{
		gameBall.reset();
		player1.reset();
		player2.reset();
		gameState = 0;
		paddleSpeed = Assignment6GUI.PADDLE_VELOCITY;
	}
	
	//initialize the game with velocity, angle, and period
	public void initialize(double velocity, double draw_period)
	{
		period = draw_period;
		Random num = new Random();
		/* note that I feel an angles of more than 70 degrees make the game un-fun and un-playable;
		 * therefore, I chose to use 70 degrees as a maximum angle instead of 90.  If 90 degree angles are
		 * truly desired then simply use 180-90 below in place of 140-70.
		 */
		double angle = (num.nextDouble()*140-70);
		angle = angle*Math.PI/180;
		boolean directionizer = num.nextBoolean();
		if (directionizer)
			velocity = velocity * -1;
		gameBall.set_velocity(velocity, angle);
	}
	
	//run a single cycle of the game
	public int run_cylce(boolean up1, boolean down1, boolean up2, boolean down2)
	{
		if (up1)
			player1.moveUp(period);
		if (down1)
			player1.moveDown(period);
		if (up2)
			player2.moveUp(period);
		if (down2)
			player2.moveDown(period);
		gameBall.update_pos(period);
		return gameState;		
	}
	
	//return the ball's x location
	public int get_x()
	{
		return gameBall.get_x_pos();
	}
	
	//return the ball's y location
	public int get_y()
	{
		return gameBall.get_y_pos();
	}
	
	//return game's finished state
	public int get_finish()
	
	{
		return gameState;
	}
	
	//return player1's paddle location
	public int get_paddle1()
	{
		return player1.getPosition();
	}
	
	//return player2's paddle location
	public int get_paddle2()
	{
		return player2.getPosition();
	}
	
	
	//Ball object ------------------------------------------------------
	private class Ball
	{
		private double x_velocity;
		private double y_velocity;
		private double x_pos;
		private double y_pos;
		private final int RADIUS = Assignment6GUI.RADIUS;
		private final int factor = Assignment6GUI.SPEEDFACTOR;
		private final int X_SIZE = Assignment6GUI.X_SIZE;
		private final int Y_SIZE = Assignment6GUI.Y_SIZE;
		private final int paddle_length = Assignment6GUI.PADDLELENGTH;
		
		/* -methods- */
		//constructor
		public Ball()
		{
			reset();
		}
		
		//resets the ball to initial conditions
		public void reset()
		{
			x_velocity = 0;
			y_velocity = 0;
			x_pos = X_SIZE/2;
			y_pos = Y_SIZE/2;		
		}		
		
		//updates the ball's position according to how much time has passed
		public void update_pos(double time)
		{
			//calculate updated positions
			double new_x =  (x_pos + x_velocity*time*factor);
			double new_y =  (y_pos + y_velocity*time*factor);			
			
			//update y position			
			if ((new_y < (Y_SIZE-RADIUS)) && (new_y > (0+RADIUS)))
				y_pos = new_y;
			else
			{
				if (y_velocity < 0)
					y_pos = 2*RADIUS-new_y;
				else
					y_pos = 2*(Y_SIZE-RADIUS) - new_y;	
				y_velocity = y_velocity * -1;				
			}
			
			//update x position
			if ((new_x < ((X_SIZE-WIDTH)-RADIUS)) && (new_x > WIDTH+RADIUS))
				x_pos=new_x;
			else 
			{
				if (x_velocity > 0)
				{	
					if ((y_pos+RADIUS >= player2.getPosition()) && (y_pos-RADIUS <= player2.getPosition() + paddle_length))
					{
						//reflection and difficulty increase
						x_pos = 2*((X_SIZE-WIDTH)-RADIUS)-new_x;					
						paddleSpeed =  (int) (paddleSpeed*.95);
						x_velocity = x_velocity * -1.1;					
					}
					else
						x_pos = new_x;
					if (x_pos > X_SIZE+RADIUS)
						gameState = 1;
				}			
				else
				{
					if ((y_pos+RADIUS >= player1.getPosition()) && (y_pos-RADIUS <= player1.getPosition() + paddle_length))
					{
						//reflection and difficulty increase
						x_pos = 2*(WIDTH+RADIUS)-new_x;
						paddleSpeed =  (int) (paddleSpeed*.95);
						x_velocity = x_velocity * -1.1;
					}
					else
						x_pos = new_x;
					if (x_pos < 0-RADIUS)
						gameState = 2;
				}				
			}
		}
		
		//calculate and set the velocity in x and y directions
		public void set_velocity(double magnitude, double angle)
		{
			x_velocity = magnitude*Math.cos(angle);	
			y_velocity = magnitude*Math.sin(angle);
		}
		
		//return x position
		public int get_x_pos()
		{
			return (int)Math.round(x_pos);
		}
		
		//return y position
		public int get_y_pos()
		{
			return (int)Math.round(y_pos);
		}
	}	
	
	//Paddle Object------------------------------------------------------------------------------
	private class Paddle
	{
		private double base;  //the bottom of the paddle
		private final int offset = Assignment6GUI.PADDLELENGTH; //base + offset = top of the paddle
		private final int factor = Assignment6GUI.SPEEDFACTOR;
		private final int Y_SIZE = Assignment6GUI.Y_SIZE;
		
		//constructor method
		Paddle()
		{
			reset();			
		}
		
		//reset method
		void reset()
		{
			base = Y_SIZE/2 - offset/2;
		}
		
		//getPosition method
		int getPosition()
		{
			return (int) Math.round(base);
		}
		
		//moveUp
		void moveUp(double time)
		{
			double new_base = base + paddleSpeed*time*factor;
			if (new_base + offset <= Y_SIZE)
				base = new_base;
			else
				base = Y_SIZE - offset;
		}
		
		//moveDown
		void moveDown(double time)
		{
			double new_base = base - paddleSpeed*time*factor;
			if (new_base >= 0)
				base = new_base;
			else
				base = 0;
		}
	
	}
}
