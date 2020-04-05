// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.IntArray;

public class TextArea extends TextField
{
    IntArray linesBreak;
    private String lastText;
    int cursorLine;
    int firstLineShowing;
    private int linesShowing;
    float moveOffset;
    private float prefRows;
    
    public TextArea(final String text, final Skin skin) {
        super(text, skin);
    }
    
    public TextArea(final String text, final Skin skin, final String styleName) {
        super(text, skin, styleName);
    }
    
    public TextArea(final String text, final TextFieldStyle style) {
        super(text, style);
    }
    
    @Override
    protected void initialize() {
        super.initialize();
        this.writeEnters = true;
        this.linesBreak = new IntArray();
        this.cursorLine = 0;
        this.firstLineShowing = 0;
        this.moveOffset = -1.0f;
        this.linesShowing = 0;
    }
    
    @Override
    protected int letterUnderCursor(float x) {
        if (this.linesBreak.size <= 0) {
            return 0;
        }
        if (this.cursorLine * 2 >= this.linesBreak.size) {
            return this.text.length();
        }
        final float[] glyphPositions = this.glyphPositions.items;
        final int start = this.linesBreak.items[this.cursorLine * 2];
        int end;
        int i;
        for (x += glyphPositions[start], end = this.linesBreak.items[this.cursorLine * 2 + 1], i = start; i < end && glyphPositions[i] <= x; ++i) {}
        if (i > 0 && glyphPositions[i] - x <= x - glyphPositions[i - 1]) {
            return i;
        }
        return Math.max(0, i - 1);
    }
    
    public void setPrefRows(final float prefRows) {
        this.prefRows = prefRows;
    }
    
    @Override
    public float getPrefHeight() {
        if (this.prefRows <= 0.0f) {
            return super.getPrefHeight();
        }
        float prefHeight = this.textHeight * this.prefRows;
        if (this.style.background != null) {
            prefHeight = Math.max(prefHeight + this.style.background.getBottomHeight() + this.style.background.getTopHeight(), this.style.background.getMinHeight());
        }
        return prefHeight;
    }
    
    public int getLines() {
        return this.linesBreak.size / 2 + (this.newLineAtEnd() ? 1 : 0);
    }
    
    public boolean newLineAtEnd() {
        return this.text.length() != 0 && (this.text.charAt(this.text.length() - 1) == '\n' || this.text.charAt(this.text.length() - 1) == '\r');
    }
    
    public void moveCursorLine(final int line) {
        if (line < 0) {
            this.cursorLine = 0;
            this.cursor = 0;
            this.moveOffset = -1.0f;
        }
        else if (line >= this.getLines()) {
            final int newLine = this.getLines() - 1;
            this.cursor = this.text.length();
            if (line > this.getLines() || newLine == this.cursorLine) {
                this.moveOffset = -1.0f;
            }
            this.cursorLine = newLine;
        }
        else if (line != this.cursorLine) {
            if (this.moveOffset < 0.0f) {
                this.moveOffset = ((this.linesBreak.size <= this.cursorLine * 2) ? 0.0f : (this.glyphPositions.get(this.cursor) - this.glyphPositions.get(this.linesBreak.get(this.cursorLine * 2))));
            }
            this.cursorLine = line;
            this.cursor = ((this.cursorLine * 2 >= this.linesBreak.size) ? this.text.length() : this.linesBreak.get(this.cursorLine * 2));
            while (this.cursor < this.text.length() && this.cursor <= this.linesBreak.get(this.cursorLine * 2 + 1) - 1 && this.glyphPositions.get(this.cursor) - this.glyphPositions.get(this.linesBreak.get(this.cursorLine * 2)) < this.moveOffset) {
                ++this.cursor;
            }
            this.showCursor();
        }
    }
    
    void updateCurrentLine() {
        final int index = this.calculateCurrentLineIndex(this.cursor);
        final int line = index / 2;
        if ((index % 2 == 0 || index + 1 >= this.linesBreak.size || this.cursor != this.linesBreak.items[index] || this.linesBreak.items[index + 1] != this.linesBreak.items[index]) && (line < this.linesBreak.size / 2 || this.text.length() == 0 || this.text.charAt(this.text.length() - 1) == '\n' || this.text.charAt(this.text.length() - 1) == '\r')) {
            this.cursorLine = line;
        }
        this.updateFirstLineShowing();
    }
    
    void showCursor() {
        this.updateCurrentLine();
        this.updateFirstLineShowing();
    }
    
    void updateFirstLineShowing() {
        if (this.cursorLine != this.firstLineShowing) {
            final int step = (this.cursorLine >= this.firstLineShowing) ? 1 : -1;
            while (this.firstLineShowing > this.cursorLine || this.firstLineShowing + this.linesShowing - 1 < this.cursorLine) {
                this.firstLineShowing += step;
            }
        }
    }
    
    private int calculateCurrentLineIndex(final int cursor) {
        int index;
        for (index = 0; index < this.linesBreak.size && cursor > this.linesBreak.items[index]; ++index) {}
        return index;
    }
    
