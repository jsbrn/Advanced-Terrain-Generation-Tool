package world;

import java.awt.Graphics;
import world.generation.Generator;

public class World {

    private static World world;
    private int[] dimensions;
    private int[][] terrain;
    
    public static void newWorld(int w, int h) {
        world = new World(w, h);
    }
    
    private World(int w, int h) {
        this.dimensions = new int[]{w, h};
        this.terrain = new int[][]{};
    }
    
    public void generate(Generator g) { g.generate(this); }
    public void setTile(int tile, int x, int y) { terrain[x][y] = tile; }
    public int getTile(int x, int y) { return terrain[x][y]; }
    
    public int width() { return terrain.length; }
    public int height() { return terrain.length > 0 ? terrain[0].length : 0; }
    
    public void draw(Graphics g) {
        for (int x = 0; x < terrain.length; x++) {
            for (int y = 0; y < terrain[0].length; y++) {
                //g.drawImage(null, x, y, null);
            }
        }
    }
    
}
