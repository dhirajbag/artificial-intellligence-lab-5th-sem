import java.io.*;
import java.util.*;

public class bfs {

    public static int[] run_bfs(int source, int destination, boolean graph[][]){
        boolean visited[] = new boolean[graph.length];
        Queue<Integer> Q = new LinkedList<Integer>();
        int path_to[] = new int[graph.length];
        path_to[destination] = -1;
        path_to[source] = -1;

        Q.add(source);
        visited[source] = true;

        while(! Q.isEmpty()){
            int u = Q.remove();

            if(u==destination) break;

            for(int v = 0; v < graph[u].length; v++){
                if(graph[u][v] && !visited[v]){
                    Q.add(v);
                    path_to[v] = u;
                    visited[v] = true;
                }
            }
        }

        return path_to;
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


        int path_to[] = run_bfs(source, destination, graph);

        if(path_to[destination]==-1){
            System.out.println("=> No path from source do destination.");
        }
        else {
            System.out.println("=> Path found:");
            int reach = destination;
            while(reach != -1){
                /* Printing in 1 based numbering */
                System.out.print( (reach+1) + (reach != source ? " <-- " : ""));
                reach = path_to[reach];
            }
        }

    }
    
}
