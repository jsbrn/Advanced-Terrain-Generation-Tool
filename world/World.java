package world;

import gui.Canvas;
import gui.GUI;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import misc.Assets;
import misc.MiscMath;
import world.terrain.Generator;
import world.terrain.misc.DiamondSquare;
import world.terrain.misc.LinearGradient;
import world.terrain.misc.PerlinNoise;
import world.terrain.misc.RadialGradient;

/**
 * Contains the terrain data, the layers, and all associated properties. There can
 * only be one instance of World at any time. Creating a new world will destroy the existing
 * world. The world is drawn to the Canvas via {@link world.World#draw(java.awt.Graphics) World.draw(Graphics)}.
 * @author Jeremy
 * @see gui.Canvas
 */
public class World {

    private static World world;
    private ArrayList<boolean[][]> layers;
    private ArrayList<String> tile_names;
    private int[] tile_dims, dims;
    private BufferedImage spritesheet;
    private String spritesheet_uri;
    private ArrayList<Image[]> textures;
    private ArrayList<HashMap<String, Object>> layer_properties;
    private HashMap<String, float[][]> saved_heightmaps;
    private float[][] elevationMap;
    
    private Random rng;
    private long seed;
    
    private BufferedImage hmapTile;
    private RescaleOp op;
    private Graphics2D bGr;
    
    private Boolean showHeightmap;
    
    
    /**
     * Creates a new world as a static instance. Replaces the existing world.
     * @param w The width of the world cell grid.
     * @param h The height of the world cell grid.
     */
    public static void newWorld(int w, int h) { world = new World(w, h); }
    /**
     * Get the currently active world.
     * @return The World instance being edited.
     */
    public static World getWorld() { return world; }
    
    
    private World(int w, int h) {
        this.elevationMap = new float[w][h];
        this.layer_properties = new ArrayList<HashMap<String, Object>>();
        this.layers = new ArrayList<boolean[][]>();
        this.dims = new int[]{w, h};
        this.tile_names = new ArrayList<String>();
        this.newLayer();
        this.rng = new Random();
        setSeed(rng.nextLong());
        this.tile_dims = new int[]{16, 16};
        clearTiles();
        this.textures = new ArrayList<Image[]>();
        this.setSpritesheet("resources/samples/terrain/earth.png");
        this.setTileNames(new String[]{"Stone", "Lava", "Sand", "Dirt", "Grass", "Snow", "Ice", "Water", "Tree", "Rocks", "Chest"});
        this.saved_heightmaps = new HashMap<String, float[][]>();
        this.showHeightmap = false;
    }
    
    private World(int w, int h, long seed) {
        this(w, h);
        setSeed(seed);
    }
    
    public float[][] getHeightmap(String name) {
        return saved_heightmaps.get(name);
    }
    
    public String[] getSavedHeightmaps() {
        return saved_heightmaps.keySet().toArray(new String[]{});
    }
    
    public float[][] createHeightmap(String algorithmName, long seed, boolean save) {
        float[][] map = new float[columns()][rows()];
        switch (algorithmName) {
            case "Diamond Square":
                int s = (int)MiscMath.max(World.getWorld().columns(), World.getWorld().rows());
                DiamondSquare ds = new DiamondSquare(s == 0 ? 0 : 32 - Integer.numberOfLeadingZeros(s - 1), seed);
                map = ds.getMap();
                break;
            case "Perlin":
                PerlinNoise perlin = new PerlinNoise();
                // Use PerlinNoise algorithm in other location
                // 6 is a random value, I don't know what the best value would be
                float[][] whitenoise = perlin.generateWhiteNoise(columns(), rows(), seed);
                map = perlin.generatePerlinNoise(whitenoise, 6);
                break;
            case "Radial Gradient":
                RadialGradient rg = new RadialGradient(Math.max(columns(), rows()));
                map = rg.getMap();
                break;
            case "Linear Gradient":
                LinearGradient lg = new LinearGradient(columns(), rows());
                map = lg.getMap();
                break;
            default:
                System.out.println("Error");
                break;
        }
        if (save) saveHeightmap(algorithmName+" ["+seed+"]", map);
        return map;
    }
    
