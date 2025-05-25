package org.dmitriykotik.voting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.NamespacedKey;

public class Vars {
    public static Map<String, Election> elections = new HashMap<>();
    public static NamespacedKey keyPollId;
    public static final Map<String,String> playerVote = new HashMap<>();
    public static class Election {
        String id, name, owner;
        List<String> options = new ArrayList<>();
        Map<String, String> votes = new HashMap<>();
        boolean published = false;
    }

    // Config File
    public static String Prefix = "";

    public static String ReloadStart = "";
    public static String ReloadDone = "";

    public static String CommandNotFound = "";
    public static String AccessDenied = "";
    public static String AccessDeniedHelp = "";

    public static String HelpHelp = "";
    public static String HelpCreate = "";
    public static String HelpAdd = "";
    public static String HelpRemove = "";
    public static String HelpClear = "";
    public static String HelpPreview = "";
    public static String HelpPublish = "";
    public static String HelpVote = "";
    public static String HelpResult = "";
    
    public static String HelpDelete = "";
    public static String HelpReload = "";

    public static String Poll = "";
    public static String PollCreatedWithId = "";
    public static String PollNotFound = "";
    public static String PollElementAdd = "";
    public static String PollElementRemove = "";
    public static String PollElementClear = "";
    public static String PollPublish = "";
    public static String PollAlreadyVoted = "";
    public static String PollResults = "";
    public static String PollWinners = "";
    public static String PollDelete = "";

    public static String PollSkip = "";
    public static String PollSelfVote = "";
    public static String PollVoted = "";
    public static String PollInvalidElement = "";
}
