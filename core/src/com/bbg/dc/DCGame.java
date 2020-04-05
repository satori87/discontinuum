// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.bbg.dc.scenes.ExportScene;
import com.bbg.dc.scenes.GraphicsScene;
import com.bbg.dc.scenes.ListScene;
import com.bbg.dc.scenes.MonsterScene;
import com.bbg.dc.scenes.CreatureScene;
import com.bbg.dc.scenes.DollScene;
import com.bbg.dc.scenes.GameScene;
import com.bbg.dc.scenes.MapOptions;
import com.bbg.dc.scenes.MapScene;
import com.bbg.dc.scenes.LoadScene;
import com.bbg.dc.scenes.MainMenu;
import com.bbg.dc.iface.Scene;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.esotericsoftware.kryo.Kryo;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.ApplicationAdapter;

public class DCGame extends ApplicationAdapter
{
    public AssetManager manager;
    public GameInput input;
    public Kryo kryo;
    public OrthographicCamera cam;
    public ShapeRenderer shapeRenderer;
    public SpriteBatch batcher;
    public SpriteBatch obatcher;
    public SpriteBatch curbatch;
    Color oldColor;
    public Color forceCol;
    public boolean forcingColor;
    public int width;
    public int height;
    public long tick;
    private long lastTick;
    public float deltaT;
    public Scene scene;
    public MainMenu mainMenu;
    public LoadScene loadScene;
    public MapScene mapScene;
    public MapOptions mapOptions;
    public GameScene gameScene;
    public DollScene dollScene;
    public CreatureScene creatureScene;
    public MonsterScene monsterScene;
    public ListScene listScene;
    public GraphicsScene graphicsScene;
    public ExportScene exportScene;
    private int frames;
    private long fpsTimer;
    public int fps;
    public int cursor;
    public Options options;
    public static boolean optionSave;
    FrameBuffer[] fbo;
    public float[] shaderData;
    public float[] shaderFloats;
    public TextureRegion aregion;
    ShaderProgram shader;
    
    static {
        DCGame.optionSave = false;
    }
    
    public DCGame() {
        this.manager = new AssetManager();
        this.oldColor = Color.WHITE;
        this.forceCol = Color.WHITE;
        this.forcingColor = false;
        this.width = 800;
        this.height = 600;
        this.tick = 0L;
        this.lastTick = 0L;
        this.deltaT = 0.0f;
        this.frames = 0;
        this.fpsTimer = 0L;
        this.cursor = 0;
        this.shaderData = new float[] { 0.0f, 0.0f };
        this.shaderFloats = new float[8];
        this.shader = null;
    }
    
    @Override
    public void create() {
        super.create();
        this.setupKryo();
        this.loadOptions();
        AssetLoader.loadFirst(this);
        this.setupScreen();
        this.createScenes();
        (this.scene = this.loadScene).start();
    }
    
    public void setBatcher(final SpriteBatch batch) {
        this.batcher = batch;
    }
    
    public void loadOptions() {
        this.options = new Options();
    }
    
    private void createScenes() {
        this.input = new GameInput(this);
        Gdx.input.setInputProcessor(this.input);
        this.loadScene = new LoadScene(this);
        this.mainMenu = new MainMenu(this);
        this.mapOptions = new MapOptions(this);
        this.dollScene = new DollScene(this);
        this.mapScene = new MapScene(this);
        this.creatureScene = new CreatureScene(this);
        this.monsterScene = new MonsterScene(this);
        this.listScene = new ListScene(this);
        this.graphicsScene = new GraphicsScene(this);
        this.gameScene = new GameScene(this);
        this.exportScene = new ExportScene(this);
    }
    
    private void setupKryo() {
        (this.kryo = new Kryo()).register((Class)MapData.class);
        this.kryo.register((Class)Tile.class);
        this.kryo.register((Class)Att.class);
    }
    
    public void forceColor(final Color c) {
        this.forcingColor = true;
        this.forceCol = c;
    }
    
    public void endForce() {
        this.forcingColor = false;
    }
    
    public void setColor(final Color c) {
        this.oldColor = this.batcher.getColor();
        this.batcher.setColor(c);
    }
    
    public void setColor(final int[] c) {
        this.oldColor = this.batcher.getColor();
        final Color col = new Color(c[0] / 255.0f, c[1] / 255.0f, c[2] / 255.0f, c[3] / 255.0f);
        this.batcher.setColor(col);
    }
    
    public void resetColor() {
        this.batcher.setColor(this.oldColor);
    }
    
