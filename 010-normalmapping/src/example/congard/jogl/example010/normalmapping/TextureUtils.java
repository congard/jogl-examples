package example.congard.jogl.example010.normalmapping;

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
	
	public static void bindADSNTextures(GL2 gl2, int ambientUnit, int diffuseUnit, int specularUnit, int normalUnit,
			int ambientTexture, int diffuseTexture, int specularTexture, int normalTexture) {
		gl2.glUniform1i(ambientUnit, 0);
		gl2.glActiveTexture(GL2.GL_TEXTURE0);
		gl2.glBindTexture(GL2.GL_TEXTURE_2D, ambientTexture);
		
		gl2.glUniform1i(diffuseUnit, 1);
		gl2.glActiveTexture(GL2.GL_TEXTURE1);
		gl2.glBindTexture(GL2.GL_TEXTURE_2D, diffuseTexture);
		
		gl2.glUniform1i(specularUnit, 2);
		gl2.glActiveTexture(GL2.GL_TEXTURE2);
		gl2.glBindTexture(GL2.GL_TEXTURE_2D, specularTexture);
		
		gl2.glUniform1i(normalUnit, 3);
		gl2.glActiveTexture(GL2.GL_TEXTURE3);
		gl2.glBindTexture(GL2.GL_TEXTURE_2D, normalTexture);
	}
	
	public static class Mapping {
		public int ambientTextureLocation, diffuseTextureLocation, specularTextureLocation, normalTextureLocation, isNormalMappingEnabledLocation;
		int programId;
		GL2 gl2;
		
		public Mapping(GL2 gl2, int programId) {
			this.programId = programId;
			this.gl2 = gl2;
		}
		
		public void loadLocations() {
			ambientTextureLocation = gl2.glGetUniformLocation(programId, "u_Mapping.ambient");
			diffuseTextureLocation = gl2.glGetUniformLocation(programId, "u_Mapping.diffuse");
			specularTextureLocation = gl2.glGetUniformLocation(programId, "u_Mapping.specular");
			normalTextureLocation = gl2.glGetUniformLocation(programId, "u_Mapping.normal");
			isNormalMappingEnabledLocation = gl2.glGetUniformLocation(programId, "u_NormalMapping");
		}
		
		// The varying qualifier can be used only with the data types float, vec2, vec3, vec4, mat2, mat3, and mat4, or arrays of these.
		public void setNormalMappingEnabled(GL2 gl2, float isEnabled) {
			gl2.glUniform1f(isNormalMappingEnabledLocation, isEnabled);
		}
		
		public void bindADSTextures(GL2 gl2, int ambientTexture, int diffuseTexture, int specularTexture) {
			TextureUtils.bindADSTextures(gl2, ambientTextureLocation, diffuseTextureLocation, specularTextureLocation, ambientTexture, diffuseTexture, specularTexture);
		}
		
		public void bindADSNTextures(GL2 gl2, int ambientTexture, int diffuseTexture, int specularTexture, int normalTexture) {
			TextureUtils.bindADSNTextures(gl2, ambientTextureLocation, diffuseTextureLocation, specularTextureLocation, normalTextureLocation, ambientTexture, diffuseTexture, specularTexture, normalTexture);
		}
	}
}
