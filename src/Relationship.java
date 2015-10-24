

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

public class Relationship extends ERObject
{
	private static final long serialVersionUID = 1L;
	
	private Entity	e1;
	private Entity	e2;
	private boolean	e1toMany;
	private boolean	e2toMany;
	
	public Relationship()
	{
		bounds.x = 400;
		bounds.y = 200;
		bounds.width = 200;
		bounds.height = 80;
		name = "Neue Relationship";
	}
	
	public Entity getFirstEntity()
	{
		return e1;
	}
	
	public boolean getFirstEntityToMany()
	{
		return e1toMany;
	}
	
	public Entity getSecondEntity()
	{
		return e2;
	}
	
	public boolean getSecondEntityToMany()
	{
		return e2toMany;
	}
	
	@Override
	public void paint(Graphics2D g)
	{
		bounds.width = weak ? 210 : 200;
		bounds.height = weak ? 90 : 80;
		
		if (selected)
			g.setColor(new Color(0, 150, 180));
		else
			g.setColor(Color.BLACK);
			
		int e1shiftX = (int) ((e1.bounds.getCenterX() - bounds.getCenterX()) * 0.05f);
		int e2shiftX = (int) ((e2.bounds.getCenterX() - bounds.getCenterX()) * 0.05f);
		int e1shiftY = (int) ((e1.bounds.getCenterY() - bounds.getCenterY()) * 0.05f);
		int e2shiftY = (int) ((e2.bounds.getCenterY() - bounds.getCenterY()) * 0.05f);
		
		if (e1shiftX > e1.bounds.width / 3)
			e1shiftX = e1.bounds.width / 3;
		if (e1shiftX < -e1.bounds.width / 3)
			e1shiftX = -e1.bounds.width / 3;
			
		if (e2shiftX > e2.bounds.width / 3)
			e2shiftX = e2.bounds.width / 3;
		if (e2shiftX < -e2.bounds.width / 3)
			e2shiftX = -e2.bounds.width / 3;
			
		if (e1shiftY > e1.bounds.height / 3)
			e1shiftY = e1.bounds.height / 3;
		if (e1shiftY < -e1.bounds.height / 3)
			e1shiftY = -e1.bounds.height / 3;
			
		if (e2shiftY > e2.bounds.height / 3)
			e2shiftY = e2.bounds.height / 3;
		if (e2shiftY < -e2.bounds.height / 3)
			e2shiftY = -e2.bounds.height / 3;
			
		e1shiftX *= -1;
		e2shiftX *= -1;
		e1shiftY *= -1;
		e2shiftY *= -1;
		
		if (e1.bounds.getCenterX() < e2.bounds.getCenterX())
		{
			if (Math.abs(e1.bounds.getCenterY() - bounds.getCenterY()) < 100 && e1.bounds.getMaxX() + 10 < bounds.x)
			{
				g.drawLine(bounds.x, (int) bounds.getCenterY(), bounds.x + (int) ((e1.bounds.getMaxX() - bounds.x) / 2.0f),
						(int) bounds.getCenterY());
				g.drawLine(bounds.x + (int) ((e1.bounds.getMaxX() - bounds.x) / 2.0f), (int) bounds.getCenterY(),
						bounds.x + (int) ((e1.bounds.getMaxX() - bounds.x) / 2.0f), (int) e1.bounds.getCenterY() + e1shiftY);
				g.drawLine(bounds.x + (int) ((e1.bounds.getMaxX() - bounds.x) / 2.0f), (int) e1.bounds.getCenterY() + e1shiftY,
						e1.bounds.x + e1.bounds.width, (int) e1.bounds.getCenterY() + e1shiftY);
			}
			else if (e1.bounds.getCenterX() > bounds.x - 50)
			{
				g.drawLine(bounds.x, (int) bounds.getCenterY(), bounds.x - 50, (int) bounds.getCenterY());
				g.drawLine(bounds.x - 50, (int) bounds.getCenterY(), bounds.x - 50,
						(int) (bounds.getCenterY() + (e1.bounds.getCenterY() - bounds.getCenterY()) / 2.0f));
				g.drawLine(bounds.x - 50, (int) (bounds.getCenterY() + (e1.bounds.getCenterY() - bounds.getCenterY()) / 2.0f),
						(int) e1.bounds.getCenterX() + e1shiftX,
						(int) (bounds.getCenterY() + (e1.bounds.getCenterY() - bounds.getCenterY()) / 2.0f));
				g.drawLine((int) e1.bounds.getCenterX() + e1shiftX,
						(int) (bounds.getCenterY() + (e1.bounds.getCenterY() - bounds.getCenterY()) / 2.0f),
						(int) e1.bounds.getCenterX() + e1shiftX,
						e1.bounds.y + ((e1.bounds.getCenterY() > bounds.getCenterY()) ? e1.bounds.height : 0));
			}
			else
			{
				g.drawLine(bounds.x, (int) bounds.getCenterY(), (int) e1.bounds.getCenterX() + e1shiftX, (int) bounds.getCenterY());
				g.drawLine((int) e1.bounds.getCenterX() + e1shiftX, (int) bounds.getCenterY(), (int) e1.bounds.getCenterX() + e1shiftX,
						e1.bounds.y + ((e1.bounds.getCenterY() > bounds.getCenterY()) ? e1.bounds.height : 0));
			}
			if (Math.abs(e2.bounds.getCenterY() - bounds.getCenterY()) < 100 && e2.bounds.x - 10 > bounds.getMaxX())
			{
				g.drawLine(bounds.x + bounds.width, (int) bounds.getCenterY(),
						bounds.x + bounds.width + (int) ((e2.bounds.x - bounds.getMaxX()) / 2.0f), (int) bounds.getCenterY());
				g.drawLine(bounds.x + bounds.width + (int) ((e2.bounds.x - bounds.getMaxX()) / 2.0f), (int) bounds.getCenterY(),
						bounds.x + bounds.width + (int) ((e2.bounds.x - bounds.getMaxX()) / 2.0f), (int) e2.bounds.getCenterY() + e2shiftY);
				g.drawLine(bounds.x + bounds.width + (int) ((e2.bounds.x - bounds.getMaxX()) / 2.0f),
						(int) e2.bounds.getCenterY() + e2shiftY, e2.bounds.x, (int) e2.bounds.getCenterY() + e2shiftY);
			}
			else if (e2.bounds.getCenterX() < bounds.x + bounds.width + 50)
			{
				g.drawLine(bounds.x + bounds.width, (int) bounds.getCenterY(), bounds.x + bounds.width + 50, (int) bounds.getCenterY());
				g.drawLine(bounds.x + bounds.width + 50, (int) bounds.getCenterY(), bounds.x + bounds.width + 50,
						(int) (bounds.getCenterY() + (e2.bounds.getCenterY() - bounds.getCenterY()) / 2.0f));
				g.drawLine(bounds.x + bounds.width + 50,
						(int) (bounds.getCenterY() + (e2.bounds.getCenterY() - bounds.getCenterY()) / 2.0f),
						(int) e2.bounds.getCenterX() + e2shiftX,
						(int) (bounds.getCenterY() + (e2.bounds.getCenterY() - bounds.getCenterY()) / 2.0f));
				g.drawLine((int) e2.bounds.getCenterX() + e2shiftX,
						(int) (bounds.getCenterY() + (e2.bounds.getCenterY() - bounds.getCenterY()) / 2.0f),
						(int) e2.bounds.getCenterX() + e2shiftX,
						e2.bounds.y + ((e2.bounds.getCenterY() > bounds.getCenterY()) ? e2.bounds.height : 0));
			}
			else
			{
				g.drawLine(bounds.x + bounds.width, (int) bounds.getCenterY(), (int) e2.bounds.getCenterX() + e2shiftX,
						(int) bounds.getCenterY());
				g.drawLine((int) e2.bounds.getCenterX() + e2shiftX, (int) bounds.getCenterY(), (int) e2.bounds.getCenterX() + e2shiftX,
						e2.bounds.y + ((e2.bounds.getCenterY() > bounds.getCenterY()) ? e2.bounds.height : 0));
			}
		}
		else
		{
			if (Math.abs(e2.bounds.getCenterY() - bounds.getCenterY()) < 100 && e2.bounds.getMaxX() + 10 < bounds.x)
			{
				g.drawLine(bounds.x, (int) bounds.getCenterY(), bounds.x + (int) ((e2.bounds.getMaxX() - bounds.x) / 2.0f),
						(int) bounds.getCenterY());
				g.drawLine(bounds.x + (int) ((e2.bounds.getMaxX() - bounds.x) / 2.0f), (int) bounds.getCenterY(),
						bounds.x + (int) ((e2.bounds.getMaxX() - bounds.x) / 2.0f), (int) e2.bounds.getCenterY() + e2shiftY);
				g.drawLine(bounds.x + (int) ((e2.bounds.getMaxX() - bounds.x) / 2.0f), (int) e2.bounds.getCenterY() + e2shiftY,
						e2.bounds.x + e2.bounds.width, (int) e2.bounds.getCenterY() + e2shiftY);
			}
			else if (e2.bounds.getCenterX() > bounds.x - 50)
			{
				g.drawLine(bounds.x, (int) bounds.getCenterY(), bounds.x - 50, (int) bounds.getCenterY());
				g.drawLine(bounds.x - 50, (int) bounds.getCenterY(), bounds.x - 50,
						(int) (bounds.getCenterY() + (e2.bounds.getCenterY() - bounds.getCenterY()) / 2.0f));
				g.drawLine(bounds.x - 50, (int) (bounds.getCenterY() + (e2.bounds.getCenterY() - bounds.getCenterY()) / 2.0f),
						(int) e2.bounds.getCenterX() + e2shiftX,
						(int) (bounds.getCenterY() + (e2.bounds.getCenterY() - bounds.getCenterY()) / 2.0f));
				g.drawLine((int) e2.bounds.getCenterX() + e2shiftX,
						(int) (bounds.getCenterY() + (e2.bounds.getCenterY() - bounds.getCenterY()) / 2.0f),
						(int) e2.bounds.getCenterX() + e2shiftX,
						e2.bounds.y + ((e2.bounds.getCenterY() > bounds.getCenterY()) ? e2.bounds.height : 0));
			}
			else
			{
				g.drawLine(bounds.x, (int) bounds.getCenterY(), (int) e2.bounds.getCenterX() + e2shiftX, (int) bounds.getCenterY());
				g.drawLine((int) e2.bounds.getCenterX() + e2shiftX, (int) bounds.getCenterY(), (int) e2.bounds.getCenterX() + e2shiftX,
						e2.bounds.y + ((e2.bounds.getCenterY() > bounds.getCenterY()) ? e2.bounds.height : 0));
			}
			if (Math.abs(e1.bounds.getCenterY() - bounds.getCenterY()) < 100 && e1.bounds.x - 10 > bounds.getMaxX())
			{
				g.drawLine(bounds.x + bounds.width, (int) bounds.getCenterY(),
						bounds.x + bounds.width + (int) ((e1.bounds.x - bounds.getMaxX()) / 2.0f), (int) bounds.getCenterY());
				g.drawLine(bounds.x + bounds.width + (int) ((e1.bounds.x - bounds.getMaxX()) / 2.0f), (int) bounds.getCenterY(),
						bounds.x + bounds.width + (int) ((e1.bounds.x - bounds.getMaxX()) / 2.0f), (int) e1.bounds.getCenterY() + e1shiftY);
				g.drawLine(bounds.x + bounds.width + (int) ((e1.bounds.x - bounds.getMaxX()) / 2.0f),
						(int) e1.bounds.getCenterY() + e1shiftY, e1.bounds.x, (int) e1.bounds.getCenterY() + e1shiftY);
			}
			else if (e1.bounds.getCenterX() < bounds.x + bounds.width + 50)
			{
				g.drawLine(bounds.x + bounds.width, (int) bounds.getCenterY(), bounds.x + bounds.width + 50, (int) bounds.getCenterY());
				g.drawLine(bounds.x + bounds.width + 50, (int) bounds.getCenterY(), bounds.x + bounds.width + 50,
						(int) (bounds.getCenterY() + (e1.bounds.getCenterY() - bounds.getCenterY()) / 2.0f));
				g.drawLine(bounds.x + bounds.width + 50,
						(int) (bounds.getCenterY() + (e1.bounds.getCenterY() - bounds.getCenterY()) / 2.0f),
						(int) e1.bounds.getCenterX() + e1shiftX,
						(int) (bounds.getCenterY() + (e1.bounds.getCenterY() - bounds.getCenterY()) / 2.0f));
				g.drawLine((int) e1.bounds.getCenterX() + e1shiftX,
						(int) (bounds.getCenterY() + (e1.bounds.getCenterY() - bounds.getCenterY()) / 2.0f),
						(int) e1.bounds.getCenterX() + e1shiftX,
						e1.bounds.y + ((e1.bounds.getCenterY() > bounds.getCenterY()) ? e1.bounds.height : 0));
			}
			else
			{
				g.drawLine(bounds.x + bounds.width, (int) bounds.getCenterY(), (int) e1.bounds.getCenterX() + e1shiftX,
						(int) bounds.getCenterY());
				g.drawLine((int) e1.bounds.getCenterX() + e1shiftX, (int) bounds.getCenterY(), (int) e1.bounds.getCenterX() + e1shiftX,
						e1.bounds.y + ((e1.bounds.getCenterY() > bounds.getCenterY()) ? e1.bounds.height : 0));
			}
		}
		
		if (e1.bounds.getCenterX() < e2.bounds.getCenterX())
		{
			g.drawString((e1toMany) ? "n" : "1", bounds.x - 5, bounds.y + 30);
			if (e1toMany)
				g.drawString((e2toMany) ? "m" : "1", bounds.x + bounds.width + 5, bounds.y + 30);
			else
				g.drawString((e2toMany) ? "n" : "1", bounds.x + bounds.width + 5, bounds.y + 30);
		}
		else
		{
			g.drawString((e1toMany) ? "n" : "1", bounds.x + bounds.width + 5, bounds.y + 30);
			if (e1toMany)
				g.drawString((e2toMany) ? "m" : "1", bounds.x - 5, bounds.y + 30);
			else
				g.drawString((e2toMany) ? "n" : "1", bounds.x - 5, bounds.y + 30);
		}
		
		Polygon p = new Polygon();
		p.addPoint(bounds.x, bounds.y + bounds.height / 2);
		p.addPoint(bounds.x + bounds.width / 2, bounds.y);
		p.addPoint(bounds.x + bounds.width, bounds.y + bounds.height / 2);
		p.addPoint(bounds.x + bounds.width / 2, bounds.y + bounds.height);
		
		g.setColor(Color.WHITE);
		g.fill(p);
		
		if (selected)
			g.setColor(new Color(0, 150, 180));
		else
			g.setColor(Color.BLACK);
			
		g.draw(p);
		
		if (weak)
		{
			Polygon ip = new Polygon();
			ip.addPoint(bounds.x + 10, bounds.y + bounds.height / 2);
			ip.addPoint(bounds.x + bounds.width / 2, bounds.y + 5);
			ip.addPoint(bounds.x + bounds.width - 10, bounds.y + bounds.height / 2);
			ip.addPoint(bounds.x + bounds.width / 2, bounds.y + bounds.height - 5);
			
			g.draw(ip);
		}
		
		FontRenderContext frc = g.getFontRenderContext();
		Font f = new Font("Helvetica", Font.PLAIN, 18);
		TextLayout tl = new TextLayout(name, f, frc);
		Rectangle2D textBounds = tl.getBounds();
		tl.draw(g, (int) (bounds.x + bounds.width / 2 - textBounds.getWidth() / 2), bounds.y + bounds.height / 2 + 7);
		
	}
	
	public void setFirstEntity(Entity e1)
	{
		this.e1 = e1;
	}
	
	public void setFirstEntityToMany(boolean toMany)
	{
		e1toMany = toMany;
	}
	
	public void setSecondEntity(Entity e2)
	{
		this.e2 = e2;
	}
	
	public void setSecondEntityToMany(boolean toMany)
	{
		e2toMany = toMany;
	}
	
}
