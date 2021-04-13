package br.com.ritcher.descad;

import com.jsevy.jdxf.DXFDocument;
import com.jsevy.jdxf.DXFGraphics;

public interface CADEntity {
    void draw(DXFGraphics g, DXFDocument dxfDocument);
}
