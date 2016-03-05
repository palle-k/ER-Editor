
/**
  * EReditor
  * DescriptionBox.java
  * Created by Palle on 30.05.2014
  * Copyright (c) 2014 - 2015 Palle. 
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
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;

public class DescriptionBox extends ERObject
{
	private static final long serialVersionUID = 1L;
	
	private String descriptionText;
	
	public DescriptionBox()
	{
		descriptionText = ER_Editor.LOCALIZATION.getString("description_default_text");
		name = ER_Editor.LOCALIZATION.getString("description_default_title");
	}
	
	public String getText()
	{
		return descriptionText;
	}
	
	@Override
	public void paint(Graphics2D g)
	{
		g.setColor(Color.WHITE);

		if (descriptionText.length() != 0)
		{
			String[] lines = descriptionText.split("\n");
			int maxWidth = 0;
			for (String line : lines)
			{
				if (line.length() == 0)
					continue;
				FontRenderContext frc = g.getFontRenderContext();
				Font f = new Font("Helvetica", Font.PLAIN, 14);
				TextLayout tl = new TextLayout(line, f, frc);
				maxWidth = Math.max((int) tl.getBounds().getWidth(), maxWidth);
			}

			FontRenderContext frc = g.getFontRenderContext();
			Font f = new Font("Helvetica", Font.BOLD, 22);
			TextLayout tl = new TextLayout(name, f, frc);
			maxWidth = Math.max((int) tl.getBounds().getWidth(), maxWidth);

			bounds.width = maxWidth + 20;

			int height = name.trim().isEmpty() ? 0 : 24;
			height += lines.length * 18;

			bounds.height = height + 20;
		}

		g.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 10, 10);

		if (selected)
			g.setColor(new Color(0, 150, 180));
		else
			g.setColor(Color.BLACK);

		g.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 10, 10);

		if (!name.trim().isEmpty())
		{
			FontRenderContext frc = g.getFontRenderContext();
			Font f = new Font("Helvetica", Font.BOLD, 22);
			TextLayout tl = new TextLayout(name, f, frc);
			tl.draw(g, bounds.x + 10, (float) (bounds.y + 17 + 11));
		}

		if (descriptionText.length() != 0)
		{
			String[] lines = descriptionText.split("\n");

			int offset;

			if (name.trim().isEmpty())
			{
				offset = 20;
			}
			else
			{
				offset = 48;
			}

			for (int i = 0; i < lines.length; i++)
			{
				String line = lines[i];
				if (line.length() == 0)
					continue;
				FontRenderContext frc = g.getFontRenderContext();
				Font f = new Font("Helvetica", Font.PLAIN, 14);
				TextLayout tl = new TextLayout(line, f, frc);
				tl.draw(g, bounds.x + 10, bounds.y + offset + i * 18);
			}
		}

	}
	
	public void setText(String text)
	{
		descriptionText = text;
	}
	
}
