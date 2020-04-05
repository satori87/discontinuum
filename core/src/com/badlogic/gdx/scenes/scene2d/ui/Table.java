// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.graphics.Color;

public class Table extends WidgetGroup
{
    public static Color debugTableColor;
    public static Color debugCellColor;
    public static Color debugActorColor;
    static final Pool<Cell> cellPool;
    private static float[] columnWeightedWidth;
    private static float[] rowWeightedHeight;
    private int columns;
    private int rows;
    private boolean implicitEndRow;
    private final Array<Cell> cells;
    private final Cell cellDefaults;
    private final Array<Cell> columnDefaults;
    private Cell rowDefaults;
    private boolean sizeInvalid;
    private float[] columnMinWidth;
    private float[] rowMinHeight;
    private float[] columnPrefWidth;
    private float[] rowPrefHeight;
    private float tableMinWidth;
    private float tableMinHeight;
    private float tablePrefWidth;
    private float tablePrefHeight;
    private float[] columnWidth;
    private float[] rowHeight;
    private float[] expandWidth;
    private float[] expandHeight;
    Value padTop;
    Value padLeft;
    Value padBottom;
    Value padRight;
    int align;
    Debug debug;
    Array<DebugRect> debugRects;
    Drawable background;
    private boolean clip;
    private Skin skin;
    boolean round;
    public static Value backgroundTop;
    public static Value backgroundLeft;
    public static Value backgroundBottom;
    public static Value backgroundRight;
    
    static {
        Table.debugTableColor = new Color(0.0f, 0.0f, 1.0f, 1.0f);
        Table.debugCellColor = new Color(1.0f, 0.0f, 0.0f, 1.0f);
        Table.debugActorColor = new Color(0.0f, 1.0f, 0.0f, 1.0f);
        cellPool = new Pool<Cell>() {
            @Override
            protected Cell newObject() {
                return new Cell();
            }
        };
        Table.backgroundTop = new Value() {
            @Override
            public float get(final Actor context) {
                final Drawable background = ((Table)context).background;
                return (background == null) ? 0.0f : background.getTopHeight();
            }
        };
        Table.backgroundLeft = new Value() {
            @Override
            public float get(final Actor context) {
                final Drawable background = ((Table)context).background;
                return (background == null) ? 0.0f : background.getLeftWidth();
            }
        };
        Table.backgroundBottom = new Value() {
            @Override
            public float get(final Actor context) {
                final Drawable background = ((Table)context).background;
                return (background == null) ? 0.0f : background.getBottomHeight();
            }
        };
        Table.backgroundRight = new Value() {
            @Override
            public float get(final Actor context) {
                final Drawable background = ((Table)context).background;
                return (background == null) ? 0.0f : background.getRightWidth();
            }
        };
    }
    
    public Table() {
        this((Skin)null);
    }
    
    public Table(final Skin skin) {
        this.cells = new Array<Cell>(4);
        this.columnDefaults = new Array<Cell>(2);
        this.sizeInvalid = true;
        this.padTop = Table.backgroundTop;
        this.padLeft = Table.backgroundLeft;
        this.padBottom = Table.backgroundBottom;
        this.padRight = Table.backgroundRight;
        this.align = 1;
        this.debug = Debug.none;
        this.round = true;
        this.skin = skin;
        this.cellDefaults = this.obtainCell();
        this.setTransform(false);
        this.setTouchable(Touchable.childrenOnly);
    }
    
    private Cell obtainCell() {
        final Cell cell = Table.cellPool.obtain();
        cell.setLayout(this);
        return cell;
    }
    
    @Override
    public void draw(final Batch batch, final float parentAlpha) {
        this.validate();
        if (this.isTransform()) {
            this.applyTransform(batch, this.computeTransform());
            this.drawBackground(batch, parentAlpha, 0.0f, 0.0f);
            if (this.clip) {
                batch.flush();
                final float padLeft = this.padLeft.get(this);
                final float padBottom = this.padBottom.get(this);
                if (this.clipBegin(padLeft, padBottom, this.getWidth() - padLeft - this.padRight.get(this), this.getHeight() - padBottom - this.padTop.get(this))) {
                    this.drawChildren(batch, parentAlpha);
                    batch.flush();
                    this.clipEnd();
                }
            }
            else {
                this.drawChildren(batch, parentAlpha);
            }
            this.resetTransform(batch);
        }
        else {
            this.drawBackground(batch, parentAlpha, this.getX(), this.getY());
            super.draw(batch, parentAlpha);
        }
    }
    
    protected void drawBackground(final Batch batch, final float parentAlpha, final float x, final float y) {
        if (this.background == null) {
            return;
        }
        final Color color = this.getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        this.background.draw(batch, x, y, this.getWidth(), this.getHeight());
    }
    
    public void setBackground(final String drawableName) {
        if (this.skin == null) {
            throw new IllegalStateException("Table must have a skin set to use this method.");
        }
        this.setBackground(this.skin.getDrawable(drawableName));
    }
    
    public void setBackground(final Drawable background) {
        if (this.background == background) {
            return;
        }
        final float padTopOld = this.getPadTop();
        final float padLeftOld = this.getPadLeft();
        final float padBottomOld = this.getPadBottom();
        final float padRightOld = this.getPadRight();
        this.background = background;
        final float padTopNew = this.getPadTop();
        final float padLeftNew = this.getPadLeft();
        final float padBottomNew = this.getPadBottom();
        final float padRightNew = this.getPadRight();
        if (padTopOld + padBottomOld != padTopNew + padBottomNew || padLeftOld + padRightOld != padLeftNew + padRightNew) {
            this.invalidateHierarchy();
        }
        else if (padTopOld != padTopNew || padLeftOld != padLeftNew || padBottomOld != padBottomNew || padRightOld != padRightNew) {
            this.invalidate();
        }
    }
    
    public Table background(final Drawable background) {
        this.setBackground(background);
        return this;
    }
    
