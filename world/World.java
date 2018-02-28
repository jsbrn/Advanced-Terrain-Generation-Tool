package world;

import gui.Canvas;
import gui.GUI;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import misc.MiscMath;
import world.terrain.Generator;

public class World {

    private static World world;
    private ArrayList<boolean[][]> layers;
    private ArrayList<String> tile_names;
    private int[] tile_dims, dims;
    private BufferedImage spritesheet;
    private String spritesheet_uri;
    private ArrayList<Image> textures;
    private ArrayList<HashMap<String, Object>> layer_properties;
    
    private Random rng;
    private long seed;
    
    public static void newWorld(int w, int h) { world = new World(w, h); }
    public static World getWorld() { return world; }
    
    private World(int w, int h) {
        this.layer_properties = new ArrayList<HashMap<String, Object>>();
        this.layers = new ArrayList<boolean[][]>();
        this.dims = new int[]{w, h};
        this.tile_names = new ArrayList<String>();
        this.newLayer();
        this.rng = new Random();
        setSeed(rng.nextLong());
        this.tile_dims = new int[]{16, 16};
        clearTiles();
        this.textures = new ArrayList<Image>();
        this.setSpritesheet(new File("src/resources/samples/terrain/earth.png"));
        this.setTileNames(new String[]{"Stone", "Lava", "Sand", "Dirt", "Grass", "Snow", "Ice", "Water", "Tree", "Rocks"});
    }
    
    public void resize(int new_w, int new_h) {
        for (int l = 0; l < layers.size(); l++) {
            boolean[][] new_ = new boolean[new_w][new_h];
            clear(new_);
            for (int x = 0; x < new_w; x++) {
                for (int y = 0; y < new_h; y++) {
                    new_[x][y] = layers.get(l)[x][y];
                }
            }
            layers.set(l, new_);
        }
        dims = new int[]{new_w, new_h};
    }
    
    public void newLayer() {
        layers.add(0, new boolean[dims[0]][dims[1]]);
        HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put("name", "Untitled Layer");
        properties.put("tile", 0);
        properties.put("lastcmd", "");
        properties.put("rmode", 0);
        properties.put("rtiles", new int[]{});
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
        boolean[][] layer = layers.remove(index);
        layers.add((int)MiscMath.clamp(index+amount, 0, layers.size()), layer);
    }
    
    public boolean removeLayer(int index) {
        if (layers.size() == 1) return false;
        boolean[][] removed = layers.remove(index);
        layer_properties.remove(index);
        return removed != null;
    }
    
    public Object getLayerProperty(String prop, int layer) {
        return layer_properties.get(layer).get(prop);
    }
    
    public void setLayerProperty(String prop, Object value, int layer) {
        layer_properties.get(layer).put(prop, value);
    }
    
