package org.codemonkeyism.mdidesktop;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.swing.JInternalFrame;

public abstract class MDIDesktopAbstractController {

	public MDIDesktopAbstractView view;

	public MDIDesktopAbstractController(MDIDesktopAbstractView view) {
		super();
		this.view = checkNotNull(view);
	}

	/**
	 * Kick off this MVC
	 */
	public void start() {
		if (view != null) {
			view.setVisible(true);
			view.getFrame().toFront();
		}
	}
	
	public abstract void stop();

	public <T extends MDIDesktopFrame> void addFrame(T childFrame) {
		checkNotNull(childFrame);
		if (view != null) {
			view.addFrame(childFrame);
		}
	}
	
	public <T extends JInternalFrame> void removeFrame(T childFrame) {
		checkNotNull(childFrame);
		if (view != null) {
			view.removeFrame(childFrame);
		}
	}
	
	public void cascadeInternalFrames() {
		if (view != null) {
			view.cascadeInternalFrames();
		}
	}
	
	public void tileInternalFrames() {
		if (view != null) {
			view.tileInternalFrames();
		}
	}
	
	public void minimiseAllInternalFrames() {
		if (view != null) {
			view.minimiseAllInternalFrames();
		}
	}
	
	public int getChildFrameCount() {
		if (view != null) {
			return view.getDesktopPane().getAllFrames().length;
		}
		return 0;
	}
}
