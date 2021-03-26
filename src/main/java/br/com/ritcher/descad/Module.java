package br.com.ritcher.descad;

import com.jsevy.jdxf.DXFGraphics;

import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Module implements Shape {

    ArrayList<java.awt.Shape> result = new ArrayList<>();

    AffineTransform rotate = new AffineTransform();
    AffineTransform translate = new AffineTransform();

    Point2D.Double offset = new Point2D.Double();

    String[] module;

    String id;

    Point2D.Double size = new Point2D.Double();

    public Module(Module m) {
        rotate = (AffineTransform) m.rotate.clone();
        translate = (AffineTransform) m.translate.clone();
        result  = new ArrayList<>(m.result);
        module  = m.module.clone();
        size  = (Point2D.Double) m.size.clone();
        offset = (Point2D.Double) m.offset.clone();
        id  = m.id;
    }

    public Module() {

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
        return null;
    }

    public void reset(){
        rotate = new AffineTransform();
        translate = new AffineTransform();
    }

    @Override
    public void attachTo(String region, Point2D point) {
        double px = point.getX();
        double sh2 = size.getY() / 2;
        double py = point.getY();
        double ox = offset.getX();
        double oy = offset.getY();

        switch (region){
            case "O":
                rotate.rotate(Math.toRadians(90));
                translate.translate(px - sh2 + ox, py + oy);
                break;
            case "L":
                rotate.rotate(Math.toRadians(-90));
                translate.translate(px + sh2 + ox, py + oy);
                break;
            case "N":
                rotate.rotate(0);
                translate.translate(px + ox, py + sh2 + oy);
                break;
            case "S":
                rotate.rotate(Math.toRadians(180));
                translate.translate(px + ox, py - sh2 + oy);
                break;
            default:
                throw new RuntimeException(String.format("Região invalida: %s", region));
        }
    }

    public void setModule(String[] module) {
        this.module = module;
    }


    @Override
    public void draw(DXFGraphics g) {
        for (java.awt.Shape s:result ) {
            g.draw(s);
        }
    }

    public void set(){
        Point2D.Double first = null, last = null, current = null, transf;
        String command = null;
        boolean x = true, isfirst = false;
        int i = -1;

        for (String c:module) {
            i++;
            switch (c){
                case "pl":
                    command = c;
                    first = null;
                    last = null;
                    isfirst = true;
                    continue;
                case "arc":
                case "circle":
                    command = c;
                    continue;
            }
            if(command == null){
                continue;
            }

            switch (command){
                case "pl":
                    if(c.length() < 1){
                        continue;
                    }
                    if(x){
                        current = new Point2D.Double();
                        current.x = Double.parseDouble(c);
                        x = false;

                    }
                    else {
                        current.y = Double.parseDouble(c);
                        x = true;

                        transf = new Point2D.Double();
                        rotate.transform(current, transf);
                        current = transf;
                        translate.transform(current, transf);
                        current = transf;

                        if(isfirst){
                            first = (Point2D.Double) current.clone();
                            isfirst = false;
                        }
                        if(last != null){
                            Line2D.Double line = new Line2D.Double();
                            line.setLine(last.x, last.y, current.x, current.y);
                            result.add(line);
                        }
                        last = (Point2D.Double) current.clone();
                        if(i == module.length -1){
                            if(first !=null && last != null){
                                Line2D.Double line = new Line2D.Double();
                                line.setLine(last.x, last.y, first.x, first.y);
                                result.add(line);
                            }
                        }
                    }
                    break;
                case "circle":
                case "arc":
                    break;
                default:
                    throw new RuntimeException(String.format("Comando nao definido: %s", command));
            }
        }
    }

    public void setSize(Point2D.Double aDouble) {
        this.size = aDouble;
    }
}
