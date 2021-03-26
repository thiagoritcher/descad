package br.com.ritcher.descad;

import com.jsevy.jdxf.DXFDocument;
import com.jsevy.jdxf.DXFGraphics;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Rectangle implements Shape {
    String id;
    Rectangle2D.Double r = new Rectangle2D.Double();
    Point2D offset = new Point2D.Double();

    public double getX() {
        return r.getX();
    }

    public double getY() {
        return r.getY();
    }

    public double getWidth() {
        return r.getWidth();
    }

    public double getHeight() {
        return r.getHeight();
    }

    public void setRect(double w, double h) {
        r.setRect(0,0, w, h);
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setOffset(Point2D.Double point) {
       offset = point;
    }

    @Override
    public Point2D getAttachment(String region) {
        double ox = offset.getX();
        double oy = offset.getY();
        double rx = r.getX();
        double rw2 = r.getWidth() / 2;
        double ry = r.getY();
        double rh2 = r.getHeight() / 2;

        switch (region){
            case "O":
                return new Point2D.Double(rx + ox - rw2, ry + oy) ;
            case "L":
                return new Point2D.Double(rx + ox + rw2, ry + oy) ;
            case "N":
                return new Point2D.Double(rx + ox, ry + rh2 + oy) ;
            case "S":
                return new Point2D.Double(rx + ox, ry - rh2 + oy) ;
            default:
                throw new RuntimeException(String.format("Região invalida: %s", region));
        }
    }

    public void attachTo(String region, Point2D point){
        double x = point.getX();
        double rw = r.getWidth();
        double rw2 = rw / 2;
        double y = point.getY();
        double rh = r.getHeight();
        double rh2 = rh / 2;

        switch (region){
            case "O":
                r.setRect(x - rw2, y, rw, rh);
                break;
            case "L":
                r.setRect(x + rw2, y, rw, rh);
                break;
            case "N":
                r.setRect(x, y + rh2, rw, rh);
                break;
            case "S":
                r.setRect(x, y - rh2, rw, rh);
                break;
            default:
                throw new RuntimeException(String.format("Região invalida: %s", region));
        }
    }

    @Override
    public void draw(DXFGraphics g) {
        double ox = offset.getX();
        double oy = offset.getY();
        g.drawRect(r.getX() - r.getWidth()/2 + ox, r.getY() - r.getHeight()/2 + oy, r.getWidth(), r.getHeight());
    }
}
