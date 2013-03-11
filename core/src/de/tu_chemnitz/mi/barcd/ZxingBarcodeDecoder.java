package de.tu_chemnitz.mi.barcd;

import java.awt.image.BufferedImage;
import java.util.EnumMap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.GenericMultipleBarcodeReader;
import com.google.zxing.multi.MultipleBarcodeReader;

import de.tu_chemnitz.mi.barcd.geometry.Point;

/**
 * A barcode decoder using ZXing.
 * 
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 * 
 * @see <a href="http://code.google.com/p/zxing">ZXing</a>
 */
public class ZxingBarcodeDecoder implements Decoder {
    private static final EnumMap<BarcodeFormat, BarcodeType> format2type;
    
    private MultiFormatReader multiFormatReader = new MultiFormatReader();
    
    private MultipleBarcodeReader multipleBarcodeReader =
        new GenericMultipleBarcodeReader(multiFormatReader);
    
    @Override
    public Barcode decode(BufferedImage image) {
        BinaryBitmap bitmap = createBitmap(image);
        Result result;
        try {
            result = multiFormatReader.decodeWithState(bitmap);
        } catch (NotFoundException ex) {
            return null;
        }
        if (result == null) {
            return null;
        }
        return translateResult(result);
    }
    
    @Override
    public Barcode[] decodeMultiple(BufferedImage image) {
        BinaryBitmap bitmap = createBitmap(image);
        Result[] results;
        try {
            results = multipleBarcodeReader.decodeMultiple(bitmap);
        } catch (NotFoundException ex) {
            return null;
        }
        if (results == null) {
            return null;
        }
        return translateResults(results);
    }
    
    private BinaryBitmap createBitmap(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();
        
        int[] pixels = image.getRGB(0, 0, w, h, null, 0, w);
        
        RGBLuminanceSource lum = new RGBLuminanceSource(w, h, pixels);
        Binarizer binarizer = new HybridBinarizer(lum);
        BinaryBitmap bitmap = new BinaryBitmap(binarizer);
        
        return bitmap;
    }
    
    private Barcode[] translateResults(Result[] results) {
        Barcode[] barcodes = new Barcode[results.length];
        for (int i = 0; i < results.length; ++i) {
            barcodes[i] = translateResult(results[i]);
        }
        return barcodes;
    }
    
    private Barcode translateResult(Result result) {
        return new Barcode(
            translateBarcodeFormat(result.getBarcodeFormat()),
            result.getText(),
            result.getRawBytes(),
            translateResultPoints(result.getResultPoints()));
    }
    
    private BarcodeType translateBarcodeFormat(BarcodeFormat format) {
        BarcodeType type = format2type.get(format);
        if (type == null) {
            type = BarcodeType.UNKNOWN;
        }
        return type;
    }
    
    private Point[] translateResultPoints(ResultPoint[] resultPoints) {
        Point[] points = new Point[resultPoints.length];
        for (int j = 0; j < resultPoints.length; ++j) {
            points[j] = new Point(resultPoints[j].getX(), resultPoints[j].getY());
        }
        return points;
    }

    static {
        format2type = new EnumMap<BarcodeFormat, BarcodeType>(BarcodeFormat.class);
        format2type.put(BarcodeFormat.AZTEC,        BarcodeType.AZTEC_CODE);
        format2type.put(BarcodeFormat.CODABAR,      BarcodeType.CODABAR);
        format2type.put(BarcodeFormat.CODE_39,      BarcodeType.CODE_39);
        format2type.put(BarcodeFormat.CODE_93,      BarcodeType.CODE_93);
        format2type.put(BarcodeFormat.CODE_128,     BarcodeType.CODE_128);
        format2type.put(BarcodeFormat.DATA_MATRIX,  BarcodeType.DATA_MATRIX);
        format2type.put(BarcodeFormat.EAN_8,        BarcodeType.EAN_8);
        format2type.put(BarcodeFormat.EAN_13,       BarcodeType.EAN_13);
        format2type.put(BarcodeFormat.ITF,          BarcodeType.INTERLEAVED_2_OF_5);
        format2type.put(BarcodeFormat.MAXICODE,     BarcodeType.MAXICODE);
        format2type.put(BarcodeFormat.QR_CODE,      BarcodeType.QR_CODE);
        format2type.put(BarcodeFormat.PDF_417,      BarcodeType.PDF417);
        format2type.put(BarcodeFormat.RSS_14,       BarcodeType.GS1_DATABAR_OMNIDIRECTIONAL);
        format2type.put(BarcodeFormat.RSS_EXPANDED, BarcodeType.GS1_DATABAR_EXPANDED);
        format2type.put(BarcodeFormat.UPC_A,        BarcodeType.UPC_A);
        format2type.put(BarcodeFormat.UPC_E,        BarcodeType.UPC_E);
    }
}