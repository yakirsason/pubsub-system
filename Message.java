package test;

import java.util.Arrays;
import java.util.Date;

public class Message {
    public final byte[] data;
    public final String asText;
    public final double asDouble;
    public final Date date;

    public Message(byte[] data){
        this.data=Arrays.copyOf(data, data.length);
        this.asText=new String(this.data);
        double temp;
        try{
            temp=Double.parseDouble(this.asText);
        }catch(NumberFormatException e){
            temp= Double.NaN;
        }

        this.asDouble=temp;
        this.date=new Date();
    }
    public Message(String text){
        this(text.getBytes());
    }
    public Message(double d){
        this(Double.toString(d));
    }

 
}
