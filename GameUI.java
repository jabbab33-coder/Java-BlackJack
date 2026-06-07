import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * 블랙잭 Swing UI 메인 클래스
 * 이미지 기반 카드 렌더링, 베팅 UI, 결과 표시 포함
 */
public class GameUI extends JFrame {

    // ── 색상 테마 ──────────────────────────────────────────
    private static final Color BG_TABLE    = new Color(20, 100, 50);   // 카지노 초록
    private static final Color BG_DARK     = new Color(12, 65, 32);    // 어두운 테두리
    private static final Color GOLD        = new Color(212, 175, 55);  // 골드 포인트
    private static final Color TEXT_WHITE  = new Color(240, 235, 220);
    private static final Color BTN_HIT     = new Color(180, 40, 40);
    private static final Color BTN_STAND   = new Color(30, 80, 160);
    private static final Color BTN_DEAL    = new Color(180, 140, 20);
    private static final Color SCORE_BG    = new Color(0, 0, 0, 120);

    private static final int INITIAL_CHIPS = 1000;

    // ── 게임 로직 ──────────────────────────────────────────
    private GameLogic logic;

    // ── UI 컴포넌트 ────────────────────────────────────────
    private JPanel  dealerCardPanel;
    private JPanel  playerCardPanel;
    private JLabel  dealerScoreLabel;
    private JLabel  playerScoreLabel;
    private JLabel  chipsLabel;
    private JLabel  betLabel;
    private JLabel  messageLabel;
    private JButton hitButton;
    private JButton standButton;
    private JButton dealButton;
    private JSpinner betSpinner;

    public GameUI() {
        logic = new GameLogic(INITIAL_CHIPS);
        initFrame();
        initComponents();
        updateUI();
    }

    // ── 프레임 초기 설정 ───────────────────────────────────

