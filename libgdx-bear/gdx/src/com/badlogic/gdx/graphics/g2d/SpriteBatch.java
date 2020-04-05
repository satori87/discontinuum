/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Mesh.VertexDataType;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.NumberUtils;

/** Draws batched quads using indices.
 * @see Batch
 * @author mzechner
 * @author Nathan Sweet */
public class SpriteBatch implements Batch {
	/** @deprecated Do not use, this field is for testing only and is likely to be removed. Sets the {@link VertexDataType} to be
	 *             used when gles 3 is not available, defaults to {@link VertexDataType#VertexArray}. */
	@Deprecated public static VertexDataType defaultVertexDataType = VertexDataType.VertexArray;

	private Mesh mesh;

	final float[] vertices;
	int idx = 0;
	public Texture lastTexture = null;
	public Texture uTex[] = new Texture[15];
	float invTexWidth = 0, invTexHeight = 0;
	public static float particleX = 0;
	public static float particleY = 0;
	public static int particleTexNum = 2;
	public static TextureRegion paletteRegion = null;

	public void setTexture (int i, Texture t) {
		uTex[i] = t;
	}

	boolean drawing = false;

	private final Matrix4 transformMatrix = new Matrix4();
	private final Matrix4 projectionMatrix = new Matrix4();
	private final Matrix4 combinedMatrix = new Matrix4();

	private boolean blendingDisabled = false;
	private int blendSrcFunc = GL20.GL_SRC_ALPHA;
	private int blendDstFunc = GL20.GL_ONE_MINUS_SRC_ALPHA;
	private int blendSrcFuncAlpha = GL20.GL_SRC_ALPHA;
	private int blendDstFuncAlpha = GL20.GL_ONE_MINUS_SRC_ALPHA;

	private final ShaderProgram shader;
	private ShaderProgram customShader = null;
	private boolean ownsShader;

	private final Color color = new Color(1, 1, 1, 1);
	float colorPacked = Color.WHITE_FLOAT_BITS;

	/** Number of render calls since the last {@link #begin()}. **/
	public int renderCalls = 0;

	/** Number of rendering calls, ever. Will not be reset unless set manually. **/
	public int totalRenderCalls = 0;

	/** The maximum number of sprites rendered in one batch so far. **/
	public int maxSpritesInBatch = 0;

	/** Constructs a new SpriteBatch with a size of 1000, one buffer, and the default shader.
	 * @see SpriteBatch#SpriteBatch(int, ShaderProgram) */
	public SpriteBatch () {
		this(1000, null);
	}

	/** Constructs a SpriteBatch with one buffer and the default shader.
	 * @see SpriteBatch#SpriteBatch(int, ShaderProgram) */
	public SpriteBatch (int size) {
		this(size, null);
	}

