package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import renderEngine.DisplayManager;
import terrain.Terrain;

public class Player extends Entity {

	private static float RUN_SPEED = -45;
	private static final float TURN_SPEED = 45;
	private static final float GRAVITY = -0.1f;

	public float velocity = 0;
	public float sideVelocity = 0;
	public float rotY = 0;
	
	public float uVelocity = 0;
	public float acceleration = 0;
	
	public boolean isInAir = false;
	
	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}
	
	public void increaseRotationY(float r) {
		rotY += r;
	}
	
	public void move(Terrain terrain) {

		float frameSeconds = DisplayManager.getFrameTimeSeconds();
		
		Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
		float mdx = Mouse.getDX();
		Mouse.setGrabbed(true);
		
		//System.out.println(mdx);
		rotY = (float) mdx * -300f * frameSeconds;
		
		//Keyboard input
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			velocity = -RUN_SPEED;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			velocity = RUN_SPEED;
		} else {
			velocity = 0;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			sideVelocity = RUN_SPEED;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			sideVelocity = -RUN_SPEED;
		} else {
			sideVelocity = 0;
		}
		
		super.increaseRotation(0, rotY * frameSeconds, 0);
		
		// Front
		float distance = velocity * frameSeconds;

		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		super.increasePosition(dx, 0, dz);
		
		// Side
		float sideDistance = sideVelocity * frameSeconds;

		dx = (float) (sideDistance * Math.sin(Math.toRadians(super.getRotY() - 90f)));
		dz = (float) (sideDistance * Math.cos(Math.toRadians(super.getRotY() - 90f)));
		super.increasePosition(dx, 0, dz);
		
		float vNetForce = GRAVITY;

		if (!isInAir && Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			 isInAir = true;
			 uVelocity += 2f;
		} 
		
		uVelocity += vNetForce;
		
		float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		super.increasePosition(0, uVelocity, 0);
		
		if (super.getPosition().y < terrainHeight + 1.5f) {
			super.getPosition().y = terrainHeight + 1.5f;
			uVelocity = 0;
			isInAir = false;
		}
		
	}
	
	public String toString() {
		return getPosition().toString();
	}

}
