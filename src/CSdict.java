
import java.lang.System;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.util.Scanner;
import java.io.*;
import java.net.*; 
import java.util.regex.*;

public class CSdict {
    static final int MAX_LEN = 255;
    static final int PERMITTED_ARGUMENT_COUNT = 1;
    static Boolean debugOn = false;

    static Socket socket = null;
    static PrintWriter out = null;
    static BufferedReader in = null;
    static BufferedReader stdIn = null;
    static String dictionary = "*";

    public static void main(String [] args) {

    	byte cmdString[] = new byte[MAX_LEN];
    	
    	if (args.length == PERMITTED_ARGUMENT_COUNT) {
    	    debugOn = args[0].equals("-d");
    	    if (debugOn) {
    		System.out.println("Debugging output enabled");
    	    } else {
    		return;
                } 
    	} else if (args.length > PERMITTED_ARGUMENT_COUNT) {
    	    System.out.println("996 Too many command line options - Only -d is allowed");
    	    return;
    	}
    		
    	try {
    	    for (int len = 1; len > 0;) {
                System.out.print("csdict> ");
        		len = System.in.read(cmdString);
                String cmd = new String(cmdString, "UTF-8");

        		if (len <= 0) {
                    System.out.println("len <= 0");
        		    break;
                }
        		// Start processing the command here.
                else if (len > 0) {
                    if (cmd.substring(0,4).toLowerCase().equals("open")) {
                        String hostName = "";
                        int hostIndex = 5;
                        hostIndex = ignoreSpaceIndex(cmd, hostIndex);
                        while (hostIndex < len && !cmd.substring(hostIndex, hostIndex+1).equals(" ")) {
                            hostName += cmd.substring(hostIndex, hostIndex+1);
                            hostIndex++;
                        }
                        String portNumber = "";
                        int portIndex = ignoreSpaceIndex(cmd, hostIndex);
                        while (portIndex < len) {
                            portNumber += cmd.substring(portIndex, portIndex+1);
                            portIndex++;
                        }
                        if (portNumber.equals("")) {
                            portNumber = "2628";
                        }
                        open(hostName.trim(), Integer.parseInt(portNumber.trim()));

                    }
                    else if (cmd.substring(0,4).toLowerCase().equals("dict")) {
                        dict();
                    }
                    else if (cmd.substring(0,3).toLowerCase().equals("set")) {
                        String d = "";
                        int index = 4;
                        index = ignoreSpaceIndex(cmd, index);
                        while (index < len && !cmd.substring(index, index+1).equals(" ")) {
                            d += cmd.substring(index, index+1);
                            index++;
                        }
                        if (!d.equals("")) {
                            set(d.trim());
                        }
                        else
                            System.out.println("901 Incorrect number of arguments.");
                        
                    }
                    else if (cmd.substring(0,8).toLowerCase().equals("currdict")) {
                        currdict();
                    }
                    else if (cmd.substring(0,6).toLowerCase().equals("define")) {
                        String word = "";

                        int index = 7;

                        index = ignoreSpaceIndex(cmd, index);
                        while (index < len && !cmd.substring(index, index+1).equals(" ")) {
                            if (!cmd.substring(index, index+1).equals(" ")) {
                                word += cmd.substring(index, index+1);
                            }
                            index++;
                        }
                        if (index == len) {
                            query("DEFINE ", word, " ", dictionary);
                        }

                        index = ignoreSpaceIndex(cmd, index);
                        if (index < len) {
                            String w = "";
                            while (index < len) {
                                if (!cmd.substring(index, index+1).equals(" ")) {
                                    w += cmd.substring(index, index+1);
                                }
                                index++;
                            }
                            query("DEFINE ", w, " ", word);
                        }
                    }
                    else if (cmd.substring(0,5).toLowerCase().equals("match")) {
                        String word = "";

                        int index = 6;
                        index = ignoreSpaceIndex(cmd, index);
                        while (index < len && !cmd.substring(index, index+1).equals(" ")) {
                            word += cmd.substring(index, index+1);
                            index++;
                        }
                        if (index == len) {
                            query("MATCH ", word, " exact ", dictionary);
                        }
                        index = ignoreSpaceIndex(cmd, index);
                        if (index < len) {
                            index++;
                            String w = "";
                            while (index < len) {
                                w += cmd.substring(index, index+1);
                                index++;
                            }
                            query("MATCH ", w, " exact ", word);
                        }
                    }
                    else if (cmd.substring(0,11).toLowerCase().equals("prefixmatch")) {
                        String word = "";

                        int index = 12;
                        index = ignoreSpaceIndex(cmd, index);
                        while (index < len && !cmd.substring(index, index+1).equals(" ")) {
                            word += cmd.substring(index, index+1);
                            index++;
                        }
                        if (index == len) {
                            query("MATCH ", word, " prefix ", dictionary);
                        }
                        index = ignoreSpaceIndex(cmd, index);
                        if (index < len) {
                            index++;
                            String w = "";
                            while (index < len) {
                                w += cmd.substring(index, index+1);
                                index++;
                            }
                            query("MATCH ", w, " prefix ", word);
                        }
                    }
                    else if (cmd.substring(0,5).toLowerCase().equals("close")) {
                        close();
                    }
                    else if (cmd.substring(0,4).toLowerCase().equals("quit")) {
                        quit();
                    }
                    else if (cmd.substring(0,1).equals("#") || len == 1) {}
                    else
                        System.out.println("900 Invalid command.");
                }
                else
        		  System.out.println("900 Invalid command.");
    	    }
    	} catch (IOException exception) {
    	    System.err.println("998 Input error while reading commands, terminating.");
            quit();
    	}
    }
    public static void open(String hostName, int portNumber) {
        // hostName = "test.dict.org";
        // hostName = "dict.org";
        // hostName = "dict.uni-leipzig.de";

        if (socket == null) {
            try {
                socket = new Socket(hostName, portNumber);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                stdIn = new BufferedReader(new InputStreamReader(System.in));
                // System.out.println("connected!");
            } catch (IOException exception) {
                System.err.println("920 Control connection to " + hostName + " on port " + portNumber + " failed to open.");
            }
        }
        else
            System.out.println("903 supplied command is not expected at this time.");

    }
    public static void close() {
        if (socket != null) {
            try {
                socket.close();
                socket = null;
                out = null;
                in = null;
                stdIn = null;
                debugOn = false;
                dictionary= "*";
            } catch (IOException exception) {
                System.err.println("999 Processing error. quit failed");
            }
        }
        else
            System.out.println("903 supplied command is not expected at this time.");
    }
    public static void quit() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException exception) {
                System.err.println("999 Processing error. quit failed");
            }
        }
        socket = null;
        out = null;
        in = null;
        stdIn = null;
        debugOn = false;
        dictionary= "*";
        System.exit(0);
    }
    public static void dict() {
        if (socket != null) {
            out.println("SHOW DB");
            if (debugOn) {
                System.out.println("--> SHOW DB");
            }
            try {
                String fromServer = "";
                while (!fromServer.trim().contains("250 ok")) {
                    fromServer = in.readLine();
                    if (checkOutput("", "", "", fromServer)) {
                        if (debugOn == false) {
                            break;
                        }
                        else {
                            System.out.println("<-- " + fromServer);
                            break;
                        }
                    }
                    if (!fromServer.contains("150 ") 
                        && !fromServer.contains("152 ") 
                        && !fromServer.contains("220 ")
                        && !fromServer.contains("110 ")) {
                        System.out.println(fromServer);
                    }
                    else if (debugOn == true 
                        && (fromServer.contains("150 ") 
                        || fromServer.contains("152 ") 
                        || fromServer.contains("220 ")
                        || fromServer.contains("110 "))) {
                        System.out.println("<-- " + fromServer);
                    }
                }
            } catch (IOException e) {
                System.err.println("903 Supplied command not expected at this time");
            }
        }
        else
            System.out.println("903 Supplied command not expected at this time");
    }
    public static void set(String d) {
        if (socket != null) {
            dictionary = d;
        }
        else
            System.out.println("903 Supplied command not expected at this time");
    }
    public static void currdict() {
        if (socket != null) {
            System.out.println(dictionary);
        }
        else
            System.out.println("903 Supplied command not expected at this time");
    }
    public static void query(String function, String word, String strategy, String d) {
        if (socket != null) {
            String fromUser;
            fromUser = function + d + strategy + word;
            out.println(fromUser);
            if (debugOn) {
                System.out.println("--> " + function + d + strategy + word);
            }
            try {
                String fromServer = "";
                while (!fromServer.trim().contains("250 ok")) {
                    fromServer = in.readLine();
                    if (checkOutput(function, word, strategy, fromServer)) {
                        if (debugOn == false) {
                            break;
                        }
                        else {
                            System.out.println("<-- " + fromServer);
                            break;
                        }
                    }
                    if (debugOn == false 
                        && !fromServer.contains("150 ") 
                        && !fromServer.contains("152 ") 
                        && !fromServer.contains("220 ")) {
                        if (function.equals("DEFINE ") && fromServer.contains("151 ")) {
                            int cutoff = 0;
                            cutoff = 3 + word.length() + 3;
                            fromServer = "@ " + fromServer.substring(cutoff);
                        }
                            System.out.println(fromServer);
                    }
                    else if (debugOn == true
                        && !fromServer.contains("150 ") 
                        && !fromServer.contains("152 ") 
                        && !fromServer.contains("220 ")
                        && !fromServer.contains("151 ")) {
                            System.out.println(fromServer);
                    }
                    else if (debugOn == true 
                        && (fromServer.contains("150 ") 
                        || fromServer.contains("152 ") 
                        || fromServer.contains("220 ")
                        || fromServer.contains("151 "))) {
                        System.out.println("<-- " + fromServer);
                    }
                }
            } catch (IOException e) {
                System.err.println("998 Input error while reading commands, terminating.");
                quit();
            }
        }
        else
            System.out.println("903 Supplied command not expected at this time");
    }
    public static int ignoreSpaceIndex(String cmd, int index) {
        while (cmd.substring(index, index+1).equals(" ")) {
            index++;
        }
        return index;
    }
    public static boolean checkOutput(String function, String word, String strategy, String serverOutput) {
        if (function.equals("DEFINE ") && serverOutput.contains("552 no match")) {
            System.out.println("**No definition found**");
            query("MATCH ", word, " . ", "*");
            return true;
        }
        else if (strategy.equals(" exact ") && serverOutput.contains("552 no match")) {
            System.out.println("****No matching word(s) found****");
            return true;
        }
        else if (strategy.equals(" . ") && serverOutput.contains("552 no match")) {
            System.out.println("****No dictionaries have a definition for this word****");
            return true;
        }
        else if (strategy.equals(" prefix ") && serverOutput.contains("552 no match")) {
            System.out.println("*****No prefix matches found*****");
            return true;
        }
        else if (serverOutput.contains("250 ok")) {
            return true;
        }
        else if (serverOutput.contains("550 invalid database")) {
            System.out.println("930 Dictionary does not exist.");
            return true;
        }
        else if (serverOutput.contains("501 syntax error")) {
            System.out.println("902 Invalid argument");
            return true;
        }
        else 
            return false;
    }

}