    public Table background(final String drawableName) {
        this.setBackground(drawableName);
        return this;
    }
    
    public Drawable getBackground() {
        return this.background;
    }
    
    @Override
    public Actor hit(final float x, final float y, final boolean touchable) {
        if (this.clip) {
            if (touchable && this.getTouchable() == Touchable.disabled) {
                return null;
            }
            if (x < 0.0f || x >= this.getWidth() || y < 0.0f || y >= this.getHeight()) {
                return null;
            }
        }
        return super.hit(x, y, touchable);
    }
    
    public void setClip(final boolean enabled) {
        this.setTransform(this.clip = enabled);
        this.invalidate();
    }
    
    public boolean getClip() {
        return this.clip;
    }
    
    @Override
    public void invalidate() {
        this.sizeInvalid = true;
        super.invalidate();
    }
    
    public <T extends Actor> Cell<T> add(final T actor) {
        final Cell<T> cell = (Cell<T>)this.obtainCell();
        cell.actor = actor;
        if (this.implicitEndRow) {
            this.implicitEndRow = false;
            --this.rows;
            this.cells.peek().endRow = false;
        }
        final Array<Cell> cells = this.cells;
        final int cellCount = cells.size;
        Label_0224: {
            if (cellCount > 0) {
                final Cell lastCell = cells.peek();
                if (!lastCell.endRow) {
                    cell.column = lastCell.column + lastCell.colspan;
                    cell.row = lastCell.row;
                }
                else {
                    cell.column = 0;
                    cell.row = lastCell.row + 1;
                }
                if (cell.row > 0) {
                    for (int i = cellCount - 1; i >= 0; --i) {
                        final Cell other = cells.get(i);
                        for (int column = other.column, nn = column + other.colspan; column < nn; ++column) {
                            if (column == cell.column) {
                                cell.cellAboveIndex = i;
                                break Label_0224;
                            }
                        }
                    }
                }
            }
            else {
                cell.column = 0;
                cell.row = 0;
            }
        }
        cells.add(cell);
        cell.set(this.cellDefaults);
        if (cell.column < this.columnDefaults.size) {
            final Cell columnCell = this.columnDefaults.get(cell.column);
            if (columnCell != null) {
                cell.merge(columnCell);
            }
        }
        cell.merge(this.rowDefaults);
        if (actor != null) {
            this.addActor(actor);
        }
        return cell;
    }
    
    public void add(final Actor... actors) {
        for (int i = 0, n = actors.length; i < n; ++i) {
            this.add(actors[i]);
        }
    }
    
    public Cell<Label> add(final CharSequence text) {
        if (this.skin == null) {
            throw new IllegalStateException("Table must have a skin set to use this method.");
        }
        return this.add(new Label(text, this.skin));
    }
    
    public Cell<Label> add(final CharSequence text, final String labelStyleName) {
        if (this.skin == null) {
            throw new IllegalStateException("Table must have a skin set to use this method.");
        }
        return this.add(new Label(text, this.skin.get(labelStyleName, Label.LabelStyle.class)));
    }
    
    public Cell<Label> add(final CharSequence text, final String fontName, final Color color) {
        if (this.skin == null) {
            throw new IllegalStateException("Table must have a skin set to use this method.");
        }
        return this.add(new Label(text, new Label.LabelStyle(this.skin.getFont(fontName), color)));
    }
    
    public Cell<Label> add(final CharSequence text, final String fontName, final String colorName) {
        if (this.skin == null) {
            throw new IllegalStateException("Table must have a skin set to use this method.");
        }
        return this.add(new Label(text, new Label.LabelStyle(this.skin.getFont(fontName), this.skin.getColor(colorName))));
    }
    
    public Cell add() {
        return this.add((Actor)null);
    }
    
    public Cell<Stack> stack(final Actor... actors) {
        final Stack stack = new Stack();
        if (actors != null) {
            for (int i = 0, n = actors.length; i < n; ++i) {
                stack.addActor(actors[i]);
            }
        }
        return this.add(stack);
    }
    
    @Override
    public boolean removeActor(final Actor actor) {
        return this.removeActor(actor, true);
    }
    
    @Override
    public boolean removeActor(final Actor actor, final boolean unfocus) {
        if (!super.removeActor(actor, unfocus)) {
            return false;
        }
        final Cell cell = this.getCell(actor);
        if (cell != null) {
            cell.actor = null;
        }
        return true;
    }
    
    @Override
    public void clearChildren() {
        final Array<Cell> cells = this.cells;
        for (int i = cells.size - 1; i >= 0; --i) {
            final Cell cell = cells.get(i);
            final Actor actor = cell.actor;
            if (actor != null) {
                actor.remove();
            }
        }
        Table.cellPool.freeAll(cells);
        cells.clear();
        this.rows = 0;
        this.columns = 0;
        if (this.rowDefaults != null) {
            Table.cellPool.free(this.rowDefaults);
        }
        this.rowDefaults = null;
        this.implicitEndRow = false;
        super.clearChildren();
    }
    
    public void reset() {
        this.clearChildren();
        this.padTop = Table.backgroundTop;
        this.padLeft = Table.backgroundLeft;
        this.padBottom = Table.backgroundBottom;
        this.padRight = Table.backgroundRight;
        this.align = 1;
        this.debug(Debug.none);
        this.cellDefaults.reset();
        for (int i = 0, n = this.columnDefaults.size; i < n; ++i) {
            final Cell columnCell = this.columnDefaults.get(i);
            if (columnCell != null) {
                Table.cellPool.free(columnCell);
            }
        }
        this.columnDefaults.clear();
    }
    
    public Cell row() {
        if (this.cells.size > 0) {
            if (!this.implicitEndRow) {
                if (this.cells.peek().endRow) {
                    return this.rowDefaults;
                }
                this.endRow();
            }
            this.invalidate();
        }
        this.implicitEndRow = false;
        if (this.rowDefaults != null) {
            Table.cellPool.free(this.rowDefaults);
        }
        (this.rowDefaults = this.obtainCell()).clear();
        return this.rowDefaults;
    }
    
