/*
    Name: Dhiraj BAG
    Roll No.: 001911001033
    Dept.: Information Technology
    Sub.: AI LAB : Assignment 4.1
*/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;
import java.util.Comparator;
import java.util.PriorityQueue;


class Successor {

    public Successor() {

    }

    public List<BoardNode> successor(BoardNode node) {
        //successor function that takes a state and returns a list of possible states that can be reached

        List<BoardNode> list = new ArrayList<>();

        int row = node.getRowBlank();
        int col = node.getColBlank();

        //up

        if(row != 0) {  //uses information about the nature of 2d arrays to dictate the zero-tile's movement.
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

    public PathActions(BoardNode initialNode, BoardNode goalNode, Info inf) {  //the arguments are goalNode, info and initialNode so a path can be found.
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

    public void  makePQueue(Comparator<BoardNode> c) {   //creates a priority queue with a comparator as an argument to decide the order in which the queue will organize elements
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

    public int numCorPos(BoardNode node) throws Exception{
        int result = 0;
        int [][] state = node.getMatrix();
        URL path = Main.class.getResource("input.txt");
        File file = new File(path.getFile());
        FileReader fr=new FileReader(file);   //reads the file
        BufferedReader br=new BufferedReader(fr);  //creates a buffering character input stream
        String line;
        ArrayList<ArrayList<Integer>> adj=new ArrayList<>();
        int c=0;
        while((line=br.readLine())!=null)
        {
            String [] ch=line.split(" ");
            adj.add(new ArrayList<>());
            for (String s : ch) adj.get(c).add(Integer.parseInt(s));
            c++;
        }
        fr.close();    //closes the stream and release the resources

        int[][] goal=new int[adj.size()/2][adj.size()/2];
        for (int i=0;i<goal.length;i++)
            for (int j=0;j<goal.length;j++)
                goal[i][j] = adj.get(i+(adj.size()/2)).get(j);
        for(int i=0; i<state.length; i++) {
            for(int j=0; j<state.length; j++) {
                if(goal[i][j]!=state[i][j]) {
                    result += 1;
                }
            }
        }
        return result;
    }

}


class BestFirst implements Search {

    private final BoardNode initialNode;

    public BestFirst(BoardNode node) {
        this.initialNode = node;
    }

    private static class hComparator  implements Comparator<BoardNode> {

        Heuristics h1 = new Heuristics();

        public int compare(BoardNode a, BoardNode b) {
            try {
                return h1.numCorPos(a) - h1.numCorPos(b);

            }
            catch (Exception e)
            {
                System.out.println("Exception");
            }
            return 0;
        }

    }


    public void search() {

        //BestFirst search which creates a priority queue which sorts according to h(n)
        Info info = new Info();
        info.makePQueue(new hComparator()); //making a priority queue with hComparator
        BoardNode node = initialNode;
        info.pQueue.add(node);

        while(!(info.pQueue.isEmpty())) {
            node = info.pQueue.poll();
            info.incTime();
            info.visited.put(node.hashCode(), node);
            try {
                if(node.isGaol()) {
                    PathActions p = new PathActions(initialNode,node,info); // class that creates a path from goal to start Node if goal is reached.
                    p.printPath(); // the path is then printed
                    return;
                }
            }
            catch (Exception e)
            {
                System.out.println("Exception");
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
    }

}


class UniformCost implements Search {
    private final BoardNode initialNode;

    public UniformCost(BoardNode node) {
        this.initialNode = node;
    }

    private static class gComparator implements Comparator<BoardNode>{

        public int compare(BoardNode a, BoardNode b) {
            return a.getMaxCost() - b.getMaxCost();
        }
    }

    public void search() {
        //Uniform Cost search which creates a priority queue which sorts according to g(n)
        Info info = new Info();
        info.makePQueue(new gComparator()); //making a priority queue with gComparator
        BoardNode node = initialNode;
        info.pQueue.add(node);

        while(!(info.pQueue.isEmpty())) {
            node = info.pQueue.poll();
            info.incTime();
            info.visited.put(node.hashCode(), node);
            try {
                if(node.isGaol()) {
                    PathActions p = new PathActions(initialNode,node,info); // class that creates a path from goal to start Node if goal is reached.
                    p.printPath(); // the path is then printed
                    return;
                }
            }
            catch (Exception e)
            {
                System.out.println("Exception");
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
    }
}






enum DIRECTIONS {
    LEFT,RIGHT,UP,DOWN
}

interface Search {

    void search(); //search interface to help with UI when creating a Search.
}



class BoardNode {

    private final int[][]  state;
    private final List<BoardNode> children;
    private BoardNode parent;
    private int depth;
    private int blankrow;
    private int blankcol;
    private DIRECTIONS direction;
    private final String stringState;
    private int cost;
    private int maxCost;

    public BoardNode(int [][] state) {
        this.state = state; // the state
        this.depth = 1; // the depth
        this.children = new ArrayList<>(); //the children of the node
        this.parent = null;
        this.cost = 0;
        this.maxCost = 0;
        this.stringState = stringBoard();
        this.direction = null;
        for(int i=0; i<state.length; i++) {
            for(int j=0; j<state.length; j++) {
                if(state[i][j]==0) {
                    this.blankrow = i;
                    this.blankcol = j;
                    break;
                }
            }
        }

    }

    public String stringBoard() {   //method that returns a String version of the bard
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
        return blankrow;
    }

    public int getColBlank() { //getting the Column of the zero tile
        return blankcol;
    }

    public int [][] getMatrix(){ //getting the state in array form
        return state;
    }

    public int getCost() { //getting the cost of last move
        return this.cost;
    }

    public BoardNode createChild(int a, int b) {      //creating the child or possible states from current node
        int[][] temp = new int[state.length][state.length];
        for(int i=0; i<state.length; i++)
            System.arraycopy(state[i], 0, temp[i], 0, state[i].length);
        temp[blankrow][blankcol] = temp[a][b];
        int cost = state[a][b];
        temp[a][b] = 0;
        BoardNode child = new BoardNode(temp);
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

    public boolean isGaol() throws Exception{				//checking if node is goal node
        boolean result;
        URL path = Main.class.getResource("input.txt");
        File file = new File(path.getFile());
        FileReader fr=new FileReader(file);   //reads the file
        BufferedReader br=new BufferedReader(fr);  //creates a buffering character input stream
        String line;
        ArrayList<ArrayList<Integer>> adj=new ArrayList<>();
        int c=0;
        while((line=br.readLine())!=null)
        {
            String [] ch=line.split(" ");
            adj.add(new ArrayList<>());
            for (String s : ch) adj.get(c).add(Integer.parseInt(s));
            c++;
        }
        fr.close();    //closes the stream and release the resources

        int[][] goal=new int[adj.size()/2][adj.size()/2];
        for (int i=0;i<goal.length;i++)
            for (int j=0;j<goal.length;j++)
                goal[i][j] = adj.get(i+(adj.size()/2)).get(j);
        BoardNode goalNode = new BoardNode(goal);
        result = this.equals(goalNode);
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

}


public class Main {

    public static void main(String[] args) throws Exception{

        // All the boards.

        URL path = Main.class.getResource("input.txt");
        File file = new File(path.getFile());
        FileReader fr=new FileReader(file);   //reads the file
        BufferedReader br=new BufferedReader(fr);  //creates a buffering character input stream
        String line;
        ArrayList<ArrayList<Integer>> adj=new ArrayList<>();
        int c=0;
        while((line=br.readLine())!=null)
        {
            String [] ch=line.split(" ");
            adj.add(new ArrayList<>());
            for (String s : ch) adj.get(c).add(Integer.parseInt(s));
            c++;
        }
        fr.close();    //closes the stream and release the resources

        int[][] start=new int[adj.size()/2][adj.size()/2];
        for (int i=0;i<start.length;i++)
            for (int j=0;j<start.length;j++)
                start[i][j] = adj.get(i).get(j);

        // Initial Nodes for 3 Levels
        BoardNode startNode = new BoardNode(start);
        // search waiting to be initialized
        Search search = null;

        // Simple UI which prompts the User for an algorithm and difficulty level
        boolean con = true;
        while(con)	{  //the loop keeps going till User says no
            System.out.println();
            System.out.println("Welcome to 8 puzzle");    //Below are the options asking User for which search and what difficulty to pick
            System.out.println("Please chose an Algorithm below: ");
            System.out.println();
            System.out.println();

            System.out.println("1. UniformCost");
            System.out.println("2. BestFirst");


            System.out.println();
            Scanner scanner = new Scanner(System.in);
            int input = scanner.nextInt();

            switch(input) {    //switch is used to determine what search and difficulty to use

                case 1:
                    search = new UniformCost(startNode);
                    break;

                case 2:
                    search = new BestFirst(startNode);
                    break;

                default:
                    System.out.println("Please enter correct option!!!");

            }

            System.out.println();
            System.out.println("The search will begin: ");
            assert search != null;
            search.search(); //the search starts
            System.out.println("Do you want to continue?");
            System.out.println();
            System.out.println("1. Yes");
            System.out.println("2. No");
            int input5 = scanner.nextInt();
            if(input5==2) {
                con = false;
            }

        }
    }

}