    private void initFrame() {
        setTitle("♠ 블랙잭 ♠");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(780, 620);
        setLocationRelativeTo(null);

        // 메인 패널 (테이블 배경)
        JPanel mainPanel = new JPanel(new BorderLayout(0, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                // 그라디언트 테이블 배경
                GradientPaint gp = new GradientPaint(
                    0, 0, BG_DARK,
                    getWidth(), getHeight(), BG_TABLE
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // 타원형 테이블 테두리
                g2.setColor(new Color(180, 140, 20, 80));
                g2.setStroke(new BasicStroke(3f));
                g2.drawOval(30, 30, getWidth() - 60, getHeight() - 60);
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 16, 20));
        mainPanel.setOpaque(false);
        setContentPane(mainPanel);
    }

    // ── 컴포넌트 구성 ──────────────────────────────────────

    private void initComponents() {
        JPanel mainPanel = (JPanel) getContentPane();

        // 상단: 딜러 영역
        mainPanel.add(buildDealerPanel(), BorderLayout.NORTH);

        // 중앙: 메시지 + 정보
        mainPanel.add(buildCenterPanel(), BorderLayout.CENTER);

        // 하단: 플레이어 영역
        mainPanel.add(buildPlayerPanel(), BorderLayout.SOUTH);
    }

    // ── 딜러 패널 ──────────────────────────────────────────

    private JPanel buildDealerPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 6));
        wrapper.setOpaque(false);

        JLabel title = makeLabel("딜 러", 13, GOLD, Font.BOLD);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        wrapper.add(title, BorderLayout.NORTH);

        dealerCardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        dealerCardPanel.setOpaque(false);
        dealerCardPanel.setPreferredSize(new Dimension(780, CardRenderer.CARD_H + 10));
        wrapper.add(dealerCardPanel, BorderLayout.CENTER);

        dealerScoreLabel = makeScoreLabel("딜러: -");
        wrapper.add(dealerScoreLabel, BorderLayout.SOUTH);

        return wrapper;
    }

    // ── 중앙 패널 (메시지 + 칩/베팅 표시) ────────────────

    private JPanel buildCenterPanel() {
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);

        messageLabel = makeLabel("베팅 후 Deal을 눌러 시작하세요", 18, GOLD, Font.BOLD);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        center.add(messageLabel, BorderLayout.CENTER);

        // 칩 / 베팅 정보
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 4));
        infoPanel.setOpaque(false);
        chipsLabel = makeLabel("칩: " + INITIAL_CHIPS, 14, TEXT_WHITE, Font.PLAIN);
        betLabel   = makeLabel("베팅: 0",              14, GOLD,       Font.PLAIN);
        infoPanel.add(chipsLabel);
        infoPanel.add(betLabel);
        center.add(infoPanel, BorderLayout.SOUTH);

        return center;
    }

    // ── 플레이어 패널 ─────────────────────────────────────

    private JPanel buildPlayerPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 8));
        wrapper.setOpaque(false);

        playerScoreLabel = makeScoreLabel("플레이어: -");
        wrapper.add(playerScoreLabel, BorderLayout.NORTH);

        playerCardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        playerCardPanel.setOpaque(false);
        playerCardPanel.setPreferredSize(new Dimension(780, CardRenderer.CARD_H + 10));
        wrapper.add(playerCardPanel, BorderLayout.CENTER);

        JLabel pTitle = makeLabel("플 레 이 어", 13, GOLD, Font.BOLD);
        pTitle.setHorizontalAlignment(SwingConstants.CENTER);
        wrapper.add(pTitle, BorderLayout.SOUTH);

        wrapper.add(buildControlPanel(), BorderLayout.SOUTH);

        return wrapper;
    }

    // ── 컨트롤 패널 (버튼 + 베팅) ────────────────────────

    private JPanel buildControlPanel() {
        JPanel control = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 8));
        control.setOpaque(false);

        // 베팅 스피너
        SpinnerNumberModel model = new SpinnerNumberModel(100, 10, INITIAL_CHIPS, 10);
        betSpinner = new JSpinner(model);
        betSpinner.setPreferredSize(new Dimension(90, 36));
        betSpinner.setFont(new Font("SansSerif", Font.BOLD, 14));

        // Deal 버튼
        dealButton = makeButton("Deal", BTN_DEAL);
        dealButton.addActionListener(e -> onDeal());

        // Hit 버튼
        hitButton = makeButton("Hit", BTN_HIT);
        hitButton.addActionListener(e -> onHit());

        // Stand 버튼
        standButton = makeButton("Stand", BTN_STAND);
        standButton.addActionListener(e -> onStand());

        JLabel betLbl = makeLabel("베팅:", 13, TEXT_WHITE, Font.PLAIN);
        control.add(betLbl);
        control.add(betSpinner);
        control.add(dealButton);
        control.add(hitButton);
        control.add(standButton);

        return control;
    }

    // ── 버튼 액션 ──────────────────────────────────────────

    private void onDeal() {
        if (logic.getState() == GameLogic.GameState.ROUND_END) {
            // 다음 라운드
            if (logic.isGameOver()) {
                showGameOver();
                return;
            }
            logic.nextRound();
        }

        if (logic.getState() == GameLogic.GameState.BETTING) {
            int bet = (int) betSpinner.getValue();
            if (!logic.placeBet(bet)) {
                showMessage("⚠ 칩이 부족합니다!", Color.ORANGE);
                return;
            }
            logic.dealInitialCards();
        }

        updateUI();

        // 블랙잭으로 바로 끝난 경우 메시지 표시
        if (logic.getState() == GameLogic.GameState.ROUND_END) {
            showResultMessage();
        }
    }

    private void onHit() {
        if (logic.getState() != GameLogic.GameState.PLAYER_TURN) return;
        logic.hit();
        updateUI();
        if (logic.getState() == GameLogic.GameState.ROUND_END) {
            showResultMessage();
        }
    }

    private void onStand() {
        if (logic.getState() != GameLogic.GameState.PLAYER_TURN) return;
        logic.stand();
        updateUI();
        showResultMessage();
    }

    // ── UI 갱신 ────────────────────────────────────────────

    private void updateUI() {
        GameLogic.GameState state = logic.getState();
        Player player = logic.getPlayer();
        Dealer dealer = logic.getDealer();

        // 카드 패널 갱신
        renderDealerCards(state);
        renderPlayerCards(player.getHand());

        // 점수 갱신
        if (state == GameLogic.GameState.BETTING) {
            dealerScoreLabel.setText("딜러: -");
            playerScoreLabel.setText("플레이어: -");
            messageLabel.setText("베팅 후 Deal을 눌러 시작하세요");
        } else {
            playerScoreLabel.setText("플레이어: " + player.getScore());
        }

        // 칩/베팅 표시
        chipsLabel.setText("칩: " + player.getChips());
        betLabel.setText("베팅: " + player.getBet());

        // 버튼 활성화 제어
        boolean isPlayerTurn = state == GameLogic.GameState.PLAYER_TURN;
        boolean isBetting    = state == GameLogic.GameState.BETTING
                            || state == GameLogic.GameState.ROUND_END;

        hitButton.setEnabled(isPlayerTurn);
        standButton.setEnabled(isPlayerTurn);
        dealButton.setEnabled(isBetting);
        betSpinner.setEnabled(isBetting);
    }

    /**
     * 딜러 카드 렌더링 (플레이어 턴 중엔 두 번째 카드 숨김)
     */
    private void renderDealerCards(GameLogic.GameState state) {
        dealerCardPanel.removeAll();
        List<Card> hand = logic.getDealer().getHand();
        boolean hideSecond = state == GameLogic.GameState.PLAYER_TURN;

        for (int i = 0; i < hand.size(); i++) {
            JLabel cardLabel;
            if (i == 1 && hideSecond) {
                cardLabel = CardRenderer.createBackLabel();
            } else {
                cardLabel = CardRenderer.createCardLabel(hand.get(i));
            }
            dealerCardPanel.add(cardLabel);
        }

        // 점수 표시 (숨길 때는 첫 카드만)
        if (!hand.isEmpty()) {
            if (hideSecond) {
                dealerScoreLabel.setText("딜러: " + hand.get(0).getValue() + " + ?");
            } else {
                dealerScoreLabel.setText("딜러: " + logic.getDealer().getScore());
            }
        }

        dealerCardPanel.revalidate();
        dealerCardPanel.repaint();
    }

    /**
     * 플레이어 카드 렌더링
     */
    private void renderPlayerCards(List<Card> hand) {
        playerCardPanel.removeAll();
        for (Card card : hand) {
            playerCardPanel.add(CardRenderer.createCardLabel(card));
        }
        playerCardPanel.revalidate();
        playerCardPanel.repaint();
    }

    /**
     * 라운드 결과 메시지 표시
     */
    private void showResultMessage() {
        GameLogic.RoundResult result = logic.getLastResult();
        if (result == null) return;

        String msg;
        Color  color;

        switch (result) {
            case PLAYER_BLACKJACK:
                msg = "🎰 블랙잭! 1.5배 획득!"; color = GOLD; break;
            case DEALER_BLACKJACK:
                msg = "딜러 블랙잭... 패배";     color = Color.RED; break;
            case BOTH_BLACKJACK:
                msg = "둘 다 블랙잭 — 무승부";   color = Color.CYAN; break;
            case PLAYER_BUST:
                msg = "💥 버스트! 패배";          color = Color.RED; break;
            case DEALER_BUST:
                msg = "딜러 버스트! 승리 🎉";    color = new Color(100, 255, 100); break;
            case PLAYER_WIN:
                msg = "승리! 🎉";               color = new Color(100, 255, 100); break;
            case DEALER_WIN:
                msg = "패배...";                color = Color.RED; break;
            case PUSH:
                msg = "무승부 (Push)";           color = Color.CYAN; break;
            default:
                msg = ""; color = TEXT_WHITE;
        }

        showMessage(msg, color);

        // 게임 오버 체크
        if (logic.isGameOver()) {
            Timer t = new Timer(1200, e -> showGameOver());
            t.setRepeats(false);
            t.start();
        } else {
            // 다음 라운드 안내
            Timer t = new Timer(300, e -> {
                String current = messageLabel.getText();
                messageLabel.setText(current + "   ▶ Deal로 계속");
            });
            t.setRepeats(false);
            t.start();
        }
    }

    private void showMessage(String msg, Color color) {
        messageLabel.setText(msg);
        messageLabel.setForeground(color);
    }

    private void showGameOver() {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "칩이 모두 소진되었습니다.\n다시 시작하시겠습니까?",
            "게임 오버",
            JOptionPane.YES_NO_OPTION
        );
        if (choice == JOptionPane.YES_OPTION) {
            logic = new GameLogic(INITIAL_CHIPS);
            ((SpinnerNumberModel) betSpinner.getModel()).setMaximum(INITIAL_CHIPS);
            updateUI();
            showMessage("새 게임 시작! 베팅 후 Deal을 누르세요.", GOLD);
        } else {
            System.exit(0);
        }
    }

    // ── 팩토리 헬퍼 ───────────────────────────────────────

    private JLabel makeLabel(String text, int size, Color color, int style) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", style, size));
        label.setForeground(color);
        return label;
    }

    private JLabel makeScoreLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                // 텍스트 뒤에 작은 둥근 배경만 그리기 (라벨 전체 X)
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                FontMetrics fm = g2.getFontMetrics(getFont());
                int textW = fm.stringWidth(getText()) + 24;
                int textH = fm.getHeight() + 6;
                int x = (getWidth() - textW) / 2;
                int y = (getHeight() - textH) / 2;
                g2.setColor(new Color(0, 0, 0, 110));
                g2.fillRoundRect(x, y, textW, textH, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        label.setFont(new Font("SansSerif", Font.BOLD, 15));
        label.setForeground(TEXT_WHITE);
        label.setOpaque(false);
        label.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        return label;
    }

    private JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isEnabled() ? bg : bg.darker().darker());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(255, 255, 255, 60));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(100, 38));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // 호버 효과
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { btn.repaint(); }
        });

        return btn;
    }

    // ── 진입점 ─────────────────────────────────────────────

    public static void main(String[] args) {
        // macOS 메뉴바 타이틀
        System.setProperty("apple.awt.application.name", "블랙잭");

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            GameUI ui = new GameUI();
            ui.setVisible(true);
        });
    }
}
