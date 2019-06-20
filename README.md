# SetRankPlugin
Plugin provide permissions and commands for setting rank to players 

# TODO
your chat main.config (for ex nucleus) need to be like this:

 #config.chat.template.prefix
 prefix="{{o:prefix:s}}{{o:rank:s}}{{displayname}}{{o:suffix:p}}&f: "
 
 DONT forget to add {{o:rank:s}} !

# Commands:
rankset - alias: setrank, permission: setrank.commands.set, discription: Sets rank to the player.

ranksetother - alias: setrankother, permission: setrank.commands.setother, discription: Sets rank to other player.

removerank - alias: rankremove, permission: setrank.commands.remove, discription: Remove rank from player.

removerankother - alias: rankremoveother, permission: setrank.commands.removeother, discription: Remove rank from other player.

# Config: 
args length - Determines length of rank, to prevent super big ranks like 'LOOOOOOOOOOOOOOOOL' etc. Default value 6.
