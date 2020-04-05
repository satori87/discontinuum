// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc.aistuff;

import java.io.IOException;
import java.io.FileNotFoundException;
import com.esotericsoftware.kryo.io.Output;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import com.bbg.dc.DCGame;
import java.util.ArrayList;

public class MapGraph
{
    public ArrayList<MapNode> mapNodes;
    
    public MapGraph() {
        this.mapNodes = new ArrayList<MapNode>(4096);
        for (int x = 0; x < 64; ++x) {
            for (int y = 0; y < 64; ++y) {
                this.mapNodes.add(new MapNode(x * 64 + y, x, y));
            }
        }
    }
    
    public void save(final DCGame game, final int id) {
        try {
            final FileOutputStream f = new FileOutputStream(new File("maps/mapgraph" + id));
            final OutputStream outputStream = new DeflaterOutputStream(f);
            final Output output = new Output(outputStream);
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
}
