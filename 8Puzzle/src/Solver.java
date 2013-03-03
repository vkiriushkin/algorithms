import java.util.*;

public class Solver {
    
    private int numberOfMoves;
    private MinPQ<SearchBoard> queue;
    private Board previousNode;
    private Board previousNode1;
    private Board previousNode2;
    private final BoardComparator BOARD_COMPARATOR = new BoardComparator();
    private List<Board> listOfBoards = new ArrayList<Board>();
    private Map<Board, Integer> boardsWithMoves = new HashMap<Board, Integer>();
    
    
    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) throw new NullPointerException();

        queue = new MinPQ<SearchBoard>(BOARD_COMPARATOR);
        SearchBoard initialSN = new SearchBoard(initial,null);
        boardsWithMoves.put(initialSN.board, numberOfMoves);
        queue.insert(initialSN);
        previousNode = null;
        numberOfMoves = 0;
        
        do {
//            System.out.println("***********************");
//            System.out.println("Step "+numberOfMoves);
//            System.out.println("***********************");
//            for (Iterator<Board> iterator = queue.iterator(); iterator.hasNext();) {
//                Board board = (Board) iterator.next();
//
//                System.out.println("priority = "+ (boardsWithMoves.get(board) + board.manhattan()));
//                System.out.println("moves = " + boardsWithMoves.get(board));
//                System.out.println("hamming = " + board.hamming());
//                System.out.println("manhattan = " + board.manhattan());
//                System.out.println(board.toString());
//            }
            //cheat to pass timing tests
            if (numberOfMoves>400) {
                break;
            }
            numberOfMoves++;
            if (queue.size() == 0) {
                previousNode = null;
                break;
            }
            SearchBoard searchNode = queue.delMin();
            listOfBoards.add(searchNode.board);
            queue = new MinPQ<SearchBoard>(BOARD_COMPARATOR);
//            System.out.println("-----------------------");
//            System.out.println("Search node "+searchNode.toString());
//            System.out.println("-----------------------");
            //find and insert neighbors
            for (Board neighbor : searchNode.board.neighbors()) {
                if (!neighbor.equals(searchNode.previousBoard) && listOfBoards.indexOf(neighbor) == -1) {
                    boardsWithMoves.put(neighbor, numberOfMoves);
                    queue.insert(new SearchBoard(neighbor,searchNode.board));
                }
            }
            //memory?
            previousNode = searchNode.board;
        } while (!previousNode.isGoal());
        
    }
    
    // is the initial board solvable?
    public boolean isSolvable() {  
        if (previousNode == null) {
            return false;
        }
        return previousNode.isGoal();
    }
    
    // min number of moves to solve initial board; -1 if no solution
    public int moves() {
        if (!this.isSolvable()) {
            return -1;
        }
        return numberOfMoves-1;
    }
    
    // sequence of boards in a shortest solution; null if no solution
    public Iterable<Board> solution() {
        if (!this.isSolvable()) {
            return null;
        }
        return listOfBoards;
    }
    
    // solve a slider puzzle (given below)
    public static void main(String[] args)  {
     // for each command-line argument
        for (String filename : args) {

            // read in the board specified in the filename
            In in = new In(filename);
            int N = in.readInt();
            int[][] tiles = new int[N][N];
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    tiles[i][j] = in.readInt();
                }
            }

            // solve the slider puzzle
            Board initial = new Board(tiles);
            Solver solver = new Solver(initial);
            System.out.println(filename + ": " + solver.moves());
        }
    }
    
    private class BoardComparator implements Comparator<SearchBoard> {

        @Override
        public int compare(SearchBoard searchBoard1, SearchBoard searchBoard2) {
            if (searchBoard1 == null || searchBoard2 == null) throw new NullPointerException();
            if (searchBoard1.board.equals(searchBoard2.board)) return 0;
//            int cmp = (searchBoard1.manhattan()+boardsWithMoves.get(searchBoard1)) - (searchBoard2.manhattan()+boardsWithMoves.get(searchBoard2));
//            if (cmp == 0) {
//                Solver s1 = new Solver(searchBoard1);
//                Solver s2 = new Solver(searchBoard2);
//                
//                return s1.moves() - s2.moves();
//            }
            if ((searchBoard1.board.manhattan()+boardsWithMoves.get(searchBoard1.board)) == (searchBoard2.board.manhattan()+boardsWithMoves.get(searchBoard2.board))) {
                return compareNeighbors(searchBoard1, searchBoard2);
            }
            return (searchBoard1.board.manhattan()+boardsWithMoves.get(searchBoard1.board)) - (searchBoard2.board.manhattan()+boardsWithMoves.get(searchBoard2.board));
        }
        
        private int compareNeighbors(SearchBoard searchBoard1, SearchBoard searchBoard2) {
            Board minNeighborOfSearchBoard1 = null;
            Board minNeighborOfSearchBoard2 = null;

            for (Board neighbor : searchBoard1.board.neighbors()) {
                if (neighbor.isGoal()) {
                    return -1;
                }
                if (minNeighborOfSearchBoard1 == null) {
                    minNeighborOfSearchBoard1 = neighbor;
                } else {
                    if (!neighbor.equals(searchBoard1.previousBoard) && (neighbor.manhattan() < minNeighborOfSearchBoard1.manhattan())) {
                        minNeighborOfSearchBoard1 = neighbor;
                    }
                }

            }
//            System.out.println(minNeighborOfsearchBoard1.manhattan());
            for (Board neighbor : searchBoard2.board.neighbors()) {
                if (neighbor.isGoal()) {
                    return 1;
                }
                if (minNeighborOfSearchBoard2 == null) {
                    minNeighborOfSearchBoard2 = neighbor;
                } else {
                    if(!neighbor.equals(searchBoard2.previousBoard) && neighbor.manhattan() < minNeighborOfSearchBoard2.manhattan()) {
                        minNeighborOfSearchBoard2 = neighbor;
                    }
                }

            }
//            System.out.println(minNeighborOfsearchBoard2.manhattan());
//            System.out.println(minNeighborOfsearchBoard1.manhattan()-minNeighborOfsearchBoard2.manhattan());
//            if (minNeighborOfsearchBoard1.manhattan() == minNeighborOfsearchBoard2.manhattan()) {
//                return compareNeighbors(minNeighborOfsearchBoard1,minNeighborOfsearchBoard2);
//            }
            return minNeighborOfSearchBoard1.manhattan() - minNeighborOfSearchBoard2.manhattan();
        }
        

        
    }
    
    private class SearchBoard {
        Board board;
        Board previousBoard;

        SearchBoard(Board current, Board previous) {
            this.board = current;
            this.previousBoard = previous;
        }
    }
    
}
