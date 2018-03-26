package example.congard.jogl.shaders.lighting.sl;

/**
 * Represents the cube with different color shapes.
 * 
 * @author Congard
 */
public class Cube {
	// X, Y, Z
			public final static float[] vertices = {
					// Front face
					-1.0f, 1.0f, 1.0f,				
					-1.0f, -1.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 
					-1.0f, -1.0f, 1.0f, 				
					1.0f, -1.0f, 1.0f,
					1.0f, 1.0f, 1.0f,
					
					// Right face
					1.0f, 1.0f, 1.0f,				
					1.0f, -1.0f, 1.0f,
					1.0f, 1.0f, -1.0f,
					1.0f, -1.0f, 1.0f,				
					1.0f, -1.0f, -1.0f,
					1.0f, 1.0f, -1.0f,
					
					// Back face
					1.0f, 1.0f, -1.0f,				
					1.0f, -1.0f, -1.0f,
					-1.0f, 1.0f, -1.0f,
					1.0f, -1.0f, -1.0f,				
					-1.0f, -1.0f, -1.0f,
					-1.0f, 1.0f, -1.0f,
					
					// Left face
					-1.0f, 1.0f, -1.0f,				
					-1.0f, -1.0f, -1.0f,
					-1.0f, 1.0f, 1.0f, 
					-1.0f, -1.0f, -1.0f,				
					-1.0f, -1.0f, 1.0f, 
					-1.0f, 1.0f, 1.0f, 
					
					// Top face
					-1.0f, 1.0f, -1.0f,				
					-1.0f, 1.0f, 1.0f, 
					1.0f, 1.0f, -1.0f, 
					-1.0f, 1.0f, 1.0f, 				
					1.0f, 1.0f, 1.0f, 
					1.0f, 1.0f, -1.0f,
					
					// Bottom face
					1.0f, -1.0f, -1.0f,				
					1.0f, -1.0f, 1.0f, 
					-1.0f, -1.0f, -1.0f,
					1.0f, -1.0f, 1.0f, 				
					-1.0f, -1.0f, 1.0f,
					-1.0f, -1.0f, -1.0f,
			};	
			
			// R, G, B, A
			public final static float[] colors = {				
					// Front face (red)
					1.0f, 0.0f, 0.0f, 1.0f,				
					1.0f, 0.0f, 0.0f, 1.0f,
					1.0f, 0.0f, 0.0f, 1.0f,
					1.0f, 0.0f, 0.0f, 1.0f,				
					1.0f, 0.0f, 0.0f, 1.0f,
					1.0f, 0.0f, 0.0f, 1.0f,
					
					// Right face (green)
					0.0f, 1.0f, 0.0f, 1.0f,				
					0.0f, 1.0f, 0.0f, 1.0f,
					0.0f, 1.0f, 0.0f, 1.0f,
					0.0f, 1.0f, 0.0f, 1.0f,				
					0.0f, 1.0f, 0.0f, 1.0f,
					0.0f, 1.0f, 0.0f, 1.0f,
					
					// Back face (blue)
					0.0f, 0.0f, 1.0f, 1.0f,				
					0.0f, 0.0f, 1.0f, 1.0f,
					0.0f, 0.0f, 1.0f, 1.0f,
					0.0f, 0.0f, 1.0f, 1.0f,				
					0.0f, 0.0f, 1.0f, 1.0f,
					0.0f, 0.0f, 1.0f, 1.0f,
					
					// Left face (yellow)
					1.0f, 1.0f, 0.0f, 1.0f,				
					1.0f, 1.0f, 0.0f, 1.0f,
					1.0f, 1.0f, 0.0f, 1.0f,
					1.0f, 1.0f, 0.0f, 1.0f,				
					1.0f, 1.0f, 0.0f, 1.0f,
					1.0f, 1.0f, 0.0f, 1.0f,
					
					// Top face (cyan)
					0.0f, 1.0f, 1.0f, 1.0f,				
					0.0f, 1.0f, 1.0f, 1.0f,
					0.0f, 1.0f, 1.0f, 1.0f,
					0.0f, 1.0f, 1.0f, 1.0f,				
					0.0f, 1.0f, 1.0f, 1.0f,
					0.0f, 1.0f, 1.0f, 1.0f,
					
					// Bottom face (magenta)
					1.0f, 0.0f, 1.0f, 1.0f,				
					1.0f, 0.0f, 1.0f, 1.0f,
					1.0f, 0.0f, 1.0f, 1.0f,
					1.0f, 0.0f, 1.0f, 1.0f,				
					1.0f, 0.0f, 1.0f, 1.0f,
					1.0f, 0.0f, 1.0f, 1.0f
			};
			
			// X, Y, Z
			// The normal is used in light calculations and is a vector which points
			// orthogonal to the plane of the surface. For a cube model, the normals
			// should be orthogonal to the points of each face.
			public final static float[] normals = {												
					// Front face
					0.0f, 0.0f, 1.0f,				
					0.0f, 0.0f, 1.0f,
					0.0f, 0.0f, 1.0f,
					0.0f, 0.0f, 1.0f,				
					0.0f, 0.0f, 1.0f,
					0.0f, 0.0f, 1.0f,
					
					// Right face 
					1.0f, 0.0f, 0.0f,				
					1.0f, 0.0f, 0.0f,
					1.0f, 0.0f, 0.0f,
					1.0f, 0.0f, 0.0f,				
					1.0f, 0.0f, 0.0f,
					1.0f, 0.0f, 0.0f,
					
					// Back face 
					0.0f, 0.0f, -1.0f,				
					0.0f, 0.0f, -1.0f,
					0.0f, 0.0f, -1.0f,
					0.0f, 0.0f, -1.0f,				
					0.0f, 0.0f, -1.0f,
					0.0f, 0.0f, -1.0f,
					
					// Left face 
					-1.0f, 0.0f, 0.0f,				
					-1.0f, 0.0f, 0.0f,
					-1.0f, 0.0f, 0.0f,
					-1.0f, 0.0f, 0.0f,				
					-1.0f, 0.0f, 0.0f,
					-1.0f, 0.0f, 0.0f,
					
					// Top face 
					0.0f, 1.0f, 0.0f,			
					0.0f, 1.0f, 0.0f,
					0.0f, 1.0f, 0.0f,
					0.0f, 1.0f, 0.0f,				
					0.0f, 1.0f, 0.0f,
					0.0f, 1.0f, 0.0f,
					
					// Bottom face 
					0.0f, -1.0f, 0.0f,			
					0.0f, -1.0f, 0.0f,
					0.0f, -1.0f, 0.0f,
					0.0f, -1.0f, 0.0f,				
					0.0f, -1.0f, 0.0f,
					0.0f, -1.0f, 0.0f
			};
}
