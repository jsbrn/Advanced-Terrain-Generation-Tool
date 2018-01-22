package world.generation;

import java.util.HashMap;
import world.World;

public abstract class Generator {

    /**
     * This is a static list of generators that the GUI/CLI uses when you specify the name of the generator.
     * Put all Generator instances in this list and use getGenerator(name) to access them.
     */
    private static final Object[][] generators = new Object[][]{
        {"FlatGenerator", new FlatGenerator()}
    };
    
    /**
     * Returns the specified generator from the list of terrain generators.
     * @param name The name of the generator (see list in world.generation.Generator)
     * @return A Generator instance, or null if no match found.
     */
    public static Generator getGenerator(String name) {
        for (Object o[]: generators) if (o[0].equals(name)) return (Generator)o[1];
        return null;
    }
    
    /**
     * Fills the specified world instance. Must be implemented by each generator type.
     * @param w The World to fill with terrain.
     */
    public abstract void generate(World w);

    private HashMap<String, Object> parameters 
            = new HashMap<String, Object>();
    
    /**
     * Set this Generator's parameter. Stores parameters in a HashMap.
     * @param param The name of the parameter, as a String.
     * @param value The value (of any type).
     */
    public final void setParameter(String param, Object value) {
        parameters.put(param, value);
    }
    
    /**
     * Get the specified parameter from the generator.
     * @param param The parameter name.
     * @return The object set by setParameter; cast it to whatever type you expect it to be.
     */
    public final Object getParameter(String param) {
        return parameters.get(param);
    }
}
