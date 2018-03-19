/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world.terrain.generators;

import misc.MiscMath;
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
        this.setParameter("waves", "1");
    }
    
    @Override
    public void generate(World w, int layer) {
        
        
        float waves = Float.parseFloat(getParameter("waves"));
        
        ArrayList<Double> sineFRandom = new ArrayList<>();
        ArrayList<Double> sineARandom = new ArrayList<>();
        
        // Random numbers to create the sine waves
        Random random = new Random(System.currentTimeMillis());
        
        // Add random numbers to sineFRandom
        // With define the frequency of its sine wave. Multiplier of [0.7, 4.7]
        for (int a = 0; a < waves; a++) {
            sineFRandom.add(((random.nextDouble() * 4) + 0.7));
        }
        
        // Add random amplitudes to the waves. Bounded [-1, 1]
        for (int a = 0; a < waves; a++) {
            sineARandom.add(((random.nextDouble() * 0.5) - 0.25));
        }
        
        double h;
        double total;
       
        // For each column determine which tiles to draw for a smooth sine wave
        for (int i = 0; i < w.columns(); i++){
            
            // Reset total for each column pass
            total = 0;
            
            // Number to pass into Math.sin function.
            double x_sine = (double)i / (double)w.columns();
            
            
            // Calculate the average sine wave height for this x position
            // Normalizes the multiple sine waves into one sine wave.
            for (int a = 0; a < sineFRandom.size(); a++) {
                
                // Change range from [0, 1] -> [0, 2PI] * frequency multiplier
                x_sine = x_sine * 2 * Math.PI * sineFRandom.get(a);
                
                // Randomize amplitude of the wave
                x_sine += sineARandom.get(a);
                
                h = ((Math.sin(x_sine) + 1) / 2) * w.rows();
                h = (h / 1.25) + 0.1;
                total += h;
            }
            
            // Take average height of all waves
            int yStart = (int)Math.floor(total / sineFRandom.size());
            
            
            // Print tiles below start tile
            while (yStart < w.rows()){
                
                // Draw tiles for that column
                w.setTile(i, yStart, layer, true);
                yStart++;
            }
        }
    }
}
