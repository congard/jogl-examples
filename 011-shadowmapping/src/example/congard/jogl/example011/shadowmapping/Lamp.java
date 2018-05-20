package example.congard.jogl.example011.shadowmapping;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import example.congard.jogl.example011.shadowmapping.TextureUtils.TextureArray;
import free.lib.congard.ml.graphics.GMatrix;
import free.lib.congard.ml.graphics.Vec3;
import free.lib.congard.ml.graphics.Vec4;

/**
 * 
 * @author congard
 *
 */
public class Lamp {
	// real coordinates (in world)
	public float[] mLightPosInWorldSpace = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
	// coordinates in eye space (for drawing)
	public float[] mLightPosInEyeSpace = new float[4];
	// light color
	public float[] mLightColor = new float[3];
	// ambient strength param; recommended: 0.01
	public float ambientStrength;
	// diffuse strength param; recommended: 1
	public float diffuseStrength;
	// specular strength param; recommended: 0.1
	public float specularStrength;
	// constant term; usually kept at 1.0
	public float kc;
	// linear term
	public float kl;
	// quadratic term
	public float kq;
	// shininess; recommended: 32
	public int shininess;
	// shadow map, 1024 - default resolution
	public ShadowMapping sm = new ShadowMapping(1024, 1024);
	
	// mathods for ShadowMapping >>
	public Lamp setShadowMapResolution(int width, int height) {
		sm.width = width;
		sm.height = height;
		return this;
	}
	
	public Lamp initShadowMapping(GL2 gl) {
		sm.createDepthMap(gl);
		sm.createDepthMapFBO(gl);
		sm.updateMatrices();
		sm.init(gl);
		sm.createBuffer();
		return this;
	}
	
	public Lamp writeShadowMapBuffer(GL2 gl) {
		gl.glBindTexture(GL.GL_TEXTURE_2D, sm.depthMap.get(0));
		gl.glGetTexImage(GL.GL_TEXTURE_2D, 0, GL2.GL_DEPTH_COMPONENT, GL.GL_UNSIGNED_BYTE, sm.pixels);
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
		return this;
	}
	// <<
	
	// methods for attenuation >>
	public Lamp setKc(float kc) {
		this.kc = kc;
		return this;
	}
	
	public Lamp setKl(float kl) {
		this.kl = kl;
		return this;
	}
	
	public Lamp setKq(float kq) {
		this.kq = kq;
		return this;
	}
	
	public Lamp setAttenuationTerms(float kc, float kl, float kq) {
		this.kc = kc;
		this.kl = kl;
		this.kq = kq;
		return this;
	}
	// <<
	
	public void setX(float x) {
		mLightPosInWorldSpace[0] = x;
	}
	
	public void setY(float y) {
		mLightPosInWorldSpace[1] = y;
	}
	
	public void setZ(float z) {
		mLightPosInWorldSpace[2] = z;
	}
	
	public Lamp setWorldXYZ(float[] xyz) {
		mLightPosInWorldSpace = Vec4.createVec4(xyz);
		return this;
	}
	
	public Lamp setEyeXYZ(float[] xyz) {
		mLightPosInEyeSpace = Vec4.createVec4(xyz);
		return this;
	}
	
	// This 32 value is the shininess value of the highlight.
	// The higher the shininess value of an object, 
	// the more it properly reflects the light instead of scattering it all around and
	// thus the smaller the highlight becomes
	public Lamp setShininess(int shininess) {
		this.shininess = shininess;
		return this;
	}
	
	public Lamp setColor(float r, float g, float b) {
		mLightColor = Vec3.createVec3(r, g, b);
		return this;
	}
	
	// writes result xyzw in mLightPosInEyeSpace
	// for drawing you need only xyz
	public void calculateLampPosInEyeSpace(float[] mMatrix) {
		GMatrix.multiplyMV(mLightPosInEyeSpace, 0, mMatrix, 0, mLightPosInWorldSpace, 0);
	}
	
	public Lamp setAmbientStrength(float ambientStrength) {
		this.ambientStrength = ambientStrength;
		return this;
	}
	
