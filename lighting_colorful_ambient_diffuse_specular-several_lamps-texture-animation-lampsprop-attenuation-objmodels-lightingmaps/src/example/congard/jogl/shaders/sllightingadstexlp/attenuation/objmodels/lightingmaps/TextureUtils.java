package example.congard.jogl.shaders.sllightingadstexlp.attenuation.objmodels.lightingmaps;

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
	
	public static void bindADSTextures(GL2 gl2, int ambientUnit, int diffuseUnit, int specularUnit, 
			int ambientTexture, int diffuseTexture, int specularTexture) {
		gl2.glUniform1i(ambientUnit, 0);
		gl2.glActiveTexture(GL2.GL_TEXTURE0);
		gl2.glBindTexture(GL2.GL_TEXTURE_2D, ambientTexture);
		
		gl2.glUniform1i(diffuseUnit, 1);
		gl2.glActiveTexture(GL2.GL_TEXTURE1);
		gl2.glBindTexture(GL2.GL_TEXTURE_2D, diffuseTexture);
		
		gl2.glUniform1i(specularUnit, 2);
		gl2.glActiveTexture(GL2.GL_TEXTURE2);
		gl2.glBindTexture(GL2.GL_TEXTURE_2D, specularTexture);
	}
}
