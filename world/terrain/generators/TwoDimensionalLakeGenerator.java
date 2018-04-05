/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world.terrain.generators;

import world.World;
import world.terrain.Generator;

/**
 * Create lakes on 2D landscapes, and even in 2D caves
 * @author Swaggert
 */
public class TwoDimensionalLakeGenerator extends Generator {
    
    public TwoDimensionalLakeGenerator() {
        super();
        this.setParameter("sLakeX", "0.5");
        this.setParameter("sLakeY", "0.3");
    }
    
    
    /**
     * Draws a lake below passed in x, y coordinate contained by the layer below. 
     * Does this through recursive calls.
     * @param w Current generator world.
     * @param layer Current layer being generated.
     * @param x Current x-coordinate on map.
     * @param y Current y-coordinate on map.
     */
    public void recursiveDraw(World w, int layer, int x, int y) {
        
        // Draw tile at current position
        w.setTile(x, y, layer, true);
        
        
        // Recursive calls, function call stored on the memory stack
        // until function returns to this function call
        // Try to draw tile to the right
        if (x+1 < w.columns())
            if (w.getTile(x+1, y) == -1)
                recursiveDraw(w, layer, x+1, y);
        
        // Try to draw tile to the bottom
        if (y+1 < w.rows())
            if (w.getTile(x, y+1) == -1)
                recursiveDraw(w, layer, x, y+1);
        
        // Try to draw tile to the left
        if (x-1 >= 0)
            if (w.getTile(x-1, y) == -1)
                recursiveDraw(w, layer, x-1, y);
        
    }
    
    @Override
    public void generate(World w, int layer) {
        
        
        int sLakeX = (int)Math.floor(Float.parseFloat(getParameter("sLakeX")) * w.columns());
        int sLakeY = (int)Math.floor(Float.parseFloat(getParameter("sLakeY")) * w.rows());
        
        recursiveDraw(w, layer, sLakeX, sLakeY);
    }
}