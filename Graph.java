package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import test.TopicManagerSingleton.TopicManager;

public class Graph extends ArrayList<Node>{
    
    public boolean hasCycles() {
        for(Node n : this){
            if(n.hasCycles()) return true;

        }
        return false;
    }
   public void createFromTopics(){
        this.clear();
        Map<String,Node> nodes = new HashMap<>();

        for(Topic t : TopicManagerSingleton.get().getTopics()){
            String topicNodeName = "T" + t.name;

            if(!nodes.containsKey(topicNodeName)){
                Node topicNode = new Node(topicNodeName);
                nodes.put(topicNodeName, topicNode);
                this.add(topicNode);
            }

            for(Agent a : t.getSubscribers()){
                String agentNodeName = "A" + a.getName();
                if(!nodes.containsKey(agentNodeName)){
                    Node agentNode = new Node(agentNodeName);
                    nodes.put(agentNodeName, agentNode);
                    this.add(agentNode);
                }
            }

            for(Agent a : t.getPublishers()){
                String agentNodeName = "A" + a.getName();
                if(!nodes.containsKey(agentNodeName)){
                    Node agentNode = new Node(agentNodeName);
                    nodes.put(agentNodeName, agentNode);
                    this.add(agentNode);
                }
            }
        }

        for(Topic t : TopicManagerSingleton.get().getTopics()){
            Node topicNode = nodes.get("T" + t.name);

            for(Agent a : t.getSubscribers()){
                Node agentNode = nodes.get("A" + a.getName());
                topicNode.addEdge(agentNode);
            }

            for(Agent a : t.getPublishers()){
                Node agentNode = nodes.get("A" + a.getName());
                agentNode.addEdge(topicNode);
            }
        }
    }
}
