package de.tu_chemnitz.mi.barcd.util;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class VideoImageDisplay extends JFrame {
    private static final long serialVersionUID = -5184274079279042989L;
    
    private ImageComponent imageComponent;
    
    public VideoImageDisplay() {
        super();
        imageComponent = new ImageComponent();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().add(imageComponent);
        setVisible(true);
        pack();
    }
    
    public void setImage(Image image) {
        imageComponent.setImage(image);
    }

    private class ImageComponent extends JComponent {
        private static final long serialVersionUID = -8679564837940533456L;
        
        private Image image;
        
        private Dimension size;
        
        public ImageComponent() {
            this.setSize(0, 0);
        }
        
        public void setImage(Image image) {
            SwingUtilities.invokeLater(new ImageRunnable(image));
        }
        
        public synchronized void paint(Graphics g) {
            if (image != null) {
                g.drawImage(image, 0, 0, this);
            }
        }

        private class ImageRunnable implements Runnable {
            private Image image;
            
            public ImageRunnable(Image image) {
                super();
                this.image = image;
            }
            
            public void run() {
                ImageComponent.this.image = image;
                Dimension size = new Dimension(
                    ImageComponent.this.image.getWidth(null),
                    ImageComponent.this.image.getHeight(null));
                if (!size.equals(ImageComponent.this.size)) {
                    ImageComponent.this.size = size;
                    VideoImageDisplay.this.setSize(size);
                    VideoImageDisplay.this.setVisible(true);
                }
                ImageComponent.this.repaint();
            }
        }
    }
}
