package UI;

/**
 * 카드 한 장을 나타내는 클래스
 * 무늬(suit)와 숫자(rank)로 구성됨
 */
public class Card {

    // 카드 무늬 열거형
    public enum Suit {
        SPADES("♠"), HEARTS("♥"), DIAMONDS("♦"), CLUBS("♣");

        private final String symbol;

        Suit(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }
    }

    // 카드 숫자/문자 열거형
    public enum Rank {
        TWO("2", 2), THREE("3", 3), FOUR("4", 4), FIVE("5", 5),
        SIX("6", 6), SEVEN("7", 7), EIGHT("8", 8), NINE("9", 9),
        TEN("10", 10), JACK("J", 10), QUEEN("Q", 10), KING("K", 10),
        ACE("A", 11); // ACE는 기본 11, 필요 시 1로 처리

        private final String symbol;
        private final int value;

        Rank(String symbol, int value) {
            this.symbol = symbol;
            this.value = value;
        }

        public String getSymbol() {
            return symbol;
        }

        public int getValue() {
            return value;
        }
    }

    private final Suit suit;
    private final Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    public int getValue() {
        return rank.getValue();
    }

    @Override
    public String toString() {
        return "[" + suit.getSymbol() + rank.getSymbol() + "]";
    }
}
