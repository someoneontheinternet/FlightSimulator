package entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

	private Vector3f position = new Vector3f(0, 3f, 0);
	private float pitch;
	private float yaw;
	private float roll;

	private Player player;
	private float playerYaw;
	private float distanceFromPlayer = 50;

	private final float moveValue = 0.1f;

	public Camera(Player player) {
		this.player = player;
		playerYaw = player.getRotY();
	}

	public void move() {
		calculateZoom();
		calculatePitch();
		calculateYaw();

		float hd = calculateHorizontalDistance();
		float vd = calculateVerticalDistance();

		calculateCameraPosition(hd, vd);
		this.yaw = 180 - (player.getRotY() + playerYaw);

	}

	private void calculatePitch() {
		if (Mouse.isButtonDown(0)) {
			float pitchChange = Mouse.getDY() * 0.1f;
			pitch -= pitchChange;

			if (pitch > 80)
				pitch = 80f;
			else if (pitch <= 1)
				pitch = 1f;

		}
	}

	private void calculateYaw() {
		if (Mouse.isButtonDown(0)) {
			float yawChange = Mouse.getDX() * 0.1f;
			playerYaw -= yawChange;
		}
	}

	private void calculateCameraPosition(float hd, float vd) {
		float theta = player.getRotY() + playerYaw;
		float offsetX = (float) (hd * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (hd * Math.cos(Math.toRadians(theta)));

		position.x = player.getPosition().x - offsetX;
		position.z = player.getPosition().z - offsetZ;
		position.y = player.getPosition().y + vd;

	}

	private float calculateHorizontalDistance() {
		float hD = (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
		if (hD < 0)
			hD = 0;
		return hD;
	}

	private float calculateVerticalDistance() {
		float vD = (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
		if (vD < 0)
			vD = 0;
		return vD;
	}

	private void calculateZoom() {
		float zoomLevel = Mouse.getDWheel() * 0.005f;
		distanceFromPlayer -= zoomLevel;
		if (distanceFromPlayer < 10f)
			distanceFromPlayer = 10f;
		else if (distanceFromPlayer > 200f)
			distanceFromPlayer = 200f;

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
