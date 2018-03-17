package example.congard.jogl.shaders;

import java.io.File;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import free.lib.congard.ml.graphics.GMatrix;

/**
 * Rendering with shaders
 * 
 * @author Congard
 * 
 * Links:
 * I:
 * t.me/congard
 * guthub.com/congard
 * dbcongard@gmail.com
 * 
 * serhiy:
 * github.com/serhiy
 */
public class Renderer implements GLEventListener {
	private FloatBuffer vertexBuffer;
	private IntBuffer indexBuffer;
	private FloatBuffer colorBuffer;
	private ShaderProgram shaderProgram;
	private float[] mProjectionMatrix = new float[16];
	private float[] mViewMatrix = new float[16];
	private float[] mMatrix = new float[16];
	private float eyeX = 2, eyeY = 0, eyeZ = 3, // положение камеры / camera position
			lookX = 0, lookY = 0, lookZ = 0,  // точка направления т.е куда смотрим / look point
			upX = 0, upY = 1, upZ = 0; // смотрим по оси у / look at the axis y
	
	@Override
	public void init(GLAutoDrawable glAutoDrawable) {
		GL2 gl2 = glAutoDrawable.getGL().getGL2();

		File vertexShader = new File("./resources/shaders/vertex_shader.glsl");
		File fragmentShader = new File("./resources/shaders/fragment_shader.glsl");

		shaderProgram = new ShaderProgram();
		if (!shaderProgram.init(gl2, vertexShader, fragmentShader)) {
			throw new IllegalStateException("Unable to initiate the shaders!");
		}
		System.out.println("Shaders created");

		vertexBuffer = Buffers.newDirectFloatBuffer(Cube.vertices.length);
		indexBuffer = Buffers.newDirectIntBuffer(Cube.indices.length);
		colorBuffer = Buffers.newDirectFloatBuffer(Cube.colors.length);

		vertexBuffer.put(Cube.vertices);
		indexBuffer.put(Cube.indices);
		colorBuffer.put(Cube.colors);

		gl2.glEnable(GL2.GL_DEPTH_TEST);
		gl2.glDepthMask(true);
		
		gl2.glClearColor(0.5f, 0.5f, 0.5f, 1);
		createMatrices();
	}
	
	private void createMatrices() {
		System.out.println("Creating matrices >>");
		createProjectionMatrix(1, 1);
		createViewMatrix();
		createPVMatrix();
		System.out.println("<<");
	}
	
	private void createProjectionMatrix(int width, int height) {
		float ratio = 1;
        float left = -1;
        float right = 1;
        float bottom = -1;
        float top = 1;
        float near = 2f, far = 24;
        if (width > height) {
            ratio = (float) width / (float) height;
            left *= ratio;
            right *= ratio;
        } else {
            ratio = (float) height / (float) width;
            bottom *= ratio;
            top *= ratio;
        }

        GMatrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
        System.out.println("Projection matrix created");
	}
	
	private void createViewMatrix() {
		GMatrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
		System.out.println("View matrix created");
	}
	
	private void createPVMatrix() {
		GMatrix.multiplyMM(mMatrix, mProjectionMatrix, mViewMatrix);
		System.out.println("Final (Projection * View) matrix created");
	}
	
	private void bindMatrix(GL2 gl) {
		gl.glUniformMatrix4fv(shaderProgram.pvMatrixLocation, 1, false, mMatrix, 0);
		System.out.println("Bind to final matrix complete");
	}

	@Override
	public void dispose(GLAutoDrawable glAutoDrawable) {
		GL2 gl2 = glAutoDrawable.getGL().getGL2();
		shaderProgram.dispose(gl2);
	}

	@Override
	public void display(GLAutoDrawable glAutoDrawable) {
		GL2 gl2 = glAutoDrawable.getGL().getGL2();

		gl2.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		gl2.glUseProgram(shaderProgram.getProgramId());
		
		gl2.glEnableVertexAttribArray(shaderProgram.inPositionLocation);
		gl2.glEnableVertexAttribArray(shaderProgram.inColorLocation);

		gl2.glVertexAttribPointer(shaderProgram.inPositionLocation, 3, GL2.GL_FLOAT, false, 0, vertexBuffer.rewind());
		gl2.glVertexAttribPointer(shaderProgram.inColorLocation, 3, GL2.GL_FLOAT, false, 0, colorBuffer.rewind());

		bindMatrix(gl2);
		
		gl2.glDrawElements(GL2.GL_TRIANGLES, Cube.indices.length, GL2.GL_UNSIGNED_INT, indexBuffer.rewind());
		
		gl2.glDisableVertexAttribArray(shaderProgram.inPositionLocation);
		gl2.glDisableVertexAttribArray(shaderProgram.inColorLocation);

		gl2.glUseProgram(0);
	}

	@Override
	public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
		System.out.println("reshape >>");
		createProjectionMatrix(width, height);
		createPVMatrix();
		System.out.println("<<");
	}
}
