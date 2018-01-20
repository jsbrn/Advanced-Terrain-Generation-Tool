package world.generation;

import java.util.HashMap;
import world.World;

public abstract class Generator {

    private HashMap<String, Object> parameters 
            = new HashMap<String, Object>();
    
    public abstract void generate(World w);
    
    public final void setParameter(String param, Object value) {
        parameters.put(param, value);
    }
    
    public final Object getParameter(String param) {
        return parameters.get(param);
    }
}