    private void endRow() {
        final Array<Cell> cells = this.cells;
        int rowColumns = 0;
        for (int i = cells.size - 1; i >= 0; --i) {
            final Cell cell = cells.get(i);
            if (cell.endRow) {
                break;
            }
            rowColumns += cell.colspan;
        }
        this.columns = Math.max(this.columns, rowColumns);
        ++this.rows;
        cells.peek().endRow = true;
    }
    
    public Cell columnDefaults(final int column) {
        Cell cell = (this.columnDefaults.size > column) ? this.columnDefaults.get(column) : null;
        if (cell == null) {
            cell = this.obtainCell();
            cell.clear();
            if (column >= this.columnDefaults.size) {
                for (int i = this.columnDefaults.size; i < column; ++i) {
                    this.columnDefaults.add(null);
                }
                this.columnDefaults.add(cell);
            }
            else {
                this.columnDefaults.set(column, cell);
            }
        }
        return cell;
    }
    
    public <T extends Actor> Cell<T> getCell(final T actor) {
        final Array<Cell> cells = this.cells;
        for (int i = 0, n = cells.size; i < n; ++i) {
            final Cell c = cells.get(i);
            if (c.actor == actor) {
                return (Cell<T>)c;
            }
        }
        return null;
    }
    
    public Array<Cell> getCells() {
        return this.cells;
    }
    
    @Override
    public float getPrefWidth() {
        if (this.sizeInvalid) {
            this.computeSize();
        }
        final float width = this.tablePrefWidth;
        if (this.background != null) {
            return Math.max(width, this.background.getMinWidth());
        }
        return width;
    }
    
    @Override
    public float getPrefHeight() {
        if (this.sizeInvalid) {
            this.computeSize();
        }
        final float height = this.tablePrefHeight;
        if (this.background != null) {
            return Math.max(height, this.background.getMinHeight());
        }
        return height;
    }
    
    @Override
    public float getMinWidth() {
        if (this.sizeInvalid) {
            this.computeSize();
        }
        return this.tableMinWidth;
    }
    
    @Override
    public float getMinHeight() {
        if (this.sizeInvalid) {
            this.computeSize();
        }
        return this.tableMinHeight;
    }
    
    public Cell defaults() {
        return this.cellDefaults;
    }
    
    public Table pad(final Value pad) {
        if (pad == null) {
            throw new IllegalArgumentException("pad cannot be null.");
        }
        this.padTop = pad;
        this.padLeft = pad;
        this.padBottom = pad;
        this.padRight = pad;
        this.sizeInvalid = true;
        return this;
    }
    
    public Table pad(final Value top, final Value left, final Value bottom, final Value right) {
        if (top == null) {
            throw new IllegalArgumentException("top cannot be null.");
        }
        if (left == null) {
            throw new IllegalArgumentException("left cannot be null.");
        }
        if (bottom == null) {
            throw new IllegalArgumentException("bottom cannot be null.");
        }
        if (right == null) {
            throw new IllegalArgumentException("right cannot be null.");
        }
        this.padTop = top;
        this.padLeft = left;
        this.padBottom = bottom;
        this.padRight = right;
        this.sizeInvalid = true;
        return this;
    }
    
    public Table padTop(final Value padTop) {
        if (padTop == null) {
            throw new IllegalArgumentException("padTop cannot be null.");
        }
        this.padTop = padTop;
        this.sizeInvalid = true;
        return this;
    }
    
    public Table padLeft(final Value padLeft) {
        if (padLeft == null) {
            throw new IllegalArgumentException("padLeft cannot be null.");
        }
        this.padLeft = padLeft;
        this.sizeInvalid = true;
        return this;
    }
    
    public Table padBottom(final Value padBottom) {
        if (padBottom == null) {
            throw new IllegalArgumentException("padBottom cannot be null.");
        }
        this.padBottom = padBottom;
        this.sizeInvalid = true;
        return this;
    }
    
    public Table padRight(final Value padRight) {
        if (padRight == null) {
            throw new IllegalArgumentException("padRight cannot be null.");
        }
        this.padRight = padRight;
        this.sizeInvalid = true;
        return this;
    }
    
    public Table pad(final float pad) {
        this.pad(new Value.Fixed(pad));
        return this;
    }
    
    public Table pad(final float top, final float left, final float bottom, final float right) {
        this.padTop = new Value.Fixed(top);
        this.padLeft = new Value.Fixed(left);
        this.padBottom = new Value.Fixed(bottom);
        this.padRight = new Value.Fixed(right);
        this.sizeInvalid = true;
        return this;
    }
    
    public Table padTop(final float padTop) {
        this.padTop = new Value.Fixed(padTop);
        this.sizeInvalid = true;
        return this;
    }
    
    public Table padLeft(final float padLeft) {
        this.padLeft = new Value.Fixed(padLeft);
        this.sizeInvalid = true;
        return this;
    }
    
    public Table padBottom(final float padBottom) {
        this.padBottom = new Value.Fixed(padBottom);
        this.sizeInvalid = true;
        return this;
    }
    
    public Table padRight(final float padRight) {
        this.padRight = new Value.Fixed(padRight);
        this.sizeInvalid = true;
        return this;
    }
    
    public Table align(final int align) {
        this.align = align;
        return this;
    }
    
    public Table center() {
        this.align = 1;
        return this;
    }
    
    public Table top() {
        this.align |= 0x2;
        this.align &= 0xFFFFFFFB;
        return this;
    }
    
    public Table left() {
        this.align |= 0x8;
        this.align &= 0xFFFFFFEF;
        return this;
    }
    
    public Table bottom() {
        this.align |= 0x4;
        this.align &= 0xFFFFFFFD;
        return this;
    }
    
    public Table right() {
        this.align |= 0x10;
        this.align &= 0xFFFFFFF7;
        return this;
    }
    
