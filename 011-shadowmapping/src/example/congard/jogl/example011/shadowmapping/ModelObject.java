package example.congard.jogl.example011.shadowmapping;

import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;

import free.lib.congard.ml.graphics.Vec3;
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
	public FloatBuffer tangentsBuffer;
	public FloatBuffer bitangentsBuffer;
	public Model model;
	public int ambientTexture, diffuseTexture, specularTexture, normalTexture;
	public float[] origin = new float[3];
	public float[] angles = new float[3];
	
	private ModelObject(FloatBuffer vertexBuffer, FloatBuffer normalsBuffer, FloatBuffer texCoordsBuffer,
			FloatBuffer tangentsBuffer, FloatBuffer bitangentsBuffer, 
			Model model,
			int ambientTexture, int diffuseTexture, int specularTexture, int normalTexture,
			float[] origin, float[] angles) {
		
		this.vertexBuffer = vertexBuffer;
		this.normalsBuffer = normalsBuffer;
		this.texCoordsBuffer = texCoordsBuffer;
		this.tangentsBuffer = tangentsBuffer;
		this.bitangentsBuffer = bitangentsBuffer;
		this.ambientTexture = ambientTexture;
		this.diffuseTexture = diffuseTexture;
		this.specularTexture = specularTexture;
		this.normalTexture = normalTexture;
		this.model = model;
		this.origin = origin;
		this.angles = angles;
	}
	
	public ModelObject(Model model) {
		this.model = model;
		init();
	}
	
	public ModelObject(Model model, int texture) {
		this.ambientTexture = texture;
		this.diffuseTexture = texture;
		this.specularTexture = texture;
		this.model = model;
		init();
	}
	
	public ModelObject(Model model, int texture, int normalTexture) {
		this.ambientTexture = texture;
		this.diffuseTexture = texture;
		this.specularTexture = texture;
		this.normalTexture = normalTexture;
		this.model = model;
		init();
	}
	
	public ModelObject(Model model, int ambientTexture, int diffuseTexture, int specularTexture) {
		this.ambientTexture = ambientTexture;
		this.diffuseTexture = diffuseTexture;
		this.specularTexture = specularTexture;
		this.model = model;
		init();
	}
	
	public ModelObject(Model model, int ambientTexture, int diffuseTexture, int specularTexture, int normalTexture) {
		this.ambientTexture = ambientTexture;
		this.diffuseTexture = diffuseTexture;
		this.specularTexture = specularTexture;
		this.normalTexture = normalTexture;
		this.model = model;
		init();
	}
	
	private void init() {
		vertexBuffer = Buffers.newDirectFloatBuffer(model.vertices.length);
		normalsBuffer = Buffers.newDirectFloatBuffer(model.normals.length);
		texCoordsBuffer = Buffers.newDirectFloatBuffer(model.texCoords.length);
		
		vertexBuffer.put(model.vertices);
		normalsBuffer.put(model.normals);
		texCoordsBuffer.put(model.texCoords);
	}
	
	public ModelObject clone() {
		return new ModelObject(vertexBuffer, normalsBuffer, texCoordsBuffer, tangentsBuffer, bitangentsBuffer, model, ambientTexture, diffuseTexture, specularTexture, normalTexture, origin, angles);
	}
	
	// This needed for correct normal mapping
	// Model must be TRIANGULATED!
	public void mkTBBuffers() {
		float[][] vertices = get2d(model.vertices, 3);
		float[][] tx = get2d(model.texCoords, 2);
		float[][] tangents = new float[vertices.length][3];
		float[][] bitangents = new float[vertices.length][3];
		
		for (int i = 0; i<vertices.length; i+=3) {
			float[] edge1 = vmv(vertices[i + 1], vertices[i + 0]);
			float[] edge2 = vmv(vertices[i + 2], vertices[i + 0]);
			float[] deltaUV1 = vmv(tx[i + 1], tx[i + 0]);
			float[] deltaUV2 = vmv(tx[i + 2], tx[i + 0]);
			
			float f = 1.0f / (deltaUV1[0] * deltaUV2[1] - deltaUV2[0] * deltaUV1[1]);
			
			float[] tangent = new float[3];
			tangent[0] = f * (deltaUV2[1] * edge1[0] - deltaUV1[1] * edge2[0]);
			tangent[1] = f * (deltaUV2[1] * edge1[1] - deltaUV1[1] * edge2[1]);
			tangent[2] = f * (deltaUV2[1] * edge1[2] - deltaUV1[1] * edge2[2]);
			Vec3.normalize(tangent);
			
			float[] bitangent = new float[3];
			bitangent[0] = f * (-deltaUV2[0] * edge1[0] + deltaUV1[0] * edge2[0]);
			bitangent[1] = f * (-deltaUV2[0] * edge1[1] + deltaUV1[0] * edge2[1]);
			bitangent[2] = f * (-deltaUV2[0] * edge1[2] + deltaUV1[0] * edge2[2]);
			Vec3.normalize(bitangent);
			
			tangents[i + 0] = tangent;
			tangents[i + 1] = tangent;
			tangents[i + 2] = tangent;
			
			bitangents[i + 0] = bitangent;
			bitangents[i + 1] = bitangent;
			bitangents[i + 2] = bitangent;
		}
		
		// creating buffers
		float[] tmp = get1d(tangents);
		tangentsBuffer = Buffers.newDirectFloatBuffer(tmp.length);
		tangentsBuffer.put(tmp);
		tmp = get1d(bitangents);
		bitangentsBuffer = Buffers.newDirectFloatBuffer(tmp.length);
		bitangentsBuffer.put(tmp);
	}
	
	private float[] get1d(float[][] arr) {
		float[] result = new float[arr.length * arr[1].length];
		for (int y = 0; y<arr.length; y++)
			for (int x = 0; x<arr[0].length; x++) result[y * arr[0].length + x] = arr[y][x];
		return result;
	}
	
	private float[][] get2d(float[] arr, int width) {
		float[][] result = new float[arr.length/width][width];
		for (int y = 0; y<result.length; y++)
			for (int x = 0; x<width; x++) result[y][x] = arr[y * width + x];
		return result;
	}
	
	// vec minus vec
	private float[] vmv(float[] vec1, float[] vec2) {
		float[] result = new float[vec1.length];
		for (int i = 0; i<vec1.length; i++) {
			result[i] = vec1[i] - vec2[i];
		}
		return result;
	}
}
