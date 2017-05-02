
/**
  * EReditor
  * EntityEditor.java
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

import javax.swing.JPanel;

public class EntityEditor extends JPanel implements ERSelectionNotifier
{
	private static final long serialVersionUID = 3858228349931664236L;
	
	private final EntityEditorPanel			eeditor;
	private final RelationshipEditorPanel	reditor;
	private final DescriptionBoxEditorPanel deditor;
	
	public EntityEditor()
	{
		setLayout(null);
		eeditor = new EntityEditorPanel();
		eeditor.setBounds(0, 0, 300, 500);
		eeditor.setVisible(false);
		add(eeditor);
		reditor = new RelationshipEditorPanel();
		reditor.setBounds(0, 0, 300, 500);
		reditor.setVisible(false);
		add(reditor);
		deditor = new DescriptionBoxEditorPanel();
		deditor.setBounds(0, 0, 300, 500);
		deditor.setVisible(false);
		add(deditor);
	}
	
	@Override
	public void didSelectEntity(Entity e)
	{
		if (e == null)
		{
			reditor.setVisible(false);
			eeditor.setVisible(false);
			return;
		}
		reditor.setVisible(false);
		deditor.setVisible(false);
		eeditor.setEntity(e);
		eeditor.setVisible(true);
	}
	
	@Override
	public void didSelectRelationship(Relationship r)
	{
		if (r == null)
		{
			reditor.setVisible(false);
			eeditor.setVisible(false);
			return;
		}
		eeditor.setVisible(false);
		deditor.setVisible(false);
		reditor.setRelationship(r);
		reditor.setVisible(true);
	}

	@Override
	public void didSelectDescriptionBox(final DescriptionBox b)
	{
		if (b == null)
		{
			reditor.setVisible(false);
			eeditor.setVisible(false);
			deditor.setVisible(false);
			return;
		}
		eeditor.setVisible(false);
		reditor.setVisible(false);
		deditor.setDescriptionBox(b);
		deditor.setVisible(true);
	}
	
	public void setChangeHistory(ERChangeHistory history)
	{
		eeditor.setChangeHistory(history);
		reditor.setChangeHistory(history);
		deditor.setChangeHistory(history);
	}
	
	public void setModelQuery(ERModelQuery query)
	{
		reditor.setModelQuery(query);
		eeditor.setModelQuery(query);
	}
	
	public void setRepaintRequest(RepaintRequest request)
	{
		eeditor.setRepaintRequest(request);
		reditor.setRepaintRequest(request);
		deditor.setRepaintRequest(request);
	}
}
