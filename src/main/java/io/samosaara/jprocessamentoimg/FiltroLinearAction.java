package io.samosaara.jprocessamentoimg;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class FiltroLinearAction implements ActionListener {
    private final ImagePanel target;
    private final JProgressBar carregando = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
    private JButton btnSource;
    private Thread worker = new Thread();

    public FiltroLinearAction(ImagePanel target) {
        this.target = target;
    }

    private double[] calculateMedian(WritableRaster raster, int x, int y, int kernel) {
        var pixelBuffer = new double[4];
        double median = 0;
        double alphaMedian = 0;

        for (int i = x - kernel; i <= x + kernel; i++) {
            for (int j = y - kernel; j <= y + kernel; j++) {
                if (i > 0 && i < raster.getWidth() && j > 0 && j < raster.getHeight()) {
                    raster.getPixel(i, j, pixelBuffer);
                    // Assumindo a partir daqui que pixelBuffer está populado com a cor do pixel
                    // e já que a imagem está em grayscale todos os canais estão da mesma cor
                    median += pixelBuffer[0];
                    alphaMedian += pixelBuffer[3];
                }
            }
        }
        double lateralKernel = (2.0 * kernel) + 1;
        lateralKernel *= lateralKernel; // Ao quadrado
        if (alphaMedian == 0) {
            alphaMedian = lateralKernel * 255;
        } else {
            alphaMedian /= lateralKernel;
        }
        median /= lateralKernel;
        return new double[]{median, median, median, alphaMedian};
    }

    private void applyFilter(int kernelSize) {
        final WritableRaster originalRaster = Util.deepCopy(this.target.getImage()).getRaster();
        final WritableRaster raster = this.target.getImage().getRaster();
        for (int x = 0; x < raster.getWidth(); x++) {
            if (Thread.interrupted()) {
                break;
            }
            for (int y = 0; y < raster.getHeight(); y++) {
                if (Thread.interrupted()) {
                    Thread.currentThread().interrupt();
                    break;
                }
                final double[] median = this.calculateMedian(originalRaster, x, y, kernelSize);
                raster.setPixel(x, y, median);
                this.carregando.setValue(this.carregando.getValue() + 1);
                if (y % 100 == 0) {
                    this.target.repaint();
                }
            }
        }
        this.btnSource.setText("Aplicar filtro linear");
        this.target.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final int kernelSize;
        if (this.worker.isAlive() && "Interromper".equals(e.getActionCommand())) {
            this.worker.interrupt();
            return;
        }
        try {
            kernelSize = Integer.parseInt(JOptionPane.showInputDialog(this.target, "Digite o tamnho do kernel"));
        } catch (NumberFormatException e1) {
            return;
        }
        final BufferedImage image = this.target.getImage();
        this.carregando.setMaximum(image.getWidth() * image.getHeight());
        this.worker = new Thread(() -> this.applyFilter(kernelSize));
        this.worker.start();
        if (e.getSource() instanceof JButton) {
            this.btnSource = (JButton) e.getSource();
            this.btnSource.setText("Interromper");
        }
    }

    public JProgressBar getCarregando() {
        return this.carregando;
    }
}
