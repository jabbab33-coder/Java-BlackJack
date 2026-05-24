package UI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 카드 덱(52장)을 나타내는 클래스
 * 카드 생성, 셔플, 드로우 기능 포함
 */
public class Deck {

    private List<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        initializeDeck();
    }

    /**
     * 52장의 카드를 생성하여 덱 초기화
     */
    private void initializeDeck() {
        cards.clear();
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
    }

    /**
     * 덱을 무작위로 섞기
     */
    public void shuffle() {
        Collections.shuffle(cards);
        System.out.println("🔀 카드를 섞었습니다.");
    }

    /**
     * 덱에서 카드 한 장 뽑기
     * @return 뽑힌 카드, 덱이 비어있으면 null
     */
    public Card drawCard() {
        if (cards.isEmpty()) {
            System.out.println("⚠️  덱에 카드가 없습니다. 덱을 재초기화합니다.");
            initializeDeck();
            shuffle();
        }
        return cards.remove(cards.size() - 1);
    }

    /**
     * 덱에 남은 카드 수 반환
     */
    public int remainingCards() {
        return cards.size();
    }

    /**
     * 덱이 비어있는지 확인
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * 덱 초기화 및 셔플 (게임 재시작 시 사용)
     */
    public void reset() {
        initializeDeck();
        shuffle();
    }

    @Override
    public String toString() {
        return "덱 남은 카드: " + remainingCards() + "장";
    }
}
