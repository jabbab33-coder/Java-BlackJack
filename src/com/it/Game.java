package com.it;

import java.util.Scanner;

/**
 * 블랙잭 게임 전체를 관리하는 클래스
 * 게임 흐름: 베팅 → 카드 배분 → 플레이어 턴 → 딜러 턴 → 결과 판정
 */
public class Game {

    private static final int INITIAL_CHIPS = 1000;

    private final Deck   deck;
    private final Player player;
    private final Dealer dealer;
    private final Scanner scanner;

    public Game() {
        deck    = new Deck();
        player  = new Player("플레이어", INITIAL_CHIPS);
        dealer  = new Dealer();
        scanner = new Scanner(System.in);
    }

    /**
     * 게임 시작
     */
    public void start() {
        printTitle();
        deck.shuffle();

        while (true) {
            // 칩이 없으면 게임 종료
            if (player.getChips() <= 0) {
                System.out.println("\n💸 칩이 모두 소진되었습니다. 게임을 종료합니다.");
                break;
            }

            playRound();

            System.out.println("\n계속 플레이하시겠습니까? (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (!input.equals("y")) {
                System.out.println("\n🎩 게임을 종료합니다. 최종 칩: " + player.getChips());
                break;
            }
        }

        scanner.close();
    }

    /**
     * 라운드 1회 진행
     */
    private void playRound() {
        printSeparator("새 라운드");
        player.clearHand();
        dealer.clearHand();

        // 덱 카드 부족 시 리셋
        if (deck.remainingCards() < 10) {
            System.out.println("🔄 덱을 리셋합니다.");
            deck.reset();
        }

        // 1단계: 베팅
        if (!placeBet()) return;

        // 2단계: 초기 카드 배분 (각 2장)
        dealInitialCards();

        // 3단계: 블랙잭 즉시 체크
        if (checkBlackjack()) return;

        // 4단계: 플레이어 턴
        playerTurn();

        // 플레이어 버스트 시 딜러 턴 생략
        if (!player.isBust()) {
            // 5단계: 딜러 턴
            dealer.playTurn(deck);
        }

        // 6단계: 결과 판정
        determineResult();
    }

    /**
     * 베팅 입력
     */
    private boolean placeBet() {
        System.out.println("\n💰 보유 칩: " + player.getChips());
        System.out.print("베팅 금액을 입력하세요: ");

        int amount;
        try {
            amount = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("❌ 올바른 숫자를 입력하세요.");
            return false;
        }

        return player.placeBet(amount);
    }

    /**
     * 초기 카드 배분 (플레이어 2장, 딜러 2장)
     */
    private void dealInitialCards() {
        printSeparator("카드 배분");

        player.addCard(deck.drawCard());
        dealer.addCard(deck.drawCard());
        player.addCard(deck.drawCard());
        dealer.addCard(deck.drawCard());

        // 딜러는 첫 장만 공개
        dealer.showFirstCard();
        player.showHand();
    }

    /**
     * 블랙잭 즉시 체크
     * @return 블랙잭으로 라운드 종료 시 true
     */
    private boolean checkBlackjack() {
        boolean playerBJ = player.isBlackjack();
        boolean dealerBJ = dealer.isBlackjack();

        if (!playerBJ && !dealerBJ) return false;

        printSeparator("블랙잭 체크");
        dealer.showHand();
        player.showHand();

        if (playerBJ && dealerBJ) {
            System.out.println("🤝 둘 다 블랙잭! 무승부(Push)");
            player.pushBet();
        } else if (playerBJ) {
            System.out.println("🎉 플레이어 블랙잭! 1.5배 지급!");
            player.winBlackjack();
        } else {
            System.out.println("😞 딜러 블랙잭! 패배");
            player.loseBet();
        }

        printChips();
        return true;
    }

    /**
     * 플레이어 턴 (히트/스탠드 선택)
     */
    private void playerTurn() {
        printSeparator("플레이어 턴");

        while (!player.isBust() && !player.isStanding()) {
            player.showHand();
            System.out.print("행동을 선택하세요 (h: 히트 / s: 스탠드): ");
            String action = scanner.nextLine().trim().toLowerCase();

            switch (action) {
                case "h":
                    Card drawn = deck.drawCard();
                    player.addCard(drawn);
                    System.out.println("카드를 받았습니다: " + drawn);
                    break;

                case "s":
                    player.stand();
                    System.out.println("스탠드를 선택했습니다.");
                    break;

                default:
                    System.out.println("❌ h(히트) 또는 s(스탠드)를 입력하세요.");
            }
        }

        player.showHand();
    }

    /**
     * 최종 결과 판정
     */
    private void determineResult() {
        printSeparator("결과");

        dealer.showHand();
        player.showHand();

        int playerScore = player.getScore();
        int dealerScore = dealer.getScore();

        if (player.isBust()) {
            System.out.println("💥 버스트! 패배");
            player.loseBet();

        } else if (dealer.isBust()) {
            System.out.println("🎉 딜러 버스트! 승리!");
            player.winBet();

        } else if (playerScore > dealerScore) {
            System.out.println("🎉 승리! (" + playerScore + " vs " + dealerScore + ")");
            player.winBet();

        } else if (playerScore < dealerScore) {
            System.out.println("😞 패배 (" + playerScore + " vs " + dealerScore + ")");
            player.loseBet();

        } else {
            System.out.println("🤝 무승부(Push) (" + playerScore + " vs " + dealerScore + ")");
            player.pushBet();
        }

        printChips();
    }

    // ─── 출력 헬퍼 ──────────────────────────────────────────

    private void printTitle() {
        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║      ♠ ♥  블랙잭 게임  ♦ ♣       ║");
        System.out.println("╚══════════════════════════════════╝");
        System.out.println("초기 지급 칩: " + INITIAL_CHIPS);
    }

    private void printSeparator(String title) {
        System.out.println("\n── " + title + " " + "─".repeat(Math.max(0, 28 - title.length())));
    }

    private void printChips() {
        System.out.println("💰 현재 보유 칩: " + player.getChips());
    }

    /**
     * 프로그램 진입점
     */
    public static void main(String[] args) {
        new Game().start();
    }
}
