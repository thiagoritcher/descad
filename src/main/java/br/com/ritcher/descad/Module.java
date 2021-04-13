package br.com.ritcher.descad;

import com.jsevy.jdxf.DXFDocument;
import com.jsevy.jdxf.DXFGraphics;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Module implements Shape {

    ArrayList<CADEntity> result = new ArrayList<>();

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
    public void draw(DXFGraphics g, DXFDocument dxfDocument) {
        for (CADEntity s:result ) {
            s.draw(g, dxfDocument);
        }
    }

    public void set(){
        String command = null;

        for (int i = 0; i < module.length ; i++) {
            String c = module[i];
            switch (c){
                case "pl":
                    i = polyline(i +1);
                    continue;
                case "arc":
                    i = arc_center_angle_start_finish(i +1);
                    continue;
                case "arc_3p":
                case "rc_circle":
                case "dc_circle":
                case "2p_circle":
                case "pc_circle":
                    command = c;
                    continue;
            }
            if(command == null){
                continue;
            }
        }
    }

    private int arc_center_angle_start_finish(int j) {
        Point2D.Double center = null;
        double radius = 0, start = 0, finish = 0;
        boolean inverted;

        int i = j;

        String c = module[i];
        center = new Point2D.Double(Double.parseDouble(module[i]), Double.parseDouble(module[i+1]));
        i += 2;

        radius = Double.parseDouble(module[i]);
        i++;

        start = Math.toRadians(Double.parseDouble(module[i]));
        i++;

        finish = Math.toRadians(Double.parseDouble(module[i]));
        i++;

        inverted = Boolean.parseBoolean(module[i]);
        i++;

        Arc a = new Arc(center, radius, start, finish, inverted);
        a.transform(rotate);
        a.transform(translate);
        result.add(a);

        return i;
    }

    private int polyline(int j) {
        PolyLine p = new PolyLine();
        result.add(p);
        int i = j;
        for (; i < module.length ; i++) {
            try {
                p.add(new Point2D.Double(Double.parseDouble(module[i]), Double.parseDouble(module[i+1])));
                i++;
            }
            catch (NumberFormatException nfe){
                return i;
            }
        }
        return i;
    }

    public void setSize(Point2D.Double aDouble) {
        this.size = aDouble;
    }

    class Line implements CADEntity {
        double x1, y1, x2, y2;

        public Line(double x1, double y1, double x2, double y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        @Override
        public void draw(DXFGraphics g, DXFDocument dxfDocument) {
            g.drawLine(x1, y1, x2, y2);
        }
    }

    class PolyLine implements CADEntity {

        public boolean add(Point2D.Double aDouble) {
            return points.add(aDouble);
        }

        List<Point2D.Double> points = new ArrayList<>();
        @Override
        public void draw(DXFGraphics g, DXFDocument dxfDocument) {
            double[] x = new double[points.size() + 1], y = new double[points.size() + 1];
            int i = -1;
            for (Point2D.Double p: points) {
               i++;
               rotate.transform(p, p);
               translate.transform(p, p);

               x[i]=p.getX();
               y[i]=p.getY();
            }
            x[i+1]=x[0];
            y[i+1]=y[0];

            g.drawPolyline(x , y,points.size() + 1);
        }
    }
}
