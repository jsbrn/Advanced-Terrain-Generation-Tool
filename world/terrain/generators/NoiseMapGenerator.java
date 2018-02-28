package world.terrain.generators;

import gui.Canvas;
import java.awt.Color;
import java.awt.image.BufferedImage;
import misc.MiscMath;
import world.World;
import world.terrain.Generator;
import world.terrain.misc.DiamondSquare;

public class NoiseMapGenerator extends Generator {

    public NoiseMapGenerator() {
        this.setParameter("cutoff", "0.5");
    }
    
    @Override
    public void generate(World w, int layer) {
        int s = (int)MiscMath.max(World.getWorld().columns(), World.getWorld().rows());
        DiamondSquare ds = new DiamondSquare(s == 0 ? 0 : 32 - Integer.numberOfLeadingZeros(s - 1));
        float[][] dsmap = ds.getMap();
        for (int i = 0; i < w.columns(); i++) {
            for (int j = 0; j < w.rows(); j++) {
                if (i >= dsmap.length || j >= dsmap.length) continue;
                if (dsmap[i][j] > Double.parseDouble(getParameter("cutoff"))) w.setTile(i, j, layer, true);
            }
        }
    }
    
}