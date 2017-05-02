
/**
  * EReditor
  * HelpWindow.java
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

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HelpWindow extends JFrame
{
	
	private static final long serialVersionUID = -5009861948532989725L;
	
	public HelpWindow()
	{
		setBounds(500, 0, 560, 400);
		setAlwaysOnTop(true);
		
		JLabel txtHelp = new JLabel();
		JScrollPane scpHelp = new JScrollPane(txtHelp);
		scpHelp.getVerticalScrollBar().setUnitIncrement(6);
		scpHelp.getHorizontalScrollBar().setUnitIncrement(6);
		scpHelp.setBorder(new EmptyBorder(0, 0, 0, 0));
		add(scpHelp);
		
		try
		{
			BufferedReader helpReader = new BufferedReader(new InputStreamReader(
					getClass().getResourceAsStream("help_" + ER_Editor.LOCALIZATION.getLocale().getLanguage() + ".html")));
			String helpText = "";
			String helpLine;
			
			while ((helpLine = helpReader.readLine()) != null)
			{
				helpText += helpLine;
			}
			helpText = helpText.replaceAll("(?s)<!--.*?-->", "");
			txtHelp.setText(helpText);
			
			helpReader.close();
		}
		catch (IOException e)
		{
			txtHelp.setText(ER_Editor.LOCALIZATION.getString("help_load_error"));
			e.printStackTrace();
		}
		
		setVisible(true);
	}
	
}
