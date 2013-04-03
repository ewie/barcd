/* possible actions
 * public Boolean hasShade(BufferedImage biShade, int iExactness)
 * public int isBlurry(BufferedImage biBlurry)
 * xxxxxxxxxxxxxxx public Boolean isDark(BufferedImage biSlope)
 * public int isRotated(BufferedImage biRotate)
 * xxxxxxxxxxxxxxx public int isSlope(BufferedImage biSlope)
 * public BufferedImage bluredBufferedImage(BufferedImage biBlur)
 * public BufferedImage brightenBufferedImage_linear(BufferedImage biBright, float brightness)
 * public BufferedImage brightenBufferedImage_quadratic(BufferedImage biBright)
 * public BufferedImage cutoffRectangleBufferedImage(BufferedImage biCut, int iStartX, int iStartY, int iWidthCut, int iHeightCut)
 * public BufferedImage edgeDetectBufferedImage(BufferedImage biEdge)
 * public BufferedImage findShades(BufferedImage biShade, int iPixelDistance){
 * public BufferedImage giveHistogram(BufferedImage biHisto, Boolean bDraw)
 * public BufferedImage interpolateBufferedImage(BufferedImage biSplit, Point pPoints[], float brightness)
 * public BufferedImage negativBufferedImage(BufferedImage biNegativ)
 * public BufferedImage rotateBufferedImage(BufferedImage biRotate, int iAngle)
 * public BufferedImage sharpenBufferedImage(BufferedImage biSharp)
 */

package de.tu_chemnitz.mi.barcd.image;

import ij.ImagePlus;
import ij.plugin.filter.BackgroundSubtracter;
import ij.plugin.filter.UnsharpMask;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.RescaleOp;
import java.awt.image.ShortLookupTable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
//import barcode.CannyEdgeDetector;

class ImageProcessing extends JFrame  {

  //calculates brightness only on basis of the RGB colors
  private static int getBrightness(Color c){
      return (int)Math.sqrt(
              c.getRed()    * c.getRed()    * .241 +
              c.getGreen()  * c.getGreen()  * .691 +
              c.getBlue()   * c.getBlue()   * .068);
  }


  //checks for gradient difference over the edges
  //iExactness gives the number of pixels checked on one edge
  //only reliable if the input image has only the barcode(with its white margin) and no other surroundings in it
  //possible improvement could be if the average of a 3x3 point is calculated and returning the shadow edges
  public Boolean hasShade(BufferedImage biShade, int iExactness){
      int iWidth = biShade.getWidth();
      int iHeight = biShade.getHeight();
      //check around the barcode with iWidthPart(vertical) or iHeightPart(horizontal) away from the limits of the image
      int iWidthPart = (int)(iWidth/(float)iExactness);
      int iHeightPart = (int)(iHeight/(float)iExactness);

      //first line is upper horizontal line in image, second is lower horizontal line
      //third is left vertical line, fourth is right vertical line
      int[] iBrightnessGrid = new int[iExactness*4];


      //check out the brightness of pixels in the area around the barcode
      for (int i = 0; i < iExactness; i++){
          iBrightnessGrid[i] = getBrightness(new Color(
                  biShade.getRGB( (int)((i+0.5f)*iWidthPart), (int)(iHeight/20.0f))  ));
          iBrightnessGrid[i+iExactness] = getBrightness(new Color(
                  biShade.getRGB( (int)((i+0.5f)*iWidthPart), (iHeight-(int)(iHeight/20.0f)))  ));
          iBrightnessGrid[i+(iExactness*2)] = getBrightness(new Color(
                  biShade.getRGB( (int)(iWidth/20.0f), (int)((i+0.5f)*iHeightPart))  ));
          iBrightnessGrid[i+(iExactness*3)] = getBrightness(new Color(
                  biShade.getRGB( (iWidth-(int)(iWidth/20.0f)), (int)((i+0.5f)*iHeightPart))  ));
      }

      //calculate the maximum brightness difference of all pixels
      int iMin = 255, iMax = 0, iDifference = 0;
      for (int i = 0; i < (iExactness*4); i++){
          if (iMin > iBrightnessGrid[i]) iMin = iBrightnessGrid[i];
          if (iMax < iBrightnessGrid[i]) iMax = iBrightnessGrid[i];
//          System.out.print(iBrightnessGrid[i]);System.out.print(", ");
      }
      iDifference = iMax - iMin;
//      System.out.println(); System.out.println(iDifference);

      //if the brightness difference bigger than 100 there is (most likely) a real/problematic shade
      if (iDifference > 100){
          return true;
      }else{
          return false;
      }
  }


