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
public class LinearGradient {

    float map[][];
    
    public LinearGradient(int width, int height) {
        map = new float[width][height];

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                map[x][y] = y / (float)height;
            }
        }
    }
    
    public float[][] getMap() { return map; }
}