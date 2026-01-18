package test;

import java.util.ArrayList;
import java.util.List;

public class Topic {
    public final String name;
    private final List<Agent> subs;
    private final List<Agent> pubs;
    Topic(String name){
        this.name=name;
        this.subs=new ArrayList<>();
        this.pubs=new ArrayList<>();
    }

    public void subscribe(Agent a){
        if(a== null)return;
       
        if(!subs.contains(a)){
            subs.add(a);
        }
    }
    public void unsubscribe(Agent a){
      if(a== null)return;
        subs.remove(a);
    }

    public void publish(Message m){
        if(m==null)return;
        
        for(Agent a :new ArrayList<>(subs)){
            a.callback(name, m);
        }
    }

    public void addPublisher(Agent a){
         if(a== null)return;
         if(!pubs.contains(a)){
             pubs.add(a);
        }
    }

    public void removePublisher(Agent a){
        if(a== null) return;
        pubs.remove(a);
    }

    public List<Agent> getSubscribers() {
        return new ArrayList<>(subs);
    }

    
    public List<Agent> getPublishers() {
        return new ArrayList<>(pubs);
    }


}
