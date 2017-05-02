import java.awt.Point;

/**
  * EReditor
  * DescriptionBoxChangeEvent.java
  * Created by Palle Klewitz on 04.11.2015
  * Copyright (c) 2015 - 2017 Palle Klewitz. All rights reserved.
  */

public class DescriptionBoxChangeEvent extends ERChangeEvent
{
	
	public static final String CHANGE_DESCRIPTION = "change_description";
	
	public DescriptionBoxChangeEvent(Object source, String property, Object before, Object after)
	{
		super(source, property, before, after);
	}
	
	@Override
	public void redo()
	{
		if (propertyName.equals(CHANGE_POSITION))
		{
			((ERObject) source).bounds.x = ((Point) afterValue).x;
			((ERObject) source).bounds.y = ((Point) afterValue).y;
		}
		else if (propertyName.equals(CHANGE_POSITION_MULTIPLE))
		{
			ERObject[] objects = (ERObject[]) source;
			Point[] positions = (Point[]) afterValue;
			for (int i = 0; i < objects.length; i++)
			{
				ERObject obj = objects[i];
				Point p = positions[i];
				obj.bounds.x = p.x;
				obj.bounds.y = p.y;
			}
		}
	}
	
	@Override
	public void undo()
	{
		if (propertyName.equals(CHANGE_POSITION))
		{
			((ERObject) source).bounds.x = ((Point) beforeValue).x;
			((ERObject) source).bounds.y = ((Point) beforeValue).y;
		}
		else if (propertyName.equals(CHANGE_POSITION_MULTIPLE))
		{
			ERObject[] objects = (ERObject[]) source;
			Point[] positions = (Point[]) beforeValue;
			for (int i = 0; i < objects.length; i++)
			{
				ERObject obj = objects[i];
				Point p = positions[i];
				obj.bounds.x = p.x;
				obj.bounds.y = p.y;
			}
		}
	}
	
}
