package com.obj;

public class Vertex {
	private float x;
	private float y;
	private float z;
	
	public Vertex() { }
	
	public Vertex(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vertex(int i, int j) {
		setX(i);
		setY(j);
	}
	
	public Vertex(Vertex position) {
		setX(position.getX());
		setY(position.getY());
		setZ(position.getZ());
	}
	
	public float getX() { return x; }
	public void setX(float x) { this.x = x; }
	
	public float getY() { return y; }
	public void setY(float y) { this.y = y; }
	
	public float getZ() { return z; }
	public void setZ(float z) { this.z = z; }
	
	public double norm() { return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2)); }
	
	public String toString() { return "x="+x+",y="+y+",z="+z; }
	
}
