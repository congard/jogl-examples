package example.congard.jogl.shaders.sllightingadstexlp.attenuation.objmodels;

import java.io.File;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * 
 * @author congard
 *
 */
public class TextureUtils {

	public static int loadTexture(GL2 gl, File path) {
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
//	
//	public static void bindADSTextures(GL2 gl, int ambientUnit, int diffuseUnit, int specularUnit) {
//		in next repository 
//	}
}
