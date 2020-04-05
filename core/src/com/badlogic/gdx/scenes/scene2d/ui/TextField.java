// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;

public class TextField extends Widget implements Disableable
{
    private static final char BACKSPACE = '\b';
    protected static final char ENTER_DESKTOP = '\r';
    protected static final char ENTER_ANDROID = '\n';
    private static final char TAB = '\t';
    private static final char DELETE = '\u007f';
    private static final char BULLET = '\u0095';
    private static final Vector2 tmp1;
    private static final Vector2 tmp2;
    private static final Vector2 tmp3;
    public static float keyRepeatInitialTime;
    public static float keyRepeatTime;
    protected String text;
    protected int cursor;
    protected int selectionStart;
    protected boolean hasSelection;
    protected boolean writeEnters;
    protected final GlyphLayout layout;
    protected final FloatArray glyphPositions;
    TextFieldStyle style;
    private String messageText;
    protected CharSequence displayText;
    Clipboard clipboard;
    InputListener inputListener;
    TextFieldListener listener;
    TextFieldFilter filter;
    OnscreenKeyboard keyboard;
    boolean focusTraversal;
    boolean onlyFontChars;
    boolean disabled;
    private int textHAlign;
    private float selectionX;
    private float selectionWidth;
    String undoText;
    long lastChangeTime;
    boolean passwordMode;
    private StringBuilder passwordBuffer;
    private char passwordCharacter;
    protected float fontOffset;
    protected float textHeight;
    protected float textOffset;
    float renderOffset;
    private int visibleTextStart;
    private int visibleTextEnd;
    private int maxLength;
    boolean focused;
    boolean cursorOn;
    float blinkTime;
    final Timer.Task blinkTask;
    final KeyRepeatTask keyRepeatTask;
    boolean programmaticChangeEvents;
    
    static {
        tmp1 = new Vector2();
        tmp2 = new Vector2();
        tmp3 = new Vector2();
        TextField.keyRepeatInitialTime = 0.4f;
        TextField.keyRepeatTime = 0.1f;
    }
    
    public TextField(final String text, final Skin skin) {
        this(text, skin.get(TextFieldStyle.class));
    }
    
    public TextField(final String text, final Skin skin, final String styleName) {
        this(text, skin.get(styleName, TextFieldStyle.class));
    }
    
    public TextField(final String text, final TextFieldStyle style) {
        this.layout = new GlyphLayout();
        this.glyphPositions = new FloatArray();
        this.keyboard = new DefaultOnscreenKeyboard();
        this.focusTraversal = true;
        this.onlyFontChars = true;
        this.textHAlign = 8;
        this.undoText = "";
        this.passwordCharacter = '\u0095';
        this.maxLength = 0;
        this.blinkTime = 0.32f;
        this.blinkTask = new Timer.Task() {
            @Override
            public void run() {
                TextField.this.cursorOn = !TextField.this.cursorOn;
                Gdx.graphics.requestRendering();
            }
        };
        this.keyRepeatTask = new KeyRepeatTask();
        this.setStyle(style);
        this.clipboard = Gdx.app.getClipboard();
        this.initialize();
        this.setText(text);
        this.setSize(this.getPrefWidth(), this.getPrefHeight());
    }
    
    protected void initialize() {
        this.addListener(this.inputListener = this.createInputListener());
    }
    
    protected InputListener createInputListener() {
        return new TextFieldClickListener();
    }
    
    protected int letterUnderCursor(float x) {
        x -= this.textOffset + this.fontOffset - this.style.font.getData().cursorX - this.glyphPositions.get(this.visibleTextStart);
        final Drawable background = this.getBackgroundDrawable();
        if (background != null) {
            x -= this.style.background.getLeftWidth();
        }
        final int n = this.glyphPositions.size;
        final float[] glyphPositions = this.glyphPositions.items;
        int i = 1;
        while (i < n) {
            if (glyphPositions[i] > x) {
                if (glyphPositions[i] - x <= x - glyphPositions[i - 1]) {
                    return i;
                }
                return i - 1;
            }
            else {
                ++i;
            }
        }
        return n - 1;
    }
    
    protected boolean isWordCharacter(final char c) {
        return Character.isLetterOrDigit(c);
    }
    
    protected int[] wordUnderCursor(final int at) {
        final String text = this.text;
        final int start = at;
        int right = text.length();
        int left = 0;
        int index = start;
        if (at >= text.length()) {
            left = text.length();
            right = 0;
        }
        else {
            while (index < right) {
                if (!this.isWordCharacter(text.charAt(index))) {
                    right = index;
                    break;
                }
                ++index;
            }
            for (index = start - 1; index > -1; --index) {
                if (!this.isWordCharacter(text.charAt(index))) {
                    left = index + 1;
                    break;
                }
            }
        }
        return new int[] { left, right };
    }
    
