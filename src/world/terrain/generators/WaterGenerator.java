package world.terrain.generators;

import gui.Canvas;
import java.awt.List;
import java.util.ArrayList;
import java.util.Random;
import world.World;
import world.terrain.Generator;

/**
* This is a basic Water Generator that generates "lakes" that are simply
* randomized rectangles set within specified dimensional constraints
* rivers are also generated, starting at random position within a lake and
* randomly meandering until the specified river length is reached
* @author Joseph Cherneske
*/
public class WaterGenerator extends Generator {
    
    
    @Override
    /*
    * Copy/Paste this for sample generation: WaterGenerator:tile=6,lakes=32,max=40,min=10,rlength=100
    */
    public void generate(World w, int layer) {
        /*Parameters
         ==========*/
        
        //tile: the designated tile attribute number used for water
        int t = Integer.parseInt(getParameter("tile"));
        
        /* lakes: the total number of lakes to generate, which also will be the
         * total number of rivers
         */
        int lakes = Integer.parseInt(getParameter("lakes"));
        
        //max, min: the minimum and maximum dimensional ranges of the rectangular lakes
        int max = Integer.parseInt(getParameter("max"));
        int min = Integer.parseInt(getParameter("min"));
        
        //rlength: the length of the rivers to be generated
        int rlength = Integer.parseInt(getParameter("rlength"));
        
         /*Variables
         ==========*/
         
        /* points is an ArrayList of integer Arrays used to store the starting
         * points for the generated lakes
         */
        ArrayList<int[]> points = new ArrayList<>();
        
        //Create a new random object rand and get set the seed from the World
        Random rand = new Random();
        rand.setSeed(w.getSeed());
        
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
            
            //Now draw the rectangles on the World!
            for(int i=0; i<rheight; i++){
                for(int j=0; j<rwidth; j++){
                    if(x[0]+j>w.columns()-1||x[1]+i>w.rows()-1)continue;
                    w.setTile(x[0]+j,x[1]+i, layer, true);
                }
            }
            
            /* River Generation
             * ================
             * Define the starting direction of the river, which is
             * represented by an integer from 0 to 3:
             * 0 being North, 1 being East, 2 being South, and 3 being West
             */
            int direction = rand.nextInt(4); //Sets the random direction
            
            /* Define two more int variables,
             * newDir: will temporarily hold the river's next direction (0-3)
             * next[]: holds the next river tile corridinate, initilizated by the
             * x and y coordinates of the last lake generated
             * sectionLength: holds the length of the current section of river
             * being currently generated
             * rTries: keeps track of how many times we tried to see if we can
             * draw a river
            */
            int newDir; 
            int next[] = x;
            int sectionLength;
            int rTries;
            
            //Randomly set the river starting point somewhere in the lake
            next[0]+=rand.nextInt(rwidth/2);
            next[1]+=rand.nextInt(rheight/2);
            
            /* outer river loop, labeled 'roverloop', which loops for the set
             * river length by the rlength parameter
             */
            riverloop:
            for(int i=0; i<rlength;i++){
                /* Generate a random length from 5 to 20 tiles for the next
                 * section of river to be drawn on the world
                */
                
                /* if this is the first loop lets make sure we leave the confines
                 * of the lake, otherwise continue with generating a random length
                 * and finding a new direction
                */
                if(i==0){
                    sectionLength = max+1;
                }else{
                    sectionLength = 5 + rand.nextInt(15);
                    
                    /* Now that we know the length of the next section, lets set the
                    * next direction...
                    *
                    * The while loop keeps going forever until we find a new direction
                    * that isn't backwards so we don't draw a length of river onto
                    * itself, and also does not cross paths with another body of
                    * water or river. If it is determined that there will be collision
                    * using the for loops within the switch statements,
                    * the section length is reduced
                   */

                   rivercheck:
                   while(true){
                       /* If sectionLength has been widdled down to 0 then just
                        * give up trying to make a river :(
                        */
                       if(sectionLength<=0){System.out.println("Can't make that river!"); break riverloop;}
                       
                       newDir = rand.nextInt(4);
                       if(newDir!=(direction-2)%4||newDir!=(direction+2)%4){
                           riverswitch:
                           switch(direction){
                           case 0://check north
                               for(int chk = 1; chk<=sectionLength; chk++){
                                   try{
                                       if(w.getTile(next[0], next[1]+chk)==t){
                                            //Set the section length to chk-1 and try again
                                            sectionLength = chk-1;
                                            continue rivercheck;
                                        }
                                   }catch(java.lang.ArrayIndexOutOfBoundsException exception){
                                       break riverloop;
                                   }
                               }
                               break rivercheck;//we went through without issues
                           case 1://check east
                               for(int chk = 1; chk<=sectionLength; chk++){
                                   try{
                                       if(w.getTile(next[0]+chk, next[1])==t){
                                       //Set the section length to chk-1 and try again
                                       sectionLength = chk-1;
                                       continue rivercheck;
                                       }
                                   //catch if we go outside the world and break riverloop
                                   }catch(java.lang.ArrayIndexOutOfBoundsException exception){
                                       break riverloop;
                                   }
                               }
                               break rivercheck;//we went through without issues
                           case 2://check south
                               for(int chk = 1; chk<=sectionLength; chk++){
                                   try{
                                       if(w.getTile(next[0], next[1]-chk)==t){
                                            //Set the section length to chk-1 and try again
                                            sectionLength = chk-1;
                                            continue rivercheck;
                                        }
                                   }catch(java.lang.ArrayIndexOutOfBoundsException exception){
                                       break riverloop;
                                   }
                               }
                               break rivercheck;//we went through without issues
                           case 3://check west
                               for(int chk = 1; chk<=sectionLength; chk++){
                                   try{
                                        if(w.getTile(next[0]-chk, next[1])==t){
                                            //Set the section length to chk-1 and try again
                                            sectionLength = chk-1;
                                            continue rivercheck;
                                        }
                                   }catch(java.lang.ArrayIndexOutOfBoundsException exception){
                                       break riverloop;
                                   }
                               break rivercheck;//we went through without issues
                               }
                           }
                       }
                   }

                   //New direcion has been found, so set the current diection to it!
                   direction = newDir;
                }
                
                /* inner river loop, which loops for the size of sectionLength
                 * drawing a straight line of tiles on the world
                */
                for(int j=0; j<sectionLength; j++){
                    /* Check the current direction and shift the coordinates of the
                     * next tile to be drawn on accordingly
                     */
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
                    
                    /* Now make sure we are in still in the world's bounds before
                     * we try to draw a tile, and if we're out of bounds break
                     * out of the riverloop
                     */
                    if( next[0]>w.columns()-1||
                        next[1]+i>w.rows()-1||
                        next[0]<0||
                        next[1]<0)
                        break riverloop;
                    
                    //Everything is OK, draw the designated water tile at the next coordinates
                    w.setTile(next[0], next[1], layer, true);
                    
                    //Increment i by one since we used up one of our 'rlength'
                    i++;
                }
                
                
                
            }
        }
    }
}
