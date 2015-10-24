


import java.io.Serializable;

public class Attribute implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private boolean	isKeyAttribute;
	private String	name;
	
	public Attribute()
	{
		name = "Attribut";
		isKeyAttribute = false;
	}
	
	public String getName()
	{
		return name;
	}
	
	public boolean isKeyAttribute()
	{
		return isKeyAttribute;
	}
	
	public void setKeyAttribute(boolean aFlag)
	{
		isKeyAttribute = aFlag;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
}
