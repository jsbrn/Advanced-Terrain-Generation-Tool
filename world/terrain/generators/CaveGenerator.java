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
        this.setParameter("roughness", "0.4");
        this.setParameter("windyness", "0.4");
    }
    
    
     @Override
   public void generate(World w, int layer) {
       
        // Variables
        float roughness = Float.parseFloat(getParameter("roughness"));
        float windyness = Float.parseFloat(getParameter("windyness"));
        int[] diceRoll = { -3, -2, -1, 1, 2, 3 };
        int[] diceRoll2 = { -2, -1, 1, 2 };
        Random random = new Random(System.currentTimeMillis());
        int maxIteration = 5;
        
        
        for (int a = 0; a < maxIteration; a++) 
        {
            
            // Pick random position to start the cave near the bottom
            int x = (a+1) * (w.columns() / maxIteration) + (random.nextInt() % (w.columns() / maxIteration));
            int y = w.rows() - (2 + random.nextInt() % 5);
        
            // Pick out start width
            int width = 3;
       
            // Create cave until the surface is reached
            while (w.getTile(x, y) != -1 || y >= 0) {
            
                // Draw current layer
                for (int i = 0; i < width; i++) {
                    if (x+i < w.columns())
                        if (w.getTile(x+1, y) != -1)
                            w.setTile(x+i, y, layer, true);
                }
            
                // Move y up in relation to the world
                y--;
            
                // Randomize width
                if (random.nextFloat() < roughness) {
                    int pick = random.nextInt(5 - 0 + 1) + 0;
                    width += diceRoll[pick];
                }
            
                // Resize width if too extreme
                if (width < 3)
                    width = 3;
                if (width > (int)(w.columns() * 0.1))
                    width = (int)(w.columns() * 0.1);
            
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
