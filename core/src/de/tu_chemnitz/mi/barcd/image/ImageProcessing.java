package de.tu_chemnitz.mi.barcd.image;
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
import ij.*;
import ij.plugin.filter.*;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

/**
 * A class to improve the quality of images who contain barcodes.
 * 
 * @author      René Richter
 * @version     1.0
 * @since       2013-04-01
 *
 */
class ImageProcessing extends JFrame  {
  
    private static final long serialVersionUID = 1L;




/**
 * Calculates the brightness on basis of the RGB colors.
 * 
 * @param c     color from which the brightness shall be calculated 
 * @return      gives back the brightness between 0(dark) and 255(bright)
 */
private static int getBrightness(Color c){
      return (int)Math.sqrt(
              c.getRed()    * c.getRed()    * .241 +
              c.getGreen()  * c.getGreen()  * .691 +
              c.getBlue()   * c.getBlue()   * .068);
  }

  

/**
 * Checks if there is a shadow partly darkening the image.
 * 
 * Method checks for the gradient difference on the edges.
 * <p>
 * It is only reliable if the input image has only the barcode
 * (with its white margin!) and no other surroundings in it. As those
 * surroundings will produce a different gradient.
 * <p>
 * Image has to be 20x20 at least or a ArrayIndexOutOfBoundsException is thrown.
 * 
 * @param biShade       The image to inspect.
 * @param iExactness    Describes the number of pixels to check on every edge ( 1 <= iExactness <= Minimum(ImageWidth, ImageHeight) )
 * @return              True if a shadow was found, false if not.
 */
public Boolean hasShade(BufferedImage biShade, int iExactness){
  //possible improvement could be if the average of a 3x3 point is calculated and returning the shadow edges
      if (iExactness < 1) throw new IllegalArgumentException( "Exactness must be at least 1." );
      int iWidth = biShade.getWidth();
      int iHeight = biShade.getHeight();
      if ( (iWidth<20) || (iHeight<20) ) throw new IllegalArgumentException( "Image must be at least 20x20 pixel big." );
      
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
      }
      iDifference = iMax - iMin;
      
      //if the brightness difference is bigger than 100 there is (most likely) a real/problematic shade
      if (iDifference > 100){
          return true;
      }else{
          return false;
      }
  }

  


/**
 * Checks how blurry an image is.
 * 
 * Uses a ImageJ plug-in for the calculation of the Mexican-Hat-Filter
 * /Laplacian-of-Gaussian-Filter and then simply
 * takes the maximum brightness from all pixels.
 * <p>
 * As a image gets more blurry a higher number is returned.
 * 
 * @param biBlurry      The image to inspect.
 * @return              How blurry is the image as an Integer(higher is more blurry).
 */
public int isBlurry(BufferedImage biBlurry){
    int iWidth = biBlurry.getWidth();
    int iHeight = biBlurry.getHeight();

    ImagePlus iplNew = new ImagePlus("NewImagePlus", biBlurry);
    ImageProcessor iprNew = iplNew.getProcessor().duplicate().convertToFloat();

    //calculate LoG filter
    Mexican_Hat_Filter mhfNew = new Mexican_Hat_Filter();
    mhfNew.run((FloatProcessor)iprNew);
    int[] iPixels = new int[iWidth*iHeight];
    iprNew.getBufferedImage().getRGB(0, 0, iWidth, iHeight, iPixels, 0, iWidth);
    
    //look for brightest pixel
    int iBright = 0, iMaxBright = 0;
    for(int i = 0; i < (iWidth*iHeight); i++){
        iBright = getBrightness(new Color(iPixels[0]));
        if (iBright > iMaxBright) iMaxBright = iBright;
    }
    //System.out.println(iMaxBright);    
    
    return iMaxBright;
  }

  


/**
 * Checks how dark an image is.
 * 
 * Uses ImageJ to get the histogram and calculates the average brightness of all pixels.
 * 
 * @param biDark        The image to inspect.
 * @return              How dark is the image as an Integer(smaller is darker).
 */
