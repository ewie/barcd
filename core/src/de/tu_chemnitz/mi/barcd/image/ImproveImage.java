/*
 * Copyright (c) 2012-2013 Erik Wienhold & René Richter
 * Licensed under the BSD 3-Clause License.
 */
package de.tu_chemnitz.mi.barcd.image;

import java.awt.image.BufferedImage;

/**
 * Class to improve the image quality of images with barcodes.
 *
 * @author René Richter
 * @version     1.0
 * @since       2013-04-01
 *
 */
public class ImproveImage {

    ImageProcessing ipNew = new ImageProcessing();


    /**
     * Normalizes an images brightness.
     *
     * Checks how bright the image is in average and brightens it up if necessary.
     *
     * @param biPrep    The image to check for brightness.
     * @return          The image with normalized brightness.
     */
    public BufferedImage checkBrightness(BufferedImage biPrep){

        // brighten image up a bit to normalize it
        // this also might help with a little bit blurry images and pictures where the color difference is not big enough
        int iBrightness = ipNew.isDark(biPrep);
        if (iBrightness < 128){
            float fRescale = (float)115 / iBrightness;
            biPrep = ipNew.brighten_linear(biPrep, fRescale);
        }

        return biPrep;
    }




    /**
     * Sharpens an image if needed.
     *
     * Checks how blurry the image is and sharpens it if necessary.
     *
     * @param biBlur    The image to check for blurriness.
     * @return          The sharpened image.
     */
    public BufferedImage checkBlur(BufferedImage biBlur){

        int iBlur = ipNew.isBlurry(biBlur);
        if (iBlur > 200) {
            // if 5 times is not enough then it will most likely never be
            int iRun = 0;
            while ( (iBlur > 200) && (iRun < 5) ){
                iRun++;
                biBlur = ipNew.sharpen(biBlur);
            }
        }
        return biBlur;

    }




    /**
     * Rotates an image.
     *
     * Checks how the barcode in the image is rotated and then rotates the image back.
     *
     * @param biRot     The image to rotate.
     * @return          The rotated image.
     */
    public BufferedImage checkRotation(BufferedImage biRot){


        // circle the image in a readable position
        // as it will never be a perfect image/angle always rotate the image
        biRot = ipNew.rotate(biRot, -(ipNew.isRotated(biRot)));

        // do it two times and 90 degree will be found too but takes longer
        //biNew = ipNew.rotateBufferedImage(biRot, -(ipNew.isRotated(biRot)));

        return biRot;
    }




    /**
     * Removes the shadows in an image.
     *
     * Checks for shadows and if positive removes them.
     *
     * @param biShadow  The image to check for shadows.
     * @return          The image without shadows.
     */
    public BufferedImage checkShadow(BufferedImage biShadow){

        // check if shadow is there and clear it if necessary
        if (ipNew.hasShade(biShadow, 3)){
            biShadow  = ipNew.findShades(biShadow);
        }
        return biShadow;
    }




    /**
     * Improves the quality of a image with a barcode inside.
     *
     * Checks for brightness, shadows, rotation and blurriness. In this sequence.
     *
     * @param biWhole   The image to improve.
     * @return          The improved image.
     */
    public BufferedImage checkImage(BufferedImage biWhole){

        biWhole = checkBrightness(biWhole);
        biWhole = checkShadow(biWhole);
        biWhole = checkRotation(biWhole);
        biWhole = checkBlur(biWhole);

        return biWhole;
    }
}
