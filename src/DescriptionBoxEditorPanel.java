import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.LayoutManager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
  * EReditor
  * DescriptionBoxEditorPanel.java
  * Created by Palle Klewitz on 04.11.2015
  * Copyright (c) 2015 - 2017 Palle Klewitz. All rights reserved.
  */

public class DescriptionBoxEditorPanel extends JPanel implements KeyListener
{
	private JLabel		lblTitle;
	private JTextField	txtTitle;
	private JLabel		lblDescription;
	private JScrollPane	scpDescription;
	private JTextArea	txtDescription;
	private DescriptionBox descriptionBox;
	private ERChangeHistory history;
	private RepaintRequest request;
	
	public DescriptionBoxEditorPanel()
	{
		super();
		init();
	}
	
	public DescriptionBoxEditorPanel(boolean isDoubleBuffered)
	{
		super(isDoubleBuffered);
		init();
	}
	
	public DescriptionBoxEditorPanel(LayoutManager layout)
	{
		super(layout);
		init();
	}
	
	public DescriptionBoxEditorPanel(LayoutManager layout, boolean isDoubleBuffered)
	{
		super(layout, isDoubleBuffered);
		init();
	}

	public DescriptionBox getDescriptionBox()
	{
		return descriptionBox;
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
	
	}
	
	@Override
	public void keyReleased(KeyEvent e)
	{
		if (e.getSource() == txtTitle)
		{
			if (descriptionBox != null)
			{
				DescriptionBoxChangeEvent changeEvent = new DescriptionBoxChangeEvent(descriptionBox, DescriptionBoxChangeEvent.CHANGE_NAME, descriptionBox.getName(), txtTitle.getText());
				history.pushEvent(changeEvent);
				descriptionBox.setName(txtTitle.getText());
				request.objectNeedsRepaint(descriptionBox);
			}
		}
		else if (e.getSource() == txtDescription)
		{
			DescriptionBoxChangeEvent changeEvent = new DescriptionBoxChangeEvent(descriptionBox, DescriptionBoxChangeEvent.CHANGE_DESCRIPTION, descriptionBox.getText(), txtDescription.getText());
			history.pushEvent(changeEvent);
			descriptionBox.setText(txtDescription.getText());
			request.objectNeedsRepaint(descriptionBox);
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e)
	{
	
	}

	public void setDescriptionBox(final DescriptionBox descriptionBox)
	{
		this.descriptionBox = descriptionBox;
		if (descriptionBox != null)
		{
			txtTitle.setText(descriptionBox.getName());
			txtDescription.setText(descriptionBox.getText());
			txtTitle.setEnabled(true);
			txtDescription.setEnabled(true);
		}
		else
		{
			txtTitle.setText("");
			txtDescription.setText("");
			txtTitle.setEnabled(false);
			txtDescription.setEnabled(false);
		}
	}

	public void setChangeHistory(final ERChangeHistory history)
	{
		this.history = history;
	}
	
	private void init()
	{
		setLayout(null);
		
		lblTitle = new JLabel();
		lblTitle.setBounds(5, 5, 100, 27);
		lblTitle.setText(ER_Editor.LOCALIZATION.getString("description_box_title"));
		add(lblTitle);
		
		txtTitle = new JTextField();
		txtTitle.setBounds(110, 5, 180, 27);
		txtTitle.setEnabled(false);
		txtTitle.addKeyListener(this);
		add(txtTitle);
		
		lblDescription = new JLabel();
		lblDescription.setBounds(5, 37, 290, 27);
		lblDescription.setText(ER_Editor.LOCALIZATION.getString("description_box_description"));
		add(lblDescription);
		
		txtDescription = new JTextArea();
		txtDescription.setEnabled(false);
		txtDescription.addKeyListener(this);
		
		scpDescription = new JScrollPane(txtDescription);
		scpDescription.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scpDescription.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scpDescription.setBounds(5, 74, 290, 300);
		add(scpDescription);
	}

	public void setRepaintRequest(RepaintRequest request)
	{
		this.request = request;
	}

}
