package world.terrain.generators;

import gui.Canvas;
import java.util.Random;
import world.World;
import world.terrain.Generator;

public class FlatGenerator extends Generator {
    
    public FlatGenerator() { }
    
    @Override
    public void generate(World w, int layer) {
        for (int i = 0; i < w.columns(); i++) {
            for (int j = 0; j < w.rows(); j++) {
                if (j < w.rows() - Integer.parseInt(getParameter("height"))) continue;
                int t = (Integer)w.getLayerProperty("tile", layer);
                int tcount = w.getTileCount();
                w.setTile(i, j, layer, true);
            }
        }
    }

}
