package example.congard.jogl.shaders.sllightingadstexlp.attenuation.objmodels.lightingmaps;

import java.io.File;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import example.congard.jogl.shaders.sllightingadstexlp.attenuation.objmodels.lightingmaps.ShaderProgram.Lamps;
import free.lib.congard.ml.geometry.Geometry;
import free.lib.congard.ml.graphics.GMatrix;
import free.lib.congard.ml.graphics.Vec3;
import free.lib.congard.objloader.LoaderConstants;
import free.lib.congard.objloader.Model;

/**
 * Rendering with shaders & ads lighting & lamps properties
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
	private ShaderProgram shaderProgram;
	private float[] mProjectionMatrix = new float[16];
	private float[] mViewMatrix = new float[16];
	private float[] mModelMatrix = new float[16];
	private float[] mMVPMatrix = new float[16];
	private float[] mMVMatrix = new float[16];
	private float[][] lampsRotationAnglesRad;
	private float eyeX = 0, eyeY = 0, eyeZ = 24, // положение камеры / camera position
			lookX = 0, lookY = 0, lookZ = 0,  // точка направления т.е куда смотрим / look point
			upX = 0, upY = 1, upZ = 0; // смотрим по оси у / look at the axis y
	private Lamps structLamps;
	private ModelObject cube, lamp;
	
	private final static int LAMPS_COUNT = 4; // 4 lamps on scene
	
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

		cube = new ModelObject(loadModel(new File("./resources/models/cube.obj")), 
				TextureUtils.loadTexture(gl2, new File("./resources/textures/cube_ambient.jpg")),
				TextureUtils.loadTexture(gl2, new File("./resources/textures/cube_diffuse.jpg")),
				TextureUtils.loadTexture(gl2, new File("./resources/textures/cube_specular.jpg")));
		int id = TextureUtils.loadTexture(gl2, new File("./resources/textures/lamp.jpg"));
		lamp = new ModelObject(loadModel(new File("./resources/models/lamp.obj")), id, id, id);

		gl2.glEnable(GL2.GL_DEPTH_TEST);
		gl2.glDepthMask(true);
		
		gl2.glClearColor(0.1f, 0.1f, 0.1f, 1);
		createMatrices();
		
		structLamps = new Lamps(gl2, shaderProgram, LAMPS_COUNT);
		structLamps.loadLocations();
		lamps = new Lamp[] {
				createLamp(24, 0, 24).setColor(1, 0, 0).setAmbientStrength(0.01f).setDiffuseStrength(1).setSpecularStrength(0.1f).setShininess(16).setAttenuationTerms(1, 0.045f, 0.0075f), // red lamp
				createLamp(-24, 0, 24).setColor(0, 1, 0).setAmbientStrength(0.01f).setDiffuseStrength(1).setSpecularStrength(0.2f).setShininess(32).setAttenuationTerms(1, 0.045f, 0.0075f), // green lamp
				createLamp(0, 0, -48).setColor(0, 0, 1).setAmbientStrength(0.01f).setDiffuseStrength(1).setSpecularStrength(0.3f).setShininess(64).setAttenuationTerms(1, 0.045f, 0.0075f), // blue lamp
				createLamp(0, 0, 0).setColor(1, 1, 1).setAmbientStrength(0.01f).setDiffuseStrength(1).setSpecularStrength(0.3f).setShininess(64).setAttenuationTerms(1, 0.045f, 0.0075f) // white lamp
		};
		// this array need only for animation
		lampsRotationAnglesRad = new float[][] {
			new float[] { (float)Math.sin(Math.toRadians(0.5)), (float)Math.cos(Math.toRadians(0.5)) } // for camera
		};
		
		// animation thread
		new Thread(new Runnable() {
			float[] cameraPos;
			
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(16);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					cameraPos = Geometry.rotateAroundYf(0, 0, 0, eyeX, eyeY, eyeZ, lampsRotationAnglesRad[0][0], lampsRotationAnglesRad[0][1]);
					eyeX = cameraPos[0];
					eyeY = cameraPos[1];
					eyeZ = cameraPos[2];
				}
			}
		}).start();
	}
	
	private static Model loadModel(File path) {
		Model m = new Model(path); // path to obj model
		m.enable(LoaderConstants.TEX_VERTEX_2D); // xy texture coords
		m.setDefaultPolyTypes(GL2.GL_TRIANGLES, GL2.GL_QUADS, GL2.GL_POLYGON); // for easy drawing
		m.load(); // loading model
		m.convertToFloatArrays(true, true);
		m.cleanup();
		return m;
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
        float near = 2f, far = 96;
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
        //System.out.println("Projection matrix created");
	}
	
	private void createViewMatrix() {
		GMatrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
		//System.out.println("View matrix created");
	}
	
	private void createModelMatrix() {
		GMatrix.setIdentityM(mModelMatrix, 0);
	}
	
	private void createMVMatrix() {
		GMatrix.multiplyMM(mMVMatrix, mViewMatrix, mModelMatrix);
		//System.out.println("Final (View * Model) matrix created");
	}
	
	private void createMVPMatrix() {
		GMatrix.multiplyMM(mMVPMatrix, mViewMatrix, mModelMatrix);
		GMatrix.multiplyMM(mMVPMatrix, mProjectionMatrix, mMVPMatrix);
		//System.out.println("Final (Projection * View * Model) matrix created");
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
	
	private void updateAndBindMatrices(GL2 gl2) {
		createMVMatrix();
		createMVPMatrix();
		bindMatrices(gl2);
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
		gl2.glEnableVertexAttribArray(shaderProgram.a_NormalLocation);
		gl2.glEnableVertexAttribArray(shaderProgram.a_TextureLocation);

		gl2.glVertexAttribPointer(shaderProgram.a_PositionLocation, 3, GL2.GL_FLOAT, false, 0, cube.vertexBuffer.rewind());
		gl2.glVertexAttribPointer(shaderProgram.a_NormalLocation, 3, GL2.GL_FLOAT, false, 0, cube.normalsBuffer.rewind());
		gl2.glVertexAttribPointer(shaderProgram.a_TextureLocation, 2, GL2.GL_FLOAT, false, 0, cube.texCoordsBuffer.rewind());

		// updating view matrix (because camera position was changed)
		createViewMatrix();
		// updating lamps eye space position
		for (int i = 0; i<lamps.length; i++) lamps[i].calculateLampPosInEyeSpace(mViewMatrix);
		// sending updated matrices to vertex shader
		GMatrix.setIdentityM(mModelMatrix, 0);
		updateAndBindMatrices(gl2);
		// sending lamps positions to fragment shader
		gl2.glUniform1i(shaderProgram.u_LampsCountLocation, lamps.length); // lamps count
		gl2.glUniform3f(shaderProgram.u_ViewPosLocation, eyeX, eyeY, eyeZ); // current camera position
		for (int i = 0; i<lamps.length; i++) {
			gl2.glUniform1f(structLamps.getAmbientStrengthLocation(i), lamps[i].ambientStrength);
			gl2.glUniform1f(structLamps.getDiffuseStrengthLocation(i), lamps[i].diffuseStrength);
			gl2.glUniform1f(structLamps.getSpecularStrengthLocation(i), lamps[i].specularStrength);
			gl2.glUniform1f(structLamps.getKcLocation(i), lamps[i].kc);
			gl2.glUniform1f(structLamps.getKlLocation(i), lamps[i].kl);
			gl2.glUniform1f(structLamps.getKqLocation(i), lamps[i].kq);
			gl2.glUniform1i(structLamps.getShininessLocation(i), lamps[i].shininess);
			gl2.glUniform3f(structLamps.getLampPosLocation(i), lamps[i].getEyeX(), lamps[i].getEyeY(), lamps[i].getEyeZ());
			gl2.glUniform3f(structLamps.getLampColorLocation(i), lamps[i].getLightR(), lamps[i].getLightG(), lamps[i].getLightB());
		}
		
		// drawing cubes >>
		TextureUtils.bindADSTextures(gl2, 
				shaderProgram.u_AmbientTextureUnitLocation, shaderProgram.u_DiffuseTextureUnitLocation, shaderProgram.u_SpecularTextureUnitLocation, 
				cube.ambientTexture, cube.diffuseTexture, cube.specularTexture);
		int i;
		for (int c = 0; c<SceneObjects.count*2; c++) {
			i = c+1 > SceneObjects.count ? SceneObjects.count*2-c-1 : c;
			//i = c;
			for (int j = 0; j<SceneObjects.defaultCubesCoordinates.length; j++) {
				GMatrix.setIdentityM(mModelMatrix, 0);
				GMatrix.translateM(mModelMatrix, 0, 
						SceneObjects.defaultCubesCoordinates[j][0]*(i+1), 
						SceneObjects.defaultCubesCoordinates[j][1]*(i+1), 
						SceneObjects.defaultCubesCoordinates[j][2]*(c+1));
				updateAndBindMatrices(gl2);
				// drawing model
				for (Model.VerticesDescriptor vd : cube.model.vd) gl2.glDrawArrays(vd.POLYTYPE, vd.START, vd.END); // drawing
			}
		}
		// <<
		
		// drawing lamps >>
		TextureUtils.bindADSTextures(gl2, 
				shaderProgram.u_AmbientTextureUnitLocation, shaderProgram.u_DiffuseTextureUnitLocation, shaderProgram.u_SpecularTextureUnitLocation, 
				lamp.ambientTexture, lamp.diffuseTexture, lamp.specularTexture);
		
		gl2.glVertexAttribPointer(shaderProgram.a_PositionLocation, 3, GL2.GL_FLOAT, false, 0, lamp.vertexBuffer.rewind());
		gl2.glVertexAttribPointer(shaderProgram.a_NormalLocation, 3, GL2.GL_FLOAT, false, 0, lamp.normalsBuffer.rewind());
		gl2.glVertexAttribPointer(shaderProgram.a_TextureLocation, 2, GL2.GL_FLOAT, false, 0, lamp.texCoordsBuffer.rewind());
		
		for (int j = 0; j<lamps.length; j++) {
			GMatrix.setIdentityM(mModelMatrix, 0);
			GMatrix.translateM(mModelMatrix, 0, lamps[j].getWorldX(), lamps[j].getWorldY(), lamps[j].getWorldZ());
			updateAndBindMatrices(gl2);
			// drawing model
			for (Model.VerticesDescriptor vd : lamp.model.vd) gl2.glDrawArrays(vd.POLYTYPE, vd.START, vd.END); // drawing
		}
		// <<
		
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
	
	static class SceneObjects {
		public static final int count = 4;
		public static final float[][] defaultCubesCoordinates = {
				Vec3.createVec3(3, 3, -4),
				Vec3.createVec3(3, -3, -4),
				Vec3.createVec3(-3, -3, -4),
				Vec3.createVec3(-3, 3, -4)
		};
	}
}
