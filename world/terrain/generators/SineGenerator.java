/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world.terrain.generators;

import world.World;
import world.terrain.Generator;
import java.util.*;
/**
 * Creates side scrolling terrain based on the interference of sine waves.
 * @author Ryan Swaggert
 * 
 */
public class SineGenerator extends Generator {
    
    public SineGenerator() {
        super();
        // One string parameter with an array of parameters
        this.setParameter("waves", "1.0,0.0,0.0,0.4|1.0,0.0,0.6,1.0");
    }
    
    @Override
    public void generate(World w, int layer) {
        
        
        // Paremeters for a wave
        // frequency, amplitude, xMin, xMax
        String waves = getParameter("waves");
        String[] wavesArray = waves.split("\\|");
        System.out.println(wavesArray[0]);
        System.out.println(wavesArray[1]);
        
        // Necessary information to store
        ArrayList<Double> frequencies = new ArrayList<>();
        ArrayList<Double> amplitudes = new ArrayList<>();
        ArrayList<Double> xMin = new ArrayList<>();
        ArrayList<Double> xMax = new ArrayList<>();
        ArrayList<Double> height = new ArrayList<>();
        
        // Parse info from user defined string
        for (int a = 0; a < wavesArray.length; a++) {
            
            String[] waveValues = wavesArray[a].split(",");
            System.out.println(waveValues[0]);
            // Ensure frequency is not zero, as entry should not be <0
            frequencies.add( Double.parseDouble(waveValues[0]) + 0.001 );
            amplitudes.add( Double.parseDouble(waveValues[1]) );
            xMin.add( Double.parseDouble(waveValues[2]) );
            xMax.add( Double.parseDouble(waveValues[3]) );
        }
        
        // Need height value at each column
        for (int a = 0; a < w.columns(); a++) 
            height.add(0.0);
        
        double h;
        
        // Have an array that gets modified every loop
        // Average height array every loop
        for (int a = 0; a < wavesArray.length; a++) {
            
            int min = (int)Math.floor(xMin.get(a) * w.columns());
            int max = (int)Math.floor(xMax.get(a) * w.columns());
            
            
            // Determine height for each column of sine wave
            for (int i = min; i < max; i++) {
                
                
                // Number to pass into Math.sin function.
                double x_sine = (double)i / (double)w.columns();
                
                // Change range from [0, 1] -> [0, 2PI] * frequency multiplier
                x_sine = x_sine * 2 * Math.PI * frequencies.get(a);
                
                // Change amplitude of the wave
                x_sine += amplitudes.get(a);
                
                // Height value
                h = ((Math.sin(x_sine) + 1) / 2) * w.rows();
                
                // Below 0.2 is filled
                // h = (h / 1.25) + 0.1;
                
                // Add height values
                // Division should effective average out waves for that column
                height.set(i, (height.get(a) + h) / 2);
            }
        }
            
        
        for (int i = 0; i < w.columns(); i++) {
            
            int yStart = (int)Math.floor(height.get(i));
            
            // Print tiles below start tile
            while (yStart < w.rows() && yStart != 0){
                
                // Draw tiles for that column
                w.setTile(i, yStart, layer, true);
                yStart++;
            }
        }
    }
}
