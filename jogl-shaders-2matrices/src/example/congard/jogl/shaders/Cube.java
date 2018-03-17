package example.congard.jogl.shaders;

/**
 * Represents the cube with different color shapes.
 * 
 * @author Congard & serhiy
 */
public class Cube {
	public static final float [] vertices = { /* Front face */
										0.0f,  0.0f,  0.0f,
            							0.5f,  0.0f,  0.0f,
										0.5f,  0.5f,  0.0f,
										0.0f,  0.5f,  0.0f,
										/* Left face */
										0.0f,  0.0f,  0.5f,
            							0.0f,  0.0f,  0.0f,
										0.0f,  0.5f,  0.0f,
										0.0f,  0.5f,  0.5f,
										/* Back face */
										0.5f,  0.0f,  0.5f,
										0.0f,  0.0f,  0.5f,
            							0.0f,  0.5f,  0.5f,
										0.5f,  0.5f,  0.5f,
										/* Right face */
										0.5f,  0.0f,  0.0f,
										0.5f,  0.0f,  0.5f,
            							0.5f,  0.5f,  0.5f,
										0.5f,  0.5f,  0.0f,
										/* Top face */
										0.0f,  0.5f,  0.0f,
										0.5f,  0.5f,  0.0f,
            							0.5f,  0.5f,  0.5f,
										0.0f,  0.5f,  0.5f,
										/* Bottom face */
										0.5f,  0.0f,  0.5f,
										0.0f,  0.0f,  0.5f,
            							0.0f,  0.0f,  0.0f,
										0.5f,  0.0f,  0.0f,
										};
	
	public static final int [] indices = 		{/* Front face */
										0, 1, 2,
										2, 0, 3,
										/* Left face */
										4, 5, 6,
										6, 4, 7,
										/* Back face */
										8, 9, 10,
										10, 8, 11,
										/* Right face */
										12, 13, 14,
										14, 12, 15,
										/* Top face */
										16, 17, 18,
										18, 16, 19,
										/* Bottom face */
										20, 21, 22,
										22, 20, 23
										};
	
	public static final float [] colors = 	{ /* Front face */
										 1.0f,   0.0f,   0.0f,
										 1.0f,   0.0f,   0.0f,
										 1.0f,   0.0f,   0.0f,
										 1.0f,   0.0f,   0.0f,
										 /* Left face */
										 0.0f,   1.0f,   0.0f,
										 0.0f,   1.0f,   0.0f,
										 0.0f,   1.0f,   0.0f,
										 0.0f,   1.0f,   0.0f,
										 /* Back face */
										 0.0f,   0.0f,   1.0f,
										 0.0f,   0.0f,   1.0f,
										 0.0f,   0.0f,   1.0f,
										 0.0f,   0.0f,   1.0f,
										 /* Right face */
										 1.0f,   0.647f, 0.0f,
										 1.0f,   0.647f, 0.0f,
										 1.0f,   0.647f, 0.0f,
										 1.0f,   0.647f, 0.0f,
										 /* Top face */
										 1.0f,   1.0f,   1.0f,
										 1.0f,   1.0f,   1.0f,
										 1.0f,   1.0f,   1.0f,
										 1.0f,   1.0f,   1.0f,
										 /* Bottom face */
										 1.0f,   1.0f,   0.0f,
										 1.0f,   1.0f,   0.0f,
										 1.0f,   1.0f,   0.0f,
										 1.0f,   1.0f,   0.0f
										 };
}
