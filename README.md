# SetRankPlugin
Plugin can provide your server with permissions and commands which will enable you to set ranks to players.

# TODO
your chat main.config (for ex nucleus) need to be like this:

 #config.chat.template.prefix
 prefix="{{o:prefix:s}}{{o:rank:s}}{{displayname}}{{o:suffix:p}}&f: "
 
 DONT forget to add {{o:rank:s}} !

# Commands:

Alias for rank: "r" or "rank".

rank add <rank> - permission: setrank.commands.add, discription: Adds rank.

rank remove - permission: setrank.commands.remove, discription: Removes rank.

rank reload - permission: setrank.admin.reload, discription: Reloads config file. //working

Alias for rankother: "ro" or "ran

rankother add <player> <rank> - permission: setrank.admin.add, discription: Adds rank to other player.

rankother remove <player> - permission: setrank.admin.remove, discription: Remove a rank from other player.

# Config: 
Rank character length - Determines length of rank, to prevent super big ranks like 'LOOOOOOOOOOOOOOOOL' etc. Default value 6.

Latin Letters check - If player have at least one non latin letter in rank, he will get error. Default false

Check black list, rank lengt and latin letters for "rankother add" command - Default false.

"Black list"=[
    Admin,
    Moder
]

# ChangeLog 
22.06.2019

Added smart black list in which you can add commands and it doesn’t matter how the word will be spelled the blacklisted word won’t be able for use to casual players.

Batter check for rank length, excluding color codes. Before &3Test.length() == 6, now length() == 4. 

Permission name change.

2 commands instead 4, added child to 2 basic commands.

23.06.2019
Permissions fixes, added latin letters check. Code changes. 

16.07.2019
Reload command and sponge plugins reload now working. Completely recoded some parts of plugin, instead of having 1 heavy main.class i split it into smaller classes. 
