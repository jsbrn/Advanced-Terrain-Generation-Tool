/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world.terrain.misc;

/**
 *
 * @author dykst
 */
public class RadialGradient {

    float map[][];
    
    public RadialGradient(int width) {
        map = new float[width][width];
        float center = (float) (width/2.0);
        
        for (int y = 0; y < map.length; y++)
        {
            for (int x = 0; x < map.length; x++)
            {
                map[x][y] = -(float) Math.sqrt(Math.pow(x-center, 2) + Math.pow(y - center, 2));
            }
        }
        
        float max = map[(int) center][(int) center];
        float min = map[(int) center][0];
        
        for (int y = 0; y < map.length; y++)
        {
            for (int x = 0; x < map.length; x++)
            {
                map[x][y] = (map[x][y]-min)/(max-min);
                if (map[x][y] < 0)
                {
                    map[x][y] = 0;
                }
            }
        }
    }
    
    public float[][] getMap() { return map; }
    
}