public int isDark(BufferedImage biDark){
    int iWidth = biDark.getWidth();
    int iHeight = biDark.getHeight();
    
    //get histogram
    ImagePlus iplNew = new ImagePlus("NewImagePlus", biDark);
    ImageProcessor iprNew = iplNew.getProcessor();
    int[] iHistogram = iprNew.getHistogram();
    int iHistoAverage = 0, iHistoHelp = 0;

    //calculate the real average pixel brightness and which brightness has the most pixel
    for (int k = 0; k < iHistogram.length; k++){
        iHistoHelp += iHistogram[k];
        if ((((iWidth*iHeight)/2.0f) < iHistoHelp) && (iHistoAverage == 0)) iHistoAverage = k;
    }
    int k = iHistoAverage + 1;
    while ((iHistogram[k] == 0) && (k < 256)){
        k++;          
    }
    iHistoAverage += (int)((k - iHistoAverage)/2);

    return iHistoAverage;
  }

  
  // has to be minimum 30 pixels high
/**
 * Checks how much the barcode in the image is rotated.
 * 
 * Checks for a big brightness difference in scanlines from the left edge.
 * Tries to find the longest line of darker pixels and calculates the angle to the y-axis.
 * <p>
 * The image needs to be at least 30 pixel high.
 * 
 * @param biRotate      The image to inspect.
 * @return              Angle of rotation.
 */
public int isRotated(BufferedImage biRotate){
 // maybe additional check for the white margin needed or other border lines could confuse the calculations
    int iWidth = biRotate.getWidth();
    int iHeight = biRotate.getHeight();
    if (iHeight < 30 ) throw new IllegalArgumentException( "Image height must be at least 30 pixel." );

    // get pixels from image
    int[] iPixels = new int[iWidth*iHeight];
    int[] iPixelsGrid = new int[iWidth*iHeight];
    biRotate.getRGB(0, 0, iWidth, iHeight, iPixels, 0, iWidth);
    iPixelsGrid = iPixels;
    
    int iBrightNow = 0, iBrightLast = 0; // actual pixel's brightness and last pixel's brightness
    int iLineGrid = (int)(iHeight/30); // distance between lines to check
    int iColumnGrid = 3; // Pixel jump in a line
    int iRun = 0; // Running variable
    
    // create array for the first pixel in every line who is much darker
    int[] iLineCoordinates = new int[(int)(iHeight/iLineGrid)];
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
            
                // if color difference to big, stop
                while ( (iBrightNow+30 > (iBrightLast)) && (iRun < (iWidth-iColumnGrid)) ){
                    iRun += iColumnGrid; 
                    iBrightLast = iBrightNow;
                    iBrightNow = getBrightness(new Color(iPixels[iRun+(j*iWidth)]));
                }
                // is the actual last Point more left than the last one from that line
                if ( (iLineCoordinates[(int)(j/iLineGrid)] > iRun) ) 
                    iLineCoordinates[(int)(j/iLineGrid)] = iRun;
            }
    }

        
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
    for (int i = 1; i < ((int)(iHeight/iLineGrid)); i++){
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
        iLastSlope = (int)((iLineCoordinates[i] - iLineCoordinates[i-1]) / iLineGrid);
        iLastN = (iLineCoordinates[i-1]) - (iLastSlope * (i-1)*iLineGrid);
    }
    
    // check out first an last points of the biggest group
    int iEdgeStart = 0;
    int iEdgeEnd = 0;
    int iAngle = 0;
    int iHelp = 0;
    iRun = 0;
    
    while ( (iPointGroup[iRun] != iBiggestGroup) && (iRun < iPointGroup.length) ) {
        iRun++;
    }
    iEdgeStart = iRun;
    while ( (iPointGroup[iRun] == iBiggestGroup) && (iRun < iPointGroup.length) ){
        iRun++;
    }
    iEdgeEnd = iRun;
    //take 2 points more in the middle to make sure they are on the edge
    iHelp = (int)((iEdgeEnd-iEdgeStart)/3);
    iEdgeStart = iEdgeStart + iHelp;
    iEdgeEnd = iEdgeEnd - iHelp;
    if (iEdgeStart == iEdgeEnd) iEdgeEnd++;
    
    
    // calculate angle with pythagoras and trigonometry
    double a,b,c;
    a = (iEdgeEnd * iLineGrid) - (iEdgeStart * iLineGrid);
    b = Math.abs(iLineCoordinates[iEdgeStart] - iLineCoordinates[iEdgeEnd]);
    c = Math.sqrt((Math.pow(a, 2) + Math.pow(b, 2)));
    iAngle = (int) Math.toDegrees(Math.asin(b/c));
    if ((iLineCoordinates[iEdgeStart] - iLineCoordinates[iEdgeEnd]) < 0) iAngle = - iAngle;
        
    biRotate.setRGB(0, 0, iWidth, iHeight, iPixelsGrid, 0, iWidth);

    return iAngle;
  }



  
