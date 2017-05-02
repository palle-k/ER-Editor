
/**
  * EReditor
  * EntityChangeEvent.java
  * Created by Palle on 30.05.2014
  * Copyright (c) 2014 - 2017 Palle.
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

import java.awt.Point;
import java.util.ArrayList;

public class EntityChangeEvent extends ERChangeEvent
{
	
	public static final String	CHANGE_INHERITS			= "change_inheritance";
	public static final String	CHANGE_AGGREGATES_TO	= "change_aggregation";
	public static final String	CHANGE_ATTRIBUTE_ADD	= "change_add_attribute";
	public static final String	CHANGE_ATTRIBUTE_REMOVE	= "change_remove_attribute";
	public static final String	CHANGE_ATTRIBUTE_RENAME	= "change_rename_attribute";
	public static final String	CHANGE_ATTRIBUTE_IS_KEY	= "change_key_attribute";
	
	public EntityChangeEvent(Object source, String property, Object before, Object after)
	{
		super(source, property, before, after);
	}
	
	@SuppressWarnings("unchecked")
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
		else if (propertyName.equals(CHANGE_NAME))
			((ERObject) source).name = (String) afterValue;
		else if (propertyName.equals(CHANGE_WEAK))
			((ERObject) source).weak = (Boolean) afterValue;
		else if (propertyName.equals(CHANGE_ADD))
			((ArrayList<Entity>) source).add((Entity) afterValue);
		else if (propertyName.equals(CHANGE_PASTE))
		{
			ArrayList<Entity> entities = (ArrayList<Entity>) source;
			ArrayList<Entity> pastedEntities = (ArrayList<Entity>) afterValue;
			for (Entity e : pastedEntities)
				entities.add(e);
		}
		else if (propertyName.equals(CHANGE_DELETE))
			((ArrayList<Entity>) source).remove(beforeValue);
		else if (propertyName.equals(CHANGE_DELETE_MULTIPLE))
		{
			Entity[] entities = (Entity[]) beforeValue;
			for (Entity e : entities)
				((ArrayList<Entity>) source).remove(e);
		}
		else if (propertyName.equals(CHANGE_INHERITS))
			((Entity) source).setParentEntity((Entity) afterValue);
		else if (propertyName.equals(CHANGE_AGGREGATES_TO))
			((Entity) source).setAggregatedEntity((Entity) afterValue);
		else if (propertyName.equals(CHANGE_ATTRIBUTE_ADD))
			((Entity) source).attributes.add((Attribute) afterValue);
		else if (propertyName.equals(CHANGE_ATTRIBUTE_REMOVE))
			((Entity) source).attributes.remove(beforeValue);
		else if (propertyName.equals(CHANGE_ATTRIBUTE_RENAME))
			((Attribute) source).setName((String) afterValue);
		else if (propertyName.equals(CHANGE_ATTRIBUTE_IS_KEY))
			((Attribute) source).setKeyAttribute((Boolean) afterValue);
			
	}
	
	@SuppressWarnings("unchecked")
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
		else if (propertyName.equals(CHANGE_NAME))
			((ERObject) source).name = (String) beforeValue;
		else if (propertyName.equals(CHANGE_WEAK))
			((ERObject) source).weak = (Boolean) beforeValue;
		else if (propertyName.equals(CHANGE_ADD))
			((ArrayList<Entity>) source).remove(afterValue);
		else if (propertyName.equals(CHANGE_PASTE))
		{
			ArrayList<Entity> entities = (ArrayList<Entity>) source;
			ArrayList<Entity> pastedEntities = (ArrayList<Entity>) afterValue;
			while (pastedEntities.size() > 0)
			{
				entities.remove(pastedEntities.get(0));
				pastedEntities.remove(0);
			}
		}
		else if (propertyName.equals(CHANGE_DELETE))
			((ArrayList<Entity>) source).add((Entity) beforeValue);
		else if (propertyName.equals(CHANGE_DELETE_MULTIPLE))
		{
			Entity[] entities = (Entity[]) beforeValue;
			for (Entity e : entities)
				((ArrayList<Entity>) source).add(e);
		}
		else if (propertyName.equals(CHANGE_INHERITS))
			((Entity) source).setParentEntity((Entity) beforeValue);
		else if (propertyName.equals(CHANGE_AGGREGATES_TO))
			((Entity) source).setAggregatedEntity((Entity) beforeValue);
		else if (propertyName.equals(CHANGE_ATTRIBUTE_ADD))
			((Entity) source).attributes.remove(afterValue);
		else if (propertyName.equals(CHANGE_ATTRIBUTE_REMOVE))
			((Entity) source).attributes.add((Attribute) beforeValue);
		else if (propertyName.equals(CHANGE_ATTRIBUTE_RENAME))
			((Attribute) source).setName((String) beforeValue);
		else if (propertyName.equals(CHANGE_ATTRIBUTE_IS_KEY))
			((Attribute) source).setKeyAttribute((Boolean) beforeValue);
			
	}
}