    public void saveHeightmap(String name, float[][] map) {
        saved_heightmaps.put(name+" ["+columns()+", "+rows()+"]", map);
    }
    
    public void deleteHeightmap(String name) {
        saved_heightmaps.remove(name);
    }
    
    public float[][] combineHeightmaps(float[][] map1, float[][] map2, boolean addOperation) {
        float[][] sum = new float[map1.length][map2.length];
        if (map1.length == 0 || map2.length == 0) return sum;
        if (map1.length != map2.length || map1[0].length != map2[0].length) return sum;
        for (int i = 0; i < map1.length; i++) {
            for (int j = 0; j < map1[i].length; j++) {
                sum[i][j] = addOperation ? map1[i][j] + map2[i][j] : map1[i][j] * map2[i][j];
            }
        }
        normalizeHeightmap(sum);
        return sum;
    }
    
    public boolean combineHeightmaps(String newName, float[][] map1, float[][] map2, boolean addOperation) {
        float[][] result = combineHeightmaps(map1, map2, addOperation);
        normalizeHeightmap(result);
        saveHeightmap(newName, result);
        return true;
    }
    
    public void normalizeHeightmap(float[][] map) {
        float highest = 0;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] > highest) highest = map[i][j];
            }
        }
        if (highest <= 0) return;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                map[i][j] /= highest;
            }
        }
    }
    
    public void smoothHeightmap(int levels, float[][] map) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                map[i][j] = (float)Math.floor(map[i][j] * levels) / levels;
            }
        }
    }
    
    public void deleteHeightmap(int i) {
        if (getHeightmapName(i) == null) return;
        saved_heightmaps.remove(getHeightmapName(i));
    }
    
    public float[][] getHeightmap(int i) {
        if (getHeightmapName(i) == null) return null;
        return saved_heightmaps.get(getHeightmapName(i));
    }
    
    public String getHeightmapName(int i) {
        int index = 0;
        for (String name: saved_heightmaps.keySet()) {
            if (index == i) { return name; }
            index++;
        }
        return null;
    }
    
    public void setElevationHeightmap(String name) {
        elevationMap = getHeightmap(name);
    }
    
    public float getElevation(int x, int y) {
        if (x > elevationMap.length - 1 || x < 0 || elevationMap.length == 0) return 0;
        if (y < 0 || y > elevationMap[0].length - 1) return 0;
        return elevationMap[x][y];
    }
    
    /**
     * Writes the noise map to a BufferedImage.
     * @return The buffered image instance created.
     */
    public BufferedImage toBufferedImage(String mapName) {
        float[][] map = getHeightmap(mapName);
        BufferedImage output = new BufferedImage(map.length, map.length,BufferedImage.TYPE_INT_RGB);
        float max = map[0][0];
        float min = map[0][0];
        for (int y = 0; y < output.getHeight(); y++) {
            for (int x = 0; x < output.getWidth(); x++) {
                if (map[x][y] > max) {
                    max = map[x][y];
                }
                else if (map[x][y] < min)
                {
                    min = map[x][y];
                }
            }
        }
        for (int y = 0; y < output.getHeight(); y++) {
            for (int x = 0; x < output.getWidth(); x++) {
                float value = 255*map[x][y];
                output.setRGB(x, y, new Color((int)value,(int)value,(int)value).getRGB());
            }
        }
        return output;
    }
    
    /**
     * Resize the world to the new specified grid dimensions. If shrinking, this method
     * will cut off any tiles that do not fit in the new dimensions (with the fixed origin being at
     * the top-left). If expanding, this method will fill the new space with empty cells.
     * @param new_w The new width (in tiles).
     * @param new_h The new height (in tiles).
     */
    public void resize(int new_w, int new_h) {
        for (int l = 0; l < layers.size(); l++) {
            boolean[][] new_ = new boolean[new_w][new_h];
            clear(new_);
            for (int x = 0; x < new_w; x++) {
                for (int y = 0; y < new_h; y++) {
                    new_[x][y] = getTile(x, y, l) > -1;
                }
            }
            layers.set(l, new_);
        }
        dims = new int[]{new_w, new_h};
    }
    
    /**
     * Adds a new layer to the top of the world, with default properties.
     * Those properties are as follows: 
     * <ul>
     *  <li>name = "Untitled Layer"</li>
     *  <li>tile = 0</li>
     *  <li>lastcmd = an empty String</li>
     *  <li>rmode = 0</li>
     *  <li>rtiles = an empty integer array</li>
     * </ul>
     * For a high-level description of the layer properties, please see {@link #getLayerProperty(java.lang.String, int) getLayerProperty(String, int)}.
     */
    public final void newLayer() {
        layers.add(0, new boolean[dims[0]][dims[1]]);
        HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put("name", "Untitled Layer");
        properties.put("tile", 0);
        properties.put("lastcmd", "");
        properties.put("rmode", 0);
        properties.put("rtiles", new int[]{});
        properties.put("elevation", 0);
        layer_properties.add(0, properties);
        clearTiles(0);
    }
    
    /**
     * Set the tile definitions, i.e. the names of the tiles in the spritesheet for now.
     * This will soon be replaced by a tile attribute system.
     * @param names A String array containing the names.
     */
    public void setTileNames(String[] names) {
        tile_names = new ArrayList<String>();
        for (String def: names) tile_names.add(def);
    }
    
    /**
     * Return the names of the tiles in the spritesheet.
     * @return An ArrayList (java.lang.String) containing the names, in order.
     */
    public ArrayList<String> getTileNames() {
        return tile_names;
    }
    
    /**
     * Move the specified layer up or down by the amount given.
     * @param index The index of the layer to move (0 being the topmost layer).
     * @param amount The amount to move it. An amount less than 0 moves the layer up.
     */
    public void reorderLayer(int index, int amount) {
        boolean[][] layer = layers.remove(index);
        layers.add((int)MiscMath.clamp(index+amount, 0, layers.size()), layer);
    }
    
    /**
     * Remove the layer from the world.
     * @param index The index of the layer to remove.
     * @return True if successful, false if not.
     */
    public boolean removeLayer(int index) {
        boolean[][] removed = layers.remove(index);
        layer_properties.remove(index);
        return removed != null;
    }
    
    /**
     * Get the value of the specified property for the specified layer. The available properties are:
     * <ul>
     * <li>Name ("name"; String)</li>
     * <li>Tile ID ("tile"; Integer; the tile that is used by the layer)</li>
     * <li>Last command ("lastcmd"; String; the last generator input string received by the user 
     * ({@link gui.GUI#openCommandLineButtonActionPerformed(java.awt.event.ActionEvent) via this method})</li>
     * <li>Restricted tiles ("rtiles"; int[]; the list of tiles selected by the user to be whitelisted or blacklisted by the generator)</li>
     * <li>Restriction mode ("rmode"; Integer; allow all, whitelist or blacklist the tiles marked as restricted)</li>
     * </ul>
     * @param prop The name of the property to retrieve.
     * @param layer The index of the desired layer.
     * @return An Object that must be casted to the expected type.
     */
    public Object getLayerProperty(String prop, int layer) {
        Object val = layer_properties.get(layer).get(prop);
        return val;
    }
    
    /**
     * Set the specified layer property.
     * @param prop The name of the property to set.
     * @param value The value to give it.
     * @param layer The index of the layer to apply the new value to.
     * @see World#getLayerProperty(java.lang.String, int) for a high-level description of the available properties.
     */
    public void setLayerProperty(String prop, Object value, int layer) {
        layer_properties.get(layer).put(prop, value);
        System.out.println("Layer "+layer+", property '"+prop+"' set to "+value);
    }
    
    /**
     * Get the terrain array for the specified layer. Terrain is stored as a multi-dimensional boolean array, and the
     * tile ID layer property is used to determine which tiles to display at a given location.
     * @param layer The index of the layer.
     * @return A boolean array (the size of the world dimensions) indicating the placement of tiles on the given layer.
     */
    public boolean[][] getTerrain(int layer) {
        return layers.get(layer);
    }
    
    public Random rng() { return rng; }
    public final void setSeed(long seed) { rng.setSeed(seed); this.seed = seed; }
    public long getSeed() { return seed; }
    
    /**
     * Marks the specified tile location on the specified layer as filled if the layer tile restrictions allow.
     * @param x The x-coordinate (in tiles).
     * @param y The y-coordinate (in tiles).
     * @param layer The layer index.
     * @param set The state of the tile (placed or not; true or false).
     * @see #getLayerProperty(java.lang.String, int)
     * @see #getTerrain(int)
     */
    public boolean setTile(int x, int y, int layer, boolean set) { 
        if (x < 0 || x >= columns() || y < 0 || y >= rows()) return false;
        if (isTileAllowed(x, y, layer)) {
            getTerrain(layer)[x][y] = set;
            return true;
        } else { return false; }
    }
    
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
     * Get the tile ID at the specified location, on the specified layer. 
     * @param x The x-coordinate (in tiles).
     * @param y The y-coordinate (in tiles).
     * @param layer The layer index.
     * @return The tile ID (ranging from 0 to the highest possible tile ID) or -1 if no tile exists.
     * @see #getTileCount()
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
    
    public int getTopmostLayer(int x, int y) {
        for (int l = 0; l < layers.size(); l++) {
            if (getTile(x, y, l) > -1) return l;
        }
        return -1;
    }
    
    /**
     * Get the topmost tile underneath the specified layer. Useful for checking the tile directly underneath
     * the layer you are working with, without including empty, non-visible layers.
     * @param x The x-coordinate (in tiles).
     * @param y The y-coordinate (in tiles).
     * @param layer The layer to look under.
     * @return The tile ID, or -1 if no tile exists.
     * @see #getTile(int, int, int)
     */
    public int getTileBelow(int x, int y, int layer) {
        if (x < 0 || x >= columns() || y < 0 || y >= rows()) return -1;
        for (int l = layer+1; l < layers.size(); l++) {
            if (!layers.get(l)[x][y]) continue;
            return getTile(x, y, l);
        }
        return -1;
    }
    
    /**
     * Is a tile allowed to exist on the layer at the specified coordinates? 
     * Uses {@link #getTileBelow(int, int, int) getTileBelow} and the layer's tile restrictions to
     * determine.
     * @param x The x-coordinate (in tiles).
     * @param y The y-coordinate (in tiles).
     * @param layer The layer index.
     * @return True if the tile is allowed to be placed, false if not.
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
    
    /**
     * Get the number of layers that exist in the world.
     * @return An integer.
     */
    public final int layerCount() { return layers.size(); }
    
    /**
     * Remove all tiles in the specified layer.
     * @param layer The layer index.
     * @see #clear(boolean[][])
     */
    public final void clearTiles(int layer) {
        clear(layers.get(layer));
    }
    
    /**
     * Clears the given multi-dimensional boolean array. Resets all values to false.
     * @param terrain The array to clear.
     */
    private void clear(boolean[][] terrain) {
        for (int x = 0; x < terrain.length; x++) {
            for (int y = 0; y < terrain[0].length; y++) {
                terrain[x][y] = false;
            }
        }
    }
    
    /**
     * Clear all tiles in all layers. Does not change the layer properties at all.
     * @see #clearTiles(int)
     */
    public final void clearTiles() {
        for (int i = 0; i < layers.size(); i++) clearTiles(i);
    }
    
    /**
     * Gets the number of tiles in the World's spritesheet. The tile ID
     * @return An integer.
     */
    public int getTileCount() { return textures.size(); }
    
    /**
     * Accepts a classpath or filesystem location in String form. Reads the image found at the URI.
     * Prepares the image based on the size of the tiles in the world and stores the URI for future reference.
     * @param uri The location of the spritesheet image.
     */
    public void setSpritesheet(String uri) {
        boolean internal = uri.indexOf("resources/") == 0;
        textures = new ArrayList<Image[]>();
        
        Image shadesTemp[] = new Image[100];
        
        BufferedImage img = null;
        try {
            img = internal 
                    ? ImageIO.read(getClass().getResourceAsStream("/"+uri))
                    : ImageIO.read(new File(uri));
            spritesheet = img;
            for (int i = 0; i < img.getWidth() / tile_dims[0]; i++){
                //split the spritesheet into preloaded images
                //create levels of shading
                for(int j = 0; j < 100; j++){
                    
                    //new buffered image based on the tile size
                    hmapTile = new BufferedImage(tile_dims[0],tile_dims[1],BufferedImage.TYPE_INT_RGB);
                    
                    //draw the tile from the source onto the bufferedimage
                    bGr = hmapTile.createGraphics();
                    bGr.drawImage(spritesheet.getSubimage(i*tile_dims[0], 0, tile_dims[0], tile_dims[1]), 0, 0, null);
                    bGr.dispose();

                    //assign the brightness value out of 100
                    op = new RescaleOp(j/100f,0,null);

                    hmapTile = op.filter(hmapTile, null);

                    shadesTemp[j] = hmapTile;
                }
                //clone the array of shades into the textures
                textures.add(shadesTemp.clone());
            }
            spritesheet_uri = uri; //keep track of the uri for saving/loading
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    /**
     * Get the number of grid columns in the world, effectively the width in tiles.
     * @return An integer.
     */
    public int columns() { return dims[0]; }
    /**
     * Get the number of grid rows in the world, effectively the height in tiles.
     * @return An integer.
     */
    public int rows() { return dims[1]; }
    /**
     * Get the actual width of the world: the number of columns multiplied by the size of each cell/tile.
     * @return An integer.
     */
    public int width() { return columns()*tile_dims[0]; }
    /**
     * Get the actual height of the world: the number of rows multiplied by the size of each cell/tile.
     * @return An integer.
     */
    public int height() { return rows()*tile_dims[1]; }
    
    /**
     * Get the world-coordinates (with the origin at 0,0 being the top-left corner of the world) from the on-screen coordinates.
     * @param osx The on-screen x-coordinate.
     * @param osy The on-screen y-coordinate.
     * @return An integer array describing the world coordinates {x, y}.
     * @see #getOnscreenCoordinates(double, double)
     * @see gui.Canvas#paintComponent(java.awt.Graphics) 
     */
    public int[] getWorldCoordinates(int osx, int osy) {
        int[] camera = Canvas.getCamera();
        return new int[]{osx - camera[0], osy - camera[1]};
    }
    
    /**
     * Get the on-screen coordinates from the world-coordinates.
     * @param x The world x-coordinate.
     * @param y The world y-coordinate.
     * @return An integer array describing the on-scren coordinates {osx, osy}.
     * @see #getWorldCoordinates(int, int) 
     */
    public int[] getOnscreenCoordinates(double x, double y) {
        int tx = ((int)x)*tile_dims[0], ty = ((int)y)*tile_dims[1];
        int[] camera = Canvas.getCamera();
        int[] canvas_dims = Canvas.getDimensions();
        return new int[]{(canvas_dims[0]/2) - camera[0] + tx, 
            (canvas_dims[1]/2) - camera[1] + ty};
    }
    
    /**
     * Creates a new world and populates it with data from the specified world save file.
     * @param world_file The File to load from.
     * @return True if successful, false if not.
     */
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
                if (line.indexOf("spritesheet: ") == 0) this.setSpritesheet(line.replace("spritesheet: ", ""));
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
                        if (keyval.length < 2) continue; //do not load, no value
                        Object val = keyval[1];
                        if ("rtiles".equals(keyval[0])) { //int arrays
                            int[] rtiles = new int[]{};
                            String[] arr = keyval[1].split("\\s*");
                            rtiles = new int[arr.length];
                            for (int r = 0; r < rtiles.length; r++) rtiles[r] = Integer.parseInt(arr[r]);
                            val = rtiles;
                        } else if (keyval[1].matches("\\d*\\s?")) {
                            val = Integer.parseInt(keyval[1].trim());
                        }
                        setLayerProperty(keyval[0], val, layer);
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
    
    /**
     * Writes the world to a file in the specified folder. Generates a name for the world based on the current timestamp.
     * @param folder The folder to save the world to.
     * @return True if successful, false if not.
     * @see #exportTerrain(java.io.BufferedWriter, boolean)
     */
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
            for (HashMap<String, Object> lprops: layer_properties) {
                for (String key: lprops.keySet()) {
                    Object val = lprops.get(key);
                    String tostring = val.toString();
                    if (val instanceof int[]) { tostring = ""; for (int v: (int[])val) tostring += v+" "; }
                    props += ", "+key+" -> "+tostring;
                }
                bw.write("layer: "+props.replaceFirst(",\\s*", "")+"\n");
            }
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
    
    /**
     * Export the terrain to a file in the specified folder. Generates a name for the file based on the current timestamp.
     * @param folder The folder to save to.
     * @return True if successful, false if not.
     * @see #exportTerrain(java.io.BufferedWriter, boolean) 
     */
    public boolean exportTerrain(File folder) {
        FileWriter fw;
        File f = new File(folder.getAbsolutePath()+"/terrain-export-"+(System.currentTimeMillis()/1000)
                +"-"+MiscMath.randomInt(0, 1000)+".txt");
        System.out.println("Exporting terrain to file " + f.getAbsoluteFile().getAbsolutePath());
       
        try { 
            if (!f.exists()) f.createNewFile();
            fw = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(fw);
            boolean success = exportTerrain(bw, true);
            bw.close();
            return success;
        } catch (IOException ex) {
            Logger.getLogger(World.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    /**
     * Write terrain data to the specified BufferedWriter. Does not close the BufferedWriter, since it is used in other methods
     * that do.
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
    
    /**
     * Draw the world to the specified Graphics instance. For example, the instance used by the Canvas.
     * @param g The Graphics instance to draw to.
     * @see gui.Canvas#paintComponent(java.awt.Graphics)
     */
    public void draw(Graphics g, boolean showElevationMap) {
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
                        if (getTile(x, y, l) > -1){ 
                            //draw the tiles using the heightmap to determine the shade
                            g.drawImage(textures.get(getTile(x,y,l))[showHeightmap ? Math.abs((int)Math.floor(this.getHeightmap(0)[x][y]*100)-1) : 99], osc[0], osc[1], null);
                           // there is currently no heightmap selection and it defaults to the heightmap at the index 0
                            
                        }
                    } else {
                        found_null = true;
                        g.drawLine(osc[0], osc[1], osc[0] + tile_dims[0], osc[1]+tile_dims[1]);
                    }
                    
                    if (l == 0) { //if working in the topmost layer
                        for (int i = 0; i < 9; i++) {
                            int x2 = x - 1 + (i % 3);
                            int y2 = y - 1 + (i / 3);
                            if (!showElevationMap) {
                                int topmost = getTopmostLayer(x, y);
                                int topmost2 = getTopmostLayer(x2, y2);
                                if (topmost > -1 && topmost2 > -1) {
                                    if ((Integer)getLayerProperty("elevation", topmost) > (Integer)getLayerProperty("elevation", topmost2)) {
                                        g.drawImage(Assets.getShadow(i), osc[0] - tile_dims[0] + ((i % 3)*tile_dims[0]),
                                                osc[1] - tile_dims[1] + ((i/3)*tile_dims[1]), null);
                                    }
                                }
                            } else {
                                if (getElevation(x, y) > getElevation(x2, y2)) {
                                    g.drawImage(Assets.getShadow(i), osc[0] - tile_dims[0] + ((i % 3)*tile_dims[0]),
                                            osc[1] - tile_dims[1] + ((i/3)*tile_dims[1]), null);
                                }
                            }
                        }
                    }

                }
            }
        }
        if (found_null) g.drawString("Null tiles found in your map. Check your tile spritesheet.", 15, 40);
    }
    
    public void heightmapShow(){
        showHeightmap=true;
        System.out.println("show");
    }
    
    public void heightmapHide(){
        showHeightmap=false;
        System.out.println("HIDE");
    }
    
}
