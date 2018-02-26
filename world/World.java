package world;

import gui.Canvas;
import gui.GUI;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import misc.MiscMath;
import world.terrain.Generator;

public class World {

    private static World world;
    private ArrayList<int[][]> layers;
    private int[] tile_dims, dims;
    private BufferedImage spritesheet;
    private ArrayList<Image> textures;
    
    private Random rng;
    private long seed;
    
    public static void newWorld(int w, int h) { world = new World(w, h); }
    public static World getWorld() { return world; }
    
    private World(int w, int h) {
        this.layers = new ArrayList<int[][]>();
        this.dims = new int[]{w, h};
        this.rng = new Random();
        setSeed(rng.nextLong());
        this.tile_dims = new int[]{16, 16};
        clearTiles();
        this.textures = new ArrayList<Image>();
        this.setSpritesheet(new File("src/resources/samples/terrain/earth.png"));
    }
    
    public void addLayer() {
        layers.add(new int[dims[0]][dims[1]]);
    }
    
    public boolean removeLayer(int index) {
        layers.remove(index);
    }
    
    public int[][] getLayer(int index) {
        return layers.get(index);
    }
    
    private World(int w, int h, long seed) {
        this(w, h);
        setSeed(seed);
    }
    
    public Random rng() { return rng; }
    public final void setSeed(long seed) { rng.setSeed(seed); this.seed = seed; }
    public long getSeed() { return seed; }
    
    public void generate(Generator g) { setSeed(seed); g.generate(this); }
    public void setTile(int x, int y, int layer, int tile) { getLayer(layer)[x][y] = tile; }
    /**
     * Get the topmost visible tile at the {x, y} coordinate specified.
     * @param x The x coord
     * @param y The y coord
     * @return A tile ID (integer).
     */
    public int getTile(int x, int y) { 
        for (int l = layers.size() - 1; l >= -1; l++) {
            if (layers.get(l)[x][y] == -1) continue;
            return layers.get(l)[x][y];
        }
        return -1;
    }
    public int getTile(int x, int y, int layer) { 
        return layers.get(l)[x][y];
    }
    public final void clearTiles() {
        for (int x = 0; x < columns(); x++) {
            for (int y = 0; y < rows(); y++) {
                layers[x][y] = -1;
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
    
    public int columns() { return layers.length; }
    public int rows() { return layers.length > 0 ? layers[0].length : 0; }
    
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
    
    public boolean exportTerrain(File folder) {
        FileWriter fw;
        File f = new File(folder.getAbsolutePath()+"/terrain-export-"+(System.currentTimeMillis()/100000)+".txt");
        System.out.println("Exporting terrain to file " + f.getAbsoluteFile().getAbsolutePath());
        try {
            if (!f.exists()) f.createNewFile();
            fw = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(fw);
            
            for(int j = 0;j < rows(); j++) {
                String row = "";
                for(int i = 0; i < columns(); i++) {
                    row += layers[i][j]+" ";
                }
                bw.write(row.trim()+"\n");
            }
            
            bw.close();
            System.out.println("Exported terrain to "+f.getAbsolutePath());
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    public void draw(Graphics g) {
        g.setColor(Color.red);
        boolean found_null = false;
        for (int x = 0; x < layers.length; x++) {
            for (int y = 0; y < layers[0].length; y++) {
                //get the onscreen coordinates of the tile
                int osc[] = getOnscreenCoordinates(x, y);
                //if tile is offscreen, don't render
                if (!MiscMath.pointIntersectsRect(osc[0], osc[1], 
                        -tile_dims[0], -tile_dims[1], 
                        Canvas.getDimensions()[0] + tile_dims[0], Canvas.getDimensions()[1] + tile_dims[1])) continue;
                //if tile ID is valid, draw. otherwise, indicate.
                if (layers[x][y] < getTileCount()) {
                    if (layers[x][y] > -1) g.drawImage(textures.get(layers[x][y]), osc[0], osc[1], null);
                } else {
                    found_null = true;
                    g.drawLine(osc[0], osc[1], osc[0] + tile_dims[0], osc[1]+tile_dims[1]);
                }
                    
            }
        }
        if (found_null) g.drawString("Null tiles found in your map. Check your tile spritesheet.", 15, 25);
    }
    
}
