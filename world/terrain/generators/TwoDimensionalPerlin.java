/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world.terrain.generators;

import java.util.Random;
import world.terrain.misc.PerlinNoise;
import world.World;
import world.terrain.Generator;
/**
 * Creates side scrolling terrain based on the interference of Perlin Noise algorithm.
 * @author Swaggert
 */
public class TwoDimensionalPerlin extends Generator {
    
    
    public TwoDimensionalPerlin() {
        super();
    }

    @Override
   public void generate(World w, int layer) {
       
        PerlinNoise perlin = new PerlinNoise();
        // Use PerlinNoise algorithm in other location
        // 6 is a random value, I don't know what the best value would be
        float[][] map = World.getWorld().createHeightmap("Perlin", getSeed(), false);
        
        // Pick random row from Perlin map
        // Absolute value function prevent array out of bounds exceptions
        Random random = World.getWorld().rng();
        int pRow = Math.abs(random.nextInt() % map[0].length);
            
        
        // For each column to draw
        for (int i = 0; i < w.columns(); i++) {
            
            // Get height value at column index
            int yStart = (int)Math.floor( (map[i][pRow]) * w.rows() );
                
            // Print tiles below start tile
            while (yStart < w.rows()) {
                
                // Draw tiles for that column
                w.setTile(i, yStart, layer, true);
                yStart++;
            }
         
        }
   }
    
}
