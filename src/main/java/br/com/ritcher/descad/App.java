package br.com.ritcher.descad;

import com.jsevy.jdxf.DXFDocument;
import com.jsevy.jdxf.DXFGraphics;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Hello world!
 *
 */
public class App {
	String input = null;

	private DXFDocument dxfDocument;
	private DXFGraphics dxfGraphics;
	private String file;
	private String infile;

	public static void main(String[] args) throws IOException {
		App ap = new App();
		for (int i = 0; i < args.length; i++) {
			switch (args[i]){
				case "--input":
				case "-i":
					ap.infile = args[i+1];
					i++;
					break;
				case "--output":
					ap.file = args[i+1];
					i++;
					break;
				case "--help":
				default:
					print_help(null);
					break;
			}
		}

		if(ap.infile == null || ap.file == null){
			print_help("--input e --output devem ser especificados.");
			return;
		}

		ap.readInput();
		ap.process();
		ap.write();
	}

	private static void print_help(String s) {
		StringBuilder sb = new StringBuilder();
		if(s != null){
			sb.append("Erro: ").append(s).append('\n');
			sb.append("---------------------------------").append('\n');
		}
		sb.append("Descad - CAD descritivo").append('\n');
		sb.append("Desenho a partir de informações em arquivo csv").append('\n');
		sb.append("descad --input,-i <input_file> --output,-o <dxf_file>").append('\n');
		sb.append("---------------------------------").append('\n');
		sb.append("Exemplo de entrada:").append('\n');
		sb.append("Modules").append('\n');
		sb.append("ID	W	H	Commands").append('\n');
		sb.append("m1	80	15	pl	-40	-7.5	40	-7.5	25	7.5").append('\n');
		sb.append("m2	5	5	pl	-2.5	-2.5	2.5	-2.5	0	5").append('\n');
		sb.append('\n');
		sb.append("Design").append('\n');
		sb.append("ID	W	H	Loc	Module	dx	dy").append('\n');
		sb.append("	1	150	150	0	R	0	0").append('\n');
		sb.append("	2	80	150	O1	R").append('\n');
		sb.append("	3	20	150	O2	R").append('\n');
		sb.append("4	80	150	L1	R").append('\n');
		sb.append("5	20	150	L4	R").append('\n');
		sb.append("6	150	80	S1	R").append('\n');
		sb.append("7	150	20	S6	R").append('\n');
		sb.append("8	150	80	N1	R").append('\n');
		sb.append("9	153	153	N8	R").append('\n');
		sb.append("10	20	153	O9	R").append('\n');
		sb.append("11	15	153	O10	R").append('\n');
		sb.append("12	20	153	L9	R").append('\n');
		sb.append("13	15	153	L12	R").append('\n');
		sb.append("14	153	20	N9	R").append('\n');
		sb.append("15	153	15	N14	R").append('\n');
		sb.append("16	0	0	N2	m1").append('\n');
		sb.append("17	0	0	S2	m1").append('\n');
		sb.append("18	0	0	N4	m1").append('\n');
		sb.append("19	0	0	S4	m1").append('\n');
		sb.append("20	35	20	O14	R").append('\n');
		sb.append("21	35	20	L14	R").append('\n');
		sb.append("22	0	0	S21	m2	2.5	0").append('\n');
		sb.append("23	0	0	S20	m2	-2.5	0").append('\n');
		System.out.println(sb.toString());
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
