/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Kmaps;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author student
 */
public class KMapIndicator {
	private Point position;
	private Color backgroundColor;
	private Color strokeColor;
	private ArrayList<Boolean> variables = new ArrayList<>();
	private boolean value;
	private int size;

	public KMapIndicator(Point position, int size,Color backgroundColor, Color strokeColor, boolean value ,boolean a, boolean b, boolean c, boolean d) {
		this.position = position;
		this.backgroundColor = backgroundColor;
		this.strokeColor = strokeColor;
		this.size = size;
		for(int i = 0; i < variables.size(); i++){
			switch(i){
				case 0:
					variables.add(a);
					break;
				case 1:
					variables.add(b);
					break;
				case 2:
					variables.add(c);
					break;
				case 3:
					variables.add(d);
					break;
			}
		}
	}

	public KMapIndicator(Point position, int size, boolean value ,boolean a, boolean b, boolean c, boolean d) {
		this.position = position;
		this.size = size;
		this.backgroundColor = Color.GRAY;
		this.strokeColor = Color.BLACK;
		for(int i = 0; i < variables.size(); i++){
			switch(i){
				case 0:
					variables.add(a);
					break;
				case 1:
					variables.add(b);
					break;
				case 2:
					variables.add(c);
					break;
				case 3:
					variables.add(d);
					break;
			}
		}
	}
	
	public KMapIndicator(Point position, boolean value ,ArrayList<Boolean> vars) {
		this.position = position;
		this.variables = vars;
		this.size = 20;
		this.backgroundColor = Color.GRAY;
		this.strokeColor = Color.BLACK;
	}
	
	public KMapIndicator(Point position, int size, boolean value ,ArrayList<Boolean> vars) {
		this.position = position;
		this.variables = vars;
		this.size = size;
		this.backgroundColor = Color.GRAY;
		this.strokeColor = Color.BLACK;
	}

	
	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public Color getStrokeColor() {
		return strokeColor;
	}

	public void setStrokeColor(Color strokeColor) {
		this.strokeColor = strokeColor;
	}

	public ArrayList<Boolean> getVariables() {
		return variables;
	}

	public void setVariables(ArrayList<Boolean> variables) {
		this.variables = variables;
	}
	
	public boolean getValue(){
		return this.value;
	}
	
	public boolean isValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
	
}
