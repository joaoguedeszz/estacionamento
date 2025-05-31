import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EstacionamentoGUI extends JFrame {

    private List<Vaga> vagas = new ArrayList<>();
    private JTable tabelaVagas;
    private DefaultTableModel tableModel;
    private final double precoPorHora = 5.0;
    private JPanel mainPanel;
    private BufferedImage imagemFundo;

    public EstacionamentoGUI(int totalVagas) {
        for (int i = 1; i <= totalVagas; i++) {
            vagas.add(new Vaga(i));
        }

        try {
            imagemFundo = ImageIO.read(new File("img/fundo.png"));
        } catch (IOException e) {
            imagemFundo = null;
        }

        setTitle("Sistema de Estacionamento");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(900, 600));

        // Menu lateral renovado
        JPanel menuLateral = new JPanel();
        menuLateral.setLayout(new BoxLayout(menuLateral, BoxLayout.Y_AXIS));
        menuLateral.setBackground(new Color(25, 33, 43));  // tom escuro moderno
        menuLateral.setBorder(new EmptyBorder(25, 15, 25, 15));
        menuLateral.setPreferredSize(new Dimension(240, getHeight()));

        // Logo maior, centralizado e com espaço abaixo
        try {
            BufferedImage logoImg = ImageIO.read(new File("img/logozinha.png"));
            ImageIcon logoIcon = new ImageIcon(logoImg.getScaledInstance(200, 110, Image.SCALE_SMOOTH));
            JLabel logoLabel = new JLabel(logoIcon);
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            logoLabel.setBorder(new EmptyBorder(0, 0, 30, 0));
            menuLateral.add(logoLabel);
        } catch (IOException ignored) {}

        // Botões menu lateral (com espaçamento e alinhamento)
        menuLateral.add(criarBotaoMenu("Página Inicial"));
        menuLateral.add(Box.createVerticalStrut(15));
        menuLateral.add(criarBotaoMenu("Vagas"));
        menuLateral.add(Box.createVerticalStrut(15));
        menuLateral.add(criarBotaoMenu("Buscar por Placa"));
        menuLateral.add(Box.createVerticalGlue()); // espaço flexível
        menuLateral.add(criarBotaoMenu("Sair"));

        add(menuLateral, BorderLayout.WEST);

        // Painel principal
        mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(new Color(245, 248, 252)); // fundo bem claro, quase branco
        add(mainPanel, BorderLayout.CENTER);

        // Tabela vagas com linhas alternadas e alinhamento centralizado
        String[] colunas = {"Nº Vaga", "Status", "Carro", "Entrada"};
        tableModel = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaVagas = new JTable(tableModel);
        tabelaVagas.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tabelaVagas.setRowHeight(32);
        tabelaVagas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 17));
        tabelaVagas.getTableHeader().setBackground(new Color(10, 94, 181));
        tabelaVagas.getTableHeader().setForeground(Color.WHITE);
        tabelaVagas.setSelectionBackground(new Color(10, 94, 181, 180));
        tabelaVagas.setSelectionForeground(Color.WHITE);
        tabelaVagas.setFillsViewportHeight(true);
        tabelaVagas.setShowGrid(false);
        tabelaVagas.setIntercellSpacing(new Dimension(0, 0));

        // Renderer para linhas alternadas
        tabelaVagas.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private final Color evenColor = new Color(240, 245, 250);
            private final Color oddColor = Color.WHITE;

            @Override
            public Component getTableCellRendererComponent(JTable table,
                                                           Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? evenColor : oddColor);
                }
                setHorizontalAlignment(col == 2 ? LEFT : CENTER); // alinhamento carro à esquerda, outros centralizados
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabelaVagas);
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(10, 94, 181)),
                "Vagas do Estacionamento", 0, 0,
                new Font("Segoe UI", Font.BOLD, 16),
                new Color(10, 94, 181)));

        // Botões ação em painel horizontal moderno
        JButton estacionarBtn = criarBotaoAcao("Estacionar");
        JButton liberarBtn = criarBotaoAcao("Liberar Vaga");
        JButton adicionarVagasBtn = criarBotaoAcao("Adicionar Vagas");

        JPanel painelBotoesVagas = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 15));
        painelBotoesVagas.setBackground(new Color(245, 248, 252));
        painelBotoesVagas.add(estacionarBtn);
        painelBotoesVagas.add(liberarBtn);
        painelBotoesVagas.add(adicionarVagasBtn);

        // Ações botões menu lateral
        Component[] botoesMenu = menuLateral.getComponents();
        ((JButton) botoesMenu[1]).addActionListener(e -> mostrarMensagemBemVindo());
        ((JButton) botoesMenu[3]).addActionListener(e -> mostrarTelaVagas(scrollPane, painelBotoesVagas));
        ((JButton) botoesMenu[5]).addActionListener(e -> buscarPorPlaca());
        ((JButton) botoesMenu[7]).addActionListener(e -> System.exit(0));

        // Ações botões ações vagas
        estacionarBtn.addActionListener(e -> estacionarCarro());
        liberarBtn.addActionListener(e -> liberarVaga());
        adicionarVagasBtn.addActionListener(e -> adicionarVagas());

        atualizarTabela();
        mostrarMensagemBemVindo();

        setVisible(true);
    }

    private void mostrarMensagemBemVindo() {
        mainPanel.removeAll();

        if (imagemFundo != null) {
            PainelImagemFundo painelFundo = new PainelImagemFundo(imagemFundo);
            painelFundo.setLayout(new BorderLayout());

            JLabel texto = new JLabel("Bem-vindo ao Sistema de Estacionamento");
            texto.setFont(new Font("Segoe UI", Font.BOLD, 30));
            texto.setForeground(new Color(10, 94, 181));
            texto.setHorizontalAlignment(SwingConstants.CENTER);

            JPanel painelTransparente = new JPanel(new BorderLayout());
            painelTransparente.setOpaque(false);
            painelTransparente.setBorder(new EmptyBorder(20, 20, 20, 20));
            painelTransparente.add(texto, BorderLayout.CENTER);

            painelFundo.add(painelTransparente, BorderLayout.CENTER);
            mainPanel.setLayout(new BorderLayout());
            mainPanel.add(painelFundo, BorderLayout.CENTER);

        } else {
            JLabel bemVindoLabel = new JLabel("Bem-vindo ao Sistema de Estacionamento");
            bemVindoLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
            bemVindoLabel.setForeground(new Color(10, 94, 181));
            bemVindoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            mainPanel.add(bemVindoLabel, BorderLayout.CENTER);
        }

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void mostrarTelaVagas(JScrollPane tabelaScrollPane, JPanel botoesPanel) {
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout(15, 15));
        mainPanel.add(tabelaScrollPane, BorderLayout.CENTER);
        mainPanel.add(botoesPanel, BorderLayout.SOUTH);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private JButton criarBotaoMenu(String texto) {
        JButton botao = new JButton(texto);
        botao.setFocusPainted(false);
        botao.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 17));
        botao.setForeground(Color.WHITE);
        botao.setBackground(new Color(30, 39, 54));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        botao.setAlignmentX(Component.CENTER_ALIGNMENT);
        botao.setMaximumSize(new Dimension(210, 50));

        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                botao.setBackground(new Color(10, 94, 181));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                botao.setBackground(new Color(30, 39, 54));
            }
        });
        return botao;
    }

    private JButton criarBotaoAcao(String texto) {
        JButton botao = new JButton(texto);
        botao.setFocusPainted(false);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 16));
        botao.setForeground(Color.WHITE);
        botao.setBackground(new Color(10, 94, 181));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        botao.setPreferredSize(new Dimension(160, 45));
        botao.setOpaque(true);

        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                botao.setBackground(new Color(8, 76, 146));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                botao.setBackground(new Color(10, 94, 181));
            }
        });
        return botao;
    }

    private void atualizarTabela() {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (Vaga v : vagas) {
            String status = v.isOcupada() ? "Ocupada" : "Livre";
            String carro = v.isOcupada() ? v.getCarro().toString() : "-";
            String horaEntrada = v.isOcupada() ? v.getHoraEntrada().format(formatter) : "-";
            tableModel.addRow(new Object[]{v.getNumero(), status, carro, horaEntrada});
        }
    }

    private void estacionarCarro() {
        String placa = JOptionPane.showInputDialog(this, "Informe a placa do carro:");
        if (placa == null || placa.trim().isEmpty()) return;

        Vaga vagaLivre = vagas.stream().filter(v -> !v.isOcupada()).findFirst().orElse(null);
        if (vagaLivre == null) {
            JOptionPane.showMessageDialog(this, "Não há vagas livres!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Carro carro = new Carro(placa.trim());
        vagaLivre.ocupar(carro, LocalDateTime.now());
        atualizarTabela();
    }

    private void liberarVaga() {
        int linha = tabelaVagas.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma vaga para liberar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Vaga vaga = vagas.get(linha);
        if (!vaga.isOcupada()) {
            JOptionPane.showMessageDialog(this, "A vaga já está livre.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        LocalDateTime saida = LocalDateTime.now();
        Duration duracao = Duration.between(vaga.getHoraEntrada(), saida);
        double horas = duracao.toMinutes() / 60.0;
        double valor = Math.ceil(horas) * precoPorHora;

        String mensagem = String.format("O valor a pagar é: R$ %.2f\nConfirmar liberação da vaga?", valor);
        int confirmar = JOptionPane.showConfirmDialog(this, mensagem, "Liberação", JOptionPane.YES_NO_OPTION);
        if (confirmar == JOptionPane.YES_OPTION) {
            vaga.liberar();
            atualizarTabela();
        }
    }

    private void adicionarVagas() {
        String input = JOptionPane.showInputDialog(this, "Quantas vagas deseja adicionar?");
        if (input == null || input.trim().isEmpty()) return;

        try {
            int qtd = Integer.parseInt(input);
            if (qtd <= 0) {
                JOptionPane.showMessageDialog(this, "Digite um número válido.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int maiorNumero = vagas.stream().mapToInt(Vaga::getNumero).max().orElse(0);
            for (int i = 1; i <= qtd; i++) {
                vagas.add(new Vaga(maiorNumero + i));
            }
            atualizarTabela();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Digite um número válido.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarPorPlaca() {
        String placa = JOptionPane.showInputDialog(this, "Digite a placa para busca:");
        if (placa == null || placa.trim().isEmpty()) return;

        List<Vaga> vagasEncontradas = new ArrayList<>();
        for (Vaga v : vagas) {
            if (v.isOcupada() && v.getCarro().getPlaca().equalsIgnoreCase(placa.trim())) {
                vagasEncontradas.add(v);
            }
        }
        if (vagasEncontradas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhuma vaga encontrada para a placa: " + placa, "Resultado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (Vaga v : vagasEncontradas) {
            sb.append("Vaga nº ").append(v.getNumero())
                    .append(" - Entrada: ").append(v.getHoraEntrada().format(formatter)).append("\n");
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Vagas encontradas", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EstacionamentoGUI(5));
    }

    // Classe interna para pintar imagem de fundo no painel
    private static class PainelImagemFundo extends JPanel {
        private BufferedImage imagem;

        public PainelImagemFundo(BufferedImage img) {
            this.imagem = img;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (imagem != null) {
                int width = getWidth();
                int height = getHeight();
                g.drawImage(imagem, 0, 0, width, height, this);
            }
        }
    }

    // Classe Vaga
    private static class Vaga {
        private final int numero;
        private boolean ocupada;
        private Carro carro;
        private LocalDateTime horaEntrada;

        public Vaga(int numero) {
            this.numero = numero;
            this.ocupada = false;
        }

        public int getNumero() {
            return numero;
        }

        public boolean isOcupada() {
            return ocupada;
        }

        public Carro getCarro() {
            return carro;
        }

        public LocalDateTime getHoraEntrada() {
            return horaEntrada;
        }

        public void ocupar(Carro carro, LocalDateTime entrada) {
            this.carro = carro;
            this.horaEntrada = entrada;
            this.ocupada = true;
        }

        public void liberar() {
            this.carro = null;
            this.horaEntrada = null;
            this.ocupada = false;
        }
    }

    // Classe Carro
    private static class Carro {
        private final String placa;

        public Carro(String placa) {
            this.placa = placa.toUpperCase();
        }

        public String getPlaca() {
            return placa;
        }

        @Override
        public String toString() {
            return placa;
        }
    }
}
