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
public class Changeable {
    private int SIZE;
    private boolean value;
    private Point position;
    private Color bgColor;
    private Color strokeColor;

    public Changeable(int SIZE, boolean value, Point position, Color bgColor, Color strokeColor) {
        this.SIZE = SIZE;
        this.value = value;
        this.position = position;
        this.bgColor = bgColor;
        this.strokeColor = strokeColor;
    }

    public Changeable(int SIZE, boolean value, Point position) {
        this.SIZE = SIZE;
        this.value = value;
        this.position = position;
        this.bgColor = Color.GREEN;
        this.strokeColor = Color.BLACK;
    }
    
    

    public int getSIZE() {
        return SIZE;
    }

    public void setSIZE(int SIZE) {
        this.SIZE = SIZE;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public Color getBgColor() {
        return bgColor;
    }

    public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
    }

    public Color getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
    }
    
    public void clicked(){
        this.value = !this.value;
    }
    
    public boolean getValue(){
        return this.value;
    }
}
