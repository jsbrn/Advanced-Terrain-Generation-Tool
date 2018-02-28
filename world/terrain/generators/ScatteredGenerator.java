package world.terrain.generators;

import misc.MiscMath;
import world.World;
import world.terrain.Generator;
import world.terrain.misc.DiamondSquare;

/**
 * Creates a scattered plot of tiles using the DiamondSquare algorithm.
 * @author Ryan Dykstra (Classified1)
 */
public class ScatteredGenerator extends Generator {

    public ScatteredGenerator() {
        this.setParameter("amount", "0.5");
        this.setParameter("min", "0");
        this.setParameter("max", "1");
    }

    @Override
   public void generate(World w, int layer) {
        int s = (int)MiscMath.max(World.getWorld().columns(), World.getWorld().rows());
        DiamondSquare ds = new DiamondSquare(s == 0 ? 0 : 32 - Integer.numberOfLeadingZeros(s - 1));
        float amount = 1-Float.parseFloat(getParameter("amount"));
        float min = Float.parseFloat(getParameter("min"));
        float max = Float.parseFloat(getParameter("max"));


        float[][] dsmap = ds.getMap();
        for (int i = 0; i < w.columns(); i++) {
            for (int j = 0; j < w.rows(); j++) {
                if (i >= dsmap.length || j >= dsmap.length) {
                    continue;
                }
                float prob = dsmap[i][j];
                if (prob < amount) {
                    continue;
                } else {
                    float rand = (float) Math.random();
                    rand *= (amount-1);
                    prob = (min - max)*prob - min + max*amount;
                    if (rand > prob) {
                        w.setTile(i, j, layer, true);
                    }
                }
            }
        }
    }

}