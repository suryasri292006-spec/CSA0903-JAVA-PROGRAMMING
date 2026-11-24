
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;

// Team class
class Team {

    final String name;
    final int points;
    final int goalDiff;

    Team(String name, int points, int goalDiff) {
        this.name = name;
        this.points = points;
        this.goalDiff = goalDiff;
    }

    @Override
    public String toString() {
        return name + "(" + points + "," + goalDiff + ")";
    }
}

// Generic Leaderboard class
class Leaderboard<T> {

    private final List<T> items = new ArrayList<>();

    private final Comparator<? super T> order;

    Leaderboard(Comparator<? super T> order) {
        this.order = order;
    }

    // Add one item
    void add(T item) {
        items.add(item);
    }

    // Add multiple items using wildcard
    void addAll(List<? extends T> newItems) {
        items.addAll(newItems);
    }

    // TODO 1: Return sorted copy without modifying original list
    List<T> ranking() {

        List<T> sorted = new ArrayList<>(items);

        sorted.sort(order);

        return sorted;
    }

    // Display original list
    List<T> originalList() {
        return new ArrayList<>(items);
    }
}

// Player class for reusability demonstration
class Player {

    String name;
    int score;

    Player(String name, int score) {
        this.name = name;
        this.score = score;
    }

    @Override
    public String toString() {
        return name + "(" + score + ")";
    }
}

// Main class
public class Main {

    public static void main(String[] args) {

        System.out.println("SPORTS LEADERBOARD SYSTEM");
        System.out.println("====================================");

        // TODO 2: Correct comparator
        Comparator<Team> byRank =
                Comparator.comparingInt((Team t) -> t.points)
                .reversed()
                .thenComparing(
                        Comparator.comparingInt(
                                (Team t) -> t.goalDiff
                        ).reversed()
                )
                .thenComparing(t -> t.name);

        // TEST 1: Normal ranking with ties
        System.out.println("\nTEST 1: CORRECT TEAM RANKING");

        Leaderboard<Team> board =
                new Leaderboard<>(byRank);

        board.add(new Team("Panthers", 9, 4));
        board.add(new Team("Eagles", 9, 6));
        board.add(new Team("Cobras", 6, 1));
        board.add(new Team("Bisons", 9, 4));

        System.out.println("Original list:");
        System.out.println(board.originalList());

        System.out.println("Ranked list:");
        System.out.println(board.ranking());

        // TEST 2: Several ties at every level
        System.out.println("\nTEST 2: MULTIPLE TIES");

        Leaderboard<Team> board2 =
                new Leaderboard<>(byRank);

        board2.add(new Team("Zebras", 12, 5));
        board2.add(new Team("Lions", 12, 5));
        board2.add(new Team("Tigers", 12, 8));
        board2.add(new Team("Bears", 12, 8));
        board2.add(new Team("Wolves", 8, 3));
        board2.add(new Team("Foxes", 8, 3));

        System.out.println("Ranked list:");
        System.out.println(board2.ranking());

        // TEST 3: Deliberately faulty comparator
        System.out.println("\nTEST 3: FAULTY COMPARATOR");

        Comparator<Team> faultyOrder =
                Comparator.comparingInt((Team t) -> t.points)
                .reversed()
                .thenComparing(
                        Comparator.comparingInt(
                                (Team t) -> t.goalDiff
                        ).reversed()
                );

        Leaderboard<Team> faultyBoard =
                new Leaderboard<>(faultyOrder);

        faultyBoard.add(new Team("Zebras", 12, 5));
        faultyBoard.add(new Team("Lions", 12, 5));
        faultyBoard.add(new Team("Tigers", 12, 8));
        faultyBoard.add(new Team("Bears", 12, 8));

        System.out.println("Faulty ranking:");
        System.out.println(faultyBoard.ranking());

        System.out.println("Correct ranking:");
        System.out.println(
                new Leaderboard<Team>(byRank) {{
                    add(new Team("Zebras", 12, 5));
                    add(new Team("Lions", 12, 5));
                    add(new Team("Tigers", 12, 8));
                    add(new Team("Bears", 12, 8));
                }}.ranking()
        );

        // TEST 4: Generic leaderboard with Players
        System.out.println("\nTEST 4: PLAYER RANKING");

        Comparator<Player> playerOrder =
                Comparator.comparingInt((Player p) -> p.score)
                .reversed()
                .thenComparing(p -> p.name);

        Leaderboard<Player> playerBoard =
                new Leaderboard<>(playerOrder);

        playerBoard.add(new Player("Arun", 90));
        playerBoard.add(new Player("Divya", 95));
        playerBoard.add(new Player("Kumar", 85));
        playerBoard.add(new Player("Bala", 95));

        System.out.println("Player ranking:");
        System.out.println(playerBoard.ranking());

        // TEST 5: Wildcard addAll
        System.out.println("\nTEST 5: ADD MULTIPLE TEAMS");

        List<Team> extraTeams = new ArrayList<>();

        extraTeams.add(new Team("Falcons", 15, 7));
        extraTeams.add(new Team("Hawks", 10, 3));

        board.addAll(extraTeams);

        System.out.println("Updated ranking:");
        System.out.println(board.ranking());

        System.out.println("\n====================================");
        System.out.println("PROGRAM EXECUTED SUCCESSFULLY");
    }
}
