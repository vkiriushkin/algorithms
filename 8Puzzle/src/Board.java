import java.util.ArrayList;
import java.util.List;

public class Board {

    private int[][] blocks;

    // construct a board from an N-by-N array of blocks
    // (where blocks[i][j] = block in row i, column j)
    public Board(int[][] blocks) {
        this.blocks = new int[blocks[0].length][blocks[0].length];
        for (int i = 0; i < dimension(); i++)
            for (int j = 0; j < dimension(); j++) 
                this.blocks[i][j] = blocks[i][j];
    }
    
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] blocks = new int[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);
        System.out.println(initial.manhattan());
        blocks[0][0] = 1;
        blocks[0][1] = 0;
        System.out.println(initial.manhattan());
        
        
        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
        StdOut.print(initial);
    }
    
    // board dimension N                                   
    public int dimension() {
        return blocks[0].length;
    }

    // number of blocks out of place
    public int hamming() {
        int hammingCount = 0;
        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j < dimension(); j++) {
                //check if it's last cell
                if (blocks[i][j] == 0) {
                    continue;
                } else {
                    if (blocks[i][j] != (j + i * dimension() + 1)) {
                        hammingCount++;
                    }
                }

            }
        }
        return hammingCount;
    }

    // sum of Manhattan distances between blocks and goal
    public int manhattan() {
        int manhattanCount = 0;
        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j < dimension(); j++) {
                //check if cell value is 0
                if (blocks[i][j] == 0) {
                    continue;
                } else {
                    //check if cell is at right place
                    if (blocks[i][j] == (j + i * dimension() + 1)) {
                        continue;
                    } else {
                        int expJ = (blocks[i][j] % dimension()) - 1;
                        int expI = blocks[i][j] / dimension();
                        if (expI > 0 && expJ == -1) {
                            expJ += dimension();
                            expI -= 1;
                        }
                        manhattanCount += (Math.abs(j - expJ) + Math.abs(i - expI));
                    }
                }
            }
        }
        return manhattanCount;
    }

    // is this board the goal board?
    public boolean isGoal() {
        if (this == null) {
            return false;
        }
        return hamming() == 0;
    }

    // a board obtained by exchanging two adjacent blocks in the same row
    public Board twin() {
        if (dimension() > 1) {
            int[][] blocksCopy = new int[dimension()][dimension()];
            for (int i = 0; i < dimension(); i++)
                for (int j = 0; j < dimension(); j++)
                    blocksCopy[i][j] = blocks[i][j];
            outer:
            for (int i = 0; i < dimension(); i++)
                for (int j = 0; j < dimension(); j++) {
                    if (j != dimension()-1 && blocksCopy[i][j] != 0 && blocksCopy[i][j+1] !=0) {
                        int a = blocksCopy[i][j];
                        blocksCopy[i][j] = blocksCopy[i][j+1];
                        blocksCopy[i][j+1] = a;
                        break outer;
                    }
                }
                    
            return new Board(blocksCopy);
        }
        
        return null;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (this == y) return true;
        if (y == null) return false;
        if (this.getClass() != y.getClass()) return false;
        
        Board toCompare = (Board) y;
        if (this.dimension() != toCompare.dimension()) {
            return false;
        }
        
        return this.toString().equals(toCompare.toString());
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        List<Board> neighbors = new ArrayList<Board>();
        int[][] blocksCopyBottom = new int[dimension()][dimension()];
        int[][] blocksCopyTop = new int[dimension()][dimension()];
        int[][] blocksCopyLeft = new int[dimension()][dimension()];
        int[][] blocksCopyRight = new int[dimension()][dimension()];
        for (int i = 0; i < dimension(); i++)
            for (int j = 0; j < dimension(); j++) {
                blocksCopyBottom[i][j] = blocks[i][j];
                blocksCopyTop[i][j] = blocks[i][j];
                blocksCopyLeft[i][j] = blocks[i][j];
                blocksCopyRight[i][j] = blocks[i][j];
            }
        
        outer:
        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j < dimension(); j++) {
                //check if cell value is 0
                if (blocksCopyBottom[i][j] == 0) {
                    boolean topLeftCorner = (i == 0 && j == 0);
                    boolean top = (i == 0 && j >0 && j < dimension()-1);
                    boolean topRightCorner = (i == 0 && j == dimension()-1);
                    boolean bottomLeftCorner = (i == dimension()-1 && j == 0);
                    boolean bottom = (i == dimension()-1 && j > 0 && j < dimension()-1);
                    boolean bottomRightCorner = (i == dimension()-1 && j == dimension()-1);
                    boolean left = (i >0 && i < dimension()-1 && j == 0);
                    boolean right = (i > 0 && i < dimension()-1 && j == dimension()-1);
                    boolean middle = (i >0 && i < dimension()-1 && j > 0 && j < dimension()-1);
                    
                    if (topLeftCorner || topRightCorner || top || left || right || middle) {
                        //neighbor from bottom
                        int b = blocksCopyBottom[i+1][j];
                        blocksCopyBottom[i+1][j] = blocks[i][j];
                        blocksCopyBottom[i][j] = b;
                        neighbors.add(new Board(blocksCopyBottom));
                        
                    } 
                    if(topLeftCorner || top || left || bottomLeftCorner || bottom || middle) {
                        //neighbor from right
                        int a = blocksCopyRight[i][j+1];
                        blocksCopyRight[i][j+1] = blocks[i][j];
                        blocksCopyRight[i][j] = a;
                        neighbors.add(new Board(blocksCopyRight));
                    }
                    if(top || topRightCorner || right || bottomRightCorner || bottom || middle) {
                        //neighbor from left
                        int a = blocksCopyLeft[i][j-1];
                        blocksCopyLeft[i][j-1] = blocks[i][j];
                        blocksCopyLeft[i][j] = a;
                        neighbors.add(new Board(blocksCopyLeft));
                    }
                    if(left || right || bottomLeftCorner || bottomRightCorner || bottom || middle) {
                        //neighbor from top
                        int a = blocksCopyTop[i-1][j];
                        blocksCopyTop[i-1][j] = blocks[i][j];
                        blocksCopyTop[i][j] = a;
                        neighbors.add(new Board(blocksCopyTop));
                    }
                    break outer;
                }
            }
        }
        return neighbors;
    }

    // string representation of the board (in the output format specified below)
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append(dimension());
        for (int i = 0; i < dimension(); i++) {
            string.append("\n");
            for (int j = 0; j < dimension(); j++) {
                string.append(blocks[i][j]+" ");
            }
        }
        
        return string.toString();
        
    }
}