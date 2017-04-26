import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import models.RawModel;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import terrain.Terrain;
import texture.ModelTexture;
import texture.TerrainTexture;
import texture.TerrainTexturePack;
import utils.ModelData;
import utils.OBJLoader;

public class Main {

	public static void main(String[] args) {

		Random r = new Random();
		
		DisplayManager.createDisplay();
		Loader loader = new Loader();

		// Terrain
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("default-green"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("default-red"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("default-yellow"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("default-blue"));
		TerrainTexturePack ttp = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		
		Terrain terrain1 = new Terrain(0, -1, loader, ttp, blendMap);
		
		// Tree Model
		ModelData dTree = OBJLoader.loadOBJ("d-tree");
		RawModel dTreeModel = loader.loadToVAO(dTree.getVertices(), dTree.getTextureCoords(), dTree.getNormals(), dTree.getIndices());
		
		// Fighter Jet
		ModelData fj = OBJLoader.loadOBJ("fighter-jet");
		RawModel fjModel = loader.loadToVAO(fj.getVertices(), fj.getTextureCoords(), fj.getNormals(), fj.getIndices());
		TexturedModel staticDTree = new TexturedModel(dTreeModel, new ModelTexture(loader.loadTexture("d-tree-texture")));
		ModelTexture dTreeTexture = staticDTree.getTexture();
		dTreeTexture.setShineDamper(10);
		dTreeTexture.setReflectivity(1);
		
		Entity dTreeEntity = new Entity(staticDTree, new Vector3f(0, 0, 0), 0, 0, 0, 1);
		
		TexturedModel staticModel = new TexturedModel(fjModel, new ModelTexture(loader.loadTexture("fighter-jet-texture")));
		ModelTexture texture = staticModel.getTexture();
		texture.setShineDamper(20);
		texture.setReflectivity(1);

		Player player = new Player(staticModel, new Vector3f(0, 2, 0), 0, 0, 0, 1);
		
		// Light
		Light light = new Light(new Vector3f(50, 10000, 50), new Vector3f(1, 1, 1));

		Camera camera = new Camera(player);

		MasterRenderer renderer = new MasterRenderer();
		
		while (!Display.isCloseRequested()) {

			player.move();
			camera.move();

			// Adding to render queue
			renderer.processEntity(player);
			renderer.processEntity(dTreeEntity);
			
			// Adding to terrain render queue
			renderer.processTerrain(terrain1);

			
			renderer.render(light, camera);
			DisplayManager.updateDisplay();
		}

		renderer.cleanUp();

		loader.cleanUp();
		DisplayManager.closeDisplay();
		quit();
	}
	
	public static void quit() {
		System.exit(0);
	}
	
}
