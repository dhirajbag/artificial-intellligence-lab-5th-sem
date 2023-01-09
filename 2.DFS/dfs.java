/*
    Student: Dhiraj Bag
    Roll: 001911001033
    BE in IT (3rd year 1st semester)
    AI Lab - Assignment 2

*/
import java.util.*;
import java.io.*;

public class dfs {

    private ArrayList<Integer> current_path = new ArrayList<Integer>();
    private void print_path(){
        if(current_path.size()==0){
            System.out.println("No path found.");
        }
        else{
            System.out.println("Following path is found: ");

            for(int i=0; i<current_path.size(); i++){
                System.out.print((1+current_path.get(i)) + (i != current_path.size()-1 ? " --> " : "") );
                /* printing in 1 based numbering */
            }
            System.out.print("\n\n");
        }
    }

    public boolean run_dfs(int source, int destination, boolean graph[][], boolean is_visited[]){

        if(is_visited[source]) return false;

        is_visited[source] = true;
        current_path.add(source);
       if(source == destination){
           return true;
       }

       for(int i=0; i<graph[source].length; i++){
           if(graph[source][i] && run_dfs(i, destination, graph, is_visited))
                return true;
       }

       current_path.remove(current_path.size()-1);
       return false;
    }

    private static void print_graph(boolean graph[][]){
        for(int i=0; i<graph.length; i++){
            for(int j=0; j<graph[i].length; j++){
                System.out.print( (graph[i][j] ? 1 : 0) +" ");
            }
            System.out.print("\n");
        }
    }
    public static void main(String args[]) throws Exception {

        File file = new File("./input.txt");
  
        BufferedReader br = new BufferedReader(new FileReader(file));

        String line = br.readLine();
        int n = Integer.parseInt(line);

        boolean graph[][]  = new boolean[n][n];
        for(int i=0; i<n; i++){
            line = br.readLine();
            String nums_str[] = line.trim().split(" ");
            for(int j=0; j<n; j++){
                graph[i][j] = Integer.parseInt(nums_str[j])==1 ? true : false;
            }
        }

        line = br.readLine();
        int source = Integer.parseInt(line) - 1; /* 1 based numbering to 0 based numbering */

        line = br.readLine();
        int destination = Integer.parseInt(line) - 1;

        br.close();

        //print_graph(graph);

        if(source == destination){
            System.out.println("=>  Path found: Source and destination are same.");
            return;
        }

        dfs solution = new dfs();
        boolean visited[] = new boolean[n]; 
        solution.run_dfs(source, destination, graph, visited);
        solution.print_path();
    }
    
}
