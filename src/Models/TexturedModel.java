package Models;

//@author VividerAphid

import Textures.ModelTexture;
import org.lwjgl.util.vector.Vector3f;


public class TexturedModel {

    protected RawModel rawModel;
    protected ModelTexture texture;
    
    public TexturedModel(RawModel model, ModelTexture texture){
        this.rawModel = model;
        this.texture = texture;
    }
    
    public RawModel getRawModel(){
        return rawModel;
    }
    
    public ModelTexture getTexture(){
        return texture;
    }
}
