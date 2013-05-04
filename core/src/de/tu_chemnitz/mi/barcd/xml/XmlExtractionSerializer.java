package de.tu_chemnitz.mi.barcd.xml;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.bind.JAXBElement;

import de.tu_chemnitz.mi.barcd.Barcode;
import de.tu_chemnitz.mi.barcd.BarcodeType;
import de.tu_chemnitz.mi.barcd.Extraction;
import de.tu_chemnitz.mi.barcd.Region;
import de.tu_chemnitz.mi.barcd.geometry.ConvexPolygon;
import de.tu_chemnitz.mi.barcd.geometry.GenericConvexPolygon;
import de.tu_chemnitz.mi.barcd.geometry.Point;
import de.tu_chemnitz.mi.barcd.xml.binding.BarcodeElement;
import de.tu_chemnitz.mi.barcd.xml.binding.BarcodeFormat;
import de.tu_chemnitz.mi.barcd.xml.binding.BarcodesElement;
import de.tu_chemnitz.mi.barcd.xml.binding.ExtractionElement;
import de.tu_chemnitz.mi.barcd.xml.binding.ObjectFactory;
import de.tu_chemnitz.mi.barcd.xml.binding.PointElement;
import de.tu_chemnitz.mi.barcd.xml.binding.PointsElement;
import de.tu_chemnitz.mi.barcd.xml.binding.RegionElement;
import de.tu_chemnitz.mi.barcd.xml.binding.RegionsElement;

