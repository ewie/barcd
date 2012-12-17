package de.tu_chemnitz.mi.barcd.video;

import de.tu_chemnitz.mi.barcd.image.LuminanceImage;
import au.notzed.jjmpeg.AVCodecContext;
import au.notzed.jjmpeg.AVFrame;
import au.notzed.jjmpeg.AVPlane;

public class Frame {
    private AVFrame frame;
    private AVCodecContext codecContext;
    
    public Frame(AVFrame frame, AVCodecContext codecContext) {
        this.frame = frame;
        this.codecContext = codecContext;
    }
    
    public int getWidth() {
        return this.codecContext.getWidth();
    }
    
    public int getHeight() {
        return this.codecContext.getHeight();
    }
    
    public LuminanceImage toLuminanceImage() {
        return toLuminanceImage(new LuminanceImage(getWidth(), getHeight()));
    }
    
    public LuminanceImage toLuminanceImage(LuminanceImage image) {
        if (image.getWidth() != getWidth() || image.getHeight() != getHeight()) {
            return null;
        }
        
        AVPlane yplane = frame.getPlaneAt(0,  this.codecContext.getPixFmt(), getWidth(), getHeight());
        //AVPlane uplane = frame.getPlaneAt(1, this.codecContext.getPixFmt(), width, height);
        //AVPlane vplane = frame.getPlaneAt(2, this.codecContext.getPixFmt(), width, height);
        
        byte[] ydata = new byte[yplane.data.capacity()];
        //byte[] udata = new byte[uplane.data.capacity()];
        //byte[] vdata = new byte[vplane.data.capacity()];
        
        int lineSize = frame.getLineSizeAt(0);
        
        yplane.data.get(ydata);
        //uplane.data.get(udata);
        //vplane.data.get(vdata);
        
        for (int y = 0; y < getHeight(); ++y) {
            for (int x = 0; x < getWidth(); ++x) {
                int Y = ydata[x + lineSize * y] & 0xff;
                /*
                int U = udata[x/2 + (lineSize/2) * (y/2)] & 0xff;
                int V = vdata[x/2 + (lineSize/2) * (y/2)] & 0xff;
                int r = Y + (1402 + V) / 1000;
                int g = Y - (344 * U + 714 * V) / 1000;
                int b = Y + (1772 * V) / 1000;
                out.setValue(x, y, (r + g + g + b) / 4);
                */
                image.setValueAt(x, y, Y);
            }
        }
        
        return image;
    }
    
    public int getNumber() {
        return this.frame.getCodedPictureNumber();
    }
}
