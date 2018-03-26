package example.congard.jogl.shaders.lighting.sl;

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
	
	// collects all lamps coordinates to one array
	public static float[] createFullList(Lamp[] ls) {
		float[] result = new float[ls.length * 3]; // *3 because xyz
		
		for (int i = 0; i<ls.length; i++) {
			result[i * 3 + 0] = ls[i].mLightPosInEyeSpace[0];
			result[i * 3 + 1] = ls[i].mLightPosInEyeSpace[1];
			result[i * 3 + 2] = ls[i].mLightPosInEyeSpace[2];
		}
		
		return result;
	}
}
