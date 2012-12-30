package de.tu_chemnitz.mi.barcd.app;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import boofcv.gui.image.ShowImages;

import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.xuggle.xuggler.demos.VideoImage;

import de.tu_chemnitz.mi.barcd.Extraction;
import de.tu_chemnitz.mi.barcd.Extractor;
import de.tu_chemnitz.mi.barcd.HighFrequenceRegionFinder;
import de.tu_chemnitz.mi.barcd.geometry.Point;
import de.tu_chemnitz.mi.barcd.geometry.Rectangle;
import de.tu_chemnitz.mi.barcd.geometry.Region;
import de.tu_chemnitz.mi.barcd.image.GammaDenoisingOperator;
import de.tu_chemnitz.mi.barcd.image.LuminanceImage;
import de.tu_chemnitz.mi.barcd.image.LuminanceImage.Grayscaler;
import de.tu_chemnitz.mi.barcd.video.FrameReader;
import de.tu_chemnitz.mi.barcd.video.OpenCVFrameReader;
import de.tu_chemnitz.mi.barcd.video.XugglerFrameReader;
import de.tu_chemnitz.mi.barcd.video.FrameReaderException;

import sun.misc.BASE64Encoder;

public class Application {
    private String inputPath;
    //private String outputPath;
    
    public Application() {
        this.inputPath = "/home/ewie/workspace/Barcode/barcode18.jpg";
        //this.outputPath = ".";
    }
    
