package com.myorg.app;

import java.util.Map;

public class Greeting {
    public String onEvent(Map<String, String> event) {
        System.out.println("received: " + event);
        return "Event processed !";
    }
}
