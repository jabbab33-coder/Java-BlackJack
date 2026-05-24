package com.it;

import java.util.ArrayList;
import java.util.List;

/**
 * 딜러를 나타내는 클래스
 * 딜러 규칙: 합계 16 이하면 반드시 히트, 17 이상이면 스탠드
 */
public class Dealer {

    private static final int HIT_THRESHOLD = 17; // 딜러 히트 기준

    private List<Card> hand;

    public Dealer() {
        this.hand = new ArrayList<>();
    }

    /**
     * 손패에 카드 추가
     */
    public void addCard(Card card) {
        hand.add(card);
    }

    /**
     * 손패 점수 계산 (ACE 유연 처리)
     */
    public int getScore() {
        int score = 0;
        int aceCount = 0;

        for (Card card : hand) {
            score += card.getValue();
            if (card.getRank() == Card.Rank.ACE) {
                aceCount++;
            }
        }

        while (score > 21 && aceCount > 0) {
            score -= 10;
            aceCount--;
        }

        return score;
    }

    /**
     * 딜러가 카드를 더 받아야 하는지 판단
     * 합계 16 이하면 히트
     */
    public boolean shouldHit() {
        return getScore() < HIT_THRESHOLD;
    }

    /**
     * 블랙잭 여부 확인
     */
    public boolean isBlackjack() {
        return hand.size() == 2 && getScore() == 21;
    }

    /**
     * 버스트 여부 확인
     */
    public boolean isBust() {
        return getScore() > 21;
    }

    /**
     * 손패 초기화
     */
    public void clearHand() {
        hand.clear();
    }

    /**
     * 첫 번째 카드만 공개 (게임 시작 시 한 장은 숨김)
     */
    public void showFirstCard() {
        if (!hand.isEmpty()) {
            System.out.println("딜러의 패 [" + hand.get(0) + "] [??]");
        }
    }

    /**
     * 전체 패 공개
     */
    public void showHand() {
        System.out.print("딜러의 패 " + hand + " → 합계: " + getScore());
        if (isBlackjack()) System.out.print(" 🎰 블랙잭!");
        if (isBust())      System.out.print(" 💥 버스트!");
        System.out.println();
    }

    /**
     * 딜러 자동 플레이 (17 이상까지 히트)
     */
    public void playTurn(Deck deck) {
        System.out.println("\n── 딜러 턴 ──────────────────────");
        showHand();

        while (shouldHit()) {
            System.out.println("딜러가 카드를 뽑습니다...");
            addCard(deck.drawCard());
            showHand();
        }

        if (!isBust()) {
            System.out.println("딜러 스탠드 (합계: " + getScore() + ")");
        }
    }

    // ─── Getters ───────────────────────────────────────────
    public List<Card> getHand() { return hand; }

    @Override
    public String toString() {
        return "딜러 | 패: " + hand.size() + "장";
    }
}
