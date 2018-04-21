package world.terrain.misc;

import java.util.Random;

/**
 * @author Ryan Swaggert
 * Source code credit: http://devmag.org.za/2009/04/25/perlin-noise/
 */

public class PerlinNoise {

    /**
     * Returns a 2D array of random values [0, 1] for Perlin Noise algorithm.
     * @param width Number of columns for terrain you want to generate.
     * @param height Number of rows for terrain you want to generate.
     * @param seed The seed to use for the generation.
     * @return A 2D array of random values [0, 1].
     */
    public float[][] generateWhiteNoise(int width, int height, long seed) {
        Random random = new Random(seed);
        float[][] noise = new float[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                noise[i][j] = random.nextFloat() % 1;
            }
        }
        return noise;
    }

    /**
     * Linearly interpolate two values.
     * @param x0 First value to linearly interpolate.
     * @param x1 Second value to linearly interpolate.
     * @param alpha Determines whether returned value will be closer to x0 or x1.
     * @return If alpha is to 0, the closer the returned value will be to x0; the closer alpha is to 1, the closer the returned value will be to x1.
     */ 
    private float interpolate(float x0, float x1, float alpha) {
        return x0 * (1 - alpha) + alpha * x1;
    }

    /**
     * Create smoothed noise.
     * @param baseNoise Noise to smooth out. Should be white noise.
     * @param octave Number of sample noise arrays to smooth out.
     * @return A 2D array of values [0, 1] that are smoothed out in relation to each other.
     */
    private float[][] generateSmoothNoise(float[][] baseNoise, int octave) {
        int width = baseNoise.length;
        int height = baseNoise[0].length;
        float[][] smoothNoise = new float[width][height];
        int samplePeriod = 1 << octave; // calculates 2 ^ k
        float sampleFrequency = 1.0f / samplePeriod;

        for (int i = 0; i < width; i++) {
            //calculate the horizontal sampling indices
            int sample_i0 = (i / samplePeriod) * samplePeriod;
            int sample_i1 = (sample_i0 + samplePeriod) % width; //wrap around
            float horizontal_blend = (i - sample_i0) * sampleFrequency;

            for (int j = 0; j < height; j++) {
                //calculate the vertical sampling indices
                int sample_j0 = (j / samplePeriod) * samplePeriod;
                int sample_j1 = (sample_j0 + samplePeriod) % height; //wrap around
                float vertical_blend = (j - sample_j0) * sampleFrequency;
                //blend the top two corners
                float top = interpolate(baseNoise[sample_i0][sample_j0],
                baseNoise[sample_i1][sample_j0], horizontal_blend);
                //blend the bottom two corners
                float bottom = interpolate(baseNoise[sample_i0][sample_j1],
                baseNoise[sample_i1][sample_j1], horizontal_blend);
                //final blend
                smoothNoise[i][j] = interpolate(top, bottom, vertical_blend);
            }
        }
        return smoothNoise;
    }

    /**
     * Create Perlin noise.
     * @param baseNoise Noise to smooth out. Should be white noise.
     * @param octaveCount How many octaves of different wavelength and frequency to generate.
     * @return A 2D array of values [0, 1] that are smoothed out, normalized blended arrays.
     */
    public float[][] generatePerlinNoise(float[][] baseNoise, int octaveCount) {
        
        int width = baseNoise.length;
        int height = baseNoise[0].length;
        float[][][] smoothNoise = new float[octaveCount][][]; //an array of 2D arrays containing
        float persistance = 0.5f;

        //generate smooth noise
        for (int i = 0; i < octaveCount; i++) {
            smoothNoise[i] = generateSmoothNoise(baseNoise, i);
        }

        float[][] perlinNoise = new float[width][height];
        float amplitude = 1.0f;
        float totalAmplitude = 0.0f;

        //blend noise together
        for (int octave = octaveCount - 1; octave >= 0; octave--) {
            amplitude *= persistance;
            totalAmplitude += amplitude;
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    perlinNoise[i][j] += smoothNoise[octave][i][j] * amplitude;
                }
            }
        }

        //normalisation
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                perlinNoise[i][j] /= totalAmplitude;
            }
        }
        return perlinNoise;
    }
}