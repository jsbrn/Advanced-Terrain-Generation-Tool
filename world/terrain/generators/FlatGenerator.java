package world.terrain.generators;

import java.util.Random;
import world.World;
import world.terrain.Generator;

public class FlatGenerator extends Generator {

    private Random r;
    
    public FlatGenerator() { r = new Random(); }
    
    @Override
    public void generate(World w) {
        for (int i = 0; i < w.columns(); i++) {
            for (int j = 0; j < w.rows(); j++) {
                if (j < w.rows() - Integer.parseInt(getParameter("height"))) continue;
                String t = getParameter("tile");
                int tcount = w.getTileCount();
                w.setTile("random".equals(t) ? (tcount == 0 ? 0 : r.nextInt() % tcount) : Integer.parseInt(t), i, j);
            }
        }
    }

}
