package com.obj.parser.obj;

import com.obj.Vertex;
import com.obj.WavefrontObject;
import com.obj.parser.LineParser;

public class VertexParser extends LineParser {
	Vertex vertex = null;
	
	public VertexParser() {}

	public void parse() {
		vertex = new Vertex();
		
		try {
			vertex.setX(Float.parseFloat(words[1]));
			vertex.setY(Float.parseFloat(words[2]));
			vertex.setZ(Float.parseFloat(words[3]));
		} catch(Exception e) { throw new RuntimeException("VertexParser Error"); }
		
	}

	public void incoporateResults(WavefrontObject wavefrontObject) { wavefrontObject.getVertices().add(vertex); }

}
