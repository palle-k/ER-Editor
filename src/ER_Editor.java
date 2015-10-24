
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

import java.awt.Dimension;
import java.awt.Event;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class ER_Editor implements ActionListener, ERHistoryChangeNotifier
{
	public static ResourceBundle LOCALIZATION;
	
	public static void main(String[] args)
	{
		try
		{
			System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "ER-Editor");
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
		
		}
		
		Locale locale = Locale.getDefault();
		LOCALIZATION = ResourceBundle.getBundle("Localizable", locale);
		
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				new ER_Editor();
			}
		});
	}
	
	private final JScrollPane	spERView;
	private final ERView		erView;
	private final EntityEditor	editor;
	private final JFrame		frame;
	
	private JMenuItem	menuNew, menuOpen, menuSave, menuSaveAs, menuExportImage, menuExportModel;
	private JMenuItem	menuCut, menuCopy, menuPaste, menuDelete, menuUndo, menuRedo, menuSelectAll;
	private JMenuItem	menuAddEntity, menuAddRelationship;
	private JMenuItem	menuZoomOriginal, menuZoomIn, menuZoomOut, menuExpand, menuImplode;
	private JMenuItem	menuItemHelp;
	private JMenu		menuExport;
	
	private final ERChangeHistory changeHistory;
	
	public ER_Editor()
	{
		
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ER_FrameLayout layout = new ER_FrameLayout();
		frame.setLayout(layout);
		frame.setBounds(0, 0, 1280, 720);
		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				if (!erView.model.isEmpty())
					if (JOptionPane.showConfirmDialog(frame, "Soll das aktuelle Modell gesichert werden?", "Sichern",
							JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION)
						erView.model.save(frame);
			}
		});
		
		try
		{
			@SuppressWarnings("rawtypes")
			Class util = Class.forName("com.apple.eawt.FullScreenUtilities");
			@SuppressWarnings("rawtypes")
			Class params[] = new Class[] { Window.class, Boolean.TYPE };
			@SuppressWarnings("unchecked")
			Method method = util.getMethod("setWindowCanFullScreen", params);
			method.invoke(util, frame, true);
		}
		catch (Exception e)
		{
		
		}
		
		changeHistory = new ERChangeHistory();
		changeHistory.setHistoryChangeNotifier(this);
		
		erView = new ERView();
		erView.setSize(3000, 2000);
		erView.setPreferredSize(new Dimension(3000, 2000));
		erView.setMinimumSize(new Dimension(3000, 2000));
		erView.setChangeHistory(changeHistory);
		spERView = new JScrollPane(erView, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		spERView.setAutoscrolls(true);
		spERView.getVerticalScrollBar().setUnitIncrement(6);
		spERView.getHorizontalScrollBar().setUnitIncrement(6);
		spERView.setBorder(new EmptyBorder(0, 0, 0, 0));
		
		AdjustmentListener adjustmentListener = new AdjustmentListener()
		{
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e)
			{
				JViewport viewPort = spERView.getViewport();
				erView.setVisibleRect(viewPort.getViewRect());
			}
		};
		spERView.getHorizontalScrollBar().addAdjustmentListener(adjustmentListener);
		spERView.getVerticalScrollBar().addAdjustmentListener(adjustmentListener);
		
		frame.add(spERView);
		
		editor = new EntityEditor();
		erView.setERSelectionNotifier(editor);
		erView.setERSelectionNotifier(layout);
		editor.setRepaintRequest(erView);
		editor.setModelQuery(erView);
		editor.setChangeHistory(changeHistory);
		frame.add(editor);
		
		JMenuBar menuBar = new JMenuBar();
		
		JMenu menuFile = new JMenu();
		menuFile.setText(LOCALIZATION.getString("file_menu"));
		{
			menuNew = new JMenuItem();
			menuNew.setText(LOCALIZATION.getString("new_diagram"));
			menuNew.setMnemonic('N');
			menuNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			menuNew.addActionListener(this);
			menuFile.add(menuNew);
			
			menuOpen = new JMenuItem();
			menuOpen.setText(LOCALIZATION.getString("open_diagram"));
			menuOpen.setMnemonic('O');
			menuOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			menuOpen.addActionListener(this);
			menuFile.add(menuOpen);
			
			menuFile.addSeparator();
			
			menuSave = new JMenuItem();
			menuSave.setText(LOCALIZATION.getString("save_diagram"));
			menuSave.setMnemonic('S');
			menuSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			menuSave.addActionListener(this);
			menuFile.add(menuSave);
			
			menuSaveAs = new JMenuItem();
			menuSaveAs.setText(LOCALIZATION.getString("save_diagram_as"));
			menuSaveAs.setAccelerator(
					KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | Event.SHIFT_MASK));
			menuSaveAs.addActionListener(this);
			menuFile.add(menuSaveAs);
			
			menuFile.addSeparator();
			
			menuExport = new JMenu();
			menuExport.setText(LOCALIZATION.getString("export_diagram"));
			{
				menuExportImage = new JMenuItem();
				menuExportImage.setText(LOCALIZATION.getString("export_image"));
				menuExportImage.addActionListener(this);
				menuExport.add(menuExportImage);
				
				menuExportModel = new JMenuItem();
				menuExportModel.setText(LOCALIZATION.getString("export_model"));
				menuExportModel.addActionListener(this);
				menuExport.add(menuExportModel);
			}
			menuFile.add(menuExport);
		}
		menuBar.add(menuFile);
		
		JMenu menuEdit = new JMenu();
		menuEdit.setText(LOCALIZATION.getString("edit_menu"));
		{
			menuUndo = new JMenuItem();
			menuUndo.setText(LOCALIZATION.getString("undo"));
			menuUndo.setMnemonic('Z');
			menuUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			menuUndo.addActionListener(this);
			menuUndo.setEnabled(false);
			menuEdit.add(menuUndo);
			
			menuRedo = new JMenuItem();
			menuRedo.setText(LOCALIZATION.getString("redo"));
			menuRedo.setAccelerator(
					KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | Event.SHIFT_MASK));
			menuRedo.addActionListener(this);
			menuRedo.setEnabled(false);
			menuEdit.add(menuRedo);
			
			menuEdit.addSeparator();
			
			menuCut = new JMenuItem();
			menuCut.setText(LOCALIZATION.getString("cut"));
			menuCut.setMnemonic('X');
			menuCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			menuCut.setActionCommand((String) TransferHandler.getCutAction().getValue(Action.NAME));
			menuCut.addActionListener(this);
			menuEdit.add(menuCut);
			
			menuCopy = new JMenuItem();
			menuCopy.setText(LOCALIZATION.getString("copy"));
			menuCopy.setMnemonic('C');
			menuCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			menuCopy.setActionCommand((String) TransferHandler.getCopyAction().getValue(Action.NAME));
			menuCopy.addActionListener(this);
			menuEdit.add(menuCopy);
			
			menuPaste = new JMenuItem();
			menuPaste.setText(LOCALIZATION.getString("paste"));
			menuPaste.setMnemonic('V');
			menuPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			menuPaste.setActionCommand((String) TransferHandler.getPasteAction().getValue(Action.NAME));
			menuPaste.addActionListener(this);
			menuEdit.add(menuPaste);
			
			menuEdit.addSeparator();
			
			menuSelectAll = new JMenuItem();
			menuSelectAll.setText(LOCALIZATION.getString("select_all"));
			menuSelectAll.setMnemonic('A');
			menuSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			menuSelectAll.addActionListener(this);
			menuEdit.add(menuSelectAll);
			
			menuDelete = new JMenuItem();
			menuDelete.setText(LOCALIZATION.getString("delete"));
			menuDelete.setMnemonic('A');
			menuDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0));
			menuDelete.addActionListener(this);
			menuEdit.add(menuDelete);
			
		}
		menuBar.add(menuEdit);
		
		JMenu menuModel = new JMenu();
		menuModel.setText(LOCALIZATION.getString("model_menu"));
		{
			menuAddEntity = new JMenuItem();
			menuAddEntity.setText(LOCALIZATION.getString("add_entity"));
			menuAddEntity.setMnemonic('E');
			menuAddEntity.setAccelerator(
					KeyStroke.getKeyStroke(KeyEvent.VK_E, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | Event.SHIFT_MASK));
			menuAddEntity.addActionListener(this);
			menuModel.add(menuAddEntity);
			
			menuAddRelationship = new JMenuItem();
			menuAddRelationship.setMnemonic('R');
			menuAddRelationship.setAccelerator(
					KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | Event.SHIFT_MASK));
			menuAddRelationship.setText(LOCALIZATION.getString("add_relationship"));
			menuAddRelationship.addActionListener(this);
			menuModel.add(menuAddRelationship);
		}
		menuBar.add(menuModel);
		
		JMenu menuView = new JMenu();
		menuView.setText(LOCALIZATION.getString("view_menu"));
		{
			menuZoomOriginal = new JMenuItem();
			menuZoomOriginal.setText(LOCALIZATION.getString("zoom_original"));
			menuZoomOriginal.setMnemonic('0');
			menuZoomOriginal.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			menuZoomOriginal.addActionListener(this);
			menuView.add(menuZoomOriginal);
			
			menuZoomOut = new JMenuItem();
			menuZoomOut.setText(LOCALIZATION.getString("zoom_out"));
			menuZoomOut.setMnemonic('-');
			menuZoomOut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			menuZoomOut.addActionListener(this);
			menuView.add(menuZoomOut);
			
			menuZoomIn = new JMenuItem();
			menuZoomIn.setText(LOCALIZATION.getString("zoom_in"));
			menuZoomIn.setMnemonic('+');
			menuZoomIn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			menuZoomIn.addActionListener(this);
			menuView.add(menuZoomIn);
			
			menuImplode = new JMenuItem();
			menuImplode.setText(LOCALIZATION.getString("compress"));
			menuImplode.setAccelerator(
					KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | Event.SHIFT_MASK));
			menuImplode.addActionListener(this);
			menuView.add(menuImplode);
			
			menuExpand = new JMenuItem();
			menuExpand.setText(LOCALIZATION.getString("expand"));
			menuExpand.setAccelerator(
					KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | Event.SHIFT_MASK));
			menuExpand.addActionListener(this);
			menuView.add(menuExpand);
		}
		menuBar.add(menuView);
		
		JMenu menuHelp = new JMenu();
		menuHelp.setText(LOCALIZATION.getString("help_menu"));
		{
			menuItemHelp = new JMenuItem();
			menuItemHelp.setText(LOCALIZATION.getString("show_help"));
			menuItemHelp.setMnemonic('H');
			menuItemHelp.setAccelerator(
					KeyStroke.getKeyStroke(KeyEvent.VK_H, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | Event.SHIFT_MASK));
			menuItemHelp.addActionListener(this);
			menuHelp.add(menuItemHelp);
		}
		menuBar.add(menuHelp);
		
		frame.setJMenuBar(menuBar);
		frame.setTitle("ER-Editor: " + erView.model.getFilename());
		frame.setVisible(true);
		
		if (System.getProperty("os.name").toLowerCase().startsWith("mac os x"))
		{
			com.apple.eawt.Application app = com.apple.eawt.Application.getApplication();
			app.setAboutHandler(new com.apple.eawt.AboutHandler()
			{
				@Override
				public void handleAbout(com.apple.eawt.AppEvent.AboutEvent arg0)
				{
					showAboutWindow();
				}
			});
		}
	}
	
	@Override
	public void actionPerformed(final ActionEvent e)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				if (e.getSource() == menuNew)
				{
					if (!erView.model.isEmpty())
						if (JOptionPane.showConfirmDialog(frame, LOCALIZATION.getString("save_confirmation"),
								LOCALIZATION.getString("save_confirm_option"), JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION)
							erView.model.save(frame);
					changeHistory.reset();
					erView.model = new ERModel();
				}
				else if (e.getSource() == menuOpen)
				{
					if (!erView.model.isEmpty())
						if (JOptionPane.showConfirmDialog(frame, LOCALIZATION.getString("save_confirmation"),
								LOCALIZATION.getString("save_confirm_option"), JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION)
							erView.model.save(frame);
					changeHistory.reset();
					ERModel model = ERModel.open(frame);
					erView.model = (model == null) ? new ERModel() : model;
				}
				else if (e.getSource() == menuSave)
					erView.model.save(frame);
				else if (e.getSource() == menuSaveAs)
					erView.model.saveAs(frame);
				else if (e.getSource() == menuExportImage)
					erView.model.export();
				else if (e.getSource() == menuExportModel)
					erView.model.exportModel();
				else if (e.getSource() == menuUndo)
				{
					if (changeHistory.canUndo())
					{
						changeHistory.undo();
						erView.deselectAll();
					}
				}
				else if (e.getSource() == menuRedo)
				{
					if (changeHistory.canRedo())
					{
						changeHistory.redo();
						erView.deselectAll();
					}
				}
				else if (e.getSource() == menuCut)
				{
					if (erView.isFocusOwner())
						try
						{
							erView.cutSelected();
						}
						catch (IOException e1)
						{
							e1.printStackTrace();
						}
					// TODO CUT FOR EntityEditor/RelationshipEditor
				}
				else if (e.getSource() == menuCopy)
				{
					if (erView.isFocusOwner())
						try
						{
							erView.copySelected();
						}
						catch (IOException e1)
						{
							e1.printStackTrace();
						}
					// TODO COPY FOR EntityEditor/RelationshipEditor
				}
				else if (e.getSource() == menuPaste)
				{
					if (erView.isFocusOwner())
						try
						{
							erView.paste();
						}
						catch (HeadlessException | ClassNotFoundException | UnsupportedFlavorException | IOException e1)
						{
							e1.printStackTrace();
						}
					// TODO PASTE FOR EntityEditor/RelationshipEditor
				}
				else if (e.getSource() == menuSelectAll)
					erView.selectAll();
				else if (e.getSource() == menuDelete)
					erView.deleteSelected();
				else if (e.getSource() == menuAddEntity)
					erView.addEntity();
				else if (e.getSource() == menuAddRelationship)
					erView.requestRelationship();
				else if (e.getSource() == menuZoomOriginal)
					erView.zoomOriginal();
				else if (e.getSource() == menuZoomIn)
					erView.zoomIn();
				else if (e.getSource() == menuZoomOut)
					erView.zoomOut();
				else if (e.getSource() == menuImplode)
					erView.implode();
				else if (e.getSource() == menuExpand)
					erView.expand();
				else if (e.getSource() == menuItemHelp)
					new HelpWindow();
				erView.repaint();
				frame.setTitle("ER-Editor: " + erView.model.getFilename());
			}
		});
	}
	
	@Override
	public void historyDidChange(ERChangeHistory history)
	{
		menuUndo.setEnabled(history.canUndo());
		menuRedo.setEnabled(history.canRedo());
		if (history.canUndo())
			menuUndo.setText(String.format(LOCALIZATION.getString("undo_with_format"), history.peekUndo()));
		else
			menuUndo.setText(LOCALIZATION.getString("undo"));
		if (history.canRedo())
			menuRedo.setText(String.format(LOCALIZATION.getString("redo_with_format"), history.peekRedo()));
		else
			menuRedo.setText(LOCALIZATION.getString("redo"));
	}
	
	public void showAboutWindow()
	{
		JDialog about = new JDialog(frame);
		about.setBounds(about.getParent().getX() + about.getParent().getWidth() / 2 - 100,
				about.getParent().getY() + about.getParent().getHeight() / 2 - 100, 200, 200);
		about.setLayout(null);
		about.setResizable(false);
		
		JLabel appName = new JLabel();
		appName.setBounds(0, 0, 200, 180);
		appName.setText("<html><head><style>body { text-align: center; width: 150px; }</style></head>"
				+ "<body><h1>ER-Editor</h1><p>v3.4.0<br><br>&copy; 2014 - 2015 Palle</p>");
		about.add(appName);
		about.setVisible(true);
	}
}
