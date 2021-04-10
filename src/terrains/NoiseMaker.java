package terrains;

//@author VividerAphid

public class NoiseMaker {

    public float[][] basicNoise2D(int width, int height){
        float[][] img = new float[width][height];
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                img[x][y] = (float) Math.random();
            }
        }
        return img;
    }
    
    public float[] basicNoise1D(int size){
        float[] output = new float[size];
        for(int r = 0; r < size; r++){
            output[r] = (float) Math.random();
        }
        return output;
    }
    
    public void perlinNoise(int count, float[] seed, int octaves, float[] output){
        for(int r =0; r < count; r++){
           float noise = 0.0f;
           float scale = 1.0f;
           float scaleAcc = 0.0f;
            for(int o = 0; o < octaves; o++){
            int pitch = count >> o;
            int sample1 = (r / pitch) * pitch;
            int sample2 = (sample1 + pitch) % count;
            
            float blend = (float)(r-sample1) / (float)pitch;
            float sample = (1.0f - blend) * seed[sample1] + blend * seed[sample2];
            noise += sample * scale;
            scaleAcc += scale;
            scale = scale / 2.0f;
            }
            output[r] = noise / scaleAcc;
        }
        
    }
    
    public float[][] perlinNoise2D(int width, int height, float[] seed, float bias, int octaves){
        float[][] output = new float[width][height];
        for(int x =0; x < width; x++){
          for(int y = 0; y < height; y++){
           float noise = 0.0f;
           float scale = 1.0f;
           float scaleAcc = 0.0f;
            for(int o = 0; o < octaves; o++){
            int pitch = width >> o;
            int xSample1 = (x / pitch) * pitch;
            int xSample2 = (xSample1 + pitch) % width;
            int ySample1 = (y / pitch) * pitch;
            int ySample2 = (ySample1 + pitch) % width;
            
            float blendX = (float)(x-xSample1) / (float)pitch;
            float blendY = (float)(y-ySample1) / (float)pitch;
            
            float sample1 = (1.0f - blendX) * seed[ySample1 * width + xSample1] + blendX * seed[ySample1 * width + xSample2];
            float sample2 = (1.0f - blendX) * seed[ySample2 * width + xSample1] + blendX * seed[ySample2 * width + xSample2];
            
            noise += (blendY * (sample2 - sample1) + sample1) * scale;
            scaleAcc += scale;
            scale = scale / bias;
            }
            output[x][y] = noise / scaleAcc;
        }
        }
        return output;
}
}