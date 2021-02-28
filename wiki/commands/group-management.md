# Group Management Commands

These commands are used to manage groups using files. This is to create, delete or change stuff about commands
in a faster way, this may get removed when there's a web-page to add the permissions directly there

> Remember to use the prefix! Usually a dot "." or a minus "-"

* **Name:** groupManager
* **Aliases:** gm

The command alone does not do much, it does have subcommands which may be used for the actions.

## Subcommands

* [list](#gm-list)
* [save [group]](#gm-save-group)
* [load [file-name]](#gm-load-file-name)
* [new](#gm-new)

### gm list

This command will list all groups with its respective id, name and weight

### gm save [group]

This command will save the group to a file "groups/<group-name>.json" which you can edit to later
load it and override the group.

The parameter group may be the id or name of the group.

### gm load [file-name]

This command will load the file as a group and write it to memory, if the id in the file matches another group
it will be **overridden**.

The parameter file-name must be the exact tame that the file has inside the directory "groups" please
do not use name with spaces as the command does not accept those.

## gm new

This will create a file with a new id to use for a group which can be edited for a new group.