package Models;

//@author VividerAphid

import Textures.ModelTexture;
import org.lwjgl.util.vector.Vector3f;


public class HuedModel extends TexturedModel{

    private Vector3f hue;
    
    public HuedModel(RawModel model, ModelTexture texture, Vector3f hue){
        super(model, texture);
        this.hue = hue;
    }
    
    public Vector3f getHue(){
        return this.hue;
    }
}
