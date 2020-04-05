// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc;

import com.badlogic.gdx.graphics.Color;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import com.esotericsoftware.kryo.io.Output;
import java.io.FileOutputStream;
import java.io.File;

public class MonsterData
{
    public Doll doll;
    public int id;
    public String name;
    public int maxHP;
    public int walkSpeed;
    public int sight;
    public int pursueMinRange;
    public int pursueMaxRange;
    public int attackSpeed;
    public int attackCoolDown;
    public int lightAlpha;
    public int lightDist;
    public int flee;
    public int wanderRange;
    public int combatRange;
    public int shadowSize;
    public int radius;
    public int mass;
    public boolean[] flags;
    
    public MonsterData() {
        this.id = 0;
        this.name = "Rockdude";
        this.maxHP = 100;
        this.walkSpeed = 20;
        this.sight = 5;
        this.pursueMinRange = 30;
        this.pursueMaxRange = 38;
        this.attackSpeed = 40;
        this.attackCoolDown = 400;
        this.lightAlpha = 0;
        this.lightDist = 0;
        this.flee = 25;
        this.wanderRange = 10;
        this.combatRange = 20;
        this.shadowSize = 5;
        this.radius = 10;
        this.mass = 150;
    }
    
    public MonsterData(final int i) {
        this.id = 0;
        this.name = "Rockdude";
        this.maxHP = 100;
        this.walkSpeed = 20;
        this.sight = 5;
        this.pursueMinRange = 30;
        this.pursueMaxRange = 38;
        this.attackSpeed = 40;
        this.attackCoolDown = 400;
        this.lightAlpha = 0;
        this.lightDist = 0;
        this.flee = 25;
        this.wanderRange = 10;
        this.combatRange = 20;
        this.shadowSize = 5;
        this.radius = 10;
        this.mass = 150;
        this.id = i;
        this.doll = new Doll();
        this.flags = new boolean[20];
    }
    
    public MonsterData copy() {
        final MonsterData md = new MonsterData(this.id);
        md.doll = this.doll.copy();
        md.name = this.name;
        md.maxHP = this.maxHP;
        md.walkSpeed = this.walkSpeed;
        md.attackCoolDown = this.attackCoolDown;
        md.attackSpeed = this.attackSpeed;
        md.lightAlpha = this.lightAlpha;
        md.lightDist = this.lightDist;
        md.pursueMaxRange = this.pursueMaxRange;
        md.pursueMinRange = this.pursueMinRange;
        md.sight = this.sight;
        md.flee = this.flee;
        md.wanderRange = this.wanderRange;
        md.combatRange = this.combatRange;
        md.shadowSize = this.shadowSize;
        md.radius = this.radius;
        for (int i = 0; i < 20; ++i) {
            md.flags[i] = this.flags[i];
        }
        return md;
    }
    
    public void save(final DCGame game) {
        try {
            final FileOutputStream f = new FileOutputStream(new File("monsters/mon" + this.id));
            final Output output = new Output((OutputStream)f);
            game.kryo.writeObject(output, (Object)this);
            output.close();
            f.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
    }
    
    private static Color getSlimeCol(final int r, final int g, final int b) {
        return new Color(r / 255.0f, g / 255.0f, b / 255.0f, 0.7f);
    }
    
    public static Color getSlimeColor(final int mon, final int m) {
        final Color c = Color.WHITE;
        if (mon == 11) {
            switch (m) {
                case 0: {
                    return getSlimeCol(94, 244, 0);
                }
                case 1: {
                    return getSlimeCol(174, 244, 0);
                }
                case 2: {
                    return getSlimeCol(244, 232, 0);
                }
                case 3: {
                    return getSlimeCol(244, 150, 0);
                }
                case 4: {
                    return getSlimeCol(244, 69, 0);
                }
                case 5: {
                    return getSlimeCol(244, 0, 12);
                }
                case 6: {
                    return getSlimeCol(244, 0, 93);
                }
                case 7: {
                    return getSlimeCol(244, 0, 173);
                }
                case 8: {
                    return getSlimeCol(233, 0, 244);
                }
                case 9: {
                    return getSlimeCol(152, 0, 244);
                }
                case 10: {
                    return getSlimeCol(71, 0, 244);
                }
                case 11: {
                    return getSlimeCol(0, 10, 244);
                }
                case 12: {
                    return getSlimeCol(0, 91, 244);
                }
                case 13: {
                    return getSlimeCol(0, 172, 244);
                }
                case 14: {
                    return getSlimeCol(0, 244, 234);
                }
                case 15: {
                    return getSlimeCol(0, 244, 152);
                }
                case 16: {
                    return getSlimeCol(0, 244, 71);
                }
                case 17: {
                    return getSlimeCol(10, 244, 0);
                }
            }
        }
        else if (mon == 12) {
            switch (m) {
                case 0: {
                    return getSlimeCol(16, 148, 82);
                }
                case 1: {
                    return getSlimeCol(16, 132, 148);
                }
                case 2: {
                    return getSlimeCol(16, 75, 148);
                }
                case 3: {
                    return getSlimeCol(89, 16, 148);
                }
                case 4: {
                    return getSlimeCol(148, 16, 136);
                }
                case 5: {
                    return getSlimeCol(148, 135, 16);
                }
                case 6: {
                    return getSlimeCol(148, 21, 16);
                }
                case 7: {
                    return getSlimeCol(31, 148, 16);
                }
                case 8: {
                    return getSlimeCol(42, 25, 50);
                }
            }
        }
        return c;
    }
    
    public static int getBloodType(final int mon, final int m) {
        switch (mon) {
            case 1: {
                return 4;
            }
            case 2: {
                return 2;
            }
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8: {
                return 6;
            }
            case 9: {
                switch (m) {
                    case 3:
                    case 4: {
                        return 8;
                    }
                    case 5: {
                        return 10;
                    }
                    case 7:
                    case 18: {
                        return 12;
                    }
                    case 15:
                    case 16:
                    case 17: {
                        return 14;
                    }
                    default: {
                        return 16;
                    }
                }
                break;
            }
            case 10:
            case 15: {
                return 16;
            }
            case 11:
            case 12: {
                return 18;
            }
            case 14:
            case 17: {
                return 20;
            }
            case 16: {
                return 22;
            }
            default: {
                return 0;
            }
        }
    }
}
