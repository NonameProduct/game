package com.example.till.game;

/**
 * Created by till on 23.12.15.
 */
public class MyVector {
    double[] content;
    public MyVector(double x, double y){
        content = new double[]{x, y};
    }

    public MyVector add(MyVector v) {
        return new MyVector(this.getX() + v.getX(), this.getY() + v.getY());
    }

    public MyVector scale(double scale) {
        return new MyVector(this.getX() * scale, this.getY() * scale);
    }

    public double getX() {
        return content[0];
    }
    public double getY() {
        return content[1];
    }

    @Override
    public String toString() {
        return "(" + getX() + ", " + getY() + ")";
    }
}
