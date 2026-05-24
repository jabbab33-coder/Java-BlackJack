package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * 카드 이미지를 Java2D로 직접 그리는 클래스
 * 외부 이미지 파일 없이 프로그래밍으로 카드 렌더링
 */
public class CardRenderer {

    public static final int CARD_W = 90;
    public static final int CARD_H = 130;

    private static final Color RED_COLOR  = new Color(200, 30, 30);
    private static final Color DARK_COLOR = new Color(20, 20, 20);
    private static final Color CARD_BG    = new Color(255, 252, 245);
    private static final Color CARD_BACK  = new Color(20, 60, 120);
    private static final Color SHADOW     = new Color(0, 0, 0, 60);

    /**
     * 카드 한 장을 이미지로 변환하여 JLabel로 반환
     */
    public static JLabel createCardLabel(Card card) {
        ImageIcon icon = new ImageIcon(createCardImage(card));
        return new JLabel(icon);
    }

    /**
     * 뒷면 카드 JLabel 반환
     */
    public static JLabel createBackLabel() {
        ImageIcon icon = new ImageIcon(createBackImage());
        return new JLabel(icon);
    }

    /**
     * 카드 앞면 이미지 생성
     */
    public static Image createCardImage(Card card) {
        // 그림자 포함 크기
        java.awt.image.BufferedImage img =
            new java.awt.image.BufferedImage(CARD_W + 4, CARD_H + 4,
                java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 그림자
        g.setColor(SHADOW);
        g.fill(new RoundRectangle2D.Float(3, 3, CARD_W, CARD_H, 10, 10));

        // 카드 배경
        g.setColor(CARD_BG);
        g.fill(new RoundRectangle2D.Float(0, 0, CARD_W, CARD_H, 10, 10));

        // 카드 테두리
        g.setColor(new Color(200, 195, 185));
        g.setStroke(new BasicStroke(1f));
        g.draw(new RoundRectangle2D.Float(0, 0, CARD_W - 1, CARD_H - 1, 10, 10));

        Color suitColor = isRed(card) ? RED_COLOR : DARK_COLOR;

        // 좌상단 숫자
        g.setColor(suitColor);
        g.setFont(new Font("Serif", Font.BOLD, 16));
        String rankStr = card.getRank().getSymbol();
        g.drawString(rankStr, 6, 18);

        // 좌상단 무늬
        g.setFont(new Font("Serif", Font.PLAIN, 13));
        g.drawString(card.getSuit().getSymbol(), 6, 32);

        // 중앙 큰 무늬
        g.setFont(new Font("Serif", Font.PLAIN, 38));
        FontMetrics fm = g.getFontMetrics();
        String suitSymbol = card.getSuit().getSymbol();
        int sx = (CARD_W - fm.stringWidth(suitSymbol)) / 2;
        g.drawString(suitSymbol, sx, CARD_H / 2 + 14);

        // 우하단 (뒤집어서)
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.translate(CARD_W, CARD_H);
        g2.rotate(Math.PI);
        g2.setColor(suitColor);
        g2.setFont(new Font("Serif", Font.BOLD, 16));
        g2.drawString(rankStr, 6, 18);
        g2.setFont(new Font("Serif", Font.PLAIN, 13));
        g2.drawString(suitSymbol, 6, 32);
        g2.dispose();

        g.dispose();
        return img;
    }

    /**
     * 카드 뒷면 이미지 생성
     */
    public static Image createBackImage() {
        java.awt.image.BufferedImage img =
            new java.awt.image.BufferedImage(CARD_W + 4, CARD_H + 4,
                java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 그림자
        g.setColor(SHADOW);
        g.fill(new RoundRectangle2D.Float(3, 3, CARD_W, CARD_H, 10, 10));

        // 배경
        g.setColor(CARD_BACK);
        g.fill(new RoundRectangle2D.Float(0, 0, CARD_W, CARD_H, 10, 10));

        // 격자 패턴
        g.setColor(new Color(255, 255, 255, 25));
        g.setStroke(new BasicStroke(1f));
        for (int x = 0; x < CARD_W; x += 8) {
            g.drawLine(x, 0, x, CARD_H);
        }
        for (int y = 0; y < CARD_H; y += 8) {
            g.drawLine(0, y, CARD_W, y);
        }

        // 내부 테두리
        g.setColor(new Color(255, 255, 255, 60));
        g.setStroke(new BasicStroke(2f));
        g.draw(new RoundRectangle2D.Float(6, 6, CARD_W - 12, CARD_H - 12, 6, 6));

        // 중앙 무늬
        g.setFont(new Font("Serif", Font.BOLD, 28));
        g.setColor(new Color(255, 255, 255, 100));
        FontMetrics fm = g.getFontMetrics();
        String symbol = "★";
        g.drawString(symbol, (CARD_W - fm.stringWidth(symbol)) / 2, CARD_H / 2 + 10);

        // 테두리
        g.setColor(new Color(150, 180, 230));
        g.setStroke(new BasicStroke(1.5f));
        g.draw(new RoundRectangle2D.Float(0, 0, CARD_W - 1, CARD_H - 1, 10, 10));

        g.dispose();
        return img;
    }

    private static boolean isRed(Card card) {
        return card.getSuit() == Card.Suit.HEARTS || card.getSuit() == Card.Suit.DIAMONDS;
    }
}