	/** Constructs a new SpriteBatch. Sets the projection matrix to an orthographic projection with y-axis point upwards, x-axis
	 * point to the right and the origin being in the bottom left corner of the screen. The projection will be pixel perfect with
	 * respect to the current screen resolution.
	 * <p>
	 * The defaultShader specifies the shader to use. Note that the names for uniforms for this default shader are different than
	 * the ones expect for shaders set with {@link #setShader(ShaderProgram)}. See {@link #createDefaultShader()}.
	 * @param size The max number of sprites in a single batch. Max of 8191.
	 * @param defaultShader The default shader to use. This is not owned by the SpriteBatch and must be disposed separately. */
	public SpriteBatch (int size, ShaderProgram defaultShader) {
		// 32767 is max vertex index, so 32767 / 4 vertices per sprite = 8191 sprites max.
		if (size > 8191) throw new IllegalArgumentException("Can't have more than 8191 sprites per batch: " + size);

		VertexDataType vertexDataType = (Gdx.gl30 != null) ? VertexDataType.VertexBufferObjectWithVAO : defaultVertexDataType;

		mesh = new Mesh(vertexDataType, false, size * 4, size * 6, //
			new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE), //
			new VertexAttribute(Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE), //
			new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"), //
			new VertexAttribute(Usage.Generic, 1, "a_data0"), //
			new VertexAttribute(Usage.Generic, 1, "a_data1"), //
			new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "1"), //
			new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "2"), //
			new VertexAttribute(Usage.Generic, 1, "a_data2"), //
			new VertexAttribute(Usage.Generic, 1, "a_data3") //
			);

		projectionMatrix.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		vertices = new float[size * Sprite.SPRITE_SIZE];

		int len = size * 6;
		short[] indices = new short[len];
		short j = 0;
		for (int i = 0; i < len; i += 6, j += 4) {
			indices[i] = j;
			indices[i + 1] = (short)(j + 1);
			indices[i + 2] = (short)(j + 2);
			indices[i + 3] = (short)(j + 2);
			indices[i + 4] = (short)(j + 3);
			indices[i + 5] = j;
		}
		mesh.setIndices(indices);

		if (defaultShader == null) {
			shader = createDefaultShader();
			ownsShader = true;
		} else
			shader = defaultShader;
	}

	/** Returns a new instance of the default shader used by SpriteBatch for GL2 when no shader is specified. */
	static public ShaderProgram createDefaultShader () {
		String vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
			+ "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
			+ "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
			+ "uniform mat4 u_projTrans;\n" //
			+ "varying vec4 v_color;\n" //
			+ "varying vec2 v_texCoords;\n" //
			+ "\n" //
			+ "void main()\n" //
			+ "{\n" //
			+ "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
			+ "   v_color.a = v_color.a * (255.0/254.0);\n" //
			+ "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
			+ "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
			+ "}\n";
		String fragmentShader = "#ifdef GL_ES\n" //
			+ "#define LOWP lowp\n" //
			+ "precision mediump float;\n" //
			+ "#else\n" //
			+ "#define LOWP \n" //
			+ "#endif\n" //
			+ "varying LOWP vec4 v_color;\n" //
			+ "varying vec2 v_texCoords;\n" //
			+ "uniform sampler2D u_texture;\n" //
			+ "void main()\n"//
			+ "{\n" //
			+ "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n" //
			+ "}";

		ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
		if (!shader.isCompiled()) throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
		return shader;
	}

	@Override
	public void begin () {
		if (drawing) throw new IllegalStateException("SpriteBatch.end must be called before begin.");
		renderCalls = 0;

		Gdx.gl.glDepthMask(false);
		if (customShader != null)
			customShader.begin();
		else
			shader.begin();
		setupMatrices();

		drawing = true;
	}

	@Override
	public void end () {
		if (!drawing) throw new IllegalStateException("SpriteBatch.begin must be called before end.");
		if (idx > 0) flush();
		lastTexture = null;
		drawing = false;

		GL20 gl = Gdx.gl;
		gl.glDepthMask(true);
		if (isBlendingEnabled()) gl.glDisable(GL20.GL_BLEND);

		if (customShader != null)
			customShader.end();
		else
			shader.end();
	}

	@Override
	public void setColor (Color tint) {
		color.set(tint);
		colorPacked = tint.toFloatBits();
	}

	@Override
	public void setColor (float r, float g, float b, float a) {
		color.set(r, g, b, a);
		colorPacked = color.toFloatBits();
	}

	@Override
	public Color getColor () {
		return color;
	}

	@Override
	public void setPackedColor (float packedColor) {
		Color.abgr8888ToColor(color, packedColor);
		this.colorPacked = packedColor;
	}

	@Override
	public float getPackedColor () {
		return colorPacked;
	}

	@Override
	public void draw (Texture texture, float x, float y, float originX, float originY, float width, float height, float scaleX,
		float scaleY, float rotation, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY) {
		if (!drawing) throw new IllegalStateException("SpriteBatch.begin must be called before draw.");

		float[] vertices = this.vertices;

		if (texture != lastTexture)
			switchTexture(texture);
		else if (idx == vertices.length) //
			flush();

		// bottom left and top right corner points relative to origin
		final float worldOriginX = x + originX;
		final float worldOriginY = y + originY;
		float fx = -originX;
		float fy = -originY;
		float fx2 = width - originX;
		float fy2 = height - originY;

		// scale
		if (scaleX != 1 || scaleY != 1) {
			fx *= scaleX;
			fy *= scaleY;
			fx2 *= scaleX;
			fy2 *= scaleY;
		}

		// construct corner points, start from top left and go counter clockwise
		final float p1x = fx;
		final float p1y = fy;
		final float p2x = fx;
		final float p2y = fy2;
		final float p3x = fx2;
		final float p3y = fy2;
		final float p4x = fx2;
		final float p4y = fy;

		float x1;
		float y1;
		float x2;
		float y2;
		float x3;
		float y3;
		float x4;
		float y4;

		// rotate
		if (rotation != 0) {
			final float cos = MathUtils.cosDeg(rotation);
			final float sin = MathUtils.sinDeg(rotation);

			x1 = cos * p1x - sin * p1y;
			y1 = sin * p1x + cos * p1y;

			x2 = cos * p2x - sin * p2y;
			y2 = sin * p2x + cos * p2y;

			x3 = cos * p3x - sin * p3y;
			y3 = sin * p3x + cos * p3y;

			x4 = x1 + (x3 - x2);
			y4 = y3 - (y2 - y1);
		} else {
			x1 = p1x;
			y1 = p1y;

			x2 = p2x;
			y2 = p2y;

			x3 = p3x;
			y3 = p3y;

			x4 = p4x;
			y4 = p4y;
		}

		x1 += worldOriginX;
		y1 += worldOriginY;
		x2 += worldOriginX;
		y2 += worldOriginY;
		x3 += worldOriginX;
		y3 += worldOriginY;
		x4 += worldOriginX;
		y4 += worldOriginY;

		float u = srcX * invTexWidth;
		float v = (srcY + srcHeight) * invTexHeight;
		float u2 = (srcX + srcWidth) * invTexWidth;
		float v2 = srcY * invTexHeight;

		if (flipX) {
			float tmp = u;
			u = u2;
			u2 = tmp;
		}

		if (flipY) {
			float tmp = v;
			v = v2;
			v2 = tmp;
		}

		float color = this.colorPacked;
		int idx = this.idx;
		vertices[idx] = x1;
		vertices[idx + 1] = y1;
		vertices[idx + 2] = color;
		vertices[idx + 3] = u;
		vertices[idx + 4] = v;

		vertices[idx + 5] = x2;
		vertices[idx + 6] = y2;
		vertices[idx + 7] = color;
		vertices[idx + 8] = u;
		vertices[idx + 9] = v2;

		vertices[idx + 10] = x3;
		vertices[idx + 11] = y3;
		vertices[idx + 12] = color;
		vertices[idx + 13] = u2;
		vertices[idx + 14] = v2;

		vertices[idx + 15] = x4;
		vertices[idx + 16] = y4;
		vertices[idx + 17] = color;
		vertices[idx + 18] = u2;
		vertices[idx + 19] = v;
		this.idx = idx + 20;
	}

	@Override
	public void draw (Texture texture, float x, float y, float width, float height, int srcX, int srcY, int srcWidth,
		int srcHeight, boolean flipX, boolean flipY) {
		draw(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight, flipX, flipY, new float[] {0, 0}, null, null);
	}

	public void draw (Texture texture, float x, float y, float width, float height, int srcX, int srcY, int srcWidth,
		int srcHeight, boolean flipX, boolean flipY, float[] data, TextureRegion aregion, TextureRegion bregion) {
		if (!drawing) throw new IllegalStateException("SpriteBatch.begin must be called before draw.");

		float[] vertices = this.vertices;

		if (texture != lastTexture && data[1] == 0)
			switchTexture(texture);
		else if (idx == vertices.length) //
			flush();

		float u = srcX * invTexWidth;
		float v = (srcY + srcHeight) * invTexHeight;
		float u2 = (srcX + srcWidth) * invTexWidth;
		float v2 = srcY * invTexHeight;
		final float fx2 = x + width;
		final float fy2 = y + height;

		if (flipX) {
			float tmp = u;
			u = u2;
			u2 = tmp;
		}

		if (flipY) {
			float tmp = v;
			v = v2;
			v2 = tmp;
		}

		float au = 0;
		float av = 0;
		float au2 = 0;
		float av2 = 0;
		float bu = 0;
		float bv = 0;
		float bu2 = 0;
		float bv2 = 0;
		if (aregion != null) {
			au = aregion.u;
			av = aregion.v2;
			au2 = aregion.u2;
			av2 = aregion.v;
		}
		if (bregion != null) {
			bu = bregion.u;
			bv = bregion.v2;
			bu2 = bregion.u2;
			bv2 = bregion.v;
		}
		float pu = 0;
		float pv = 0;
		float pu2 = 0;
		float pv2 = 0;
		if (paletteRegion != null) {
			pu = paletteRegion.u;
			pv = paletteRegion.v2;
			pu2 = paletteRegion.u2;
			pv2 = paletteRegion.v;
		}
		float color = this.colorPacked;
		int idx = this.idx;
		vertices[idx + X1] = x;
		vertices[idx + Y1] = y;
		vertices[idx + C1] = color;
		vertices[idx + U1] = u;
		vertices[idx + V1] = v;
		vertices[idx + D1] = data[0];
		vertices[idx + E1] = data[1];
		vertices[idx + P1] = au;
		vertices[idx + Q1] = av;
		vertices[idx + F1] = bu;
		vertices[idx + G1] = bv;
		vertices[idx + M1] = pu;
		vertices[idx + N1] = pv;

		vertices[idx + X2] = x;
		vertices[idx + Y2] = fy2;
		vertices[idx + C2] = color;
		vertices[idx + U2] = u;
		vertices[idx + V2] = v2;
		vertices[idx + D2] = data[0];
		vertices[idx + E2] = data[1];
		vertices[idx + P2] = au;
		vertices[idx + Q2] = av2;
		vertices[idx + F2] = bu;
		vertices[idx + G2] = bv2;
		vertices[idx + M2] = pu;
		vertices[idx + N2] = pv2;

		vertices[idx + X3] = fx2;
		vertices[idx + Y3] = fy2;
		vertices[idx + C3] = color;
		vertices[idx + U3] = u2;
		vertices[idx + V3] = v2;
		vertices[idx + D3] = data[0];
		vertices[idx + E3] = data[1];
		vertices[idx + P3] = au2;
		vertices[idx + Q3] = av2;
		vertices[idx + F3] = bu2;
		vertices[idx + G3] = bv2;
		vertices[idx + M3] = pu2;
		vertices[idx + N3] = pv2;

		vertices[idx + X4] = fx2;
		vertices[idx + Y4] = y;
		vertices[idx + C4] = color;
		vertices[idx + U4] = u2;
		vertices[idx + V4] = v;
		vertices[idx + D4] = data[0];
		vertices[idx + E4] = data[1];
		vertices[idx + P4] = au2;
		vertices[idx + Q4] = av;
		vertices[idx + F4] = bu2;
		vertices[idx + G4] = bv;
		vertices[idx + M4] = pu2;
		vertices[idx + N4] = pv;

		this.idx = idx + 52;
	}

	@Override
	public void draw (Texture texture, float x, float y, int srcX, int srcY, int srcWidth, int srcHeight) {
		if (!drawing) throw new IllegalStateException("SpriteBatch.begin must be called before draw.");

		float[] vertices = this.vertices;

		if (texture != lastTexture)
			switchTexture(texture);
		else if (idx == vertices.length) //
			flush();

		final float u = srcX * invTexWidth;
		final float v = (srcY + srcHeight) * invTexHeight;
		final float u2 = (srcX + srcWidth) * invTexWidth;
		final float v2 = srcY * invTexHeight;
		final float fx2 = x + srcWidth;
		final float fy2 = y + srcHeight;

		float color = this.colorPacked;
		int idx = this.idx;
		vertices[idx] = x;
		vertices[idx + 1] = y;
		vertices[idx + 2] = color;
		vertices[idx + 3] = u;
		vertices[idx + 4] = v;

		vertices[idx + 5] = x;
		vertices[idx + 6] = fy2;
		vertices[idx + 7] = color;
		vertices[idx + 8] = u;
		vertices[idx + 9] = v2;

		vertices[idx + 10] = fx2;
		vertices[idx + 11] = fy2;
		vertices[idx + 12] = color;
		vertices[idx + 13] = u2;
		vertices[idx + 14] = v2;

		vertices[idx + 15] = fx2;
		vertices[idx + 16] = y;
		vertices[idx + 17] = color;
		vertices[idx + 18] = u2;
		vertices[idx + 19] = v;
		this.idx = idx + 20;
	}

	@Override
	public void draw (Texture texture, float x, float y, float width, float height, float u, float v, float u2, float v2) {
		if (!drawing) throw new IllegalStateException("SpriteBatch.begin must be called before draw.");

		float[] vertices = this.vertices;

		if (texture != lastTexture)
			switchTexture(texture);
		else if (idx == vertices.length) //
			flush();

		final float fx2 = x + width;
		final float fy2 = y + height;

		float color = this.colorPacked;
		int idx = this.idx;
		vertices[idx] = x;
		vertices[idx + 1] = y;
		vertices[idx + 2] = color;
		vertices[idx + 3] = u;
		vertices[idx + 4] = v;

		vertices[idx + 5] = x;
		vertices[idx + 6] = fy2;
		vertices[idx + 7] = color;
		vertices[idx + 8] = u;
		vertices[idx + 9] = v2;

		vertices[idx + 10] = fx2;
		vertices[idx + 11] = fy2;
		vertices[idx + 12] = color;
		vertices[idx + 13] = u2;
		vertices[idx + 14] = v2;

		vertices[idx + 15] = fx2;
		vertices[idx + 16] = y;
		vertices[idx + 17] = color;
		vertices[idx + 18] = u2;
		vertices[idx + 19] = v;
		this.idx = idx + 20;
	}

	@Override
	public void draw (Texture texture, float x, float y) {
		draw(texture, x, y, texture.getWidth(), texture.getHeight());
	}

	@Override
	public void draw (Texture texture, float x, float y, float width, float height) {
		if (!drawing) throw new IllegalStateException("SpriteBatch.begin must be called before draw.");

		float[] vertices = this.vertices;

		if (texture != lastTexture)
			switchTexture(texture);
		else if (idx == vertices.length) //
			flush();

		final float fx2 = x + width;
		final float fy2 = y + height;
		final float u = 0;
		final float v = 1;
		final float u2 = 1;
		final float v2 = 0;

		float color = this.colorPacked;
		int idx = this.idx;
		vertices[idx] = x;
		vertices[idx + 1] = y;
		vertices[idx + 2] = color;
		vertices[idx + 3] = u;
		vertices[idx + 4] = v;

		vertices[idx + 5] = x;
		vertices[idx + 6] = fy2;
		vertices[idx + 7] = color;
		vertices[idx + 8] = u;
		vertices[idx + 9] = v2;

		vertices[idx + 10] = fx2;
		vertices[idx + 11] = fy2;
		vertices[idx + 12] = color;
		vertices[idx + 13] = u2;
		vertices[idx + 14] = v2;

		vertices[idx + 15] = fx2;
		vertices[idx + 16] = y;
		vertices[idx + 17] = color;
		vertices[idx + 18] = u2;
		vertices[idx + 19] = v;
		this.idx = idx + 20;
	}

	@Override
	public void draw (Texture texture, float[] spriteVertices, int offset, int count) {
		if (!drawing) throw new IllegalStateException("SpriteBatch.begin must be called before draw.");

		int verticesLength = vertices.length;
		int remainingVertices = verticesLength;

		remainingVertices -= idx;
		if (remainingVertices == 0) {
			flush();
			remainingVertices = verticesLength;
		}

		int copyCount = Math.min(remainingVertices, count);

		System.arraycopy(spriteVertices, offset, vertices, idx, copyCount);
		idx += copyCount;
		count -= copyCount;
		while (count > 0) {
			offset += copyCount;
			flush();
			copyCount = Math.min(verticesLength, count);
			System.arraycopy(spriteVertices, offset, vertices, 0, copyCount);
			idx += copyCount;
			count -= copyCount;
		}
	}

	@Override
	public void draw (TextureRegion region, float x, float y) {
		draw(region, x, y, region.getRegionWidth(), region.getRegionHeight());
	}

	@Override
	public void draw (TextureRegion region, float x, float y, float width, float height) {
		if (!drawing) throw new IllegalStateException("SpriteBatch.begin must be called before draw.");

		float[] vertices = this.vertices;

		Texture texture = region.texture;
		if (texture != lastTexture) {
			switchTexture(texture);
		} else if (idx == vertices.length) //
			flush();

		final float fx2 = x + width;
		final float fy2 = y + height;
		final float u = region.u;
		final float v = region.v2;
		final float u2 = region.u2;
		final float v2 = region.v;

		float color = this.colorPacked;
		int idx = this.idx;
		vertices[idx] = x;
		vertices[idx + 1] = y;
		vertices[idx + 2] = color;
		vertices[idx + 3] = u;
		vertices[idx + 4] = v;

		vertices[idx + 5] = x;
		vertices[idx + 6] = fy2;
		vertices[idx + 7] = color;
		vertices[idx + 8] = u;
		vertices[idx + 9] = v2;

		vertices[idx + 10] = fx2;
		vertices[idx + 11] = fy2;
		vertices[idx + 12] = color;
		vertices[idx + 13] = u2;
		vertices[idx + 14] = v2;

		vertices[idx + 15] = fx2;
		vertices[idx + 16] = y;
		vertices[idx + 17] = color;
		vertices[idx + 18] = u2;
		vertices[idx + 19] = v;
		this.idx = idx + 20;
	}

	@Override
	public void draw (TextureRegion region, float x, float y, float originX, float originY, float width, float height,
		float scaleX, float scaleY, float rotation) {
		draw(region, x, y, originX, originY, width, height, scaleX, scaleY, rotation, new float[] {0, 0}, null, null);
	}

	public void draw (TextureRegion region, float x, float y, float originX, float originY, float width, float height,
		float scaleX, float scaleY, float rotation, float[] data, TextureRegion aregion, TextureRegion bregion) {
		if (!drawing) throw new IllegalStateException("SpriteBatch.begin must be called before draw.");

		float[] vertices = this.vertices;
		Texture texture = region.texture;

		if (texture != lastTexture && data[1] == 0) {
			switchTexture(texture);
		} else if (idx == vertices.length) //
			flush();

		// bottom left and top right corner points relative to origin
		final float worldOriginX = x + originX;
		final float worldOriginY = y + originY;
		float fx = -originX;
		float fy = -originY;
		float fx2 = width - originX;
		float fy2 = height - originY;

		// scale
		if (scaleX != 1 || scaleY != 1) {
			fx *= scaleX;
			fy *= scaleY;
			fx2 *= scaleX;
			fy2 *= scaleY;
		}

		// construct corner points, start from top left and go counter clockwise
		final float p1x = fx;
		final float p1y = fy;
		final float p2x = fx;
		final float p2y = fy2;
		final float p3x = fx2;
		final float p3y = fy2;
		final float p4x = fx2;
		final float p4y = fy;

		float x1;
		float y1;
		float x2;
		float y2;
		float x3;
		float y3;
		float x4;
		float y4;

		// rotate
		if (rotation != 0) {
			final float cos = MathUtils.cosDeg(rotation);
			final float sin = MathUtils.sinDeg(rotation);

			x1 = cos * p1x - sin * p1y;
			y1 = sin * p1x + cos * p1y;

			x2 = cos * p2x - sin * p2y;
			y2 = sin * p2x + cos * p2y;

			x3 = cos * p3x - sin * p3y;
			y3 = sin * p3x + cos * p3y;

			x4 = x1 + (x3 - x2);
			y4 = y3 - (y2 - y1);
		} else {
			x1 = p1x;
			y1 = p1y;

			x2 = p2x;
			y2 = p2y;

			x3 = p3x;
			y3 = p3y;

			x4 = p4x;
			y4 = p4y;
		}

		x1 += worldOriginX;
		y1 += worldOriginY;
		x2 += worldOriginX;
		y2 += worldOriginY;
		x3 += worldOriginX;
		y3 += worldOriginY;
		x4 += worldOriginX;
		y4 += worldOriginY;

		final float u = region.u;
		final float v = region.v2;
		final float u2 = region.u2;
		final float v2 = region.v;

		float au = 0;
		float av = 0;
		float au2 = 0;
		float av2 = 0;
		float bu = 0;
		float bv = 0;
		float bu2 = 0;
		float bv2 = 0;
		if (aregion != null) {
			au = aregion.u;
			av = aregion.v;
			au2 = aregion.u2;
			av2 = aregion.v2;
		}
		if (bregion != null) {
			bu = bregion.u;
			bv = bregion.v;
			bu2 = bregion.u2;
			bv2 = bregion.v2;
		}
		float pu = 0;
		float pv = 0;
		float pu2 = 0;
		float pv2 = 0;
		float px = 0;
		float py = 0;
		if (paletteRegion != null) {
			pu = paletteRegion.u;
			pv = paletteRegion.v2;
			pu2 = paletteRegion.u2;
			pv2 = paletteRegion.v;
			px = ((float)paletteRegion.getRegionX() + 0.5f)/4096f;
			py = ((float)paletteRegion.getRegionY() + 0.5f)/4096f;
		}
		

		float color = this.colorPacked;
		int idx = this.idx;
		vertices[idx + X1] = x1;
		vertices[idx + Y1] = y1;
		vertices[idx + C1] = color;
		vertices[idx + U1] = u;
		vertices[idx + V1] = v;
		vertices[idx + D1] = data[0];
		vertices[idx + E1] = data[1];
		vertices[idx + P1] = au;
		vertices[idx + Q1] = av;
		vertices[idx + F1] = bu;
		vertices[idx + G1] = bv;
		vertices[idx + M1] = px;
		vertices[idx + N1] = py;

		vertices[idx + X2] = x2;
		vertices[idx + Y2] = y2;
		vertices[idx + C2] = color;
		vertices[idx + U2] = u;
		vertices[idx + V2] = v2;
		vertices[idx + D2] = data[0];
		vertices[idx + E2] = data[1];
		vertices[idx + P2] = au;
		vertices[idx + Q2] = av2;
		vertices[idx + F2] = bu;
		vertices[idx + G2] = bv2;
		vertices[idx + M2] = px;
		vertices[idx + N2] = py;

		vertices[idx + X3] = x3;
		vertices[idx + Y3] = y3;
		vertices[idx + C3] = color;
		vertices[idx + U3] = u2;
		vertices[idx + V3] = v2;
		vertices[idx + D3] = data[0];
		vertices[idx + E3] = data[1];
		vertices[idx + P3] = au2;
		vertices[idx + Q3] = av2;
		vertices[idx + F3] = bu2;
		vertices[idx + G3] = bv2;
		vertices[idx + M3] = px;
		vertices[idx + N3] = py;

		vertices[idx + X4] = x4;
		vertices[idx + Y4] = y4;
		vertices[idx + C4] = color;
		vertices[idx + U4] = u2;
		vertices[idx + V4] = v;
		vertices[idx + D4] = data[0];
		vertices[idx + E4] = data[1];
		vertices[idx + P4] = au2;
		vertices[idx + Q4] = av;
		vertices[idx + F4] = bu2;
		vertices[idx + G4] = bv;
		vertices[idx + M4] = px;
		vertices[idx + N4] = py;

		this.idx = idx + 52;
	}

	@Override
	public void draw (TextureRegion region, float x, float y, float originX, float originY, float width, float height,
		float scaleX, float scaleY, float rotation, boolean clockwise) {
		if (!drawing) throw new IllegalStateException("SpriteBatch.begin must be called before draw.");

		float[] vertices = this.vertices;

		Texture texture = region.texture;
		if (texture != lastTexture) {
			switchTexture(texture);
		} else if (idx == vertices.length) //
			flush();

		// bottom left and top right corner points relative to origin
		final float worldOriginX = x + originX;
		final float worldOriginY = y + originY;
		float fx = -originX;
		float fy = -originY;
		float fx2 = width - originX;
		float fy2 = height - originY;

		// scale
		if (scaleX != 1 || scaleY != 1) {
			fx *= scaleX;
			fy *= scaleY;
			fx2 *= scaleX;
			fy2 *= scaleY;
		}

		// construct corner points, start from top left and go counter clockwise
		final float p1x = fx;
		final float p1y = fy;
		final float p2x = fx;
		final float p2y = fy2;
		final float p3x = fx2;
		final float p3y = fy2;
		final float p4x = fx2;
		final float p4y = fy;

		float x1;
		float y1;
		float x2;
		float y2;
		float x3;
		float y3;
		float x4;
		float y4;

		// rotate
		if (rotation != 0) {
			final float cos = MathUtils.cosDeg(rotation);
			final float sin = MathUtils.sinDeg(rotation);

			x1 = cos * p1x - sin * p1y;
			y1 = sin * p1x + cos * p1y;

			x2 = cos * p2x - sin * p2y;
			y2 = sin * p2x + cos * p2y;

			x3 = cos * p3x - sin * p3y;
			y3 = sin * p3x + cos * p3y;

			x4 = x1 + (x3 - x2);
			y4 = y3 - (y2 - y1);
		} else {
			x1 = p1x;
			y1 = p1y;

			x2 = p2x;
			y2 = p2y;

			x3 = p3x;
			y3 = p3y;

			x4 = p4x;
			y4 = p4y;
		}

		x1 += worldOriginX;
		y1 += worldOriginY;
		x2 += worldOriginX;
		y2 += worldOriginY;
		x3 += worldOriginX;
		y3 += worldOriginY;
		x4 += worldOriginX;
		y4 += worldOriginY;

		float u1, v1, u2, v2, u3, v3, u4, v4;
		if (clockwise) {
			u1 = region.u2;
			v1 = region.v2;
			u2 = region.u;
			v2 = region.v2;
			u3 = region.u;
			v3 = region.v;
			u4 = region.u2;
			v4 = region.v;
		} else {
			u1 = region.u;
			v1 = region.v;
			u2 = region.u2;
			v2 = region.v;
			u3 = region.u2;
			v3 = region.v2;
			u4 = region.u;
			v4 = region.v2;
		}

		float color = this.colorPacked;
		int idx = this.idx;
		vertices[idx] = x1;
		vertices[idx + 1] = y1;
		vertices[idx + 2] = color;
		vertices[idx + 3] = u1;
		vertices[idx + 4] = v1;

		vertices[idx + 5] = x2;
		vertices[idx + 6] = y2;
		vertices[idx + 7] = color;
		vertices[idx + 8] = u2;
		vertices[idx + 9] = v2;

		vertices[idx + 10] = x3;
		vertices[idx + 11] = y3;
		vertices[idx + 12] = color;
		vertices[idx + 13] = u3;
		vertices[idx + 14] = v3;

		vertices[idx + 15] = x4;
		vertices[idx + 16] = y4;
		vertices[idx + 17] = color;
		vertices[idx + 18] = u4;
		vertices[idx + 19] = v4;
		this.idx = idx + 20;
	}

	@Override
	public void draw (TextureRegion region, float width, float height, Affine2 transform) {
		if (!drawing) throw new IllegalStateException("SpriteBatch.begin must be called before draw.");

		float[] vertices = this.vertices;

		Texture texture = region.texture;
		if (texture != lastTexture) {
			switchTexture(texture);
		} else if (idx == vertices.length) {
			flush();
		}

		// construct corner points
		float x1 = transform.m02;
		float y1 = transform.m12;
		float x2 = transform.m01 * height + transform.m02;
		float y2 = transform.m11 * height + transform.m12;
		float x3 = transform.m00 * width + transform.m01 * height + transform.m02;
		float y3 = transform.m10 * width + transform.m11 * height + transform.m12;
		float x4 = transform.m00 * width + transform.m02;
		float y4 = transform.m10 * width + transform.m12;

		float u = region.u;
		float v = region.v2;
		float u2 = region.u2;
		float v2 = region.v;

		float color = this.colorPacked;
		int idx = this.idx;
		vertices[idx] = x1;
		vertices[idx + 1] = y1;
		vertices[idx + 2] = color;
		vertices[idx + 3] = u;
		vertices[idx + 4] = v;

		vertices[idx + 5] = x2;
		vertices[idx + 6] = y2;
		vertices[idx + 7] = color;
		vertices[idx + 8] = u;
		vertices[idx + 9] = v2;

		vertices[idx + 10] = x3;
		vertices[idx + 11] = y3;
		vertices[idx + 12] = color;
		vertices[idx + 13] = u2;
		vertices[idx + 14] = v2;

		vertices[idx + 15] = x4;
		vertices[idx + 16] = y4;
		vertices[idx + 17] = color;
		vertices[idx + 18] = u2;
		vertices[idx + 19] = v;
		this.idx = idx + 20;
	}

	@Override
	public void flush () {
		if (idx == 0) return;

		renderCalls++;
		totalRenderCalls++;
		int spritesInBatch = idx / 52;
		if (spritesInBatch > maxSpritesInBatch) maxSpritesInBatch = spritesInBatch;
		int count = spritesInBatch * 6;

		int i = 0;

		if (uTex[0] != null) {
			uTex[0].bind(1);
		}
		i++;
		if (uTex[1] != null) {
			uTex[1].bind(2);
		}
		i++;
		if (uTex[2] != null) {
			uTex[2].bind(3);
		}
		i++;
		if (uTex[3] != null) {
			uTex[3].bind(4);
		}
		i++;
		if (uTex[4] != null) {
			uTex[4].bind(5);
		}
		i++;
		if (uTex[5] != null) {
			uTex[5].bind(6);
		}
		i++;
		if (uTex[6] != null) {
			uTex[6].bind(7);
		}
		i++;
		if (uTex[7] != null) {
			uTex[7].bind(8);
		}
		i++;
		if (uTex[8] != null) {
			uTex[8].bind(9);
		}
		i++;
		if (uTex[9] != null) {
			uTex[9].bind(10);
		}
		i++;
		if (uTex[10] != null) {
			uTex[10].bind(11);
		}
		i++;
		if (uTex[11] != null) {
			uTex[11].bind(12);
		}
		i++;
		if (uTex[12] != null) {
			uTex[12].bind(13);
		}
		i++;
		if (uTex[13] != null) {
			uTex[13].bind(14);
		}
		i++;
		if (uTex[14] != null) {
			uTex[14].bind(15);
		}
		i++;

		if (lastTexture != null) {
			lastTexture.bind(0);
		}

		// Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
		Mesh mesh = this.mesh;
		mesh.setVertices(vertices, 0, idx);
		mesh.getIndicesBuffer().position(0);
		mesh.getIndicesBuffer().limit(count);

		if (blendingDisabled) {
			Gdx.gl.glDisable(GL20.GL_BLEND);
		} else {
			Gdx.gl.glEnable(GL20.GL_BLEND);
			if (blendSrcFunc != -1) Gdx.gl.glBlendFuncSeparate(blendSrcFunc, blendDstFunc, blendSrcFuncAlpha, blendDstFuncAlpha);
		}

		mesh.render(customShader != null ? customShader : shader, GL20.GL_TRIANGLES, 0, count);

		idx = 0;
	}

	@Override
	public void disableBlending () {
		if (blendingDisabled) return;
		flush();
		blendingDisabled = true;
	}

	@Override
	public void enableBlending () {
		if (!blendingDisabled) return;
		flush();
		blendingDisabled = false;
	}

	@Override
	public void setBlendFunction (int srcFunc, int dstFunc) {
		setBlendFunctionSeparate(srcFunc, dstFunc, srcFunc, dstFunc);
	}

	@Override
	public void setBlendFunctionSeparate (int srcFuncColor, int dstFuncColor, int srcFuncAlpha, int dstFuncAlpha) {
		if (blendSrcFunc == srcFuncColor && blendDstFunc == dstFuncColor && blendSrcFuncAlpha == srcFuncAlpha
			&& blendDstFuncAlpha == dstFuncAlpha) return;
		flush();
		blendSrcFunc = srcFuncColor;
		blendDstFunc = dstFuncColor;
		blendSrcFuncAlpha = srcFuncAlpha;
		blendDstFuncAlpha = dstFuncAlpha;
	}

	@Override
	public int getBlendSrcFunc () {
		return blendSrcFunc;
	}

	@Override
	public int getBlendDstFunc () {
		return blendDstFunc;
	}

	@Override
	public int getBlendSrcFuncAlpha () {
		return blendSrcFuncAlpha;
	}

	@Override
	public int getBlendDstFuncAlpha () {
		return blendDstFuncAlpha;
	}

	@Override
	public void dispose () {
		mesh.dispose();
		if (ownsShader && shader != null) shader.dispose();
	}

	@Override
	public Matrix4 getProjectionMatrix () {
		return projectionMatrix;
	}

	@Override
	public Matrix4 getTransformMatrix () {
		return transformMatrix;
	}

	@Override
	public void setProjectionMatrix (Matrix4 projection) {
		if (drawing) flush();
		projectionMatrix.set(projection);
		if (drawing) setupMatrices();
	}

	@Override
	public void setTransformMatrix (Matrix4 transform) {
		if (drawing) flush();
		transformMatrix.set(transform);
		if (drawing) setupMatrices();
	}

	private void setupMatrices () {
		combinedMatrix.set(projectionMatrix).mul(transformMatrix);
		// if (customShader != null) {
		customShader.setUniformMatrix("u_projTrans", combinedMatrix);
		customShader.setUniformi("u_texture", 0);
		customShader.setUniformi("u_texture2", 1);
		customShader.setUniformi("u_texture3", 2);
		customShader.setUniformi("u_texture4", 3);
		customShader.setUniformi("u_texture5", 4);
		customShader.setUniformi("u_texture6", 5);
		customShader.setUniformi("u_texture7", 6);
		customShader.setUniformi("u_texture8", 7);
		customShader.setUniformi("u_texture9", 8);
		customShader.setUniformi("u_texture10", 9);
		customShader.setUniformi("u_texture11", 10);
		customShader.setUniformi("u_texture12", 11);
		customShader.setUniformi("u_texture13", 12);
		customShader.setUniformi("u_texture14", 13);
		customShader.setUniformi("u_texture15", 14);
		customShader.setUniformi("u_texture16", 15);
		shader.setUniformMatrix("u_projTrans", combinedMatrix);
		shader.setUniformi("u_texture", 0);
		shader.setUniformi("u_texture2", 1);
		shader.setUniformi("u_texture3", 2);
		shader.setUniformi("u_texture4", 3);
		shader.setUniformi("u_texture5", 4);
		shader.setUniformi("u_texture6", 5);
		shader.setUniformi("u_texture7", 6);
		shader.setUniformi("u_texture8", 7);
		shader.setUniformi("u_texture9", 8);
		shader.setUniformi("u_texture10", 9);
		shader.setUniformi("u_texture11", 10);
		shader.setUniformi("u_texture12", 11);
		shader.setUniformi("u_texture13", 12);
		shader.setUniformi("u_texture14", 13);
		shader.setUniformi("u_texture15", 14);
		shader.setUniformi("u_texture16", 15);

		// }
	}

	protected void switchTexture (Texture texture) {
		flush();
		lastTexture = texture;
		invTexWidth = 1.0f / texture.getWidth();
		invTexHeight = 1.0f / texture.getHeight();
	}

	@Override
	public void setShader (ShaderProgram shader) {
		if (drawing) {
			flush();
			if (customShader != null)
				customShader.end();
			else
				this.shader.end();
		}
		customShader = shader;
		if (drawing) {
			if (customShader != null)
				customShader.begin();
			else
				this.shader.begin();
			setupMatrices();
		}
	}

	@Override
	public ShaderProgram getShader () {
		if (customShader == null) {
			return shader;
		}
		return customShader;
	}

	@Override
	public boolean isBlendingEnabled () {
		return !blendingDisabled;
	}

	public boolean isDrawing () {
		return drawing;
	}
}