    @Override
    public void setDebug(final boolean enabled) {
        this.debug(enabled ? Debug.all : Debug.none);
    }
    
    @Override
    public Table debug() {
        super.debug();
        return this;
    }
    
    @Override
    public Table debugAll() {
        super.debugAll();
        return this;
    }
    
    public Table debugTable() {
        super.setDebug(true);
        if (this.debug != Debug.table) {
            this.debug = Debug.table;
            this.invalidate();
        }
        return this;
    }
    
    public Table debugCell() {
        super.setDebug(true);
        if (this.debug != Debug.cell) {
            this.debug = Debug.cell;
            this.invalidate();
        }
        return this;
    }
    
    public Table debugActor() {
        super.setDebug(true);
        if (this.debug != Debug.actor) {
            this.debug = Debug.actor;
            this.invalidate();
        }
        return this;
    }
    
    public Table debug(final Debug debug) {
        super.setDebug(debug != Debug.none);
        if (this.debug != debug) {
            if ((this.debug = debug) == Debug.none) {
                this.clearDebugRects();
            }
            else {
                this.invalidate();
            }
        }
        return this;
    }
    
    public Debug getTableDebug() {
        return this.debug;
    }
    
    public Value getPadTopValue() {
        return this.padTop;
    }
    
    public float getPadTop() {
        return this.padTop.get(this);
    }
    
    public Value getPadLeftValue() {
        return this.padLeft;
    }
    
    public float getPadLeft() {
        return this.padLeft.get(this);
    }
    
    public Value getPadBottomValue() {
        return this.padBottom;
    }
    
    public float getPadBottom() {
        return this.padBottom.get(this);
    }
    
    public Value getPadRightValue() {
        return this.padRight;
    }
    
    public float getPadRight() {
        return this.padRight.get(this);
    }
    
    public float getPadX() {
        return this.padLeft.get(this) + this.padRight.get(this);
    }
    
    public float getPadY() {
        return this.padTop.get(this) + this.padBottom.get(this);
    }
    
    public int getAlign() {
        return this.align;
    }
    
    public int getRow(float y) {
        final Array<Cell> cells = this.cells;
        int row = 0;
        y += this.getPadTop();
        int i = 0;
        final int n = cells.size;
        if (n == 0) {
            return -1;
        }
        if (n == 1) {
            return 0;
        }
        while (i < n) {
            final Cell c = cells.get(i++);
            if (c.actorY + c.computedPadTop < y) {
                break;
            }
            if (!c.endRow) {
                continue;
            }
            ++row;
        }
        return row;
    }
    
    public void setSkin(final Skin skin) {
        this.skin = skin;
    }
    
    public void setRound(final boolean round) {
        this.round = round;
    }
    
    public int getRows() {
        return this.rows;
    }
    
    public int getColumns() {
        return this.columns;
    }
    
    public float getRowHeight(final int rowIndex) {
        if (this.rowHeight == null) {
            return 0.0f;
        }
        return this.rowHeight[rowIndex];
    }
    
    public float getRowMinHeight(final int rowIndex) {
        if (this.sizeInvalid) {
            this.computeSize();
        }
        return this.rowMinHeight[rowIndex];
    }
    
    public float getRowPrefHeight(final int rowIndex) {
        if (this.sizeInvalid) {
            this.computeSize();
        }
        return this.rowPrefHeight[rowIndex];
    }
    
    public float getColumnWidth(final int columnIndex) {
        if (this.columnWidth == null) {
            return 0.0f;
        }
        return this.columnWidth[columnIndex];
    }
    
    public float getColumnMinWidth(final int columnIndex) {
        if (this.sizeInvalid) {
            this.computeSize();
        }
        return this.columnMinWidth[columnIndex];
    }
    
    public float getColumnPrefWidth(final int columnIndex) {
        if (this.sizeInvalid) {
            this.computeSize();
        }
        return this.columnPrefWidth[columnIndex];
    }
    
    private float[] ensureSize(final float[] array, final int size) {
        if (array == null || array.length < size) {
            return new float[size];
        }
        for (int i = 0, n = array.length; i < n; ++i) {
            array[i] = 0.0f;
        }
        return array;
    }
    
    @Override
    public void layout() {
        final float width = this.getWidth();
        final float height = this.getHeight();
        this.layout(0.0f, 0.0f, width, height);
        final Array<Cell> cells = this.cells;
        if (this.round) {
            for (int i = 0, n = cells.size; i < n; ++i) {
                final Cell c = cells.get(i);
                final float actorWidth = (float)Math.round(c.actorWidth);
                final float actorHeight = (float)Math.round(c.actorHeight);
                final float actorX = (float)Math.round(c.actorX);
                final float actorY = height - Math.round(c.actorY) - actorHeight;
                c.setActorBounds(actorX, actorY, actorWidth, actorHeight);
                final Actor actor = c.actor;
                if (actor != null) {
                    actor.setBounds(actorX, actorY, actorWidth, actorHeight);
                }
            }
        }
        else {
            for (int i = 0, n = cells.size; i < n; ++i) {
                final Cell c = cells.get(i);
                final float actorHeight2 = c.actorHeight;
                final float actorY2 = height - c.actorY - actorHeight2;
                c.setActorY(actorY2);
                final Actor actor2 = c.actor;
                if (actor2 != null) {
                    actor2.setBounds(c.actorX, actorY2, c.actorWidth, actorHeight2);
                }
            }
        }
        final Array<Actor> children = this.getChildren();
        for (int j = 0, n2 = children.size; j < n2; ++j) {
            final Actor child = children.get(j);
            if (child instanceof Layout) {
                ((Layout)child).validate();
            }
        }
    }
    
