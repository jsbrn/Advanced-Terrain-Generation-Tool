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
 * Create lakes on 2D landscapes, and even in 2D caves
 * @author Swaggert
 */
public class TwoDimensionalLakeGenerator extends Generator {
    
    public TwoDimensionalLakeGenerator() {
        super();
    }
    
    
    @Override
    public void generate(World w, int layer) {
        
        // Data structures
        LinkedList<Integer> tilePositionX = new LinkedList();
        LinkedList<Integer> tilePositionY = new LinkedList();
        
        int sLakeX = Canvas.getCamera()[0];
        int sLakeY = Canvas.getCamera()[1];
        
        tilePositionX.add(sLakeX/16);
        tilePositionY.add(sLakeY/16);
        
        System.out.println(tilePositionX.getLast());
        System.out.println(tilePositionY.getLast());
        
        while (!tilePositionX.isEmpty() && !tilePositionY.isEmpty()) {
            
            int x = tilePositionX.getLast();
            int y = tilePositionY.getLast();
            
            System.out.println(tilePositionX.getLast());
            System.out.println(tilePositionY.getLast());
            
            // Draw tile at current position
            w.setTile(x, y, layer, true);
        
            // Recursive calls, function call stored on the memory stack
            // until function returns to this function call
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
        
            tilePositionX.removeLast();
            tilePositionY.removeLast();
            
        }
    }
}