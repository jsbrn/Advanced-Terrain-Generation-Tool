package world.generation;

import world.World;

public class FlatGenerator extends Generator {

    @Override
    public void generate(World w) {
        for (int i = 0; i < w.width(); i++) {
            for (int j = 0; j < w.height(); j++) {
                w.setTile(0, i, j);
            }
        }
    }

}