    private void computeSize() {
        this.sizeInvalid = false;
        final Array<Cell> cells = this.cells;
        final int cellCount = cells.size;
        if (cellCount > 0 && !cells.peek().endRow) {
            this.endRow();
            this.implicitEndRow = true;
        }
        final int columns = this.columns;
        final int rows = this.rows;
        final float[] ensureSize = this.ensureSize(this.columnMinWidth, columns);
        this.columnMinWidth = ensureSize;
        final float[] columnMinWidth = ensureSize;
        final float[] ensureSize2 = this.ensureSize(this.rowMinHeight, rows);
        this.rowMinHeight = ensureSize2;
        final float[] rowMinHeight = ensureSize2;
        final float[] ensureSize3 = this.ensureSize(this.columnPrefWidth, columns);
        this.columnPrefWidth = ensureSize3;
        final float[] columnPrefWidth = ensureSize3;
        final float[] ensureSize4 = this.ensureSize(this.rowPrefHeight, rows);
        this.rowPrefHeight = ensureSize4;
        final float[] rowPrefHeight = ensureSize4;
        final float[] ensureSize5 = this.ensureSize(this.columnWidth, columns);
        this.columnWidth = ensureSize5;
        final float[] columnWidth = ensureSize5;
        final float[] ensureSize6 = this.ensureSize(this.rowHeight, rows);
        this.rowHeight = ensureSize6;
        final float[] rowHeight = ensureSize6;
        final float[] ensureSize7 = this.ensureSize(this.expandWidth, columns);
        this.expandWidth = ensureSize7;
        final float[] expandWidth = ensureSize7;
        final float[] ensureSize8 = this.ensureSize(this.expandHeight, rows);
        this.expandHeight = ensureSize8;
        final float[] expandHeight = ensureSize8;
        float spaceRightLast = 0.0f;
        for (int i = 0; i < cellCount; ++i) {
            final Cell c = cells.get(i);
            final int column = c.column;
            final int row = c.row;
            final int colspan = c.colspan;
            final Actor a = c.actor;
            if (c.expandY != 0 && expandHeight[row] == 0.0f) {
                expandHeight[row] = c.expandY;
            }
            if (colspan == 1 && c.expandX != 0 && expandWidth[column] == 0.0f) {
                expandWidth[column] = c.expandX;
            }
            c.computedPadLeft = c.padLeft.get(a) + ((column == 0) ? 0.0f : Math.max(0.0f, c.spaceLeft.get(a) - spaceRightLast));
            c.computedPadTop = c.padTop.get(a);
            if (c.cellAboveIndex != -1) {
                final Cell above = cells.get(c.cellAboveIndex);
                final Cell cell = c;
                cell.computedPadTop += Math.max(0.0f, c.spaceTop.get(a) - above.spaceBottom.get(a));
            }
            final float spaceRight = c.spaceRight.get(a);
            c.computedPadRight = c.padRight.get(a) + ((column + colspan == columns) ? 0.0f : spaceRight);
            c.computedPadBottom = c.padBottom.get(a) + ((row == rows - 1) ? 0.0f : c.spaceBottom.get(a));
            spaceRightLast = spaceRight;
            float prefWidth = c.prefWidth.get(a);
            float prefHeight = c.prefHeight.get(a);
            final float minWidth = c.minWidth.get(a);
            final float minHeight = c.minHeight.get(a);
            final float maxWidth = c.maxWidth.get(a);
            final float maxHeight = c.maxHeight.get(a);
            if (prefWidth < minWidth) {
                prefWidth = minWidth;
            }
            if (prefHeight < minHeight) {
                prefHeight = minHeight;
            }
            if (maxWidth > 0.0f && prefWidth > maxWidth) {
                prefWidth = maxWidth;
            }
            if (maxHeight > 0.0f && prefHeight > maxHeight) {
                prefHeight = maxHeight;
            }
            if (colspan == 1) {
                final float hpadding = c.computedPadLeft + c.computedPadRight;
                columnPrefWidth[column] = Math.max(columnPrefWidth[column], prefWidth + hpadding);
                columnMinWidth[column] = Math.max(columnMinWidth[column], minWidth + hpadding);
            }
            final float vpadding = c.computedPadTop + c.computedPadBottom;
            rowPrefHeight[row] = Math.max(rowPrefHeight[row], prefHeight + vpadding);
            rowMinHeight[row] = Math.max(rowMinHeight[row], minHeight + vpadding);
        }
        float uniformMinWidth = 0.0f;
        float uniformMinHeight = 0.0f;
        float uniformPrefWidth = 0.0f;
        float uniformPrefHeight = 0.0f;
        for (int j = 0; j < cellCount; ++j) {
            final Cell c2 = cells.get(j);
            final int column2 = c2.column;
            final int expandX = c2.expandX;
            Label_0878: {
                if (expandX != 0) {
                    final int nn = column2 + c2.colspan;
                    for (int ii = column2; ii < nn; ++ii) {
                        if (expandWidth[ii] != 0.0f) {
                            break Label_0878;
                        }
                    }
                    for (int ii = column2; ii < nn; ++ii) {
                        expandWidth[ii] = (float)expandX;
                    }
                }
            }
            if (c2.uniformX == Boolean.TRUE && c2.colspan == 1) {
                final float hpadding2 = c2.computedPadLeft + c2.computedPadRight;
                uniformMinWidth = Math.max(uniformMinWidth, columnMinWidth[column2] - hpadding2);
                uniformPrefWidth = Math.max(uniformPrefWidth, columnPrefWidth[column2] - hpadding2);
            }
            if (c2.uniformY == Boolean.TRUE) {
                final float vpadding2 = c2.computedPadTop + c2.computedPadBottom;
                uniformMinHeight = Math.max(uniformMinHeight, rowMinHeight[c2.row] - vpadding2);
                uniformPrefHeight = Math.max(uniformPrefHeight, rowPrefHeight[c2.row] - vpadding2);
            }
        }
        if (uniformPrefWidth > 0.0f || uniformPrefHeight > 0.0f) {
            for (int j = 0; j < cellCount; ++j) {
                final Cell c2 = cells.get(j);
                if (uniformPrefWidth > 0.0f && c2.uniformX == Boolean.TRUE && c2.colspan == 1) {
                    final float hpadding3 = c2.computedPadLeft + c2.computedPadRight;
                    columnMinWidth[c2.column] = uniformMinWidth + hpadding3;
                    columnPrefWidth[c2.column] = uniformPrefWidth + hpadding3;
                }
                if (uniformPrefHeight > 0.0f && c2.uniformY == Boolean.TRUE) {
                    final float vpadding3 = c2.computedPadTop + c2.computedPadBottom;
                    rowMinHeight[c2.row] = uniformMinHeight + vpadding3;
                    rowPrefHeight[c2.row] = uniformPrefHeight + vpadding3;
                }
            }
        }
        for (int j = 0; j < cellCount; ++j) {
            final Cell c2 = cells.get(j);
            final int colspan2 = c2.colspan;
            if (colspan2 != 1) {
                final int column3 = c2.column;
                final Actor a2 = c2.actor;
                final float minWidth = c2.minWidth.get(a2);
                float prefWidth2 = c2.prefWidth.get(a2);
                final float maxWidth = c2.maxWidth.get(a2);
                if (prefWidth2 < minWidth) {
                    prefWidth2 = minWidth;
                }
                if (maxWidth > 0.0f && prefWidth2 > maxWidth) {
                    prefWidth2 = maxWidth;
                }
                float spannedPrefWidth;
                float spannedMinWidth = spannedPrefWidth = -(c2.computedPadLeft + c2.computedPadRight);
                float totalExpandWidth = 0.0f;
                for (int ii2 = column3, nn2 = ii2 + colspan2; ii2 < nn2; ++ii2) {
                    spannedMinWidth += columnMinWidth[ii2];
                    spannedPrefWidth += columnPrefWidth[ii2];
                    totalExpandWidth += expandWidth[ii2];
                }
                final float extraMinWidth = Math.max(0.0f, minWidth - spannedMinWidth);
                final float extraPrefWidth = Math.max(0.0f, prefWidth2 - spannedPrefWidth);
                for (int ii3 = column3, nn3 = ii3 + colspan2; ii3 < nn3; ++ii3) {
                    final float ratio = (totalExpandWidth == 0.0f) ? (1.0f / colspan2) : (expandWidth[ii3] / totalExpandWidth);
                    final float[] array = columnMinWidth;
                    final int n = ii3;
                    array[n] += extraMinWidth * ratio;
                    final float[] array2 = columnPrefWidth;
                    final int n2 = ii3;
                    array2[n2] += extraPrefWidth * ratio;
                }
            }
        }
        this.tableMinWidth = 0.0f;
        this.tableMinHeight = 0.0f;
        this.tablePrefWidth = 0.0f;
        this.tablePrefHeight = 0.0f;
        for (int j = 0; j < columns; ++j) {
            this.tableMinWidth += columnMinWidth[j];
            this.tablePrefWidth += columnPrefWidth[j];
        }
        for (int j = 0; j < rows; ++j) {
            this.tableMinHeight += rowMinHeight[j];
            this.tablePrefHeight += Math.max(rowMinHeight[j], rowPrefHeight[j]);
        }
        final float hpadding4 = this.padLeft.get(this) + this.padRight.get(this);
        final float vpadding4 = this.padTop.get(this) + this.padBottom.get(this);
        this.tableMinWidth += hpadding4;
        this.tableMinHeight += vpadding4;
        this.tablePrefWidth = Math.max(this.tablePrefWidth + hpadding4, this.tableMinWidth);
        this.tablePrefHeight = Math.max(this.tablePrefHeight + vpadding4, this.tableMinHeight);
    }
    