    @Override
    protected void sizeChanged() {
        this.lastText = null;
        final BitmapFont font = this.style.font;
        final Drawable background = this.style.background;
        final float availableHeight = this.getHeight() - ((background == null) ? 0.0f : (background.getBottomHeight() + background.getTopHeight()));
        this.linesShowing = (int)Math.floor(availableHeight / font.getLineHeight());
    }
    
    @Override
    protected float getTextY(final BitmapFont font, final Drawable background) {
        float textY = this.getHeight();
        if (background != null) {
            textY = (float)(int)(textY - background.getTopHeight());
        }
        return textY;
    }
    
    @Override
    protected void drawSelection(final Drawable selection, final Batch batch, final BitmapFont font, final float x, final float y) {
        int i = this.firstLineShowing * 2;
        float offsetY = 0.0f;
        final int minIndex = Math.min(this.cursor, this.selectionStart);
        final int maxIndex = Math.max(this.cursor, this.selectionStart);
        while (i + 1 < this.linesBreak.size && i < (this.firstLineShowing + this.linesShowing) * 2) {
            final int lineStart = this.linesBreak.get(i);
            final int lineEnd = this.linesBreak.get(i + 1);
            if ((minIndex >= lineStart || minIndex >= lineEnd || maxIndex >= lineStart || maxIndex >= lineEnd) && (minIndex <= lineStart || minIndex <= lineEnd || maxIndex <= lineStart || maxIndex <= lineEnd)) {
                final int start = Math.max(this.linesBreak.get(i), minIndex);
                final int end = Math.min(this.linesBreak.get(i + 1), maxIndex);
                final float selectionX = this.glyphPositions.get(start) - this.glyphPositions.get(this.linesBreak.get(i));
                final float selectionWidth = this.glyphPositions.get(end) - this.glyphPositions.get(start);
                selection.draw(batch, x + selectionX + this.fontOffset, y - this.textHeight - font.getDescent() - offsetY, selectionWidth, font.getLineHeight());
            }
            offsetY += font.getLineHeight();
            i += 2;
        }
    }
    
    @Override
    protected void drawText(final Batch batch, final BitmapFont font, final float x, final float y) {
        float offsetY = 0.0f;
        for (int i = this.firstLineShowing * 2; i < (this.firstLineShowing + this.linesShowing) * 2 && i < this.linesBreak.size; i += 2) {
            font.draw(batch, this.displayText, x, y + offsetY, this.linesBreak.items[i], this.linesBreak.items[i + 1], 0.0f, 8, false);
            offsetY -= font.getLineHeight();
        }
    }
    
    @Override
    protected void drawCursor(final Drawable cursorPatch, final Batch batch, final BitmapFont font, final float x, final float y) {
        final float textOffset = (this.cursor >= this.glyphPositions.size || this.cursorLine * 2 >= this.linesBreak.size) ? 0.0f : (this.glyphPositions.get(this.cursor) - this.glyphPositions.get(this.linesBreak.items[this.cursorLine * 2]));
        cursorPatch.draw(batch, x + textOffset + this.fontOffset + font.getData().cursorX, y - font.getDescent() / 2.0f - (this.cursorLine - this.firstLineShowing + 1) * font.getLineHeight(), cursorPatch.getMinWidth(), font.getLineHeight());
    }
    
    @Override
    protected void calculateOffsets() {
        super.calculateOffsets();
        if (!this.text.equals(this.lastText)) {
            this.lastText = this.text;
            final BitmapFont font = this.style.font;
            final float maxWidthLine = this.getWidth() - ((this.style.background != null) ? (this.style.background.getLeftWidth() + this.style.background.getRightWidth()) : 0.0f);
            this.linesBreak.clear();
            int lineStart = 0;
            int lastSpace = 0;
            final Pool<GlyphLayout> layoutPool = Pools.get(GlyphLayout.class);
            final GlyphLayout layout = layoutPool.obtain();
            for (int i = 0; i < this.text.length(); ++i) {
                final char lastCharacter = this.text.charAt(i);
                if (lastCharacter == '\r' || lastCharacter == '\n') {
                    this.linesBreak.add(lineStart);
                    this.linesBreak.add(i);
                    lineStart = i + 1;
                }
                else {
                    lastSpace = (this.continueCursor(i, 0) ? lastSpace : i);
                    layout.setText(font, this.text.subSequence(lineStart, i + 1));
                    if (layout.width > maxWidthLine) {
                        if (lineStart >= lastSpace) {
                            lastSpace = i - 1;
                        }
                        this.linesBreak.add(lineStart);
                        this.linesBreak.add(lastSpace + 1);
                        lineStart = ++lastSpace;
                    }
                }
            }
            layoutPool.free(layout);
            if (lineStart < this.text.length()) {
                this.linesBreak.add(lineStart);
                this.linesBreak.add(this.text.length());
            }
            this.showCursor();
        }
    }
    
    @Override
    protected InputListener createInputListener() {
        return new TextAreaListener();
    }
    
