package example.congard.jogl.shaders.sllighting.texture;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL2;

/**
 * Shader program utilities.
 * 
 * @author Congard & serhiy
 */
public class ShaderUtils {
	
	private ShaderUtils() {
		/* Prevent initialization, only static methods below. */
	}
	
	// log of shader obj
	// getShaderInfoLog(gl, vertexShaderId) - example
	public static String getShaderInfoLog(final GL2 gl, final int shaderObj) {
        final int[] infoLogLength=new int[1];
        gl.glGetShaderiv(shaderObj, GL2.GL_INFO_LOG_LENGTH, infoLogLength, 0);

        if(infoLogLength[0]==0) {
            return "(no info log)";
        }
        final int[] charsWritten=new int[1];
        final byte[] infoLogBytes = new byte[infoLogLength[0]];
        gl.glGetShaderInfoLog(shaderObj, infoLogLength[0], charsWritten, 0, infoLogBytes, 0);

        return new String(infoLogBytes, 0, charsWritten[0]);
    }
	
	// fixed
	public static String loadResource(String fileName) throws Exception {
		FileInputStream in = new FileInputStream(fileName);
		byte b[] = new byte[in.available()];
		in.read(b);
		in.close();
		return new String(b);
	}
	
	/**
	 * Creates and compile the shader in the shader program.
	 * 
	 * @param gl2 context.
	 * @param programId to create its shaders.
	 * @param shaderCode to compile.
	 * @param shaderType of the shader to be compiled.
	 * @return the id of the created and compiled shader.
	 * @throws Exception when an error occurs creating the shader program.
	 */
	public static int createShader(GL2 gl2, int programId, String shaderCode, int shaderType) throws Exception {
		int shaderId = gl2.glCreateShader(shaderType);
		if (shaderId == 0) {
			throw new Exception("Error creating shader. Shader id is zero.");
		}
		
		gl2.glShaderSource(shaderId, 1, new String[] { shaderCode }, null);
		gl2.glCompileShader(shaderId);
		
		IntBuffer intBuffer = IntBuffer.allocate(1);
		gl2.glGetShaderiv(shaderId, GL2.GL_COMPILE_STATUS, intBuffer);

		if (intBuffer.get(0) != 1) {
			gl2.glGetShaderiv(shaderId, GL2.GL_INFO_LOG_LENGTH, intBuffer);
			int size = intBuffer.get(0);
			if (size > 0) {
				ByteBuffer byteBuffer = ByteBuffer.allocate(size);
				gl2.glGetShaderInfoLog(shaderId, size, intBuffer, byteBuffer);
				System.out.println(new String(byteBuffer.array()));
			}
			throw new Exception("Error compiling shader!");
		}

		gl2.glAttachShader(programId, shaderId);

		return shaderId;
	}

	/**
	 * Links the shaders within created shader program.
	 * 
	 * @param gl2 context.
	 * @param programId to link its shaders.
	 * @throws Exception when an error occurs linking the shaders.
	 */
	public static void link(GL2 gl2, int programId) throws Exception {
		gl2.glLinkProgram(programId);

		IntBuffer intBuffer = IntBuffer.allocate(1);
		gl2.glGetProgramiv(programId, GL2.GL_LINK_STATUS, intBuffer);

		if (intBuffer.get(0) != 1) {
			gl2.glGetProgramiv(programId, GL2.GL_INFO_LOG_LENGTH, intBuffer);
			int size = intBuffer.get(0);
			if (size > 0) {
				ByteBuffer byteBuffer = ByteBuffer.allocate(size);
				gl2.glGetProgramInfoLog(programId, size, intBuffer, byteBuffer);
				System.out.println(new String(byteBuffer.array()));
			}
			throw new Exception("Error linking shader program!");
		}

		gl2.glValidateProgram(programId);

		intBuffer = IntBuffer.allocate(1);
		gl2.glGetProgramiv(programId, GL2.GL_VALIDATE_STATUS, intBuffer);

		if (intBuffer.get(0) != 1) {
			gl2.glGetProgramiv(programId, GL2.GL_INFO_LOG_LENGTH, intBuffer);
			int size = intBuffer.get(0);
			if (size > 0) {
				ByteBuffer byteBuffer = ByteBuffer.allocate(size);
				gl2.glGetProgramInfoLog(programId, size, intBuffer, byteBuffer);
				System.out.println(new String(byteBuffer.array()));
			}
			throw new Exception("Error validating shader program!");
		}
	}
}
