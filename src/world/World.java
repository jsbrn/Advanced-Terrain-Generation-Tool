package world;

import gui.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import misc.MiscMath;
import world.terrain.Generator;

public class World {

    private static World world;
    private static World reworld;

    public static World resize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private int[][] terrain;
    private int[][] temp;
    private int[] tile_dims;
    private BufferedImage spritesheet;
    private ArrayList<Image> textures;
    
    private Random rng;
    private long seed;
    
    public static void newWorld(int w, int h) { world = new World(w, h); }
    public static World getWorld() { return world; }
        
    private World(int w, int h) {
        this.terrain = new int[w][h];
        this.rng = new Random();
        setSeed(rng.nextLong());
        this.tile_dims = new int[]{16, 16};
        clearTiles();
        this.textures = new ArrayList<Image>();
    }
    
    private World(int w, int h, long seed) {
        this(w, h);
        setSeed(seed);
    }
    
    public Random rng() { return rng; }
    public final void setSeed(long seed) { rng.setSeed(seed); this.seed = seed; }
    public long getSeed() { return seed; }
    
    public void generate(Generator g) { setSeed(seed); g.generate(this); }
    public void setTile(int tile, int x, int y) { terrain[x][y] = tile; }
    public int getTile(int x, int y) { return terrain[x][y]; }
    public final void clearTiles() {
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
        g.setColor(Color.red);
        boolean found_null = false;
        for (int x = 0; x < terrain.length; x++) {
            for (int y = 0; y < terrain[0].length; y++) {
                //get the onscreen coordinates of the tile
                int osc[] = getOnscreenCoordinates(x, y);
                //if tile is offscreen, don't render
                if (!MiscMath.pointIntersectsRect(osc[0], osc[1], 
                        -tile_dims[0], -tile_dims[1], 
                        Canvas.getDimensions()[0] + tile_dims[0], Canvas.getDimensions()[1] + tile_dims[1])) continue;
                //if tile ID is valid, draw. otherwise, indicate.
                if (terrain[x][y] < getTileCount()) {
                    if (terrain[x][y] > -1) g.drawImage(textures.get(terrain[x][y]), osc[0], osc[1], null);
                } else {
                    found_null = true;
                    g.drawLine(osc[0], osc[1], osc[0] + tile_dims[0], osc[1]+tile_dims[1]);
                }
                    
            }
        }
        if (found_null) g.drawString("Null tiles found in your map. Check your tile spritesheet.", 15, 25);
    }
    
    public void setResize(int w, int h){
        for(int r=0; r<h; r++){
            for(int c=0; c<w; c++){
                terrain[r][c]= terrain[h][w];
            }
        }
    }
    public void resize(int scaledWidth, int scaledHeight){

        /**resize the world
         *
         */
        temp = terrain;
        if(scaledWidth > terrain[0].length && scaledHeight > terrain.length){    //compare size of column
                resize(terrain[0].length, terrain.length);
                for(int i=terrain.length; i<scaledHeight; i++){
                for(int j=terrain[0].length;j<scaledWidth;j++){
                    terrain[i][j]= -1;
                }
            }
        }else if(scaledWidth > terrain[0].length && scaledHeight <= terrain.length){
                resize(terrain[0].length, scaledHeight);
                for(int i=0; i<scaledHeight; i++){
                for(int j=terrain[0].length;j<scaledWidth;j++){
                    terrain[i][j]= -1;
                }
            }
        }else if(scaledWidth <= terrain[0].length && scaledHeight > terrain.length){ //compare size of row
            setResize(scaledWidth, terrain.length);
            for(int i=terrain.length; i<scaledHeight; i++){
                for(int j=0;j<scaledWidth;j++){
                    terrain[i][j]= -1;
                }
            }
        }
        else{
            setResize(scaledWidth,scaledHeight);
        }
    }
}
