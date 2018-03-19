package world.terrain;

import gui.GUI;
import world.terrain.generators.*;
import java.util.HashMap;
import java.util.Random;
import javax.swing.JPanel;
import world.World;

/**
 * An extendable, abstract class that places tiles into the world.
 * @author Jeremy
 */
public abstract class Generator {

    private long seed;
    private Random rng;
    
    /**
     * This is a static list of generators that the GUI/CLI uses when you specify the name of the generator.
     * Put all Generator instances in this list and use getGenerator(name) to access them.
     */
    private static final Object[][] generators = new Object[][]{
        {"Fill", new FillGenerator(), GUI.NO_OPTIONS},
        {"WaterGenerator", new WaterGenerator(), GUI.NO_OPTIONS}, 
        {"NoiseMap", new NoiseMapGenerator(), GUI.NOISEMAP_OPTIONS},
        {"Scattered", new ScatteredGenerator(), GUI.SCATTER_OPTIONS},
        {"Sine", new SineGenerator(), GUI.NO_OPTIONS},
    };
    
    public Generator() {
        this.seed = new Random().nextInt();
        this.rng = new Random(seed);
    }
    
    /**
     * Get the number of available generators.
     * @return An integer.
     * @see #generators
     */
    public static final int generatorCount() { return generators.length; }
    
    public static final JPanel getOptionsPanel(int generator) {
        return (JPanel)generators[generator][2];
    }
    
    /**
     * Get the Generator instance at the specified index.
     * @param index The index of the generator.
     * @return The desired Generator.
     * @see #generators
     */
    public static final Generator getGenerator(int index) {
        return (Generator)generators[index][1];
    }
    
    /**
     * Get the name of the Generator instance at the specified index.
     * @param index The index of the generator.
     * @return The name, as a String.
     * @see #generators
     */
    public static String getGeneratorName(int index) {
        return (String)generators[index][0];
    }
    
    public void setSeed(long seed) { 
        this.seed = seed; 
        this.rng = new Random(seed);
    }
    public long getSeed() { return seed; }
    public void randomSeed() { setSeed(new Random().nextLong()); }
    public Random rng() { return rng; }
    public void reset() { setSeed(seed); }
    
    /**
     * Returns the specified generator from the list of terrain generators.
     * @param name The name of the generator (see list in world.generation.Generator)
     * @return A Generator instance, or null if no match found.
     * @see #generators
     */
    public static Generator getGenerator(String name) {
        for (Object o[]: generators) if (o[0].equals(name)) return (Generator)o[1];
        return null;
    }
    
    /**
     * Fills the specified world instance. Must be implemented by each generator type.
     * @param w The World to fill with terrain.
     * @param layer The tile layer to generate in.
     */
    public abstract void generate(World w, int layer);

    private HashMap<String, String> parameters 
            = new HashMap<String, String>();
    
    /**
     * Set this Generator's parameter. Stores parameters in a HashMap. It stores them as a String
     * because the user can enter them in via a text field, or opt to use the GUI sliders.
     * @param param The name of the parameter, as a String.
     * @param value The value (as a String), i.e. "0.4".
     */
    public final void setParameter(String param, String value) {
        parameters.put(param, value);
    }
    
    /**
     * Get the specified parameter from the generator.
     * @param param The parameter name.
     * @return The String set by setParameter; convert it to whatever type you expect it to be.
     */
    public final String getParameter(String param) {
        return parameters.get(param);
    }
    
}
