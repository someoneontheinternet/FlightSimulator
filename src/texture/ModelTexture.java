package texture;

public class ModelTexture {

	private int textureID;
	private float reflectivity = 0;
	private float shineDamper = 1;

	private boolean hasTransparency = false;
	private boolean useFalseLighting = false;

	public ModelTexture(int id) {
		this.textureID = id;
	}

	public boolean isUseFalseLighting() {
		return useFalseLighting;
	}

	public void setUseFalseLighting(boolean useFalseLighting) {
		this.useFalseLighting = useFalseLighting;
	}

	public boolean getHasTransparency() {
		return hasTransparency;
	}

	public void setHasTransparency(boolean s) {
		hasTransparency = s;
	}

	public int getID() {
		return this.textureID;
	}

	public int getTextureID() {
		return textureID;
	}

	public void setTextureID(int textureID) {
		this.textureID = textureID;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

}
