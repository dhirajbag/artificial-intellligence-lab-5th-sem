/*
           Name: Dhiraj Bag
           Roll: 001911001033
           B.E. in Information Technology
           (3rd Year - 1st Semester)

           Sub: AI Lab Assignment 5: Implementing
            A Star Algorithm for Solving 8 Puzzle
            Problem
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

class Successor {

    public Successor() {

    }

    public List<BoardNode> successor(BoardNode node) {
        //successor function that takes a state and returns a list of possible states that can be reached

        List<BoardNode> list = new ArrayList<>();

        int row = node.getRowBlank();
        int col = node.getColBlank();

        //up

        if(row != 0) {  //uses information about the nature of 2d arrays to dictate the zero-tile's movemnt.
            BoardNode upNode = node.createChild(row-1, col);
            upNode.setDir(DIRECTIONS.UP);
            list.add(upNode);

        }

        //down

        if(row != 2) {
            BoardNode downNode = node.createChild(row+1, col);
            downNode.setDir(DIRECTIONS.DOWN);
            list.add(downNode);
        }

        //right

        if(col != 2) {
            BoardNode rightNode = node.createChild(row, col+1);
            rightNode.setDir(DIRECTIONS.RIGHT);
            list.add(rightNode);
        }

        //left
        if(col != 0) {
            BoardNode leftNode = node.createChild(row, col-1); // a child is created if the zero tile can move left.
            leftNode.setDir(DIRECTIONS.LEFT);
            list.add(leftNode);
        }

        return list;  // a list of children is returned.

    }
}

class PathActions {
    // this class provides an object that is used to trace back the path from the goal
    // it then prints the path
    List<BoardNode> path;
    Info info; //info object is used in order to print details about space and time

    public PathActions(BoardNode initialNode, BoardNode goalNode, Info inf) {  //the arguments are goalNode, info and intialNode so a path can be found.
        path = this.getPath(initialNode, goalNode);
        this.info = inf;
    }


    private List<BoardNode> getPath(BoardNode initialNode, BoardNode goalNode) {  //given a goalNode and initialNode this method uses node's parents to trace it's way back up
        BoardNode tempNode = goalNode;
        List<BoardNode> list = new ArrayList<>();

        while(!(tempNode.equals(initialNode))) {
            list.add(tempNode);
            tempNode = tempNode.getParent();

        }
        list.add(initialNode);
        return list;  // a list of the path is returned in reverse order
    }


    public void printPath() {   //this method enables us to print the path in correct order from start node to goal node with sufficient details.
        int size = path.size();

        for(int i= size-1;i>=0;i--) {
            System.out.println();
            System.out.println();
            System.out.println("Direction Moved: " + path.get(i).getDir());
            System.out.println("Depth: " + path.get(i).getDepth());
            System.out.println("Cost: " + path.get(i).getCost());
            System.out.println("MaxCost: " + path.get(i).getMaxCost());
            System.out.println();
            System.out.println("Current Node: \n");
            System.out.println(Arrays.deepToString(path.get(i).getMatrix()).replace("], ", "]\n").replace("[[", "[").replace("]]", "]"));
            System.out.println();
        }
        System.out.println("Time: " + info.getTime());
        System.out.println("Space: " + info.getSpace());
    }
}

class Info {
    //info class that is used in every search
    public Queue<BoardNode> queue; // this class has datastructures such as queue, stack, and priority Queue in order to keep track of time, space and which nodes are in the queue
    public Stack<BoardNode> stack;
    public PriorityQueue<BoardNode> pQueue;
    public int time;
    private int maxQueueSize;
    public HashMap<Integer,BoardNode> visited;


    public Info() {
        queue = new LinkedList<>();
        stack = new Stack<>();
        pQueue = new PriorityQueue<>();
        time = 0;
        maxQueueSize = 0;
        visited = new HashMap<>();



    }

    public void  makePQueue(Comparator<BoardNode> c) {   //creates a prioirty queue with a comparator as an argument to decidee the order in which the queue will organize elements
        pQueue = new PriorityQueue<>(c);
    }

    public void incTime() {  //timer method that begins timer
        time += 1;
    }

    public void pQueueSize() {  //behaves similar to queueSize() but for priority queue
        if(pQueue.size()>maxQueueSize) {
            maxQueueSize = pQueue.size();
        }
    }

    public int getTime() { //time is returned
        return time;
    }

    public int getSpace() {  //space is returned
        return maxQueueSize;
    }

}

class Heuristics {

    //heuristic class that creates both heuristics

    public Heuristics() {

    }

    //First Heuristic which tells us how many tiles are in an incorrect position

    public int numCorPos(BoardNode node) {
        int[][] goal = node.getGoalMatrix();
        int result = 0;
        int [][] state = node.getMatrix();
        for(int i=0; i<state.length; i++) {
            for(int j=0; j<state.length; j++) {
                if(goal[i][j]!=state[i][j]) {
                    result += 1;
                }
            }
        }
        return result;
    }

    public int manhattan(BoardNode node) {   //second heuristic which uses a goal state to help determined how far argument node tiles are from desired position
        int result = 0;
        int [][]state = node.getMatrix();
        for(int i=0; i<state.length; i++) {
            for(int j=0; j<state.length; j++) {
                int value = state[i][j];
                result += Math.abs(i - node.getRow(value)) + Math.abs(j - node.getCol(value));
            }
        }
        return result;
    }




}

class Astar implements Search {
    //Astar class that creates the Astar search
    private final BoardNode initialNode;
    private final int i;

    public Astar(BoardNode node, int i) {
        this.initialNode = node;
        this.i = i; // this int value helps determine which heuristic will be used
    }

    private static class f1Comparator implements Comparator<BoardNode>{  //comparator for tiles misplaced heuristic that will be used in Priority Queue

        Heuristics h = new Heuristics();

        public int compare(BoardNode a, BoardNode b) {
            return (a.getMaxCost() + h.numCorPos(a)) - (b.getMaxCost()+h.numCorPos(b));
        }
    }


    private static class f2Comparator implements Comparator<BoardNode>{			//comparator for manhattan heuristic and totalCost

        Heuristics h = new Heuristics();

        public int compare(BoardNode a, BoardNode b) {
            return (a.getMaxCost() + h.manhattan(a)) - (b.getMaxCost()+h.manhattan(b));
        }
    }

    public boolean search() {

        //Astar search which creates a priority queue which sorts according to h(n)
        Info info = new Info();
        if(this.i==1) {
            info.makePQueue(new f1Comparator());
        }
        else {
            info.makePQueue(new f2Comparator());
        }
        //making a priority queue with one of the heuristics determine the Comparator
        BoardNode node = initialNode;
        info.pQueue.add(node);

        while(!(info.pQueue.isEmpty())) {
            node = info.pQueue.poll();
            info.incTime();
            info.visited.put(node.hashCode(), node);
            if(node.isGoal()) {
                PathActions p = new PathActions(initialNode,node,info); // class that creates a path from goal to start Node if goal is reached.
                p.printPath(); // the path is then printed
                return true;
            }

            Successor s = new Successor(); // Successor class created to provide next possible moves from current node
            List<BoardNode> list = s.successor(node); // list of potential children

            for(BoardNode temp: list) {
                boolean ans = info.visited.containsKey(temp.hashCode()); //Uses temporary node's hashCode to check if it has been expanded or not.
                if(!ans) { //if it hasn't been expanded then we can now check if there is a node in the Priority Queue with a higher Cost
                    if(!(info.pQueue.contains(temp))){
                        info.pQueue.add(temp);
                        info.pQueueSize();
                    }


                }
            }
        }
        return false;
    }

}


interface Search {

    boolean search();
}

enum DIRECTIONS {
    LEFT,RIGHT,UP,DOWN
}

class BoardNode {

    private final int[][]  state;
    private final int[][]  goal;
    private final List<BoardNode> children;
    private BoardNode parent;
    private int depth;
    private int blank_row;
    private int blank_col;
    private DIRECTIONS direction;
    private final String stringState;
    private int cost;
    private int maxCost;

    public BoardNode(int [][] state, int [][] goal) {
        this.state = state; // the state
        this.goal = goal;
        this.depth = 1; // the depth
        this.children = new ArrayList<>(); //the children of the node
        this.parent = null;
        this.cost = 0;
        this.maxCost = 0;
        this.stringState = stringBoard();
        this.direction = null;
        for(int i=0; i<=2; i++) {
            for(int j=0; j<=2; j++) {
                if(state[i][j]==0) {
                    this.blank_row = i;
                    this.blank_col = j;
                    break;
                }
            }
        }

    }

    public String stringBoard() {   //method that returns a String version of the baord
        StringBuilder sb = new StringBuilder();
        for (int[] ints : state) {
            for (int anInt : ints) {
                sb.append(anInt);
            }
        }
        return sb.toString();
    }

    public void addChild(BoardNode child) { //adding a Child to the node
        child.setParent(this);
        child.setDepth(this.getDepth()+1);
        child.setMaxCost(child.getCost());
        this.children.add(child);

    }

    public void setParent(BoardNode parent) { //setting the Parent of the node
        this.parent = parent;
    }

    public void setDepth(int depth) {  //setting the Depth of the node
        this.depth = depth;
    }
    public int getDepth() {  //getting the Depth of the node
        return depth;
    }

    public BoardNode getParent() {  //getting the Parent of the node
        return parent;
    }

    public int getRowBlank() {  //getting the Row of the zero tile
        return blank_row;
    }

    public int getColBlank() { //getting the Column of the zero tile
        return blank_col;
    }

    public int [][] getMatrix(){ //getting the state in array form
        return state;
    }
    public int [][] getGoalMatrix(){ //getting the state in array form
        return goal;
    }


    public int getCost() { //getting the cost of last move
        return this.cost;
    }


    public List<BoardNode> getChildren(){ //getting the children
        return children;
    }

    public BoardNode createChild(int a, int b) {      //creating the child or possible states from current node
        int[][] temp = new int[state.length][state.length];
        for(int i=0; i<state.length; i++)
            System.arraycopy(state[i], 0, temp[i], 0, state[i].length);
        temp[blank_row][blank_col] = temp[a][b];
        int cost = state[a][b];
        temp[a][b] = 0;
        BoardNode child = new BoardNode(temp, goal);
        child.setCost(cost);							//adding to Child to parent
        addChild(child);
        return child;
    }

    public void setDir(DIRECTIONS d) {				//setting the Direction moved
        this.direction = d;
    }
    public DIRECTIONS getDir() {				//getting the direction moved
        return direction;
    }

    public boolean isGoal() {				//checking if node is goal node
        boolean result;
        result = Arrays.deepEquals(this.state, this.goal);
        return result;
    }

    @Override
    public boolean equals(Object object ) {    //equals for HashMap

        if(!(object instanceof BoardNode)) {
            return false;
        }
        BoardNode check = (BoardNode) object;

        return check.getString().equals(this.getString());
    }

    @Override
    public int hashCode() {			//Hashcode generated from String version of board
        int result = 17;
        result = 37 * result + this.getString().hashCode();
        return result;
    }

    public String getString() {			//getting String version of Board
        return stringState;
    }

    public void setCost(int i) {					//setting cost
        this.cost = i;
    }
    public void setMaxCost(int i) {
        this.maxCost = this.getParent().getMaxCost() + i;			//setting MaxCost
    }

    public int getMaxCost() { //getting the current MaxCode to get to current Node
        return maxCost;
    }

    public int getRow(int value) {				//getting the Row of a value in goalState
        int row = 0;
        for(int i=0; i<=2; i++) {
            for(int j=0; j<=2; j++) {
                if(goal[i][j]==value) {
                    row = i;
                    break;
                }
            }
        }
        return row;
    }

    public int getCol(int value) {			//getting the Column of value in goal state used for Manhattan computation
        int col = 0;
        for(int i=0; i<=2; i++) {
            for(int j=0; j<=2; j++) {
                if(goal[i][j]==value) {
                    col = j;
                }
            }
        }
        return col;
    }


}

public class Puzzle {

    private static int[][] start;
    private static int[][] goal;

    public static void getInput() throws Exception
    {
        URL path = Puzzle.class.getResource("input.txt");
        assert path != null;
        File file = new File(path.getFile());
        FileReader fr=new FileReader(file);   //reads the file
        BufferedReader br=new BufferedReader(fr);  //creates a buffering character input stream
//        StringBuilder sb=new StringBuilder();    //constructs a string buffer with no characters
        String line;
        ArrayList<ArrayList<Integer>> adj=new ArrayList<>();
        int c=0;
        while((line=br.readLine())!=null)
        {
//            sb.append(line);      //appends line to string buffer
            String [] ch=line.split(" ");
            adj.add(new ArrayList<>());
            for (String s : ch) adj.get(c).add(Integer.parseInt(s));
            c++;
//            sb.append("\n");     //line feed
        }
        fr.close();    //closes the stream and release the resources

        start=new int[adj.size()/2][adj.size()/2];
        for (int i=0;i<start.length;i++)
            for (int j=0;j<start.length;j++)
                start[i][j] = adj.get(i).get(j);

        goal=new int[start.length][start.length];
        for (int i=0;i<goal.length;i++)
            for (int j=0;j<goal.length;j++)
                goal[i][j] = adj.get(i+(adj.size()/2)).get(j);
    }


    public static void main(String[] args) throws Exception {



        getInput(); // taking input of start and goal state from the input.txt


        // Initial Nodes
        BoardNode startNode = new BoardNode(start, goal);

        // search waiting to be initialized
        Search search = null;


        boolean con = true;
        while(con)	{  //the loop keeps going till User says no
            System.out.println();
            System.out.println("8 puzzle using A* Algorithm");
            System.out.println();
            System.out.println();
            System.out.println("This is the A* algorithm, please pick a heuristic: ");
            System.out.println();
            System.out.println("1. Misplaced Tiles");
            System.out.println("2. Manhattan");
            System.out.println();
            Scanner scanner = new Scanner(System.in);
            int input = scanner.nextInt();

            switch(input){

                case 1:
                    search = new Astar(startNode,1);
                    break;

                case 2:
                    search = new Astar(startNode,2);
                    break;

            }


            System.out.println("The search will begin: ");
            assert search != null;
            if(!search.search()) //the search starts
            {
                 System.out.println("No solutions!!!");
            }
            System.out.println("Do you want to continue?");
            System.out.println();
            System.out.println("1. Yes");
            System.out.println("2. No");
            int input2 = scanner.nextInt();
            if(input2==2) {
                con = false;
                scanner.close();
            }

        }

    }

}
