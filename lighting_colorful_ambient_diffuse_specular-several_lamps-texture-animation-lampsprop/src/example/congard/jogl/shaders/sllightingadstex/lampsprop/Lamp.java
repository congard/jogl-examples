package example.congard.jogl.shaders.sllightingadstex.lampsprop;

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
	// diffuse strength param
	public float diffuseStrength = 1;
	// specular strength param
	public float specularStrength = 0.1f;
	// shininess
	public int shininess = 32;
	
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
