package br.com.ritcher.descad;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import com.jsevy.jdxf.*;

public class Arc implements CADEntity {
	Point2D.Double center, p1, p2, pm;
	double radius, startAngle, endAngle;
	boolean clockwise = false;

	public Arc(double cx, double cy, double radius, double startAngle, double endAngle, boolean clockwise) {
		super();
		setup(new Point2D.Double(cx, cy), radius, startAngle, endAngle, clockwise, null, null, null);
	}

	public Arc(Point2D.Double center, double radius, double startAngle, double endAngle, boolean inverted) {
		super();
		setup(center, radius, startAngle, endAngle, inverted, null, null, null);
	}

	private void setup(Point2D.Double center, double radius, double startAngle, double endAngle, boolean clockwise, Point2D.Double p1, Point2D.Double p2, Point2D.Double pm ) {
		this.center = center;
		this.radius = radius;
		this.startAngle = posAngle(startAngle);
		this.endAngle = posAngle(endAngle);
		this.clockwise = clockwise;
		this.p1 = p1;
		this.p2 = p2;
		this.pm = p2;

		double m = (endAngle + startAngle)/2;

		if(p1 == null){
			if(!clockwise) {while(startAngle > endAngle) { startAngle -= Math.PI*2; }}
			else { while(startAngle < endAngle) { startAngle += Math.PI*2; } }

			this.p1 = new Point2D.Double(center.x + Math.cos(startAngle) * radius,
					center.y + Math.sin(startAngle) * radius);

			this.p2 = new Point2D.Double(center.x + Math.cos(endAngle) * radius,
					center.y + Math.sin(endAngle) * radius);
		}

		if(pm == null){

			this.pm = new Point2D.Double(center.x + Math.cos(m) * radius,
					center.y + Math.sin(m) * radius);
		}
	}

	public void transform(AffineTransform at) {
		at.transform(center, center);
		at.transform(p1, p1);
		at.transform(p2, p2);
		at.transform(pm, pm);
		
		radius = p1.distance(center);
		//startAngle = Math.atan2(center.y - p1.y,center.x - p1.x );
		//endAngle = Math.atan2(center.y - p2.y,center.x - p2.x );
		double mAng = posAngle(Math.atan2(pm.y - center.y,pm.x - center.x));
		double a1 = posAngle(Math.atan2(p1.y - center.y,p1.x - center.x));
		double a2 = posAngle(Math.atan2(p2.y - center.y,p2.x  - center.x));
		
	
		if(!clockwise) {
			if(posAngle(a1 - mAng) < posAngle(a2 - mAng)) { endAngle = a1; startAngle = a2;}
			else { endAngle = a2; startAngle = a1;}
		}
		else {
			if(posAngle(a1 - mAng) > posAngle(a2 - mAng)) { endAngle = a1; startAngle = a2;}
			else { endAngle = a2; startAngle = a1;}
		}
	}
	
	private double posAngle(double a) {
		while(a > Math.PI * 2) {
			a -= 2*Math.PI;
		}
		while(a < 0) {
			a += 2*Math.PI;
		}
		return a;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		 return new Arc((Double) center.clone(), radius, startAngle, endAngle, clockwise);
	}

	@Override
	public String toString() {
		return "Arc [center=" + center + ", p1=" + p1 + ", p2=" + p2 + ", radius=" + radius + ", startAngle="
				+ Math.toDegrees(startAngle) + ", endAngle=" + Math.toDegrees(endAngle) + ", inverted=" + clockwise + "]";
	}


	@Override
	public void draw(DXFGraphics g, DXFDocument dxfDocument) {
		dxfDocument.addEntity(new DXFArc(new RealPoint(center.x, center.y, 0), radius, startAngle, endAngle, !clockwise, g));
	}
}
