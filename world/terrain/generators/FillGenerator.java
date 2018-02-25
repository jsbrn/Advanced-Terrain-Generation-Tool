/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world.terrain.generators;

import world.World;
import world.terrain.Generator;

/**
 *
 * @author Joseph
 */
public class FillGenerator extends Generator{
    
    //simple generator that just fills the world with tiles of set attribute "tile"
    @Override
    public void generate(World w){
        int t = Integer.parseInt(getParameter("tile"));
        for(int i = 0;i<w.columns();i++){
            for(int j = 0; j<w.rows();j++){
                w.setTile(t, i, j);
            }
        }
    }
}