    int[] wordUnderCursor(final float x) {
        return this.wordUnderCursor(this.letterUnderCursor(x));
    }
    
    boolean withinMaxLength(final int size) {
        return this.maxLength <= 0 || size < this.maxLength;
    }
    
    public void setMaxLength(final int maxLength) {
        this.maxLength = maxLength;
    }
    
    public int getMaxLength() {
        return this.maxLength;
    }
    
    public void setOnlyFontChars(final boolean onlyFontChars) {
        this.onlyFontChars = onlyFontChars;
    }
    
    public void setStyle(final TextFieldStyle style) {
        if (style == null) {
            throw new IllegalArgumentException("style cannot be null.");
        }
        this.style = style;
        this.textHeight = style.font.getCapHeight() - style.font.getDescent() * 2.0f;
        this.invalidateHierarchy();
    }
    
    public TextFieldStyle getStyle() {
        return this.style;
    }
    
    protected void calculateOffsets() {
        float visibleWidth = this.getWidth();
        final Drawable background = this.getBackgroundDrawable();
        if (background != null) {
            visibleWidth -= background.getLeftWidth() + background.getRightWidth();
        }
        final int glyphCount = this.glyphPositions.size;
        final float[] glyphPositions = this.glyphPositions.items;
        final float distance = glyphPositions[Math.max(0, this.cursor - 1)] + this.renderOffset;
        if (distance <= 0.0f) {
            this.renderOffset -= distance;
        }
        else {
            final int index = Math.min(glyphCount - 1, this.cursor + 1);
            final float minX = glyphPositions[index] - visibleWidth;
            if (-this.renderOffset < minX) {
                this.renderOffset = -minX;
            }
        }
        float maxOffset = 0.0f;
        final float width = glyphPositions[glyphCount - 1];
        for (int i = glyphCount - 2; i >= 0; --i) {
            final float x = glyphPositions[i];
            if (width - x > visibleWidth) {
                break;
            }
            maxOffset = x;
        }
        if (-this.renderOffset > maxOffset) {
            this.renderOffset = -maxOffset;
        }
        this.visibleTextStart = 0;
        float startX = 0.0f;
        for (int j = 0; j < glyphCount; ++j) {
            if (glyphPositions[j] >= -this.renderOffset) {
                this.visibleTextStart = Math.max(0, j);
                startX = glyphPositions[j];
                break;
            }
        }
        final int length = Math.min(this.displayText.length(), glyphPositions.length - 1);
        this.visibleTextEnd = Math.min(length, this.cursor + 1);
        while (this.visibleTextEnd <= length && glyphPositions[this.visibleTextEnd] <= startX + visibleWidth) {
            ++this.visibleTextEnd;
        }
        this.visibleTextEnd = Math.max(0, this.visibleTextEnd - 1);
        if ((this.textHAlign & 0x8) == 0x0) {
            this.textOffset = visibleWidth - (glyphPositions[this.visibleTextEnd] - startX);
            if ((this.textHAlign & 0x1) != 0x0) {
                this.textOffset = (float)Math.round(this.textOffset * 0.5f);
            }
        }
        else {
            this.textOffset = startX + this.renderOffset;
        }
        if (this.hasSelection) {
            final int minIndex = Math.min(this.cursor, this.selectionStart);
            final int maxIndex = Math.max(this.cursor, this.selectionStart);
            final float minX2 = Math.max(glyphPositions[minIndex] - glyphPositions[this.visibleTextStart], -this.textOffset);
            final float maxX = Math.min(glyphPositions[maxIndex] - glyphPositions[this.visibleTextStart], visibleWidth - this.textOffset);
            this.selectionX = minX2;
            this.selectionWidth = maxX - minX2 - this.style.font.getData().cursorX;
        }
    }
    
    private Drawable getBackgroundDrawable() {
        final Stage stage = this.getStage();
        final boolean focused = stage != null && stage.getKeyboardFocus() == this;
        return (this.disabled && this.style.disabledBackground != null) ? this.style.disabledBackground : ((focused && this.style.focusedBackground != null) ? this.style.focusedBackground : this.style.background);
    }
    
