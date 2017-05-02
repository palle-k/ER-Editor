
/**
  * EReditor
  * ERSelection.java
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

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

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
