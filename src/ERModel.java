
/**
  * EReditor
  * ERModel.java
  * Created by Palle on 14.05.2014
  * Copyright (c) 2014 - 2015 Palle. 
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

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ERModel
{
	@SuppressWarnings("unchecked")
	public static ERModel open(File f) throws IOException, ClassNotFoundException
	{
		FileInputStream fis = new FileInputStream(f);
		ObjectInputStream ois = new ObjectInputStream(fis);
		ERModel model = new ERModel();
		model.entities = (ArrayList<Entity>) ois.readObject();
		model.relationships = (ArrayList<Relationship>) ois.readObject();
		model.fileName = f;
		model.saved = true;
		ois.close();
		return model;
	}
	
	public static ERModel open(JFrame parentFrame)
	{
		FileDialog dialog = new FileDialog(parentFrame, "ER-Modelldatei ausw\u00e4hlen...", FileDialog.LOAD);
		dialog.setFile("*.erm");
		dialog.setFilenameFilter((dir, name) -> name.endsWith(".erm"));
		dialog.setVisible(true);
		String filename = dialog.getFile();
		String dir = dialog.getDirectory();
		if (filename != null)
		{
			try
			{
				return open(new File(dir + filename));
			}
			catch (FileNotFoundException e)
			{
				JOptionPane.showMessageDialog(null, ER_Editor.LOCALIZATION.getString("open_error_message") + "\n" + e.getLocalizedMessage(),
						ER_Editor.LOCALIZATION.getString("open_error_title"), JOptionPane.ERROR_MESSAGE);
						
			}
			catch (ClassNotFoundException e)
			{
				JOptionPane.showMessageDialog(null, ER_Editor.LOCALIZATION.getString("open_error_message") + "\n" + e.getLocalizedMessage(),
						ER_Editor.LOCALIZATION.getString("open_error_title"), JOptionPane.ERROR_MESSAGE);
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(null, ER_Editor.LOCALIZATION.getString("open_error_message") + "\n" + e.getLocalizedMessage(),
						ER_Editor.LOCALIZATION.getString("open_error_title"), JOptionPane.ERROR_MESSAGE);
			}
		}
		return null;
	}
	
	protected ArrayList<Entity>			entities;
	protected ArrayList<Relationship>	relationships;
	protected ArrayList<DescriptionBox>	descriptions;
	
	protected File fileName;
	
	private boolean saved;
	
	public ERModel()
	{
		entities = new ArrayList<Entity>();
		relationships = new ArrayList<Relationship>();
		descriptions = new ArrayList<DescriptionBox>();
	}
	
	public void addEntity()
	{
		for (Entity e1 : entities)
			e1.deselect();
		entities.add(new Entity());
	}
	
	public void export()
	{
		JFileChooser chooser = new JFileChooser();
		if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
		{
			File f = chooser.getSelectedFile();
			try
			{
				ImageIO.write(render(), "png", f);
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(null, ER_Editor.LOCALIZATION.getString("image_export_error_message"),
						ER_Editor.LOCALIZATION.getString("image_export_error_title"), JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	public void exportModel()
	{
		ERModelConverter converter = new ERModelConverter(this);
		converter.convert();
		converter.display();
	}
	
	public String getFilename()
	{
		if (saved)
			return fileName.getName();
		else
			return ER_Editor.LOCALIZATION.getString("model_name");
	}
	
	public boolean isEmpty()
	{
		return entities.size() == 0 && relationships.size() == 0 && saved == false;
	}
	
	public void layoutBoxes()
	{
	
	}
	
	public boolean needsSave()
	{
		if (!saved)
			return true;
		else
		{
			try
			{
				return !open(fileName).equals(this);
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return true;
	}
	
	public BufferedImage render()
	{
		int maxX = 0;
		int maxY = 0;
		
		for (Relationship r : relationships)
		{
			Rectangle bounds = r.bounds;
			maxX = (int) Math.max(maxX, bounds.getMaxX() + 200);
			maxY = (int) Math.max(maxY, bounds.getMaxY() + 200);
		}
		
		for (Entity e : entities)
		{
			Rectangle bounds = e.bounds;
			maxX = (int) Math.max(maxX, bounds.getMaxX() + 200);
			maxY = (int) Math.max(maxY, bounds.getMaxY() + 200);
		}
		
		BufferedImage image = new BufferedImage(maxX * 2, maxY * 2, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, maxX * 2, maxY * 2);
		g.scale(2.0f, 2.0f);
		
		for (Relationship r : relationships)
		{
			r.deselect();
			r.paint(g);
		}
		for (Entity e : entities)
		{
			e.deselect();
			e.paint(g);
		}
		
		g.dispose();
		return image;
	}
	
	public boolean save(JFrame parentFrame)
	{
		boolean success = false;
		if (!saved)
			success = saveAs(true, parentFrame);
		else
			success = save(fileName);
		saved = saved || success;
		return success;
	}
	
	public boolean saveAs(JFrame parentFrame)
	{
		return saveAs(true, parentFrame);
	}
	
	public boolean saveAsCopy(JFrame parentFrame)
	{
		return saveAs(false, parentFrame);
	}
	
	private boolean save(File f)
	{
		if (!f.getAbsolutePath().endsWith(".erm"))
			f = new File(f.getAbsolutePath() + ".erm");
		try
		{
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(entities);
			oos.writeObject(relationships);
			oos.close();
			// XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new
			// FileOutputStream(f)));
			// encoder.writeObject(entities);
			// encoder.writeObject(relationships);
			// encoder.close();
			return true;
		}
		catch (FileNotFoundException e)
		{
			JOptionPane.showMessageDialog(null, ER_Editor.LOCALIZATION.getString("save_error_message") + "\n" + e.getLocalizedMessage(),
					ER_Editor.LOCALIZATION.getString("save_error_title"), JOptionPane.ERROR_MESSAGE);
			return false;
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null, ER_Editor.LOCALIZATION.getString("save_error_message") + "\n" + e.getLocalizedMessage(),
					ER_Editor.LOCALIZATION.getString("save_error_title"), JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
	}
	
	private boolean saveAs(boolean keepNewFileName, JFrame parent)
	{
		FileDialog dialog = new FileDialog(parent, ER_Editor.LOCALIZATION.getString("save_as"), FileDialog.SAVE);
		dialog.setVisible(true);
		String filename = dialog.getFile();
		String dir = dialog.getDirectory();
		if (filename != null)
		{
			if (keepNewFileName)
				fileName = new File(dir + filename);
			return save(new File(dir + filename));
		}
		return false;
	}
}
