/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world.terrain.generators;

import world.World;
import world.terrain.Generator;

/**
 * A simple generator that fills the entire layer.
 * @author Joseph
 */
public class FillGenerator extends Generator {
    
    @Override
    public void generate(World w, int layer){
        for (int i = 0; i < w.columns(); i++){
            for (int j = 0; j < w.rows(); j++){
                w.setTile(i, j, layer, true);
            }
        }
    }
}
