package world.terrain.generators;

import gui.Canvas;
import java.awt.Color;
import java.awt.List;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import world.World;
import world.terrain.Generator;
import world.terrain.misc.Perlin;

/**
* This is a basic Water Generator that generates "lakes" that are simply
* randomized rectangles set within specified dimensional constraints
* rivers are also generated, starting at random position within a lake and
* randomly meandering until the specified river length is reached
* @author Joseph Cherneske
*/
public class WaterGenerator extends Generator {
    
    public WaterGenerator(){
        super();
        this.setParameter("lakes", "3");
        this.setParameter("max", "20");
        this.setParameter("min", "10");
        this.setParameter("rlength", "100");
        this.setParameter("elevation", ".8");
    }
    @Override
    /*
    * Copy/Paste this for sample generation: WaterGenerator:tile=6,lakes=32,max=40,min=10,rlength=100
    */
    public void generate(World w, int layer) {
        /*Parameters
         ==========*/
        
        /* lakes: the total number of lakes to generate, which also will be the
         * total number of rivers
         */
        int lakes = Integer.parseInt(getParameter("lakes"));
        
        //max, min: the minimum and maximum dimensional ranges of the rectangular lakes
        int max = Integer.parseInt(getParameter("max"));
        int min = Integer.parseInt(getParameter("min"));
        
        //rlength: the length of the rivers to be generated
        int rlength = Integer.parseInt(getParameter("rlength"));
        
        int lakeoctaves = Integer.parseInt(getParameter("lakeoctaves"));
        
        int riverchecklen = Integer.parseInt(getParameter("riverchecklen"));
        
         /*Variables
         ==========*/
         
        /* points is an ArrayList of integer Arrays used to store the starting
         * points for the generated lakes
         */
        ArrayList<int[]> points = new ArrayList<>();
        
        //Create a new random object rand and get set the seed from the World
        Random rand = new Random();
        rand.setSeed(getSeed());
        
        Perlin perlin = new Perlin();
        
        //TEMPORARY heightmap we will use for river generation along with buffered image
        float[][] hmap = perlin.generatePerlinNoise(perlin.generateWhiteNoise(w.columns(), w.rows(), getSeed()), 5);
        
        BufferedImage bi = new BufferedImage(hmap.length,hmap[0].length, BufferedImage.TYPE_INT_RGB);
        for(int hi = 0; hi < hmap.length; hi++){
            for(int hj = 0; hj < hmap[0].length; hj++){
                bi.setRGB(hi, hj, (new Color(hmap[hi][hj],hmap[hi][hj],hmap[hi][hj]).getRGB()));
            }
        }
        
        
        
        
        
        //Now generate our random starting points for the lakes
        for(int i=0;i<lakes;i++){
            points.add(new int[]{rand.nextInt(w.columns()),rand.nextInt(w.rows())});
            System.out.println("Point " + i + " x=" + points.get(points.size()-1)[0] + " y=" + points.get(points.size()-1)[1]);
        }
        
        //Now draw rectangles on the world using the generated coordinates
        for(int[] x: points){
            /* set the width and height of the rectangles using a random number
             * from parameter min to max
             */
            int rwidth = min+rand.nextInt(max-min);
            int rheight = min+rand.nextInt(max-min);
            
            //New: use perlin noise algorithm to make the lakes look more realistic
            
            
            float[][] perlinmap = perlin.generatePerlinNoise(perlin.generateWhiteNoise(rwidth, rheight, getSeed()),lakeoctaves);
            //create a lake "mask", basically a rectangular gradient.
            float[][] lakemask = new float[rwidth][rheight];
             
            for(int i=0;i<rwidth;i++){
                for(int j=0;j<rheight;j++){
                    if(i<=j*rwidth/rheight&&i<=rwidth-j*rwidth/rheight){//top
                        lakemask[i][j]= ((float)i/rwidth*2);
                    }else if(i<=j*rwidth/rheight&&i>rwidth-j*rwidth/rheight){//right
                        lakemask[i][j]=((float)(rheight-j)/rheight*2);
                    }else if(i>j*rwidth/rheight&&i>rwidth-j*rwidth/rheight){//down
                        lakemask[i][j]= ((float)(rwidth-i)/rwidth*2);
                    }else if(i>j*rwidth/rheight&&i<=rwidth-j*rwidth/rheight){//left
                        lakemask[i][j]=((float)j/rheight*2);
                    }else{
                        lakemask[i][j]=0;
                    }
                }
            }
            
            
            //Now draw the rectangles on the World!
            for(int i=0; i<rheight; i++){
                for(int j=0; j<rwidth; j++){
                    if(x[0]+j>w.columns()-1||x[1]+i>w.rows()-1)continue;
                    //w.setTile(x[0]+j,x[1]+i, layer, true);
                    if (perlinmap[j][i]+lakemask[j][i] > Double.parseDouble(getParameter("elevation"))) w.setTile(x[0]+j, x[1]+i, layer, true);
                    
                }
            }
            
            int next[] = x;
            
            
            //river starting point in middle of lake
            next[0]+=rwidth/2;
            next[1]+=rheight/2;
            
            for(int i=0; i<rlength;i++){
                //determine the next direction by adding the next few
                //tiles of heightmap
                float dsum[] = {0,0,0,0};
                
                for(int j=0;j<riverchecklen;j++){
                    try{
                    dsum[0]+=hmap[next[0]][next[1]+j];
                    dsum[1]+=hmap[next[0]+j][next[1]];
                    dsum[2]+=hmap[next[0]][next[1]-j];
                    dsum[3]+=hmap[next[0]-j][next[1]];
                    }catch(java.lang.ArrayIndexOutOfBoundsException e){
                        //just don't do anything
                    }
                }
                
                int direction = 0;
                float lowest = dsum[0];
                
                //find the lowest elevation
                for(int j=1;j<4;j++){
                    if(dsum[j]<lowest){
                        lowest=dsum[j];
                        direction=j;
                    }
                }
                
                
                switch(direction){
                    case 0://north
                        next[1]+=1;
                        break;
                    case 1://east
                        next[0]+=1;
                        break;
                    case 2://south
                        next[1]-=1;
                        break;
                    case 3://west
                        next[0]-=1;
                }
                try{
                    w.setTile(next[0], next[1], layer, true);
                }catch(java.lang.ArrayIndexOutOfBoundsException e){
                    System.out.println("boop");
                    break;
                }
            }
                
                
                
        }
        
        //FOR DEBUGGING PURPOSES, OUTPUTS THE HEIGHTMAP AND WATER
        //go over the world and draw the water onto the image
        for(int i = 0; i < w.width()-1; i++){
            for(int j = 0; j < w.height()-1; j++){
                if(w.getTile(i, j, layer)==0){
                    bi.setRGB(i, j, (new Color(0,0,1).getRGB()));
                }
                
            }
        }
        
        try {
            ImageIO.write(bi, "png", new File("C:/Users/Joe/Documents/heightmap.png")); //Make sure to change the file location.
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

