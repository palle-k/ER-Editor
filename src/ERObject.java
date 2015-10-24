

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
