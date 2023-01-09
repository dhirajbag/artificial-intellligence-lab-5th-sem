/*
    Student: Dhiraj Bag
    Roll: 001911001033
    BE in IT (3rd year 1st semester)
    AI Lab - Assignment 3 : Iterative Deepening

*/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/* Point[1]: Implementation of Iterative Deepening Algorithm */
class iddfs{
    public static void run(int graph[][], int src, int max_depth, String identifiers[]){
        int n = graph.length;
        

        for(int depth=1; depth <= max_depth; depth++){
            int visited[] = new int[n];
            System.out.println("\nIDDFS from "+identifiers[src]+" with depth "+depth+": ");
            dfs_visit(src, graph, visited, 0, depth, false, "", identifiers);
        }

    }

    private static void dfs_visit(int u, int graph[][], int visited[], int depth, int max_depth, boolean first_call, String indent, String identifiers[]){

        int n = graph.length;
        visited[u] = 1;

        if(first_call)
            System.out.print(" --> "+identifiers[u]);
        else{
            System.out.println(indent+":");
              System.out.print(indent+": --> "+identifiers[u]);
        }

        if(depth==max_depth){
            System.out.println(" --#");
            return ;
        }

        first_call = true;
        for(int i=0; i<n; i++){
            if(graph[u][i] != 0 && visited[i]==0 ){
                dfs_visit(i, graph, visited, depth+1, max_depth, first_call, indent+"      ", identifiers);
                first_call = false;
            }
        }

        if(first_call)
            System.out.println(" --#");
    }
}

class dfs{
    public static void run(int graph[][], int src, int max_depth, String identifiers[]){
        int n = graph.length;
        

        int visited[] = new int[n];
        System.out.println("\nDFS from "+identifiers[src]+" with max depth "+max_depth);
        dfs_visit(src, graph, visited, 0, max_depth, false, "", identifiers);

    }

    private static void dfs_visit(int u, int graph[][], int visited[], int depth, int max_depth, boolean first_call, String indent, String identifiers[]){

        int n = graph.length;
        visited[u] = 1;

        if(first_call)
            System.out.print(" --> "+identifiers[u]);
        else{
            System.out.println(indent+":");
              System.out.print(indent+": --> "+identifiers[u]);
        }

        if(depth==max_depth){
            System.out.println(" --#");
            return ;
        }

        first_call = true;
        for(int i=0; i<n; i++){
            if(graph[u][i] != 0 && visited[i]==0 ){
                dfs_visit(i, graph, visited, depth+1, max_depth, first_call, indent+"      ", identifiers);
                first_call = false;
            }
        }

        if(first_call)
            System.out.println(" --#");
    }
}


public class iddfs_compare {
    public static void main(String args[]) throws IOException{
        File file = new File("./input.txt"); /* Point[4]: Graph read from file */

        BufferedReader br = new BufferedReader(new FileReader(file));

        String line = br.readLine();
        int n = Integer.parseInt(line);

        line = br.readLine();
        String identifiers[] = line.trim().split(" ");

        int graph[][]  = new int[n][n];
        for(int i=0; i<n; i++){
            line = br.readLine();
            String nums_str[] = line.trim().split(" ");
            for(int j=0; j<n; j++){
                graph[i][j] = Integer.parseInt(nums_str[j]);
            }
        }
        int src = Integer.parseInt(br.readLine()) -1; // 1 based numbering to 0 based numbering
        br.close();

        int max_depth = 5;

        System.out.println("=>IDDFS Visit Given Below: ");
        iddfs.run(graph, src, max_depth, identifiers); /* Point[2]: Executed IDDFS on the given graph */

        System.out.println("\n\n=>DFS Visit Given Below: ");
        dfs.run(graph, src, max_depth, identifiers);  /* Point[3]: Executed previous DFS on the given graph with depth 5 */

    }    
}
