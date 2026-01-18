package test;

import java.util.Map;
import java.util.Collection;
import java.util.HashMap;

public class TopicManagerSingleton {

    public static class TopicManager{
        private final Map<String, Topic> topics;
        private TopicManager()
        {
            this.topics=new HashMap<>();
        }
        private static final TopicManager instance= new TopicManager();

        public Topic getTopic(String name){
            Topic t= topics.get(name);
            if(t==null){
                t= new Topic(name);
                topics.put(name,t);
            }
            return t;
        }
        public Collection<Topic> getTopics(){
            return topics.values();
        }
        public void clear(){
            topics.clear();
        }
    }
    public static TopicManager get(){
        return TopicManager.instance;
    }
    
}
