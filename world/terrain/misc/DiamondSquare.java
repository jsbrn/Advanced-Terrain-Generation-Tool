package world.terrain.misc;

import java.awt.Color;
import java.awt.image.BufferedImage;

// https://en.wikipedia.org/wiki/Diamond-square_algorithm
// To use for other things use the command: float map[][] = new DiamondSquare(n).map;
// where 2^n = the map's width and height (so n=10 will give a 1024 by 1024 array).
// author: Ryan Dykstra
public class DiamondSquare {
    
    private static final float SCALAR = (float) (1/Math.sqrt(2));
    private float map[][];
    
    public DiamondSquare(int logWidth) {
        
        map = new float[1<<logWidth][1<<logWidth];
        map[0][0] = 0;
        
        float magnitude = 1;
        for (int i = 0; i < logWidth; i++) {
            for (int y = 0; y < 1<<i; y++) {
                for (int x = 0; x < 1<<i; x++) {
                    Diamond(x*(map.length>>i), y*(map.length>>i), map.length>>i, map.length>>i, magnitude);
                }
            }
            magnitude*=SCALAR;
            for (int y = 0; y < 1<<i; y++) {
                for (int x = 0; x < 1<<i; x++) {
                    Square(x*(map.length>>i)+(map.length>>(i+1)), y*(map.length>>i), map.length>>i, map.length>>i, magnitude);
                    Square(x*(map.length>>i), y*(map.length>>i)+(map.length>>(i+1)), map.length>>i, map.length>>i, magnitude);
                }
            }
            magnitude*=SCALAR;
        }
        
        float max = map[0][0];
        float min = map[0][0];
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map.length; x++) {
                if (map[x][y] > max)
                {
                    max = map[x][y];
                }
                else if (map[x][y] < min)
                {
                    min = map[x][y];
                }
            }
        }
        
        for (int y = 0; y < map.length; y++)
        {
            for (int x = 0; x < map.length; x++)
            {
                map[x][y] = (map[x][y]-min)/(max-min);
            }
        }
        
        
        
    }
    
    private void Diamond(int x0, int y0, int w, int h, float magnitude)
    {
        int x1 = (x0+w)%map.length;
        int y1 = (y0+h)%map.length;
        
        int xMid = x0+w/2;
        int yMid = y0+h/2;
        float average = map[x0][y0];
        average += map[x0][y1];
        average += map[x1][y0];
        average += map[x1][y1];
        average /= 4;
        
        map[xMid][yMid] = average + (float)((Math.random()*2-1)*magnitude);
    }
    
    private void Square(int x0, int y0, int w, int h, float magnitude)
    {
        x0 %= map.length;
        y0 %= map.length;
        int x1 = (x0+w)%map.length;
        int y1 = (y0+h)%map.length;
        
        int xMid = (x0+w/2)%map.length;
        int yMid = (y0+h/2)%map.length;
        
        float average = map[x0][yMid];
        average += map[xMid][y0];
        average += map[x1][yMid];
        average += map[xMid][y1];
        average /= 4;
        
        map[xMid][yMid] = average + (float)((Math.random()*2-1)*magnitude);
        
        
    }
    
    public BufferedImage toBufferedImage() {
        BufferedImage output = new BufferedImage(map.length, map.length,BufferedImage.TYPE_INT_RGB);
        float max = map[0][0];
        float min = map[0][0];
        for (int y = 0; y < output.getHeight(); y++)
        {
            for (int x = 0; x < output.getWidth(); x++)
            {
                if (map[x][y] > max)
                {
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
    
    public float[][] getMap() { return map; }
    
    public String toString()
    {
        String output = "";
        
        for (int y = 0; y < map.length; y++)
        {
            for (int x = 0; x < map.length; x++)
            {
                output += String.format("%+5.3f ", map[x][y]);
            }
            output += "\n";
        }
        
        return output;
    }

}