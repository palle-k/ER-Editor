
/**
  * EReditor
  * Entity.java
  * Created by Palle on 30.05.2014
  * Copyright (c) 2014 - 2015 Palle 
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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class Entity extends ERObject
{
	private static final long serialVersionUID = 1L;
	
	protected ArrayList<Attribute>	attributes;
	private boolean					abstractEntity;
	private Entity					parentEntity;
	private Entity					aggregatedEntity;
	
	public Entity()
	{
		attributes = new ArrayList<Attribute>();
		bounds.x = 500;
		bounds.y = 400;
		bounds.width = 200;
		bounds.height = 80;
		name = ER_Editor.LOCALIZATION.getString("entity_default_name");
	}
	
	public Entity getAggregatedEntity()
	{
		return aggregatedEntity;
	}
	
	public Entity getParent()
	{
		return parentEntity;
	}
	
	public boolean hasAggregatedEntity()
	{
		return aggregatedEntity != null;
	}
	
	public boolean hasParentEntity()
	{
		return parentEntity != null;
	}
	
	public boolean isAbstract()
	{
		return abstractEntity;
	}
	
	@Override
	public void paint(Graphics2D g)
	{
		bounds.width = weak ? 210 : 200;
		bounds.height = weak ? 90 : 80;
		
		// Parent Entity
		
		if (parentEntity != null)
		{
			int shiftX = (int) ((parentEntity.bounds.getCenterX() - bounds.getCenterX()) * 0.05f);
			int shiftY = (int) ((parentEntity.bounds.getCenterY() - bounds.getCenterY()) * 0.05f);
			shiftX *= -1;
			shiftY *= -1;
			
			if (selected)
				g.setColor(new Color(0, 150, 180));
			else
				g.setColor(Color.BLACK);
			if (Math.abs(bounds.getCenterX() - parentEntity.bounds.getCenterX()) < 130)
			{
				g.drawLine((int) bounds.getCenterX(), (int) bounds.getCenterY(), (int) bounds.getCenterX(),
						(int) (bounds.getCenterY() + (parentEntity.bounds.getCenterY() - bounds.getCenterY()) / 2.0f));
				g.drawLine((int) bounds.getCenterX(),
						(int) (bounds.getCenterY() + (parentEntity.bounds.getCenterY() - bounds.getCenterY()) / 2.0f),
						(int) parentEntity.bounds.getCenterX() + shiftX,
						(int) (bounds.getCenterY() + (parentEntity.bounds.getCenterY() - bounds.getCenterY()) / 2.0f));
				if (bounds.getCenterY() < parentEntity.bounds.getCenterY())
				{
					g.drawLine((int) parentEntity.bounds.getCenterX() + shiftX,
							(int) (bounds.getCenterY() + (parentEntity.bounds.getCenterY() - bounds.getCenterY()) / 2.0f),
							(int) parentEntity.bounds.getCenterX() + shiftX, parentEntity.bounds.y);
					int tcPosX = (int) parentEntity.bounds.getCenterX() + shiftX;
					int tcPosY = parentEntity.bounds.y;
					g.drawLine(tcPosX, tcPosY, tcPosX - 5, tcPosY - 8);
					g.drawLine(tcPosX + 5, tcPosY - 8, tcPosX - 5, tcPosY - 8);
					g.drawLine(tcPosX, tcPosY, tcPosX + 5, tcPosY - 8);
				}
				else
				{
					g.drawLine((int) parentEntity.bounds.getCenterX() + shiftX,
							(int) (bounds.getCenterY() + (parentEntity.bounds.getCenterY() - bounds.getCenterY()) / 2.0f),
							(int) parentEntity.bounds.getCenterX() + shiftX, parentEntity.bounds.y + parentEntity.bounds.height);
					int tcPosX = (int) parentEntity.bounds.getCenterX() + shiftX;
					int tcPosY = parentEntity.bounds.y + parentEntity.bounds.height;
					g.drawLine(tcPosX, tcPosY, tcPosX - 5, tcPosY + 8);
					g.drawLine(tcPosX + 5, tcPosY + 8, tcPosX - 5, tcPosY + 8);
					g.drawLine(tcPosX, tcPosY, tcPosX + 5, tcPosY + 8);
				}
			}
			else if (Math.abs(bounds.getCenterY() - parentEntity.bounds.getCenterY()) < 100)
			{
				g.drawLine((int) bounds.getCenterX(), (int) (bounds.getCenterY()),
						(int) (bounds.getCenterX() + (parentEntity.bounds.getCenterX() - bounds.getCenterX()) / 2.0f),
						(int) (bounds.getCenterY()));
				g.drawLine((int) (bounds.getCenterX() + (parentEntity.bounds.getCenterX() - bounds.getCenterX()) / 2.0f),
						(int) bounds.getCenterY(),
						(int) (bounds.getCenterX() + (parentEntity.bounds.getCenterX() - bounds.getCenterX()) / 2.0f),
						(int) parentEntity.bounds.getCenterY() + shiftY);
				if (bounds.getCenterX() < parentEntity.bounds.getCenterX())
				{
					g.drawLine((int) (bounds.getCenterX() + (parentEntity.bounds.getCenterX() - bounds.getCenterX()) / 2.0f),
							(int) parentEntity.bounds.getCenterY() + shiftY, parentEntity.bounds.x,
							(int) parentEntity.bounds.getCenterY() + shiftY);
					int tcPosX = parentEntity.bounds.x;
					int tcPosY = (int) parentEntity.bounds.getCenterY() + shiftY;
					g.drawLine(tcPosX, tcPosY, tcPosX - 8, tcPosY - 5);
					g.drawLine(tcPosX - 8, tcPosY - 5, tcPosX - 8, tcPosY + 5);
					g.drawLine(tcPosX - 8, tcPosY + 5, tcPosX, tcPosY);
				}
				else
				{
					g.drawLine((int) (bounds.getCenterX() + (parentEntity.bounds.getCenterX() - bounds.getCenterX()) / 2.0f),
							(int) parentEntity.bounds.getCenterY() + shiftY, parentEntity.bounds.x + parentEntity.bounds.width,
							(int) parentEntity.bounds.getCenterY() + shiftY);
					int tcPosX = parentEntity.bounds.x + parentEntity.bounds.width;
					int tcPosY = (int) parentEntity.bounds.getCenterY() + shiftY;
					g.drawLine(tcPosX, tcPosY, tcPosX + 8, tcPosY - 5);
					g.drawLine(tcPosX + 8, tcPosY - 5, tcPosX + 8, tcPosY + 5);
					g.drawLine(tcPosX + 8, tcPosY + 5, tcPosX, tcPosY);
				}
			}
			else
			{
				g.drawLine((int) bounds.getCenterX(), (int) bounds.getCenterY(), (int) parentEntity.bounds.getCenterX() + shiftX,
						(int) bounds.getCenterY());
				if (bounds.getCenterY() < parentEntity.bounds.getCenterY())
				{
					g.drawLine((int) parentEntity.bounds.getCenterX() + shiftX, (int) bounds.getCenterY(),
							(int) parentEntity.bounds.getCenterX() + shiftX, parentEntity.bounds.y);
					int tcPosX = (int) parentEntity.bounds.getCenterX() + shiftX;
					int tcPosY = parentEntity.bounds.y;
					g.drawLine(tcPosX, tcPosY, tcPosX - 5, tcPosY - 8);
					g.drawLine(tcPosX + 5, tcPosY - 8, tcPosX - 5, tcPosY - 8);
					g.drawLine(tcPosX, tcPosY, tcPosX + 5, tcPosY - 8);
				}
				else
				{
					g.drawLine((int) parentEntity.bounds.getCenterX() + shiftX, (int) bounds.getCenterY(),
							(int) parentEntity.bounds.getCenterX() + shiftX, (int) parentEntity.bounds.getMaxY());
					int tcPosX = (int) parentEntity.bounds.getCenterX() + shiftX;
					int tcPosY = parentEntity.bounds.y + parentEntity.bounds.height;
					g.drawLine(tcPosX, tcPosY, tcPosX - 5, tcPosY + 8);
					g.drawLine(tcPosX + 5, tcPosY + 8, tcPosX - 5, tcPosY + 8);
					g.drawLine(tcPosX, tcPosY, tcPosX + 5, tcPosY + 8);
				}
			}
			
		}
		
		// Aggregated Entity
		
		if (aggregatedEntity != null)
		{
			int shiftX = (int) ((aggregatedEntity.bounds.getCenterX() - bounds.getCenterX()) * 0.05f);
			int shiftY = (int) ((aggregatedEntity.bounds.getCenterY() - bounds.getCenterY()) * 0.05f);
			shiftX *= -1;
			shiftY *= -1;
			
			if (selected)
				g.setColor(new Color(0, 150, 180));
			else
				g.setColor(Color.BLACK);
			if (Math.abs(bounds.getCenterX() - aggregatedEntity.bounds.getCenterX()) < 130)
			{
				g.drawLine((int) bounds.getCenterX(), (int) bounds.getCenterY(), (int) bounds.getCenterX(),
						(int) (bounds.getCenterY() + (aggregatedEntity.bounds.getCenterY() - bounds.getCenterY()) / 2.0f));
				g.drawLine((int) bounds.getCenterX(),
						(int) (bounds.getCenterY() + (aggregatedEntity.bounds.getCenterY() - bounds.getCenterY()) / 2.0f),
						(int) aggregatedEntity.bounds.getCenterX() + shiftX,
						(int) (bounds.getCenterY() + (aggregatedEntity.bounds.getCenterY() - bounds.getCenterY()) / 2.0f));
				if (bounds.getCenterY() < aggregatedEntity.bounds.getCenterY())
				{
					g.drawLine((int) aggregatedEntity.bounds.getCenterX() + shiftX,
							(int) (bounds.getCenterY() + (aggregatedEntity.bounds.getCenterY() - bounds.getCenterY()) / 2.0f),
							(int) aggregatedEntity.bounds.getCenterX() + shiftX, aggregatedEntity.bounds.y);
					int tcPosX = (int) aggregatedEntity.bounds.getCenterX() + shiftX;
					int tcPosY = aggregatedEntity.bounds.y;
					g.drawLine(tcPosX, tcPosY, tcPosX - 8, tcPosY - 12);
					g.drawLine(tcPosX, tcPosY, tcPosX + 8, tcPosY - 12);
					g.drawLine(tcPosX, tcPosY - 24, tcPosX - 8, tcPosY - 12);
					g.drawLine(tcPosX, tcPosY - 24, tcPosX + 8, tcPosY - 12);
				}
				else
				{
					g.drawLine((int) aggregatedEntity.bounds.getCenterX() + shiftX,
							(int) (bounds.getCenterY() + (aggregatedEntity.bounds.getCenterY() - bounds.getCenterY()) / 2.0f),
							(int) aggregatedEntity.bounds.getCenterX() + shiftX,
							aggregatedEntity.bounds.y + aggregatedEntity.bounds.height);
					int tcPosX = (int) aggregatedEntity.bounds.getCenterX() + shiftX;
					int tcPosY = aggregatedEntity.bounds.y + aggregatedEntity.bounds.height;
					g.drawLine(tcPosX, tcPosY, tcPosX - 8, tcPosY + 12);
					g.drawLine(tcPosX, tcPosY, tcPosX + 8, tcPosY + 12);
					g.drawLine(tcPosX, tcPosY + 24, tcPosX - 8, tcPosY + 12);
					g.drawLine(tcPosX, tcPosY + 24, tcPosX + 8, tcPosY + 12);
				}
			}
			else if (Math.abs(bounds.getCenterY() - aggregatedEntity.bounds.getCenterY()) < 100)
			{
				g.drawLine((int) bounds.getCenterX(), (int) (bounds.getCenterY()),
						(int) (bounds.getCenterX() + (aggregatedEntity.bounds.getCenterX() - bounds.getCenterX()) / 2.0f),
						(int) (bounds.getCenterY()));
				g.drawLine((int) (bounds.getCenterX() + (aggregatedEntity.bounds.getCenterX() - bounds.getCenterX()) / 2.0f),
						(int) bounds.getCenterY(),
						(int) (bounds.getCenterX() + (aggregatedEntity.bounds.getCenterX() - bounds.getCenterX()) / 2.0f),
						(int) aggregatedEntity.bounds.getCenterY() + shiftY);
				if (bounds.getCenterX() < aggregatedEntity.bounds.getCenterX())
				{
					g.drawLine((int) (bounds.getCenterX() + (aggregatedEntity.bounds.getCenterX() - bounds.getCenterX()) / 2.0f),
							(int) aggregatedEntity.bounds.getCenterY() + shiftY, aggregatedEntity.bounds.x,
							(int) aggregatedEntity.bounds.getCenterY() + shiftY);
					int tcPosX = aggregatedEntity.bounds.x;
					int tcPosY = (int) aggregatedEntity.bounds.getCenterY() + shiftY;
					g.drawLine(tcPosX, tcPosY, tcPosX - 12, tcPosY - 8);
					g.drawLine(tcPosX, tcPosY, tcPosX - 12, tcPosY + 8);
					g.drawLine(tcPosX - 12, tcPosY - 8, tcPosX - 24, tcPosY);
					g.drawLine(tcPosX - 12, tcPosY + 8, tcPosX - 24, tcPosY);
				}
				else
				{
					g.drawLine((int) (bounds.getCenterX() + (aggregatedEntity.bounds.getCenterX() - bounds.getCenterX()) / 2.0f),
							(int) aggregatedEntity.bounds.getCenterY() + shiftY, aggregatedEntity.bounds.x + aggregatedEntity.bounds.width,
							(int) aggregatedEntity.bounds.getCenterY() + shiftY);
					int tcPosX = aggregatedEntity.bounds.x + aggregatedEntity.bounds.width;
					int tcPosY = (int) aggregatedEntity.bounds.getCenterY() + shiftY;
					g.drawLine(tcPosX + 24, tcPosY, tcPosX + 12, tcPosY - 8);
					g.drawLine(tcPosX + 24, tcPosY, tcPosX + 12, tcPosY + 8);
					g.drawLine(tcPosX + 12, tcPosY + 8, tcPosX, tcPosY);
					g.drawLine(tcPosX + 12, tcPosY - 8, tcPosX, tcPosY);
				}
			}
			else
			{
				g.drawLine((int) bounds.getCenterX(), (int) bounds.getCenterY(), (int) aggregatedEntity.bounds.getCenterX() + shiftX,
						(int) bounds.getCenterY());
				if (bounds.getCenterY() < aggregatedEntity.bounds.getCenterY())
				{
					g.drawLine((int) aggregatedEntity.bounds.getCenterX() + shiftX, (int) bounds.getCenterY(),
							(int) aggregatedEntity.bounds.getCenterX() + shiftX, aggregatedEntity.bounds.y);
					int tcPosX = (int) aggregatedEntity.bounds.getCenterX() + shiftX;
					int tcPosY = aggregatedEntity.bounds.y;
					g.drawLine(tcPosX, tcPosY, tcPosX - 8, tcPosY - 12);
					g.drawLine(tcPosX, tcPosY, tcPosX + 8, tcPosY - 12);
					g.drawLine(tcPosX, tcPosY - 24, tcPosX - 8, tcPosY - 12);
					g.drawLine(tcPosX, tcPosY - 24, tcPosX + 8, tcPosY - 12);
				}
				else
				{
					g.drawLine((int) aggregatedEntity.bounds.getCenterX() + shiftX, (int) bounds.getCenterY(),
							(int) aggregatedEntity.bounds.getCenterX() + shiftX, (int) aggregatedEntity.bounds.getMaxY());
					int tcPosX = (int) aggregatedEntity.bounds.getCenterX() + shiftX;
					int tcPosY = aggregatedEntity.bounds.y + aggregatedEntity.bounds.height;
					g.drawLine(tcPosX, tcPosY, tcPosX - 8, tcPosY + 12);
					g.drawLine(tcPosX, tcPosY, tcPosX + 8, tcPosY + 12);
					g.drawLine(tcPosX, tcPosY + 24, tcPosX - 8, tcPosY + 12);
					g.drawLine(tcPosX, tcPosY + 24, tcPosX + 8, tcPosY + 12);
				}
			}
			
		}
		
		// Entity
		
		float a_radius_x = Math.max(0, attributes.size() - 8) * 15 + 200;
		float a_radius_y = Math.max(0, attributes.size() - 8) * 15 + 150;
		
		for (int i = 0; i < attributes.size(); i++)
		{
			float angle = (float) i / attributes.size() * -2.0f * 3.141592653f + 3.141592653f;
			float posX = (float) (Math.cos(angle) * a_radius_x + bounds.getCenterX());
			float posY = (float) (Math.sin(angle) * -a_radius_y + bounds.getCenterY());
			
			if (selected)
				g.setColor(new Color(0, 150, 180));
			else
				g.setColor(Color.BLACK);
			g.drawLine((int) posX, (int) posY, bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
			
			FontRenderContext frc = g.getFontRenderContext();
			Font f = new Font("Helvetica", Font.PLAIN, 12);
			TextLayout tl = new TextLayout(attributes.get(i).getName(), f, frc);
			Rectangle2D textBounds = tl.getBounds();
			
			int radius = Math.max((int) (textBounds.getWidth() / 2) + 8, 40);
			
			g.setColor(Color.WHITE);
			g.fillOval((int) posX - radius, (int) posY - 40, 2 * radius, 80);
			
			if (selected)
				g.setColor(new Color(0, 150, 180));
			else
				g.setColor(Color.BLACK);
			g.drawOval((int) posX - radius, (int) posY - 40, 2 * radius, 80);
			if (attributes.get(i).isKeyAttribute())
				g.fillArc((int) posX - radius, (int) posY - 40, 2 * radius, 80, 180, 180);
				
			tl.draw(g, (int) (posX - textBounds.getWidth() / 2), posY + (attributes.get(i).isKeyAttribute() ? -2 : 3));
		}
		
		g.setColor(Color.WHITE);
		g.fill(bounds);
		
		if (selected)
			g.setColor(new Color(0, 150, 180));
		else
			g.setColor(Color.BLACK);
			
		g.draw(bounds);
		if (weak)
		{
			Rectangle innerRect = new Rectangle(bounds.x + 5, bounds.y + 5, bounds.width - 10, bounds.height - 10);
			g.draw(innerRect);
		}
		FontRenderContext frc = g.getFontRenderContext();
		Font f = new Font("Helvetica", Font.PLAIN, 18);
		TextLayout tl = new TextLayout(name, f, frc);
		Rectangle2D textBounds = tl.getBounds();
		tl.draw(g, (int) (bounds.x + bounds.width / 2 - textBounds.getWidth() / 2), bounds.y + bounds.height / 2 + 7);
	}
	
	public void setAbstract(boolean aFlag)
	{
		abstractEntity = aFlag;
	}
	
	public void setAggregatedEntity(Entity aggregatedEntity)
	{
		this.aggregatedEntity = aggregatedEntity;
	}
	
	public void setParentEntity(Entity e)
	{
		parentEntity = e;
	}
}
