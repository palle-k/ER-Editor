
/**
  * EReditor
  * ERObject.java
  * Created by Palle on 30.05.2014
  * Copyright (c) 2014 - 2016 Palle.
  * 
  * Permission is hereby granted, free of charge, to any person obtaining a copy
  * of this software and associated documentation files (the "Software"), to deal
  * in the Software without restriction, including without limitation the rights
  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  * copies of the Software, and to permit persons to whom the Software is
  * furnished to do so, subject to the following conditions:
  * 
  * The above copyright notice and this permission notice shall be included in
  * all copies or substantial portions of the Software.
  * 
  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  * THE SOFTWARE.
  */

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;

public abstract class ERObject implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	protected Rectangle	bounds		= new Rectangle();
	protected boolean	selected	= false;
	protected String	name		= "Empty";
	protected boolean	weak;
	
	public void deselect()
	{
		selected = false;
	}
	
	public Point getLocation()
	{
		return bounds.getLocation();
	}
	
	public String getName()
	{
		return name;
	}
	
	public boolean isAffectedBySelectionRectangle(Rectangle r)
	{
		return bounds.intersects(r);
	}
	
	public boolean isAffectedByTouch(Point p)
	{
		return bounds.contains(p);
	}
	
	public boolean isSelected()
	{
		return selected;
	}
	
	public boolean isWeak()
	{
		return weak;
	}
	
	public abstract void paint(Graphics2D g);
	
	public void select()
	{
		selected = true;
	}
	
	public void setLocation(int x, int y)
	{
		bounds.x = x;
		bounds.y = y;
	}
	
	public void setLocation(Point p)
	{
		bounds.x = p.x;
		bounds.y = p.y;
	}
	
	public void setName(String name)
	{
		if (name.length() == 0)
			name = " ";
		this.name = name;
	}
	
	public void setWeak(boolean aFlag)
	{
		weak = aFlag;
	}
}
