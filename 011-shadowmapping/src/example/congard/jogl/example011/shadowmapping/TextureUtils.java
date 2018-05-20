package example.congard.jogl.example011.shadowmapping;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL;
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
	
	public static void bindShadowMap(GL2 gl, int shadowMapUnit, int shadowMapTexture) {
		
	}
	
	/**
	 * 
	 * @author congard
	 *
	 */
	public static class TextureArray {
		public IntBuffer textureArray;
		public int width, height, layerCount, mipLevelCount = 1;
		public ByteBuffer pixels;
		
		// TYPE = GL_RED by default because we use shadow map with one component, we put it to red component
		public int PRECISION = GL2.GL_RGBA8, TYPE = GL2.GL_RED;
		
		public TextureArray() {
			textureArray = IntBuffer.allocate(1);
		}
		
		public void createTexture(GL2 gl) {
			gl.glGenTextures(1, textureArray);
		}
		
		public void write(GL2 gl) {
			gl.glActiveTexture(GL.GL_TEXTURE0);
	        gl.glBindTexture(GL2.GL_TEXTURE_2D_ARRAY, get());
	        gl.glTexStorage3D(GL2.GL_TEXTURE_2D_ARRAY, mipLevelCount, PRECISION, width, height, layerCount);
	        
	        gl.glTexSubImage3D(GL2.GL_TEXTURE_2D_ARRAY, 0, 0, 0, 0, width, height, layerCount, TYPE, GL.GL_UNSIGNED_BYTE, pixels);
	        
	        // Always set reasonable texture parameters
	        gl.glTexParameteri(GL2.GL_TEXTURE_2D_ARRAY, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
	        gl.glTexParameteri(GL2.GL_TEXTURE_2D_ARRAY, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
	        gl.glTexParameteri(GL2.GL_TEXTURE_2D_ARRAY, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE);
	        gl.glTexParameteri(GL2.GL_TEXTURE_2D_ARRAY, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE);
	        gl.glBindTexture(GL2.GL_TEXTURE_2D_ARRAY, 0);
		}
		
		public int get() {
			return textureArray.get(0);
		}
		
		// color components for example RGBA(4) RGB(3)
		public static ByteBuffer createByteBuffer(int width, int height, int colorComponents) {
			return ByteBuffer.allocate(width * height * colorComponents);
		}
		
		public static ByteBuffer createFullByteBuffer(ByteBuffer[] buffers) {
			// all buffers have the same size
			ByteBuffer result = ByteBuffer.allocate(buffers[0].capacity() * buffers.length);
			
			for (int i = 0; i<buffers.length; i++) {
				for (int j = 0; j<buffers[0].capacity(); j++) result.put(j+i*buffers[0].capacity(), buffers[i].get(j));
			}
			
			return result;
		}
	}
	
	/**
	 * 
	 * @author congard
	 *
	 */
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
