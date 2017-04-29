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
import renderEngine.MasterRenderer;
import terrain.Terrain;
import texture.ModelTexture;
import texture.TerrainTexture;
import texture.TerrainTexturePack;
import utils.Loader;
import utils.ModelData;
import utils.OBJLoader;

public class Main {

	public static void main(String[] args) {

		Random r = new Random();
		
		DisplayManager.createDisplay();
		Loader loader = new Loader();

		// Terrain
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		TerrainTexturePack ttp = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		
		Terrain terrain1 = new Terrain(0, -1, loader, ttp, blendMap, "height-map");
		//Terrain terrain2 = new Terrain(0, 0, loader, ttp, blendMap, "height-map");
		//Terrain terrain3 = new Terrain(-1, 0, loader, ttp, blendMap, "height-map");
		//Terrain terrain4 = new Terrain(-1, -1, loader, ttp, blendMap, "height-map");
		
		// Tree Model
		ModelData dTree = OBJLoader.loadOBJ("cherry-tree");
		RawModel dTreeModel = loader.loadToVAO(dTree.getVertices(), dTree.getTextureCoords(), dTree.getNormals(), dTree.getIndices());
		TexturedModel staticDTree = new TexturedModel(dTreeModel, new ModelTexture(loader.loadTexture("cherry-texture")));
		ModelTexture dTreeTexture = staticDTree.getTexture();
		dTreeTexture.setShineDamper(50);
		dTreeTexture.setReflectivity(0);
		
		// Fighter Jet
		ModelData fj = OBJLoader.loadOBJ("fighter-jet");
		RawModel fjModel = loader.loadToVAO(fj.getVertices(), fj.getTextureCoords(), fj.getNormals(), fj.getIndices());

		TexturedModel staticModel = new TexturedModel(fjModel, new ModelTexture(loader.loadTexture("fighter-jet-texture")));
		ModelTexture texture = staticModel.getTexture();
		texture.setShineDamper(20);
		texture.setReflectivity(1);

		// Trees
		ArrayList<Entity> treeList = new ArrayList<>();
		
		for (int i = 0; i < 500; i++) {
			float rx = (float) ((Math.random()) * 1000);
			float rz = (float) ((Math.random() - 1) * 1000);
			float y = terrain1.genHeightOfTerrain(rx, rz);
			Entity treeModel = new Entity(staticDTree, new Vector3f(rx, y - 1f, rz), 0, (float) (Math.random() * 360), 0, (float) ((Math.random() * 0.2) + 0.9f));
			treeList.add(treeModel);
		}
		
		
		Player player = new Player(staticModel, new Vector3f(0, 2, 0), 0, 0, 0, 1);
		// Light
		Light light = new Light(new Vector3f(500, 10000, 500), new Vector3f(1, 1, 1));
		Camera camera = new Camera(player);
		MasterRenderer renderer = new MasterRenderer();
		
		while (!Display.isCloseRequested()) {

			player.move(terrain1);
			camera.move();

			// Adding to render queue
			renderer.processEntity(player);
			
			// Trees
			for (Entity e : treeList) {
				renderer.processEntity(e);
			}
			
			// Adding to terrain render queue
			renderer.processTerrain(terrain1);
			//renderer.processTerrain(terrain2);
			//renderer.processTerrain(terrain3);
			//renderer.processTerrain(terrain4);

			
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
