<center>
  <img src="https://cdn.modrinth.com/data/cached_images/efac58e3820179642c8a548e0d264650c955b0de.png">
<br><br><br>

In 25w15a, Mojang released the Locator Bar, which can show waypoints. Currently, only entities can be waypoints with players showing by default.

This mod extends the `/waypoint` command to allow adding/modifying/removing of "static" waypoints.

<br>
  <img src="https://cdn.modrinth.com/data/cached_images/efac58e3820179642c8a548e0d264650c955b0de.png">
</center>
<br><br>

### Commands

- List all waypoints
    - `/waypoint list`
- Add waypoint
    - `/waypoint static add <id> <x y z> [color <color>] [style <style>] [range <range>]`
    - color/style/range are all optional, and order doesn't matter
- Modify waypoint
    - `/waypoint static modify <id> location <x y z>`
    - `/waypoint static modify <id> color <color>`
    - `/waypoint static modify <id> color hex <color>`
    - `/waypoint static modify <id> style reset`
    - `/waypoint static modify <id> style set <style>`
    - `/waypoint static modify <id> range <range>`
- Remove waypoint
    - `/waypoint static remove <id>`