/**
 * Checks for the angle of a possible slope.
 * 
 * Not yet implmented.
 * 
 * @param biSlope       The image to inspect.
 * @return              Angle of slope. 
 */
public Integer isSlope(BufferedImage biSlope){

      return 0;
  }

  


/**
 * Blurs a image with a 5x5-Gaussian-Filter.
 * 
 * Convolves the image with a normalized filter matrix with gaussian values.
 * 
 * @param biBlur        The image to blur.
 * @return              The blurred image.
 */
public BufferedImage blurBufferedImage_gaussian(BufferedImage biBlur){
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
            
      //use filter on original image to create new blurred image
      BufferedImage biResult = new BufferedImage(biBlur.getWidth(), biBlur.getHeight(), BufferedImage.TYPE_INT_RGB); 
      convolve.filter(biBlur, biResult);
      return biResult;
  }
  



/**
* Blurs a image with a Mean-Filter.
* 
* Uses the Median-Filter from ImageJ
* 
* @param biBlur         The image to blur.
* @return               The blurred image.
*/
public BufferedImage blurBufferedImage_mean(BufferedImage biBlur){
    
    ImagePlus iplNew = new ImagePlus("NewImagePlus", biBlur);
    ImageProcessor iprNew = iplNew.getProcessor().duplicate();
    iprNew.medianFilter();
    return iprNew.getBufferedImage();
}

  


/**
 * Brightens up a image with a linear filter.
 * 
 * With the linear filter everything in the image is brightened up evenly.
 * <p>
 * The normal image has a value of fBrighness = 1.0.
 * Everything above brightens the image, everything under it darkens it.
 * The most useful interval is between 0.0 and 2.0 .
 * 
 * @param biBright      The image to blur.
 * @param fBrightness   Brightening/Darkening Factor.
 * @return              The brightened/darkened image.
 */
public BufferedImage brightenBufferedImage_linear(BufferedImage biBright, float fBrightness){
      int iWidth = biBright.getWidth();
      int iHeight = biBright.getHeight();
      
      BufferedImage biResult = new BufferedImage(iWidth, iHeight, BufferedImage.TYPE_INT_RGB);
      RescaleOp rescaleOp = new RescaleOp(fBrightness, 15, null);
      rescaleOp.filter(biBright, biResult);
      
      return biResult;
  }

  


/**
 * Brightens up a image with a quadratic filter.
 * 
 * Brightens every pixel dependent on how dark they are (dark spots become bright faster).
 * <p>
 * The old color value is set to the new 'brighter' one.
 * ( colorNew = (colorOld/255)*(colorOld/255)* 255 )
 * <p>
 * Realistic brightening process.
 * 
 * @param biBright      The image to blur.
 * @return              The brightened/darkened image.
 */
public BufferedImage brightenBufferedImage_quadratic(BufferedImage biBright){
    //assign new values to old ones
    short[] rootBrighten = new short[256];
    for (int i = 0; i < 256; i++)
        rootBrighten[i] = (short)(Math.sqrt((double)i/255.0) * 255.0);
    
    //set lookup table for exchange
    LookupTable table = new ShortLookupTable(0, rootBrighten);
    LookupOp invertOp = new LookupOp(table, null);
    
    //exchanges values from pixel colors with LookupTable counterpart
    BufferedImage biResult = new BufferedImage(biBright.getWidth(), biBright.getHeight(), BufferedImage.TYPE_INT_RGB);
    invertOp.filter(biBright, biResult);
    return biResult;
  }
   



