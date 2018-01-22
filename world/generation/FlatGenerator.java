package world.generation;

import java.util.Random;
import world.World;

public class FlatGenerator extends Generator {

    private Random r;
    
    protected FlatGenerator() { r = new Random(); }
    
    @Override
    public void generate(World w) {
        for (int i = 0; i < w.columns(); i++) {
            for (int j = 0; j < w.rows(); j++) {
                String t = getParameter("tile");
                w.setTile("random".equals(t) ? r.nextInt() % w.getTileCount() : Integer.parseInt(t), i, j);
            }
        }
    }

}
