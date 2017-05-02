
/**
  * EReditor
  * ERChangeHistory.java
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
		if (changeStack.size() > 512)
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
