package example.congard.jogl.example011.shadowmapping;

import com.jogamp.opengl.GL2;

/**
 * 
 * @author congard
 *
 */
public class Lamps {
	int[][] lamps;
	ShaderProgram sp;
	GL2 gl2;
	
	public Lamps(GL2 gl2, ShaderProgram sp, int lampsCount) {
		this.sp = sp;
		this.gl2 = gl2;
		lamps = new int[lampsCount][11]; // 10 - count of glsl variables
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
			lamps[i][9] = gl2.glGetUniformLocation(sp.programId, "u_Lamps[" + i + "].lightSpaceMatrix");
			lamps[i][10] = gl2.glGetUniformLocation(sp.programId, "u_Lamps[" + i + "].lightModelMatrix");
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
	
	public int getLightSpaceMatrixLocation(int lampId) {
		return lamps[lampId][9];
	}
	
	public int getLightModelMatrixLocation(int lampId) {
		return lamps[lampId][10];
	}
}