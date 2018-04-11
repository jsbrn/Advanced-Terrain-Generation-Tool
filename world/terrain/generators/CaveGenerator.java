/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world.terrain.generators;

import java.util.Random;
import world.World;
import world.terrain.Generator;

/**
 *
 * @author Swaggert
 */
// Source: http://roguebasin.roguelikedevelopment.org/index.php?title=Basic_directional_dungeon_generation
public class CaveGenerator extends Generator {
    
    public CaveGenerator() {
        super();
        this.setParameter("roughness", "0.1");
        this.setParameter("windyness", "4");
        this.setParameter("thiccness", "8");
    }
    
    
   @Override
   public void generate(World w, int layer) {
       
        // Variables
        float roughness = Float.parseFloat(getParameter("roughness"));
        float windyness = Float.parseFloat(getParameter("windyness"));
        
        int[] diceRoll = {-2, -1, 1};
        int[] diceRoll2 = { -2, -1, 1, 2 };
        Random random = new Random(getSeed());
        int maxIteration = 5;
        
        
        for (int a = 0; a < maxIteration; a++) 
        {
            int width = Integer.parseInt(getParameter("thiccness"));
            // Pick random position to start the cave near the bottom
            int x = (a+1) * (w.columns() / maxIteration) + (random.nextInt() % (w.columns() / maxIteration));
            int y = w.rows() - (2 + random.nextInt() % 5);

            // Create cave until the surface is reached
            while (w.getTile(x, y, layer) != -1 || y >= 0) {
            
                // Draw current layer
                for (int i = 0; i < width; i++) {
                    if (x+i < w.columns())
                            w.setTile(x+i, y, layer, false);
                }
            
                // Move y up in relation to the world
                y--;
            
                // Randomize width
                if (random.nextFloat() < roughness) {
                    int pick = random.nextInt(diceRoll.length);
                    width += diceRoll[pick];
                }
            
                // Randomize windyness
                if (random.nextFloat() < windyness) {
                    int pick = random.nextInt(3 - 0 + 1) + 0;
                    x += diceRoll2[pick];
                }
            
                // Reposition windyness if too extreme
                if (x < 0)
                    x = 0;
                if (x+width > w.columns())
                    x = w.columns() - width;
            }
       
        }
    }
}
