
/**
  * EReditor
  * ER_Editor.java
  * Created by Palle on 14.05.2014
  * Copyright (c) 2014 - 2015 Palle
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

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class ER_Editor
{
	public static ResourceBundle LOCALIZATION;
	
	protected static ArrayList<ERFrame> openedFrames;
	
	public static void main(String[] args)
	{
		try
		{
			System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
		
		}
		
		Locale locale = Locale.getDefault();
		LOCALIZATION = ResourceBundle.getBundle("Localizable", locale);
		
		openedFrames = new ArrayList<ERFrame>();
		
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				new ERFrame();
			}
		});
		
	}
	
}
