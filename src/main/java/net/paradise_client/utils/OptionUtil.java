package net.paradide_client.utils;
import net.paradise_client.utils.OptionType;

public record OptionUtil(String name, OptionType type, String[] args) {
    public OptionUtil(String name, OptionType type) {
        this(name, type, null);
    }
}

