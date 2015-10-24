

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.TransferHandler;

public class ERSelection extends TransferHandler implements Transferable
{
	private static final long serialVersionUID = 8768764939380673657L;
	
	private ERObject[] transferData;
	
	public ERSelection()
	{
	
	}
	
	public ERSelection(ERObject[] data)
	{
		transferData = data;
	}
	
	public ERSelection(String property)
	{
		super(property);
	}
	
	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
	{
		if (isDataFlavorSupported(flavor))
			return transferData;
		return null;
	}
	
	@Override
	public DataFlavor[] getTransferDataFlavors()
	{
		DataFlavor[] flavors = new DataFlavor[1];
		flavors[0] = new DataFlavor(ERObject.class, "Entity-Relationship Object");
		return flavors;
	}
	
	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		return flavor.equals(new DataFlavor(ERObject.class, "Entity-Relationship Object"));
	}
	
}
