// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Selection;
import com.badlogic.gdx.utils.Array;

public class Tree extends WidgetGroup
{
    TreeStyle style;
    final Array<Node> rootNodes;
    final Selection<Node> selection;
    float ySpacing;
    float iconSpacingLeft;
    float iconSpacingRight;
    float padding;
    float indentSpacing;
    private float prefWidth;
    private float prefHeight;
    private boolean sizeInvalid;
    private Node foundNode;
    Node overNode;
    Node rangeStart;
    private ClickListener clickListener;
    
    public Tree(final Skin skin) {
        this(skin.get(TreeStyle.class));
    }
    
    public Tree(final Skin skin, final String styleName) {
        this(skin.get(styleName, TreeStyle.class));
    }
    
    public Tree(final TreeStyle style) {
        this.rootNodes = new Array<Node>();
        this.ySpacing = 4.0f;
        this.iconSpacingLeft = 2.0f;
        this.iconSpacingRight = 2.0f;
        this.padding = 0.0f;
        this.sizeInvalid = true;
        (this.selection = new Selection<Node>() {
            @Override
            protected void changed() {
                switch (this.size()) {
                    case 0: {
                        Tree.this.rangeStart = null;
                        break;
                    }
                    case 1: {
                        Tree.this.rangeStart = this.first();
                        break;
                    }
                }
            }
        }).setActor(this);
        this.selection.setMultiple(true);
        this.setStyle(style);
        this.initialize();
    }
    
    private void initialize() {
        this.addListener(this.clickListener = new ClickListener() {
            @Override
            public void clicked(final InputEvent event, final float x, final float y) {
                final Node node = Tree.this.getNodeAt(y);
                if (node == null) {
                    return;
                }
                if (node != Tree.this.getNodeAt(this.getTouchDownY())) {
                    return;
                }
                if (Tree.this.selection.getMultiple() && Tree.this.selection.hasItems() && UIUtils.shift()) {
                    if (Tree.this.rangeStart == null) {
                        Tree.this.rangeStart = node;
                    }
                    final Node rangeStart = Tree.this.rangeStart;
                    if (!UIUtils.ctrl()) {
                        Tree.this.selection.clear();
                    }
                    final float start = rangeStart.actor.getY();
                    final float end = node.actor.getY();
                    if (start > end) {
                        Tree.this.selectNodes(Tree.this.rootNodes, end, start);
                    }
                    else {
                        Tree.this.selectNodes(Tree.this.rootNodes, start, end);
                        Tree.this.selection.items().orderedItems().reverse();
                    }
                    Tree.this.selection.fireChangeEvent();
                    Tree.this.rangeStart = rangeStart;
                    return;
                }
                if (node.children.size > 0 && (!Tree.this.selection.getMultiple() || !UIUtils.ctrl())) {
                    float rowX = node.actor.getX();
                    if (node.icon != null) {
                        rowX -= Tree.this.iconSpacingRight + node.icon.getMinWidth();
                    }
                    if (x < rowX) {
                        node.setExpanded(!node.expanded);
                        return;
                    }
                }
                if (!node.isSelectable()) {
                    return;
                }
                Tree.this.selection.choose(node);
                if (!Tree.this.selection.isEmpty()) {
                    Tree.this.rangeStart = node;
                }
            }
            
            @Override
            public boolean mouseMoved(final InputEvent event, final float x, final float y) {
                Tree.this.setOverNode(Tree.this.getNodeAt(y));
                return false;
            }
            
            @Override
            public void exit(final InputEvent event, final float x, final float y, final int pointer, final Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                if (toActor == null || !toActor.isDescendantOf(Tree.this)) {
                    Tree.this.setOverNode(null);
                }
            }
        });
    }
    
    public void setStyle(final TreeStyle style) {
        this.style = style;
        if (this.indentSpacing == 0.0f) {
            this.indentSpacing = Math.max(style.plus.getMinWidth(), style.minus.getMinWidth());
        }
    }
    
    public void add(final Node node) {
        this.insert(this.rootNodes.size, node);
    }
    
    public void insert(final int index, final Node node) {
        this.remove(node);
        node.parent = null;
        this.rootNodes.insert(index, node);
        node.addToTree(this);
        this.invalidateHierarchy();
    }
    
