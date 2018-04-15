package example.congard.jogl.example010.normalmapping;

import java.io.File;

import com.jogamp.opengl.GL2;

/**
 * Manages the shader program.
 * 
 * @author Congard
 */
public class ShaderProgram {
	private int programId;
	private int vertexShaderId;
	public int fragmentShaderId;
	private boolean initialized = false;
	public int a_PositionLocation, a_NormalLocation, a_TextureLocation, a_TangentLocation, a_BitangentLocation, 
	u_MVPMatrixLocation, u_MVMatrixLocation, 
	u_LampsCountLocation, u_ViewPosLocation;

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
			a_TextureLocation = gl2.glGetAttribLocation(programId, "a_Texture");
			a_TangentLocation = gl2.glGetAttribLocation(programId, "a_Tangent");
			a_BitangentLocation = gl2.glGetAttribLocation(programId, "a_Bitangent");
			u_MVPMatrixLocation = gl2.glGetUniformLocation(programId, "u_MVPMatrix");
			u_MVMatrixLocation = gl2.glGetUniformLocation(programId, "u_MVMatrix");
			u_LampsCountLocation = gl2.glGetUniformLocation(programId, "u_LampsCount");
			u_ViewPosLocation = gl2.glGetUniformLocation(programId, "u_ViewPos");
		
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
	
	public static class Lamps {
		int[][] lamps;
		ShaderProgram sp;
		GL2 gl2;
		
		public Lamps(GL2 gl2, ShaderProgram sp, int lampsCount) {
			this.sp = sp;
			this.gl2 = gl2;
			lamps = new int[lampsCount][9]; // 9 - count of glsl variables
		}
		
		public void loadLocations() {
			for (int i = 0; i<lamps.length; i++) {
				lamps[i][0] = gl2.glGetUniformLocation(sp.programId, "u_Lamps[" + i + "].diffuseStrength");
				lamps[i][1] = gl2.glGetUniformLocation(sp.programId, "u_Lamps[" + i + "].specularStrength");
				lamps[i][2] = gl2.glGetUniformLocation(sp.programId, "u_Lamps[" + i + "].shininess");
				lamps[i][3] = gl2.glGetUniformLocation(sp.programId, "u_Lamps[" + i + "].lampPos");
				lamps[i][4] = gl2.glGetUniformLocation(sp.programId, "u_Lamps[" + i + "].lampColor");
				lamps[i][5] = gl2.glGetUniformLocation(sp.programId, "u_Lamps[" + i + "].kc");
				lamps[i][6] = gl2.glGetUniformLocation(sp.programId, "u_Lamps[" + i + "].kl");
				lamps[i][7] = gl2.glGetUniformLocation(sp.programId, "u_Lamps[" + i + "].kq");
				lamps[i][8] = gl2.glGetUniformLocation(sp.programId, "u_Lamps[" + i + "].ambientStrength");
			}
		}
		
		public int getDiffuseStrengthLocation(int lampId) {
			return lamps[lampId][0];
		}
		
		public int getSpecularStrengthLocation(int lampId) {
			return lamps[lampId][1];
		}
		
		public int getShininessLocation(int lampId) {
			return lamps[lampId][2];
		}
		
		public int getLampPosLocation(int lampId) {
			return lamps[lampId][3];
		}
		
		public int getLampColorLocation(int lampId) {
			return lamps[lampId][4];
		}
		
		public int getKcLocation(int lampId) {
			return lamps[lampId][5];
		}
		
		public int getKlLocation(int lampId) {
			return lamps[lampId][6];
		}
		
		public int getKqLocation(int lampId) {
			return lamps[lampId][7];
		}
		
		public int getAmbientStrengthLocation(int lampId) {
			return lamps[lampId][8];
		}
	}
}
