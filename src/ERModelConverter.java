

import java.util.ArrayList;
import java.util.Stack;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class ERModelConverter
{
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
		
		JScrollPane resultPane = new JScrollPane(resultArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		frame.add(resultPane);
		frame.setVisible(true);
	}
	
	public Stack<ReturnData> traceRelationship(Relationship r)
	{
		Stack<ReturnData> returnStack = new Stack<ReturnData>();
		if (!r.getFirstEntityToMany() && !r.getSecondEntityToMany())
		{
			ReturnData rData = new ReturnData();
			// 1:1 relationship
			if (!transformedEntities.contains(r.getFirstEntity()))
			{
				for (Attribute a : r.getFirstEntity().attributes)
					rData.addAttribute((a.isKeyAttribute() ? "pk_" : "") + a.getName());
				rData.addTableName(r.getFirstEntity().getName());
				transformedEntities.add(r.getFirstEntity());
			}
			if (!transformedEntities.contains(r.getSecondEntity()))
			{
				for (Attribute a : r.getSecondEntity().attributes)
					rData.addAttribute((a.isKeyAttribute() ? "pk_" : "") + a.getName());
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
								for (Attribute a : r2.getFirstEntity().attributes)
									if (a.isKeyAttribute())
										rData.addAttribute("fk_" + r2.getFirstEntity().getName() + "_" + r2.getName().replaceAll(" ", "_")
												+ "_" + r2.getSecondEntity().getName() + "_" + a.getName());
												
							}
							else if (r2.getSecondEntity() == r.getSecondEntity()
									&& (r2.getSecondEntityToMany() && !r2.getFirstEntityToMany()))
							{
								// 1:n relationship
								for (Attribute a : r2.getSecondEntity().attributes)
									if (a.isKeyAttribute())
										rData.addAttribute("fk_" + r2.getSecondEntity().getName() + "_" + r2.getName().replaceAll(" ", "_")
												+ "_" + r2.getFirstEntity().getName() + "_" + a.getName());
												
							}
							// wenn n:m: ignorieren
						}
					}
				}
			}
			returnStack.push(rData);
		}
		else if (!r.getFirstEntityToMany() && r.getSecondEntityToMany())
		{
			// 1:n relationship
			if (!transformedEntities.contains(r.getSecondEntity()))
			{
				ReturnData rData = new ReturnData();
				
				for (Attribute a : r.getSecondEntity().attributes)
					rData.addAttribute((a.isKeyAttribute() ? "pk_" : "") + a.getName());
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
							for (Attribute a : r2.getSecondEntity().attributes)
								if (a.isKeyAttribute())
									rData.addAttribute("fk_" + r2.getSecondEntity().getName() + "_" + r2.getName().replaceAll(" ", "_")
											+ "_" + r2.getFirstEntity().getName() + "_" + a.getName());
											
						}
						else if (r2.getSecondEntity() == r.getSecondEntity() && (!r2.getFirstEntityToMany() && r2.getSecondEntityToMany()))
						{
							// 1:n relationship
							for (Attribute a : r2.getFirstEntity().attributes)
								if (a.isKeyAttribute())
									rData.addAttribute("fk_" + r2.getFirstEntity().getName() + "_" + r2.getName().replaceAll(" ", "_") + "_"
											+ r2.getSecondEntity().getName() + "_" + a.getName());
											
						}
						// wenn n:m: ignorieren
					}
					
				}
				returnStack.push(rData);
			}
		}
		else if (r.getFirstEntityToMany() && !r.getSecondEntityToMany())
		{
			// 1:n relationship
			if (!transformedEntities.contains(r.getFirstEntity()))
			{
				ReturnData rData = new ReturnData();
				
				for (Attribute a : r.getFirstEntity().attributes)
					rData.addAttribute((a.isKeyAttribute() ? "pk_" : "") + a.getName());
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
							for (Attribute a : r2.getSecondEntity().attributes)
								if (a.isKeyAttribute())
									rData.addAttribute("fk_" + r2.getSecondEntity().getName() + "_" + r2.getName().replaceAll(" ", "_")
											+ "_" + r2.getFirstEntity().getName() + "_" + a.getName());
											
						}
						else if (r2.getSecondEntity() == r.getFirstEntity() && (!r2.getFirstEntityToMany() && r2.getSecondEntityToMany()))
						{
							// 1:n relationship
							for (Attribute a : r2.getFirstEntity().attributes)
								if (a.isKeyAttribute())
									rData.addAttribute("fk_" + r2.getFirstEntity().getName() + "_" + r2.getName().replaceAll(" ", "_") + "_"
											+ "_" + r2.getSecondEntity().getName() + a.getName());
											
						}
						// wenn n:m: ignorieren
					}
				}
				returnStack.push(rData);
			}
		}
		else if (r.getFirstEntityToMany() && r.getSecondEntityToMany())
		{
			ReturnData rData = new ReturnData();
			rData.addTableName(r.getFirstEntity().getName());
			rData.addTableName(r.getSecondEntity().getName());
			for (Attribute a : r.getFirstEntity().attributes)
				if (a.isKeyAttribute())
					rData.addAttribute("fk_" + r.getFirstEntity().getName() + "_" + r.getName().replaceAll(" ", "_") + "_"
							+ r.getSecondEntity().getName() + "_" + a.getName());
			for (Attribute a : r.getSecondEntity().attributes)
				if (a.isKeyAttribute())
					rData.addAttribute("fk_" + r.getSecondEntity().getName() + "_" + r.getName().replaceAll(" ", "_") + "_"
							+ r.getFirstEntity().getName() + "_" + a.getName());
			returnStack.push(rData);
		}
		return returnStack;
	}
}