	public Lamp setDiffuseStrength(float diffuseStrength) {
		this.diffuseStrength = diffuseStrength;
		return this;
	}
	
	public Lamp setSpecularStrength(float specularStrength) {
		this.specularStrength = specularStrength;
		return this;
	}
	
	// lamp world position
	public float getWorldX() {
		return mLightPosInWorldSpace[0];
	}
	
	public float getWorldY() {
		return mLightPosInWorldSpace[1];
	}
	
	public float getWorldZ() {
		return mLightPosInWorldSpace[2];
	}
	
	// lamp eye position
	public float getEyeX() {
		return mLightPosInEyeSpace[0];
	}
	
	public float getEyeY() {
		return mLightPosInEyeSpace[1];
	}
	
	public float getEyeZ() {
		return mLightPosInEyeSpace[2];
	}
	
	// lamp light color
	public float getLightR() {
		return mLightColor[0];
	}
	
	public float getLightG() {
		return mLightColor[1];
	}
	
	public float getLightB() {
		return mLightColor[2];
	}
	
	/**
	 * 
	 * @author congard
	 *
	 */
	public class ShadowMapping {
		public ByteBuffer pixels;
		public IntBuffer depthMapFBO, depthMap;
		public int width, height;
		public float[] lightProjection = new float[16];
		public float[] lightView = new float[16];
		public float[] lightSpaceMatrix = new float[16];
		public float[] modelMatrix = new float[16];
		public float left = -10, right = 10, 
				top = 10, bottom = -10, 
				near = 1, far = 16, 
				dirX = 0, dirY = 0, dirZ = 0;
		
		public ShadowMapping(int width, int height) {
			this.width = width;
			this.height = height;
		}
		

		public void createBuffer() {
			pixels = TextureArray.createByteBuffer(width, height, 1);
		}
		
		
		public void updateData(GL2 gl, ShaderProgram shaderProgram) {
			gl.glUniformMatrix4fv(shaderProgram.getValueId("u_LightSpaceMatrix"), 1, false, lightSpaceMatrix, 0);
			gl.glUniformMatrix4fv(shaderProgram.getValueId("u_ModelMatrix"), 1, false, modelMatrix, 0);
		}
		
		public void updateMatrices() {
			GMatrix.orthoM(lightProjection, 0, left, right, bottom, top, near, far);
			GMatrix.setLookAtM(lightView, 0, getWorldX(), getWorldY(), getWorldZ(), dirX, dirY, dirZ, 0, 1, 0);
			GMatrix.multiplyMM(lightSpaceMatrix, lightProjection, lightView);
		}
		
		public void createDepthMapFBO(GL2 gl) {
			depthMapFBO = IntBuffer.allocate(4);
			gl.glGenFramebuffers(1, depthMapFBO);
		}
		
		public void createDepthMap(GL2 gl) {
			depthMap = IntBuffer.allocate(4);
			gl.glGenTextures(1, depthMap);
		}
		
		public void init(GL2 gl) {
			gl.glBindTexture(GL.GL_TEXTURE_2D, depthMap.get(0));
			gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL2.GL_DEPTH_COMPONENT, width, height, 0, GL2.GL_DEPTH_COMPONENT, GL.GL_FLOAT, null);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,GL. GL_NEAREST);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
			gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, depthMapFBO.get(0));
			gl.glFramebufferTexture2D(GL.GL_FRAMEBUFFER, GL.GL_DEPTH_ATTACHMENT, GL.GL_TEXTURE_2D, depthMap.get(0), 0);
			gl.glDrawBuffer(GL.GL_NONE);
			gl.glReadBuffer(GL.GL_NONE);
			gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
			gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
		}
		
		public void begin(GL2 gl) {
			gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, depthMapFBO.get(0));
			gl.glActiveTexture(GL.GL_TEXTURE0);
			gl.glBindTexture(GL.GL_TEXTURE_2D, depthMap.get(0));
			gl.glViewport(0, 0, width, height);
			GMatrix.setIdentityM(modelMatrix, 0);
		}
		
		public void end(GL2 gl) {
			gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
		}
	}
}
