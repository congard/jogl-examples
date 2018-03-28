/*
 * sl - several lamps
 */
package example.congard.jogl.shaders.sllighting.texture;

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
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame jFrame = new JFrame ("Shaders");
				jFrame.setSize(640, 640);
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
				
				// for animation
				//FPSAnimator fps = new FPSAnimator(glJPanel, frames_per_second, false);
				//fps.start();
				
				jFrame.getContentPane().add(glJPanel);
				
				jFrame.setVisible(true);
			}
		});
	}
}
