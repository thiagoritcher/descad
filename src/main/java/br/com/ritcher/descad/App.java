package br.com.ritcher.descad;

import com.jsevy.jdxf.DXFDocument;
import com.jsevy.jdxf.DXFGraphics;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App {
	String input = null;

	private DXFDocument dxfDocument;
	private DXFGraphics dxfGraphics;
	private final String file = "C:\\Users\\talvares\\Downloads\\descad\\out.dxf";
	private final String infile = "C:\\Users\\talvares\\Downloads\\descad\\input.csv";

	public static void main(String[] args) throws IOException {
		App ap = new App();
		ap.readInput();
		ap.process();
		ap.write();
	}

	void readInput() throws IOException {
		BufferedReader fr = new BufferedReader(new FileReader(infile));
		StringBuilder buffer = new StringBuilder();

		String line = fr.readLine();
		while(line != null){
			buffer.append(line).append("\n");
			line = fr.readLine();
		}
		input = buffer.toString();
	}


	HashMap<String, Shape> map = new HashMap<>();
	HashMap<String, Module> moduleMap = new HashMap<>();
	void write() throws IOException {

		AffineTransform t = new AffineTransform();
		t.scale(1, -1);

		dxfGraphics.transform(t);

//		dxfDocument.setLayer("1");
//		dxfGraphics.setColor(Color.RED);

		for (Shape s: map.values()) {
			s.draw(dxfGraphics);
		}

		String dxfText = dxfDocument.toDXFString();
		File fileo = new File(file);
		System.out.println("Escrevendo dxf para ");
		System.out.println(fileo.getAbsolutePath());
		FileWriter fileWriter = new FileWriter(fileo);
		fileWriter.write(dxfText);
		fileWriter.flush();
		fileWriter.close();
	}

	void process() {
		dxfDocument = new DXFDocument("Descad");
		dxfGraphics = dxfDocument.getGraphics();

		int status = 0;
		String[] rs = input.split("\n");
		for (String r: rs) {
			String[] ps = r.split("\t\\s*");
			if(ps.length < 1){
				continue;
			}

			switch (ps[0]){
				case "Modules":
					status = 1;
					continue;
				case "Design":
					status = 2;
					continue;
				case "ID":
					continue;
			}

			if(status == 1){
				module(ps);
			}
			else if(status == 2){
				design(ps);
			}
		}
	}

	private void module(String[] ps) {
		String id = ps[0];
		if(id.length() > 0){


			Module m = new Module();
			m.setSize(new Point2D.Double( Double.parseDouble(ps[1]), Double.parseDouble(ps[2]) ));

			ArrayList<String> al = new ArrayList<>(Arrays.asList(ps));
			al.remove(0);
			al.remove(0);
			al.remove(0);
			m.setModule(al.toArray(new String[0]));
			moduleMap.put(id, m);
		}
	}

	private void design(String[] ps) {
		if("R".equals(ps[4])){
			rectangle(ps);
		}
		else {
			Module module = moduleMap.get(ps[4]);

			module = new Module(module);
			module.reset();


			String id = ps[0];
			String lc = ps[3].trim();
			module.id = id;

			if(ps.length > 5){
				module.setOffset(new Point2D.Double(Double.parseDouble(ps[5]),Double.parseDouble(ps[6])));
			}

			if("0".equals(lc)){
				map.put(id, module);
				return;
			}


			String lcid = lc.substring(1);
			String lcl = lc.substring(0,1);

			Shape lcr = map.get(lcid);

			Point2D pa = lcr.getAttachment(lcl);
			module.attachTo(lcl, pa);
			module.set();
			map.put(id, module);
		}
	}

	private void rectangle(String[] ps) {
		Rectangle ro = new Rectangle();
		String id = ps[0];

		double w = Double.parseDouble(ps[1]);
		double h = Double.parseDouble(ps[2]);
		ro.setRect(w, h);

		if(ps.length > 5){
			ro.setOffset(new Point2D.Double(Double.parseDouble(ps[5]),Double.parseDouble(ps[6])));
		}

		String lc = ps[3].trim();
		if("0".equals(lc)){
			map.put(id, ro);
			return;
		}
		String lcid = lc.substring(1);
		String lcl = lc.substring(0,1);

		Shape lcr = map.get(lcid);

		Point2D pa = lcr.getAttachment(lcl);
		ro.attachTo(lcl, pa);

		map.put(id, ro);
	}
}
