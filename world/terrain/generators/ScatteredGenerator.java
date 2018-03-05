package world.terrain.generators;

import misc.MiscMath;
import world.World;
import world.terrain.Generator;
import world.terrain.misc.DiamondSquare;
import world.terrain.misc.Perlin;

/**
 * Creates a scattered plot of tiles using the DiamondSquare algorithm.
 * @author Ryan Dykstra (Classified1)
 * @see DiamondSquare
 */
public class ScatteredGenerator extends Generator {

    public ScatteredGenerator() {
        this.setParameter("amount", "0.5");
        this.setParameter("min", "0");
        this.setParameter("max", "1");
    }

    @Override
   public void generate(World w, int layer) {
       
        float amount = 1f - Float.parseFloat(getParameter("amount"));
        float min = Float.parseFloat(getParameter("min"));
        float max = Float.parseFloat(getParameter("max"));
       
        Perlin perlin = new Perlin();
        // Use PerlinNoise algorithm in other location
        // 6 is a random value, I don't know what the best value would be
        float[][] whitenoise = perlin.generateWhiteNoise(w.columns(), w.rows());
        float[][] map = perlin.generatePerlinNoise(whitenoise, 6);

        for (int i = 0; i < w.columns(); i++) {
            for (int j = 0; j < w.rows(); j++) {
                if (i >= map.length || j >= map.length) {
                    continue;
                }
                float prob = map[i][j];
                if (prob < amount) {
                    continue;
                } else {
                    float rand = (float) Math.random();
                    rand *= (amount-1);
                    prob = (min - max)*prob - min + max*amount;
                    if (rand > prob) w.setTile(i, j, layer, true);
                }
            }
        }
    }

}