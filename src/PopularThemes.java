import java.io.File;
import  java.util.List;
import  java.util.Scanner;
import  java.util.HashMap;
import java.util.ArrayList;
import  java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Lunga Tsewu
 * @date  22/07/2024
 *
 * @class Map users to theme they voted for as favourite theme and display theme with most votes and users who voted for theme
 */
public class PopularThemes {

    private static  final Logger LOGGER = Logger.getLogger(PopularThemes.class.getName());
    /**
     *  Map users to the theme they voted for
     * @param themes is the map of a theme to list of users voted for the theme
     * @param users map of user id to user who voted
     * @param data is the voting data
     */
    private  void mapThemeToListUsersVotedForTheme(HashMap<String,List<User>> themes, Map<Long,User> users, String[] data){
        var votedUsers = themes.get(data[1]);
        if(votedUsers == null || votedUsers.isEmpty()){
            mapThemeToListUsersVotedForTheme(themes,users,new ArrayList<>(),data);
        }
        else {
            mapThemeToListUsersVotedForTheme(themes,users,votedUsers,data);
        }
    }

    /**
     * Add users to the theme map
     * @param themes is the map of a theme to list of users voted for the theme
     * @param users map of user id to user who voted
     * @param userList list of voted users
     * @param data is the voting data
     */
    private   void mapThemeToListUsersVotedForTheme(HashMap<String,List<User>> themes, Map<Long,User> users, List<User> userList, String[] data){
        var user = users.get(Long.valueOf(data[0].trim()));
        userList.add(user);
        themes.put(data[1],userList);
    }

    /**
     * Display Popular them
     * @param themes is the map of a theme to list of users voted for the theme
     */
    private   void displayPopularTheme(HashMap<String, List<User>> themes){
        long popularTheme=-1;
        String theme=null;

        for(var key : themes.keySet()){
             if(themes.get(key).size()>popularTheme){
                 theme=key;
                 popularTheme=themes.get(theme).size();
             }
        }
        var output= String.format("Popular Theme is %s.\nUsers voted for this theme are:\n%s ",theme,usersToString(themes.get(theme)));
        LOGGER.info(output);

    }

    /**
     * Map List of users to format number. firstname lastname
     * @param users list of user to map to string
     * @return string of user detail in form of number. firstname lastname, ...
     */
    private    String usersToString(List<User> users){
        var sortedUsers = users.stream().sorted(User::compareTo);
        var numbering = new AtomicLong(0);
        return  sortedUsers.map(user -> numbering.addAndGet(1)+". " +user.firstName()+" "+ user.lastName()).collect(Collectors.joining(",\n"));
    }

    /**
     * Reads the context of users and favourite file
     * @param mapUsers  map to store user from user.txt file
     * @param favThemes map to store them with list of users who voted for the theme
     */
    private  void readFile(HashMap<Long, User> mapUsers,HashMap<String, List<User>> favThemes){
        try {
            var scannerUsers = new Scanner(new File("src/data/users.txt"));
            var scannerThemes = new Scanner(new File("src/data/favourites.txt"));
            while (scannerUsers.hasNext()) {
                String[] data =  scannerUsers.nextLine().trim().split("\t");
                var user =data[1].trim().split(" ");
                mapUsers.put(Long.parseLong(data[0].trim()), new User(user[0].trim(), user[1].trim()));

            }
            while (scannerThemes.hasNext()) {
                var data = scannerThemes.nextLine().trim().split(" ");
                mapThemeToListUsersVotedForTheme(favThemes, mapUsers,data);
            }
        }catch (Exception e) {
            LOGGER.warning("Unexpected error thrown, error: " + e);
        }
    }

    public static void main(String[] args)  {
        var popularThemes = new PopularThemes();
        var mapUsers = new HashMap<Long, User>();
        var favThemes = new HashMap<String, List<User>>();
        popularThemes.readFile(mapUsers,favThemes);
        popularThemes.displayPopularTheme(favThemes);
    }
}