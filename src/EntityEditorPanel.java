
/**
  * EReditor
  * EntityEditorPanel.java
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

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

public class EntityEditorPanel extends JPanel implements ListSelectionListener, ActionListener, KeyListener
{
	private static final long serialVersionUID = 3734616267915157581L;
	
	private Entity							entity;
	private final JTextField				txtEntityName;
	private final JLabel					lblEntityName;
	private final JList<String>				listAttributes;
	private final DefaultListModel<String>	dlmAttributeList;
	private final JLabel					lblAttributeName;
	private final JTextField				txtAttributeName;
	private final JCheckBox					cbxAttributeIsKey;
	private final JLabel					lblAttributes;
	private final JButton					btnAddAttribute;
	private final JButton					btnDeleteAttribute;
	private final JCheckBox					cbxIsWeakEntity;
	private final JCheckBox					cbxInherits;
	private final JComboBox<String>			cbxInheritingEntity;
	private final JCheckBox					cbxAggregatesTo;
	private final JComboBox<String>			cbxAggregatesToEntity;
	private RepaintRequest					request;
	private ERModelQuery					query;
	private Attribute						selectedAttribute;
	private ERChangeHistory					history;
	
	private int listSelectionIndex = -1;
	
	public EntityEditorPanel()
	{
		setLayout(null);
		
		lblEntityName = new JLabel();
		lblEntityName.setBounds(5, 5, 100, 27);
		lblEntityName.setText(ER_Editor.LOCALIZATION.getString("entity_name"));
		add(lblEntityName);
		
		txtEntityName = new JTextField();
		txtEntityName.setBounds(110, 5, 180, 27);
		txtEntityName.addKeyListener(this);
		txtEntityName.setEnabled(false);
		add(txtEntityName);
		
		lblAttributes = new JLabel();
		lblAttributes.setBounds(5, 37, 100, 27);
		lblAttributes.setText(ER_Editor.LOCALIZATION.getString("attributes"));
		add(lblAttributes);
		
		dlmAttributeList = new DefaultListModel<String>();
		listAttributes = new JList<String>(dlmAttributeList);
		listAttributes.setEnabled(false);
		listAttributes.addListSelectionListener(this);
		JScrollPane scpListAttributes = new JScrollPane(listAttributes, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scpListAttributes.setBounds(5, 64, 280, 200);
		add(scpListAttributes);
		
		lblAttributeName = new JLabel();
		lblAttributeName.setBounds(5, 274, 100, 27);
		lblAttributeName.setText(ER_Editor.LOCALIZATION.getString("attribute_name"));
		add(lblAttributeName);
		
		txtAttributeName = new JTextField();
		txtAttributeName.setBounds(110, 274, 180, 27);
		txtAttributeName.addKeyListener(this);
		txtAttributeName.setEnabled(false);
		add(txtAttributeName);
		
		cbxAttributeIsKey = new JCheckBox();
		cbxAttributeIsKey.setBounds(5, 311, 280, 27);
		cbxAttributeIsKey.setText(ER_Editor.LOCALIZATION.getString("key_attribute"));
		cbxAttributeIsKey.addActionListener(this);
		cbxAttributeIsKey.setEnabled(false);
		add(cbxAttributeIsKey);
		
		btnAddAttribute = new JButton();
		btnAddAttribute.setBounds(5, 348, 140, 27);
		btnAddAttribute.setText(ER_Editor.LOCALIZATION.getString("new_attribute"));
		btnAddAttribute.setEnabled(false);
		btnAddAttribute.addActionListener(this);
		add(btnAddAttribute);
		
		btnDeleteAttribute = new JButton();
		btnDeleteAttribute.setBounds(150, 348, 140, 27);
		btnDeleteAttribute.setText(ER_Editor.LOCALIZATION.getString("delete_attribute"));
		btnDeleteAttribute.setEnabled(false);
		btnDeleteAttribute.addActionListener(this);
		add(btnDeleteAttribute);
		
		cbxIsWeakEntity = new JCheckBox();
		cbxIsWeakEntity.setBounds(5, 385, 290, 27);
		cbxIsWeakEntity.setText(ER_Editor.LOCALIZATION.getString("weak_entity"));
		cbxIsWeakEntity.addActionListener(this);
		add(cbxIsWeakEntity);
		
		cbxInherits = new JCheckBox();
		cbxInherits.setBounds(5, 422, 120, 27);
		cbxInherits.setText(ER_Editor.LOCALIZATION.getString("inherits_from"));
		cbxInherits.addActionListener(this);
		add(cbxInherits);
		
		cbxInheritingEntity = new JComboBox<String>();
		cbxInheritingEntity.setBounds(130, 422, 160, 27);
		cbxInheritingEntity.addActionListener(this);
		cbxInheritingEntity.setEnabled(false);
		add(cbxInheritingEntity);
		
		cbxAggregatesTo = new JCheckBox();
		cbxAggregatesTo.setBounds(5, 459, 120, 27);
		cbxAggregatesTo.setText(ER_Editor.LOCALIZATION.getString("part_of"));
		cbxAggregatesTo.addActionListener(this);
		add(cbxAggregatesTo);
		
		cbxAggregatesToEntity = new JComboBox<String>();
		cbxAggregatesToEntity.setBounds(130, 459, 160, 27);
		cbxAggregatesToEntity.addActionListener(this);
		cbxAggregatesToEntity.setEnabled(false);
		add(cbxAggregatesToEntity);
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == btnAddAttribute)
		{
			cbxAttributeIsKey.setSelected(false);
			dlmAttributeList.clear();
			entity.attributes.add(new Attribute());
			for (Attribute a : entity.attributes)
				dlmAttributeList.addElement(a.getName());
			listAttributes.setSelectedIndex(dlmAttributeList.getSize() - 1);
			selectedAttribute = entity.attributes.get(entity.attributes.size() - 1);
			EntityChangeEvent changeEvent = new EntityChangeEvent(entity, EntityChangeEvent.CHANGE_ATTRIBUTE_ADD, null, selectedAttribute);
			history.pushEvent(changeEvent);
			
		}
		else if (e.getSource() == btnDeleteAttribute)
		{
			EntityChangeEvent changeEvent = new EntityChangeEvent(entity, EntityChangeEvent.CHANGE_ATTRIBUTE_REMOVE, selectedAttribute,
					null);
			history.pushEvent(changeEvent);
			entity.attributes.remove(listAttributes.getSelectedIndex());
			dlmAttributeList.clear();
			for (Attribute a : entity.attributes)
				dlmAttributeList.addElement(a.getName());
		}
		else if (e.getSource() == cbxIsWeakEntity)
		{
			EntityChangeEvent changeEvent = new EntityChangeEvent(entity, EntityChangeEvent.CHANGE_WEAK, !cbxIsWeakEntity.isSelected(),
					cbxIsWeakEntity.isSelected());
			history.pushEvent(changeEvent);
			entity.setWeak(cbxIsWeakEntity.isSelected());
		}
		else if (e.getSource() == cbxInherits)
		{
			cbxInheritingEntity.setEnabled(cbxInherits.isSelected());
			if (!cbxInherits.isSelected())
			{
				EntityChangeEvent changeEvent = new EntityChangeEvent(entity, EntityChangeEvent.CHANGE_INHERITS, entity.getParent(), null);
				history.pushEvent(changeEvent);
				entity.setParentEntity(null);
			}
			else if (cbxInheritingEntity.getSelectedIndex() != -1)
			{
				EntityChangeEvent changeEvent = new EntityChangeEvent(entity, EntityChangeEvent.CHANGE_INHERITS, entity.getParent(),
						query.getAllEntities().get(cbxInheritingEntity.getSelectedIndex()));
				history.pushEvent(changeEvent);
				entity.setParentEntity(query.getAllEntities().get(cbxInheritingEntity.getSelectedIndex()));
			}
		}
		else if (e.getSource() == cbxInheritingEntity)
		{
			if (!cbxInherits.isSelected())
			{
				return;
			}
			else if (cbxInheritingEntity.getSelectedIndex() != -1)
			{
				EntityChangeEvent changeEvent = new EntityChangeEvent(entity, EntityChangeEvent.CHANGE_INHERITS, entity.getParent(),
						query.getAllEntities().get(cbxInheritingEntity.getSelectedIndex()));
				history.pushEvent(changeEvent);
				entity.setParentEntity(query.getAllEntities().get(cbxInheritingEntity.getSelectedIndex()));
			}
		}
		else if (e.getSource() == cbxAggregatesTo)
		{
			cbxAggregatesToEntity.setEnabled(cbxAggregatesTo.isSelected());
			if (!cbxAggregatesTo.isSelected())
			{
				EntityChangeEvent changeEvent = new EntityChangeEvent(entity, EntityChangeEvent.CHANGE_AGGREGATES_TO,
						entity.getAggregatedEntity(), null);
				history.pushEvent(changeEvent);
				entity.setAggregatedEntity(null);
			}
			else if (cbxAggregatesToEntity.getSelectedIndex() != -1)
			{
				EntityChangeEvent changeEvent = new EntityChangeEvent(entity, EntityChangeEvent.CHANGE_AGGREGATES_TO,
						entity.getAggregatedEntity(), query.getAllEntities().get(cbxAggregatesToEntity.getSelectedIndex()));
				history.pushEvent(changeEvent);
				entity.setAggregatedEntity(query.getAllEntities().get(cbxAggregatesToEntity.getSelectedIndex()));
			}
		}
		else if (e.getSource() == cbxAggregatesToEntity)
		{
			if (!cbxAggregatesTo.isSelected())
			{
				return;
			}
			else if (cbxAggregatesToEntity.getSelectedIndex() != -1)
			{
				EntityChangeEvent changeEvent = new EntityChangeEvent(entity, EntityChangeEvent.CHANGE_AGGREGATES_TO,
						entity.getAggregatedEntity(), query.getAllEntities().get(cbxAggregatesToEntity.getSelectedIndex()));
				history.pushEvent(changeEvent);
				entity.setAggregatedEntity(query.getAllEntities().get(cbxAggregatesToEntity.getSelectedIndex()));
			}
		}
		else if (e.getSource() == cbxAttributeIsKey)
		{
			if (selectedAttribute != null)
			{
				selectedAttribute.setKeyAttribute(cbxAttributeIsKey.isSelected());
				EntityChangeEvent changeEvent = new EntityChangeEvent(selectedAttribute, EntityChangeEvent.CHANGE_ATTRIBUTE_IS_KEY,
						!cbxAttributeIsKey.isSelected(), cbxAttributeIsKey.isSelected());
				history.pushEvent(changeEvent);
			}
		}
		request.objectNeedsRepaint(entity);
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
	}
	
	@Override
	public void keyReleased(KeyEvent e)
	{
		if (e.getSource() == txtEntityName)
		{
			if (entity != null)
			{
				EntityChangeEvent changeEvent = new EntityChangeEvent(entity, EntityChangeEvent.CHANGE_NAME, entity.getName(),
						txtEntityName.getText());
				history.pushEvent(changeEvent);
				entity.setName(txtEntityName.getText());
				request.objectNeedsRepaint(entity);
			}
		}
		else if (e.getSource() == txtAttributeName)
		{
			EntityChangeEvent changeEvent = new EntityChangeEvent(selectedAttribute, EntityChangeEvent.CHANGE_ATTRIBUTE_RENAME,
					selectedAttribute.getName(), txtAttributeName.getText());
			history.pushEvent(changeEvent);
			selectedAttribute.setName(txtAttributeName.getText());
			request.objectNeedsRepaint(entity);
			int index = listAttributes.getSelectedIndex();
			dlmAttributeList.set(index, selectedAttribute.getName());
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e)
	{
	
	}
	
	public void setChangeHistory(ERChangeHistory history)
	{
		this.history = history;
	}
	
	public void setEntity(Entity e)
	{
		dlmAttributeList.clear();
		txtEntityName.setText("");
		entity = e;
		if (entity != null)
		{
			txtEntityName.setEnabled(true);
			txtEntityName.setText(entity.getName());
			listAttributes.setEnabled(true);
			listAttributes.clearSelection();
			cbxAttributeIsKey.setEnabled(false);
			txtAttributeName.setEnabled(false);
			btnAddAttribute.setEnabled(true);
			btnDeleteAttribute.setEnabled(false);
			cbxIsWeakEntity.setSelected(entity.isWeak());
			cbxInherits.setSelected(entity.hasParentEntity());
			cbxInheritingEntity.setEnabled(entity.hasParentEntity());
			
			List<Entity> entities    = query.getAllEntities();
			int          entityIndex = 0;
			if (entity.hasParentEntity())
				entityIndex = entities.indexOf(entity.getParent());
			cbxInheritingEntity.removeAllItems();
			for (Entity e1 : entities)
				cbxInheritingEntity.addItem(e1.getName());
			if (entity.hasParentEntity())
			{
				cbxInheritingEntity.setSelectedIndex(entityIndex);
			}
			
			cbxAggregatesTo.setSelected(entity.hasAggregatedEntity());
			cbxAggregatesToEntity.setEnabled(entity.hasAggregatedEntity());
			if (entity.hasAggregatedEntity())
				entityIndex = entities.indexOf(entity.getAggregatedEntity());
			cbxAggregatesToEntity.removeAllItems();
			for (Entity e1 : entities)
				cbxAggregatesToEntity.addItem(e1.getName());
			if (entity.hasAggregatedEntity())
			{
				cbxAggregatesToEntity.setSelectedIndex(entityIndex);
			}
			
			for (Attribute a : entity.attributes)
				dlmAttributeList.addElement(a.getName());
		}
		else
		{
			txtEntityName.setEnabled(false);
			listAttributes.setEnabled(false);
			btnAddAttribute.setEnabled(false);
			btnDeleteAttribute.setEnabled(false);
			cbxInheritingEntity.setEnabled(false);
		}
	}
	
	public void setModelQuery(ERModelQuery query)
	{
		this.query = query;
	}
	
	public void setRepaintRequest(RepaintRequest request)
	{
		this.request = request;
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		int index = listAttributes.getSelectedIndex();
		if (index == listSelectionIndex)
			return;
		listSelectionIndex = index;
		if (index == -1)
		{
			selectedAttribute = null;
			txtAttributeName.setEnabled(false);
			btnDeleteAttribute.setEnabled(false);
			cbxAttributeIsKey.setEnabled(false);
		}
		else
		{
			selectedAttribute = entity.attributes.get(index);
			txtAttributeName.setEnabled(true);
			txtAttributeName.setText(selectedAttribute.getName());
			btnDeleteAttribute.setEnabled(true);
			cbxAttributeIsKey.setEnabled(true);
			cbxAttributeIsKey.setSelected(selectedAttribute.isKeyAttribute());
			txtAttributeName.requestFocusInWindow();
			txtAttributeName.setSelectionStart(0);
			txtAttributeName.setSelectionEnd(txtAttributeName.getText().length());
		}
	}
	
}
