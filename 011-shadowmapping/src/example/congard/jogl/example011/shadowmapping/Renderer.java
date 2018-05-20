package example.congard.jogl.example011.shadowmapping;

import java.io.File;
import java.nio.ByteBuffer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import example.congard.jogl.example011.shadowmapping.ShaderProgram.GLSLValue;
import example.congard.jogl.example011.shadowmapping.TextureUtils.Mapping;
import example.congard.jogl.example011.shadowmapping.TextureUtils.TextureArray;
import free.lib.congard.ml.geometry.Geometry;
import free.lib.congard.ml.graphics.GMatrix;
import free.lib.congard.objloader.LoaderConstants;
import free.lib.congard.objloader.Model;

/**
 * Rendering with normal mapping && shadow mapping
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
	private ShaderProgram shaderProgram, shaderShadowProgram;
	private float[] mProjectionMatrix = new float[16];
	private float[] mViewMatrix = new float[16];
	private float[] mModelMatrix = new float[16];
	private float[] mMVPMatrix = new float[16];
	private float[] mMVMatrix = new float[16];
	private float[][] rotationAnglesRad;
	private float eyeX = 0, eyeY = 8, eyeZ = 12, // положение камеры / camera position
			lookX = 0, lookY = 4.5f, lookZ = 0,  // точка направления т.е куда смотрим / look point
			upX = 0, upY = 1, upZ = 0; // смотрим по оси у / look at the axis y
	private Lamps structLamps;
	private ModelObject cube, floor, rapier, lucy;
	private Mapping mapping;
	private TextureArray shadowMaps;
	private ModelObject models[] = new ModelObject[4];
	//private Debug debug;
	
	private final static int LAMPS_COUNT = 2; // 2 lamps on scene
	private final static int SHADOW_MAP_WIDTH = 1024, SHADOW_MAP_HEIGHT = 1024;
	
	// window size
	private int WIN_W = 1024, WIN_H = 1024;
	
	@Override
	public void init(GLAutoDrawable glAutoDrawable) {
		GL2 gl2 = glAutoDrawable.getGL().getGL2();

		File vertexShader = new File("./resources/shaders/vertex_shader.glsl");
		File fragmentShader = new File("./resources/shaders/fragment_shader.glsl");
		File vertexShadowShader = new File("./resources/shaders/vertex_shadow_shader.glsl");
		File fragmentShadowShader = new File("./resources/shaders/fragment_shadow_shader.glsl");
		
		shaderProgram = new ShaderProgram();
		if (!shaderProgram.init(gl2, vertexShader, fragmentShader)) {
			throw new IllegalStateException("Unable to initiate the shaders!");
		}
		
		shaderShadowProgram = new ShaderProgram();
		if (!shaderShadowProgram.init(gl2, vertexShadowShader, fragmentShadowShader)) {
			throw new IllegalStateException("Unable to initiate the shaders!");
		}
		
		shaderProgram.loadValuesIds(gl2, 
				new GLSLValue("a_Position", GLSLValue.ATTRIB),
				new GLSLValue("a_Normal", GLSLValue.ATTRIB),
				new GLSLValue("a_Texture", GLSLValue.ATTRIB),
				new GLSLValue("a_Tangent", GLSLValue.ATTRIB),
				new GLSLValue("a_Bitangent", GLSLValue.ATTRIB),
				new GLSLValue("u_MVPMatrix", GLSLValue.UNIFORM),
				new GLSLValue("u_MVMatrix", GLSLValue.UNIFORM),
				new GLSLValue("u_LampsCount", GLSLValue.UNIFORM),
				new GLSLValue("u_ViewPos", GLSLValue.UNIFORM)
		);
		
		shaderShadowProgram.loadValuesIds(gl2, 
				new GLSLValue("a_Position", GLSLValue.ATTRIB),
				new GLSLValue("u_LightSpaceMatrix", GLSLValue.UNIFORM),
				new GLSLValue("u_ModelMatrix", GLSLValue.UNIFORM)
		);
		
		//debug = new Debug(gl2);
		
		System.out.println("Shaders created");
		
		shadowMaps = new TextureArray();
		shadowMaps.layerCount = LAMPS_COUNT;
		shadowMaps.width = SHADOW_MAP_WIDTH;
		shadowMaps.height = SHADOW_MAP_HEIGHT;
		shadowMaps.createTexture(gl2);
		
		mapping = new Mapping(gl2, shaderProgram.getProgramId());
		mapping.loadLocations();
		System.out.println(mapping.normalTextureLocation);
		
		System.out.println("Loading models");
		// loading obj models >>
		cube = new ModelObject(loadModel(new File("./resources/models/cube/cube_triangulated.obj")), 
				TextureUtils.loadTexture(gl2, new File("./resources/textures/cube/cube_ambient.jpg")),
				TextureUtils.loadTexture(gl2, new File("./resources/textures/cube/cube_diffuse.jpg")),
				TextureUtils.loadTexture(gl2, new File("./resources/textures/cube/cube_specular.jpg")),
				TextureUtils.loadTexture(gl2, new File("./resources/textures/cube/cube_normal.jpg")));
		//lamp = new ModelObject(loadModel(new File("./resources/models/lamp.obj")), TextureUtils.loadTexture(gl2, new File("./resources/textures/lamp.jpg")));
		floor = new ModelObject(loadModel(new File("./resources/models/brickwall/brickwall_triangulated.obj")), 
				TextureUtils.loadTexture(gl2, new File("./resources/textures/brickwall/brickwall.jpg")),
				TextureUtils.loadTexture(gl2, new File("./resources/textures/brickwall/brickwall_normal.jpg")));
		rapier = new ModelObject(loadModel(new File("./resources/models/italian_rapier/italian_rapier_triangulated.obj")));
		rapier.ambientTexture = TextureUtils.loadTexture(gl2, new File("./resources/textures/italian_rapier/diffuse.png"));
		rapier.diffuseTexture = rapier.ambientTexture;
		rapier.specularTexture = TextureUtils.loadTexture(gl2, new File("./resources/textures/italian_rapier/specular.png"));
		rapier.normalTexture = TextureUtils.loadTexture(gl2, new File("./resources/textures/italian_rapier/normal.png"));
		lucy = new ModelObject(loadModel(new File("./resources/models/LucyAngel/Stanfords_Lucy_Angel_triangulated.obj")));
		lucy.ambientTexture = TextureUtils.loadTexture(gl2, new File("./resources/textures/LucyAngel/Stanfords_Lucy_Angel_diffuse.jpg"));
		lucy.diffuseTexture = lucy.ambientTexture;
		lucy.specularTexture = TextureUtils.loadTexture(gl2, new File("./resources/textures/LucyAngel/Stanfords_Lucy_Angel_specular.jpg"));;
		lucy.normalTexture = TextureUtils.loadTexture(gl2, new File("./resources/textures/LucyAngel/Stanfords_Lucy_Angel_normal.jpg"));
		
		floor.angles[0] = -90;
		cube.angles[0] = 0;
		rapier.angles[0] = 90;
		rapier.origin[2] = 3;
		rapier.origin[1] = 3;
		lucy.origin[1] = cube.model.maxY;
		
		// making tangents and bitangents buffers
		// only for TRIANGULATED models
		cube.mkTBBuffers();
		floor.mkTBBuffers();
		rapier.mkTBBuffers();
		lucy.mkTBBuffers();
		// <<
		
		models[0] = cube;
		models[1] = floor;
		models[2] = rapier;
		models[3] = lucy;
		System.out.println("Models loaded");
		
		gl2.glEnable(GL2.GL_DEPTH_TEST);
		gl2.glDepthMask(true);
		
		gl2.glClearColor(0.01f, 0.01f, 0.01f, 1);
		createMatrices();
		
		structLamps = new Lamps(gl2, shaderProgram, LAMPS_COUNT);
		structLamps.loadLocations();
		lamps = new Lamp[] {
				createLamp(-3, (cube.model.maxY-cube.model.minY)/2 + 3, 3).setColor(1, 1, 1).setAmbientStrength(0.01f).setDiffuseStrength(1).setSpecularStrength(1f).setShininess(32).setAttenuationTerms(1, 0.045f, 0.0075f).initShadowMapping(gl2), // white lamp
				createLamp(3, (cube.model.maxY-cube.model.minY)/2 + 3, 3).setColor(1, 1, 1).setAmbientStrength(0.01f).setDiffuseStrength(1).setSpecularStrength(1f).setShininess(32).setAttenuationTerms(1, 0.045f, 0.0075f).initShadowMapping(gl2) // white lamp
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
	}
	
	private void createModelMatrix() {
		GMatrix.setIdentityM(mModelMatrix, 0);
	}
	
	private void createMVMatrix() {
		GMatrix.multiplyMM(mMVMatrix, mViewMatrix, mModelMatrix);
	}
	
	private void createMVPMatrix() {
		GMatrix.multiplyMM(mMVPMatrix, mViewMatrix, mModelMatrix);
		GMatrix.multiplyMM(mMVPMatrix, mProjectionMatrix, mMVPMatrix);
		// Final (Projection * View * Model) matrix
	}
	
	private void bindMatrices(GL2 gl) {
		// bind to matrices
		gl.glUniformMatrix4fv(shaderProgram.getValueId("u_MVPMatrix"), 1, false, mMVPMatrix, 0);
		gl.glUniformMatrix4fv(shaderProgram.getValueId("u_MVMatrix"), 1, false, mMVMatrix, 0);
	}
	
	private Lamp createLamp(float x, float y, float z) {
		Lamp l = new Lamp();
		l.setX(x);
		l.setY(y);
		l.setZ(z);
		l.calculateLampPosInEyeSpace(mViewMatrix);
		l.sm.width = SHADOW_MAP_WIDTH;
		l.sm.height = SHADOW_MAP_HEIGHT;
		return l;
	}
	
	private void updateAndBindMatrices(GL2 gl2) {
		createMVMatrix();
		createMVPMatrix();
		bindMatrices(gl2);
	}
	
	private void sendLampsData(GL2 gl2) {
		gl2.glUniform1i(shaderProgram.getValueId("u_LampsCount"), lamps.length); // lamps count
		gl2.glUniform3f(shaderProgram.getValueId("u_ViewPos"), eyeX, eyeY, eyeZ); // current camera position
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
			gl2.glUniformMatrix4fv(structLamps.getLightSpaceMatrixLocation(i), 1, false, lamps[i].sm.lightSpaceMatrix, 0);
		}
	}
	
	private void updateLampsLightModelMatrix(GL2 gl, float[] lightModelMatrix) {
		for (int i = 0; i<lamps.length; i++)
			gl.glUniformMatrix4fv(structLamps.getLightModelMatrixLocation(i), 1, false, lightModelMatrix, 0);
	}
	
	@Override
	public void dispose(GLAutoDrawable glAutoDrawable) {
		GL2 gl2 = glAutoDrawable.getGL().getGL2();
		shaderProgram.dispose(gl2);
	}

	private ByteBuffer[] shadowMapsBuffers = new ByteBuffer[LAMPS_COUNT];
	@Override
	public void display(GLAutoDrawable glAutoDrawable) {
		GL2 gl2 = glAutoDrawable.getGL().getGL2();
		
		gl2.glUseProgram(shaderShadowProgram.getProgramId());
		for (int i = 0; i<lamps.length; i++) {
			renderToDepthMap(gl2, shaderShadowProgram, lamps[i]);
			shadowMapsBuffers[i] = lamps[i].sm.pixels;
		}
		gl2.glUseProgram(0);
		
		gl2.glUseProgram(shaderProgram.getProgramId());
		gl2.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		shadowMaps.pixels = TextureArray.createFullByteBuffer(shadowMapsBuffers);
		shadowMaps.write(gl2);
		gl2.glUniform1i(gl2.glGetUniformLocation(shaderProgram.getProgramId(), "shadowMaps"), 10);
		gl2.glActiveTexture(GL2.GL_TEXTURE10);
		gl2.glBindTexture(GL2.GL_TEXTURE_2D_ARRAY, shadowMaps.get());
		
		render(gl2, shaderProgram);
		gl2.glUseProgram(0);
	}
	
	private void renderToDepthMap(GL2 gl, ShaderProgram shaderProgram, Lamp lamp) {
		lamp.sm.begin(gl);
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
		
		VertexAttribTools.enable(gl, shaderProgram.getValueId("a_Position"));
		
		for (ModelObject model : models) {
			VertexAttribTools.pointer(gl, shaderProgram.getValueId("a_Position"), 3, model.vertexBuffer.rewind());
			GMatrix.setIdentityM(lamp.sm.modelMatrix, 0);
			GMatrix.translateM(lamp.sm.modelMatrix, 0, model.origin[0], model.origin[1], model.origin[2]);
			GMatrix.rotateM(lamp.sm.modelMatrix, 0, model.angles[0], 1, 0, 0);
			GMatrix.rotateM(lamp.sm.modelMatrix, 0, model.angles[1], 0, 1, 0);
			GMatrix.rotateM(lamp.sm.modelMatrix, 0, model.angles[2], 0, 0, 1);
			lamp.sm.updateMatrices();
			lamp.sm.updateData(gl, shaderProgram);
			for (Model.VerticesDescriptor vd : model.model.vd) gl.glDrawArrays(vd.POLYTYPE, vd.START, vd.END); // drawing
		}
		
		VertexAttribTools.disable(gl, shaderProgram.getValueId("a_Position"));
		
		lamp.sm.end(gl);
		
		lamp.writeShadowMapBuffer(gl);
	}
	
	private void render(GL2 gl, ShaderProgram shaderProgram) {
		// view port to window size
		gl.glViewport(0, 0, WIN_W, WIN_H);
		// updating view matrix (because camera position was changed)
		createViewMatrix();
		// updating lamps eye space position
		for (int i = 0; i<lamps.length; i++) lamps[i].calculateLampPosInEyeSpace(mViewMatrix);
		// sending updated matrices to vertex shader
		GMatrix.setIdentityM(mModelMatrix, 0);
		updateAndBindMatrices(gl);
		// sending lamps positions to fragment shader
		sendLampsData(gl);
		
		VertexAttribTools.enable(gl, shaderProgram.getValueId("a_Position"), shaderProgram.getValueId("a_Normal"), shaderProgram.getValueId("a_Texture"));
		
		// drawing >>
		mapping.setNormalMappingEnabled(gl, 1); // with mapping
		VertexAttribTools.enable(gl, shaderProgram.getValueId("a_Tangent"), shaderProgram.getValueId("a_Bitangent"));

		for (ModelObject model : models) {
			mapping.bindADSNTextures(gl,
					model.ambientTexture, model.diffuseTexture, model.specularTexture, model.normalTexture);
			VertexAttribTools.pointer(gl, shaderProgram.getValueId("a_Position"), 3, model.vertexBuffer.rewind());
			VertexAttribTools.pointer(gl, shaderProgram.getValueId("a_Normal"), 3, model.normalsBuffer.rewind());
			VertexAttribTools.pointer(gl, shaderProgram.getValueId("a_Tangent"), 3, model.tangentsBuffer.rewind());
			VertexAttribTools.pointer(gl, shaderProgram.getValueId("a_Bitangent"), 3, model.bitangentsBuffer.rewind());
			VertexAttribTools.pointer(gl, shaderProgram.getValueId("a_Texture"), 2, model.texCoordsBuffer.rewind());
			GMatrix.setIdentityM(mModelMatrix, 0);
			GMatrix.translateM(mModelMatrix, 0, model.origin[0], model.origin[1], model.origin[2]);
			GMatrix.rotateM(mModelMatrix, 0, model.angles[0], 1, 0, 0);
			GMatrix.rotateM(mModelMatrix, 0, model.angles[1], 0, 1, 0);
			GMatrix.rotateM(mModelMatrix, 0, model.angles[2], 0, 0, 1);
			updateAndBindMatrices(gl);
			updateLampsLightModelMatrix(gl, mModelMatrix);
			for (Model.VerticesDescriptor vd : model.model.vd) gl.glDrawArrays(vd.POLYTYPE, vd.START, vd.END); // drawing
		}
		VertexAttribTools.disable(gl, shaderProgram.getValueId("a_Tangent"), shaderProgram.getValueId("a_Bitangent"));
		// <<
		
//		// drawing lamps >>
//		mapping.setNormalMappingEnabled(gl, 0);
//		mapping.bindADSTextures(gl,
//				lamp.ambientTexture, lamp.diffuseTexture, lamp.specularTexture);
//		
//		VertexAttribTools.pointer(gl, shaderProgram.getValueId("a_Position"), 3, lamp.vertexBuffer.rewind());
//		VertexAttribTools.pointer(gl, shaderProgram.getValueId("a_Normal"), 3, lamp.normalsBuffer.rewind());
//		VertexAttribTools.pointer(gl, shaderProgram.getValueId("a_Texture"), 2, lamp.texCoordsBuffer.rewind());
//		
//		for (int j = 0; j<lamps.length; j++) {
//			GMatrix.setIdentityM(mModelMatrix, 0);
//			GMatrix.translateM(mModelMatrix, 0, lamps[j].getWorldX(), lamps[j].getWorldY(), lamps[j].getWorldZ());
//			updateAndBindMatrices(gl);
//			// drawing model
//			for (Model.VerticesDescriptor vd : lamp.model.vd) gl.glDrawArrays(vd.POLYTYPE, vd.START, vd.END); // drawing
//		}
//		// <<
//		
		VertexAttribTools.disable(gl, shaderProgram.getValueId("a_Position"), shaderProgram.getValueId("a_Normal"), shaderProgram.getValueId("a_Texture"));

	}

	@Override
	public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
		System.out.println("reshape >>");
		createProjectionMatrix(width, height);
		createMVPMatrix();
		createMVMatrix();
		WIN_W = width;
		WIN_H = height;
		System.out.println("<<");
	}
}
