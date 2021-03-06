/*
 * Copyright (c) 2012-2013 Erik Wienhold & René Richter
 * Licensed under the BSD 3-Clause License.
 */
package de.tu_chemnitz.mi.barcd.app;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class ImageDisplay extends JFrame {
    private static final long serialVersionUID = -5184274079279042989L;

    private final ImageComponent imageComponent;

    public ImageDisplay() {
        super();
        imageComponent = new ImageComponent();
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        getContentPane().add(imageComponent);
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

        @Override
        public synchronized void paint(Graphics g) {
            if (image != null) {
                g.drawImage(image, 0, 0, this);
            }
        }

        private class ImageRunnable implements Runnable {
            private final Image image;

            public ImageRunnable(Image image) {
                super();
                this.image = image;
            }

            @Override
            public void run() {
                ImageComponent.this.image = image;
                Dimension size = new Dimension(
                    ImageComponent.this.image.getWidth(null),
                    ImageComponent.this.image.getHeight(null));
                if (!size.equals(ImageComponent.this.size)) {
                    ImageComponent.this.size = size;
                    ImageDisplay.this.setSize(size);
                    ImageDisplay.this.setVisible(true);
                }
                ImageComponent.this.repaint();
            }
        }
    }
}
