# DictionaryClient
Run this client with Eclipse IDE and enter queries from the console.

open SERVER PORT - 
    opens a new control connection to a dictionary server (ex: dict.org). The name can be a domain or an IP address. PORT parameter is optional, if it's missing then default 2628 is used.

dict - 
    Retrieve and print the list of all the dictionaries the server supports. Each line will consist of a single word that is the the name of a dictionary followed by some
    information about the dictionary.

set DICTIONARY -
    Set the dictionary to retrieve definitions or matches from. The string representing
    the dictionary name can be anything. However for subsequent define and match commands to work the string will have to be eiher the first word on one of the lines
    returned by the dict command or one of the required virtual databases defined in
    section 3.4 of the RFC. The default dictionary to use if the set command has not
    been given is *. When a connection is established to a dictionary server, the dictionary to use is initially set to *. Multiple set commands simply result in a new dictionary to search being set. Multiple set commands do not result in the building of a collection of dictionaries to search.

currdict - 
    Prints the name of the current dictionary being used. Initially this value is *.

define WORD - 
    Retrieve and print all the definitions for WORD. WORD is looked up in the
    dictionary or dictionaries as specified through the set command.

match WORD -
    Retrieve and print all the exact matches for WORD. WORD is looked up in the
    dictionary or dictionaries as specified through the set command.

prefixmatch WORD -
    Retrieve and print all the prefix matches. for WORD. WORD is looked up in the
    dictionary or dictionaries as specified through the set command.

close - 
    After sending the appropriate command to the server and receiving a response,
    closes the established connection and enters a state where the next command
    expected is open or quit.

quit - 
    Closes any established connection and exits the program. This command is valid at
    any time.