    public boolean[][] getTerrain(int layer) {
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
    public void setTile(int x, int y, int layer, boolean set) { if (isTileAllowed(x, y, layer)) getTerrain(layer)[x][y] = set; }
    
    /**
     * Get the topmost visible tile at the {x, y} coordinate specified.
     * @param x The x coord
     * @param y The y coord
     * @return A tile ID (integer).
     */
    public int getTile(int x, int y) { 
        return getTileBelow(x, y, -1);
    }
    
    /**
     * Get the tile at the specified layer. 
     * @param x
     * @param y
     * @param layer
     * @return 
     */
    public int getTile(int x, int y, int layer) {
        if (x < 0 || y < 0) return -1;
        if (layer < 0 || layer >= layers.size()) return -1;
        boolean[][] l = layers.get(layer);
        if (x < l.length) if (y < l[0].length) {
            return l[x][y] ? (Integer)getLayerProperty("tile", layer) : -1;
        }
        return -1;
    }
    
    /**
     * Get the topmost tile underneath the specified layer.
     * @param x The x coord
     * @param y The y coord
     * @param layer The layer to look under.
     * @return 
     */
    public int getTileBelow(int x, int y, int layer) {
        for (int l = layer+1; l < layers.size(); l++) {
            if (!layers.get(l)[x][y]) continue;
            return getTile(x, y, l);
        }
        return -1;
    }
    
    /**
     * Is a tile allowed to exist on the layer at the specified coordinates? Uses the layers beneath to determine.
     * @param x
     * @param y
     * @param layer
     * @return 
     */
    public boolean isTileAllowed(int x, int y, int layer) {
        int tile = getTileBelow(x, y, layer);
        int rmode = (Integer)getLayerProperty("rmode", layer);
        if (rmode == 0) return true;
        boolean whitelist = rmode == 1;
        int[] tile_ids = (int[])getLayerProperty("rtiles", layer);
        boolean on_list = false;
        for (int t: tile_ids) if (tile == t) on_list = true;
        return (whitelist && on_list) || (!whitelist && !on_list);
    }
    
    public final int layerCount() { return layers.size(); }
    
    public final void clearTiles(int layer) {
        clear(layers.get(layer));
    }
    
    private void clear(boolean[][] terrain) {
        for (int x = 0; x < terrain.length; x++) {
            for (int y = 0; y < terrain[0].length; y++) {
                terrain[x][y] = false;
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
            for (int i = 0; i < img.getWidth() / tile_dims[0]; i++) //split the spritesheet into preloaded images
                textures.add(spritesheet.getSubimage(i*tile_dims[0], 0, tile_dims[0], tile_dims[1]));
            spritesheet_uri = sprite.getAbsolutePath(); //keep track of the uri for saving/loading
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
    
    public boolean load(File world_file) {
        if (!world_file.exists()) return false;
        FileReader fr;
        System.out.println("Loading from file: " + world_file.getAbsoluteFile().getAbsolutePath());
        try {
            fr = new FileReader(world_file);
            BufferedReader br = new BufferedReader(fr);
            layers.clear();
            int layer = 0, row = 0;
            while (true) {
                String line = br.readLine();
                if (line == null) break;
                line = line.trim();
                if (line.indexOf("spritesheet: ") == 0) this.setSpritesheet(new File(line.replace("spritesheet: ", "")));
                if (line.indexOf("tnames: ") == 0) this.setTileNames(line.replace("tnames: ", "").split("\\s*,\\s*"));
                if (line.indexOf("dims: ") == 0) {
                    String[] dimstr = line.replace("dims: ", "").split("\\s*,\\s*");
                    this.resize(Integer.parseInt(dimstr[0]), Integer.parseInt(dimstr[1]));
                    for (int i = 0; i < Integer.parseInt(dimstr[2]); i++) newLayer(); //add in all layers to apply z depth
                }
                if (line.indexOf("layer: ") == 0) {
                    String propstr = line.replace("layer: ", "");
                    String[] props = propstr.split("\\s*,\\s*");
                    for (String prop: props) {
                        String[] keyval = prop.split("\\s*->\\s*");
                        boolean integer = keyval[1].matches("^\\d+$");
                        setLayerProperty(keyval[0], integer ? Integer.parseInt(keyval[1]) : keyval[1], layer);
                    }
                    layer++;
                }
                
                if (line.equals("---BEGIN TERRAIN DATA---")) layer = 0;
                if (line.isEmpty()) { 
                    layer++; row = 0; 
                } else if ((line.charAt(0)+"").matches("^\\d+$")) {
                    //prepare row
                    String[] ro = line.split(" ");
                    for (int i = 0; i < ro.length; i++) layers.get(layer)[i][row] = Integer.parseInt(ro[i]) == 1;
                    row++;
                }
                
            }
            Canvas.setCamera(0, 0);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean save(File folder) {
        FileWriter fw;
        File f = new File(folder.getAbsolutePath()+"/world-"+(System.currentTimeMillis()/1000)
                +"-"+MiscMath.randomInt(0, 1000)+".txt");
        System.out.println("Saving world to file " + f.getAbsoluteFile().getAbsolutePath());
        try {
            if (!f.exists()) f.createNewFile();
            fw = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("spritesheet: "+spritesheet_uri+"\n");
            String tnames = ""; for (String s: tile_names) tnames += ", "+s; 
            bw.write("tnames: "+tnames.replaceFirst(",\\s*", "")+"\n");
            bw.write("dims: "+dims[0]+", "+dims[1]+", "+layers.size()+"\n"); //x, y, z (depth, or layer count)
            String props = "";
            for (HashMap<String, Object> lprops: layer_properties)
                for (String key: lprops.keySet())
                    props += ", "+key+" -> "+lprops.get(key);
            bw.write("layer: "+props.replaceFirst(",\\s*", "")+"\n");
            bw.write("---BEGIN TERRAIN DATA---\n");
            exportTerrain(bw, false);   
            bw.write("---END TERRAIN DATA---\n");
            bw.close();
            System.out.println("Saved world to "+f.getAbsolutePath());
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
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
            return exportTerrain(bw, true);
        } catch (IOException ex) {
            Logger.getLogger(World.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    /**
     * Write terrain data to the specified BufferedWriter. Does not close BufferedWriter.
     * @param bw The BufferedWriter to write to.
     * @param raw If true, write the raw tile IDs (0 - n) instead of 0 or 1.
     * @return True if successful, false if not.
     */
    public boolean exportTerrain(BufferedWriter bw, boolean raw) {
        
        try {
            for (int l = 0; l < layers.size(); l++) {
                for (int j = 0; j < rows(); j++) {
                    String row = "";
                    for (int i = 0; i < columns(); i++) {
                        //if raw, output tile ID. else, output 1 if tile is set and 0 if not set
                        int id = raw ? getTile(i, j, l) : (getTile(i, j, l) != -1 ? 1 : 0);
                        row += id+" ";
                    }
                    bw.write(row.trim()+"\n");
                }
                if (l < layers.size() - 1) bw.write("\n");
            }
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
