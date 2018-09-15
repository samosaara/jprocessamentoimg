package io.samosaara.jprocessamentoimg;

import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class PainelPrincipal extends JFrame {

    final private ImagePanel img = new ImagePanel();
    final private JTextField txtCaminhoAquivo = new JTextField();
    final private JFileChooser selecionadorImg = new JFileChooser();
    final private JButton btnFiltro = new JButton("Aplicar filtro linear");
    final private JButton btnReset = new JButton("Reset");
    private BufferedImage originalImg;
    private JProgressBar progressoFiltro;

    public PainelPrincipal() {
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final Dimension size = new Dimension(800, 600);
        this.setSize(size);
        this.setLayout(new MigLayout("fill", "[grow][]", "[][grow][]"));
        this.setPreferredSize(size);
        this.setTitle("Filtro de imagens");
        this.buildTela();
        this.buildSelecionadorArquivo();
        this.pack();
        this.setLocationRelativeTo(null);
    }

    private void buildSelecionadorArquivo() {
        final String home = Objects.requireNonNullElse(System.getenv("HOME"), System.getenv("HOMEPATH"));
        this.selecionadorImg.setCurrentDirectory(new File(home));
        this.selecionadorImg.setAcceptAllFileFilterUsed(false);
        final FileNameExtensionFilter extensoes = new FileNameExtensionFilter("Imagens",
                "jpg", "png", "jpeg", "bmp");
        this.selecionadorImg.addChoosableFileFilter(extensoes);
        this.selecionadorImg.setDialogTitle("Escolha a imagem a ser aberta");
        this.selecionadorImg.setMultiSelectionEnabled(false);
    }

    private void buildTela() {
        this.txtCaminhoAquivo.addActionListener((ev) -> this.loadImagem(new File(this.txtCaminhoAquivo.getText())));
        final JButton btnAbrir = new JButton("Abrir...");
        btnAbrir.addActionListener((event) -> {
            final var codRetorno = this.selecionadorImg.showDialog(this, "Abrir");
            if (codRetorno != JFileChooser.APPROVE_OPTION) {
                return;
            }
            this.loadImagem(this.selecionadorImg.getSelectedFile());
        });
        // botao de aplicar o filtro
        final FiltroLinearAction filtro = new FiltroLinearAction(this.img);
        this.progressoFiltro = filtro.getCarregando();
        this.btnFiltro.addActionListener(filtro);
        this.btnFiltro.setEnabled(false);
        // Botao de reset
        this.btnReset.setEnabled(false);
        this.btnReset.addActionListener((ev) -> {
            this.img.setImage(Util.deepCopy(this.originalImg));
            this.progressoFiltro.setValue(0);
            this.img.repaint();
        });
        this.add(this.txtCaminhoAquivo, "growx");
        this.add(btnAbrir, "wrap");
        this.add(this.img, "grow, span, wrap");
        this.add(this.progressoFiltro, "growx, span, wrap");
        this.add(this.btnFiltro, "growx");
        this.add(this.btnReset);
    }

    private void converteEscalaCinza(BufferedImage img) {
        final var raster = img.getRaster();
        final var coeficientesCor = new double[]{0.2126, 0.7152, 0.0722};

        var rgbaVals = new double[4];
        for (var x = 0; x < raster.getWidth(); x++) {
            for (var y = 0; y < raster.getHeight(); y++) {
                raster.getPixel(x, y, rgbaVals);

                var gray = 0.0d;
                for (var i = 0; i < 3; i++) {
                    gray += rgbaVals[i] * coeficientesCor[i];
                }
                raster.setPixel(x, y, new double[]{gray, gray, gray, rgbaVals[3]});
            }
        }
    }

    private void loadImagem(File arquivo) {
        try {
            this.originalImg = ImageIO.read(arquivo);
            this.converteEscalaCinza(this.originalImg);
            this.img.setImage(Util.deepCopy(this.originalImg));
            this.txtCaminhoAquivo.setText(arquivo.getCanonicalPath());
            this.btnFiltro.setEnabled(true);
            this.btnReset.setEnabled(true);
            this.progressoFiltro.setValue(0);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Arquivo não existe ou nao pôde ser lido",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
        this.img.repaint();
    }
}
