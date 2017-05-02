
/**
  * EReditor
  * RelationshipEditorPanel.java
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

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

public class RelationshipEditorPanel extends JPanel implements ActionListener, KeyListener
{
	private static final long serialVersionUID = -3192472999724623164L;
	
	private Relationship			relationship;
	private final JTextField		txtRelationshipName;
	private final JLabel			lblRelationshipName;
	private final JLabel			lblRelationshipEntity1;
	private final JLabel			lblRelationshipEntity2;
	private final JComboBox<String>	cbxRelationshipEntity1;
	private final JComboBox<String>	cbxRelationshipEntity2;
	private final JLabel			lblCardinality1;
	private final JLabel			lblCardinality2;
	private final JRadioButton		rbnCardinality1toOne;
	private final JRadioButton		rbnCardinality1toMany;
	private final ButtonGroup		bgpCardinality1;
	private final JRadioButton		rbnCardinality2toOne;
	private final JRadioButton		rbnCardinality2toMany;
	private final ButtonGroup		bgpCardinality2;
	private final JCheckBox			cbxIsWeakRelationship;
	private ERModelQuery			query;
	private RepaintRequest			request;
	private ERChangeHistory			history;
	
	public RelationshipEditorPanel()
	{
		setLayout(null);
		
		lblRelationshipName = new JLabel();
		lblRelationshipName.setBounds(5, 74, 100, 27);
		lblRelationshipName.setText(ER_Editor.LOCALIZATION.getString("relationship_role"));
		add(lblRelationshipName);
		
		txtRelationshipName = new JTextField();
		txtRelationshipName.setBounds(110, 74, 180, 27);
		txtRelationshipName.addKeyListener(this);
		add(txtRelationshipName);
		
		lblRelationshipEntity1 = new JLabel();
		lblRelationshipEntity1.setBounds(5, 5, 100, 27);
		lblRelationshipEntity1.setText(ER_Editor.LOCALIZATION.getString("relationship_first_entity"));
		add(lblRelationshipEntity1);
		
		cbxRelationshipEntity1 = new JComboBox<String>();
		cbxRelationshipEntity1.setBounds(110, 5, 180, 27);
		cbxRelationshipEntity1.addActionListener(this);
		add(cbxRelationshipEntity1);
		
		lblCardinality1 = new JLabel();
		lblCardinality1.setBounds(5, 37, 100, 27);
		lblCardinality1.setText(ER_Editor.LOCALIZATION.getString("relationship_cardinality"));
		add(lblCardinality1);
		
		rbnCardinality1toOne = new JRadioButton();
		rbnCardinality1toOne.setBounds(110, 37, 80, 27);
		rbnCardinality1toOne.setText("1");
		rbnCardinality1toOne.addActionListener(this);
		add(rbnCardinality1toOne);
		
		rbnCardinality1toMany = new JRadioButton();
		rbnCardinality1toMany.setBounds(200, 37, 80, 27);
		rbnCardinality1toMany.setText("N");
		rbnCardinality1toMany.addActionListener(this);
		add(rbnCardinality1toMany);
		
		bgpCardinality1 = new ButtonGroup();
		bgpCardinality1.add(rbnCardinality1toMany);
		bgpCardinality1.add(rbnCardinality1toOne);
		
		lblRelationshipEntity2 = new JLabel();
		lblRelationshipEntity2.setBounds(5, 111, 100, 27);
		lblRelationshipEntity2.setText(ER_Editor.LOCALIZATION.getString("relationship_second_entity"));
		add(lblRelationshipEntity2);
		
		cbxRelationshipEntity2 = new JComboBox<String>();
		cbxRelationshipEntity2.setBounds(110, 111, 180, 27);
		cbxRelationshipEntity2.addActionListener(this);
		add(cbxRelationshipEntity2);
		
		lblCardinality2 = new JLabel();
		lblCardinality2.setBounds(5, 148, 100, 27);
		lblCardinality2.setText(ER_Editor.LOCALIZATION.getString("relationship_cardinality"));
		add(lblCardinality2);
		
		rbnCardinality2toOne = new JRadioButton();
		rbnCardinality2toOne.setBounds(110, 148, 80, 27);
		rbnCardinality2toOne.setText("1");
		rbnCardinality2toOne.addActionListener(this);
		add(rbnCardinality2toOne);
		
		rbnCardinality2toMany = new JRadioButton();
		rbnCardinality2toMany.setBounds(200, 148, 80, 27);
		rbnCardinality2toMany.setText("N");
		rbnCardinality2toMany.addActionListener(this);
		add(rbnCardinality2toMany);
		
		bgpCardinality2 = new ButtonGroup();
		bgpCardinality2.add(rbnCardinality2toMany);
		bgpCardinality2.add(rbnCardinality2toOne);
		
		cbxIsWeakRelationship = new JCheckBox();
		cbxIsWeakRelationship.setBounds(5, 185, 280, 27);
		cbxIsWeakRelationship.setText(ER_Editor.LOCALIZATION.getString("relationship_weak"));
		cbxIsWeakRelationship.addActionListener(this);
		add(cbxIsWeakRelationship);
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (relationship == null)
			return;
		if (e.getSource() == txtRelationshipName)
		{
			RelationshipChangeEvent changeEvent = new RelationshipChangeEvent(relationship, RelationshipChangeEvent.CHANGE_NAME,
					relationship.name, txtRelationshipName.getText());
			history.pushEvent(changeEvent);
			relationship.setName(txtRelationshipName.getText());
		}
		else if (e.getSource() == cbxRelationshipEntity1 && cbxRelationshipEntity1.getSelectedIndex() != -1)
		{
			RelationshipChangeEvent changeEvent = new RelationshipChangeEvent(relationship, RelationshipChangeEvent.CHANGE_FIRST_ENTITY,
					relationship.getFirstEntity(), query.getAllEntities().get(cbxRelationshipEntity1.getSelectedIndex()));
			history.pushEvent(changeEvent);
			relationship.setFirstEntity(query.getAllEntities().get(cbxRelationshipEntity1.getSelectedIndex()));
		}
		else if (e.getSource() == cbxRelationshipEntity2 && cbxRelationshipEntity2.getSelectedIndex() != -1)
		{
			RelationshipChangeEvent changeEvent = new RelationshipChangeEvent(relationship, RelationshipChangeEvent.CHANGE_SECOND_ENTITY,
					relationship.getSecondEntity(), query.getAllEntities().get(cbxRelationshipEntity2.getSelectedIndex()));
			history.pushEvent(changeEvent);
			relationship.setSecondEntity(query.getAllEntities().get(cbxRelationshipEntity2.getSelectedIndex()));
		}
		else if (e.getSource() == rbnCardinality1toOne || e.getSource() == rbnCardinality1toMany)
		{
			RelationshipChangeEvent changeEvent = new RelationshipChangeEvent(relationship,
					RelationshipChangeEvent.CHANGE_FIRST_ENTITY_TO_MANY, !rbnCardinality1toMany.isSelected(),
					rbnCardinality1toMany.isSelected());
			history.pushEvent(changeEvent);
			relationship.setFirstEntityToMany(rbnCardinality1toMany.isSelected());
		}
		else if (e.getSource() == rbnCardinality2toOne || e.getSource() == rbnCardinality2toMany)
		{
			RelationshipChangeEvent changeEvent = new RelationshipChangeEvent(relationship,
					RelationshipChangeEvent.CHANGE_SECOND_ENTITY_TO_MANY, !rbnCardinality2toMany.isSelected(),
					rbnCardinality2toMany.isSelected());
			history.pushEvent(changeEvent);
			relationship.setSecondEntityToMany(rbnCardinality2toMany.isSelected());
		}
		else if (e.getSource() == cbxIsWeakRelationship)
		{
			RelationshipChangeEvent changeEvent = new RelationshipChangeEvent(relationship, RelationshipChangeEvent.CHANGE_WEAK,
					!cbxIsWeakRelationship.isSelected(), cbxIsWeakRelationship.isSelected());
			history.pushEvent(changeEvent);
			relationship.setWeak(cbxIsWeakRelationship.isSelected());
		}
		request.objectNeedsRepaint(relationship);
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
	}
	
	@Override
	public void keyReleased(KeyEvent e)
	{
		
		if (relationship == null)
			return;
		if (e.getSource() == txtRelationshipName)
		{
			RelationshipChangeEvent changeEvent = new RelationshipChangeEvent(relationship, RelationshipChangeEvent.CHANGE_NAME,
					relationship.name, txtRelationshipName.getText());
			history.pushEvent(changeEvent);
			relationship.setName(txtRelationshipName.getText());
			request.objectNeedsRepaint(relationship);
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
	
	public void setModelQuery(ERModelQuery query)
	{
		this.query = query;
	}
	
	public void setRelationship(Relationship r)
	{
		relationship = r;
		if (r != null)
		{
			txtRelationshipName.setText(relationship.getName());
			cbxIsWeakRelationship.setSelected(relationship.isWeak());
			cbxRelationshipEntity1.removeActionListener(this);
			cbxRelationshipEntity2.removeActionListener(this);
			List<Entity> listEntities = query.getAllEntities();
			String[]     entityNames  = new String[listEntities.size()];
			int          e1index      = 0;
			int          e2index      = 0;
			for (int i = 0; i < listEntities.size(); i++)
			{
				entityNames[i] = listEntities.get(i).getName();
				if (relationship.getFirstEntity() == listEntities.get(i))
					e1index = i;
				if (relationship.getSecondEntity() == listEntities.get(i))
					e2index = i;
			}
			cbxRelationshipEntity1.removeAllItems();
			cbxRelationshipEntity2.removeAllItems();
			for (String s : entityNames)
			{
				cbxRelationshipEntity1.addItem(s);
				cbxRelationshipEntity2.addItem(s);
			}
			cbxRelationshipEntity1.setSelectedIndex(e1index);
			cbxRelationshipEntity2.setSelectedIndex(e2index);
			cbxRelationshipEntity1.addActionListener(this);
			cbxRelationshipEntity2.addActionListener(this);
			rbnCardinality1toOne.setSelected(!relationship.getFirstEntityToMany());
			rbnCardinality1toMany.setSelected(relationship.getFirstEntityToMany());
			rbnCardinality2toOne.setSelected(!relationship.getSecondEntityToMany());
			rbnCardinality2toMany.setSelected(relationship.getSecondEntityToMany());
		}
	}
	
	public void setRepaintRequest(RepaintRequest request)
	{
		this.request = request;
	}
	
}