    public void remove(final Node node) {
        if (node.parent != null) {
            node.parent.remove(node);
            return;
        }
        this.rootNodes.removeValue(node, true);
        node.removeFromTree(this);
        this.invalidateHierarchy();
    }
    
    @Override
    public void clearChildren() {
        super.clearChildren();
        this.setOverNode(null);
        this.rootNodes.clear();
        this.selection.clear();
    }
    
    public Array<Node> getNodes() {
        return this.rootNodes;
    }
    
    @Override
    public void invalidate() {
        super.invalidate();
        this.sizeInvalid = true;
    }
    
    private void computeSize() {
        this.sizeInvalid = false;
        this.prefWidth = this.style.plus.getMinWidth();
        this.prefWidth = Math.max(this.prefWidth, this.style.minus.getMinWidth());
        this.prefHeight = this.getHeight();
        final float plusMinusWidth = Math.max(this.style.plus.getMinWidth(), this.style.minus.getMinWidth());
        this.computeSize(this.rootNodes, this.indentSpacing, plusMinusWidth);
        this.prefWidth += this.padding * 2.0f;
        this.prefHeight = this.getHeight() - this.prefHeight;
    }
    
    private void computeSize(final Array<Node> nodes, final float indent, final float plusMinusWidth) {
        final float ySpacing = this.ySpacing;
        final float spacing = this.iconSpacingLeft + this.iconSpacingRight;
        for (int i = 0, n = nodes.size; i < n; ++i) {
            final Node node = nodes.get(i);
            float rowWidth = indent + plusMinusWidth;
            final Actor actor = node.actor;
            if (actor instanceof Layout) {
                final Layout layout = (Layout)actor;
                rowWidth += layout.getPrefWidth();
                node.height = layout.getPrefHeight();
                layout.pack();
            }
            else {
                rowWidth += actor.getWidth();
                node.height = actor.getHeight();
            }
            if (node.icon != null) {
                rowWidth += spacing + node.icon.getMinWidth();
                node.height = Math.max(node.height, node.icon.getMinHeight());
            }
            this.prefWidth = Math.max(this.prefWidth, rowWidth);
            this.prefHeight -= node.height + ySpacing;
            if (node.expanded) {
                this.computeSize(node.children, indent + this.indentSpacing, plusMinusWidth);
            }
        }
    }
    
    @Override
    public void layout() {
        if (this.sizeInvalid) {
            this.computeSize();
        }
        final float plusMinusWidth = Math.max(this.style.plus.getMinWidth(), this.style.minus.getMinWidth());
        this.layout(this.rootNodes, this.padding, this.getHeight() - this.ySpacing / 2.0f, plusMinusWidth);
    }
    
    private float layout(final Array<Node> nodes, final float indent, float y, final float plusMinusWidth) {
        final float ySpacing = this.ySpacing;
        final float spacing = this.iconSpacingLeft + this.iconSpacingRight;
        for (int i = 0, n = nodes.size; i < n; ++i) {
            final Node node = nodes.get(i);
            float x = indent + plusMinusWidth;
            if (node.icon != null) {
                x += spacing + node.icon.getMinWidth();
            }
            y -= node.getHeight();
            node.actor.setPosition(x, y);
            y -= ySpacing;
            if (node.expanded) {
                y = this.layout(node.children, indent + this.indentSpacing, y, plusMinusWidth);
            }
        }
        return y;
    }
    
    @Override
    public void draw(final Batch batch, final float parentAlpha) {
        this.drawBackground(batch, parentAlpha);
        final Color color = this.getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        final float plusMinusWidth = Math.max(this.style.plus.getMinWidth(), this.style.minus.getMinWidth());
        this.draw(batch, this.rootNodes, this.padding, plusMinusWidth);
        super.draw(batch, parentAlpha);
    }
    
