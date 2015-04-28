Shiny Notifier
=========

What is It?
------------
Shiny Notifier is a way for server administrators to track when shiny Pixelmon and other Pixelmon of interest have been captured. It provides notifications on capture and several ways to check who has captured Pixelmon of interest to you. Check out the commands section for details on to use it. You will need pex or some other way to grant the appropriate permissions to users you want to have access to this toolkit.

Installation and Setup
------------
1. Drop the JAR in your mods folder.
2. Edit the config file with additional Pixelmon you want to watch. It's filled with the 13 legendary Pixelmon by default. The file should be in your config folder and called shinynotifier.cfg.

Commands
------------
- **gscheck**
        * **description**: Check the captured shinies/watched of a player
        * **usage**: /<command> [player]
        * **permission required**: net.pixellife.shinynotifier.GSCheckCommand
- **gstop**
        * **description**: Check the top N players in M days by how many shinies/watched they've captured
        * **usage**: /<command> \[top\] \[days\]
        * **permission required**: net.pixellife.shinynotifier.GSTopCommand
- **gspurge**
        * **description**: Purge the record of a given player. This removes all data about shinies/watched Pixelmon the player has captured.
        * **usage**: /<command> [player]
        * **permission required**: net.pixellife.shinynotifier.GSPurgeCommand
- **gsreload**
        * **description**: Reload configuration file for ShinyNotifier without restarting the server. Useful when you need to add a new watched Pixelmon.
        * **usage**: /<command>
        * **permission required**: net.pixellife.shinynotifier.GSReloadCommand

Permissions
-------------
- ShinyNotifier.receive:
        * Receive notifications from the mod on shiny/watched capture
- net.pixellife.shinynotifier.GSCheckCommand:
        * Allows you to check the captured shinies/watched of a player - permits the /gscheck command
- net.pixellife.shinynotifier.GSTopCommand:
        * Allows you to check the top N players in M days by how many shinies/watched they've captured - permits the /gstop command
- net.pixellife.shinynotifier.GSPurgeCommand:
        * Allows you to purge the record of a given player - permits the /gspurge command
- net.pixellife.shinynotifier.GSReloadCommand:
        * Allows you to reload ShinyNotifier's configuration file - permits the /gsreload command