    private void layout(final float layoutX, final float layoutY, final float layoutWidth, final float layoutHeight) {
        final Array<Cell> cells = this.cells;
        final int cellCount = cells.size;
        if (this.sizeInvalid) {
            this.computeSize();
        }
        final float padLeft = this.padLeft.get(this);
        final float hpadding = padLeft + this.padRight.get(this);
        final float padTop = this.padTop.get(this);
        final float vpadding = padTop + this.padBottom.get(this);
        final int columns = this.columns;
        final int rows = this.rows;
        final float[] expandWidth = this.expandWidth;
        final float[] expandHeight = this.expandHeight;
        final float[] columnWidth = this.columnWidth;
        final float[] rowHeight = this.rowHeight;
        float totalExpandWidth = 0.0f;
        float totalExpandHeight = 0.0f;
        for (int i = 0; i < columns; ++i) {
            totalExpandWidth += expandWidth[i];
        }
        for (int i = 0; i < rows; ++i) {
            totalExpandHeight += expandHeight[i];
        }
        final float totalGrowWidth = this.tablePrefWidth - this.tableMinWidth;
        float[] columnWeightedWidth;
        if (totalGrowWidth == 0.0f) {
            columnWeightedWidth = this.columnMinWidth;
        }
        else {
            final float extraWidth = Math.min(totalGrowWidth, Math.max(0.0f, layoutWidth - this.tableMinWidth));
            columnWeightedWidth = (Table.columnWeightedWidth = this.ensureSize(Table.columnWeightedWidth, columns));
            final float[] columnMinWidth = this.columnMinWidth;
            final float[] columnPrefWidth = this.columnPrefWidth;
            for (int j = 0; j < columns; ++j) {
                final float growWidth = columnPrefWidth[j] - columnMinWidth[j];
                final float growRatio = growWidth / totalGrowWidth;
                columnWeightedWidth[j] = columnMinWidth[j] + extraWidth * growRatio;
            }
        }
        final float totalGrowHeight = this.tablePrefHeight - this.tableMinHeight;
        float[] rowWeightedHeight;
        if (totalGrowHeight == 0.0f) {
            rowWeightedHeight = this.rowMinHeight;
        }
        else {
            rowWeightedHeight = (Table.rowWeightedHeight = this.ensureSize(Table.rowWeightedHeight, rows));
            final float extraHeight = Math.min(totalGrowHeight, Math.max(0.0f, layoutHeight - this.tableMinHeight));
            final float[] rowMinHeight = this.rowMinHeight;
            final float[] rowPrefHeight = this.rowPrefHeight;
            for (int k = 0; k < rows; ++k) {
                final float growHeight = rowPrefHeight[k] - rowMinHeight[k];
                final float growRatio2 = growHeight / totalGrowHeight;
                rowWeightedHeight[k] = rowMinHeight[k] + extraHeight * growRatio2;
            }
        }
        for (int l = 0; l < cellCount; ++l) {
            final Cell c = cells.get(l);
            final int column = c.column;
            final int row = c.row;
            final Actor a = c.actor;
            float spannedWeightedWidth = 0.0f;
            final int colspan = c.colspan;
            for (int ii = column, nn = ii + colspan; ii < nn; ++ii) {
                spannedWeightedWidth += columnWeightedWidth[ii];
            }
            final float weightedHeight = rowWeightedHeight[row];
            float prefWidth = c.prefWidth.get(a);
            float prefHeight = c.prefHeight.get(a);
            final float minWidth = c.minWidth.get(a);
            final float minHeight = c.minHeight.get(a);
            final float maxWidth = c.maxWidth.get(a);
            final float maxHeight = c.maxHeight.get(a);
            if (prefWidth < minWidth) {
                prefWidth = minWidth;
            }
            if (prefHeight < minHeight) {
                prefHeight = minHeight;
            }
            if (maxWidth > 0.0f && prefWidth > maxWidth) {
                prefWidth = maxWidth;
            }
            if (maxHeight > 0.0f && prefHeight > maxHeight) {
                prefHeight = maxHeight;
            }
            c.actorWidth = Math.min(spannedWeightedWidth - c.computedPadLeft - c.computedPadRight, prefWidth);
            c.actorHeight = Math.min(weightedHeight - c.computedPadTop - c.computedPadBottom, prefHeight);
            if (colspan == 1) {
                columnWidth[column] = Math.max(columnWidth[column], spannedWeightedWidth);
            }
            rowHeight[row] = Math.max(rowHeight[row], weightedHeight);
        }
        if (totalExpandWidth > 0.0f) {
            float extra = layoutWidth - hpadding;
            for (int j = 0; j < columns; ++j) {
                extra -= columnWidth[j];
            }
            if (extra > 0.0f) {
                float used = 0.0f;
                int lastIndex = 0;
                for (int k = 0; k < columns; ++k) {
                    if (expandWidth[k] != 0.0f) {
                        final float amount = extra * expandWidth[k] / totalExpandWidth;
                        final float[] array = columnWidth;
                        final int n = k;
                        array[n] += amount;
                        used += amount;
                        lastIndex = k;
                    }
                }
                final float[] array2 = columnWidth;
                final int n2 = lastIndex;
                array2[n2] += extra - used;
            }
        }
        if (totalExpandHeight > 0.0f) {
            float extra = layoutHeight - vpadding;
            for (int j = 0; j < rows; ++j) {
                extra -= rowHeight[j];
            }
            if (extra > 0.0f) {
                float used = 0.0f;
                int lastIndex = 0;
                for (int k = 0; k < rows; ++k) {
                    if (expandHeight[k] != 0.0f) {
                        final float amount = extra * expandHeight[k] / totalExpandHeight;
                        final float[] array3 = rowHeight;
                        final int n3 = k;
                        array3[n3] += amount;
                        used += amount;
                        lastIndex = k;
                    }
                }
                final float[] array4 = rowHeight;
                final int n4 = lastIndex;
                array4[n4] += extra - used;
            }
        }
        for (int l = 0; l < cellCount; ++l) {
            final Cell c = cells.get(l);
            final int colspan2 = c.colspan;
            if (colspan2 != 1) {
                float extraWidth2 = 0.0f;
                for (int column2 = c.column, nn2 = column2 + colspan2; column2 < nn2; ++column2) {
                    extraWidth2 += columnWeightedWidth[column2] - columnWidth[column2];
                }
                extraWidth2 -= Math.max(0.0f, c.computedPadLeft + c.computedPadRight);
                extraWidth2 /= colspan2;
                if (extraWidth2 > 0.0f) {
                    for (int column2 = c.column, nn2 = column2 + colspan2; column2 < nn2; ++column2) {
                        final float[] array5 = columnWidth;
                        final int n5 = column2;
                        array5[n5] += extraWidth2;
                    }
                }
            }
        }
        float tableWidth = hpadding;
        float tableHeight = vpadding;
        for (int m = 0; m < columns; ++m) {
            tableWidth += columnWidth[m];
        }
        for (int m = 0; m < rows; ++m) {
            tableHeight += rowHeight[m];
        }
        int align = this.align;
        float x = layoutX + padLeft;
        if ((align & 0x10) != 0x0) {
            x += layoutWidth - tableWidth;
        }
        else if ((align & 0x8) == 0x0) {
            x += (layoutWidth - tableWidth) / 2.0f;
        }
        float y = layoutY + padTop;
        if ((align & 0x4) != 0x0) {
            y += layoutHeight - tableHeight;
        }
        else if ((align & 0x2) == 0x0) {
            y += (layoutHeight - tableHeight) / 2.0f;
        }
        float currentX = x;
        float currentY = y;
        for (int i2 = 0; i2 < cellCount; ++i2) {
            final Cell c2 = cells.get(i2);
            float spannedCellWidth = 0.0f;
            for (int column3 = c2.column, nn3 = column3 + c2.colspan; column3 < nn3; ++column3) {
                spannedCellWidth += columnWidth[column3];
            }
            spannedCellWidth -= c2.computedPadLeft + c2.computedPadRight;
            currentX += c2.computedPadLeft;
            final float fillX = c2.fillX;
            final float fillY = c2.fillY;
            if (fillX > 0.0f) {
                c2.actorWidth = Math.max(spannedCellWidth * fillX, c2.minWidth.get(c2.actor));
                final float maxWidth = c2.maxWidth.get(c2.actor);
                if (maxWidth > 0.0f) {
                    c2.actorWidth = Math.min(c2.actorWidth, maxWidth);
                }
            }
            if (fillY > 0.0f) {
                c2.actorHeight = Math.max(rowHeight[c2.row] * fillY - c2.computedPadTop - c2.computedPadBottom, c2.minHeight.get(c2.actor));
                final float maxHeight2 = c2.maxHeight.get(c2.actor);
                if (maxHeight2 > 0.0f) {
                    c2.actorHeight = Math.min(c2.actorHeight, maxHeight2);
                }
            }
            align = c2.align;
            if ((align & 0x8) != 0x0) {
                c2.actorX = currentX;
            }
            else if ((align & 0x10) != 0x0) {
                c2.actorX = currentX + spannedCellWidth - c2.actorWidth;
            }
            else {
                c2.actorX = currentX + (spannedCellWidth - c2.actorWidth) / 2.0f;
            }
            if ((align & 0x2) != 0x0) {
                c2.actorY = currentY + c2.computedPadTop;
            }
            else if ((align & 0x4) != 0x0) {
                c2.actorY = currentY + rowHeight[c2.row] - c2.actorHeight - c2.computedPadBottom;
            }
            else {
                c2.actorY = currentY + (rowHeight[c2.row] - c2.actorHeight + c2.computedPadTop - c2.computedPadBottom) / 2.0f;
            }
            if (c2.endRow) {
                currentX = x;
                currentY += rowHeight[c2.row];
            }
            else {
                currentX += spannedCellWidth + c2.computedPadRight;
            }
        }
        if (this.debug == Debug.none) {
            return;
        }
        this.clearDebugRects();
        currentX = x;
        currentY = y;
        if (this.debug == Debug.table || this.debug == Debug.all) {
            this.addDebugRect(layoutX, layoutY, layoutWidth, layoutHeight, Table.debugTableColor);
            this.addDebugRect(x, y, tableWidth - hpadding, tableHeight - vpadding, Table.debugTableColor);
        }
        for (int i2 = 0; i2 < cellCount; ++i2) {
            final Cell c2 = cells.get(i2);
            if (this.debug == Debug.actor || this.debug == Debug.all) {
                this.addDebugRect(c2.actorX, c2.actorY, c2.actorWidth, c2.actorHeight, Table.debugActorColor);
            }
            float spannedCellWidth = 0.0f;
            for (int column3 = c2.column, nn3 = column3 + c2.colspan; column3 < nn3; ++column3) {
                spannedCellWidth += columnWidth[column3];
            }
            spannedCellWidth -= c2.computedPadLeft + c2.computedPadRight;
            currentX += c2.computedPadLeft;
            if (this.debug == Debug.cell || this.debug == Debug.all) {
                this.addDebugRect(currentX, currentY + c2.computedPadTop, spannedCellWidth, rowHeight[c2.row] - c2.computedPadTop - c2.computedPadBottom, Table.debugCellColor);
            }
            if (c2.endRow) {
                currentX = x;
                currentY += rowHeight[c2.row];
            }
            else {
                currentX += spannedCellWidth + c2.computedPadRight;
            }
        }
    }
    