  //checks if the image is blurry/not sharp enough
  //uses ImageJ for edge detection
  //not very good!!
  public int isBlurry(BufferedImage biBlurry){
    int iWidth = biBlurry.getWidth();
    int iHeight = biBlurry.getHeight();

    ImagePlus iplNew = new ImagePlus("NewImagePlus", biBlurry);
    ImageProcessor iprNew = iplNew.getProcessor().duplicate();
    iprNew.findEdges();
    int blur = 0;
    for (int x = 0; x < iWidth; x++) {
        for (int y = 0; y < iHeight; y++) {
            blur += iprNew.getPixel(x, y) & 0xFF;
        }
    }
    return (blur/(iWidth*iHeight));
  }


  //checks how dark the image is
  //not implemented yet because not needed
  public Boolean isDark(BufferedImage biSlope){

      return true;
  }


  // checks how much the barcode is rotated
  // maybe additional check for the white margin needed or other border lines could confuse the calculations
  public int isRotated(BufferedImage biRotate){
    int iWidth = biRotate.getWidth();
    int iHeight = biRotate.getHeight();

    // get pixels from image
    int[] iPixels = new int[iWidth*iHeight];
    int[] iPixelsGrid = new int[iWidth*iHeight];
    biRotate.getRGB(0, 0, iWidth, iHeight, iPixels, 0, iWidth);
    iPixelsGrid = iPixels;

    int iBrightNow = 0, iBrightLast = 0; // actual pixel's brightness and last pixel's brightness
    int iLineGrid = iHeight/30; // distance between lines to check
    int iColumnGrid = 3; // Pixel jump in a line
    int iRun = 0; // Running variable

    // create array for the first pixel in every line who is much darker
    int[] iLineCoordinates = new int[iHeight/iLineGrid];
    for (int i = 0; i < iLineCoordinates.length; i++){
        iLineCoordinates[i] = iWidth+1;
    }


    //check the gradient for every line
    for (int j = iLineGrid; j < (iHeight-iLineGrid); j+=iLineGrid){
            //check pixels in one line
            //get brightness of first pixel
            for (int k = 0; k < iColumnGrid; k++){
                iRun = iColumnGrid-k;
                iBrightNow = getBrightness(new Color(iPixels[iRun+(j*iWidth)]));
                iBrightLast = iBrightNow;

                //color pixels red for not much color difference, blue for most likely a edge
                while ( (iBrightNow+30 > (iBrightLast)) && (iRun < (iWidth-iColumnGrid)) ){
//                    iPixelsGrid[iRun+(j*iWidth)] = 0xffff0000;
                    iRun += iColumnGrid;
                    iBrightLast = iBrightNow;
                    iBrightNow = getBrightness(new Color(iPixels[iRun+(j*iWidth)]));
                }
//                System.out.print( iRun );System.out.println( ", " );
                // if color difference to big, stop and make last pixel blue
//                iPixelsGrid[iRun+(j*iWidth)] = 0xff00ffff;
                if ( (iLineCoordinates[j/iLineGrid] > iRun) )
                    iLineCoordinates[j/iLineGrid] = iRun;
            }
    }


//    for (int i=0; i<(int)(iHeight/iLineGrid); i++){
//        System.out.print( iLineCoordinates[i] );System.out.println( ", " );
//    }

    // calculate start and end point of one edge
    int[] iPointGroup = new int[iLineCoordinates.length];
    int iGroupNumber = 0;
    int iGroupMembers = 0, iBiggestGroup = 0, iMaxMembers = 0;
    int iSafetyMargin = 20; // if image is blurry the line could be not exact straight and this sets a number of pixels around to still accept the line
    int iPredictedX = 0, iLastSlope = 0, iLastN = 0;

    // first point
    iPointGroup[0] = iGroupNumber;
    iBiggestGroup = iGroupNumber;
    iGroupMembers = 1;
    for (int i = 1; i < (iHeight/iLineGrid); i++){
        // calculations use a 90 degree rotated image for y = mx+n
        // if last line was in the other direction as the actual points direct
        iPredictedX = i * iLastSlope + iLastN;
        if ( (iLineCoordinates[i] < (iPredictedX - iSafetyMargin) ) ||
             (iLineCoordinates[i] > (iPredictedX + iSafetyMargin) ) ||
             (iLineCoordinates[i] > (iHeight-10)) ){
            if (iGroupMembers > iMaxMembers){
                iBiggestGroup = iGroupNumber;
                iMaxMembers = iGroupMembers;
            }
            iGroupNumber++;
            iGroupMembers = 0;
        }
        iPointGroup[i] = iGroupNumber;
        iGroupMembers++;
        iLastSlope = (iLineCoordinates[i] - iLineCoordinates[i-1]) / iLineGrid;
//        System.out.print( iPointGroup[i] );System.out.println( ", " );
//        System.out.print( iLineCoordinates[i] );System.out.print( ", " );System.out.print( iPredictedX );System.out.println( ", " );
        iLastN = (iLineCoordinates[i-1]) - (iLastSlope * (i-1)*iLineGrid);
    }

    // check out first an last points of the biggest group
    int iEdgeStart = 0;
    int iEdgeEnd = 0;
    int iAngle = 0;
    int iHelp = 0;
    iRun = 0;

    while ( (iPointGroup[iRun] != iBiggestGroup) && (iRun < iPointGroup.length) ) {
//        System.out.print( iLineCoordinates[iRun] );System.out.print( ", " );System.out.print( (iWidth-(iLineGrid*2)) );System.out.println( ", " );
        iRun++;
    }
//    System.out.print( iLineCoordinates[iRun] );System.out.print( ",+" );System.out.print( (iWidth-(iLineGrid*2)) );System.out.println( ", " );
    iEdgeStart = iRun;
    while ( (iPointGroup[iRun] == iBiggestGroup) && (iRun < iPointGroup.length) ){
        iRun++;
    }
    iEdgeEnd = iRun;
    System.out.print( iEdgeStart );System.out.print( ", " );System.out.print( iEdgeEnd );System.out.println( ", " );
    //take 2 points more in the middle to make sure they are on the edge
    iHelp = (iEdgeEnd-iEdgeStart)/3;
    iEdgeStart = iEdgeStart + iHelp;
    iEdgeEnd = iEdgeEnd - iHelp;
    if (iEdgeStart == iEdgeEnd) iEdgeEnd++;

//  for (int i=4; i<8; i++){
//  System.out.print( iLineCoordinates[i] );System.out.println( ", " );
//}


    // calculate angle with pythagoras and trigonometry
    double a,b,c;
    a = (iEdgeEnd * iLineGrid) - (iEdgeStart * iLineGrid);
    b = Math.abs(iLineCoordinates[iEdgeStart] - iLineCoordinates[iEdgeEnd]);
    c = Math.sqrt((Math.pow(a, 2) + Math.pow(b, 2)));
    iAngle = (int) Math.toDegrees(Math.asin(b/c));
    if ((iLineCoordinates[iEdgeStart] - iLineCoordinates[iEdgeEnd]) < 0) iAngle = - iAngle;

//    System.out.print( iEdgeStart );System.out.print( ", " );System.out.print( iEdgeEnd );System.out.println( ", " );
    System.out.print( iAngle );System.out.print( ", " );

    biRotate.setRGB(0, 0, iWidth, iHeight, iPixelsGrid, 0, iWidth);

    return iAngle;
  }


