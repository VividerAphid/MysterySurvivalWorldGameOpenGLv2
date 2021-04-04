package Entities;

//@author VividerAphid

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;


public class Camera {
    private Vector3f position = new Vector3f(0,0,0);
    private float speed = 0.02f;
    private float pitch;
    private float yaw;
    private float roll;
    
    public Camera(){}
    public Camera(Vector3f position){
        this.position = position;
    }

    public void move(){
        if(Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_COMMA)){
            position.z -= this.speed;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_E)){
            position.x += this.speed;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_A)){
            position.x -= this.speed;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_O)){
            position.z += this.speed;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
            position.y += this.speed;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
            position.y -= this.speed;
        }
    }
    
    public void setSpeed(float speed){
        this.speed = speed;
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
