package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

	private Vector3f position = new Vector3f(0, 3f, 0);
	private float pitch;
	private float yaw;
	private float roll;
	
	private float moveValue = 0.025f;

	public Camera() {
		yaw = 0;
	}

	public void resetValue() {
		if (yaw < -180.0f) {
			yaw = 180.0f;
		}
		if (yaw > 180.0f) {
			yaw = -180.0f;
		}
	}
	
	public void move() {
		
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			moveValue = 0.2f;
		} else {
			moveValue = 0.025f;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			position.z -= moveValue;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			position.x += moveValue;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			position.x -= moveValue;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			if (yaw < 90 || yaw > -90) {
				position.z += moveValue;
			} else {
				position.z -= moveValue;
			}
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
			position.y += 0.1f;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
			position.y -= 0.1f;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			yaw -= 1.0f;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			yaw += 1.0f;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			pitch -= 0.3f;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			pitch += 0.3f;
		}
		
		resetValue();
		
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
