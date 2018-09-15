package io.samosaara.jprocessamentoimg;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePanel extends JComponent {
    private BufferedImage image;

    public ImagePanel() {
        this(null);
    }

    public ImagePanel(BufferedImage image) {
        this.image = image;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.clearRect(0, 0, this.getWidth(), this.getHeight());
        if (this.image == null) {
            return;
        }
        final var scaled = this.getScaledDimension(new Dimension(this.image.getWidth(), this.image.getHeight()), this.getSize());
        var scaleImg = this.image.getScaledInstance(scaled.width, scaled.height, Image.SCALE_SMOOTH);
        final int centerX = (this.getWidth() / 2) - (scaled.width / 2);
        final int centerY = (this.getHeight() / 2) - (scaled.height / 2);
        g.drawImage(scaleImg, centerX, centerY, scaled.width, scaled.height, null);
    }

    private Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {
        int original_width = imgSize.width;
        int original_height = imgSize.height;
        int bound_width = boundary.width;
        int bound_height = boundary.height;
        int new_width = original_width;
        int new_height = original_height;

        // first check if we need to scale width
        if (original_width > bound_width) {
            //scale width to fit
            new_width = bound_width;
            //scale height to maintain aspect ratio
            new_height = (new_width * original_height) / original_width;
        }

        // then check if we need to scale even with the new height
        if (new_height > bound_height) {
            //scale height to fit instead
            new_height = bound_height;
            //scale width to maintain aspect ratio
            new_width = (new_height * original_width) / original_height;
        }

        return new Dimension(new_width, new_height);
    }

    public BufferedImage getImage() {
        return this.image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

}
