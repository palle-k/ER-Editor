
/**
  * EReditor
  * ERChangeEvent.java
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

public abstract class ERChangeEvent
{
	public static final String	CHANGE_POSITION				= "change_entity_position";
	public static final String	CHANGE_POSITION_MULTIPLE	= "change_position_multiple";
	public static final String	CHANGE_WEAK					= "change_weak";
	public static final String	CHANGE_NAME					= "change_name";
	
	public static final String	CHANGE_DELETE			= "change_delete";
	public static final String	CHANGE_DELETE_MULTIPLE	= "change_delete_multiple";
	public static final String	CHANGE_ADD				= "change_add";
	public static final String	CHANGE_PASTE			= "change_paste";
	protected Object			source;
	protected Object			beforeValue;
	protected Object			afterValue;
	protected String			propertyName;
	
	public ERChangeEvent(Object source, String property, Object before, Object after)
	{
		setPropertyChange(property, before, after);
		this.source = source;
	}
	
	public Object getAfterChangeValue()
	{
		return afterValue;
	}
	
	public Object getBeforeChangeValue()
	{
		return beforeValue;
	}
	
	public String getProperty()
	{
		return propertyName;
	}
	
	public ERObject getSource()
	{
		return (ERObject) source;
	}
	
	public abstract void redo();
	
	public void setPropertyChange(String property, Object before, Object after)
	{
		propertyName = property;
		beforeValue = before;
		afterValue = after;
	}
	
	public void setSource(ERObject object)
	{
		source = object;
	}
	
	@Override
	public String toString()
	{
		return ER_Editor.LOCALIZATION.getString(propertyName);
	}
	
	public abstract void undo();
}
