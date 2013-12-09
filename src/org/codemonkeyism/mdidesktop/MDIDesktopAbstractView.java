package org.codemonkeyism.mdidesktop;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.beans.PropertyVetoException;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;

/**
 * MDI Desktop Controller
 * 
 * This is the base class to extend to add Multiple Document Interface Desktop
 * functionality to your project. This class will setup the basic layout of
 * frame, scroll pane and desktop pane. You can then add menu bars and menus etc
 * 
 * @author Dean
 * 
 */
public abstract class MDIDesktopAbstractView {

	private JFrame frame;
	private MDIDesktopPane desktopPane;
	private JMenuBar menuBar;

	/**
	 * All hail the mighty constructor
	 */
	public MDIDesktopAbstractView() {
		initialize();
	}

	/**
	 * Make the pane visible. Use lazy instantiation if initialize() has not yet
	 * been called.
	 * 
	 * @param visible
	 */
	public void setVisible(boolean visible) {
		if (visible && frame == null) {
			initialize();
		}
		frame.setVisible(visible);
	}

	/**
	 * Initialise the frame, scroll pane and desktop pane
	 */
	private void initialize() {
		frame = new JFrame();
		setBounds(100, 100, 800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JScrollPane scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

		desktopPane = new MDIDesktopPane();
		// desktopPane.setBackground(Color.LIGHT_GRAY);
		// desktopPane.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		scrollPane.setViewportView(desktopPane);
	}

	public JFrame getFrame() {
		return frame;
	}

	/**
	 * Set the main frame's bounds
	 * 
	 * @param originX
	 * @param originY
	 * @param width
	 * @param height
	 */
	public void setBounds(int originX, int originY, int width, int height) {
		frame.setBounds(originX, originY, width, height);
	}

	/**
	 * Add a menu bar to the main jframe
	 * 
	 * @param menuBar
	 * @param layout
	 */
	public void addMenuBar(JMenuBar menuBar, String borderLayout) {
		this.menuBar = menuBar;
		frame.getContentPane().add(menuBar, borderLayout);
	}

	/**
	 * Add a menu to the menubar
	 * 
	 * @param menu
	 */
	public void addMenu(JMenu menu) {
		if (menuBar == null) {
			return;
		}
		menuBar.add(menu);
	}

	/**
	 * Add an internal child frame to the desktop frame.
	 * 
	 * This also sets the parent component of the child frame to ensure there is
	 * no mismatch between various parents and children
	 * 
	 * @param childFrame
	 * @param title
	 */
	public synchronized <T extends MDIDesktopFrame> void addFrame(T childFrame) {
		// Add the new child frame to the parent desktop panel
		if (childFrame.getParentFrame() == null) {
			childFrame.setParentFrame(desktopPane);
		}
		desktopPane.add(childFrame);
		childFrame.setVisible(true);
		try {
			childFrame.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Remove an internal child frame from the desktop
	 * 
	 * @param childFrame
	 */
	public synchronized <T extends JInternalFrame> void removeFrame(T childFrame) {
		try {
			childFrame.setClosed(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		desktopPane.remove(childFrame);
	}

	/**
	 * Set the frame's icon
	 * 
	 * @param iconPath
	 */
	public void setIcon(String iconPath) {
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource(iconPath)));
	}

	/**
	 * Set the main frame title
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		frame.setTitle(title);
	}

	/**
	 * Return the desktop pane
	 * 
	 * @return desktopPane
	 */
	public MDIDesktopPane getDesktopPane() {
		if (desktopPane == null) {
			initialize();
		}
		return desktopPane;
	}

	/**
	 * Cascade all the internal frames
	 */
	public void cascadeInternalFrames() {
		desktopPane.cascadeFrames();
	}

	/**
	 * Tile all the internal frames
	 */
	public void tileInternalFrames() {
		desktopPane.tileFrames();
	}

	/**
	 * Minimise all the internal frames
	 */
	public void minimiseAllInternalFrames() {
		desktopPane.minimizeAllFrames();
	}
}
