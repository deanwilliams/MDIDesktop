package org.codemonkeyism.mdidesktop;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

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

		Rectangle rec = optionPane.getBounds();
		setBounds(rec.x, rec.y, rec.width, rec.height);
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
		
		pack();
	}

}
