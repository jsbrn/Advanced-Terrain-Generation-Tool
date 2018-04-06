/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world.terrain.misc;

import java.util.ArrayList;
import world.World;
import world.terrain.Generator;

/**
 * Creates an outline of the specified layer, with selected thickness
 * @author Swaggert
 */
public class TileOutliner extends Generator {
    
    public TileOutliner() {
        super();
        this.setParameter("layerNumber", "0");
    }
    
    
    @Override
    public void generate(World w, int layer) {
        
        // Variables
        int layerNumber = Integer.parseInt(getParameter("layerNumber"));
        ArrayList<Integer> X = new ArrayList<>();
        ArrayList<Integer> Y = new ArrayList<>();
        
        // Check if tiles are edge tiles for that layer
        for (int i = 0; i < w.columns(); i++) {
            for (int j = 0; j < w.rows(); j++) {
                
                // Left check
                if (i-1 >= 0)
                    if (w.getTile(i-1, j) == layerNumber && w.getTile(i, j) != layerNumber) {
                        X.add(i-1); Y.add(j);
                        continue;
                    }
        
                // Right check
                if (i+1 < w.columns())
                    if (w.getTile(i+1, j) == layerNumber && w.getTile(i, j) != layerNumber) {
                        X.add(i+1); Y.add(j);
                        continue;
                    }
        
                // Bottom check
                if (j-1 >= 0)
                     if (w.getTile(i, j-1) == layerNumber && w.getTile(i, j) != layerNumber) {
                        X.add(i); Y.add(j-1);
                        continue;
                    }
                
                // Top check
                if (j+1 < w.rows())
                    if (w.getTile(i, j+1) == layerNumber && w.getTile(i, j) != layerNumber) {
                        X.add(i); Y.add(j+1);
                        continue;
                    }
                
                // Top-left check
                if (i-1 >= 0 && j+1 < w.rows())
                    if (w.getTile(i-1, j+1) == layerNumber && w.getTile(i, j) != layerNumber) {
                        X.add(i-1); Y.add(j+1);
                        continue;
                    }
        
                // Top-right check
                if (i+1 < w.columns() && j+1 < w.rows())
                    if (w.getTile(i+1, j+1) == layerNumber && w.getTile(i, j) != layerNumber) {
                        X.add(i+1); Y.add(j+1);
                        continue;
                    }
        
                // Bottom-left check
                if (i-1 >= 0 && j-1 >= 0)
                    if (w.getTile(i-1, j-1) == layerNumber && w.getTile(i, j) != layerNumber) {
                        X.add(i-1); Y.add(j-1);
                        continue;
                    }
                
                // Bottom-right check
                if (i+1 < w.columns() && j-1 >= 0)
                    if (w.getTile(i+1, j-1) == layerNumber && w.getTile(i, j) != layerNumber) {
                        X.add(i+1); Y.add(j-1);
                    }
            }
        }
        
        // Draw locations saved in arrays to draw
         for (int i = 0; i < X.size(); i++)
            w.setTile(X.get(i), Y.get(i), layer, true);
          
    }
    
}