/**
 * XML serializer/unserializer for {@link Extraction}.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class XmlExtractionSerializer extends XmlSerializer<Extraction> {
    private final ObjectFactory elements = new ObjectFactory();

    public XmlExtractionSerializer() {
        super(ExtractionElement.class.getPackage().getName());
    }

    @Override
    protected Extraction restoreModel(JAXBElement<?> e)
        throws XmlSerializerException
    {
        ExtractionElement fe = (ExtractionElement) e.getValue();
        int number = fe.getFrameNumber();
        Collection<Region> regions = restoreRegions(fe.getRegions());
        Collection<Barcode> barcodes = restoreRegionlessBarcodes(fe.getBarcodes(), fe.getRegions());
        return new Extraction(number, regions, barcodes);
    }

    @Override
    protected JAXBElement<?> createRootElement(Extraction extraction)
        throws XmlSerializerException
    {
        return elements.createExtraction(createExtractionElement(extraction));
    }

    private ExtractionElement createExtractionElement(Extraction frame) {
        ExtractionElement fe = elements.createExtractionElement();
        fe.setRegions(createRegionsElement(frame.getRegions()));
        fe.setFrameNumber(frame.getFrameNumber());
        fe.setBarcodes(createBarcodesElement(frame.getBarcodes()));
        return fe;
    }

    private BarcodesElement createBarcodesElement(Collection<Barcode> barcodes) {
        BarcodesElement be = elements.createBarcodesElement();
        List<BarcodeElement> bes = be.getBarcode();
        for (Barcode barcode : barcodes) {
            bes.add(createBarcodeElement(barcode));
        }
        return be;
    }

    private RegionsElement createRegionsElement(Collection<Region> regions) {
        RegionsElement re = elements.createRegionsElement();
        List<RegionElement> res = re.getRegion();
        for (Region region : regions) {
            res.add(createRegionElement(region));
        }
        return re;
    }

    private RegionElement createRegionElement(Region region) {
        RegionElement re = elements.createRegionElement();
        re.setCoverage(region.getCoverage());
        Barcode barcode = region.getBarcode();
        if (barcode != null) {
            re.setBarcode(createBarcodeElement(barcode));
        }
        List<String> pes = re.getPolygon();
        ConvexPolygon polygon = region.getConvexPolygon();
        Point[] vertices = polygon.getVertices();
        for (Point v : vertices) {
            pes.add(v.getX() + "," + v.getY());
        }
        return re;
    }

    private BarcodeElement createBarcodeElement(Barcode barcode) {
        BarcodeElement be = elements.createBarcodeElement();
        be.setId(barcode.getType().toString() + "_" + barcode.getText());
        be.setBytes(barcode.getBytes());
        be.setText(barcode.getText());
        be.setType(type2format.get(barcode.getType()));
        be.setPoints(createPointsElement(barcode.getAnchorPoints()));
        return be;
    }

    private PointsElement createPointsElement(Point[] points) {
        PointsElement pe = elements.createPointsElement();
        List<PointElement> pes = pe.getPoint();
        for (Point p : points) {
            pes.add(createPointElement(p));
        }
        return pe;
    }

    private PointElement createPointElement(Point p) {
        PointElement pe = elements.createPointElement();
        pe.setX(p.getX());
        pe.setY(p.getY());
        return pe;
    }

    private Collection<Region> restoreRegions(RegionsElement e) {
        List<RegionElement> res = e.getRegion();
        LinkedList<Region> regions = new LinkedList<Region>();
        for (RegionElement re : res) {
            regions.add(restoreRegion(re));
        }
        return regions;
    }

    private Region restoreRegion(RegionElement e) {
        double coverage = e.getCoverage();
        ConvexPolygon polygon = restorePolygon(e.getPolygon());
        Region region = new Region(polygon, coverage);
        region.setBarcode(restoreBarcode((BarcodeElement) e.getBarcode()));
        return region;
    }

    private Collection<Barcode> restoreRegionlessBarcodes(BarcodesElement bse, RegionsElement rse) {
        // Map each barcode to its region if a region contains a barcode. Use
        // BarcodeElement as key instead of Barcode, because there could be
        // multiple instances of equal barcodes because of restoreBarcode. This
        // way we can make use of Object#equal.
        HashMap<BarcodeElement, RegionElement> map = new HashMap<BarcodeElement, RegionElement>();
        for (RegionElement re : rse.getRegion()) {
            BarcodeElement be = (BarcodeElement) re.getBarcode();
            if (be != null) {
                map.put(be, re);
            }
        }

        // Restore all barcodes which have no corresponding region.
        Collection<Barcode> barcodes = new LinkedList<Barcode>();
        List<BarcodeElement> bes = bse.getBarcode();
        for (BarcodeElement be : bes) {
            if (!map.containsKey(be)) {
                Barcode b = restoreBarcode(be);
                barcodes.add(b);
            }
        }

        return barcodes;
    }

    private Barcode restoreBarcode(BarcodeElement e) {
        String text = e.getText();
        byte[] raw = e.getBytes();
        BarcodeType type = format2type.get(e.getType());
        Point[] points = restorePoints(e.getPoints());
        return new Barcode(type, text, raw, points);
    }

    private Point[] restorePoints(PointsElement e) {
        List<PointElement> pes = e.getPoint();
        Point[] points = new Point[pes.size()];
        int i = 0;
        for (PointElement pe : pes) {
            points[i++] = new Point(pe.getX(), pe.getY());
        }
        return points;
    }

    private GenericConvexPolygon restorePolygon(List<String> ps) {
        Point[] vertices = new Point[ps.size()];
        int i = 0;
        for (String s : ps) {
            int p = s.indexOf(',');
            String sx = s.substring(0, p);
            String sy = s.substring(p + 1);
            vertices[i++] = new Point(Double.valueOf(sx), Double.valueOf(sy));
        }
        return new GenericConvexPolygon(vertices);
    }

    private static EnumMap<BarcodeFormat, BarcodeType> format2type;
    private static EnumMap<BarcodeType, BarcodeFormat> type2format;

    static {
        format2type = new EnumMap<BarcodeFormat, BarcodeType>(BarcodeFormat.class);
        type2format = new EnumMap<BarcodeType, BarcodeFormat>(BarcodeType.class);

        for (BarcodeType type : BarcodeType.values()) {
            format2type.put(BarcodeFormat.fromValue(type.value()), type);
        }

        for (Entry<BarcodeFormat, BarcodeType> e : format2type.entrySet()) {
            type2format.put(e.getValue(), e.getKey());
        }
    }

}
