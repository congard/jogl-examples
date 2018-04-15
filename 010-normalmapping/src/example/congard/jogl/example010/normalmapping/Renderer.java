package example.congard.jogl.example010.normalmapping;

import java.io.File;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import example.congard.jogl.example010.normalmapping.ShaderProgram.Lamps;
import example.congard.jogl.example010.normalmapping.TextureUtils.Mapping;
import free.lib.congard.ml.geometry.Geometry;
import free.lib.congard.ml.graphics.GMatrix;
import free.lib.congard.objloader.LoaderConstants;
import free.lib.congard.objloader.Model;

/**
 * Rendering with normal mapping
 * But only for triangulated models
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
	private float[][] rotationAnglesRad;
	private float eyeX = 0, eyeY = 1, eyeZ = 5, // положение камеры / camera position
			lookX = 0, lookY = 1, lookZ = 0,  // точка направления т.е куда смотрим / look point
			upX = 0, upY = 1, upZ = 0; // смотрим по оси у / look at the axis y
	private Lamps structLamps;
	private ModelObject cube, lamp;
	private Mapping mapping;
	
	private final static int LAMPS_COUNT = 1; // 1 lamps on scene
	
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

		mapping = new Mapping(gl2, shaderProgram.getProgramId());
		mapping.loadLocations();
		System.out.println(mapping.normalTextureLocation);
		
		// loading obj models >>
		cube = new ModelObject(loadModel(new File("./resources/models/cube/cube_triangulated.obj")), 
				TextureUtils.loadTexture(gl2, new File("./resources/textures/cube/cube_ambient.jpg")),
				TextureUtils.loadTexture(gl2, new File("./resources/textures/cube/cube_diffuse.jpg")),
				TextureUtils.loadTexture(gl2, new File("./resources/textures/cube/cube_specular.jpg")),
				TextureUtils.loadTexture(gl2, new File("./resources/textures/cube/cube_normal.jpg")));
		lamp = new ModelObject(loadModel(new File("./resources/models/lamp.obj")), TextureUtils.loadTexture(gl2, new File("./resources/textures/lamp.jpg")));

		// making tangents and bitangents buffers
		// only for TRIANGULATED models
		cube.mkTBBuffers();
		// <<
		
		gl2.glEnable(GL2.GL_DEPTH_TEST);
		gl2.glDepthMask(true);
		
		gl2.glClearColor(0.1f, 0.1f, 0.1f, 1);
		createMatrices();
		
		structLamps = new Lamps(gl2, shaderProgram, LAMPS_COUNT);
		structLamps.loadLocations();
		lamps = new Lamp[] {
				createLamp(0, (cube.model.maxY-cube.model.minY)/2, 5).setColor(1, 1, 1).setAmbientStrength(0.01f).setDiffuseStrength(1).setSpecularStrength(1.5f).setShininess(32).setAttenuationTerms(1, 0.045f, 0.0075f) // white lamp
		};
		// this array need only for animation
		rotationAnglesRad = new float[][] {
			new float[] { (float)Math.sin(Math.toRadians(0.1)), (float)Math.cos(Math.toRadians(0.1)) } // for camera
		};
		
		// animation thread
		new Thread(new Runnable() {
			float[] cameraPos;
			
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(4);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					cameraPos = Geometry.rotateAroundYf(0, 0, 0, eyeX, eyeY, eyeZ, rotationAnglesRad[0][0], rotationAnglesRad[0][1]);
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
		m.convertToFloatArrays(true, true); // getting texcoords and normals
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
	
	private void sendLampsData(GL2 gl2) {
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
		
		// updating view matrix (because camera position was changed)
		createViewMatrix();
		// updating lamps eye space position
		for (int i = 0; i<lamps.length; i++) lamps[i].calculateLampPosInEyeSpace(mViewMatrix);
		// sending updated matrices to vertex shader
		GMatrix.setIdentityM(mModelMatrix, 0);
		updateAndBindMatrices(gl2);
		// sending lamps positions to fragment shader
		sendLampsData(gl2);
		
		VertexAttribTools.enable(gl2, shaderProgram.a_PositionLocation, shaderProgram.a_NormalLocation, shaderProgram.a_TextureLocation);
		
		// drawing cube >>
		mapping.setNormalMappingEnabled(gl2, 1); // with mapping
		mapping.bindADSNTextures(gl2,
				cube.ambientTexture, cube.diffuseTexture, cube.specularTexture, cube.normalTexture);
		
		gl2.glEnableVertexAttribArray(shaderProgram.a_TangentLocation);
		gl2.glEnableVertexAttribArray(shaderProgram.a_BitangentLocation);

		VertexAttribTools.pointer(gl2, shaderProgram.a_PositionLocation, 3, cube.vertexBuffer.rewind());
		VertexAttribTools.pointer(gl2, shaderProgram.a_NormalLocation, 3, cube.normalsBuffer.rewind());
		VertexAttribTools.pointer(gl2, shaderProgram.a_TangentLocation, 3, cube.tangentsBuffer.rewind());
		VertexAttribTools.pointer(gl2, shaderProgram.a_BitangentLocation, 3, cube.bitangentsBuffer.rewind());
		VertexAttribTools.pointer(gl2, shaderProgram.a_TextureLocation, 2, cube.texCoordsBuffer.rewind());
		
		GMatrix.setIdentityM(mModelMatrix, 0);
		GMatrix.rotateM(mModelMatrix, 0, 0, 1, 0, 0);
		updateAndBindMatrices(gl2);
		for (Model.VerticesDescriptor vd : cube.model.vd) gl2.glDrawArrays(vd.POLYTYPE, vd.START, vd.END); // drawing
		
		VertexAttribTools.disable(gl2, shaderProgram.a_TangentLocation, shaderProgram.a_BitangentLocation);
		// <<
		
		// drawing lamps >>
		mapping.setNormalMappingEnabled(gl2, 0);
		mapping.bindADSTextures(gl2,
				lamp.ambientTexture, lamp.diffuseTexture, lamp.specularTexture);
		
		VertexAttribTools.pointer(gl2, shaderProgram.a_PositionLocation, 3, lamp.vertexBuffer.rewind());
		VertexAttribTools.pointer(gl2, shaderProgram.a_NormalLocation, 3, lamp.normalsBuffer.rewind());
		VertexAttribTools.pointer(gl2, shaderProgram.a_TextureLocation, 2, lamp.texCoordsBuffer.rewind());
		
		for (int j = 0; j<lamps.length; j++) {
			GMatrix.setIdentityM(mModelMatrix, 0);
			GMatrix.translateM(mModelMatrix, 0, lamps[j].getWorldX(), lamps[j].getWorldY(), lamps[j].getWorldZ());
			updateAndBindMatrices(gl2);
			// drawing model
			for (Model.VerticesDescriptor vd : lamp.model.vd) gl2.glDrawArrays(vd.POLYTYPE, vd.START, vd.END); // drawing
		}
		// <<
		
		VertexAttribTools.disable(gl2, shaderProgram.a_PositionLocation, shaderProgram.a_NormalLocation, shaderProgram.a_TextureLocation);

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
