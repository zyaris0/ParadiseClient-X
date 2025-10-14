package net.paradide_client.utils;

public record OptionUtil(String name, OptionType type, String[] args) {
    public OptionUtil(String name, OptionType type) {
        this(name, type, null);
    }
}