    private void clearDebugRects() {
        if (this.debugRects == null) {
            return;
        }
        DebugRect.pool.freeAll(this.debugRects);
        this.debugRects.clear();
    }
    
    private void addDebugRect(final float x, final float y, final float w, final float h, final Color color) {
        if (this.debugRects == null) {
            this.debugRects = new Array<DebugRect>();
        }
        final DebugRect rect = DebugRect.pool.obtain();
        rect.color = color;
        rect.set(x, this.getHeight() - y - h, w, h);
        this.debugRects.add(rect);
    }
    
    @Override
    public void drawDebug(final ShapeRenderer shapes) {
        if (this.isTransform()) {
            this.applyTransform(shapes, this.computeTransform());
            this.drawDebugRects(shapes);
            if (this.clip) {
                shapes.flush();
                float x = 0.0f;
                float y = 0.0f;
                float width = this.getWidth();
                float height = this.getHeight();
                if (this.background != null) {
                    x = this.padLeft.get(this);
                    y = this.padBottom.get(this);
                    width -= x + this.padRight.get(this);
                    height -= y + this.padTop.get(this);
                }
                if (this.clipBegin(x, y, width, height)) {
                    this.drawDebugChildren(shapes);
                    this.clipEnd();
                }
            }
            else {
                this.drawDebugChildren(shapes);
            }
            this.resetTransform(shapes);
        }
        else {
            this.drawDebugRects(shapes);
            super.drawDebug(shapes);
        }
    }
    
