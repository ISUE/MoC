package com.obj;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import com.obj.Vertex;
import com.obj.parser.LineParser;
import com.obj.parser.obj.ObjLineParserFactory;

//import engine.Engine;
import utils.Logger;

public class WavefrontObject {
	private ArrayList<Vertex> vertices = new ArrayList<Vertex>();
	private ArrayList<Vertex> normals = new ArrayList<Vertex>();
	private ArrayList<TextureCoordinate> textures = new ArrayList<TextureCoordinate>();
	private ArrayList<Group> groups = new ArrayList<Group>();
	private Hashtable<String,Group> groupsDirectAccess = new Hashtable<String,Group>();
	Hashtable<String, Material> materials = new Hashtable<String, Material>(); 
	public String fileName;
	
	private ObjLineParserFactory parserFactory ;
	private Material currentMaterial;
	private Material currentFaceMaterial;
	private Group currentGroup;
	private String contextfolder ="" ;
	public double radius=0;
	private BufferedImage currentMap;
	private boolean currentMapIsNew;
	
	public WavefrontObject(String fileName) {
		this.fileName = fileName;

		int lastSlashIndex = fileName.lastIndexOf('/');
		if ( lastSlashIndex != -1) this.contextfolder = fileName.substring(0,lastSlashIndex+1);

		lastSlashIndex = fileName.lastIndexOf('\\');
		if ( lastSlashIndex != -1) this.contextfolder = fileName.substring(0,lastSlashIndex+1);

		parse(fileName);

		calculateRadius();
			
	}

	private void calculateRadius() {
		double currentNorm = 0;
		for(Vertex vertex : vertices) {
			currentNorm = vertex.norm(); 
			if ( currentNorm> radius ) radius = currentNorm;
			
		}
		
	}

	public String getContextfolder() { return contextfolder; }

	//----------------------------------------------------------------------------------------------
	public void parse(String fileName) {
		parserFactory = new ObjLineParserFactory(this);
		
		InputStream fileInput = this.getClass().getResourceAsStream(fileName);
		if (fileInput == null)
			// Could not find the file in the jar.
			try {
				File file = new File(fileName);
				if (file.exists()) fileInput = new FileInputStream(file);
			} catch (Exception e) { e.printStackTrace(); }
		
		BufferedReader in = null;
		
		try {
			in = new BufferedReader(new InputStreamReader(fileInput));
		
			String currentLine = null;
			while((currentLine = in.readLine()) != null)
				parseLine(currentLine);
				
			if (in != null)
				in.close();
		
		} catch (Exception e) { e.printStackTrace(); }
		

		if (Logger.logging) {
			Logger.log("Loaded OBJ from file '"+fileName+"'");
			Logger.log(getVertices().size()+" vertices.");
			Logger.log(getNormals().size()+" normals.");
			Logger.log(getTextures().size()+" textures coordinates.");
			
		}
		
	}
	//----------------------------------------------------------------------------------------------

	//----------------------------------------------------------------------------------------------
	private void parseLine(String currentLine) {
		if ("".equals(currentLine)) return;
		
		LineParser parser = parserFactory.getLineParser(currentLine);
		parser.parse();
		parser.incoporateResults(this);
		
	}
	//----------------------------------------------------------------------------------------------

	
	public void setTextures(ArrayList<TextureCoordinate> textures) { this.textures = textures; }
	public ArrayList<TextureCoordinate> getTextures() { return textures; }
	
	public void setVertices(ArrayList<Vertex> vertices) { this.vertices = vertices; }
	public ArrayList<Vertex> getVertices() { return vertices; }
	
	public void setNormals(ArrayList<Vertex> normals) { this.normals = normals; }
	public ArrayList<Vertex> getNormals() { return normals; }
	
	public Hashtable<String, Material> getMaterials() { return this.materials; }
	public void setMaterials(Hashtable<String, Material> materials) { this.materials = materials; }
	
	public Material getCurrentMaterial() { return currentMaterial; }
	public void setCurrentMaterial(Material currentMaterial ) { this.currentMaterial = currentMaterial; }
	
	public Material getCurrentFaceMaterial() { return currentFaceMaterial; }
	public void setCurrentFaceMaterial(Material currentFaceMaterial ) {
		this.currentFaceMaterial = currentFaceMaterial;
		if (this.currentMapIsNew) {
			this.currentFaceMaterial.setMap(currentMap);
			this.currentMapIsNew = false;
			
		}
		
	}
	
	public ArrayList<Group> getGroups() { return groups; }
	public Hashtable<String, Group> getGroupsDirectAccess() { return groupsDirectAccess; }
	
	public Group getCurrentGroup() { return currentGroup; }
	public void setCurrentGroup(Group currentGroup) { this.currentGroup = currentGroup; }
	
	public void printBoudariesText() { System.out.println(getBoudariesText()); }
	public String getBoudariesText() {		
		float minX=0;
		float maxX=0;
		float minY=0;
		float maxY=0;
		float minZ=0;
		float maxZ=0;
		
		Vertex currentVertex = null;
		
		for (int i=0; i < getVertices().size(); i++) {
			currentVertex = getVertices().get(i);
			if (currentVertex.getX() > maxX) maxX = currentVertex.getX();
			if (currentVertex.getX() < minX) minX = currentVertex.getX();
			
			if (currentVertex.getY() > maxY) maxY = currentVertex.getY();
			if (currentVertex.getY() < minY) minY = currentVertex.getY();
			
			if (currentVertex.getZ() > maxZ) maxZ = currentVertex.getZ();
			if (currentVertex.getZ() < minZ) minZ = currentVertex.getZ();
			
		}
		
		return "maxX=" + maxX + " minX=" + minX +  " maxY=" +  maxY + " minY=" + minY+  " maxZ=" + maxZ+  " minZ=" + minZ;
		
	}

	public void setCurrentMap(BufferedImage buffImage) {
		this.currentMap = buffImage;
		this.currentMapIsNew = true;
		
	}

}
