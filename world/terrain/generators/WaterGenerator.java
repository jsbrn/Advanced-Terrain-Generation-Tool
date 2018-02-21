/*
 * This is a basic Water Generator that generates "lakes" that are simply
 * randomized rectangles set within specified dimensional constraints
 * rivers are also generated, starting at a lake and randomly meandering until
 * the specified river length is reached
 */
package world.terrain.generators;

import java.awt.List;
import java.util.ArrayList;
import java.util.Random;
import world.World;
import world.terrain.Generator;

/**
 *
 * @author Joe
 */
public class WaterGenerator extends Generator {
    
    @Override
    public void generate(World w){
        //Parameters
        //==========
        //set this to whatever tile type you want water to be
        int t = Integer.parseInt(getParameter("tile"));
        //number of lakes to generate
        int lakes = Integer.parseInt(getParameter("lakes"));
        //maximum dimensions of lakes generated h+w
        int max = Integer.parseInt(getParameter("max"));
        int min = Integer.parseInt(getParameter("min"));
        //maximum length of rivers
        int rlength = Integer.parseInt(getParameter("rlength"));
        
        //variables
        ArrayList<int[]> points = new ArrayList<>();
        
        //set our random seed from the world
        Random rand = new Random();
        rand.setSeed(w.getSeed());
        
        //lets pick our random midpoints
        for(int i=0;i<lakes;i++){
            points.add(new int[]{rand.nextInt(w.columns()),rand.nextInt(w.rows())});
            System.out.println("Point " + i + " x=" + points.get(points.size()-1)[0] + " y=" + points.get(points.size()-1)[1]);
        }
        
        //now draw stuff on the world
        for(int[] x: points){
            int rwidth = min+rand.nextInt(max-min);
            int rheight = min+rand.nextInt(max-min);
            //draw some rectangles
            for(int i=0; i<rheight; i++){
                for(int j=0; j<rwidth; j++){
                    if(x[0]+j>w.columns()-1||x[1]+i>w.rows()-1)continue;
                    w.setTile(t, x[0]+j,x[1]+i);
                }
            }
            //now make some rivers
            
            //starting point and direction
            int direction = rand.nextInt(4);
            int newDir;
            int last[] = x;
            
            last[0]+=rwidth/2;
            last[1]+=rheight/2;
            
            riverloop:
            for(int i=0; i<rlength;i++){
                //pick a straight length
                for(int j=0; j<rand.nextInt(15); j++){
                    switch(direction){
                        case 0://north
                            last[1]+=1;
                            break;
                        case 1://east
                            last[0]+=1;
                            break;
                        case 2://south
                            last[1]-=1;
                            break;
                        case 3://west
                            last[0]-=1;
                    }
                    
                    //end the river creation if we hit the edge
                    if(last[0]>w.columns()-1||last[1]+i>w.rows()-1||last[0]<0||last[1]<0)break riverloop;
                    
                    w.setTile(t, last[0], last[1]);
                    i++;
                }
                //change direction, ensure we don't go backwards
                while(true){
                    newDir = rand.nextInt(4);
                    if(newDir!=(direction-2)%4||newDir!=(direction+2)%4){
                        break;
                    }
                }
                
                
                direction = newDir;
                
            }
        }
    }
}
