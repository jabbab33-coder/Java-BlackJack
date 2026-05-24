package UI;

import java.util.ArrayList;
import java.util.List;

/**
 * 플레이어를 나타내는 클래스
 * 손패 관리, 점수 계산, 베팅 기능 포함
 */
public class Player {

    private final String name;
    private List<Card> hand;
    private int chips;       // 보유 칩
    private int bet;         // 현재 베팅금액
    private boolean standing; // 스탠드 여부

    public Player(String name, int initialChips) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.chips = initialChips;
        this.bet = 0;
        this.standing = false;
    }

    /**
     * 손패에 카드 추가 (히트)
     */
    public void addCard(Card card) {
        hand.add(card);
    }

    /**
     * 손패 점수 계산
     * ACE는 합계가 21을 초과하면 1로 계산
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

        // 합계가 21 초과 시 ACE를 11 → 1로 변환
        while (score > 21 && aceCount > 0) {
            score -= 10;
            aceCount--;
        }

        return score;
    }

    /**
     * 블랙잭 여부 확인 (첫 2장이 ACE + 10점짜리 카드)
     */
    public boolean isBlackjack() {
        return hand.size() == 2 && getScore() == 21;
    }

    /**
     * 버스트(21 초과) 여부 확인
     */
    public boolean isBust() {
        return getScore() > 21;
    }

    /**
     * 베팅
     * @param amount 베팅할 금액
     * @return 베팅 성공 여부
     */
    public boolean placeBet(int amount) {
        if (amount <= 0) {
            System.out.println("❌ 베팅 금액은 1 이상이어야 합니다.");
            return false;
        }
        if (amount > chips) {
            System.out.println("❌ 보유 칩이 부족합니다. 보유 칩: " + chips);
            return false;
        }
        this.bet = amount;
        this.chips -= amount;
        return true;
    }

    /**
     * 베팅 금액 반환 (패배 시)
     */
    public void loseBet() {
        // 이미 chips에서 차감되었으므로 추가 처리 없음
        bet = 0;
    }

    /**
     * 베팅 금액의 2배 지급 (승리 시)
     */
    public void winBet() {
        chips += bet * 2;
        bet = 0;
    }

    /**
     * 베팅 금액 환불 (무승부 시)
     */
    public void pushBet() {
        chips += bet;
        bet = 0;
    }

    /**
     * 블랙잭 승리 시 1.5배 지급 (베팅액 + 1.5배)
     */
    public void winBlackjack() {
        chips += bet + (int)(bet * 1.5);
        bet = 0;
    }

    /**
     * 손패 초기화 (다음 라운드 시작 시)
     */
    public void clearHand() {
        hand.clear();
        standing = false;
        bet = 0;
    }

    /**
     * 스탠드 선언
     */
    public void stand() {
        this.standing = true;
    }

    /**
     * 손패 출력
     */
    public void showHand() {
        System.out.print(name + "의 패 " + hand + " → 합계: " + getScore());
        if (isBlackjack()) System.out.print(" 🎰 블랙잭!");
        if (isBust())      System.out.print(" 💥 버스트!");
        System.out.println();
    }

    // ─── Getters ───────────────────────────────────────────
    public String getName()      { return name; }
    public List<Card> getHand()  { return hand; }
    public int getChips()        { return chips; }
    public int getBet()          { return bet; }
    public boolean isStanding()  { return standing; }

    @Override
    public String toString() {
        return name + " | 칩: " + chips + " | 베팅: " + bet;
    }
}