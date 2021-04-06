package Entities;

//@author VividerAphid

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;


public class StaticCamera {
    protected Vector3f position = new Vector3f(0,0,0);
    protected float pitch; //x
    protected float yaw; //y
    protected float roll; //z
    
    public StaticCamera(){}
    public StaticCamera(Vector3f position){
        this.position = position;
    }
    public StaticCamera(Vector3f position, float pitch, float yaw, float roll){
        this.position = position;
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
    }


    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }
        
}