/**
 * Cuts out a partial image of the whole.
 * 
 * Copies a sub-image to a new image and returns it.
 * 
 * @param biCut         The image to take a cut from.
 * @param iStartX       x variable of the starting point.
 * @param iStartY       y variable of the starting point.
 * @param iWidthCut     Width of the cut from the starting point.
 * @param iHeightCut    Height of the cut from the starting point.
 * @return              The cut-off-image.
 */
public BufferedImage cutoffRectangleBufferedImage(BufferedImage biCut, int iStartX, int iStartY, int iWidthCut, int iHeightCut){
    if ( (iStartX < 0) || (iStartX > (biCut.getWidth()-1)) || ((iStartX+iWidthCut) > biCut.getWidth()) ||
         (iStartY < 0) || (iStartY > (biCut.getHeight()-1)) || ((iStartY+iHeightCut) > biCut.getHeight())
    ){
        throw new IllegalArgumentException( "Sub-image goes out of the image." );
    }
    
    
    return biCut.getSubimage(iStartX, iStartY, iWidthCut, iHeightCut);
  }
  
  


/**
 * Shows Edges in a image.
 * 
 * Uses the edge detection of ImageJ.
 * 
 * @param biEdge        The image to show the edges on.
 * @return              The image of the edges.
 */
public BufferedImage edgeDetectBufferedImage(BufferedImage biEdge){
    
      ImagePlus iplNew = new ImagePlus("NewImagePlus", biEdge);
      ImageProcessor iprNew = iplNew.getProcessor().duplicate().convertToFloat();
      iprNew.findEdges();

      return iprNew.getBufferedImage();
  }
  
  


/**
 * Searches for the shadows in a image and takes them out.
 * 
 * Uses the BackgroundSubtracter from ImageJ.
 * 
 * @param biShade       The image to find the shadows in.
 * @return              The image without the shadows.
 */
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

  


/**
 * Gives back the histogram of a image.
 * 
 * ImageJ is used to calculate the histogram.
 * 
 * @param biHisto       The image to make the histogram from.
 * @return              The image with the histogram at the left edge.
 */
public BufferedImage giveHistogram(BufferedImage biHisto){
      int iWidth = biHisto.getWidth();
      int iHeight = biHisto.getHeight();
      
      //get histogram
      ImagePlus iplNew = new ImagePlus("NewImagePlus", biHisto);
      ImageProcessor iprNew = iplNew.getProcessor();
      int[] iHistogram = iprNew.getHistogram();
      
      //calculate which brightness has the most pixel
      int iHistoMax = 0;
      for (int k = 0; k < iHistogram.length; k++){
          if (iHistoMax < iHistogram[k]) iHistoMax = iHistogram[k];
      }

      //draw histogram at the left edge
      int iMore = iHeight;
      if (iHeight<266) iMore = 276;
      BufferedImage biResult = new BufferedImage(iWidth, iMore, BufferedImage.TYPE_INT_RGB);
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
      return biResult;
  }
  
  


/**
 * Brightens a rectangle constructed from the pPoints.
 * 
 * (still developed)
 * Takes a part of the image that contains the pPoints
 * and brightens all the pixels within the theoretical rectangle.
 *<p>
 * The number of points is not checked as it should work with any number of points. 
 * <p>
 * Does not work for concave rectangles or if the points are in one line.
 * 
 * @param biSplit       The image to brighten partially.
 * @param pPoints       The points of the rectangle.
 * @param brightness    The brightening factor.
 * @return              The image with a brightened rectangular part.
 */