    @Override
    public void draw(final Batch batch, final float parentAlpha) {
        final boolean focused = this.getStage() != null && this.getStage().getKeyboardFocus() == this;
        if (focused != this.focused) {
            this.focused = focused;
            this.blinkTask.cancel();
            this.cursorOn = focused;
            if (focused) {
                Timer.schedule(this.blinkTask, this.blinkTime, this.blinkTime);
            }
            else {
                this.keyRepeatTask.cancel();
            }
        }
        final BitmapFont font = this.style.font;
        final Color fontColor = (this.disabled && this.style.disabledFontColor != null) ? this.style.disabledFontColor : ((focused && this.style.focusedFontColor != null) ? this.style.focusedFontColor : this.style.fontColor);
        final Drawable selection = this.style.selection;
        final Drawable cursorPatch = this.style.cursor;
        final Drawable background = this.getBackgroundDrawable();
        final Color color = this.getColor();
        final float x = this.getX();
        final float y = this.getY();
        final float width = this.getWidth();
        final float height = this.getHeight();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        float bgLeftWidth = 0.0f;
        float bgRightWidth = 0.0f;
        if (background != null) {
            background.draw(batch, x, y, width, height);
            bgLeftWidth = background.getLeftWidth();
            bgRightWidth = background.getRightWidth();
        }
        final float textY = this.getTextY(font, background);
        this.calculateOffsets();
        if (focused && this.hasSelection && selection != null) {
            this.drawSelection(selection, batch, font, x + bgLeftWidth, y + textY);
        }
        final float yOffset = font.isFlipped() ? (-this.textHeight) : 0.0f;
        if (this.displayText.length() == 0) {
            if (!focused && this.messageText != null) {
                if (this.style.messageFontColor != null) {
                    font.setColor(this.style.messageFontColor.r, this.style.messageFontColor.g, this.style.messageFontColor.b, this.style.messageFontColor.a * color.a * parentAlpha);
                }
                else {
                    font.setColor(0.7f, 0.7f, 0.7f, color.a * parentAlpha);
                }
                final BitmapFont messageFont = (this.style.messageFont != null) ? this.style.messageFont : font;
                messageFont.draw(batch, this.messageText, x + bgLeftWidth, y + textY + yOffset, 0, this.messageText.length(), width - bgLeftWidth - bgRightWidth, this.textHAlign, false, "...");
            }
        }
        else {
            font.setColor(fontColor.r, fontColor.g, fontColor.b, fontColor.a * color.a * parentAlpha);
            this.drawText(batch, font, x + bgLeftWidth, y + textY + yOffset);
        }
        if (!this.disabled && this.cursorOn && cursorPatch != null) {
            this.drawCursor(cursorPatch, batch, font, x + bgLeftWidth, y + textY);
        }
    }
    
    protected float getTextY(final BitmapFont font, final Drawable background) {
        final float height = this.getHeight();
        float textY = this.textHeight / 2.0f + font.getDescent();
        if (background != null) {
            final float bottom = background.getBottomHeight();
            textY = textY + (height - background.getTopHeight() - bottom) / 2.0f + bottom;
        }
        else {
            textY += height / 2.0f;
        }
        if (font.usesIntegerPositions()) {
            textY = (float)(int)textY;
        }
        return textY;
    }
    
    protected void drawSelection(final Drawable selection, final Batch batch, final BitmapFont font, final float x, final float y) {
        selection.draw(batch, x + this.textOffset + this.selectionX + this.fontOffset, y - this.textHeight - font.getDescent(), this.selectionWidth, this.textHeight);
    }
    
    protected void drawText(final Batch batch, final BitmapFont font, final float x, final float y) {
        font.draw(batch, this.displayText, x + this.textOffset, y, this.visibleTextStart, this.visibleTextEnd, 0.0f, 8, false);
    }
    
    protected void drawCursor(final Drawable cursorPatch, final Batch batch, final BitmapFont font, final float x, final float y) {
        cursorPatch.draw(batch, x + this.textOffset + this.glyphPositions.get(this.cursor) - this.glyphPositions.get(this.visibleTextStart) + this.fontOffset + font.getData().cursorX, y - this.textHeight - font.getDescent(), cursorPatch.getMinWidth(), this.textHeight);
    }
    
