import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.io.IOException;
import java.lang.reflect.Method;

/**
  * EReditor
  * ERFrame.java
  * Created by Palle Klewitz on 01.11.2015
  * Copyright (c) 2014 - 2017 Palle.
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

public class ERFrame extends JFrame implements ActionListener, ERHistoryChangeNotifier
{
	private static final long	serialVersionUID	= -3678869730031898536L;
	private JScrollPane			spERView;
	private ERView				erView;
	private EntityEditor		editor;
	
	private JMenuItem	menuNew, menuOpen, menuClose, menuSave, menuSaveAs, menuExportImage, menuExportModel;
	private JMenuItem	menuCut, menuCopy, menuPaste, menuDelete, menuUndo, menuRedo, menuSelectAll;
	private JMenuItem	menuAddEntity, menuAddRelationship, menuAddDescriptionBox;
	private JMenuItem	menuZoomOriginal, menuZoomIn, menuZoomOut, menuExpand, menuImplode;
	private JMenuItem	menuItemHelp, menuItemAbout;
	private JMenu		menuExport;
	
	private ERChangeHistory changeHistory;
	
	public ERFrame() throws HeadlessException
	{
		super();
		init();
	}
	
	public ERFrame(GraphicsConfiguration gc)
	{
		super(gc);
		init();
	}
	
	public ERFrame(String title) throws HeadlessException
	{
		super(title);
		init();
	}
	
	public ERFrame(String title, GraphicsConfiguration gc)
	{
		super(title, gc);
		init();
	}
	
	@Override
	public void actionPerformed(final ActionEvent e)
	{
		final JFrame self = this;
		SwingUtilities.invokeLater(() ->
		{
			if (e.getSource() == menuNew)
			{
				// if (!erView.model.isEmpty())
				// if (JOptionPane.showConfirmDialog(self,
				// ER_Editor.LOCALIZATION.getString("save_confirmation"),
				// ER_Editor.LOCALIZATION.getString("save_confirm_option"),
				// JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION)
				// erView.model.save(self);
				// changeHistory.reset();
				ERFrame newFrame = new ERFrame();
				newFrame.setModel(new ERModel());
				// erView.model = new ERModel();
			}
			else if (e.getSource() == menuOpen)
			{
				// if (!erView.model.isEmpty())
				// if (JOptionPane.showConfirmDialog(self,
				// ER_Editor.LOCALIZATION.getString("save_confirmation"),
				// ER_Editor.LOCALIZATION.getString("save_confirm_option"),
				// JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION)
				// erView.model.save(self);
				// changeHistory.reset();
				Thread loadThread = new Thread(() ->
				{
					ERModel model = ERModel.open(self);
					SwingUtilities.invokeLater(() ->
					{
						ERFrame newFrame = new ERFrame();
						newFrame.setModel((model == null) ? new ERModel() : model);
					});
				});
				loadThread.start();
			}
			else if (e.getSource() == menuClose)
				self.dispatchEvent(new WindowEvent(self, WindowEvent.WINDOW_CLOSING));
			else if (e.getSource() == menuSave)
			{
				getRootPane().putClientProperty("Window.documentModified", Boolean.TRUE);
				erView.model.save(self);
			}
			else if (e.getSource() == menuSaveAs)
				erView.model.saveAs(self);
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
			else if (e.getSource() == menuAddDescriptionBox)
				erView.addDescriptionBox();
			else if (e.getSource() == menuZoomOriginal)
				erView.zoomOriginal();
			else if (e.getSource() == menuZoomIn)
				erView.zoomIn();
			else if (e.getSource() == menuZoomOut)
				erView.zoomOut();
			else if (e.getSource() == menuImplode)
				erView.shrink();
			else if (e.getSource() == menuExpand)
				erView.expand();
			else if (e.getSource() == menuItemHelp)
				new HelpWindow();
			else if (e.getSource() == menuItemAbout)
				showAboutWindow();
			erView.repaint();
			setTitle("ER-Editor: " + erView.model.getFilename());
		});
	}
	
	public ERModel getModel()
	{
		return erView.model;
	}
	
	@Override
	public void historyDidChange(ERChangeHistory history)
	{
		menuUndo.setEnabled(history.canUndo());
		menuRedo.setEnabled(history.canRedo());
		if (history.canUndo())
		{
			menuUndo.setText(String.format(ER_Editor.LOCALIZATION.getString("undo_with_format"), history.peekUndo()));
			getRootPane().putClientProperty("Window.documentModified", Boolean.TRUE);
		}
		else
		{
			menuUndo.setText(ER_Editor.LOCALIZATION.getString("undo"));
			getRootPane().putClientProperty("Window.documentModified", Boolean.FALSE);
		}
		if (history.canRedo())
			menuRedo.setText(String.format(ER_Editor.LOCALIZATION.getString("redo_with_format"), history.peekRedo()));
		else
			menuRedo.setText(ER_Editor.LOCALIZATION.getString("redo"));
	}
	
	public void setModel(ERModel model)
	{
		erView.model = model;
		getRootPane().putClientProperty("Window.documentFile", erView.model.fileName);
		setTitle("ER-Editor: " + erView.model.getFilename());
	}
	
	public void showAboutWindow()
	{
		JDialog about = new JDialog(this);
		about.setBounds(about.getParent().getX() + about.getParent().getWidth() / 2 - 100,
				about.getParent().getY() + about.getParent().getHeight() / 2 - 100, 200, 150);
		about.setLayout(null);
		about.setResizable(false);
		
		JLabel appName = new JLabel();
		appName.setBounds(0, 0, 200, 120);
		appName.setText("<html><head><style>body { text-align: center; width: 150px; }</style></head>"
				+ "<body><h1 style=\"font-weight:100;font-size:16px;\">ER-Editor</h1><p style=\"font-size:9px;\">v3.6.0<br><br>&copy; 2014 - 2016 Palle</p>");
		about.add(appName);
		about.setVisible(true);
	}
	
	@SuppressWarnings("MagicConstant")
	private void init()
	{
		final ERFrame self = this;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		ER_FrameLayout layout = new ER_FrameLayout();
		setLayout(layout);
		setBounds(0, 0, 1280, 720);
		setLocationByPlatform(true);
		if (!ER_Editor.openedFrames.isEmpty())
		{
			setLocationRelativeTo(ER_Editor.openedFrames.get(ER_Editor.openedFrames.size() - 1));
			Point location = getLocation();
			location.translate(22, 22);
			setLocation(location);
		}
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icon.png")));
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				if (!erView.model.isEmpty() && erView.model.needsSave())
					if (JOptionPane.showConfirmDialog(self, ER_Editor.LOCALIZATION.getString("save_confirmation"), ER_Editor.LOCALIZATION.getString("save_confirm_option"),
							JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION)
						erView.model.save(self);
				self.removeWindowListener(this);
				self.setVisible(false);
				self.dispose();
				ER_Editor.openedFrames.remove(self);
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
			method.invoke(util, this, true);
		}
		catch (Exception e)
		{
			System.err.println("Apple full screen utilities not found.");
		}
		changeHistory = new ERChangeHistory();
		changeHistory.setHistoryChangeNotifier(this);
		
		erView = new ERView();
		erView.setSize(3000, 2000);
		erView.setPreferredSize(new Dimension(3000, 2000));
		erView.setMinimumSize(new Dimension(3000, 2000));
		erView.setChangeHistory(changeHistory);
		spERView = new JScrollPane(erView, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		spERView.setAutoscrolls(true);
		spERView.getVerticalScrollBar().setUnitIncrement(6);
		spERView.getHorizontalScrollBar().setUnitIncrement(6);
		spERView.setBorder(new EmptyBorder(0, 0, 0, 0));
		
		AdjustmentListener adjustmentListener = e ->
		{
			JViewport viewPort = spERView.getViewport();
			erView.setVisibleRect(viewPort.getViewRect());
		};
		spERView.getHorizontalScrollBar().addAdjustmentListener(adjustmentListener);
		spERView.getVerticalScrollBar().addAdjustmentListener(adjustmentListener);
		
		add(spERView);
		
		editor = new EntityEditor();
		erView.setERSelectionNotifier(editor);
		erView.setERSelectionNotifier(layout);
		editor.setRepaintRequest(erView);
		editor.setModelQuery(erView);
		editor.setChangeHistory(changeHistory);
		add(editor);
		
		JMenuBar menuBar = new JMenuBar();
		
		JMenu menuFile = new JMenu();
		menuFile.setText(ER_Editor.LOCALIZATION.getString("file_menu"));
		
		{
			menuNew = new JMenuItem();
			menuNew.setText(ER_Editor.LOCALIZATION.getString("new_diagram"));
			menuNew.setMnemonic('N');
			menuNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			menuNew.addActionListener(this);
			menuFile.add(menuNew);
			
			menuOpen = new JMenuItem();
			menuOpen.setText(ER_Editor.LOCALIZATION.getString("open_diagram"));
			menuOpen.setMnemonic('O');
			menuOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			menuOpen.addActionListener(this);
			menuFile.add(menuOpen);
			
			menuFile.addSeparator();
			
			menuClose = new JMenuItem();
			menuClose.setText(ER_Editor.LOCALIZATION.getString("close_diagram"));
			menuClose.setMnemonic('W');
			menuClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			menuClose.addActionListener(this);
			menuFile.add(menuClose);
			
			menuSave = new JMenuItem();
			menuSave.setText(ER_Editor.LOCALIZATION.getString("save_diagram"));
			menuSave.setMnemonic('S');
			menuSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			menuSave.addActionListener(this);
			menuFile.add(menuSave);
			
			menuSaveAs = new JMenuItem();
			menuSaveAs.setText(ER_Editor.LOCALIZATION.getString("save_diagram_as"));
			menuSaveAs.setAccelerator(
					KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | Event.SHIFT_MASK));
			menuSaveAs.addActionListener(this);
			menuFile.add(menuSaveAs);
			
			menuFile.addSeparator();
			
			menuExport = new JMenu();
			menuExport.setText(ER_Editor.LOCALIZATION.getString("export_diagram"));
			{
				menuExportImage = new JMenuItem();
				menuExportImage.setText(ER_Editor.LOCALIZATION.getString("export_image"));
				menuExportImage.addActionListener(this);
				menuExport.add(menuExportImage);
				
				menuExportModel = new JMenuItem();
				menuExportModel.setText(ER_Editor.LOCALIZATION.getString("export_model"));
				menuExportModel.addActionListener(this);
				menuExport.add(menuExportModel);
			}
			menuFile.add(menuExport);
		}
		menuBar.add(menuFile);
		
		JMenu menuEdit = new JMenu();
		menuEdit.setText(ER_Editor.LOCALIZATION.getString("edit_menu"));
		
		{
			menuUndo = new JMenuItem();
			menuUndo.setText(ER_Editor.LOCALIZATION.getString("undo"));
			menuUndo.setMnemonic('Z');
			menuUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			menuUndo.addActionListener(this);
			menuUndo.setEnabled(false);
			menuEdit.add(menuUndo);
			
			menuRedo = new JMenuItem();
			menuRedo.setText(ER_Editor.LOCALIZATION.getString("redo"));
			menuRedo.setAccelerator(
					KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | Event.SHIFT_MASK));
			menuRedo.addActionListener(this);
			menuRedo.setEnabled(false);
			menuEdit.add(menuRedo);
			
			menuEdit.addSeparator();
			
			menuCut = new JMenuItem();
			menuCut.setText(ER_Editor.LOCALIZATION.getString("cut"));
			menuCut.setMnemonic('X');
			menuCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			menuCut.setActionCommand((String) TransferHandler.getCutAction().getValue(Action.NAME));
			menuCut.addActionListener(this);
			menuEdit.add(menuCut);
			
			menuCopy = new JMenuItem();
			menuCopy.setText(ER_Editor.LOCALIZATION.getString("copy"));
			menuCopy.setMnemonic('C');
			menuCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			menuCopy.setActionCommand((String) TransferHandler.getCopyAction().getValue(Action.NAME));
			menuCopy.addActionListener(this);
			menuEdit.add(menuCopy);
			
			menuPaste = new JMenuItem();
			menuPaste.setText(ER_Editor.LOCALIZATION.getString("paste"));
			menuPaste.setMnemonic('V');
			menuPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			menuPaste.setActionCommand((String) TransferHandler.getPasteAction().getValue(Action.NAME));
			menuPaste.addActionListener(this);
			menuEdit.add(menuPaste);
			
			menuEdit.addSeparator();
			
			menuSelectAll = new JMenuItem();
			menuSelectAll.setText(ER_Editor.LOCALIZATION.getString("select_all"));
			menuSelectAll.setMnemonic('A');
			menuSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			menuSelectAll.addActionListener(this);
			menuEdit.add(menuSelectAll);
			
			menuDelete = new JMenuItem();
			menuDelete.setText(ER_Editor.LOCALIZATION.getString("delete"));
			menuDelete.setMnemonic('D');
			menuDelete.setAccelerator(KeyStroke.getKeyStroke(System.getProperty("os.name").equals("Mac OS X") ? KeyEvent.VK_BACK_SPACE : KeyEvent.VK_DELETE, 0));
			menuDelete.addActionListener(this);
			menuEdit.add(menuDelete);
		}
		menuBar.add(menuEdit);
		
		JMenu menuModel = new JMenu();
		menuModel.setText(ER_Editor.LOCALIZATION.getString("model_menu"));
		
		{
			menuAddEntity = new JMenuItem();
			menuAddEntity.setText(ER_Editor.LOCALIZATION.getString("add_entity"));
			menuAddEntity.setMnemonic('E');
			menuAddEntity.setAccelerator(
					KeyStroke.getKeyStroke(KeyEvent.VK_E, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | Event.SHIFT_MASK));
			menuAddEntity.addActionListener(this);
			menuModel.add(menuAddEntity);
			
			menuAddRelationship = new JMenuItem();
			menuAddRelationship.setMnemonic('R');
			menuAddRelationship.setAccelerator(
					KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | Event.SHIFT_MASK));
			menuAddRelationship.setText(ER_Editor.LOCALIZATION.getString("add_relationship"));
			menuAddRelationship.addActionListener(this);
			menuModel.add(menuAddRelationship);

			menuAddDescriptionBox = new JMenuItem();
			menuAddDescriptionBox.setText(ER_Editor.LOCALIZATION.getString("add_description_box"));
			menuAddDescriptionBox.setMnemonic('D');
			menuAddDescriptionBox.setAccelerator(
					KeyStroke.getKeyStroke(KeyEvent.VK_D, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | Event.SHIFT_MASK));
			menuAddDescriptionBox.addActionListener(this);
			menuModel.add(menuAddDescriptionBox);
		}
		menuBar.add(menuModel);
		
		JMenu menuView = new JMenu();
		menuView.setText(ER_Editor.LOCALIZATION.getString("view_menu"));
		
		{
			menuZoomOriginal = new JMenuItem();
			menuZoomOriginal.setText(ER_Editor.LOCALIZATION.getString("zoom_original"));
			menuZoomOriginal.setMnemonic('0');
			menuZoomOriginal.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			menuZoomOriginal.addActionListener(this);
			menuView.add(menuZoomOriginal);
			
			menuZoomOut = new JMenuItem();
			menuZoomOut.setText(ER_Editor.LOCALIZATION.getString("zoom_out"));
			menuZoomOut.setMnemonic('-');
			menuZoomOut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			menuZoomOut.addActionListener(this);
			menuView.add(menuZoomOut);
			
			menuZoomIn = new JMenuItem();
			menuZoomIn.setText(ER_Editor.LOCALIZATION.getString("zoom_in"));
			menuZoomIn.setMnemonic('+');
			menuZoomIn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			menuZoomIn.addActionListener(this);
			menuView.add(menuZoomIn);
			
			menuImplode = new JMenuItem();
			menuImplode.setText(ER_Editor.LOCALIZATION.getString("compress"));
			menuImplode.setAccelerator(
					KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | Event.SHIFT_MASK));
			menuImplode.addActionListener(this);
			menuView.add(menuImplode);
			
			menuExpand = new JMenuItem();
			menuExpand.setText(ER_Editor.LOCALIZATION.getString("expand"));
			menuExpand.setAccelerator(
					KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | Event.SHIFT_MASK));
			menuExpand.addActionListener(this);
			menuView.add(menuExpand);
		}
		menuBar.add(menuView);
		
		JMenu menuHelp = new JMenu();
		menuHelp.setText(ER_Editor.LOCALIZATION.getString("help_menu"));
		
		{
			menuItemHelp = new JMenuItem();
			menuItemHelp.setText(ER_Editor.LOCALIZATION.getString("show_help"));
			menuItemHelp.setMnemonic('H');
			menuItemHelp.setAccelerator(
					KeyStroke.getKeyStroke(KeyEvent.VK_H, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | Event.SHIFT_MASK));
			menuItemHelp.addActionListener(this);
			menuHelp.add(menuItemHelp);
			
			menuItemAbout = new JMenuItem();
			menuItemAbout.setText(ER_Editor.LOCALIZATION.getString("about_menu"));
			menuItemAbout.setMnemonic('A');
			menuItemAbout.addActionListener(this);
			menuHelp.add(menuItemAbout);
		}
		menuBar.add(menuHelp);
		
		setJMenuBar(menuBar);
		setTitle("ER-Editor: " + erView.model.getFilename());
		ER_Editor.openedFrames.add(this);
		setVisible(true);
	}
}
