package de.tu_chemnitz.mi.barcd.app;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.tu_chemnitz.mi.barcd.Extraction;
import de.tu_chemnitz.mi.barcd.Extractor;
import de.tu_chemnitz.mi.barcd.HighFrequenceRegionFinder;
import de.tu_chemnitz.mi.barcd.HighFrequenceRegionFinder.RegionFilter;
import de.tu_chemnitz.mi.barcd.geometry.Point;
import de.tu_chemnitz.mi.barcd.geometry.Rectangle;
import de.tu_chemnitz.mi.barcd.geometry.Region;
import de.tu_chemnitz.mi.barcd.geometry.Vector;
import de.tu_chemnitz.mi.barcd.geometry.VectorField;
import de.tu_chemnitz.mi.barcd.image.LuminanceImage;
import de.tu_chemnitz.mi.barcd.image.LuminanceImageConverter;
import de.tu_chemnitz.mi.barcd.image.op.GammaDenoisingOperator;
import de.tu_chemnitz.mi.barcd.util.VideoImageDisplay;
import de.tu_chemnitz.mi.barcd.video.BlockMatchingMotionEstimator;
import de.tu_chemnitz.mi.barcd.video.FrameReader;
import de.tu_chemnitz.mi.barcd.video.FrameReaderException;
import de.tu_chemnitz.mi.barcd.video.OpenCVFrameReader;
import de.tu_chemnitz.mi.barcd.video.ThreeStepSearchMotionEstimator;

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
            writeToXml(extractions);
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
                    raw.setTextContent(Base64.encodeBase64String(extraction.getRaw()));
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
    
    private static BufferedImage scaleImage(Image in, int w, int h){
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TRANSLUCENT);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(in, 0, 0, w, h, null);
        g.dispose();
        return out;
    }
    
    private static void frameReaderTest(FrameReader fr) {
        try {
            BufferedImage image = null, im = null;
            VideoImageDisplay video = new VideoImageDisplay();
            fr.setWidthAndHeight(fr.width() * 2, fr.height() * 2);
            HighFrequenceRegionFinder hfr = new HighFrequenceRegionFinder();
            LuminanceImage lum = null;
            LuminanceImageConverter conv = new LuminanceImageConverter();
            RegionFilter regionFilter = new RegionFilter() {
                @Override
                public boolean filter(Region region) {
                    Rectangle rr = region.orientedRectangle();
                    
                    if (rr.width() < 20 || rr.height() < 20) return false;
                    if (region.coverage(Region.BoundType.CONVEX_POLYGON) < 0.5) return false;
                    
                    return true;
                }
            };
            int k = 2;
            fr.skipFrames(8000);
            while (true) {
                image = fr.nextFrame();
                
                im = scaleImage(image, image.getWidth() / k, image.getHeight() / k);
                
                lum = conv.toLuminanceImage(im);
                
                Region[] regions = hfr.detect(lum, regionFilter);
                
                Graphics g = im.getGraphics();
                
                for (int i = 0; i < regions.length; ++i) {
                    Region r = regions[i];
                    
                    double cov = r.coverage(Region.BoundType.CONVEX_POLYGON) * 1;
                    //            + r.coverage(Region.BoundType.ORIENTED_RECTANGLE) * 2
                    //            + r.coverage(Region.BoundType.AXIS_ALIGNED_RECTANGLE) * 3) / 6;
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
                
                video.setImage(im);
            }
        } catch (FrameReaderException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public static void motionEstimatorTest(FrameReader fr)
        throws FrameReaderException
    {
        LuminanceImageConverter conv = new LuminanceImageConverter();
        
        fr.setWidthAndHeight(fr.width() * 3, fr.height() * 3);
        
        VideoImageDisplay vimage1 = new VideoImageDisplay();
        
        BlockMatchingMotionEstimator me =
            new ThreeStepSearchMotionEstimator(16);
            //new CrossSearchMotionEstimator();
        GammaDenoisingOperator gamma = new GammaDenoisingOperator(10);
        HighFrequenceRegionFinder hfr = new HighFrequenceRegionFinder();
        
        BufferedImage lastImage = fr.nextFrame();
        LuminanceImage lastLum = gamma.apply(conv.toLuminanceImage(lastImage));
        
        while (true) {
            //try {
            //    Thread.sleep(10);
            //} catch (InterruptedException ex) {
            //}
            
            BufferedImage image = fr.nextFrame();
            
            LuminanceImage lum = gamma.apply(conv.toLuminanceImage(image));
            
            Region[] regions = hfr.detect(lum, new HighFrequenceRegionFinder.RegionFilter() {
                @Override
                public boolean filter(Region region) {
                    Rectangle rr = region.orientedRectangle();
                    if (rr.width() < 50 || rr.height() < 50) {
                        return false;
                    }
                    if (region.coverage(Region.BoundType.CONVEX_POLYGON) < 0.3) {
                        return false;
                    }
                    return true;
                }
            });
            
            BufferedImage im = conv.toBufferedImage(lum);
            Graphics g = im.getGraphics();
            
            for (Region region : regions) {
                Point[] points = region.convexPolygon().points();
                Polygon poly = new Polygon();
                for (Point p : points) {
                    poly.addPoint(p.x(), p.y());
                }
                g.setColor(new Color(1, 0, 0, (float) region.coverage(Region.BoundType.CONVEX_POLYGON)));
                g.fillPolygon(poly);
            }
            
            VectorField vf = me.estimateMotionVectors(lum, lastLum);
            g.setColor(Color.GREEN);
            for (int x = vf.blockSize() / 2; x < vf.width(); x += vf.blockSize()) {
                for (int y = vf.blockSize() / 2; y < vf.height(); y += vf.blockSize()) {
                    Vector v = vf.vectorAt(x, y);
                    g.drawLine(x, y, x + v.x(), y + v.y());
                }
            }
            
            vimage1.setImage(im);
            
            lastImage = image;
            lastLum = lum;
        }
    }

    public static void main(String[] args) {
        try {
            //FrameReader fr = OpenCVFrameReader.openDevice(0);
            FrameReader fr = OpenCVFrameReader.open("/media/midori/videos/barcd/dvgrap-yadif.mp4");
            frameReaderTest(fr);
            //motionEstimatorTest(fr);
        } catch (FrameReaderException ex) {
            System.err.println(ex.getMessage());
        }

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