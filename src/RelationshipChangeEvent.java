
/**
  * EReditor
  * RelationshipChangeEvent.java
  * Created by Palle on 30.05.2014
  * Copyright (c) 2014 - 2017 Palle
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

public class RelationshipChangeEvent extends ERChangeEvent
{
	public static final String	CHANGE_FIRST_ENTITY				= "change_entity_one";
	public static final String	CHANGE_SECOND_ENTITY			= "change_entity_two";
	public static final String	CHANGE_FIRST_ENTITY_TO_MANY		= "change_cardinality_first";
	public static final String	CHANGE_SECOND_ENTITY_TO_MANY	= "change_cardinality_second";
	
	public RelationshipChangeEvent(Object source, String property, Object before, Object after)
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
			((ArrayList<Relationship>) source).add((Relationship) afterValue);
		else if (propertyName.equals(CHANGE_PASTE))
		{
			ArrayList<Relationship> relationships = (ArrayList<Relationship>) source;
			ArrayList<Relationship> pastedRelationships = (ArrayList<Relationship>) afterValue;
			for (Relationship r : pastedRelationships)
				relationships.add(r);
		}
		else if (propertyName.equals(CHANGE_DELETE))
			((ArrayList<Relationship>) source).remove(beforeValue);
		else if (propertyName.equals(CHANGE_DELETE_MULTIPLE))
		{
			Relationship[] relationships = (Relationship[]) beforeValue;
			for (Relationship r : relationships)
				((ArrayList<Relationship>) source).remove(r);
		}
		else if (propertyName.equals(CHANGE_FIRST_ENTITY))
			((Relationship) source).setFirstEntity((Entity) afterValue);
		else if (propertyName.equals(CHANGE_SECOND_ENTITY))
			((Relationship) source).setSecondEntity((Entity) afterValue);
		else if (propertyName.equals(CHANGE_FIRST_ENTITY_TO_MANY))
			((Relationship) source).setFirstEntityToMany((Boolean) afterValue);
		else if (propertyName.equals(CHANGE_SECOND_ENTITY_TO_MANY))
			((Relationship) source).setSecondEntityToMany((Boolean) afterValue);
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
			Point[] positions = (Point[]) afterValue;
			for (int i = 0; i < objects.length; i++)
			{
				ERObject obj = objects[i];
				Point p = positions[i];
				obj.bounds.x = p.x;
				obj.bounds.y = p.y;
			}
		}
		if (propertyName.equals(CHANGE_NAME))
			((ERObject) source).name = (String) beforeValue;
		else if (propertyName.equals(CHANGE_WEAK))
			((ERObject) source).weak = (Boolean) beforeValue;
		else if (propertyName.equals(CHANGE_ADD))
			((ArrayList<Relationship>) source).remove(afterValue);
		else if (propertyName.equals(CHANGE_PASTE))
		{
			ArrayList<Relationship> relationships = (ArrayList<Relationship>) source;
			ArrayList<Relationship> pastedRelationships = (ArrayList<Relationship>) afterValue;
			while (pastedRelationships.size() > 0)
			{
				relationships.remove(pastedRelationships.get(0));
				pastedRelationships.remove(0);
			}
		}
		else if (propertyName.equals(CHANGE_DELETE))
			((ArrayList<Relationship>) source).add((Relationship) beforeValue);
		else if (propertyName.equals(CHANGE_DELETE_MULTIPLE))
		{
			Relationship[] relationships = (Relationship[]) beforeValue;
			for (Relationship r : relationships)
				((ArrayList<Relationship>) source).add(r);
		}
		else if (propertyName.equals(CHANGE_FIRST_ENTITY))
			((Relationship) source).setFirstEntity((Entity) beforeValue);
		else if (propertyName.equals(CHANGE_SECOND_ENTITY))
			((Relationship) source).setSecondEntity((Entity) beforeValue);
		else if (propertyName.equals(CHANGE_FIRST_ENTITY_TO_MANY))
			((Relationship) source).setFirstEntityToMany((Boolean) beforeValue);
		else if (propertyName.equals(CHANGE_SECOND_ENTITY_TO_MANY))
			((Relationship) source).setSecondEntityToMany((Boolean) beforeValue);
	}
}
