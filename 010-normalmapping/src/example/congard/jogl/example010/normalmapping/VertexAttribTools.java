package example.congard.jogl.example010.normalmapping;

import java.nio.Buffer;

import com.jogamp.opengl.GL2;

public class VertexAttribTools {
	public static void enable(GL2 gl2, int...array) {
		for (int i = 0; i<array.length; i++) gl2.glEnableVertexAttribArray(array[i]);
	}
	
	public static void disable(GL2 gl2, int...array) {
		for (int i = 0; i<array.length; i++) gl2.glDisableVertexAttribArray(array[i]);
	}
	
	public static void pointer(GL2 gl2, int location, int count, Buffer buffer) {
		gl2.glVertexAttribPointer(location, count, GL2.GL_FLOAT, false, 0, buffer);
	}
}
