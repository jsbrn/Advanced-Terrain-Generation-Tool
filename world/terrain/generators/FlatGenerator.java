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
                if (j < w.rows() - Integer.parseInt(getParameter("height"))) continue;
                String t = getParameter("tile");
                int tcouny = w.getTileCount();
                w.setTile("random".equals(t) ? (tcouny == 0 ? 0 : r.nextInt() % tcouny) : Integer.parseInt(t), i, j);
            }
        }
    }

}