    private ShaderProgram loadShader(final String vertStr, final String fragStr) {
        final ShaderProgram shader = new ShaderProgram(Gdx.files.local(vertStr).readString(), Gdx.files.local(fragStr).readString());
        if (!shader.isCompiled()) {
            throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
        }
        return shader;
    }
    
    private void setupScreen() {
        Gdx.gl20.glEnable(3024);
        if (this.options.fullscreen) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }
        else {
            Gdx.graphics.setWindowedMode(this.options.windowX, this.options.windowY);
        }
        this.width = Gdx.graphics.getWidth();
        this.height = Gdx.graphics.getHeight();
        (this.cam = new OrthographicCamera()).setToOrtho(true, (float)this.width, (float)this.height);
        this.batcher = new SpriteBatch();
        ShaderProgram.pedantic = false;
        System.out.println("Compiling shader");
        this.shader = this.loadShader("vertex.glsl", "fragment.glsl");
        this.batcher.setShader(this.shader);
        this.batcher.setProjectionMatrix(this.cam.combined);
        this.batcher.maxSpritesInBatch = 1000;
        (this.shapeRenderer = new ShapeRenderer()).setProjectionMatrix(this.cam.combined);
        this.fbo = new FrameBuffer[8];
        for (int i = 0; i < 8; ++i) {
            this.fbo[i] = new FrameBuffer(Pixmap.Format.RGBA8888, 2048, 2048, true);
        }
        System.out.println("Screen setup");
    }
    
    public void clip(final int x, final int y, final int width, final int height) {
        final Rectangle scissors = new Rectangle();
        final Rectangle clipBounds = new Rectangle((float)x, (float)y, (float)width, (float)height);
        ScissorStack.calculateScissors(this.cam, this.batcher.getTransformMatrix(), clipBounds, scissors);
        ScissorStack.pushScissors(scissors);
    }
    
    public void endClip() {
        ScissorStack.popScissors();
    }
    
    private void frameTimer() {
        ++this.frames;
        if (this.tick > this.fpsTimer) {
            this.fpsTimer = this.tick + 1000L;
            this.fps = this.frames;
            this.frames = 0;
        }
    }
    
    @Override
    public void render() {
        this.tick = System.currentTimeMillis();
        this.deltaT = (this.tick - this.lastTick) / 1000.0f;
        this.lastTick = this.tick;
        this.frameTimer();
        this.scene.update(this.tick);
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        Gdx.gl.glClear(17664);
        this.batcher.enableBlending();
        this.batcher.begin();
        this.scene.render();
        this.drawCursor();
        this.batcher.end();
    }
    
    void drawCursor() {
        this.drawAbs(AssetLoader.cursorTex[this.cursor], this.input.getMouseX(), this.input.getMouseY(), 0, 0, 32, 32);
    }
    
    public void changeScene(final Scene newScene) {
        if (this.scene != newScene) {
            this.scene.switchFrom();
            (this.scene = newScene).switchTo();
        }
    }
    
    public void moveCameraTo(final int x, final int y) {
        this.cam.position.x = (float)x;
        this.cam.position.y = (float)y;
        this.cam.update();
        this.batcher.setProjectionMatrix(this.cam.combined);
        this.shapeRenderer.setProjectionMatrix(this.cam.combined);
    }
    
    public int getCamX() {
        return (int)this.cam.position.x;
    }
    
    public int getCamY() {
        return (int)this.cam.position.y;
    }
    
    public void drawRegion(final TextureRegion region, final int X, final int Y, final boolean centered, final float rotation, final float scale) {
        if (region != null) {
            final int width = region.getRegionWidth();
            final int height = region.getRegionHeight();
            float eX = (float)X;
            float eY = (float)Y;
            if (centered) {
                eX -= width / 2;
                eY -= height / 2;
            }
            if (this.forcingColor) {
                this.setColor(this.forceCol);
            }
            if (!region.isFlipY()) {
                region.flip(false, true);
            }
            final TextureRegion tr = region;
            final Texture tex = region.getTexture();
            int i = -1;
            i = this.getTextureNumber(tex);
            if (i >= 0) {
                this.shaderData[1] = (float)(i + 1);
            }
            else {
                this.shaderData[1] = 0.0f;
            }
            if (centered) {
                this.batcher.draw(tr, eX, eY, (float)(width / 2), (float)(height / 2), (float)width, (float)height, scale, scale, rotation, this.shaderData, this.aregion);
            }
            else {
                this.batcher.draw(tr, eX, eY, 0.0f, 0.0f, (float)width, (float)height, scale, scale, rotation, this.shaderData, this.aregion);
            }
            this.shaderData[1] = 0.0f;
            if (this.forcingColor) {
                this.resetColor();
            }
        }
    }
    
    public int getTextureNumber(final Texture t) {
        for (int i = 0; i < 15; ++i) {
            if (t == this.batcher.uTex[i]) {
                return i;
            }
        }
        return -1;
    }
    
    public void draw(final Texture tex, final int x, final int y, final int srcX, final int srcY, final int width, final int height) {
        this.draw(tex, x, y, srcX, srcY, width, height, false, true);
    }
    
    public void drawAbs(Texture tex, final int x, final int y, final int srcX, final int srcY, final int width, final int height) {
        if (tex != null || this.shaderData[1] > 0.0f) {
            if (this.shaderData[1] > 0.0f) {
                tex = this.batcher.lastTexture;
            }
            this.batcher.draw(tex, (float)(x + this.getCamX() - this.width / 2), (float)(y + this.getCamY() - this.height / 2), (float)width, (float)height, srcX, srcY, width, height, false, true, this.shaderData, this.aregion);
        }
    }
    
    public void draw(Texture tex, final int x, final int y, final int srcX, final int srcY, final int width, final int height, final boolean flipX, final boolean flipY) {
        if (tex != null || this.shaderData[1] > 0.0f) {
            if (this.forcingColor) {
                this.setColor(this.forceCol);
            }
            if (this.shaderData[1] > 0.0f) {
                tex = this.batcher.lastTexture;
            }
            this.batcher.draw(tex, (float)x, (float)y, (float)width, (float)height, srcX, srcY, width, height, flipX, flipY, this.shaderData, this.aregion);
            if (this.forcingColor) {
                this.resetColor();
            }
        }
    }
    
    public void draw(final int uTex, final int x, final int y, final int srcX, final int srcY, final int width, final int height, final boolean flipX, final boolean flipY) {
        this.draw(this.batcher.lastTexture, x, y, srcX, srcY, width, height, flipX, flipY);
    }
    
    public void beginMask(final int x, final int y, final int w, final int h) {
        this.batcher.flush();
        Gdx.gl.glColorMask(true, true, true, true);
        this.batcher.setBlendFunction(772, 773);
        Gdx.gl.glEnable(3089);
        Gdx.gl.glScissor(x, y, w, h);
    }
    
    public void endMask() {
        this.batcher.flush();
        Gdx.gl.glDisable(3089);
        this.batcher.setBlendFunction(770, 771);
    }
    
    public void resetBatcher() {
        this.batcher.end();
        this.batcher.begin();
        this.batcher.flush();
        Gdx.gl.glClear(1280);
        this.batcher.setBlendFunction(770, 771);
    }
    
    public void drawFont(final int type, final int x, final int y, final String s, final boolean centered, final float scale, final Color col) {
        if (s == null) {
            return;
        }
        float curX = (float)x;
        final float padding = 0.0f * scale;
        final float spacing = 1.0f * scale;
        float total = 0.0f;
        float oX;
        float oY;
        if (centered) {
            total = AssetLoader.getStringWidth(s, scale, padding, spacing);
            oX = (float)Math.round(-total / 2.0f);
            oY = (float)Math.round(scale * -16.0f / 2.0f);
        }
        else {
            oX = 0.0f;
            oY = 0.0f;
        }
        this.setColor(col);
        char[] charArray;
        for (int length = (charArray = s.toCharArray()).length, i = 0; i < length; ++i) {
            final int ascii;
            final char c = (char)(ascii = charArray[i]);
            if (AssetLoader.fontWidth[ascii] > 0) {
                this.drawRegion(AssetLoader.font[type][ascii], Math.round(curX + padding + oX), Math.round(y + oY), false, 0.0f, scale);
                curX += AssetLoader.fontWidth[ascii] * scale + padding * 2.0f + spacing;
            }
        }
        this.setColor(Color.WHITE);
    }
    
    public void drawFont(final int type, final int X, final int Y, final String s, final boolean centered, final float scale) {
        this.drawFont(type, X, Y, s, centered, scale, Color.WHITE);
    }
    
    public void drawFontAbs(final int type, final int X, final int Y, final String s, final boolean centered, final float scale, final Color c) {
        this.drawFont(type, X + this.getCamX() - this.width / 2, Y + this.getCamY() - this.height / 2, s, centered, scale, c);
    }
    
    public void drawFontAbs(final int type, final int X, final int Y, final String s, final boolean centered, final float scale) {
        this.drawFontAbs(type, X, Y, s, centered, scale, Color.WHITE);
    }
    
    public void setCursor(final int c) {
        this.cursor = c;
    }
    
    @Override
    public void dispose() {
        this.shapeRenderer.dispose();
        this.batcher.dispose();
    }
}
