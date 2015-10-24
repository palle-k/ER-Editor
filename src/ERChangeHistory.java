

import java.util.Stack;

public class ERChangeHistory
{
	private final Stack<ERChangeEvent>	changeStack;
	private final Stack<ERChangeEvent>	redoStack;
	private ERHistoryChangeNotifier		notifier;
	
	public ERChangeHistory()
	{
		changeStack = new Stack<ERChangeEvent>();
		redoStack = new Stack<ERChangeEvent>();
		notifier = null;
	}
	
	public boolean canRedo()
	{
		return !redoStack.isEmpty();
	}
	
	public boolean canUndo()
	{
		return !changeStack.isEmpty();
	}
	
	public String peekRedo()
	{
		return redoStack.peek().toString();
	}
	
	public String peekUndo()
	{
		return changeStack.peek().toString();
	}
	
	public void pushEvent(ERChangeEvent event)
	{
		redoStack.clear();
		changeStack.push(event);
		if (changeStack.size() > 100)
			changeStack.remove(0);
		if (notifier != null)
			notifier.historyDidChange(this);
	}
	
	public void redo()
	{
		if (!canRedo())
			return;
		ERChangeEvent event = redoStack.pop();
		event.redo();
		changeStack.push(event);
		if (notifier != null)
			notifier.historyDidChange(this);
	}
	
	public void reset()
	{
		redoStack.clear();
		changeStack.clear();
	}
	
	public void setHistoryChangeNotifier(ERHistoryChangeNotifier notifier)
	{
		this.notifier = notifier;
	}
	
	public void undo()
	{
		if (!canUndo())
			return;
		ERChangeEvent event = changeStack.pop();
		event.undo();
		redoStack.push(event);
		if (notifier != null)
			notifier.historyDidChange(this);
	}
}
