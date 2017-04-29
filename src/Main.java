import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import guis.GUITexture;
import guis.GuiRenderer;
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

		ArrayList<Terrain> terrainList = new ArrayList<>();

		int terrainCount = 1;
		
		for (int i = -terrainCount; i <= terrainCount; i++) {
			for (int j = -terrainCount; j <= terrainCount; j++) {
				Terrain terrain = new Terrain(i, j, loader, ttp, blendMap, "height-map");
				terrainList.add(terrain);
			}
		}

		// Tree Model
		ModelData dTree = OBJLoader.loadOBJ("cherry-tree");
		RawModel dTreeModel = loader.loadToVAO(dTree.getVertices(), dTree.getTextureCoords(), dTree.getNormals(),
				dTree.getIndices());
		TexturedModel staticDTree = new TexturedModel(dTreeModel,
				new ModelTexture(loader.loadTexture("cherry-texture")));
		ModelTexture dTreeTexture = staticDTree.getTexture();
		dTreeTexture.setShineDamper(50);
		dTreeTexture.setReflectivity(0);

		// Fighter Jet
		ModelData fj = OBJLoader.loadOBJ("fighter-jet");
		RawModel fjModel = loader.loadToVAO(fj.getVertices(), fj.getTextureCoords(), fj.getNormals(), fj.getIndices());

		TexturedModel staticModel = new TexturedModel(fjModel,
				new ModelTexture(loader.loadTexture("fighter-jet-texture")));
		ModelTexture texture = staticModel.getTexture();
		texture.setShineDamper(20);
		texture.setReflectivity(1);

		// Trees
		ArrayList<Entity> treeList = new ArrayList<>();

		for (int i = 0; i < 1000; i++) {
			
			float rx = (float) ((Math.random() - 0.5f) * 1000);
			float rz = (float) ((Math.random() - 0.5f) * 1000);
			
			float y = onTerrain(terrainList, rx, rz).getHeightOfTerrain(rx, rz);
			Entity treeModel = new Entity(staticDTree, new Vector3f(rx, y - 1f, rz), 0, (float) (Math.random() * 360),
					0, (float) ((Math.random() * 0.2) + 0.9f));
			
			treeList.add(treeModel);
		}

		Player player = new Player(staticModel, new Vector3f(0, 2, 0), 0, 0, 0, 1);

		// Light
		List<Light> lights = new ArrayList<>();
		lights.add(new Light(new Vector3f(500, 10000, 500), new Vector3f(1, 1, 1)));
		lights.add(new Light(new Vector3f(-500, 10000, -500), new Vector3f(1, 1, 1)));
		
		
		Camera camera = new Camera(player);
		MasterRenderer renderer = new MasterRenderer();

		while (!Display.isCloseRequested()) {

			// find the terrain that player is on

			float playerX = player.getPosition().x;
			float playerZ = player.getPosition().z;

			// set render distance origin
			renderer.setOrigin(playerX, playerZ);
			
			for (Terrain t : terrainList) {
				// Adding to terrain render queue
				renderer.processTerrain(t);
			}

			player.move(onTerrain(terrainList, playerX, playerZ));
			camera.move();

			// Adding to render queue
			renderer.processEntity(player);

			// Trees
			for (Entity e : treeList) {
				renderer.processEntity(e);
			}

			renderer.render(lights, camera);

			System.out.println(DisplayManager.getFrameTimeSeconds() * 1000 + "ms");
			DisplayManager.updateDisplay();
		}

		renderer.cleanUp();

		loader.cleanUp();
		DisplayManager.closeDisplay();
		quit();
	}

	public static Terrain onTerrain(List<Terrain> terrainList, float x, float z) {
		Terrain playerOn = terrainList.get(0);
		for (Terrain t : terrainList) {
			if (t.getX() <= x && t.getX() - t.SIZE <= x && t.getZ() <= z
					&& t.getZ() - t.SIZE <= z) {
				playerOn = t;
			}
		}
		return playerOn;
	}

	public static void quit() {
		System.exit(0);
	}

}