    void updateDisplayText() {
        final BitmapFont font = this.style.font;
        final BitmapFont.BitmapFontData data = font.getData();
        final String text = this.text;
        final int textLength = text.length();
        final StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < textLength; ++i) {
            final char c = text.charAt(i);
            buffer.append(data.hasGlyph(c) ? c : ' ');
        }
        final String newDisplayText = buffer.toString();
        if (this.passwordMode && data.hasGlyph(this.passwordCharacter)) {
            if (this.passwordBuffer == null) {
                this.passwordBuffer = new StringBuilder(newDisplayText.length());
            }
            if (this.passwordBuffer.length() > textLength) {
                this.passwordBuffer.setLength(textLength);
            }
            else {
                for (int j = this.passwordBuffer.length(); j < textLength; ++j) {
                    this.passwordBuffer.append(this.passwordCharacter);
                }
            }
            this.displayText = this.passwordBuffer;
        }
        else {
            this.displayText = newDisplayText;
        }
        this.layout.setText(font, this.displayText.toString().replace('\r', ' ').replace('\n', ' '));
        this.glyphPositions.clear();
        float x = 0.0f;
        if (this.layout.runs.size > 0) {
            final GlyphLayout.GlyphRun run = this.layout.runs.first();
            final FloatArray xAdvances = run.xAdvances;
            this.fontOffset = xAdvances.first();
            for (int k = 1, n = xAdvances.size; k < n; ++k) {
                this.glyphPositions.add(x);
                x += xAdvances.get(k);
            }
        }
        else {
            this.fontOffset = 0.0f;
        }
        this.glyphPositions.add(x);
        if (this.selectionStart > newDisplayText.length()) {
            this.selectionStart = textLength;
        }
    }
    
    public void copy() {
        if (this.hasSelection && !this.passwordMode) {
            this.clipboard.setContents(this.text.substring(Math.min(this.cursor, this.selectionStart), Math.max(this.cursor, this.selectionStart)));
        }
    }
    
    public void cut() {
        this.cut(this.programmaticChangeEvents);
    }
    
    void cut(final boolean fireChangeEvent) {
        if (this.hasSelection && !this.passwordMode) {
            this.copy();
            this.cursor = this.delete(fireChangeEvent);
            this.updateDisplayText();
        }
    }
    
    void paste(String content, final boolean fireChangeEvent) {
        if (content == null) {
            return;
        }
        final StringBuilder buffer = new StringBuilder();
        int textLength = this.text.length();
        if (this.hasSelection) {
            textLength -= Math.abs(this.cursor - this.selectionStart);
        }
        final BitmapFont.BitmapFontData data = this.style.font.getData();
        for (int i = 0, n = content.length(); i < n && this.withinMaxLength(textLength + buffer.length()); ++i) {
            final char c = content.charAt(i);
            if (!this.writeEnters || (c != '\n' && c != '\r')) {
                if (c == '\r') {
                    continue;
                }
                if (c == '\n') {
                    continue;
                }
                if (this.onlyFontChars && !data.hasGlyph(c)) {
                    continue;
                }
                if (this.filter != null && !this.filter.acceptChar(this, c)) {
                    continue;
                }
            }
            buffer.append(c);
        }
        content = buffer.toString();
        if (this.hasSelection) {
            this.cursor = this.delete(fireChangeEvent);
        }
        if (fireChangeEvent) {
            this.changeText(this.text, this.insert(this.cursor, content, this.text));
        }
        else {
            this.text = this.insert(this.cursor, content, this.text);
        }
        this.updateDisplayText();
        this.cursor += content.length();
    }
    
    String insert(final int position, final CharSequence text, final String to) {
        if (to.length() == 0) {
            return text.toString();
        }
        return String.valueOf(to.substring(0, position)) + (Object)text + to.substring(position, to.length());
    }
    
    int delete(final boolean fireChangeEvent) {
        final int from = this.selectionStart;
        final int to = this.cursor;
        final int minIndex = Math.min(from, to);
        final int maxIndex = Math.max(from, to);
        final String newText = String.valueOf((minIndex > 0) ? this.text.substring(0, minIndex) : "") + ((maxIndex < this.text.length()) ? this.text.substring(maxIndex, this.text.length()) : "");
        if (fireChangeEvent) {
            this.changeText(this.text, newText);
        }
        else {
            this.text = newText;
        }
        this.clearSelection();
        return minIndex;
    }
    
    public void next(final boolean up) {
        final Stage stage = this.getStage();
        if (stage == null) {
            return;
        }
        TextField current = this;
        final Vector2 currentCoords = current.getParent().localToStageCoordinates(TextField.tmp2.set(current.getX(), current.getY()));
        final Vector2 bestCoords = TextField.tmp1;
        while (true) {
            TextField textField = current.findNextTextField(stage.getActors(), null, bestCoords, currentCoords, up);
            if (textField == null) {
                if (up) {
                    currentCoords.set(Float.MIN_VALUE, Float.MIN_VALUE);
                }
                else {
                    currentCoords.set(Float.MAX_VALUE, Float.MAX_VALUE);
                }
                textField = current.findNextTextField(stage.getActors(), null, bestCoords, currentCoords, up);
            }
            if (textField == null) {
                Gdx.input.setOnscreenKeyboardVisible(false);
                break;
            }
            if (stage.setKeyboardFocus(textField)) {
                textField.selectAll();
                break;
            }
            current = textField;
            currentCoords.set(bestCoords);
        }
    }
    
    private TextField findNextTextField(final Array<Actor> actors, TextField best, final Vector2 bestCoords, final Vector2 currentCoords, final boolean up) {
        for (int i = 0, n = actors.size; i < n; ++i) {
            final Actor actor = actors.get(i);
            if (actor instanceof TextField) {
                if (actor != this) {
                    final TextField textField = (TextField)actor;
                    if (!textField.isDisabled() && textField.focusTraversal) {
                        if (textField.ancestorsVisible()) {
                            final Vector2 actorCoords = actor.getParent().localToStageCoordinates(TextField.tmp3.set(actor.getX(), actor.getY()));
                            final boolean below = actorCoords.y != currentCoords.y && (actorCoords.y < currentCoords.y ^ up);
                            final boolean right = actorCoords.y == currentCoords.y && (actorCoords.x > currentCoords.x ^ up);
                            if (below || right) {
                                boolean better = best == null || (actorCoords.y != bestCoords.y && (actorCoords.y > bestCoords.y ^ up));
                                if (!better) {
                                    better = (actorCoords.y == bestCoords.y && (actorCoords.x < bestCoords.x ^ up));
                                }
                                if (better) {
                                    best = (TextField)actor;
                                    bestCoords.set(actorCoords);
                                }
                            }
                        }
                    }
                }
            }
            else if (actor instanceof Group) {
                best = this.findNextTextField(((Group)actor).getChildren(), best, bestCoords, currentCoords, up);
            }
        }
        return best;
    }
    
    public InputListener getDefaultInputListener() {
        return this.inputListener;
    }
    
    public void setTextFieldListener(final TextFieldListener listener) {
        this.listener = listener;
    }
    
    public void setTextFieldFilter(final TextFieldFilter filter) {
        this.filter = filter;
    }
    
    public TextFieldFilter getTextFieldFilter() {
        return this.filter;
    }
    
    public void setFocusTraversal(final boolean focusTraversal) {
        this.focusTraversal = focusTraversal;
    }
    
    public String getMessageText() {
        return this.messageText;
    }
    
    public void setMessageText(final String messageText) {
        this.messageText = messageText;
    }
    
    public void appendText(String str) {
        if (str == null) {
            str = "";
        }
        this.clearSelection();
        this.cursor = this.text.length();
        this.paste(str, this.programmaticChangeEvents);
    }
    
    public void setText(String str) {
        if (str == null) {
            str = "";
        }
        if (str.equals(this.text)) {
            return;
        }
        this.clearSelection();
        final String oldText = this.text;
        this.text = "";
        this.paste(str, false);
        if (this.programmaticChangeEvents) {
            this.changeText(oldText, this.text);
        }
        this.cursor = 0;
    }
    
    public String getText() {
        return this.text;
    }
    
    boolean changeText(final String oldText, final String newText) {
        if (newText.equals(oldText)) {
            return false;
        }
        this.text = newText;
        final ChangeListener.ChangeEvent changeEvent = Pools.obtain(ChangeListener.ChangeEvent.class);
        final boolean cancelled = this.fire(changeEvent);
        this.text = (cancelled ? oldText : newText);
        Pools.free(changeEvent);
        return !cancelled;
    }
    
    public void setProgrammaticChangeEvents(final boolean programmaticChangeEvents) {
        this.programmaticChangeEvents = programmaticChangeEvents;
    }
    
    public boolean getProgrammaticChangeEvents() {
        return this.programmaticChangeEvents;
    }
    
    public int getSelectionStart() {
        return this.selectionStart;
    }
    
    public String getSelection() {
        return this.hasSelection ? this.text.substring(Math.min(this.selectionStart, this.cursor), Math.max(this.selectionStart, this.cursor)) : "";
    }
    
    public void setSelection(int selectionStart, int selectionEnd) {
        if (selectionStart < 0) {
            throw new IllegalArgumentException("selectionStart must be >= 0");
        }
        if (selectionEnd < 0) {
            throw new IllegalArgumentException("selectionEnd must be >= 0");
        }
        selectionStart = Math.min(this.text.length(), selectionStart);
        selectionEnd = Math.min(this.text.length(), selectionEnd);
        if (selectionEnd == selectionStart) {
            this.clearSelection();
            return;
        }
        if (selectionEnd < selectionStart) {
            final int temp = selectionEnd;
            selectionEnd = selectionStart;
            selectionStart = temp;
        }
        this.hasSelection = true;
        this.selectionStart = selectionStart;
        this.cursor = selectionEnd;
    }
    
    public void selectAll() {
        this.setSelection(0, this.text.length());
    }
    
    public void clearSelection() {
        this.hasSelection = false;
    }
    
    public void setCursorPosition(final int cursorPosition) {
        if (cursorPosition < 0) {
            throw new IllegalArgumentException("cursorPosition must be >= 0");
        }
        this.clearSelection();
        this.cursor = Math.min(cursorPosition, this.text.length());
    }
    
    public int getCursorPosition() {
        return this.cursor;
    }
    
    public OnscreenKeyboard getOnscreenKeyboard() {
        return this.keyboard;
    }
    
    public void setOnscreenKeyboard(final OnscreenKeyboard keyboard) {
        this.keyboard = keyboard;
    }
    
    public void setClipboard(final Clipboard clipboard) {
        this.clipboard = clipboard;
    }
    
    @Override
    public float getPrefWidth() {
        return 150.0f;
    }
    
    @Override
    public float getPrefHeight() {
        float topAndBottom = 0.0f;
        float minHeight = 0.0f;
        if (this.style.background != null) {
            topAndBottom = Math.max(topAndBottom, this.style.background.getBottomHeight() + this.style.background.getTopHeight());
            minHeight = Math.max(minHeight, this.style.background.getMinHeight());
        }
        if (this.style.focusedBackground != null) {
            topAndBottom = Math.max(topAndBottom, this.style.focusedBackground.getBottomHeight() + this.style.focusedBackground.getTopHeight());
            minHeight = Math.max(minHeight, this.style.focusedBackground.getMinHeight());
        }
        if (this.style.disabledBackground != null) {
            topAndBottom = Math.max(topAndBottom, this.style.disabledBackground.getBottomHeight() + this.style.disabledBackground.getTopHeight());
            minHeight = Math.max(minHeight, this.style.disabledBackground.getMinHeight());
        }
        return Math.max(topAndBottom + this.textHeight, minHeight);
    }
    
    public void setAlignment(final int alignment) {
        this.textHAlign = alignment;
    }
    
    public void setPasswordMode(final boolean passwordMode) {
        this.passwordMode = passwordMode;
        this.updateDisplayText();
    }
    
    public boolean isPasswordMode() {
        return this.passwordMode;
    }
    
    public void setPasswordCharacter(final char passwordCharacter) {
        this.passwordCharacter = passwordCharacter;
        if (this.passwordMode) {
            this.updateDisplayText();
        }
    }
    
    public void setBlinkTime(final float blinkTime) {
        this.blinkTime = blinkTime;
    }
    
    @Override
    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }
    
    @Override
    public boolean isDisabled() {
        return this.disabled;
    }
    
    protected void moveCursor(final boolean forward, final boolean jump) {
        final int limit = forward ? this.text.length() : 0;
        final int charOffset = forward ? 0 : -1;
        Label_0046: {
            break Label_0046;
            do {
                if (!this.continueCursor(this.cursor, charOffset)) {
                    break;
                }
                if (forward) {
                    if (++this.cursor < limit) {
                        continue;
                    }
                    break;
                }
                else {
                    if (--this.cursor > limit) {
                        continue;
                    }
                    break;
                }
            } while (jump);
        }
    }
    
    protected boolean continueCursor(final int index, final int offset) {
        final char c = this.text.charAt(index + offset);
        return this.isWordCharacter(c);
    }
    
    class KeyRepeatTask extends Timer.Task
    {
        int keycode;
        
        @Override
        public void run() {
            TextField.this.inputListener.keyDown(null, this.keycode);
        }
    }
    
    public static class DefaultOnscreenKeyboard implements OnscreenKeyboard
    {
        @Override
        public void show(final boolean visible) {
            Gdx.input.setOnscreenKeyboardVisible(visible);
        }
    }
    
    public class TextFieldClickListener extends ClickListener
    {
        @Override
        public void clicked(final InputEvent event, final float x, final float y) {
            final int count = this.getTapCount() % 4;
            if (count == 0) {
                TextField.this.clearSelection();
            }
            if (count == 2) {
                final int[] array = TextField.this.wordUnderCursor(x);
                TextField.this.setSelection(array[0], array[1]);
            }
            if (count == 3) {
                TextField.this.selectAll();
            }
        }
        
        @Override
        public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
            if (!super.touchDown(event, x, y, pointer, button)) {
                return false;
            }
            if (pointer == 0 && button != 0) {
                return false;
            }
            if (TextField.this.disabled) {
                return true;
            }
            this.setCursorPosition(x, y);
            TextField.this.selectionStart = TextField.this.cursor;
            final Stage stage = TextField.this.getStage();
            if (stage != null) {
                stage.setKeyboardFocus(TextField.this);
            }
            TextField.this.keyboard.show(true);
            return TextField.this.hasSelection = true;
        }
        
        @Override
        public void touchDragged(final InputEvent event, final float x, final float y, final int pointer) {
            super.touchDragged(event, x, y, pointer);
            this.setCursorPosition(x, y);
        }
        
        @Override
        public void touchUp(final InputEvent event, final float x, final float y, final int pointer, final int button) {
            if (TextField.this.selectionStart == TextField.this.cursor) {
                TextField.this.hasSelection = false;
            }
            super.touchUp(event, x, y, pointer, button);
        }
        
        protected void setCursorPosition(final float x, final float y) {
            TextField.this.cursor = TextField.this.letterUnderCursor(x);
            TextField.this.cursorOn = TextField.this.focused;
            TextField.this.blinkTask.cancel();
            if (TextField.this.focused) {
                Timer.schedule(TextField.this.blinkTask, TextField.this.blinkTime, TextField.this.blinkTime);
            }
        }
        
        protected void goHome(final boolean jump) {
            TextField.this.cursor = 0;
        }
        
        protected void goEnd(final boolean jump) {
            TextField.this.cursor = TextField.this.text.length();
        }
        
        @Override
        public boolean keyDown(final InputEvent event, final int keycode) {
            if (TextField.this.disabled) {
                return false;
            }
            TextField.this.cursorOn = TextField.this.focused;
            TextField.this.blinkTask.cancel();
            if (TextField.this.focused) {
                Timer.schedule(TextField.this.blinkTask, TextField.this.blinkTime, TextField.this.blinkTime);
            }
            final Stage stage = TextField.this.getStage();
            if (stage == null || stage.getKeyboardFocus() != TextField.this) {
                return false;
            }
            boolean repeat = false;
            final boolean ctrl = UIUtils.ctrl();
            final boolean jump = ctrl && !TextField.this.passwordMode;
            boolean handled = true;
            if (ctrl) {
                switch (keycode) {
                    case 50: {
                        TextField.this.paste(TextField.this.clipboard.getContents(), true);
                        repeat = true;
                        break;
                    }
                    case 31:
                    case 133: {
                        TextField.this.copy();
                        return true;
                    }
                    case 52: {
                        TextField.this.cut(true);
                        return true;
                    }
                    case 29: {
                        TextField.this.selectAll();
                        return true;
                    }
                    case 54: {
                        final String oldText = TextField.this.text;
                        TextField.this.setText(TextField.this.undoText);
                        TextField.this.undoText = oldText;
                        TextField.this.updateDisplayText();
                        return true;
                    }
                    default: {
                        handled = false;
                        break;
                    }
                }
            }
            Label_0635: {
                if (UIUtils.shift()) {
                    switch (keycode) {
                        case 133: {
                            TextField.this.paste(TextField.this.clipboard.getContents(), true);
                            break;
                        }
                        case 112: {
                            TextField.this.cut(true);
                            break;
                        }
                    }
                    final int temp = TextField.this.cursor;
                    switch (keycode) {
                        case 21: {
                            TextField.this.moveCursor(false, jump);
                            repeat = true;
                            handled = true;
                            break;
                        }
                        case 22: {
                            TextField.this.moveCursor(true, jump);
                            repeat = true;
                            handled = true;
                            break;
                        }
                        case 3: {
                            this.goHome(jump);
                            handled = true;
                            break;
                        }
                        case 132: {
                            this.goEnd(jump);
                            handled = true;
                            break;
                        }
                        default: {
                            break Label_0635;
                        }
                    }
                    if (!TextField.this.hasSelection) {
                        TextField.this.selectionStart = temp;
                        TextField.this.hasSelection = true;
                    }
                }
                else {
                    switch (keycode) {
                        case 21: {
                            TextField.this.moveCursor(false, jump);
                            TextField.this.clearSelection();
                            repeat = true;
                            handled = true;
                            break;
                        }
                        case 22: {
                            TextField.this.moveCursor(true, jump);
                            TextField.this.clearSelection();
                            repeat = true;
                            handled = true;
                            break;
                        }
                        case 3: {
                            this.goHome(jump);
                            TextField.this.clearSelection();
                            handled = true;
                            break;
                        }
                        case 132: {
                            this.goEnd(jump);
                            TextField.this.clearSelection();
                            handled = true;
                            break;
                        }
                    }
                }
            }
            TextField.this.cursor = MathUtils.clamp(TextField.this.cursor, 0, TextField.this.text.length());
            if (repeat) {
                this.scheduleKeyRepeatTask(keycode);
            }
            return handled;
        }
        
        protected void scheduleKeyRepeatTask(final int keycode) {
            if (!TextField.this.keyRepeatTask.isScheduled() || TextField.this.keyRepeatTask.keycode != keycode) {
                TextField.this.keyRepeatTask.keycode = keycode;
                TextField.this.keyRepeatTask.cancel();
                Timer.schedule(TextField.this.keyRepeatTask, TextField.keyRepeatInitialTime, TextField.keyRepeatTime);
            }
        }
        
        @Override
        public boolean keyUp(final InputEvent event, final int keycode) {
            if (TextField.this.disabled) {
                return false;
            }
            TextField.this.keyRepeatTask.cancel();
            return true;
        }
        
        @Override
        public boolean keyTyped(final InputEvent event, final char character) {
            if (TextField.this.disabled) {
                return false;
            }
            switch (character) {
                case '\b':
                case '\t':
                case '\n':
                case '\r': {
                    break;
                }
                default: {
                    if (character < ' ') {
                        return false;
                    }
                    break;
                }
            }
            final Stage stage = TextField.this.getStage();
            if (stage == null || stage.getKeyboardFocus() != TextField.this) {
                return false;
            }
            if (UIUtils.isMac && Gdx.input.isKeyPressed(63)) {
                return true;
            }
            if ((character == '\t' || character == '\n') && TextField.this.focusTraversal) {
                TextField.this.next(UIUtils.shift());
            }
            else {
                final boolean delete = character == '\u007f';
                final boolean backspace = character == '\b';
                final boolean enter = character == '\r' || character == '\n';
                final boolean add = enter ? TextField.this.writeEnters : (!TextField.this.onlyFontChars || TextField.this.style.font.getData().hasGlyph(character));
                final boolean remove = backspace || delete;
                if (add || remove) {
                    final String oldText = TextField.this.text;
                    final int oldCursor = TextField.this.cursor;
                    if (TextField.this.hasSelection) {
                        TextField.this.cursor = TextField.this.delete(false);
                    }
                    else {
                        if (backspace && TextField.this.cursor > 0) {
                            TextField.this.text = String.valueOf(TextField.this.text.substring(0, TextField.this.cursor - 1)) + TextField.this.text.substring(TextField.this.cursor--);
                            TextField.this.renderOffset = 0.0f;
                        }
                        if (delete && TextField.this.cursor < TextField.this.text.length()) {
                            TextField.this.text = String.valueOf(TextField.this.text.substring(0, TextField.this.cursor)) + TextField.this.text.substring(TextField.this.cursor + 1);
                        }
                    }
                    if (add && !remove) {
                        if (!enter && TextField.this.filter != null && !TextField.this.filter.acceptChar(TextField.this, character)) {
                            return true;
                        }
                        if (!TextField.this.withinMaxLength(TextField.this.text.length())) {
                            return true;
                        }
                        final String insertion = enter ? "\n" : String.valueOf(character);
                        TextField.this.text = TextField.this.insert(TextField.this.cursor++, insertion, TextField.this.text);
                    }
                    final String tempUndoText = TextField.this.undoText;
                    if (TextField.this.changeText(oldText, TextField.this.text)) {
                        final long time = System.currentTimeMillis();
                        if (time - 750L > TextField.this.lastChangeTime) {
                            TextField.this.undoText = oldText;
                        }
                        TextField.this.lastChangeTime = time;
                    }
                    else {
                        TextField.this.cursor = oldCursor;
                    }
                    TextField.this.updateDisplayText();
                }
            }
            if (TextField.this.listener != null) {
                TextField.this.listener.keyTyped(TextField.this, character);
            }
            return true;
        }
    }
    
    public static class TextFieldStyle
    {
        public BitmapFont font;
        public Color fontColor;
        public Color focusedFontColor;
        public Color disabledFontColor;
        public Drawable background;
        public Drawable focusedBackground;
        public Drawable disabledBackground;
        public Drawable cursor;
        public Drawable selection;
        public BitmapFont messageFont;
        public Color messageFontColor;
        
        public TextFieldStyle() {
        }
        
        public TextFieldStyle(final BitmapFont font, final Color fontColor, final Drawable cursor, final Drawable selection, final Drawable background) {
            this.background = background;
            this.cursor = cursor;
            this.font = font;
            this.fontColor = fontColor;
            this.selection = selection;
        }
        
        public TextFieldStyle(final TextFieldStyle style) {
            this.messageFont = style.messageFont;
            if (style.messageFontColor != null) {
                this.messageFontColor = new Color(style.messageFontColor);
            }
            this.background = style.background;
            this.focusedBackground = style.focusedBackground;
            this.disabledBackground = style.disabledBackground;
            this.cursor = style.cursor;
            this.font = style.font;
            if (style.fontColor != null) {
                this.fontColor = new Color(style.fontColor);
            }
            if (style.focusedFontColor != null) {
                this.focusedFontColor = new Color(style.focusedFontColor);
            }
            if (style.disabledFontColor != null) {
                this.disabledFontColor = new Color(style.disabledFontColor);
            }
            this.selection = style.selection;
        }
    }
    
    public interface OnscreenKeyboard
    {
        void show(final boolean p0);
    }
    
    public interface TextFieldFilter
    {
        boolean acceptChar(final TextField p0, final char p1);
        
        public static class DigitsOnlyFilter implements TextFieldFilter
        {
            @Override
            public boolean acceptChar(final TextField textField, final char c) {
                return Character.isDigit(c);
            }
        }
    }
    
    public interface TextFieldListener
    {
        void keyTyped(final TextField p0, final char p1);
    }
}
