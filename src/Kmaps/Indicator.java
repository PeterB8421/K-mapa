/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Kmaps;

import java.awt.Color;
import java.awt.Point;

/**
 *
 * @author Petr Balok
 */
public class Indicator{
    private Point position;
    private int SIZE;
    private Color backgroundColor = new Color(200,200,200);
    private Color strokeColor = Color.BLACK;
    private boolean value;

    public Indicator(Point position, int SIZE, boolean value) {
        this.position = position;
        this.SIZE = SIZE;
        this.value = value;
    }
    
    public Indicator(Point position, boolean value) {
        this.position = position;
        this.value = value;
        this.SIZE = 20;
    }

    
    
    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
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

    public int getSIZE() {
        return SIZE;
    }

    public void setSIZE(int SIZE) {
        this.SIZE = SIZE;
    }
    
    public boolean getValue(){
        return this.value;
    }
    
}
