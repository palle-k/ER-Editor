
/**
  * EReditor
  * ERView.java
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

import javax.swing.JComponent;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ERView extends JComponent implements MouseMotionListener, MouseListener, KeyListener, RepaintRequest, ERModelQuery
{
	
	private static final long serialVersionUID = -7189232076193021142L;
	
	protected ERModel								model;
	private final List<ERSelectionNotifier>	        notifier;
	
	private boolean	dragging;
	private Point	draggingStart;
	private Point	draggingEnd;
	private Point	draggingPrevious;
	private boolean	dragsObject;
	private float	zoom;
	
	private boolean			requestRelationship;
	private boolean			relationshipHasFirstEntity;
	private Relationship	newRelationship;
	
	private Rectangle visibleRect;
	
	private ERChangeHistory history;
	
	public ERView()
	{
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addKeyListener(this);
		draggingStart = new Point();
		draggingEnd = new Point();
		draggingPrevious = new Point();
		model = new ERModel();
		zoom = 1.0f;
		notifier = new ArrayList<>();
	}
	
	public void addEntity()
	{
		Entity e = new Entity();
		e.bounds.x = (int) (visibleRect.getCenterX() / zoom);
		e.bounds.y = (int) (visibleRect.getCenterY() / zoom);
		model.entities.add(e);
		EntityChangeEvent changeEvent = new EntityChangeEvent(model.entities, EntityChangeEvent.CHANGE_ADD, null, e);
		history.pushEvent(changeEvent);
	}

	public void addDescriptionBox()
	{
		DescriptionBox b = new DescriptionBox();
		b.bounds.x = (int) (visibleRect.getCenterX() / zoom);
		b.bounds.y = (int) (visibleRect.getCenterY() / zoom);
		model.descriptions.add(b);
		DescriptionBoxChangeEvent changeEvent = new DescriptionBoxChangeEvent(model.descriptions, DescriptionBoxChangeEvent.CHANGE_ADD, null, b);
		history.pushEvent(changeEvent);
	}

	public void copySelected() throws IOException
	{
		Stack<ERObject> objects = new Stack<>();
		for (Entity e : model.entities)
			if (e.selected)
				objects.add(e);
		for (Relationship r : model.relationships)
			if (r.selected)
				objects.add(r);
		for (DescriptionBox b : model.descriptions)
			if (b.selected)
				objects.add(b);
		ERObject[] objs = objects.toArray(new ERObject[objects.size()]);
		ERSelection selection = new ERSelection(objs);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
	}
	
	public void cutSelected() throws IOException
	{
		copySelected();
		deleteSelected();
	}
	
	public void deleteSelected()
	{
		Stack<Entity> removedEntities = new Stack<>();
		Stack<Relationship> removedRelationships = new Stack<>();
		Stack<DescriptionBox> removedDescriptions = new Stack<>();
		for (int i = 0; i < model.relationships.size(); i++)
		{
			Relationship r = model.relationships.get(i);
			if (r.isSelected() || r.getFirstEntity().isSelected() || r.getSecondEntity().isSelected())
			{
				removedRelationships.push(model.relationships.get(i));
				model.relationships.remove(i);
				i--;
			}
		}
		for (int i = 0; i < model.entities.size(); i++)
		{
			Entity e1 = model.entities.get(i);
			if (e1.isSelected())
			{
				removedEntities.push(model.entities.get(i));
				model.entities.remove(i);
				i--;
			}
		}
		for (int i = 0; i < model.descriptions.size(); i++)
		{
			DescriptionBox b = model.descriptions.get(i);
			if (b.isSelected())
			{
				removedDescriptions.push(model.descriptions.get(i));
				model.descriptions.remove(i);
				i--;
			}
		}
		Entity[] r_entities = removedEntities.toArray(new Entity[removedEntities.size()]);
		Relationship[] r_relationships = removedRelationships.toArray(new Relationship[removedRelationships.size()]);
		DescriptionBox[] r_descriptions = removedDescriptions.toArray(new DescriptionBox[removedDescriptions.size()]);
		if (r_relationships.length > 0)
		{
			RelationshipChangeEvent r_changeEvent = new RelationshipChangeEvent(model.relationships,
					RelationshipChangeEvent.CHANGE_DELETE_MULTIPLE, r_relationships, null);
			history.pushEvent(r_changeEvent);
		}
		if (r_entities.length > 0)
		{
			EntityChangeEvent e_changeEvent = new EntityChangeEvent(model.entities, EntityChangeEvent.CHANGE_DELETE_MULTIPLE, r_entities,
					null);
			history.pushEvent(e_changeEvent);
		}
		if (r_descriptions.length > 0)
		{
			DescriptionBoxChangeEvent b_changeEvent = new DescriptionBoxChangeEvent(model.descriptions, DescriptionBoxChangeEvent.CHANGE_DELETE_MULTIPLE, r_descriptions, null);
			history.pushEvent(b_changeEvent);
		}
		for (ERSelectionNotifier ersn : notifier)
			ersn.didSelectRelationship(null);
	}
	
	public void deselectAll()
	{
		for (Entity e : model.entities)
			e.deselect();
		for (Relationship r : model.relationships)
			r.deselect();
		for (DescriptionBox b : model.descriptions)
			b.deselect();
		for (ERSelectionNotifier ersn : notifier)
			ersn.didSelectRelationship(null);
		for (ERSelectionNotifier ersn : notifier)
			ersn.didSelectEntity(null);
		for (ERSelectionNotifier ersn : notifier)
			ersn.didSelectDescriptionBox(null);
	}
	
	public void expand()
	{
		for (Entity e : model.entities)
		{
			e.bounds.x = (int) (1.25 * e.bounds.x);
			e.bounds.y = (int) (1.25 * e.bounds.y);
		}
		for (Relationship r : model.relationships)
		{
			r.bounds.x = (int) (1.25 * r.bounds.x);
			r.bounds.y = (int) (1.25 * r.bounds.y);
		}
		for (DescriptionBox b : model.descriptions)
		{
			b.bounds.x = (int) (1.25 * b.bounds.x);
			b.bounds.y = (int) (1.25 * b.bounds.y);
		}
	}
	
	@Override
	public List<Entity> getAllEntities()
	{
		return model.entities;
	}
	
	@Override
	public List<Relationship> getAllRelationships()
	{
		return model.relationships;
	}
	
	public void shrink()
	{
		for (Entity e : model.entities)
		{
			e.bounds.x = (int) (0.8 * e.bounds.x);
			e.bounds.y = (int) (0.8 * e.bounds.y);
		}
		for (Relationship r : model.relationships)
		{
			r.bounds.x = (int) (0.8 * r.bounds.x);
			r.bounds.y = (int) (0.8 * r.bounds.y);
		}
		for (DescriptionBox b : model.descriptions)
		{
			b.bounds.x = (int) (0.8 * b.bounds.x);
			b.bounds.y = (int) (0.8 * b.bounds.y);
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
			requestRelationship = false;
		repaint();
	}
	
	@Override
	public void keyReleased(KeyEvent e)
	{
	
	}
	
	@Override
	public void keyTyped(KeyEvent e)
	{
	
	}
	
	@Override
	public void mouseClicked(MouseEvent ev)
	{
		if (ev.getClickCount() == 2)
		{
			Point p = new Point((int) (ev.getX() / zoom), (int) (ev.getY() / zoom));
			for (Entity en : model.entities)
			{
				if (en.isAffectedByTouch(p))
				{
					requestRelationship();
					if (!relationshipHasFirstEntity && requestRelationship)
					{
						newRelationship.setFirstEntity(en);
						relationshipHasFirstEntity = true;
					}
					else if (relationshipHasFirstEntity && requestRelationship && en != newRelationship.getFirstEntity())
					{
						newRelationship.setSecondEntity(en);
						requestRelationship = false;
						relationshipHasFirstEntity = false;
						newRelationship.bounds.x = (int) ((newRelationship.getFirstEntity().bounds.getCenterX()
								+ newRelationship.getSecondEntity().bounds.getCenterX()) / 2) - newRelationship.bounds.width / 2;
						newRelationship.bounds.y = (int) ((newRelationship.getFirstEntity().bounds.getCenterY()
								+ newRelationship.getSecondEntity().bounds.getCenterY()) / 2) - newRelationship.bounds.height / 2;
						model.relationships.add(newRelationship);
						RelationshipChangeEvent changeEvent = new RelationshipChangeEvent(model.relationships,
								RelationshipChangeEvent.CHANGE_ADD, null, newRelationship);
						history.pushEvent(changeEvent);
						deselectAll();
					}
					repaint();
					return;
				}
			}
			Entity e = new Entity();
			e.bounds.x = (int) (ev.getX() / zoom);
			e.bounds.y = (int) (ev.getY() / zoom);
			model.entities.add(e);
			EntityChangeEvent changeEvent = new EntityChangeEvent(model.entities, EntityChangeEvent.CHANGE_ADD, null, e);
			history.pushEvent(changeEvent);
			repaint();
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e)
	{
		draggingEnd = new Point((int) (e.getX() / zoom), (int) (e.getY() / zoom));
		if (dragsObject)
		{
			Dimension size = getSize();
			for (Entity en : model.entities)
			{
				if (en.selected)
				{
					en.bounds.x = Math.max(draggingEnd.x - draggingPrevious.x + en.bounds.x, 0);
					en.bounds.y = Math.max(draggingEnd.y - draggingPrevious.y + en.bounds.y, 0);
					size.width = Math.max(size.width, en.bounds.x + en.bounds.width + 20);
					size.height = Math.max(size.height, en.bounds.y + en.bounds.height + 20);
				}
			}
			for (Relationship re : model.relationships)
			{
				if (re.selected)
				{
					re.bounds.x = Math.max(draggingEnd.x - draggingPrevious.x + re.bounds.x, 0);
					re.bounds.y = Math.max(draggingEnd.y - draggingPrevious.y + re.bounds.y, 0);
					size.width = Math.max(size.width, re.bounds.x + re.bounds.width + 20);
					size.height = Math.max(size.height, re.bounds.y + re.bounds.height + 20);
				}
			}
			for (DescriptionBox box : model.descriptions)
			{
				if (box.selected)
				{
					box.bounds.x = Math.max(draggingEnd.x - draggingPrevious.x + box.bounds.x, 0);
					box.bounds.y = Math.max(draggingEnd.y - draggingPrevious.y + box.bounds.y, 0);
					size.width = Math.max(size.width, box.bounds.x + box.bounds.width + 20);
					size.height = Math.max(size.height, box.bounds.y + box.bounds.height + 20);
				}
			}
			setSize(size);
		}
		else
		{
			Rectangle draggingRect = new Rectangle();
			draggingRect.x = Math.min(draggingEnd.x, draggingStart.x);
			draggingRect.width = Math.abs(draggingEnd.x - draggingStart.x);
			draggingRect.y = Math.min(draggingEnd.y, draggingStart.y);
			draggingRect.height = Math.abs(draggingEnd.y - draggingStart.y);
			for (Entity e1 : model.entities)
				if (e1.bounds.intersects(draggingRect))
					e1.select();
			for (Relationship r : model.relationships)
				if (r.bounds.intersects(draggingRect))
					r.select();
			for (DescriptionBox b : model.descriptions)
				if (b.bounds.intersects(draggingRect))
					b.select();
		}
		
		repaint();
		draggingPrevious = new Point((int) (e.getX() / zoom), (int) (e.getY() / zoom));
	}
	
	@Override
	public void mouseEntered(MouseEvent e)
	{
	
	}
	
	@Override
	public void mouseExited(MouseEvent e)
	{
	
	}
	
	@Override
	public void mouseMoved(MouseEvent e)
	{
	
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		this.requestFocus();
		Entity selectedEntity = null;
		Relationship selectedRelationship = null;
		DescriptionBox selectedDescription = null;
		boolean hitsObject = false;
		Point p = new Point((int) (e.getX() / zoom), (int) (e.getY() / zoom));
		boolean deselect = true;
		for (Entity en : model.entities)
			if (en.isSelected() && en.isAffectedByTouch(p))
				deselect = false;
		for (Relationship re : model.relationships)
			if (re.isSelected() && re.isAffectedByTouch(p))
				deselect = false;
		for (DescriptionBox box : model.descriptions)
			if (box.isSelected() && box.isAffectedByTouch(p))
				deselect = false;
		for (Entity en : model.entities)
		{
			if (deselect)
				en.deselect();
			if (en.isAffectedByTouch(p))
			{
				hitsObject = true;
				for (ERSelectionNotifier ersn : notifier)
					ersn.didSelectEntity(en);
				selectedEntity = en;
				if (requestRelationship)
				{
					if (!relationshipHasFirstEntity)
					{
						newRelationship.setFirstEntity(en);
						relationshipHasFirstEntity = true;
					}
					else
					{
						newRelationship.setSecondEntity(en);
						requestRelationship = false;
						newRelationship.bounds.x = (int) ((newRelationship.getFirstEntity().bounds.getCenterX()
								+ newRelationship.getSecondEntity().bounds.getCenterX()) / 2) - newRelationship.bounds.width / 2;
						newRelationship.bounds.y = (int) ((newRelationship.getFirstEntity().bounds.getCenterY()
								+ newRelationship.getSecondEntity().bounds.getCenterY()) / 2) - newRelationship.bounds.height / 2;
						model.relationships.add(newRelationship);
						RelationshipChangeEvent changeEvent = new RelationshipChangeEvent(model.relationships,
								RelationshipChangeEvent.CHANGE_ADD, null, newRelationship);
						history.pushEvent(changeEvent);
					}
					
				}
			}
		}
		if (selectedEntity != null)
			selectedEntity.select();
			
		for (Relationship r : model.relationships)
		{
			if (deselect)
				r.deselect();
			if (r.isAffectedByTouch(p))
			{
				hitsObject = true;
				for (ERSelectionNotifier ersn : notifier)
					ersn.didSelectRelationship(r);
				selectedRelationship = r;
			}
		}

		if (selectedEntity == null && selectedRelationship != null)
			selectedRelationship.select();

		for (DescriptionBox b : model.descriptions)
		{
			if (deselect)
				b.deselect();
			if (b.isAffectedByTouch(p))
			{
				hitsObject = true;
				for (ERSelectionNotifier ersn : notifier)
					ersn.didSelectDescriptionBox(b);
				selectedDescription = b;
			}
		}

		if (selectedEntity == null && selectedRelationship == null && selectedDescription != null)
			selectedDescription.select();

		dragsObject = hitsObject;
		draggingStart = new Point((int) (e.getX() / zoom), (int) (e.getY() / zoom));
		draggingPrevious = new Point((int) (e.getX() / zoom), (int) (e.getY() / zoom));
		draggingEnd = new Point((int) (e.getX() / zoom), (int) (e.getY() / zoom));
		dragging = true;
		
		if (!hitsObject)
		{
			for (ERSelectionNotifier ersn : notifier)
				ersn.didSelectRelationship(null);
			for (ERSelectionNotifier ersn : notifier)
				ersn.didSelectEntity(null);
			for (ERSelectionNotifier ersn : notifier)
				ersn.didSelectDescriptionBox(null);
		}
		repaint();
	}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (dragsObject)
		{
			int n_drag_x = draggingStart.x - draggingEnd.x;
			int n_drag_y = draggingStart.y - draggingEnd.y;
			if (n_drag_x != 0 || n_drag_y != 0)
			{
				List<ERObject> draggedObjects = new ArrayList<>();
				for (Entity en : model.entities)
					if (en.selected)
						draggedObjects.add(en);
				for (Relationship r : model.relationships)
					if (r.selected)
						draggedObjects.add(r);
				for (DescriptionBox b : model.descriptions)
					if (b.selected)
						draggedObjects.add(b);
				Point[] before = new Point[draggedObjects.size()];
				Point[] after = new Point[draggedObjects.size()];
				for (int i = 0; i < draggedObjects.size(); i++)
				{
					after[i] = new Point();
					before[i] = new Point();
					after[i].x = draggedObjects.get(i).bounds.x;
					after[i].y = draggedObjects.get(i).bounds.y;
					before[i].x = after[i].x + n_drag_x;
					before[i].y = after[i].y + n_drag_y;
				}
				ERObject[] d_objects = draggedObjects.toArray(new ERObject[draggedObjects.size()]);
				EntityChangeEvent changeEvent = new EntityChangeEvent(d_objects, EntityChangeEvent.CHANGE_POSITION_MULTIPLE, before, after);
				history.pushEvent(changeEvent);
			}
		}
		dragging = false;
		dragsObject = false;
		repaint();
	}
	
	@Override
	public void objectNeedsRepaint(ERObject object)
	{
		this.repaint();
	}
	
	public void paste() throws HeadlessException, UnsupportedFlavorException, IOException, ClassNotFoundException
	{
		if (!Toolkit.getDefaultToolkit().getSystemClipboard()
				.isDataFlavorAvailable(new DataFlavor(ERObject.class, "Entity-Relationship Object")))
			return;
		for (Entity e : model.entities)
			e.deselect();
		for (Relationship r : model.relationships)
			r.deselect();
		for (DescriptionBox b : model.descriptions)
			b.deselect();
		ERObject[] objects = (ERObject[]) Toolkit.getDefaultToolkit().getSystemClipboard()
				.getData(new DataFlavor(ERObject.class, "Entity-Relationship Object"));
				
		List<Entity> addedEntities = new ArrayList<>();
		List<Relationship> addedRelationships = new ArrayList<>();
		List<DescriptionBox> addedDescriptions = new ArrayList<>();
		
		for (ERObject obj : objects)
		{
			obj.bounds.x += 20;
			obj.bounds.y += 20;
			obj.select();
			if (obj instanceof Entity)
			{
				Entity e = (Entity) obj;
				addedEntities.add(e);
				int num = 1;
				boolean match = false;
				String name = e.name;
				for (int i = -1; i < model.entities.size(); i++)
				{
					if (i == -1)
					{
						for (int j = 0; j < model.entities.size(); j++)
						{
							Entity e1 = model.entities.get(j);
							if (e1.name.equals(name))
							{
								match = true;
							}
						}
					}
					else
					{
						for (int j = i; j < model.entities.size(); j++)
						{
							Entity e1 = model.entities.get(j);
							if (e1.name.equals(name + num))
							{
								num++;
							}
						}
					}
				}
				if (match)
				{
					e.name = name + num;
				}
				
				model.entities.add((Entity) obj);
			}
			else if (obj instanceof Relationship)
			{
				addedRelationships.add((Relationship) obj);
				model.relationships.add((Relationship) obj);
			}
			else if (obj instanceof DescriptionBox)
			{
				addedDescriptions.add((DescriptionBox) obj);
				model.descriptions.add((DescriptionBox) obj);
			}
			if (addedEntities.size() > 0)
			{
				EntityChangeEvent changeEvent = new EntityChangeEvent(model.entities, EntityChangeEvent.CHANGE_PASTE, null, addedEntities);
				history.pushEvent(changeEvent);
			}
			if (addedRelationships.size() > 0)
			{
				RelationshipChangeEvent changeEvent = new RelationshipChangeEvent(model.relationships, RelationshipChangeEvent.CHANGE_PASTE,
						null, addedRelationships);
				history.pushEvent(changeEvent);
			}
			if (addedDescriptions.size() > 0)
			{
				DescriptionBoxChangeEvent changeEvent = new DescriptionBoxChangeEvent(model.descriptions, DescriptionBoxChangeEvent.CHANGE_PASTE, null, addedDescriptions);
				history.pushEvent(changeEvent);
			}
		}
	}
	
	public void requestRelationship()
	{
		int selectedCount = 0;
		for (Entity e : model.entities)
			if (e.isSelected())
				selectedCount++;
				
		requestRelationship = true;
		relationshipHasFirstEntity = false;
		newRelationship = new Relationship();
		
		if (selectedCount >= 1 && selectedCount <= 2)
		{
			Entity[] selected = new Entity[selectedCount];
			int j = 0;
			for (int i = 0; i < model.entities.size(); i++)
				if (model.entities.get(i).isSelected())
					selected[j++] = model.entities.get(i);
					
			relationshipHasFirstEntity = true;
			newRelationship.setFirstEntity(selected[0]);
			
			if (selectedCount == 2)
			{
				requestRelationship = false;
				relationshipHasFirstEntity = false;
				newRelationship.setSecondEntity(selected[1]);
				newRelationship.bounds.x = (int) ((newRelationship.getFirstEntity().bounds.getCenterX()
						+ newRelationship.getSecondEntity().bounds.getCenterX()) / 2) - newRelationship.bounds.width / 2;
				newRelationship.bounds.y = (int) ((newRelationship.getFirstEntity().bounds.getCenterY()
						+ newRelationship.getSecondEntity().bounds.getCenterY()) / 2) - newRelationship.bounds.height / 2;
				model.relationships.add(newRelationship);
				RelationshipChangeEvent changeEvent = new RelationshipChangeEvent(model.relationships, RelationshipChangeEvent.CHANGE_ADD,
						null, newRelationship);
				history.pushEvent(changeEvent);
			}
		}
		else
		{
			requestRelationship = true;
			relationshipHasFirstEntity = false;
			newRelationship = new Relationship();
		}
		
		deselectAll();
		repaint();
	}
	
	public void selectAll()
	{
		for (Entity e : model.entities)
			e.select();
		for (Relationship r : model.relationships)
			r.select();
		for (DescriptionBox b : model.descriptions)
			b.select();
	}
	
	public void setChangeHistory(ERChangeHistory history)
	{
		this.history = history;
	}
	
	public void setERSelectionNotifier(ERSelectionNotifier n)
	{
		notifier.add(n);
	}
	
	public void setVisibleRect(Rectangle r)
	{
		visibleRect = r;
		if (requestRelationship)
			repaint();
	}
	
	public void zoomIn()
	{
		zoom *= 1.25f;
	}
	
	public void zoomOriginal()
	{
		zoom = 1.0f;
	}
	
	public void zoomOut()
	{
		zoom /= 1.25f;
	}
	
	@Override
	protected void paintComponent(Graphics graphics)
	{
		int maxX = this.getParent().getWidth();
		int maxY = this.getParent().getHeight();
		
		for (Relationship r : model.relationships)
		{
			Rectangle bounds = r.bounds;
			maxX = (int) Math.max(maxX, bounds.getMaxX() * zoom + 200 * zoom);
			maxY = (int) Math.max(maxY, bounds.getMaxY() * zoom + 200 * zoom);
		}
		
		for (Entity e1 : model.entities)
		{
			maxX = (int) Math.max(maxX, e1.bounds.getMaxX() * zoom + 200 * zoom);
			maxY = (int) Math.max(maxY, e1.bounds.getMaxY() * zoom + 200 * zoom);
		}

		for (DescriptionBox b : model.descriptions)
		{
			Rectangle bounds = b.bounds;
			maxX = (int) Math.max(maxX, bounds.getMaxX() * zoom + 200 * zoom);
			maxY = (int) Math.max(maxY, bounds.getMaxY() * zoom + 200 * zoom);
		}

		setSize(maxX, maxY);
		setPreferredSize(new Dimension(maxX, maxY));
		
		Graphics2D g = (Graphics2D) graphics.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, maxX, maxY);
		g.scale(zoom, zoom);
		g.setColor(new Color(230, 230, 230));
		g.setStroke(new BasicStroke(0.5f));
		for (int x = 0; x < maxX / zoom; x += 50)
			g.drawLine(x, 0, x, (int) (maxY / zoom));
		for (int y = 0; y < maxY / zoom; y += 50)
			g.drawLine(0, y, (int) (maxX / zoom), y);
			
		g.setStroke(new BasicStroke(1));

		for (DescriptionBox b : model.descriptions)
			b.paint(g);
		for (Relationship r : model.relationships)
			r.paint(g);
		for (Entity e : model.entities)
			e.paint(g);

			
		g.scale(1.0f / zoom, 1.0f / zoom);
		if (dragging && !dragsObject)
		{
			g.setColor(new Color(0, 150, 180));
			Rectangle draggingRect = new Rectangle();
			draggingRect.x = (int) (Math.min(draggingEnd.x, draggingStart.x) * zoom);
			draggingRect.width = (int) (Math.abs(draggingEnd.x - draggingStart.x) * zoom);
			draggingRect.y = (int) (Math.min(draggingEnd.y, draggingStart.y) * zoom);
			draggingRect.height = (int) (Math.abs(draggingEnd.y - draggingStart.y) * zoom);
			g.draw(draggingRect);
			g.setComposite(AlphaComposite.SrcOver);
			g.setColor(new Color(0, 150, 180, 10));
			g.fill(draggingRect);
		}
		
		if (requestRelationship)
		{
			FontRenderContext frc = g.getFontRenderContext();
			Font f = new Font("Helvetica", Font.PLAIN, 20);
			TextLayout tl = new TextLayout(
					relationshipHasFirstEntity ? ER_Editor.LOCALIZATION.getString("relationship_choose_second_entity")
							: ER_Editor.LOCALIZATION.getString("relationship_choose_first_entity"),
					f, frc);
			Rectangle2D textBounds = tl.getBounds();
			g.setColor(new Color(220, 220, 220, 150));
			g.fillRoundRect((int) (visibleRect.getCenterX() - textBounds.getWidth() / 2 - 20), visibleRect.y + 20,
					(int) textBounds.getWidth() + 40, (int) textBounds.getHeight() + 28, 20, 20);
			g.setColor(Color.BLACK);
			tl.draw(g, (int) (visibleRect.getCenterX() - textBounds.getWidth() / 2), visibleRect.y + 50);
		}
	}
}
