package com.hst.dims.clients;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class JoinTest extends JFrame {

	private static final long serialVersionUID = 1L;

	public JoinTest()
	{
		this.add(new DrawPanel());
		this.setSize(800,600);
		this.setVisible(true);
	}
	
	public static void main(String[] args) {
		
		new JoinTest();
		
	}

	class DrawPanel extends JPanel implements MouseMotionListener
	{
		private static final long serialVersionUID = 1L;
		
		int drawX = 0, drawY = 0;

		public DrawPanel()
		{
			this.addMouseMotionListener(this);
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			g.clearRect(0, 0, 800, 600);
			g.drawString("zzzzzzz", drawX, drawY);
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			drawX = e.getX();
			drawY = e.getY();
			this.repaint();
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			
		}
		
	}
}
