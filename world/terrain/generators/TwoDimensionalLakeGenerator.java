/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world.terrain.generators;

import gui.Canvas;
import java.util.LinkedList;
import world.World;
import world.terrain.Generator;

/**
 * Create lakes on 2D landscapes, and even in 2D caves where no layers exist
 * @author Ryan Swaggert
 */
public class TwoDimensionalLakeGenerator extends Generator {
    
    public TwoDimensionalLakeGenerator() {
        super();
    }
    
    
    @Override
    public void generate(World w, int layer) {
        
        // Data structures to store coordinates to draw
        LinkedList<Integer> tilePositionX = new LinkedList();
        LinkedList<Integer> tilePositionY = new LinkedList();
        
        // Use camera position to get start x and y position
        int sLakeX = Canvas.getCamera()[0];
        int sLakeY = Canvas.getCamera()[1];
        
        // Divide to get into the world bounds
        // Convert from pixel coordinates to tile coordinates
        tilePositionX.add(sLakeX/16);
        tilePositionY.add(sLakeY/16);
        
        // While there are tiles in draw lists
        while (!tilePositionX.isEmpty() && !tilePositionY.isEmpty()) {
            
            // Get coordinate at the end of the lists
            int x = tilePositionX.getLast();
            int y = tilePositionY.getLast();
            
            // Draw tile at current position
            w.setTile(x, y, layer, true);
        
            // Try to draw tile to the right
            if (x+1 < w.columns())
                if (w.getTile(x+1, y) == -1) {
                    tilePositionX.addLast(x+1);
                    tilePositionY.addLast(y);
                    continue;
                }
                
        
            // Try to draw tile to the bottom
            if (y+1 < w.rows())
                if (w.getTile(x, y+1) == -1) {
                    tilePositionX.addLast(x);
                    tilePositionY.addLast(y+1);
                    continue;
                }
        
            // Try to draw tile to the left
            if (x-1 >= 0)
                if (w.getTile(x-1, y) == -1) {
                    tilePositionX.addLast(x-1);
                    tilePositionY.addLast(y);
                    continue;
                }
        
            // Remove tile position from the list if there are no adjacent tiles to draw
            tilePositionX.removeLast();
            tilePositionY.removeLast();
            
        }
    }
}