    public void run()
        throws ApplicationException
    {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(this.inputPath));
        } catch (IOException ex) {
            throw new ApplicationException(String.format("input file \"%s\" cannot be read", this.inputPath), ex);
        }
        Extractor extractor = new Extractor();
        Extraction[] extractions = extractor.process(image);
        if (extractions == null) {
            System.out.println("nothing found");
        } else {
            for (Extraction extraction : extractions) {
                System.out.println(extraction.getType() + " " + extraction.getText());
            }
            this.writeToXml(extractions);
        }
    }
    
    private void writeToXml(Extraction[] extractions)
        throws ApplicationException
    {
        try {
            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            
            Document doc = docBuilder.newDocument();
            Element root = doc.createElement("extractions");
            doc.appendChild(root);
            
            for (Extraction extraction : extractions) {
                Element e = doc.createElement("extraction");
                Element type = doc.createElement("type");
                type.setTextContent(extraction.getType().toString());
                Element text = doc.createElement("text");
                text.setTextContent(extraction.getText());
                byte[] bytes = extraction.getRaw();
                if (bytes != null) {
                    Element raw = doc.createElement("raw");
                    BASE64Encoder b64 = new BASE64Encoder();
                    raw.setTextContent(b64.encode(extraction.getRaw()));
                    e.appendChild(raw);
                }
                e.appendChild(type);
                e.appendChild(text);
                root.appendChild(e);
            }
            
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StringWriter sw = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(sw));
            
            System.out.println(sw.toString());
            
        } catch (Exception ex) {
            throw new ApplicationException("could not write XML", ex);
        }
    }
    
    private static void frameReaderTest(FrameReader fr) {
        try {
            BufferedImage image = null;
            VideoImage screen = new VideoImage();
            fr.setWidthAndHeight(fr.width() / 2, fr.height() / 2);
            GammaDenoisingOperator gamma = new GammaDenoisingOperator(10);
            HighFrequenceRegionFinder hfr = new HighFrequenceRegionFinder();
            LuminanceImage lum = null;
            List<BufferedImage> images = new LinkedList<BufferedImage>();
            BufferedImage all = new BufferedImage(fr.width() * 5, fr.height() * 3, BufferedImage.TYPE_INT_ARGB);
            while (true) {
                image = fr.nextFrame();
                
                Graphics g = all.getGraphics();
                
                images.clear();
                
                lum = LuminanceImage.fromBufferedImage(image, new Grayscaler() {
                    public int convert(int argb) {
                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;
                        return (r + g + g + b) / 4;
                    }
                });
                
                g.drawImage(lum.toBufferedImage(), fr.width(), 0, null);
                
                //lum = gamma.apply(lum);
                
                g.drawImage(lum.toBufferedImage(), fr.width() * 2, 0, null);
                
                Region[] regions = hfr.detect(lum, new HighFrequenceRegionFinder.RegionFilter() {
                    @Override
                    public boolean filter(Region region) {
                        Rectangle rr = region.orientedRectangle();
                        if (rr.width() < 50 || rr.height() < 50) return false;
                        return true;
                    }
                }, images);
                
                g.drawImage(image, 0, 0, null);
                
                int x = 3;
                int y = 0;
                
                for (BufferedImage im : images) {
                    g.drawImage(im, x * fr.width(), y * fr.height(), null);
                    x = (x + 1) % 5;
                    if (x == 0) y += 1;
                }
                
                for (int i = 0; i < regions.length; ++i) {
                    Region r = regions[i];
                    
                    double cov = (r.coverage(Region.BoundType.CONVEX_POLYGON) * 1
                                + r.coverage(Region.BoundType.ORIENTED_RECTANGLE) * 2
                                + r.coverage(Region.BoundType.AXIS_ALIGNED_RECTANGLE) * 3) / 6;
                    
                    Polygon p;
                    Point[] coords;
                    
                    p = new Polygon();
                    coords = r.axisAlignedRectangle().points();
                    for (int j = 0; j < coords.length; ++j) {
                        p.addPoint(coords[j].x(), coords[j].y());
                    }
                    g.setColor(new Color(0f, 0f, 1f, (float) cov));
                    g.fillPolygon(p);
                    
                    p = new Polygon();
                    coords = r.orientedRectangle().points();
                    for (int j = 0; j < coords.length; ++j) {
                        p.addPoint(coords[j].x(), coords[j].y());
                    }
                    g.setColor(new Color(0f, 1f, 0f, (float) cov));
                    g.fillPolygon(p);
                    
                    p = new Polygon();
                    coords = r.convexPolygon().points();
                    for (int j = 0; j < coords.length; ++j) {
                        p.addPoint(coords[j].x(), coords[j].y());
                    }
                    g.setColor(new Color(1f, 0f, 0f, (float) cov));
                    g.fillPolygon(p);
                }
                
                screen.setImage(all);
            }
        } catch (FrameReaderException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    public static void main(String[] args) {
        try {
            //FrameReader fr = XugglerFrameReader.openDevice("/dev/video0", "video4linux2");
            FrameReader fr = OpenCVFrameReader.openDevice(0);
        	frameReaderTest(fr);
        } catch (FrameReaderException ex) {
            System.err.println(ex.getMessage());
        }
        
        /*
        try {
            GammaDenoisingOperator gamma = new GammaDenoisingOperator(10);
            BinDCT bindct = new BinDCT();
            Binarizer bin = new GlobalBinarizer(new MeanValueThresholdSelector());
            DilationOperator dilate = new DilationOperator(5, 5);
            ConnectedComponentsFinder ccf = new ConnectedComponentsFinder();
            DownsamplingOperator down = new DownsamplingOperator(2);
            
            for (int k = 1; k <= 21; ++k) {
                long t = System.currentTimeMillis();
                
                BufferedImage image = ImageIO.read(new File(String.format("/home/ewie/workspace/Barcode/images/camera/%d.jpg", k)));
                
                LuminanceImage lum1 = LuminanceImage.fromBufferedImage(image, new Grayscaler() {
                    @Override
                    public int convert(int argb) {
                        int r = (argb >> 16 ) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;
                        return (r + g + g + b) / 4;
                    }
                });
                
                LuminanceImage lum2 = down.apply(gamma.apply(lum1));
                LuminanceImage lum4 = bindct.process(lum2);
                LuminanceImage lum6 = bin.apply(lum4);
                LuminanceImage lum8 = dilate.apply(lum6);
                
                Region[] regions = ccf.process(lum8);
                
                Graphics g = image.getGraphics();
                
                for (int i = 0; i < regions.length; ++i) {
                    Region r = regions[i];
                    
                    {
                        Rectangle rr = r.orientedRectangle();
                        if (rr.width() < 66 || rr.height() < 66) {
                            continue;
                        }
                    }
                    
                    double cov = (r.coverage(Region.BoundType.CONVEX_POLYGON) * 1
                                + r.coverage(Region.BoundType.ORIENTED_RECTANGLE) * 2
                                + r.coverage(Region.BoundType.AXIS_ALIGNED_RECTANGLE) * 3) / 6;
                    
                    if (cov < 0.5) {
                        continue;
                    }
                    
                    Polygon p;
                    Point[] coords;
                    
                    p = new Polygon();
                    coords = r.axisAlignedRectangle().points();
                    for (int j = 0; j < coords.length; ++j) {
                        p.addPoint(coords[j].x() * down.getFactor(), coords[j].y() * down.getFactor());
                    }
                    g.setColor(new Color(0f, 0f, 1f, (float) cov));
                    g.fillPolygon(p);
                    
                    p = new Polygon();
                    coords = r.orientedRectangle().points();
                    for (int j = 0; j < coords.length; ++j) {
                        p.addPoint(coords[j].x() * down.getFactor(), coords[j].y() * down.getFactor());
                    }
                    g.setColor(new Color(0f, 1f, 0f, (float) cov));
                    g.fillPolygon(p);
                    
                    p = new Polygon();
                    coords = r.convexPolygon().points();
                    int x = 0;
                    int y = 0;
                    for (int j = 0; j < coords.length; ++j) {
                        p.addPoint(coords[j].x() * down.getFactor(), coords[j].y() * down.getFactor());
                        x += coords[j].x() * down.getFactor();
                        y += coords[j].y() * down.getFactor();
                    }
                    x /= coords.length;
                    y /= coords.length;
                    g.setColor(new Color(1f, 0f, 0f, (float) cov));
                    g.fillPolygon(p);
                    g.setColor(Color.WHITE);
                    g.drawString(String.format("%g", cov), x, y);
                }
                
                ShowImages.showWindow(image, String.format("dct-%d", k));
                
                t = System.currentTimeMillis() - t;
                
                System.out.println(t);
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        */
        
        /*
        try {
            final int downFactor = 2;
            
            GammaDenoisingOperator gamma = new GammaDenoisingOperator(10.0);
            DownsamplingOperator down = new DownsamplingOperator(downFactor);
            HighFrequenceRegionFinder brd = new HighFrequenceRegionFinder();
            
            //MedianFilter median = new MedianFilter(3);
                
            for (int k = 1; k <= 21; ++k) {
                long t = System.currentTimeMillis();

                BufferedImage image = ImageIO.read(new File(String.format("/home/ewie/workspace/Barcode/images/camera/%d.jpg", k)));
                LuminanceImage lum = LuminanceImage.fromBufferedImage(image, new LuminanceImage.Grayscaler() {
                    @Override
                    public int convert(int argb) {
                        int r = (argb >> 16 ) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;
                        return (r + g + g + b) / 4;
                    }
                });
                
    
                LuminanceImage lum2 = down.apply(gamma.apply(lum));
                
                Region[] regions = brd.detect(lum2, new RegionFilter() {
                    @Override
                    public boolean filter(Region r) {
                        {
                            Rectangle rr = r.orientedRectangle();
                            // Exclude regions less than 50 pixels wide or high.
                            if (rr.width() < 50 / downFactor || rr.height() < 50 / downFactor) return false;
                        }
                        // Exclude sparse regions. That are regions which are spanned by
                        // less than a certain percentage of all pixels that make up the
                        // region.
                        if (r.coverage(Region.BoundType.CONVEX_POLYGON) < 0.5) return false;
                        
                        //System.out.printf("%g %g %g\n",
                        //    r.coverage(Region.BoundType.CONVEX_POLYGON),
                        //    r.coverage(Region.BoundType.ORIENTED_RECTANGLE),
                        //    r.coverage(Region.BoundType.AXIS_ALIGNED_RECTANGLE));
                        
                        return true;
                    }
                });
                
                Graphics g = image.getGraphics();
                
                for (int i = 0; i < regions.length; ++i) {
                    Region r = regions[i];
                    
                    double cov = (r.coverage(Region.BoundType.CONVEX_POLYGON) * 1
                                + r.coverage(Region.BoundType.ORIENTED_RECTANGLE) * 2
                                + r.coverage(Region.BoundType.AXIS_ALIGNED_RECTANGLE) * 3) / 6;
                    
                    Polygon p;
                    Point[] coords;
                    
                    p = new Polygon();
                    coords = r.axisAlignedRectangle().points();
                    for (int j = 0; j < coords.length; ++j) {
                        p.addPoint(coords[j].x() * downFactor, coords[j].y() * downFactor);
                    }
                    g.setColor(new Color(0f, 0f, 1f, (float) cov));
                    g.fillPolygon(p);
                    
                    p = new Polygon();
                    coords = r.orientedRectangle().points();
                    for (int j = 0; j < coords.length; ++j) {
                        p.addPoint(coords[j].x() * downFactor, coords[j].y() * downFactor);
                    }
                    g.setColor(new Color(0f, 1f, 0f, (float) cov));
                    g.fillPolygon(p);
                    
                    p = new Polygon();
                    coords = r.convexPolygon().points();
                    for (int j = 0; j < coords.length; ++j) {
                        p.addPoint(coords[j].x() * downFactor, coords[j].y() * downFactor);
                    }
                    g.setColor(new Color(1f, 0f, 0f, (float) cov));
                    g.fillPolygon(p);
                }
                
                //ShowImages.showWindow(image, "image");
                
                System.out.println(System.currentTimeMillis() - t);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        */
        
        /*
        Application app = new Application();
        try {
            app.run();
        } catch (ApplicationException ex) {
            System.out.println(ex.getMessage());
        }
        */
    }
}