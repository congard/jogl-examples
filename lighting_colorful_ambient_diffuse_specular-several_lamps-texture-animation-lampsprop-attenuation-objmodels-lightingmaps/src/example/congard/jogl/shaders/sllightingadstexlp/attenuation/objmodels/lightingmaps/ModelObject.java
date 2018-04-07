package example.congard.jogl.shaders.sllightingadstexlp.attenuation.objmodels.lightingmaps;

import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;

import free.lib.congard.objloader.Model;

/**
 * 
 * @author congard
 *
 */
public class ModelObject {
	public FloatBuffer vertexBuffer;
	public FloatBuffer normalsBuffer;
	public FloatBuffer texCoordsBuffer;
	public Model model;
	int ambientTexture, diffuseTexture, specularTexture;
	
	public ModelObject(Model model, int ambientTexture, int diffuseTexture, int specularTexture) {
		vertexBuffer = Buffers.newDirectFloatBuffer(model.vertices.length);
		normalsBuffer = Buffers.newDirectFloatBuffer(model.normals.length);
		texCoordsBuffer = Buffers.newDirectFloatBuffer(model.texCoords.length);
		
		vertexBuffer.put(model.vertices);
		normalsBuffer.put(model.normals);
		texCoordsBuffer.put(model.texCoords);
		
		this.ambientTexture = ambientTexture;
		this.diffuseTexture = diffuseTexture;
		this.specularTexture = specularTexture;
		this.model = model;
	}
}