    @Override
    protected void drawDebugBounds(final ShapeRenderer shapes) {
    }
    
    private void drawDebugRects(final ShapeRenderer shapes) {
        if (this.debugRects == null || !this.getDebug()) {
            return;
        }
        shapes.set(ShapeRenderer.ShapeType.Line);
        shapes.setColor(this.getStage().getDebugColor());
        float x = 0.0f;
        float y = 0.0f;
        if (!this.isTransform()) {
            x = this.getX();
            y = this.getY();
        }
        for (int i = 0, n = this.debugRects.size; i < n; ++i) {
            final DebugRect debugRect = this.debugRects.get(i);
            shapes.setColor(debugRect.color);
            shapes.rect(x + debugRect.x, y + debugRect.y, debugRect.width, debugRect.height);
        }
    }
    
    public Skin getSkin() {
        return this.skin;
    }
    
    public enum Debug
    {
        none("none", 0), 
        all("all", 1), 
        table("table", 2), 
        cell("cell", 3), 
        actor("actor", 4);
        
        private Debug(final String name, final int ordinal) {
        }
    }
    
    public static class DebugRect extends Rectangle
    {
        static Pool<DebugRect> pool;
        Color color;
        
        static {
            DebugRect.pool = Pools.get(DebugRect.class);
        }
    }
}
