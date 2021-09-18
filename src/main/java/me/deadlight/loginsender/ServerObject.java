package me.deadlight.loginsender;

public class ServerObject implements Comparable<ServerObject>{
    public int count;
    public String name;

    public ServerObject(String name, int count) {
        this.name = name;
        this.count = count;
    }


    @Override
    public int compareTo(ServerObject o) {
        return Integer.compare(this.count, o.count);
    }
}