public BufferedImage interpolateBufferedImage(BufferedImage biSplit, Point pPoints[], float brightness){
//improvement: use Polygon object and find boundaries with getBounds in a Rectangle
//improvement could be 2 sets of edges/points for the left way from the highest to the lowest and the right way
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
      n[0] = (float)pPoints[iNext].y - ((float)pPoints[iNext].x * m[0]);
      //set second to next to last edges
      for (int i = 0; i < iNumberP-2; i++){
          iNow = qOrderedPointsY.get(i);
          iNext = qOrderedPointsY.get(i+2);
          m[i+1] = (float)(pPoints[iNext].y - pPoints[iNow].y) / (float)(pPoints[iNext].x - pPoints[iNow].x);
          n[i+1] = (float)pPoints[iNext].y - ((float)pPoints[iNext].x * m[i+1]);
     }
      //set last edge
      iNow = qOrderedPointsY.get(iNumberP-2);
      iNext = qOrderedPointsY.get(iNumberP-1);
      m[iNumberP-1] = (float)(pPoints[iNext].y - pPoints[iNow].y) / (float)(pPoints[iNext].x - pPoints[iNow].x);
      n[iNumberP-1] = (float)pPoints[iNext].y - ((float)pPoints[iNext].x * m[iNumberP-1]);

      
      int iX1 = 0, iX2 = 0;
      iNow = 0;
      iNext = 1;
      //calculate which pixels lay in the rectangle and copy them in the original
      for (int k = 0; k < (iNumberP-1); k++){
          iNow = qOrderedPointsY.get(k);
          iNext = qOrderedPointsY.get(k+1);
          System.out.print( iNow );System.out.print( ", " );System.out.print( iNext );System.out.println( ", " );
          for (int i = pPoints[iNow].y; i < pPoints[iNext].y; i++){
              iX1 = (int)Math.round(((float)i - n[k])/m[k]) - iStartX;
              iX2 = (int)Math.round(((float)i - n[k+1])/m[k+1]) - iStartX;
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



  
/**
 * Replaces the image with it's negative.
 * 
 * Replaces every pixels colors with their negatives ( colorNew = 255 - colorOld ).
 * 
 * @param biNegativ     The image to filter.
 * @return              The negative image of the input image.
 */
public BufferedImage negativBufferedImage(BufferedImage biNegativ){
      //calculate opposite color
      short[] invert = new short[256];
      for (int i = 0; i < 256; i++) 
          invert[i] = (short)(255 - i);
      
      //create LookupTable for the filter operation
      LookupTable table = new ShortLookupTable(0, invert);
      LookupOp invertOp = new LookupOp(table, null);
      
      //make new BufferedImage and replace old ones with new ones
      BufferedImage biResult = new BufferedImage(biNegativ.getWidth(), biNegativ.getHeight(), BufferedImage.TYPE_INT_RGB);
      invertOp.filter(biNegativ, biResult);
      return biResult;
  }



  
/**
 * Rotates the image clockwise for iAngle degrees.
 * 
 * The image is rotated with a affine transformation and
 * the frame is adjusted afterwards.
 * <p>
 * The image gets a bit blurry with every turn.
 * 
 * @param biRotate      The image to rotate.
 * @param iAngle        The rotation angle.
 * @return              The rotated image of the input image.
 */
public BufferedImage rotateBufferedImage(BufferedImage biRotate, int iAngle){  
//improvement: the image frame is not calculated efficiently
      int iWidth = biRotate.getWidth();  
      int iHeight = biRotate.getHeight();
      int iCenterX = (int) iWidth/2;
      int iCenterY = (int) iHeight/2;
      int iNewSide = Math.round( (float)(Math.sqrt( (Math.pow(iWidth, 2) + Math.pow(iHeight, 2)) )) );
//      System.out.print(iWidth);System.out.print(", ");System.out.print(iHeight);System.out.print(", ");System.out.println(iNewSide);
      double dRadians = iAngle*Math.PI/180;
            
      //rotates with affine transformation back to the center of image
      AffineTransform transform = new AffineTransform();
      transform.translate((int)(iNewSide/2), (int)(iNewSide/2));
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
  }
  
  


/**
 * Sharpens an image.
 * 
 * Uses the Unsharp-Mask-Filter from ImageJ.
 * 
 * @param biSharp       The image to sharpen.
 * @return              The sharpened image.
 */
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
      
      return iprNew.getBufferedImage();
  }
  
}