  //checks if the image has a slope
  //not implemented yet
  public Boolean isSlope(BufferedImage biSlope){

      return true;
  }


  //blurs the image with a 5x5 Gauss filter
  //second possibility with a median filter is outcommented
  public BufferedImage bluredBufferedImage(BufferedImage biBlur){
      //normalized Gauss filter matrix
      float data[] = {
              0.0000f,  0.0182f,  0.0364f,  0.0182f,  0.0000f,
              0.0182f,  0.0545f,  0.0909f,  0.0545f,  0.0182f,
              0.0364f,  0.0909f,  0.1636f,  0.0909f,  0.0364f,
              0.0182f,  0.0545f,  0.0909f,  0.0545f,  0.0182f,
              0.0000f,  0.0182f,  0.0364f,  0.0182f,  0.0000f };

      //create filter with matrix
      Kernel kernel = new Kernel(5, 5, data);
      ConvolveOp convolve = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);

      //with median filter
//      ImagePlus iplNew = new ImagePlus("NewImagePlus", biBlur);
//      ImageProcessor iprNew = iplNew.getProcessor().duplicate();
//      iprNew.medianFilter();
//      return iprNew.getBufferedImage();

      //use filter on original image to create new blurred image
      BufferedImage biResult = new BufferedImage(biBlur.getWidth(), biBlur.getHeight(), BufferedImage.TYPE_INT_RGB);
      convolve.filter(biBlur, biResult);
      return biResult;
  }


  //brighten everything evenly(best for now)
  //possible improvement could be a smooth low pass filter
  public BufferedImage brightenBufferedImage_linear(BufferedImage biBright, float fBrightness){
      int iWidth = biBright.getWidth();
      int iHeight = biBright.getHeight();

      BufferedImage biResult = new BufferedImage(iWidth, iHeight, BufferedImage.TYPE_INT_RGB);
      RescaleOp rescaleOp = new RescaleOp(fBrightness, 15, null);
      rescaleOp.filter(biBright, biResult);

      return biResult;
  }


  //brighten every pixel dependent on how dark they are (dark spots become bright faster)
  //more realistic brightening process but dark parts are the most wanted for decoding barcodes
  public BufferedImage brightenBufferedImage_quadratic(BufferedImage biBright){
    //assign new values to old ones
    short[] rootBrighten = new short[256];
    for (int i = 0; i < 256; i++)
        rootBrighten[i] = (short)(Math.sqrt(i/255.0) * 255.0);

    //set lookup table for exchange
    LookupTable table = new ShortLookupTable(0, rootBrighten);
    LookupOp invertOp = new LookupOp(table, null);

    //exchange values
    BufferedImage biResult = new BufferedImage(biBright.getWidth(), biBright.getHeight(), BufferedImage.TYPE_INT_RGB);
    invertOp.filter(biBright, biResult);
    return biResult;
  }


  //takes sub-image of area from the starting point till iWidthCut and iHeightCut
  public BufferedImage cutoffRectangleBufferedImage(BufferedImage biCut, int iStartX, int iStartY, int iWidthCut, int iHeightCut){
      return biCut.getSubimage(iStartX, iStartY, iWidthCut, iHeightCut);
  }


  //shows edges through a filtering process
  //uses ImageJ
  public BufferedImage edgeDetectBufferedImage(BufferedImage biEdge){
      ImagePlus iplNew = new ImagePlus("NewImagePlus", biEdge);
      ImageProcessor iprNew = iplNew.getProcessor().duplicate();
      iprNew.findEdges();
      return iprNew.getBufferedImage();
  }


  //finds shades in BufferedImage
  //uses ImageJ
  public BufferedImage findShades(BufferedImage biShade){
//public BufferedImage findShades(BufferedImage biShade, int iPixelDistance){
      int iWidth = biShade.getWidth();
      int iHeight = biShade.getHeight();
      //check every (iPixelDistance*iPixelDistance) border pixel and calculate big changes to find shade
      //maybe improvement over case-by-case-analysis over 3 pixels
//
//      //get pixels from image
//      int[] iPixels = new int[iWidth*iHeight];
//      int[] iPixelsGrid = new int[iWidth*iHeight];
//      biShade.getRGB(0, 0, iWidth, iHeight, iPixels, 0, iWidth);
//      iPixelsGrid = iPixels;
//
//      float fAverage = 0.0f;
//      int iMax = 0, iMin = 255;
//      int iBrightNow = 0, iBrightLast = 0;
//      int iGrid = iPixelDistance;
//      //only use 10% from every site
//      int iMargin = (int)Math.round(iWidth*0.1f);
//
//      //check the gradient for every line
//      for (int j = iGrid; j < iMargin; j+=iGrid){
//          //check pixels in one line
//          for (int k = iGrid; k > 0; k--){
//          for (int i = iGrid-k; i < (iHeight-1); i+=iGrid){
//              //get brightness of current pixel and check if its the first
//              iBrightNow = getBrightness(new Color(iPixels[j+(i*iWidth)]));
//              if (iBrightLast == 0) iBrightLast = iBrightNow;
//
//              //color pixels red for not much color difference, blue for a bit and light blue for much
//              if (iBrightNow+30 < iBrightLast) iBrightNow = iBrightLast;
//              if ((iBrightNow+10 < (iBrightLast)) && (iBrightNow+30 > (iBrightLast))) iPixelsGrid[j+(i*iWidth)] = 0xff0000ff;
//              else if (iBrightNow+30 < (iBrightLast)) iPixelsGrid[j+(i*iWidth)] = 0xff00ffff;
//              else iPixelsGrid[j+(i*iWidth)] = 0xffff0000;
//
//              //calculate average and minimum/maximum
//              fAverage += iBrightNow;
//              if (iBrightNow < iMin) iMin = iBrightNow;
//              else if (iBrightNow > iMax) iMax = iBrightNow;
//
//              iBrightLast = iBrightNow;
//          }}
//      iBrightNow = 0;
//      iBrightLast = 0;
//      fAverage = fAverage / (iWidth/iPixelDistance);
//      System.out.print( iMin );System.out.print( ", " );System.out.print( iMax );System.out.println( ", " );
//      System.out.print( fAverage );System.out.print( ", " );System.out.print(666 );System.out.println( ", " );
//      }
//      //frame the shade
      BufferedImage biResult = new BufferedImage(iWidth, iHeight, BufferedImage.TYPE_INT_RGB);



      ImagePlus iplNew = new ImagePlus("NewImagePlus", biShade);
      ImageProcessor iprNew = iplNew.getProcessor();
      //iprNew.smooth();

      BackgroundSubtracter bsNew = new BackgroundSubtracter();
      bsNew.rollingBallBackground(iprNew, 30, false, true, false, false, false);
      //bsNew.run(iprNew);

      biResult = iprNew.getBufferedImage();



//      biResult.setRGB(0, 0, iWidth, iHeight, iPixelsGrid, 0, iWidth);

      return biResult;
  }


  //calculates the histogram of the image and if bDraw is true puts it in the picture
  //uses ImageJ
  //improvement could be if the histogram is drawn somewhere else
  public BufferedImage giveHistogram(BufferedImage biHisto, Boolean bDraw){
      int iWidth = biHisto.getWidth();
      int iHeight = biHisto.getHeight();

      ImagePlus iplNew = new ImagePlus("NewImagePlus", biHisto);
      ImageProcessor iprNew = iplNew.getProcessor();
      int[] iHistogram = iprNew.getHistogram();
      int iHistoMax = 0, iHistoAverage = 0, iHistoHelp = 0;

      for (int k = 0; k < iHistogram.length; k++){
          if (iHistoMax < iHistogram[k]) iHistoMax = iHistogram[k];
          iHistoHelp += iHistogram[k];
          if ((((iWidth*iHeight)/2.0f) < iHistoHelp) && (iHistoAverage == 0)) iHistoAverage = k;
      }
      System.out.print(iHistoHelp);System.out.print(", ");System.out.println(iHistoAverage);

      //draw histogram if bDraw true
      int iMore = iHeight;
      if (iHeight<266) iMore = 276;
      BufferedImage biResult = new BufferedImage(iWidth, iMore, BufferedImage.TYPE_INT_RGB);
      if (bDraw) {
          int[] iPixels = new int[iWidth*iMore];
          int iTill = 0;
          biHisto.getRGB(0, 0, iWidth, iHeight, iPixels, 0, iWidth);
          for (int i = 0; i < (iHistogram.length); i++){
              iTill = (int)((iHistogram[i]/(float)iHistoMax)*100);
              for (int j = 0; j < iTill; j++){
                  iPixels[j+(i+10)*iWidth] = 0xff000000;
              }
              for (int j = 0; j < (100-iTill); j++){
                  iPixels[(iTill+j)+(i+10)*iWidth] = 0xffffffff;
              }
          }
          biResult.setRGB(0, 0, iWidth, iMore, iPixels, 0, iWidth);
      }
      return biResult;
  }


  //rectangle within the 4 points is made brighter by 'brightness'
  //no check for number of points but should work theoretically with all kinds of polygons
  //only checked if out of boundaries not if all in one line or rectangle concave
  //improvement: use Polygon object and find boundaries with getBounds in a Rectangle
  //improvement could be 2 sets of edges/points for the left way from the highest to the lowest and the right way
  public BufferedImage interpolateBufferedImage(BufferedImage biSplit, Point pPoints[], float brightness){
      int iWidthMax = biSplit.getWidth();
      int iHeightMax = biSplit.getHeight();
      int iNumberP = pPoints.length;

      //start point and distances for cut
      int iStartX = iWidthMax;
      int iStartY = iHeightMax;
      int iEndX = 0;
      int iEndY = 0;
      int iWidthCut = 0;
      int iHeightCut = 0;

      //set the points in a vertical order
      List<Integer> qOrderedPointsY = new ArrayList<Integer>();


      //are all points within the boundaries and what is the minimum x/y + how long is the cut
      for (int i = 0; i < iNumberP; i++){
          if ((pPoints[i].x < 0) | (pPoints[i].y < 0) | (pPoints[i].x > biSplit.getWidth()) | (pPoints[i].y > biSplit.getHeight())){
              return null;
          }
          if (pPoints[i].x < iStartX) iStartX = pPoints[i].x;
          if (pPoints[i].y < iStartY) iStartY = pPoints[i].y;
          if (pPoints[i].x > iEndX) iEndX = pPoints[i].x;
          if (pPoints[i].y > iEndY) iEndY = pPoints[i].y;

          //calculation to bring points in vertical order
          int iCurrent = 0;
          int iListSize = qOrderedPointsY.size();
          while ((iListSize > iCurrent) && (pPoints[qOrderedPointsY.get(iCurrent)].y <= pPoints[i].y)){
              iCurrent++;
          }
          qOrderedPointsY.add(iCurrent, i);
      }
      iWidthCut = iEndX-iStartX;
      iHeightCut = iEndY-iStartY;


      //take pixels from old BufferedImage(with shade)
      int[] iPixelsSplitOriginal = new int[iWidthCut*iHeightCut];
      biSplit.getRGB(iStartX, iStartY, iWidthCut, iHeightCut, iPixelsSplitOriginal, 0, iWidthCut);

      //get sub-image of old BufferedImage in new one and brighten it up, than take pixels
      int[] iPixelsSplitBrighter = new int[iWidthCut*iHeightCut];
      BufferedImage biBright = new BufferedImage(iWidthCut, iHeightCut, BufferedImage.TYPE_INT_RGB);
      biBright = biSplit.getSubimage(iStartX, iStartY, iWidthCut, iHeightCut);
      biBright = brightenBufferedImage_linear(biBright, brightness);
      biBright.getRGB(0, 0, iWidthCut, iHeightCut, iPixelsSplitBrighter, 0, iWidthCut);

      //only works for quadrangles where within the vertical order the new edge changes sites every time
      //set first edge in order of the vertical points
      float m[] = new float[iNumberP], n[] = new float[iNumberP];
      int iNow = qOrderedPointsY.get(0);
      int iNext = qOrderedPointsY.get(1);
      m[0] = (float)(pPoints[iNext].y - pPoints[iNow].y) / (float)(pPoints[iNext].x - pPoints[iNow].x);
      n[0] = pPoints[iNext].y - (pPoints[iNext].x * m[0]);
      //set second to next to last edges
      for (int i = 0; i < iNumberP-2; i++){
          iNow = qOrderedPointsY.get(i);
          iNext = qOrderedPointsY.get(i+2);
          m[i+1] = (float)(pPoints[iNext].y - pPoints[iNow].y) / (float)(pPoints[iNext].x - pPoints[iNow].x);
          n[i+1] = pPoints[iNext].y - (pPoints[iNext].x * m[i+1]);
     }
      //set last edge
      iNow = qOrderedPointsY.get(iNumberP-2);
      iNext = qOrderedPointsY.get(iNumberP-1);
      m[iNumberP-1] = (float)(pPoints[iNext].y - pPoints[iNow].y) / (float)(pPoints[iNext].x - pPoints[iNow].x);
      n[iNumberP-1] = pPoints[iNext].y - (pPoints[iNext].x * m[iNumberP-1]);


      int iX1 = 0, iX2 = 0;
      iNow = 0;
      iNext = 1;
      //calculate which pixels lay in the rectangle and copy them in the original
      for (int k = 0; k < (iNumberP-1); k++){
          iNow = qOrderedPointsY.get(k);
          iNext = qOrderedPointsY.get(k+1);
          System.out.print( iNow );System.out.print( ", " );System.out.print( iNext );System.out.println( ", " );
          for (int i = pPoints[iNow].y; i < pPoints[iNext].y; i++){
              iX1 = Math.round((i - n[k])/m[k]) - iStartX;
              iX2 = Math.round((i - n[k+1])/m[k+1]) - iStartX;
              System.out.print( iX1 );System.out.print( ", " );System.out.print( iX2 );System.out.println( ", " );
              //if sites are wrong nothing would be copied
              //could be better with two sets of edges for each site
              if (iX1 > iX2){
                  int h = iX2;
                  iX2   = iX1;
                  iX1   = h;}
              System.out.print( iX1 );System.out.print( ", " );System.out.print( iX2 );System.out.println( ", " );
              for (int j = iX1; j <= iX2; j++){
                  System.out.println( (j+(i-iStartY)*iWidthCut) );
                  iPixelsSplitOriginal[j+(i-iStartY)*iWidthCut] = iPixelsSplitBrighter[j+(i-iStartY)*iWidthCut];
              }
          }
      }

      //take new part and put it in old BufferedImage
      //biBright.getRGB(0, 0, iWidthCut, iHeightCut, iPixelsSplitOriginal, 0, iWidthCut);
      biSplit.setRGB(iStartX, iStartY, iWidthCut, iHeightCut, iPixelsSplitOriginal, 0, iWidthCut);
      return biSplit;
  }


  //takes opposite color for every pixel through a filter
  public BufferedImage negativBufferedImage(BufferedImage biNegativ){
      //calculate opposite color
      short[] invert = new short[256];
      for (int i = 0; i < 256; i++)
          invert[i] = (short)(255 - i);

      //create LookupTable for the filter operation
      LookupTable table = new ShortLookupTable(0, invert);
      LookupOp invertOp = new LookupOp(table, null);

      //make new BufferedImage and filter old one in the new one
      BufferedImage biResult = new BufferedImage(biNegativ.getWidth(), biNegativ.getHeight(), BufferedImage.TYPE_INT_RGB);
      invertOp.filter(biNegativ, biResult);
      return biResult;
  }


  //rotates image clockwise around the center of the image for 'iAngle' degrees
  //(the image gets more blurry with every turn)
  //improvement: the image frame is not calculated efficiently
  //second possibility of rotating with ImageJ does not work well(picture gets smaller)
  public BufferedImage rotateBufferedImage(BufferedImage biRotate, int iAngle){
      int iWidth = biRotate.getWidth();
      int iHeight = biRotate.getHeight();
      int iCenterX = iWidth/2;
      int iCenterY = iHeight/2;
      int iNewSide = Math.round( (float)(Math.sqrt( (Math.pow(iWidth, 2) + Math.pow(iHeight, 2)) )) );
//      System.out.print(iWidth);System.out.print(", ");System.out.print(iHeight);System.out.print(", ");System.out.println(iNewSide);
      double dRadians = iAngle*Math.PI/180;

      //rotates with affine transformation back to the center of image
      AffineTransform transform = new AffineTransform();
      transform.translate(iNewSide/2, iNewSide/2);
      transform.rotate(dRadians);
      transform.translate(-iCenterX, -iCenterY);

      AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
      BufferedImage biResult = new BufferedImage(iNewSide, iNewSide, BufferedImage.TYPE_INT_RGB);
      op.filter(biRotate, biResult);

      //check for pixels for transparency and cut out rectangle with no transparency
      int iMinX = 0, iMaxX = 0, iMinY = 0, iMaxY = 0;
      int iCount = 0;
      while (!((iMinX != 0) && (iMinY != 0) && (iMaxX != 0) && (iMaxY != 0))){
          for (int i = 0; i < iNewSide; i++){
              if ((iMinY == 0) && (biResult.getRGB(i, iCount) != 0xff000000)) iMinY = iCount;
//              else biResult.setRGB(i, iCount, 0xFFFF0000);
//              System.out.print( biResult.getRGB(i, iCount) );System.out.print(", ");
              if ((iMinX == 0) && (biResult.getRGB(iCount, i)  != 0xff000000)) iMinX = iCount;
//              System.out.print(((biResult.getRGB(iCount, i) >> 24) & 0xFF));System.out.print(", ");
              if ((iMaxY == 0) && (biResult.getRGB(i, (iNewSide-1 - iCount)) != 0xff000000)) iMaxY = iNewSide - iCount;
//              System.out.print(((biResult.getRGB(i, (iNewSide-1 - iCount)) >> 24) & 0xFF));System.out.print(", ");
              if ((iMaxX == 0) && (biResult.getRGB((iNewSide-1 - iCount), i) != 0xff000000)) iMaxX = iNewSide - iCount;
//              System.out.print(((biResult.getRGB((iNewSide-1 - iCount), i) >> 24) & 0xFF) != 0);System.out.println(", ");
          }
//          System.out.println();
//          System.out.print(iMinY);System.out.print(", ");System.out.print(iMinX);System.out.print(", ");System.out.print(iMaxY);System.out.print(", ");System.out.print(iMaxX);System.out.print(", ");
//          System.out.println(iCount); System.out.println((!((iMinX != 0) && (iMinY != 0) && (iMaxX != 0) && (iMaxY != 0))));
          iCount++;
      }
     biResult = cutoffRectangleBufferedImage(biResult, iMinX, iMinY, (iMaxX-iMinX), (iMaxY-iMinY));

      return biResult;

      //rotating with ImageJ
//    ImagePlus iplNew = new ImagePlus("New", biRotate);
//    ImageProcessor iprNew = iplNew.getProcessor();
//    iprNew.rotate((double)iAngle);
//    return iprNew.getBufferedImage();
  }


  //sharpens the image through unsharp masking
  //uses ImageJ
  public BufferedImage sharpenBufferedImage(BufferedImage biSharp){
      //create ImageProcessor, convert it to float and make a snapshot
      ImagePlus iplNew = new ImagePlus("New", biSharp);
      ImageProcessor iprNew = iplNew.getProcessor().duplicate().convertToFloat();
      iprNew.snapshot();

      //create UnsharpMask and use it with the ImageProcessor to sharpen its image
      UnsharpMask usmNew = new UnsharpMask();
      double dGaussSigma = 2.0;
      float fAngle = 0.6f;
      usmNew.sharpenFloat((FloatProcessor) iprNew, dGaussSigma, fAngle);

      //get BufferedImage and return it
      return iprNew.getBufferedImage();
  }

}