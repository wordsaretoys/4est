package com.wordsaretoys.rise.meshutil;


/**
 * generates an indexed grid of vertexes
 * use for heightmaps
 */

public class HeightMapper {

	protected VertexBuffer vbuffer;
	protected IndexBuffer ibuffer;
	protected int stride;

	/**
	 * override in your own subclass
	 */
	public void handle(VertexBuffer vb, boolean cw, float ir, float jr) {}
	
	/**
	 * constructor
	 * @param vb vertex buffer
	 * @param ib index buffer
	 * @param stride stride length (maintained in mesh)
	 */
	public HeightMapper(VertexBuffer vb, IndexBuffer ib, int stride) {
		vbuffer = vb;
		ibuffer = ib;
		this.stride = stride;
	}
	
	/**
	 * execute a mapping
	 * @param il, jl number of steps in each dimension
	 * @param cw true if clockwise winding, false if ccw
	 */
	public void run(int il, int jl, boolean cw) {
		int im = il - 1;
		int jm = jl - 1;
		int k = vbuffer.length / stride;
		int i, j;

		for (i = 0; i < il; i++) {
			for (j = 0; j < jl; j++, k++) {
				handle(vbuffer, cw, (float)i / (float)im, (float)j / (float)jm);
				if (i < im && j < jm) {
					if (cw) {
						ibuffer.set((short)(k), (short)(k + jl), (short)(k + 1));
						ibuffer.set((short)(k + jl), (short)(k + jl + 1), (short)(k + 1));
					} else {
						ibuffer.set((short)(k), (short)(k + 1), (short)(k + jl));
						ibuffer.set((short)(k + jl), (short)(k + 1), (short)(k + jl + 1));
					}
				}
			}
		}
	}
	
}
