

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

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
			BufferedReader helpReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("help.html")));
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
			txtHelp.setText("<html><body><p>Fehler: Hilfetext konnte nicht geladen werden.</p></body></html>");
		}
		
		setVisible(true);
	}
	
}
