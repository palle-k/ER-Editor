
/**
  * EReditor
  * ERModelConverter.java
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

import java.util.ArrayList;
import java.util.Stack;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

public class ERModelConverter
{
	private class ExtendedAttribute
	{
		private final Attribute		attribute;
		private final Relationship	relationship;
		
		private ExtendedAttribute(Attribute attribute, Relationship relationship)
		{
			this.attribute = attribute;
			this.relationship = relationship;
		}
	}
	
	private class ReturnData
	{
		private String	tableName;
		private String	attributes;
		
		private ReturnData()
		{
			tableName = "";
			attributes = "";
		}
		
		public void addAttribute(String name)
		{
			if (attributes.length() != 0)
				attributes += ", ";
			attributes += name;
		}
		
		public void addTableName(String name)
		{
			tableName += name;
		}
		
		public String getAttributes()
		{
			return attributes;
		}
		
		public String getTableName()
		{
			return tableName;
		}
		
		public boolean isEmpty()
		{
			return tableName.length() == 0 && attributes.length() == 0;
		}
		
		@Override
		public String toString()
		{
			return tableName + "(" + attributes + ")";
		}
		
		public void union(ReturnData r2)
		{
			addAttribute(r2.getAttributes());
			addTableName(r2.getTableName());
		}
	}
	
	private final ERModel		model;
	private ArrayList<String>	result;
	
	private final ArrayList<Entity> transformedEntities;
	
	public ERModelConverter(ERModel model)
	{
		this.model = model;
		transformedEntities = new ArrayList<Entity>();
	}
	
	public void convert()
	{
		result = new ArrayList<String>();
		for (Relationship r : model.relationships)
		{
			ReturnData rData = new ReturnData();
			Stack<ReturnData> returned = traceRelationship(r);
			while (!returned.isEmpty())
				rData.union(returned.pop());
			if (!rData.isEmpty())
				result.add(rData.toString());
		}
		for (Entity e : model.entities)
		{
			if (!transformedEntities.contains(e))
			{
				ReturnData rData = new ReturnData();
				rData.addTableName(e.getName());
				for (Attribute a : e.attributes)
					rData.addAttribute((a.isKeyAttribute() ? "pk_" : "") + a.getName());
				result.add(rData.toString());
			}
		}
	}
	
	public void display()
	{
		JFrame frame = new JFrame();
		frame.setBounds(400, 300, 600, 400);
		JTextArea resultArea = new JTextArea();
		resultArea.setEditable(false);
		for (String s : result)
		{
			resultArea.setText(resultArea.getText() + ((resultArea.getText().length() > 0) ? "\n" : "") + s);
		}
		
		JScrollPane resultPane = new JScrollPane(resultArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		resultPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		frame.add(resultPane);
		frame.setVisible(true);
	}
	
	public Stack<ReturnData> traceRelationship(Relationship r)
	{
		// TODO Rewrite this algorithm:
		// Multiple Methods:
		// 1. Get all Attributes of an Entity (and Attributes from connected Relationships (except a
		// specified list of Entities))
		// 2. Get all Key attributes of an Entity (and Attributes from connected Relationships,
		// Aggregations and Parent Entities
		// (except a specified list of Entities))
		// Loop over all Entities
		// Memoize converted Entities
		// If Entity not already converted:
		// Get all Attributes
		// Get all Relationships
		// Get all Attributes from Relationships
		Stack<ReturnData> returnStack = new Stack<ReturnData>();
		if (!r.getFirstEntityToMany() && !r.getSecondEntityToMany())
		{
			ReturnData rData = new ReturnData();
			// 1:1 relationship
			if (!transformedEntities.contains(r.getFirstEntity()))
			{
				Entity entity = r.getFirstEntity();
				while (entity != null)
				{
					for (Attribute a : entity.attributes)
						rData.addAttribute((a.isKeyAttribute() ? "pk_" : "") + a.getName());
					if (entity.hasAggregatedEntity())
					{
						for (Attribute a : entity.getAggregatedEntity().attributes)
							if (a.isKeyAttribute())
								rData.addAttribute("pk_part_of_" + entity.getAggregatedEntity().getName() + "_" + a.getName());
					}
					entity = entity.getParent();
				}
				rData.addTableName(r.getFirstEntity().getName());
				transformedEntities.add(r.getFirstEntity());
			}
			if (!transformedEntities.contains(r.getSecondEntity()))
			{
				Entity entity = r.getSecondEntity();
				while (entity != null)
				{
					for (Attribute a : entity.attributes)
						rData.addAttribute((a.isKeyAttribute() ? "pk_" : "") + a.getName());
					if (entity.hasAggregatedEntity())
					{
						for (Attribute a : entity.getAggregatedEntity().attributes)
							if (a.isKeyAttribute())
								rData.addAttribute("pk_part_of_" + entity.getAggregatedEntity().getName() + "_" + a.getName());
					}
					entity = entity.getParent();
				}
				rData.addTableName(r.getSecondEntity().getName());
				transformedEntities.add(r.getSecondEntity());
			}
			
			for (Relationship r2 : model.relationships)
			{
				if (!transformedEntities.contains(r2.getFirstEntity()) || !transformedEntities.contains(r2.getSecondEntity()))
				{
					// BUG ??? Falls eine der beiden 1:1 verbundenen Entitaeten noch nicht
					// umgewandelt wurde, wird sie trotzdem ignoriert
					if (model.relationships.indexOf(r2) > model.relationships.indexOf(r)
							&& !transformedEntities.contains(r2.getFirstEntity()) && !transformedEntities.contains(r2.getSecondEntity()))
					{
						if (r2.getFirstEntity() == r.getFirstEntity() || r2.getFirstEntity() == r.getSecondEntity()
								|| r2.getSecondEntity() == r.getFirstEntity() || r2.getSecondEntity() == r.getSecondEntity())
						{
							if (!r2.getFirstEntityToMany() && !r2.getSecondEntityToMany())
							{
								// 1:1 relationship
								rData.union(traceRelationship(r2).pop());
							}
							else if (r2.getFirstEntity() == r.getFirstEntity()
									&& (r2.getFirstEntityToMany() && !r2.getSecondEntityToMany()))
							{
								// 1:n relationship
								Entity entity = r2.getSecondEntity();
								while (entity != null)
								{
									for (Attribute a : entity.attributes)
										if (a.isKeyAttribute())
											rData.addAttribute(((r2.isWeak() && r2.getSecondEntity().isWeak()) ? "pk_" : "fk_")
													+ r2.getFirstEntity().getName() + "_" + r2.getName().replaceAll(" ", "_") + "_"
													+ r2.getSecondEntity().getName() + "_" + a.getName());
									if (entity.hasAggregatedEntity())
									{
										for (Attribute a : entity.getAggregatedEntity().attributes)
											if (a.isKeyAttribute())
												rData.addAttribute(((r2.isWeak() && r2.getSecondEntity().isWeak()) ? "pk_" : "fk_")
														+ "part_of_" + entity.getAggregatedEntity().getName() + "_" + a.getName());
									}
									entity = entity.getParent();
								}
								
							}
							else if (r2.getSecondEntity() == r.getSecondEntity()
									&& (r2.getSecondEntityToMany() && !r2.getFirstEntityToMany()))
							{
								// 1:n relationship
								Entity entity = r2.getFirstEntity();
								while (entity != null)
								{
									for (Attribute a : entity.attributes)
										if (a.isKeyAttribute())
											rData.addAttribute(((r2.isWeak() && r2.getFirstEntity().isWeak()) ? "pk_" : "fk_")
													+ r2.getFirstEntity().getName() + "_" + r2.getName().replaceAll(" ", "_") + "_"
													+ r2.getSecondEntity().getName() + "_" + a.getName());
									if (entity.hasAggregatedEntity())
									{
										for (Attribute a : entity.getAggregatedEntity().attributes)
											if (a.isKeyAttribute())
												rData.addAttribute(((r2.isWeak() && r2.getFirstEntity().isWeak()) ? "pk_" : "fk_")
														+ "part_of_" + entity.getAggregatedEntity().getName() + "_" + a.getName());
									}
									entity = entity.getParent();
								}
							}
							// wenn n:m: ignorieren
						}
					}
				}
			}
			returnStack.push(rData);
		}
		else if (!r.getFirstEntityToMany() && !r.getFirstEntity().isWeak() && r.getSecondEntityToMany())
		{
			// 1:n relationship
			if (!transformedEntities.contains(r.getSecondEntity()))
			{
				ReturnData rData = new ReturnData();
				
				Entity baseEntity = r.getSecondEntity();
				while (baseEntity != null)
				{
					for (Attribute a : baseEntity.attributes)
						rData.addAttribute((a.isKeyAttribute() ? "pk_" : "") + a.getName());
					if (baseEntity.hasAggregatedEntity())
					{
						for (Attribute a : baseEntity.getAggregatedEntity().attributes)
							if (a.isKeyAttribute())
								rData.addAttribute("pk_part_of_" + baseEntity.getAggregatedEntity().getName() + "_" + a.getName());
					}
					baseEntity = baseEntity.getParent();
				}
				rData.addTableName(r.getSecondEntity().getName());
				transformedEntities.add(r.getSecondEntity());
				
				for (Relationship r2 : model.relationships)
				{
					if (r2.getFirstEntity() == r.getSecondEntity() || r2.getSecondEntity() == r.getSecondEntity())
					{
						if (!r2.getFirstEntityToMany() && !r2.getSecondEntityToMany()
								&& (model.relationships.indexOf(r2) > model.relationships.indexOf(r)))
						{
							// 1:1 relationship
							rData.union(traceRelationship(r2).pop());
						}
						else if (r2.getFirstEntity() == r.getSecondEntity() && (r2.getFirstEntityToMany() && !r2.getSecondEntityToMany()))
						{
							// 1:n relationship
							Entity entity = r2.getSecondEntity();
							while (entity != null)
							{
								for (Attribute a : entity.attributes)
									if (a.isKeyAttribute())
										rData.addAttribute(((r2.isWeak() && r2.getFirstEntity().isWeak()) ? "pk_" : "fk_")
												+ r2.getFirstEntity().getName() + "_" + r2.getName().replaceAll(" ", "_") + "_"
												+ r2.getSecondEntity().getName() + "_" + a.getName());
								if (entity.hasAggregatedEntity())
								{
									for (Attribute a : entity.getAggregatedEntity().attributes)
										if (a.isKeyAttribute())
											rData.addAttribute(((r2.isWeak() && r2.getFirstEntity().isWeak()) ? "pk_" : "fk_") + "part_of_"
													+ entity.getAggregatedEntity().getName() + "_" + a.getName());
								}
								entity = entity.getParent();
							}
						}
						else if (r2.getSecondEntity() == r.getSecondEntity() && (!r2.getFirstEntityToMany() && r2.getSecondEntityToMany()))
						{
							// 1:n relationship
							Entity entity = r2.getFirstEntity();
							while (entity != null)
							{
								for (Attribute a : entity.attributes)
									if (a.isKeyAttribute())
										rData.addAttribute(((r2.isWeak() && r2.getSecondEntity().isWeak()) ? "pk_" : "fk_")
												+ r2.getFirstEntity().getName() + "_" + r2.getName().replaceAll(" ", "_") + "_"
												+ r2.getSecondEntity().getName() + "_" + a.getName());
								if (entity.hasAggregatedEntity())
								{
									for (Attribute a : entity.getAggregatedEntity().attributes)
										if (a.isKeyAttribute())
											rData.addAttribute(((r2.isWeak() && r2.getSecondEntity().isWeak()) ? "pk_" : "fk_") + "part_of_"
													+ entity.getAggregatedEntity().getName() + "_" + a.getName());
								}
								entity = entity.getParent();
							}
						}
						// wenn n:m: ignorieren
					}
					
				}
				returnStack.push(rData);
			}
		}
		else if (r.getFirstEntityToMany() && !r.getSecondEntityToMany() && !r.getSecondEntity().isWeak())
		{
			// 1:n relationship
			if (!transformedEntities.contains(r.getFirstEntity()))
			{
				ReturnData rData = new ReturnData();
				
				Entity baseEntity = r.getFirstEntity();
				while (baseEntity != null)
				{
					for (Attribute a : baseEntity.attributes)
						rData.addAttribute((a.isKeyAttribute() ? "pk_" : "") + a.getName());
					baseEntity = baseEntity.getParent();
				}
				rData.addTableName(r.getFirstEntity().getName());
				transformedEntities.add(r.getFirstEntity());
				
				for (Relationship r2 : model.relationships)
				{
					if (r2.getFirstEntity() == r.getFirstEntity() || r2.getSecondEntity() == r.getFirstEntity())
					{
						if (!r2.getFirstEntityToMany() && !r2.getSecondEntityToMany()
								&& (model.relationships.indexOf(r2) > model.relationships.indexOf(r)))
						{
							// 1:1 relationship
							rData.union(traceRelationship(r2).pop());
						}
						else if (r2.getFirstEntity() == r.getFirstEntity() && (r2.getFirstEntityToMany() && !r2.getSecondEntityToMany()))
						{
							// 1:n relationship
							Entity entity = r2.getSecondEntity();
							while (entity != null)
							{
								for (Attribute a : entity.attributes)
									if (a.isKeyAttribute())
										rData.addAttribute(((r2.isWeak() && r2.getFirstEntity().isWeak()) ? "pk_" : "fk_")
												+ r2.getFirstEntity().getName() + "_" + r2.getName().replaceAll(" ", "_") + "_"
												+ r2.getSecondEntity().getName() + "_" + a.getName());
								if (entity.hasAggregatedEntity())
								{
									for (Attribute a : entity.getAggregatedEntity().attributes)
										if (a.isKeyAttribute())
											rData.addAttribute(((r2.isWeak() && r2.getFirstEntity().isWeak()) ? "pk_" : "fk_") + "part_of_"
													+ entity.getAggregatedEntity().getName() + "_" + a.getName());
								}
								entity = entity.getParent();
							}
							
						}
						else if (r2.getSecondEntity() == r.getFirstEntity() && (!r2.getFirstEntityToMany() && r2.getSecondEntityToMany()))
						{
							// 1:n relationship
							Entity entity = r2.getFirstEntity();
							while (entity != null)
							{
								for (Attribute a : entity.attributes)
									if (a.isKeyAttribute())
										rData.addAttribute(((r2.isWeak() && r2.getSecondEntity().isWeak()) ? "pk_" : "fk_")
												+ r2.getFirstEntity().getName() + "_" + r2.getName().replaceAll(" ", "_") + "_"
												+ r2.getSecondEntity().getName() + "_" + a.getName());
								if (entity.hasAggregatedEntity())
								{
									for (Attribute a : entity.getAggregatedEntity().attributes)
										if (a.isKeyAttribute())
											rData.addAttribute(((r2.isWeak() && r2.getSecondEntity().isWeak()) ? "pk_" : "fk_") + "part_of_"
													+ entity.getAggregatedEntity().getName() + "_" + a.getName());
								}
								entity = entity.getParent();
							}
						}
						// wenn n:m: ignorieren
					}
				}
				returnStack.push(rData);
			}
		}
		else
		{
			ReturnData rData = new ReturnData();
			rData.addTableName(r.getFirstEntity().getName());
			rData.addTableName(r.getSecondEntity().getName());
			for (Attribute a : r.getFirstEntity().attributes)
				if (a.isKeyAttribute())
					rData.addAttribute("pk_" + r.getFirstEntity().getName() + "_" + r.getName().replaceAll(" ", "_") + "_"
							+ r.getSecondEntity().getName() + "_" + a.getName());
			for (Attribute a : r.getSecondEntity().attributes)
				if (a.isKeyAttribute())
					rData.addAttribute("pk_" + r.getFirstEntity().getName() + "_" + r.getName().replaceAll(" ", "_") + "_"
							+ r.getSecondEntity().getName() + "_" + a.getName());
			returnStack.push(rData);
		}
		return returnStack;
	}
	
	private ArrayList<Relationship> getAllRelationshipsForEntity(Entity forEntity)
	{
		ArrayList<Relationship> rels = new ArrayList<Relationship>();
		for (Relationship r : model.relationships)
			if (r.getFirstEntity() == forEntity || r.getSecondEntity() == forEntity)
				rels.add(r);
		return rels;
	}
	
	private ArrayList<Entity> getEntitiesWithIdentifyingKeysOfEntity(Entity entity, ArrayList<Entity> addToList)
	{
		ArrayList<Entity> entities = addToList;
		for (Relationship r : model.relationships)
		{
			if (r.getFirstEntity() != r.getSecondEntity())
				continue;
			if (r.getFirstEntity() == entity && !entities.contains(r.getSecondEntity()))
			{
				// 1:1 Relationship
				if (!(r.getFirstEntityToMany() || r.getSecondEntityToMany()))
				{
					entities.add(r.getSecondEntity());
					entities.addAll(getEntitiesWithIdentifyingKeysOfEntity(r.getSecondEntity(), entities));
				}
				// 1:N non-weak Relationship
				else if (!r.getFirstEntityToMany() && r.getSecondEntityToMany() && !(entity.isWeak() && r.isWeak()))
				{
					entities.add(r.getFirstEntity());
					entities.addAll(getEntitiesWithIdentifyingKeysOfEntity(r.getSecondEntity(), entities));
				}
			}
			else if (r.getSecondEntity() == entity && !entities.contains(r.getFirstEntity()))
			{
				// 1:1 Relationship
				if (!(r.getFirstEntityToMany() || r.getSecondEntityToMany()))
				{
					entities.add(r.getFirstEntity());
					entities.addAll(getEntitiesWithIdentifyingKeysOfEntity(r.getFirstEntity(), entities));
				}
				// 1:N non-weak Relationship
				else if (!r.getSecondEntityToMany() && r.getFirstEntityToMany() && !(entity.isWeak() && r.isWeak()))
				{
					entities.add(r.getFirstEntity());
					entities.addAll(getEntitiesWithIdentifyingKeysOfEntity(r.getSecondEntity(), entities));
				}
			}
		}
		removeDuplicates(entities);
		return entities;
	}
	
	private ArrayList<Entity> getEntitiesWithKeysForEntity(Entity entity)
	{
		return getEntitiesWithKeysForEntity(entity, false);
	}
	
	private ArrayList<Entity> getEntitiesWithKeysForEntity(Entity entity, boolean identifyingAttributesOnly)
	{
		ArrayList<Entity> entities = new ArrayList<Entity>();
		
		ArrayList<Relationship> relationships = new ArrayList<Relationship>();
		for (Relationship r : model.relationships)
			if (r.getFirstEntity() == entity)
				relationships.add(r);
			else if (r.getSecondEntity() == entity)
				relationships.add(r);
				
		return entities;
	}
	
	private ArrayList<Attribute> getKeyAttributesForEntity(Entity forEntity)
	{
		ArrayList<Attribute> keys = new ArrayList<Attribute>();
		for (Attribute attr : forEntity.attributes)
			if (attr.isKeyAttribute())
				keys.add(attr);
		return keys;
	}
	
	private void removeDuplicates(ArrayList<?> list)
	{
		for (int i = 0; i < list.size() - 1; i++)
		{
			Object o = list.get(i);
			int lastIndex;
			if (list.indexOf(o) != (lastIndex = list.lastIndexOf(o)))
				list.remove(lastIndex);
		}
	}
}
