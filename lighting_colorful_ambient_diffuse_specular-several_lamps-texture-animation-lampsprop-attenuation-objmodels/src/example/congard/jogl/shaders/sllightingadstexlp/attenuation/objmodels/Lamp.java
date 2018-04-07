package example.congard.jogl.shaders.sllightingadstexlp.attenuation.objmodels;

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
}
