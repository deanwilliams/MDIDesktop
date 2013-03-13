package org.codemonkeyism.mdidesktop;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.UIManager;

/**
 * Class used to replace the standard DesktopManager for JDesktopPane. Used to
 * provide scrollbar functionality.
 * 
 * @author Dean
 */
public class MDIDesktopManager extends DefaultDesktopManager {

	private static final long serialVersionUID = 1L;

	private static final int MIN_ICONIFIED_WIDTH = 150;

	private MDIDesktopPane desktop;

	/**
	 * Construct an MDI Desktop Manager
	 * 
	 * @param desktop
	 */
	public MDIDesktopManager(MDIDesktopPane desktop) {
		this.desktop = desktop;
	}

	/**
	 * End resizing frame
	 * 
	 * @param resizingFrame
	 */
	public void endResizingFrame(JComponent resizingFrame) {
		super.endResizingFrame(resizingFrame);
		resizeDesktop();
	}

	/**
	 * End dragging frame
	 * 
	 * @param draggingFrame
	 */
	public void endDraggingFrame(JComponent draggingFrame) {
		super.endDraggingFrame(draggingFrame);
		resizeDesktop();
	}

	/**
	 * Set the normal size of the desktop
	 */
	public void setNormalSize() {
		JScrollPane scrollPane = getScrollPane();
		int x = 0;
		int y = 0;
		Insets scrollInsets = getScrollPaneInsets();

		if (scrollPane != null) {
			Dimension d = scrollPane.getVisibleRect().getSize();
			if (scrollPane.getBorder() != null) {
				d.setSize(
						d.getWidth() - scrollInsets.left - scrollInsets.right,
						d.getHeight() - scrollInsets.top - scrollInsets.bottom);
			}

			d.setSize(d.getWidth() - 20, d.getHeight() - 20);
			desktop.setAllSize(x, y);
			scrollPane.invalidate();
			scrollPane.validate();
		}
	}

	/**
	 * Get the scrollpane insets
	 * 
	 * @return Insets
	 */
	private Insets getScrollPaneInsets() {
		JScrollPane scrollPane = getScrollPane();
		if (scrollPane == null)
			return new Insets(0, 0, 0, 0);
		else
			return getScrollPane().getBorder().getBorderInsets(scrollPane);
	}

	/**
	 * Get the scrollpane
	 * 
	 * @return JScrollPane
	 */
	private JScrollPane getScrollPane() {
		if (desktop.getParent() instanceof JViewport) {
			JViewport viewPort = (JViewport) desktop.getParent();
			if (viewPort.getParent() instanceof JScrollPane)
				return (JScrollPane) viewPort.getParent();
		}
		return null;
	}

	@Override
	protected Rectangle getBoundsForIconOf(JInternalFrame f) {
		Rectangle currentBounds = super.getBoundsForIconOf(f);
		int desktopWidth = computeDesktopIconWidth(MIN_ICONIFIED_WIDTH,
				f.getTitle());
		currentBounds.setSize(desktopWidth, currentBounds.height);
		return currentBounds;
	}

	/**
	 * Compute an appropriate desktop icon width based on the title of the frame
	 * 
	 * @param minWidth
	 * @param title
	 * @return
	 */
	private int computeDesktopIconWidth(int minWidth, String title) {
		FontRenderContext frc = new FontRenderContext(null, false, false);
		Font font = UIManager.getFont("InternalFrame.titleFont");
		
		int maxTitleWidth = 0;

		Rectangle2D r = new TextLayout(title, font, frc).getBounds();
		int textWidth = (int) r.getWidth();
		
		if (textWidth > maxTitleWidth) {
			maxTitleWidth = textWidth;
		}
		int totalWidth = minWidth + maxTitleWidth;
		return totalWidth;
	}

	/**
	 * Calculate the needed internal desktop size and set it
	 */
	protected void resizeDesktop() {
		int x = 0;
		int y = 0;
		JScrollPane scrollPane = getScrollPane();
		Insets scrollInsets = getScrollPaneInsets();

		if (scrollPane != null) {
			JInternalFrame allFrames[] = desktop.getAllFrames();
			for (int i = 0; i < allFrames.length; i++) {
				if (allFrames[i].getX() + allFrames[i].getWidth() > x) {
					x = allFrames[i].getX() + allFrames[i].getWidth();
				}
				if (allFrames[i].getY() + allFrames[i].getHeight() > y) {
					y = allFrames[i].getY() + allFrames[i].getHeight();
				}
			}
			Dimension d = scrollPane.getVisibleRect().getSize();
			if (scrollPane.getBorder() != null) {
				d.setSize(
						d.getWidth() - scrollInsets.left - scrollInsets.right,
						d.getHeight() - scrollInsets.top - scrollInsets.bottom);
			}

			if (x <= d.getWidth())
				x = ((int) d.getWidth()) - 20;
			if (y <= d.getHeight())
				y = ((int) d.getHeight()) - 20;
			desktop.setAllSize(x, y);
			scrollPane.invalidate();
			scrollPane.validate();
		}
	}
}