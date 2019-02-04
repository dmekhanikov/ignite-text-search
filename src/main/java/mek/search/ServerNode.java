package mek.search;

import org.apache.ignite.Ignition;

public class ServerNode {
    public static void main(String[] args) {
        Ignition.start("config/ignite.xml");
    }
}
