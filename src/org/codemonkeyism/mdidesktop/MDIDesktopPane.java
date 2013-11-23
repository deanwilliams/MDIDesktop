package org.codemonkeyism.mdidesktop;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.beans.PropertyVetoException;

import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.Painter;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

/**
 * An extension of JDesktopPane that supports often used MDI functionality. This
 * class also handles setting scroll bars for when windows move too far to the
 * left or bottom, providing the MDIDesktopPane is in a ScrollPane.
 * 
 * @author Dean
 */
public class MDIDesktopPane extends JDesktopPane {

	private static final long serialVersionUID = 1L;

	private static int FRAME_OFFSET = 20;
	private MDIDesktopManager manager;

	public MDIDesktopPane() {
		manager = new MDIDesktopManager(this);
		setDesktopManager(manager);
	}

	/**
	 * Set the bounds
	 */
	public void setBounds(int x, int y, int w, int h) {
		super.setBounds(x, y, w, h);
		checkDesktopSize();
	}

	@Override
	protected void addImpl(Component comp, Object constraints, int index) {
		super.addImpl(comp, constraints, index);
		checkDesktopSize();
	}

	/**
	 * Add an internal frame to this desktop
	 * 
	 * @param frame
	 * @return
	 */
	public Component add(JInternalFrame frame) {
		// JInternalFrame[] array = getAllFrames();
		// Point p;

		Component retval = super.add(frame);

//		if (array.length > 0) {
//			p = array[0].getLocation();
//			p.x = p.x + FRAME_OFFSET;
//			p.y = p.y + FRAME_OFFSET;
//		} else {
//			p = new Point(0, 0);
//		}
		
//		frame.setLocation(p.x, p.y);
		
		frame.setLocation(frame.getBounds().x, frame.getBounds().y);
		
		moveToFront(frame);
		frame.setVisible(true);
		try {
			frame.setSelected(true);
		} catch (PropertyVetoException e) {
			frame.toBack();
		}
		checkDesktopSize();
		return retval;
	}

	/**
	 * Remove a component
	 */
	public void remove(Component c) {
		super.remove(c);
		checkDesktopSize();
	}

	/**
	 * Cascade all internal frames
	 */
	public void cascadeFrames() {
		int x = 0;
		int y = 0;
		JInternalFrame[] allFrames = getAllFrames();

		manager.setNormalSize();
		// FRAME_OFFSET;
		for (int i = allFrames.length - 1; i >= 0; i--) {
			allFrames[i].setLocation(x, y);
			x = x + FRAME_OFFSET;
			y = y + FRAME_OFFSET;
		}
	}

	/**
	 * Tile all internal frames
	 */
	public void tileFrames() {
		// How many frames do we have?
		JInternalFrame[] allframes = getAllFrames();
		int count = allframes.length;
		if (count == 0)
			return;

		// Determine the necessary grid size
		int sqrt = (int) Math.sqrt(count);
		int rows = sqrt;
		int cols = sqrt;
		if (rows * cols < count) {
			cols++;
			if (rows * cols < count) {
				rows++;
			}
		}

		// Define some initial values for size & location.
		Dimension size = getSize();

		int w = size.width / cols;
		int h = size.height / rows;
		int x = 0;
		int y = 0;

		// Iterate over the frames, deiconifying any iconified frames and then
		// relocating & resizing each.
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols && ((i * cols) + j < count); j++) {
				JInternalFrame f = allframes[(i * cols) + j];

				if (!f.isClosed() && f.isIcon()) {
					try {
						f.setIcon(false);
					} catch (PropertyVetoException ignored) {
					}
				}

				if (f.isResizable()) {
					getDesktopManager().resizeFrame(f, x, y, w, h);
				}
				x += w;
			}
			y += h; // start the next row
			x = 0;
		}

	}

	/**
	 * Iconfiy all internal frames
	 */
	public void minimizeAllFrames() {
		JInternalFrame[] allFrames = getAllFrames();
		for (int i = 0; i < allFrames.length; i++) {
			try {
				allFrames[i].setIcon(true);
			} catch (PropertyVetoException e) {
				allFrames[i].toBack();
			}
		}
	}

	/**
	 * Sets all component size properties ( maximum, minimum, preferred) to the
	 * given dimension.
	 */
	public void setAllSize(Dimension d) {
		setMinimumSize(d);
		setMaximumSize(d);
		setPreferredSize(d);
	}

	/**
	 * Sets all component size properties ( maximum, minimum, preferred) to the
	 * given width and height.
	 */
	public void setAllSize(int width, int height) {
		setAllSize(new Dimension(width, height));
	}

	/**
	 * Check the current desktop size. If we have a parent and are visible then
	 * calculate the correct size
	 */
	protected void checkDesktopSize() {
		if (getParent() != null && isVisible()) {
			manager.resizeDesktop();
		}
	}

	@Override
    public void updateUI() {
        if ("Nimbus".equals(UIManager.getLookAndFeel().getName())) {
            UIDefaults map = new UIDefaults();
            Painter<JComponent> painter = new Painter<JComponent>() {

                @Override
                public void paint(Graphics2D g, JComponent c, int w, int h) {
                    // file using normal desktop color
                    g.setColor(UIManager.getDefaults().getColor("desktop"));
                    g.fillRect(0, 0, w, h);
                }

            };
            map.put("DesktopPane[Enabled].backgroundPainter", painter);
            putClientProperty("Nimbus.Overrides", map);
        }
        super.updateUI();
    }

}