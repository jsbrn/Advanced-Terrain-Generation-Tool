package world;

import gui.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import world.generation.Generator;

public class World {

    private static World world;
    private int[][] terrain;
    private int[] tile_dims;
    private BufferedImage spritesheet;
    
    public static void newWorld(int w, int h) { world = new World(w, h); Canvas.refresh(); }
    public static World getWorld() { return world; }
    
    private World(int w, int h) {
        this.terrain = new int[w][h];
        this.tile_dims = new int[]{16, 16};
    }
    
    public void generate(Generator g) { g.generate(this); }
    public void setTile(int tile, int x, int y) { terrain[x][y] = tile; }
    public int getTile(int x, int y) { return terrain[x][y]; }
    
    public void setSpritesheet(File sprite) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(sprite);
            spritesheet = img;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public int width() { return terrain.length; }
    public int height() { return terrain.length > 0 ? terrain[0].length : 0; }
    
    public void draw(Graphics g) {
        g.setColor(Color.white);
        if (spritesheet == null) { g.drawString("No tile spritesheet found!", 15, 25); return; }
        for (int x = 0; x < terrain.length; x++) {
            for (int y = 0; y < terrain[0].length; y++) {
                if (terrain[x][y] < 0) return;
                g.drawImage(spritesheet.getSubimage(terrain[x][y]*tile_dims[0], 0, tile_dims[0], tile_dims[1]), x, y, null);
            }
        }
    }
    
}
