package me.h21.Config;

import com.google.common.reflect.TypeToken;
import me.h21.SetRankMain;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Config {


    private static CommentedConfigurationNode config;

    public static void buildConfig(ConfigurationLoader<CommentedConfigurationNode> loader, File ConfigFile){
        if (!ConfigFile.exists()){
            try{
                ConfigFile.createNewFile();
                config = loader.load();
                setupConfig();
                loader.save(config);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        else{
            try{
                config = loader.load();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private static void setupConfig(){

        config.getNode("Rank character length")
                .setComment("Determines length of rank, to prevent super big ranks like 'LOOOOOOOOOOOOOOOOL' etc. Default value 6.")
                .setValue(6);

        config.getNode("Latin letters").setComment("If player have at least one non latin letter in rank, he will get error").setValue(false);

        config.getNode("Check rankother add").setComment("Check black list, rank length and latin letters for rankother add command").setValue(false);

        List<String> list = new ArrayList<>();
        list.add("Admin");
        list.add("Moder");

        try {
            config.getNode("Black list").setComment("Doesn’t matter how the word will be spelled (upperCase/lowerCase)").setValue(new TypeToken<List<String>>(){}, list);
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }

    }

    /**Config getters & setters**/

    public static int getRankLength(){

        return config.getNode("Rank character length").getInt();
    }

    public static boolean getLatinLetters(){

      return config.getNode("Latin letters").getBoolean();
    }

    public static List<String> getBlackList() throws ObjectMappingException {

        return config.getNode("Black list").getList(TypeToken.of(String.class));
    }

    public static boolean getCheckother(){

        return config.getNode("Check rankother add").getBoolean();
    }
    /**Config utils**/

    public static CommentedConfigurationNode getConfig(){

        return config;
    }

    public static void saveConfig(){

        try{
            SetRankMain.getInstance().getLoader().save(config);
        }catch(Exception e){
            e.printStackTrace();
        }

    }


    public static void reloadConfig() {

        try {
            config = SetRankMain.getInstance().getLoader().load();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
