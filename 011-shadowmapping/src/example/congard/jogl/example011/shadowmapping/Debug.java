package example.congard.jogl.example011.shadowmapping;

import java.io.File;
import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import example.congard.jogl.example011.shadowmapping.ShaderProgram.GLSLValue;

/**
 * Class for debugging
 * @author congard
 *
 */
public class Debug {
	private ShaderProgram shaderDebugProgram;
	private FloatBuffer vertexBuffer, texCoordsBuffer;
	
	public Debug(GL2 gl) {
		File vertexDebugShader = new File("./resources/shaders/vertex_debug_shader.glsl");
		File fragmentDebugShader = new File("./resources/shaders/fragment_debug_shader.glsl");
		
		
		shaderDebugProgram = new ShaderProgram();
		if (!shaderDebugProgram.init(gl, vertexDebugShader, fragmentDebugShader)) {
			throw new IllegalStateException("Unable to initiate the shaders!");
		}
		
		shaderDebugProgram.loadValuesIds(gl, 
				new GLSLValue("a_Position", GLSLValue.ATTRIB),
				new GLSLValue("a_TexCoord", GLSLValue.ATTRIB),
				new GLSLValue("u_Texture", GLSLValue.UNIFORM)
		);
		
		float[] vertices = {
				-1.0f, 1.0f, -1.0f,				
				-1.0f, -1.0f, -1.0f,
				1.0f, 1.0f, -1.0f, 
				-1.0f, -1.0f, -1.0f, 				
				1.0f, -1.0f, -1.0f,
				1.0f, 1.0f, -1.0f,
		}, texCoords = {
				0, 1,
				0, 0,
				1, 1,
				0, 0,
				1, 0,
				1, 1
		};
		
		vertexBuffer = Buffers.newDirectFloatBuffer(vertices.length);
		texCoordsBuffer = Buffers.newDirectFloatBuffer(texCoords.length);
		vertexBuffer.put(vertices).position(0);
		texCoordsBuffer.put(texCoords).position(0);
	}
	
	public void renderTexture(GL2 gl, int texture) {
		gl.glUseProgram(shaderDebugProgram.getProgramId());
		
		VertexAttribTools.enable(gl, shaderDebugProgram.getValueId("a_Position"), shaderDebugProgram.getValueId("a_TexCoord"));
		VertexAttribTools.pointer(gl, shaderDebugProgram.getValueId("a_Position"), 3, vertexBuffer.rewind());
		VertexAttribTools.pointer(gl, shaderDebugProgram.getValueId("a_TexCoord"), 2, texCoordsBuffer.rewind());
		
		gl.glActiveTexture(GL.GL_TEXTURE0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, texture);
		
		gl.glDrawArrays(GL.GL_TRIANGLES, 0, 6);
		
		VertexAttribTools.disable(gl, shaderDebugProgram.getValueId("a_Position"), shaderDebugProgram.getValueId("a_TexCoord"));
		
		gl.glUseProgram(0);
	}
}
