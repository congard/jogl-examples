package example.congard.jogl.shaders.lighting;

import free.lib.congard.ml.graphics.GMatrix;

/**
 * 
 * @author congard
 *
 */
public class Lamp {
	// real coordinates (in world)
	public final float[] mLightPosInWorldSpace = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
	// coordinates in eye space (for drawing)
	public final float[] mLightPosInEyeSpace = new float[4];
	
	public void setX(float x) {
		mLightPosInWorldSpace[0] = x;
	}
	
	public void setY(float y) {
		mLightPosInWorldSpace[1] = y;
	}
	
	public void setZ(float z) {
		mLightPosInWorldSpace[2] = z;
	}
	
	// writes result xyzw in mLightPosInEyeSpace
	// for drawing you need only xyz
	public void calculateLampPosInEyeSpace(float[] mViewMatrix) {
		GMatrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);
	}
}
