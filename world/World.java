package world;

import gui.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import world.generation.Generator;

public class World {

    private static World world;
    private int[][] terrain;
    private int[] tile_dims;
    private BufferedImage spritesheet;
    private static ArrayList<Image> textures;
    
    public static void newWorld(int w, int h) { world = new World(w, h); }
    public static World getWorld() { return world; }
    
    private World(int w, int h) {
        this.terrain = new int[w][h];
        this.tile_dims = new int[]{16, 16};
        this.clearTiles();
    }
    
    public void generate(Generator g) { g.generate(this); }
    public void setTile(int tile, int x, int y) { terrain[x][y] = tile; }
    public int getTile(int x, int y) { return terrain[x][y]; }
    public void clearTiles() {
        for (int x = 0; x < columns(); x++) {
            for (int y = 0; y < rows(); y++) {
                terrain[x][y] = -1;
            }
        }
    }
    public int getTileCount() { return textures.size(); }
    
    public void setSpritesheet(File sprite) {
        textures = new ArrayList<Image>();
        BufferedImage img = null;
        try {
            img = ImageIO.read(sprite);
            spritesheet = img;
            for (int i = 0; i < img.getWidth() / tile_dims[0]; i++) textures.add(spritesheet.getSubimage(i*tile_dims[0], 0, tile_dims[0], tile_dims[1]));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public int columns() { return terrain.length; }
    public int rows() { return terrain.length > 0 ? terrain[0].length : 0; }
    
    public int width() { return columns()*tile_dims[0]; }
    public int height() { return rows()*tile_dims[1]; }
    
    public int[] getWorldCoordinates(int x, int y) {
        int[] camera = Canvas.getCamera();
        return new int[]{x - camera[0], y - camera[1]};
    }
    
    public int[] getOnscreenCoordinates(double x, double y) {
        int tx = ((int)x)*tile_dims[0], ty = ((int)y)*tile_dims[1];
        int[] camera = Canvas.getCamera();
        int[] canvas_dims = Canvas.getDimensions();
        return new int[]{(canvas_dims[0]/2) - camera[0] + tx, 
            (canvas_dims[1]/2) - camera[1] + ty};
    }
    
    public void draw(Graphics g) {
        g.setColor(Color.white);
        if (spritesheet == null) { g.drawString("No tile spritesheet found!", 15, 25); return; }
        for (int x = 0; x < terrain.length; x++) {
            for (int y = 0; y < terrain[0].length; y++) {
                if (terrain[x][y] < 0) continue;
                int osc[] = getOnscreenCoordinates(x, y);
                g.drawImage(textures.get(terrain[x][y]), osc[0], osc[1], null);
            }
        }
    }
    
}
