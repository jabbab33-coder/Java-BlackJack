package UI;

/**
 * 게임 로직만 담당하는 클래스 (UI 독립)
 * Scanner/System.out 없이 순수 상태 관리만 수행
 */
public class GameLogic {

    public enum GameState {
        BETTING,        // 베팅 대기
        PLAYER_TURN,    // 플레이어 턴
        DEALER_TURN,    // 딜러 턴
        ROUND_END       // 라운드 종료
    }

    public enum RoundResult {
        PLAYER_BLACKJACK,   // 플레이어 블랙잭
        DEALER_BLACKJACK,   // 딜러 블랙잭
        BOTH_BLACKJACK,     // 둘 다 블랙잭 (무승부)
        PLAYER_BUST,        // 플레이어 버스트
        DEALER_BUST,        // 딜러 버스트
        PLAYER_WIN,         // 플레이어 승
        DEALER_WIN,         // 딜러 승
        PUSH                // 무승부
    }

    private final Deck   deck;
    private final Player player;
    private final Dealer dealer;
    private GameState    state;
    private RoundResult  lastResult;

    public GameLogic(int initialChips) {
        deck   = new Deck();
        player = new Player("플레이어", initialChips);
        dealer = new Dealer();
        state  = GameState.BETTING;
        deck.shuffle();
    }

    // ─── 베팅 단계 ─────────────────────────────────────────

    /**
     * 베팅 처리
     * @return 성공 여부
     */
    public boolean placeBet(int amount) {
        if (state != GameState.BETTING) return false;
        return player.placeBet(amount);
    }

    // ─── 카드 배분 ─────────────────────────────────────────

    /**
     * 초기 카드 2장씩 배분 후 블랙잭 체크
     */
    public void dealInitialCards() {
        player.clearHand();
        dealer.clearHand();

        if (deck.remainingCards() < 10) deck.reset();

        player.addCard(deck.drawCard());
        dealer.addCard(deck.drawCard());
        player.addCard(deck.drawCard());
        dealer.addCard(deck.drawCard());

        // 블랙잭 즉시 체크
        if (player.isBlackjack() || dealer.isBlackjack()) {
            resolveBlackjack();
        } else {
            state = GameState.PLAYER_TURN;
        }
    }

    private void resolveBlackjack() {
        state = GameState.ROUND_END;
        if (player.isBlackjack() && dealer.isBlackjack()) {
            lastResult = RoundResult.BOTH_BLACKJACK;
            player.pushBet();
        } else if (player.isBlackjack()) {
            lastResult = RoundResult.PLAYER_BLACKJACK;
            player.winBlackjack();
        } else {
            lastResult = RoundResult.DEALER_BLACKJACK;
            player.loseBet();
        }
    }

    // ─── 플레이어 턴 ───────────────────────────────────────

    /**
     * 히트: 카드 한 장 추가
     */
    public void hit() {
        if (state != GameState.PLAYER_TURN) return;
        player.addCard(deck.drawCard());
        if (player.isBust()) {
            lastResult = RoundResult.PLAYER_BUST;
            player.loseBet();
            state = GameState.ROUND_END;
        }
    }

    /**
     * 스탠드: 딜러 턴으로 넘어감
     */
    public void stand() {
        if (state != GameState.PLAYER_TURN) return;
        player.stand();
        state = GameState.DEALER_TURN;
        playDealerTurn();
    }

    // ─── 딜러 턴 ───────────────────────────────────────────

    /**
     * 딜러 자동 플레이 후 결과 판정
     */
    private void playDealerTurn() {
        while (dealer.shouldHit()) {
            dealer.addCard(deck.drawCard());
        }
        determineResult();
        state = GameState.ROUND_END;
    }

    private void determineResult() {
        int ps = player.getScore();
        int ds = dealer.getScore();

        if (dealer.isBust()) {
            lastResult = RoundResult.DEALER_BUST;
            player.winBet();
        } else if (ps > ds) {
            lastResult = RoundResult.PLAYER_WIN;
            player.winBet();
        } else if (ps < ds) {
            lastResult = RoundResult.DEALER_WIN;
            player.loseBet();
        } else {
            lastResult = RoundResult.PUSH;
            player.pushBet();
        }
    }

    // ─── 다음 라운드 준비 ──────────────────────────────────

    public void nextRound() {
        player.clearHand();
        dealer.clearHand();
        lastResult = null;
        state = GameState.BETTING;
    }

    // ─── Getters ───────────────────────────────────────────

    public Player      getPlayer()     { return player; }
    public Dealer      getDealer()     { return dealer; }
    public GameState   getState()      { return state; }
    public RoundResult getLastResult() { return lastResult; }
    public boolean     isGameOver()    { return player.getChips() <= 0; }
}