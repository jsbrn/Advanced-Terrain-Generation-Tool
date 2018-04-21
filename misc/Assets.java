package misc;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Assets {
    
    public static final int NORTHWEST = 0, 
            NORTH = 1, 
            NORTHEAST = 2, 
            WEST = 3, 
            EAST = 5, 
            SOUTHWEST = 6, 
            SOUTH = 7, 
            SOUTHEAST = 8;
    private static ArrayList<Image> shadows;

    public static void init() {
        loadShadows();
    }
    
    private static void loadShadows() {
        shadows = new ArrayList<Image>();
        BufferedImage img = null;
        try {
            img = ImageIO.read(Assets.class.getResourceAsStream("/resources/shadow.png"));
            for (int i = 0; i < 9; i++)
                shadows.add(img.getSubimage((i % 3)*16, (i / 3)*16, 16, 16));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static Image getShadow(int orientation) {
        return shadows.get(orientation);
    }

}