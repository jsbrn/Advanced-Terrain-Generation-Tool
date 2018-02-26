package world.terrain.generators;

import gui.Canvas;
import java.awt.Color;
import java.awt.image.BufferedImage;
import misc.MiscMath;
import world.World;
import world.terrain.Generator;
import world.terrain.misc.DiamondSquare;

public class NoiseMapGenerator extends Generator {

    @Override
    public void generate(World w, int layer) {
        int s = Integer.parseInt(getParameter("size"));
        DiamondSquare ds = new DiamondSquare(s == 0 ? 0 : 32 - Integer.numberOfLeadingZeros(s - 1));
        float[][] dsmap = ds.getMap();
        for (int i = 0; i < w.columns(); i++) {
            for (int j = 0; j < w.rows(); j++) {
                if (i >= dsmap.length || j >= dsmap.length) continue;
                if (dsmap[i][j] > .5) w.setTile(i, j, layer, (Integer)w.getLayerProperty("tile", layer));
            }
        }
    }
    
}