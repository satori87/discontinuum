// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc.iface;

import com.bbg.dc.AssetLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import com.badlogic.gdx.graphics.Color;
import java.util.List;
import com.bbg.dc.DCGame;

public class Dialog
{
    DCGame game;
    public int width;
    public int height;
    public int next;
    public boolean lead;
    public int nextTime;
    public int nextType;
    public int y;
    public int x;
    public boolean timed;
    public long timeStamp;
    public int time;
    public boolean done;
    public boolean active;
    public List<Button> choices;
    public Label msg;
    public Frame frame;
    public int id;
    public int pic;
    public int xO;
    public int yO;
    public String text;
    public String[] choicetext;
    public int[] choiceid;
    
    public Dialog(final DCGame game, final int id, final int nextType, final int next, final int nextTime, final int pic, final int width, final String text, final String[] choicetext, final int[] choiceid) {
        this.width = 0;
        this.height = 0;
        this.next = -1;
        this.lead = false;
        this.nextTime = 0;
        this.nextType = 0;
        this.y = 360;
        this.x = 540;
        this.timed = false;
        this.timeStamp = 0L;
        this.time = 0;
        this.done = false;
        this.active = false;
        this.id = 0;
        this.pic = 0;
        this.xO = 0;
        this.yO = 0;
        this.game = game;
        this.id = id;
        this.width = width;
        this.pic = pic;
        this.next = next;
        this.nextType = nextType;
        this.nextTime = nextTime;
        this.choiceid = choiceid;
        this.choicetext = choicetext;
        this.text = text;
        if (nextType < 0) {
            this.lead = false;
        }
        else {
            this.lead = true;
        }
    }
    
    public Dialog(final DCGame game, final int id, final int nextType, final int next, final int nextTime, final int pic, final int width, final String text) {
        this(game, id, nextType, next, nextTime, pic, width, text, new String[] { "Ok" }, new int[1]);
    }
    
    public void setchoices(final String[] choicetext, final int[] choiceid) {
        this.choicetext = choicetext;
        this.choiceid = choiceid;
    }
    
    public void lead(final int type, final int to, final int time) {
        this.next = to;
        this.nextTime = time;
        this.nextType = type;
        this.lead = true;
    }
    
    public void choose(final Button b) {
        this.game.scene.activeDialog = null;
        this.active = false;
        this.game.scene.buttonPressed(b.id);
        this.done = true;
        if (this.lead) {
            switch (this.nextType) {
                case 0: {
                    if (this.next < this.game.scene.dialogs.size()) {
                        this.game.scene.dialogs.get(this.next).timed = true;
                        this.game.scene.dialogs.get(this.next).timeStamp = this.game.tick + this.nextTime;
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    public void start(final long tick) {
        this.game.scene.activeDialog = this;
        this.active = true;
        if (this.pic < 100) {
            this.xO = 96;
            this.yO = 32;
            this.text = "\"" + this.text + "\"";
        }
        int textHeight = wrapText(2.0f, this.width - 40 - this.xO, this.text).size() * 30 + 32 + this.yO;
        if (textHeight < 176) {
            textHeight = 176;
        }
        this.height = textHeight + this.choicetext.length * 60;
        this.frame = new Frame(this.game, this.x, this.y, this.width, this.height - 2, true, true);
        this.msg = new Label(this.game, this.x - this.width / 2 + 8 + this.xO, this.y - this.height / 2 + 8 + 8 + this.yO, 2.0f, this.text, Color.WHITE, false);
        this.msg.wrapw = this.width - 32 - this.xO;
        this.msg.wrap = true;
        this.choices = new LinkedList<Button>();
        for (int i = 0; i < this.choicetext.length; ++i) {
            final Button b = new Button(this.game, this.choiceid[i], 1, this.x, this.y - this.height / 2 + textHeight + 24, this.width - 16, 48, this.choicetext[i]);
            b.dialog = true;
            this.choices.add(b);
        }
    }
    
    public void update(final long tick) {
        if (this.game.scene.inputLocked) {
            return;
        }
        for (final Button b : this.choices) {
            b.update(tick);
        }
    }
    
    public static List<String> wrapText(final float scale, final int width, final String text) {
        final List<String> lines = new ArrayList<String>();
        String line = "";
        String word = "";
        for (int c = 0; c < text.length(); ++c) {
            final String p = text.substring(c, c + 1);
            if (p.equals(" ")) {
                if (line.length() > 0) {
                    if (AssetLoader.getStringWidth(String.valueOf(line) + " " + word, scale, 0.0f, 1.0f) > width) {
                        lines.add(line);
                        line = word;
                        word = "";
                    }
                    else {
                        line = String.valueOf(line) + " " + word;
                        word = "";
                    }
                }
                else if (AssetLoader.getStringWidth(word, scale, 0.0f, 1.0f) > width) {
                    line = String.valueOf(word) + " ";
                    word = "";
                }
                else {
                    line = word;
                    word = "";
                }
            }
            else if (line.length() == 0 && AssetLoader.getStringWidth(String.valueOf(word) + p, scale, 0.0f, 1.0f) > width) {
                lines.add(word);
                word = "";
                word = p;
            }
            else if (line.length() > 0 && AssetLoader.getStringWidth(String.valueOf(line) + " " + word + p, scale, 0.0f, 1.0f) > width) {
                lines.add(line);
                line = "";
                word = String.valueOf(word) + p;
            }
            else {
                word = String.valueOf(word) + p;
            }
        }
        if (word.length() > 0) {
            line = String.valueOf(line) + " " + word;
        }
        if (line.length() > 0) {
            lines.add(line);
        }
        return lines;
    }
    
    public void render() {
        this.frame.render();
        this.msg.render();
        if (this.pic < 100) {}
        for (final Button b : this.choices) {
            b.render();
        }
    }
}
