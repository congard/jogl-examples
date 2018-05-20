/*
 * example 011: Shadow Mapping
 * github.com/congard
 */
package example.congard.jogl.example011.shadowmapping;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;

/**
 * The main class with respective GUI components.
 *
 * @author Congard
 */
public class Main {
	static boolean isFullScreen = !false;

	public static void main(String[] args) {
		final GLProfile glProfile = GLProfile.getDefault();
		final GLCapabilities glCapabilities = new GLCapabilities(glProfile);
		glCapabilities.setHardwareAccelerated(true);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame jFrame = new JFrame ("Shadow Mapping");
				jFrame.setSize(1024, 1024);
				jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				if (isFullScreen) {
					jFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
					jFrame.setUndecorated(true);
					jFrame.setAlwaysOnTop(true);
					jFrame.setResizable(false);
					GraphicsDevice d = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
					d.setFullScreenWindow(jFrame);
				}

				GLJPanel glJPanel = new GLJPanel(glCapabilities);
				glJPanel.addGLEventListener(new Renderer());
				glJPanel.setSize(jFrame.getSize());

				// GL FPS Animator
				// FPS: max
				new Thread(new Runnable() {
					@Override
					public void run() {
						while(true) glJPanel.repaint();
					}
				}).start();

				jFrame.getContentPane().add(glJPanel);

				jFrame.setVisible(true);
			}
		});
	}
}
