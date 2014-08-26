# FluidConverters [![Build Status](https://drone.io/github.com/rubensworks/FluidConverters/status.png)](https://drone.io/github.com/rubensworks/FluidConverters/latest)

This mod allows you to configure fluid groups in JSON config files that contain a set of fluids (with some optional properties) that can be converted to each other. For each fluid group a block will be added to the game which can accept all the fluids from that group on each side, right-clicking on a side with a fluid container from that group will enable output for that fluid on that side.

## Usage

Downloads available at [CurseForge](http://minecraft.curseforge.com/mc-mods/223737-fluidconverters)

Please refer to the wiki for more advanced config options and details about each one.

When first starting the mod, the files "_aqualava.json" and "blood.json" will be created inside the "config/fluidconverters" folder.
The file "_aqualava.json" is just an example file with all the most important config options, and since it is prefixed with '_', it will not be loaded into the mod.
The "blood.json" config will be loaded into the game and handles the conversions of different types of blood added by some mods.

The "_aqualava.json" contents can be seen below.

```
{
    groupId : "aqualava",
    groupName : "Aqua Lava",
    fluidElements : [
        {
            fluidName: "lava",
            value: 1.0
        },
        {
            fluidName: "water",
            value: 2.0
        }

    ],
    lossRatio : 1.0,
    hasRecipe : true
}
```
The only mandatory fields here are 'groupId' and 'fluidElements', more about this on the wiki.

When you define your own converter, you can use any folder structure you want, as long as it resides in the "config/fluidconverters" folder and ends with ".json". (All files not ending with ".json" or with "_" as a prefix will be ignored.)
