/* possible actions
 * public BufferedImage checkBrightness(BufferedImage biPrep)
 * public BufferedImage checkBlur(BufferedImage biBlur)
 * public BufferedImage checkRotation(BufferedImage biRot)
 * public BufferedImage checkShadow(BufferedImage biShadow)
 *
 * public BufferedImage checkImage(BufferedImage biWhole)
 */

package de.tu_chemnitz.mi.barcd.image;

import java.awt.image.BufferedImage;

public class ImproveImage {

    ImageProcessing ipNew = new ImageProcessing();
    BufferedImage biNew;

    public BufferedImage checkBrightness(BufferedImage biPrep){

        biNew = new BufferedImage(biPrep.getWidth(), biPrep.getHeight(), BufferedImage.TYPE_INT_RGB);

        // brighten image up a bit
        // this also might help with a little bit blurry images and pictures where the color difference is not big enough so always do it
        biNew = ipNew.brightenBufferedImage_linear(biPrep, 1.1f);

        return biNew;
    }

    public BufferedImage checkBlur(BufferedImage biBlur){

        biNew = new BufferedImage(biBlur.getWidth(), biBlur.getHeight(), BufferedImage.TYPE_INT_RGB);

        int iBlur = ipNew.isBlurry(biBlur);
        if (iBlur > 100) {
            // sometimes sharpness stays the same and if 5 times is not enough then it will never be
            int iRun = 0;
            while ( (iBlur > 100) && (iRun < 5) ){
                iRun++;
                biNew = ipNew.sharpenBufferedImage(biBlur);
            }
            return biNew;
        }else{
            return biBlur;
        }

    }

    public BufferedImage checkRotation(BufferedImage biRot){

        biNew = new BufferedImage(biRot.getWidth(), biRot.getHeight(), BufferedImage.TYPE_INT_RGB);

        // circle the image in a readable position
        // as it will never be a perfect image/angle always rotate the image
        biNew = ipNew.rotateBufferedImage(biRot, -(ipNew.isRotated(biRot)));

        // do it two times and 90 degree will be found too but takes longer
        //biNew = ipNew.rotateBufferedImage(biRot, -(ipNew.isRotated(biRot)));

        return biNew;
    }

    public BufferedImage checkShadow(BufferedImage biShadow){

        biNew = new BufferedImage(biShadow.getWidth(), biShadow.getHeight(), BufferedImage.TYPE_INT_RGB);

        // check if shadow is there and clear it if necessary
        if (ipNew.hasShade(biShadow, 3)){
            biNew  = ipNew.findShades(biShadow);
            return biNew;
        }else{
            return biShadow;
        }
    }

    public BufferedImage checkImage(BufferedImage biWhole){

        biNew = new BufferedImage(biWhole.getWidth(), biWhole.getHeight(), BufferedImage.TYPE_INT_RGB);

        biNew = checkBrightness(biWhole);
        biNew = checkShadow(biNew);
        biNew = checkRotation(biNew);
        biNew = checkBlur(biNew);

        return biNew;
    }
}
