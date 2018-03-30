package example.congard.jogl.shaders.sllighting.texture;

import java.io.File;
import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import free.lib.congard.ml.geometry.Geometry;
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
 */
public class Renderer implements GLEventListener {
	private Lamp[] lamps;
	private FloatBuffer vertexBuffer;
	private FloatBuffer normalsBuffer;
	private FloatBuffer texCoordsBuffer;
	private ShaderProgram shaderProgram;
	private float[] mProjectionMatrix = new float[16];
	private float[] mViewMatrix = new float[16];
	private float[] mModelMatrix = new float[16];
	private float[] mMVPMatrix = new float[16];
	private float[] mMVMatrix = new float[16];
	private float[] allLamps, allLampsColors;
	private float eyeX = 5, eyeY = 5, eyeZ = 5, // положение камеры / camera position
			lookX = 0, lookY = 0, lookZ = 0,  // точка направления т.е куда смотрим / look point
			upX = 0, upY = 1, upZ = 0; // смотрим по оси у / look at the axis y
	private int textureId = 0;
	private float angleY = 0, angleZ = 0, angleX = 0;
	
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
		texCoordsBuffer = Buffers.newDirectFloatBuffer(Cube.texCoords.length);
		
		vertexBuffer.put(Cube.vertices);
		normalsBuffer.put(Cube.normals);
		texCoordsBuffer.put(Cube.texCoords);

		gl2.glEnable(GL2.GL_DEPTH_TEST);
		gl2.glDepthMask(true);
		
		gl2.glClearColor(0.5f, 0.5f, 0.5f, 1);
		createMatrices();
		
		textureId = loadTexture(gl2, new File("./resources/textures/brick.png"));
		// unit 0
		gl2.glActiveTexture(GL2.GL_TEXTURE0);
		gl2.glUniform1i(shaderProgram.u_TextureUnitLocation, 0); // 0 - unit 0
	}
	
	private static int loadTexture(GL2 gl, File path) {
		Texture t;
		try {
			t = TextureIO.newTexture(path, true);
			return t.getTextureObject(gl);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
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
		//System.out.println("Bind to final matrices complete");
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
		angleX += 2f;
		angleY += 2.5f;
		angleZ += 3f;
		if (angleX > 360) angleX = 0;
		if (angleY > 360) angleY = 0;
		if (angleZ > 360) angleZ = 0;
		float[] xyz3 = Geometry.rotateAroundXf(0, 0, 0, 0, 0, 2, (float)Math.sin(Math.toRadians(angleX)), (float)Math.cos(Math.toRadians(angleX)));
		float[] xyz = Geometry.rotateAroundYf(0, 0, 0, 2, 0, 0, (float)Math.sin(Math.toRadians(angleY)), (float)Math.cos(Math.toRadians(angleY)));
		float[] xyz2 = Geometry.rotateAroundZf(0, 0, 0, 0, 2, 0, (float)Math.sin(Math.toRadians(angleZ)), (float)Math.cos(Math.toRadians(angleZ)));
		
		// create lamps list
		lamps = new Lamp[] {
				createLamp(xyz[0], xyz[1], xyz[2]).setColor(1, 0, 0), // red lamp, rotating around y axis
				createLamp(xyz2[0], xyz2[1], xyz2[2]).setColor(0, 1, 0), // green lamp, rotating around z axis
				createLamp(xyz3[0], xyz3[1], xyz3[2]).setColor(0, 0, 1) // blue lamp, rotating around x axis
		};
		allLamps = Lamp.createFullCoordinatesList(lamps);
		allLampsColors = Lamp.createFullColorsList(lamps);
		
		GL2 gl2 = glAutoDrawable.getGL().getGL2();

		gl2.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		gl2.glUseProgram(shaderProgram.getProgramId());
		
		gl2.glEnableVertexAttribArray(shaderProgram.a_PositionLocation);
		gl2.glEnableVertexAttribArray(shaderProgram.a_NormalLocation);
		gl2.glEnableVertexAttribArray(shaderProgram.a_TextureLocation);

		gl2.glVertexAttribPointer(shaderProgram.a_PositionLocation, 3, GL2.GL_FLOAT, false, 0, vertexBuffer.rewind());
		gl2.glVertexAttribPointer(shaderProgram.a_NormalLocation, 3, GL2.GL_FLOAT, false, 0, normalsBuffer.rewind());
		gl2.glVertexAttribPointer(shaderProgram.a_TextureLocation, 2, GL2.GL_FLOAT, false, 0, texCoordsBuffer.rewind());

		// sending matrices to vertex shader
		bindMatrices(gl2);
		// sending lamps positions to fragment shader
		gl2.glUniform3fv(shaderProgram.u_LightPosLocation, lamps.length, allLamps, 0); // lamps coordinates
		gl2.glUniform1i(shaderProgram.u_LampsCountLocation, lamps.length); // lamps count
		gl2.glUniform3fv(shaderProgram.u_LampsColorsLocation, lamps.length, allLampsColors, 0); // lamps colors
		gl2.glUniform3f(shaderProgram.u_ViewPosLocation, eyeX, eyeY, eyeZ); // current camera position
		gl2.glUniform3f(shaderProgram.u_AmbientColorLocation, 1, 1, 1); // ambient lighting color
		gl2.glBindTexture(GL2.GL_TEXTURE_2D, textureId);
		
		// draw cube start pos=0, end=Cube.vertices.length/3 (3 floats per vertex)
		gl2.glDrawArrays(GL2.GL_TRIANGLES, 0, Cube.vertices.length/3);
		
		gl2.glDisableVertexAttribArray(shaderProgram.a_PositionLocation);
		gl2.glDisableVertexAttribArray(shaderProgram.a_NormalLocation);
		gl2.glDisableVertexAttribArray(shaderProgram.a_TextureLocation);

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