    protected void drawBackground(final Batch batch, final float parentAlpha) {
        if (this.style.background != null) {
            final Color color = this.getColor();
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
            this.style.background.draw(batch, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        }
    }
    
    private void draw(final Batch batch, final Array<Node> nodes, final float indent, final float plusMinusWidth) {
        final Drawable plus = this.style.plus;
        final Drawable minus = this.style.minus;
        final float x = this.getX();
        final float y = this.getY();
        final float expandX = x + indent;
        final float iconX = expandX + plusMinusWidth + this.iconSpacingLeft;
        for (int i = 0, n = nodes.size; i < n; ++i) {
            final Node node = nodes.get(i);
            final float height = node.height;
            final Actor actor = node.actor;
            if (this.selection.contains(node) && this.style.selection != null) {
                this.style.selection.draw(batch, x, y + actor.getY() - this.ySpacing / 2.0f, this.getWidth(), height + this.ySpacing);
            }
            else if (node == this.overNode && this.style.over != null) {
                this.style.over.draw(batch, x, y + actor.getY() - this.ySpacing / 2.0f, this.getWidth(), height + this.ySpacing);
            }
            if (node.icon != null) {
                final float iconY = y + actor.getY() + Math.round((height - node.icon.getMinHeight()) / 2.0f);
                batch.setColor(actor.getColor());
                node.icon.draw(batch, iconX, iconY, node.icon.getMinWidth(), node.icon.getMinHeight());
                batch.setColor(Color.WHITE);
            }
            if (node.children.size != 0) {
                final Drawable expandIcon = node.expanded ? minus : plus;
                final float iconY2 = y + actor.getY() + Math.round((height - expandIcon.getMinHeight()) / 2.0f);
                expandIcon.draw(batch, expandX, iconY2, expandIcon.getMinWidth(), expandIcon.getMinHeight());
                if (node.expanded) {
                    this.draw(batch, node.children, indent + this.indentSpacing, plusMinusWidth);
                }
            }
        }
    }
    
    public Node getNodeAt(final float y) {
        this.foundNode = null;
        this.getNodeAt(this.rootNodes, y, this.getHeight());
        return this.foundNode;
    }
    
    private float getNodeAt(final Array<Node> nodes, final float y, float rowY) {
        for (int i = 0, n = nodes.size; i < n; ++i) {
            final Node node = nodes.get(i);
            final float height = node.height;
            rowY -= node.getHeight() - height;
            if (y >= rowY - height - this.ySpacing && y < rowY) {
                this.foundNode = node;
                return -1.0f;
            }
            rowY -= height + this.ySpacing;
            if (node.expanded) {
                rowY = this.getNodeAt(node.children, y, rowY);
                if (rowY == -1.0f) {
                    return -1.0f;
                }
            }
        }
        return rowY;
    }
    
    void selectNodes(final Array<Node> nodes, final float low, final float high) {
        for (int i = 0, n = nodes.size; i < n; ++i) {
            final Node node = nodes.get(i);
            if (node.actor.getY() < low) {
                break;
            }
            if (node.isSelectable()) {
                if (node.actor.getY() <= high) {
                    this.selection.add(node);
                }
                if (node.expanded) {
                    this.selectNodes(node.children, low, high);
                }
            }
        }
    }
    
    public Selection<Node> getSelection() {
        return this.selection;
    }
    
    public TreeStyle getStyle() {
        return this.style;
    }
    
    public Array<Node> getRootNodes() {
        return this.rootNodes;
    }
    
    public Node getOverNode() {
        return this.overNode;
    }
    
    public Object getOverObject() {
        if (this.overNode == null) {
            return null;
        }
        return this.overNode.getObject();
    }
    
    public void setOverNode(final Node overNode) {
        this.overNode = overNode;
    }
    
    public void setPadding(final float padding) {
        this.padding = padding;
    }
    
    public void setIndentSpacing(final float indentSpacing) {
        this.indentSpacing = indentSpacing;
    }
    
    public float getIndentSpacing() {
        return this.indentSpacing;
    }
    
    public void setYSpacing(final float ySpacing) {
        this.ySpacing = ySpacing;
    }
    
    public float getYSpacing() {
        return this.ySpacing;
    }
    
    public void setIconSpacing(final float left, final float right) {
        this.iconSpacingLeft = left;
        this.iconSpacingRight = right;
    }
    
    @Override
    public float getPrefWidth() {
        if (this.sizeInvalid) {
            this.computeSize();
        }
        return this.prefWidth;
    }
    
    @Override
    public float getPrefHeight() {
        if (this.sizeInvalid) {
            this.computeSize();
        }
        return this.prefHeight;
    }
    
    public void findExpandedObjects(final Array objects) {
        findExpandedObjects(this.rootNodes, objects);
    }
    
    public void restoreExpandedObjects(final Array objects) {
        for (int i = 0, n = objects.size; i < n; ++i) {
            final Node node = this.findNode(objects.get(i));
            if (node != null) {
                node.setExpanded(true);
                node.expandTo();
            }
        }
    }
    
    static boolean findExpandedObjects(final Array<Node> nodes, final Array objects) {
        final boolean expanded = false;
        for (int i = 0, n = nodes.size; i < n; ++i) {
            final Node node = nodes.get(i);
            if (node.expanded && !findExpandedObjects(node.children, objects)) {
                objects.add(node.object);
            }
        }
        return expanded;
    }
    
    public Node findNode(final Object object) {
        if (object == null) {
            throw new IllegalArgumentException("object cannot be null.");
        }
        return findNode(this.rootNodes, object);
    }
    
    static Node findNode(final Array<Node> nodes, final Object object) {
        for (int i = 0, n = nodes.size; i < n; ++i) {
            final Node node = nodes.get(i);
            if (object.equals(node.object)) {
                return node;
            }
        }
        for (int i = 0, n = nodes.size; i < n; ++i) {
            final Node node = nodes.get(i);
            final Node found = findNode(node.children, object);
            if (found != null) {
                return found;
            }
        }
        return null;
    }
    
    public void collapseAll() {
        collapseAll(this.rootNodes);
    }
    
    static void collapseAll(final Array<Node> nodes) {
        for (int i = 0, n = nodes.size; i < n; ++i) {
            final Node node = nodes.get(i);
            node.setExpanded(false);
            collapseAll(node.children);
        }
    }
    
    public void expandAll() {
        expandAll(this.rootNodes);
    }
    
    static void expandAll(final Array<Node> nodes) {
        for (int i = 0, n = nodes.size; i < n; ++i) {
            nodes.get(i).expandAll();
        }
    }
    
    public ClickListener getClickListener() {
        return this.clickListener;
    }
    
    public static class Node
    {
        final Actor actor;
        Node parent;
        final Array<Node> children;
        boolean selectable;
        boolean expanded;
        Drawable icon;
        float height;
        Object object;
        
        public Node(final Actor actor) {
            this.children = new Array<Node>(0);
            this.selectable = true;
            if (actor == null) {
                throw new IllegalArgumentException("actor cannot be null.");
            }
            this.actor = actor;
        }
        
        public void setExpanded(final boolean expanded) {
            if (expanded == this.expanded) {
                return;
            }
            this.expanded = expanded;
            if (this.children.size == 0) {
                return;
            }
            final Tree tree = this.getTree();
            if (tree == null) {
                return;
            }
            if (expanded) {
                for (int i = 0, n = this.children.size; i < n; ++i) {
                    this.children.get(i).addToTree(tree);
                }
            }
            else {
                for (int i = this.children.size - 1; i >= 0; --i) {
                    this.children.get(i).removeFromTree(tree);
                }
            }
            tree.invalidateHierarchy();
        }
        
        protected void addToTree(final Tree tree) {
            tree.addActor(this.actor);
            if (!this.expanded) {
                return;
            }
            final Object[] children = this.children.items;
            for (int i = this.children.size - 1; i >= 0; --i) {
                ((Node)children[i]).addToTree(tree);
            }
        }
        
        protected void removeFromTree(final Tree tree) {
            tree.removeActor(this.actor);
            if (!this.expanded) {
                return;
            }
            final Object[] children = this.children.items;
            for (int i = this.children.size - 1; i >= 0; --i) {
                ((Node)children[i]).removeFromTree(tree);
            }
        }
        
        public void add(final Node node) {
            this.insert(this.children.size, node);
        }
        
        public void addAll(final Array<Node> nodes) {
            for (int i = 0, n = nodes.size; i < n; ++i) {
                this.insert(this.children.size, nodes.get(i));
            }
        }
        
        public void insert(final int index, final Node node) {
            node.parent = this;
            this.children.insert(index, node);
            this.updateChildren();
        }
        
        public void remove() {
            final Tree tree = this.getTree();
            if (tree != null) {
                tree.remove(this);
            }
            else if (this.parent != null) {
                this.parent.remove(this);
            }
        }
        
        public void remove(final Node node) {
            this.children.removeValue(node, true);
            if (!this.expanded) {
                return;
            }
            final Tree tree = this.getTree();
            if (tree == null) {
                return;
            }
            node.removeFromTree(tree);
            if (this.children.size == 0) {
                this.expanded = false;
            }
        }
        
        public void removeAll() {
            final Tree tree = this.getTree();
            if (tree != null) {
                final Object[] children = this.children.items;
                for (int i = this.children.size - 1; i >= 0; --i) {
                    ((Node)children[i]).removeFromTree(tree);
                }
            }
            this.children.clear();
        }
        
        public Tree getTree() {
            final Group parent = this.actor.getParent();
            if (!(parent instanceof Tree)) {
                return null;
            }
            return (Tree)parent;
        }
        
        public Actor getActor() {
            return this.actor;
        }
        
        public boolean isExpanded() {
            return this.expanded;
        }
        
        public Array<Node> getChildren() {
            return this.children;
        }
        
        public void updateChildren() {
            if (!this.expanded) {
                return;
            }
            final Tree tree = this.getTree();
            if (tree == null) {
                return;
            }
            for (int i = this.children.size - 1; i >= 0; --i) {
                this.children.get(i).removeFromTree(tree);
            }
            for (int i = 0, n = this.children.size; i < n; ++i) {
                this.children.get(i).addToTree(tree);
            }
        }
        
        public Node getParent() {
            return this.parent;
        }
        
        public void setIcon(final Drawable icon) {
            this.icon = icon;
        }
        
        public Object getObject() {
            return this.object;
        }
        
        public void setObject(final Object object) {
            this.object = object;
        }
        
        public Drawable getIcon() {
            return this.icon;
        }
        
        public int getLevel() {
            int level = 0;
            Node current = this;
            do {
                ++level;
                current = current.getParent();
            } while (current != null);
            return level;
        }
        
        public Node findNode(final Object object) {
            if (object == null) {
                throw new IllegalArgumentException("object cannot be null.");
            }
            if (object.equals(this.object)) {
                return this;
            }
            return Tree.findNode(this.children, object);
        }
        
        public void collapseAll() {
            this.setExpanded(false);
            Tree.collapseAll(this.children);
        }
        
        public void expandAll() {
            this.setExpanded(true);
            if (this.children.size > 0) {
                Tree.expandAll(this.children);
            }
        }
        
        public void expandTo() {
            for (Node node = this.parent; node != null; node = node.parent) {
                node.setExpanded(true);
            }
        }
        
        public boolean isSelectable() {
            return this.selectable;
        }
        
        public void setSelectable(final boolean selectable) {
            this.selectable = selectable;
        }
        
        public void findExpandedObjects(final Array objects) {
            if (this.expanded && !Tree.findExpandedObjects(this.children, objects)) {
                objects.add(this.object);
            }
        }
        
        public void restoreExpandedObjects(final Array objects) {
            for (int i = 0, n = objects.size; i < n; ++i) {
                final Node node = this.findNode(objects.get(i));
                if (node != null) {
                    node.setExpanded(true);
                    node.expandTo();
                }
            }
        }
        
        public float getHeight() {
            return this.height;
        }
    }
    
    public static class TreeStyle
    {
        public Drawable plus;
        public Drawable minus;
        public Drawable over;
        public Drawable selection;
        public Drawable background;
        
        public TreeStyle() {
        }
        
        public TreeStyle(final Drawable plus, final Drawable minus, final Drawable selection) {
            this.plus = plus;
            this.minus = minus;
            this.selection = selection;
        }
        
        public TreeStyle(final TreeStyle style) {
            this.plus = style.plus;
            this.minus = style.minus;
            this.selection = style.selection;
        }
    }
}
