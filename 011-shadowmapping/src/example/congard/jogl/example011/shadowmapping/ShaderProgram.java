package example.congard.jogl.example011.shadowmapping;

import java.io.File;
import java.util.HashMap;

import com.jogamp.opengl.GL2;

/**
 * Manages the shader program.
 * 
 * @author Congard
 */
public class ShaderProgram {
	public int programId;
	public int vertexShaderId;
	public int fragmentShaderId;
	private boolean initialized = false;
	public HashMap<String, Integer> valuesIds = new HashMap<String, Integer>();
	
	/**
	 * Initializes the shader program.
	 * 
	 * @param gl2 context.
	 * @param vertexShader file.
	 * @param fragmentShader file.
	 * @return true if initialization was successful, false otherwise.
	 */
	public boolean init(GL2 gl2, File vertexShader, File fragmentShader) {
		if (initialized) {
			throw new IllegalStateException(
					"Unable to initialize the shader program! (it was already initialized)");
		}

		try {
			String vertexShaderCode = ShaderUtils.loadResource(vertexShader
					.getPath());
			String fragmentShaderCode = ShaderUtils.loadResource(fragmentShader
					.getPath());

			programId = gl2.glCreateProgram();
			vertexShaderId = ShaderUtils.createShader(gl2, programId,
					vertexShaderCode, GL2.GL_VERTEX_SHADER);
			fragmentShaderId = ShaderUtils.createShader(gl2, programId,
					fragmentShaderCode, GL2.GL_FRAGMENT_SHADER);

			ShaderUtils.link(gl2, programId);

			initialized = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return initialized;

	}
	
	public int getValueId(String name) {
		return valuesIds.containsKey(name) ? valuesIds.get(name) : 0;
	}
	
	public void loadValuesIds(GL2 gl, GLSLValue...values) {
		for (GLSLValue v : values) {
			if (v.type == GLSLValue.ATTRIB) valuesIds.put(v.name, gl.glGetAttribLocation(programId, v.name));
			else if (v.type == GLSLValue.UNIFORM) valuesIds.put(v.name, gl.glGetUniformLocation(programId, v.name));
		}
	}
	
	/**
	 * Destroys the shader program.
	 * 
	 * @param gl2 context.
	 */
	public void dispose(GL2 gl2) {
		initialized = false;
		gl2.glDetachShader(programId, vertexShaderId);
		gl2.glDetachShader(programId, fragmentShaderId);
		gl2.glDeleteProgram(programId);
	}

	/**
	 * @return shader program id.
	 */
	public int getProgramId() {
		if (!initialized) {
			throw new IllegalStateException(
					"Unable to get the program id! The shader program was not initialized!");
		}
		return programId;
	}

	public boolean isInitialized() {
		return initialized;
	}
	
	/**
	 * 
	 * @author congard
	 *
	 */
	public static class GLSLValue {
		// constants
		public static final int ATTRIB = 0, UNIFORM = 1;
		
		public String name;
		public int type;
		
		public GLSLValue(String name, int type) {
			this.name = name;
			this.type = type;
		}
	}
}
