

import javax.swing.JPanel;

public class EntityEditor extends JPanel implements ERSelectionNotifier
{
	private static final long serialVersionUID = 3858228349931664236L;
	
	private final EntityEditorPanel			eeditor;
	private final RelationshipEditorPanel	reditor;
	
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
		reditor.setRelationship(r);
		reditor.setVisible(true);
	}
	
	public void setChangeHistory(ERChangeHistory history)
	{
		eeditor.setChangeHistory(history);
		reditor.setChangeHistory(history);
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
	}
}