    @Override
    public void setSelection(final int selectionStart, final int selectionEnd) {
        super.setSelection(selectionStart, selectionEnd);
        this.updateCurrentLine();
    }
    
    @Override
    protected void moveCursor(final boolean forward, final boolean jump) {
        final int count = forward ? 1 : -1;
        final int index = this.cursorLine * 2 + count;
        if (index >= 0 && index + 1 < this.linesBreak.size && this.linesBreak.items[index] == this.cursor && this.linesBreak.items[index + 1] == this.cursor) {
            this.cursorLine += count;
            if (jump) {
                super.moveCursor(forward, jump);
            }
            this.showCursor();
        }
        else {
            super.moveCursor(forward, jump);
        }
        this.updateCurrentLine();
    }
    
    @Override
    protected boolean continueCursor(final int index, final int offset) {
        final int pos = this.calculateCurrentLineIndex(index + offset);
        return super.continueCursor(index, offset) && (pos < 0 || pos >= this.linesBreak.size - 2 || this.linesBreak.items[pos + 1] != index || this.linesBreak.items[pos + 1] == this.linesBreak.items[pos + 2]);
    }
    
    public int getCursorLine() {
        return this.cursorLine;
    }
    
    public int getFirstLineShowing() {
        return this.firstLineShowing;
    }
    
    public int getLinesShowing() {
        return this.linesShowing;
    }
    
    public float getCursorX() {
        return this.textOffset + this.fontOffset + this.style.font.getData().cursorX;
    }
    
    public float getCursorY() {
        final BitmapFont font = this.style.font;
        return -(-font.getDescent() / 2.0f - (this.cursorLine - this.firstLineShowing + 1) * font.getLineHeight());
    }
    
    public class TextAreaListener extends TextFieldClickListener
    {
        @Override
        protected void setCursorPosition(float x, float y) {
            TextArea.this.moveOffset = -1.0f;
            final Drawable background = TextArea.this.style.background;
            final BitmapFont font = TextArea.this.style.font;
            float height = TextArea.this.getHeight();
            if (background != null) {
                height -= background.getTopHeight();
                x -= background.getLeftWidth();
            }
            x = Math.max(0.0f, x);
            if (background != null) {
                y -= background.getTopHeight();
            }
            TextArea.this.cursorLine = (int)Math.floor((height - y) / font.getLineHeight()) + TextArea.this.firstLineShowing;
            TextArea.this.cursorLine = Math.max(0, Math.min(TextArea.this.cursorLine, TextArea.this.getLines() - 1));
            super.setCursorPosition(x, y);
            TextArea.this.updateCurrentLine();
        }
        
        @Override
        public boolean keyDown(final InputEvent event, final int keycode) {
            final boolean result = super.keyDown(event, keycode);
            final Stage stage = TextArea.this.getStage();
            if (stage != null && stage.getKeyboardFocus() == TextArea.this) {
                boolean repeat = false;
                final boolean shift = Gdx.input.isKeyPressed(59) || Gdx.input.isKeyPressed(60);
                if (keycode == 20) {
                    if (shift) {
                        if (!TextArea.this.hasSelection) {
                            TextArea.this.selectionStart = TextArea.this.cursor;
                            TextArea.this.hasSelection = true;
                        }
                    }
                    else {
                        TextArea.this.clearSelection();
                    }
                    TextArea.this.moveCursorLine(TextArea.this.cursorLine + 1);
                    repeat = true;
                }
                else if (keycode == 19) {
                    if (shift) {
                        if (!TextArea.this.hasSelection) {
                            TextArea.this.selectionStart = TextArea.this.cursor;
                            TextArea.this.hasSelection = true;
                        }
                    }
                    else {
                        TextArea.this.clearSelection();
                    }
                    TextArea.this.moveCursorLine(TextArea.this.cursorLine - 1);
                    repeat = true;
                }
                else {
                    TextArea.this.moveOffset = -1.0f;
                }
                if (repeat) {
                    this.scheduleKeyRepeatTask(keycode);
                }
                TextArea.this.showCursor();
                return true;
            }
            return result;
        }
        
        @Override
        public boolean keyTyped(final InputEvent event, final char character) {
            final boolean result = super.keyTyped(event, character);
            TextArea.this.showCursor();
            return result;
        }
        
        @Override
        protected void goHome(final boolean jump) {
            if (jump) {
                TextArea.this.cursor = 0;
            }
            else if (TextArea.this.cursorLine * 2 < TextArea.this.linesBreak.size) {
                TextArea.this.cursor = TextArea.this.linesBreak.get(TextArea.this.cursorLine * 2);
            }
        }
        
        @Override
        protected void goEnd(final boolean jump) {
            if (jump || TextArea.this.cursorLine >= TextArea.this.getLines()) {
                TextArea.this.cursor = TextArea.this.text.length();
            }
            else if (TextArea.this.cursorLine * 2 + 1 < TextArea.this.linesBreak.size) {
                TextArea.this.cursor = TextArea.this.linesBreak.get(TextArea.this.cursorLine * 2 + 1);
            }
        }
    }
}
