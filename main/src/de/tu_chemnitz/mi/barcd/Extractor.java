package de.tu_chemnitz.mi.barcd;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import boofcv.gui.image.ShowImages;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.multi.GenericMultipleBarcodeReader;
import com.google.zxing.multi.MultipleBarcodeReader;

import de.tu_chemnitz.mi.barcd.Extraction.Type;

public class Extractor {
    private MultipleBarcodeReader reader;
    
    public Extractor() {
        this.reader = new GenericMultipleBarcodeReader(new MultiFormatReader());
    }
    
    public Extraction[] process(BufferedImage image) {
        return this.process(image, 0, 0, image.getWidth(), image.getHeight());
    }
    
    public Extraction[] process(BufferedImage image, int x, int y, int width, int height) {
        BinaryBitmap bitmap = this.getBitmap(image, x, y, width, height);
        
        try {
            BitMatrix bits = bitmap.getBlackMatrix();
            BufferedImage bitmapImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
            for (int i = 0; i < width; ++i) {
                for (int j = 0; j < height; ++j) {
                    bitmapImage.setRGB(i, j, bits.get(i, j) ? 0xff000000 : 0xffffffff);
                }
            }
            ShowImages.showWindow(bitmapImage, "bitmap");
        } catch (NotFoundException ex) {
        }
        
        try {
            com.google.zxing.Result[] results = reader.decodeMultiple(bitmap);
            return this.translateResults(results);
        } catch (NotFoundException ex) {
            return null;
        }
    }
    
    private BinaryBitmap getBitmap(BufferedImage image, int x, int y, int width, int height) {
        int[] pixels = new int[width * height];
        
        image.getRGB(x, y, width, height, pixels, 0, width);
        
        return new BinaryBitmap(
            new GlobalHistogramBinarizer(
            //new HybridBinarizer(
                new RGBLuminanceSource(width, height, pixels)));
    }
    
    private Extraction[] translateResults(Result[] results) {
        ArrayList<Extraction> extractions = new ArrayList<Extraction>(results.length);
        for (com.google.zxing.Result result : results) {
            extractions.add(new Extraction(
                this.translateFormat(result.getBarcodeFormat()),
                result.getText(),
                result.getRawBytes()));
        }
        return extractions.toArray(new Extraction[extractions.size()]);
    }
    
    /**
     * @param format the barcode format used by zxing
     * @return the extraction type used 
     */
    private Type translateFormat(BarcodeFormat format) {
        switch (format) {
        case CODE_39:  return Type.CODE39;
        case CODE_128: return Type.CODE128;
        case EAN_13:   return Type.EAN13;
        case QR_CODE:  return Type.QR;
        case UPC_A:    return Type.UPCA;
        default:       return Type.UNKNOWN;
        }
    }
}
