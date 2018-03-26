package example.congard.jogl.shaders.lighting.sl;

import java.io.File;
import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import free.lib.congard.ml.graphics.GMatrix;

/**
 * Rendering with shaders & lighting & model matrix
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
 * 
 * shaders base:
 * github.com/learnopengles
 */
public class Renderer implements GLEventListener {
	private Lamp[] lamps;
	private FloatBuffer vertexBuffer;
	private FloatBuffer normalsBuffer;
	private FloatBuffer colorBuffer;
	private ShaderProgram shaderProgram;
	private float[] mProjectionMatrix = new float[16];
	private float[] mViewMatrix = new float[16];
	private float[] mModelMatrix = new float[16];
	private float[] mMVPMatrix = new float[16];
	private float[] mMVMatrix = new float[16];
	private float[] allLamps;
	private float eyeX = 3, eyeY = 3, eyeZ = 3, // положение камеры / camera position
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
		normalsBuffer = Buffers.newDirectFloatBuffer(Cube.normals.length);
		colorBuffer = Buffers.newDirectFloatBuffer(Cube.colors.length);

		vertexBuffer.put(Cube.vertices);
		normalsBuffer.put(Cube.normals);
		colorBuffer.put(Cube.colors);

		gl2.glEnable(GL2.GL_DEPTH_TEST);
		gl2.glDepthMask(true);
		
		gl2.glClearColor(0.5f, 0.5f, 0.5f, 1);
		createMatrices();
		
		lamps = new Lamp[] {
			createLamp(-2, 2, 2), // lamp in (-2; 2; 2)
			createLamp(2, 2, -2)  // lamp in (2; 2; -2)
		};
		allLamps = Lamp.createFullList(lamps);
	}
	
	private void createMatrices() {
		System.out.println("Creating matrices >>");
		createProjectionMatrix(1, 1);
		createViewMatrix();
		createModelMatrix();
		createMVMatrix();
		createMVPMatrix();
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
	
	private void createModelMatrix() {
		GMatrix.setIdentityM(mModelMatrix, 0);
	}
	
	private void createMVMatrix() {
		GMatrix.multiplyMM(mMVMatrix, mViewMatrix, mModelMatrix);
		System.out.println("Final (View * Model) matrix created");
	}
	
	private void createMVPMatrix() {
		GMatrix.multiplyMM(mMVPMatrix, mViewMatrix, mModelMatrix);
		GMatrix.multiplyMM(mMVPMatrix, mProjectionMatrix, mMVPMatrix);
		System.out.println("Final (Projection * View * Model) matrix created");
	}
	
	private void bindMatrices(GL2 gl) {
		gl.glUniformMatrix4fv(shaderProgram.u_MVPMatrixLocation, 1, false, mMVPMatrix, 0);
		gl.glUniformMatrix4fv(shaderProgram.u_MVMatrixLocation, 1, false, mMVMatrix, 0);
		System.out.println("Bind to final matrices complete");
	}
	
	private Lamp createLamp(float x, float y, float z) {
		Lamp l = new Lamp();
		l.setX(x);
		l.setY(y);
		l.setZ(z);
		l.calculateLampPosInEyeSpace(mViewMatrix);
		return l;
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
		
		gl2.glEnableVertexAttribArray(shaderProgram.a_PositionLocation);
		gl2.glEnableVertexAttribArray(shaderProgram.a_ColorLocation);
		gl2.glEnableVertexAttribArray(shaderProgram.a_NormalLocation);

		gl2.glVertexAttribPointer(shaderProgram.a_PositionLocation, 3, GL2.GL_FLOAT, false, 0, vertexBuffer.rewind());
		gl2.glVertexAttribPointer(shaderProgram.a_ColorLocation, 4, GL2.GL_FLOAT, false, 0, colorBuffer.rewind());
		gl2.glVertexAttribPointer(shaderProgram.a_NormalLocation, 3, GL2.GL_FLOAT, false, 0, normalsBuffer.rewind());

		// sending matrices to vertex shader
		bindMatrices(gl2);
		// sending lamps positions to fragment shader
		gl2.glUniform3fv(shaderProgram.u_LightPosLocation, lamps.length, allLamps, 0);
		
		// draw cube start pos=0, end=Cube.vertices.length/3 (3 floats per vertex)
		gl2.glDrawArrays(GL2.GL_TRIANGLES, 0, Cube.vertices.length/3);
		
		gl2.glDisableVertexAttribArray(shaderProgram.a_PositionLocation);
		gl2.glDisableVertexAttribArray(shaderProgram.a_ColorLocation);
		gl2.glDisableVertexAttribArray(shaderProgram.a_NormalLocation);

		gl2.glUseProgram(0);
	}

	@Override
	public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
		System.out.println("reshape >>");
		createProjectionMatrix(width, height);
		createMVPMatrix();
		createMVMatrix();
		System.out.println("<<");
	}
}
