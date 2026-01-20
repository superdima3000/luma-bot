package org.example.node.service.enums;

public enum ServiceCommands {
    HELP("/help"),
    PASSWORD("/password"),
    START("/start"),
    CANCEL("/cancel");

    private final String cmd;
    ServiceCommands(String cmd) {
        this.cmd = cmd;
    }

    @Override
    public String toString() {
        return cmd;
    }

    public boolean equals(String cmd){
        return this.toString().equals(cmd);
    }
}
