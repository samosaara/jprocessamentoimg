package io.samosaara.jprocessamentoimg;

public class Principal {
    private Principal() {
        final PainelPrincipal principal = new PainelPrincipal();
        principal.setVisible(true);
    }

    public static void main(String[] args) {
        new Principal();
    }
}
