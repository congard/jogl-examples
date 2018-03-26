package example.congard.jogl.shaders.lighting.sl;

import java.io.File;

import com.jogamp.opengl.GL2;

/**
 * Manages the shader program.
 * 
 * @author Congard & serhiy
 */
public class ShaderProgram {
	private int programId;
	private int vertexShaderId;
	public int fragmentShaderId;
	private boolean initialized = false;
	public int a_PositionLocation, a_ColorLocation, a_NormalLocation, u_MVPMatrixLocation, u_MVMatrixLocation, u_LightPosLocation;

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

			a_PositionLocation = gl2.glGetAttribLocation(programId, "a_Position");
			a_NormalLocation = gl2.glGetAttribLocation(programId, "a_Normal");
			a_ColorLocation = gl2.glGetAttribLocation(programId, "a_Color");
			u_MVPMatrixLocation = gl2.glGetUniformLocation(programId, "u_MVPMatrix");
			u_MVMatrixLocation = gl2.glGetUniformLocation(programId, "u_MVMatrix");
			u_LightPosLocation = gl2.glGetUniformLocation(programId, "u_LampsPos");
			
			initialized = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return initialized;

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
}
