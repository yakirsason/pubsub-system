package test;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
public class Node {
    private String name;
    private List<Node> edges;
    private Message msg;

    public Node(String name){
        this.name=name;
        this.edges=new ArrayList<>();
    }
    public String getName() {
    return name;
    }

   public void addEdge(Node n){
    if(n == null) return;
    if(!edges.contains(n)) edges.add(n);
    }


    public boolean hasCycles(){
        Set<Node> visited= new HashSet<>();
        return dfs(this,visited);
    }

    private boolean dfs(Node target, Set<Node> visited){
        visited.add(this);

        for(Node child:edges){
            if(child==null) continue;

            if (child==target) return true;

            if(!visited.contains(child)){
                if(child.dfs(target, visited)) return true;
            }
        }
        return false;
    }


}