import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.Renderer;
import shaders.StaticShader;
import texture.ModelTexture;
import utils.OBJLoader;

public class Main {

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		Loader loader = new Loader();
		StaticShader shader = new StaticShader();

		Renderer renderer = new Renderer(shader);


		RawModel model = OBJLoader.loadObj("dragon", loader);
		
		ModelTexture texture = new ModelTexture(loader.loadTexture("model/default-blue"));
		TexturedModel texturedModel = new TexturedModel(model, texture);

		Entity entity = new Entity(texturedModel, new Vector3f(0, 0, -25), 0, 0, 0, 1);

		Camera camera = new Camera();

		while (!Display.isCloseRequested()) {
			renderer.prepare();

			camera.move();

			entity.increaseRotation(0.0f, 1f, 0.0f);
			
			shader.start();
			shader.loadViewMatrix(camera);
			renderer.render(entity, shader);
			shader.stop();
			DisplayManager.updateDisplay();
		}

		shader.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
		System.exit(0);
	}
}
