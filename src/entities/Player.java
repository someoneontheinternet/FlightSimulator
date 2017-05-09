package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import renderEngine.DisplayManager;
import terrain.Terrain;

public class Player extends Entity {

	private static float RUN_SPEED = -45;
	private static final float TURN_SPEED = 45;
	private static final float GRAVITY = -9.8f / 4f;

	private static final float DRAG = -20;
	
	public float velocity = 0;
	public float rotY = 0;
	public float uVelocity = 0;
	
	public float acceleration = 0;
	
	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}
	
	public void move(Terrain terrain) {
		checkInputs();
		
		float frameSeconds = DisplayManager.getFrameTimeSeconds();
		
		// Calculate Lift
		//uVelocity = calculateLift(velocity) * frameSeconds;
		
		super.increaseRotation(0, rotY * frameSeconds, 0);
		float distance = velocity * frameSeconds;

		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		super.increasePosition(dx, 0, dz);

		uVelocity += GRAVITY * frameSeconds;

		float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		super.increasePosition(0, uVelocity, 0);
		
		if (super.getPosition().y < terrainHeight + 1.5f) {
			super.getPosition().y = terrainHeight + 1.5f;
			uVelocity = 0;
		}
		
	}

	private void checkInputs() {
		
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			RUN_SPEED = -75;
		} else {
			RUN_SPEED = -45;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			this.velocity = RUN_SPEED;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			this.velocity = (float) ((-0.5) * RUN_SPEED);
		} else {
			this.velocity = 0;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			this.rotY = TURN_SPEED;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			this.rotY = -TURN_SPEED;
		} else {
			this.rotY = 0;
		}
		
	}
	
	public String toString() {
		return getPosition().toString();
	}

}
