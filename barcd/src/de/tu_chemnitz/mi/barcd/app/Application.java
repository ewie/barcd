package de.tu_chemnitz.mi.barcd.app;

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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.tu_chemnitz.mi.barcd.Extraction;
import de.tu_chemnitz.mi.barcd.Extractor;
import de.tu_chemnitz.mi.barcd.image.GammaDenoisingOperator;
import de.tu_chemnitz.mi.barcd.image.LuminanceImage;
import de.tu_chemnitz.mi.barcd.video.Frame;
import de.tu_chemnitz.mi.barcd.video.FrameReaderSlow;
import de.tu_chemnitz.mi.barcd.video.FrameReader;
import de.tu_chemnitz.mi.barcd.video.FrameReaderException;

import au.notzed.jjmpeg.PixelFormat;
import au.notzed.jjmpeg.io.JJMediaReader;
import au.notzed.jjmpeg.io.JJMediaReader.JJReaderVideo;
import boofcv.gui.image.ShowImages;

import sun.misc.BASE64Encoder;

class ApplicationException extends Exception {
    private static final long serialVersionUID = 1L;

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}

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
    
    public static void main(String[] args) {
        try {
            FrameReader fg = new FrameReader("/media/midori/videos/rumble1_1080p.mp4");

            GammaDenoisingOperator g = new GammaDenoisingOperator(10);

            fg.skipFrame(180);
            System.out.println(fg.getFrameNumber());
            BufferedImage fr = fg.nextFrame();
            System.out.println(fg.getFrameNumber());
            
            LuminanceImage im = LuminanceImage.fromBufferedImage(fr,  new LuminanceImage.Grayscaler() {
                @Override
                public int convert(int argb) {
                    int r = (argb >> 16) & 0xff;
                    int g = (argb >> 8) & 0xff;
                    int b = argb & 0xff;
                    return (r + g + g + b) / 4;
                }
            });
            LuminanceImage im2 = g.apply(im);

            ShowImages.showWindow(fr, "fr");
            ShowImages.showWindow(im.toBufferedImage(), "im");
            ShowImages.showWindow(im2.toBufferedImage(), "im2");
        } catch (FrameReaderException ex) {
            System.err.println(ex.getMessage());
        }
        
        /*
        try {
            for (int k = 16; k <= 17; ++k) {
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
                
                //MedianFilter median = new MedianFilter(3);
                GammaDenoisingOperator gamma = new GammaDenoisingOperator(10.0);
    
                final int downFactor = 2;
                DownsamplingOperator down = new DownsamplingOperator(downFactor);

                LuminanceImage lum2 = down.apply(gamma.apply(lum));
                
                ShowImages.showWindow(lum.toBufferedImage(), "lum");
                ShowImages.showWindow(lum2.toBufferedImage(), "lum2");
                
                HighFrequenceRegionFinder brd = new HighFrequenceRegionFinder();
                RectangularRegion[] regions = brd.detect(lum2, new HighFrequenceRegionFinder.RegionFilter() {
                    @Override
                    public boolean filter(RectangularRegion r) {
                        // Exclude regions less than 50 pixels wide or high.
                        if (r.getWidth() < 50 / downFactor || r.getHeight() < 50 / downFactor) return false;
                        // Exclude sparse regions. That are regions which are spanned by
                        // less than a certain percentage of all pixels that make up the
                        // region.
                        if (r.getCoverage() < 0.5) return false;
                        return true;
                    }
                });
                
                Graphics g = image.getGraphics();
                
                for (int i = 0; i < regions.length; ++i) {
                    int x = regions[i].getX() * downFactor;
                    int y = regions[i].getY() * downFactor;
                    int w = regions[i].getWidth() * downFactor;
                    int h = regions[i].getHeight() * downFactor;
                    
                    g.setColor(new Color(1f, 0f, 1f, (float) (regions[i].getCoverage() * regions[i].getCoverage())));
                    g.fillRect(x, y, w, h);
                    g.setColor(Color.WHITE);
                    g.drawRect(x, y, w, h);
                    g.drawString(String.format("%g", regions[i].getCoverage()), x + 5, y + h - 5);
                }
                
                ShowImages.showWindow(image, "image");
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