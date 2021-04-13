package br.com.ritcher.descad;

import com.jsevy.jdxf.DXFDocument;
import com.jsevy.jdxf.DXFGraphics;

import java.awt.geom.Point2D;

public interface Shape {
    String getId();
    void setOffset(Point2D.Double point);
    Point2D getAttachment(String region);
    void attachTo(String region, Point2D point);
    void draw(DXFGraphics g, DXFDocument dxfDocument);
}
