

import java.awt.Graphics2D;

public class DescriptionBox extends ERObject
{
	private static final long serialVersionUID = 1L;
	
	private String	descriptionText;
	private String	descriptionTitle;
	
	public DescriptionBox()
	{
		descriptionText = "Beschreibung";
		descriptionTitle = "Titel";
	}
	
	public String getText()
	{
		return descriptionText;
	}
	
	public String getTitle()
	{
		return descriptionTitle;
	}
	
	@Override
	public void paint(Graphics2D g)
	{
	
	}
	
	public void setText(String text)
	{
		descriptionText = text;
	}
	
	public void setTitle(String title)
	{
		descriptionTitle = title;
	}
	
	public void setWidth(int width)
	{
	
	}
	
}
