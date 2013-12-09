package org.codemonkeyism.mdidesktop;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Wraps <code>JOptionPane</code> so that the modality is controlled.
 * 
 * @author Dean
 */
public class MDIDesktopOptionFrame extends MDIDesktopFrame {

	private static final long serialVersionUID = 1L;

	public MDIDesktopOptionFrame(final JComponent parentComponent,
			final JOptionPane optionPane, String title) {

		super(parentComponent, title, false, false, false, false);
		init();
		
		optionPane.setValue(null); // default to null

		if (desktopPane == null
				&& (parentComponent == null || (parentComponent.getParent() == null))) {
			throw new RuntimeException(
					"MDIDesktopOptionFrame: parentComponent does not have a valid parent");
		}

		// overide default layout
		getContentPane().setLayout(new BorderLayout());

		// Add pane to frame
		getContentPane().add(optionPane, BorderLayout.CENTER);

		final MDIDesktopOptionFrame frame = this;

		// Add listener for value changes
		optionPane.addPropertyChangeListener(new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent event) {
				// Let the defaultCloseOperation handle the closing
				// if the user closed the iframe without selecting a button
				// (newValue = null in that case). Otherwise, close the dialog.
				if (frame.isVisible()
						&& event.getSource() == optionPane
						&& event.getPropertyName().equals(
								JOptionPane.VALUE_PROPERTY)) {
					try {
						frame.setClosed(true);
					} catch (PropertyVetoException ex) {
						// Well its an exception but in reality its no
						// exceptional behaviour!
					}
					frame.setVisible(false);
				}
			}
		});
	}
	
	protected void init() {
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 394, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 274, Short.MAX_VALUE));

        pack();
    }

	
	public void calculateBounds() {
        Dimension frameSize = getPreferredSize();
        Dimension parentSize = new Dimension();
        Dimension rootSize = new Dimension(); // size of desktop
        Point frameCoord = new Point();

        if (desktopPane != null) {
            rootSize = desktopPane.getSize(); // size of desktop
            frameCoord = SwingUtilities.convertPoint(parent, 0, 0, desktopPane);
            parentSize = parent.getSize();
        }

        //setBounds((rootSize.width - frameSize.width) / 2, (rootSize.height - frameSize.height) / 2, frameSize.width, frameSize.height);

        // We want dialog centered relative to its parent component
        int x = (parentSize.width - frameSize.width) / 2 + frameCoord.x;
        int y = (parentSize.height - frameSize.height) / 2 + frameCoord.y;

        // If possible, dialog should be fully visible
        int ovrx = x + frameSize.width - rootSize.width;
        int ovry = y + frameSize.height - rootSize.height;
        x = Math.max((ovrx > 0 ? x - ovrx : x), 0);
        y = Math.max((ovry > 0 ? y - ovry : y), 0);
        setBounds(x, y, frameSize.width, frameSize.height);
    }

}
