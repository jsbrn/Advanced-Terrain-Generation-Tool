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
import java.util.HashMap;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import misc.MiscMath;
import world.terrain.Generator;

public class World {

    private static World world;
    private ArrayList<int[][]> layers;
    private ArrayList<String> tile_names;
    private int[] tile_dims, dims;
    private BufferedImage spritesheet;
    private ArrayList<Image> textures;
    private ArrayList<HashMap<String, Object>> layer_properties;
    
    private Random rng;
    private long seed;
    
    public static void newWorld(int w, int h) { world = new World(w, h); }
    public static World getWorld() { return world; }
    
    private World(int w, int h) {
        this.layer_properties = new ArrayList<HashMap<String, Object>>();
        this.layers = new ArrayList<int[][]>();
        this.dims = new int[]{w, h};
        this.tile_names = new ArrayList<String>();
        this.addLayer();
        this.rng = new Random();
        setSeed(rng.nextLong());
        this.tile_dims = new int[]{16, 16};
        clearTiles();
        this.textures = new ArrayList<Image>();
        this.setSpritesheet(new File("src/resources/samples/terrain/earth.png"));
        this.setTileNames(new String[]{"Stone", "Lava", "Sand", "Dirt", "Grass", "Snow", "Ice", "Water", "Tree", "Rocks"});
    }
    
    public void resize(int new_w, int new_h) {
        
    }
    
    public void addLayer() {
        layers.add(0, new int[dims[0]][dims[1]]);
        HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put("name", "Untitled Layer");
        properties.put("tile", 0);
        properties.put("lastcmd", "");
        layer_properties.add(0, properties);
        clearTiles(0);
    }
    
    /**
     * Set the tile definitions, i.e. the names of the tiles in the spritesheet for now.
     * This will soon be replaced by a tile attribute system.
     * @param names 
     */
    public void setTileNames(String[] names) {
        tile_names = new ArrayList<String>();
        for (String def: names) tile_names.add(def);
    }
    
    public ArrayList<String> getTileNames() {
        return tile_names;
    }
    
    public void reorderLayer(int index, int amount) {
        int[][] layer = layers.remove(index);
        layers.add((int)MiscMath.clamp(index+amount, 0, layers.size()), layer);
    }
    
    public boolean removeLayer(int index) {
        if (layers.size() == 1) return false;
        int[][] removed = layers.remove(index);
        layer_properties.remove(index);
        return removed != null;
    }
    
    public Object getLayerProperty(String prop, int layer) {
        return layer_properties.get(layer).get(prop);
    }
    
    public void setLayerProperty(String prop, Object value, int layer) {
        layer_properties.get(layer).put(prop, value);
    }
    
    public int[][] getTerrain(int layer) {
        return layers.get(layer);
    }
    
    private World(int w, int h, long seed) {
        this(w, h);
        setSeed(seed);
    }
    
    public Random rng() { return rng; }
    public final void setSeed(long seed) { rng.setSeed(seed); this.seed = seed; }
    public long getSeed() { return seed; }
    
    public void generate(Generator g) { setSeed(seed); g.generate(this, 0); }
    public void setTile(int x, int y, int layer, int tile) { getTerrain(layer)[x][y] = tile; }
    /**
     * Get the topmost visible tile at the {x, y} coordinate specified.
     * @param x The x coord
     * @param y The y coord
     * @return A tile ID (integer).
     */
    public int getTile(int x, int y) { 
        for (int l = 0; l < layers.size(); l++) {
            if (layers.get(l)[x][y] == -1) continue;
            return layers.get(l)[x][y];
        }
        return -1;
    }
    public int getTile(int x, int y, int layer) { 
        return layers.get(layer)[x][y];
    }
    
    public final int layerCount() { return layers.size(); }
    
    public final void clearTiles(int layer) {
        for (int x = 0; x < columns(); x++) {
                for (int y = 0; y < rows(); y++) {
                    layers.get(layer)[x][y] = -1;
                }
            }
    }
    
    /**
     * Clear all tiles in all layers. Does not change the layers at all.
     */
    public final void clearTiles() {
        for (int i = 0; i < layers.size(); i++) clearTiles(i);
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
    
    public int columns() { return dims[0]; }
    public int rows() { return dims[1]; }
    
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
        File f = new File(folder.getAbsolutePath()+"/terrain-export-"+(System.currentTimeMillis()/1000)
                +"-"+MiscMath.randomInt(0, 1000)+".txt");
        System.out.println("Exporting terrain to file " + f.getAbsoluteFile().getAbsolutePath());
        try {
            if (!f.exists()) f.createNewFile();
            fw = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("Dimensions: "+dims[0]+", "+dims[1]+"\n");
            bw.write("---BEGIN TERRAIN EXPORT---\n");
            for (int l = 0; l < layers.size(); l++) {
                for (int j = 0; j < rows(); j++) {
                    String row = "";
                    for (int i = 0; i < columns(); i++) {
                        row += getTile(i, j, l)+" ";
                    }
                    bw.write(row.trim()+"\n");
                }
                if (l < layers.size() - 1) bw.write("---\n");
            }
            bw.write("---END TERRAIN EXPORT---\n");
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
        for (int l = layers.size() - 1; l > -1; l--) {
            for (int x = 0; x < dims[0]; x++) {
                for (int y = 0; y < dims[1]; y++) {
                    //get the onscreen coordinates of the tile
                    int osc[] = getOnscreenCoordinates(x, y);
                    //if tile is offscreen, don't render
                    if (!MiscMath.pointIntersectsRect(osc[0], osc[1], 
                            -tile_dims[0], -tile_dims[1], 
                            Canvas.getDimensions()[0] + tile_dims[0], Canvas.getDimensions()[1] + tile_dims[1])) continue;
                    //if tile ID is valid, draw. otherwise, indicate.
                    if (getTile(x, y, l) < getTileCount()) {
                        if (getTile(x, y, l) > -1) g.drawImage(textures.get(getTile(x, y, l)), osc[0], osc[1], null);
                    } else {
                        found_null = true;
                        g.drawLine(osc[0], osc[1], osc[0] + tile_dims[0], osc[1]+tile_dims[1]);
                    }

                }
            }
        }
        if (found_null) g.drawString("Null tiles found in your map. Check your tile spritesheet.", 15, 40);
    }
    
}
