# Server & Bot setup

```bash
mkdir test_network
cd test_network
git clone git@github.com:GoogasDev/PGM-Trim.git bukkit

```

## In case ownership is lost

```bash
sudo chown -R $USER:$USER <dir>
```

## Current bugs

- [ ] Linking shows null instead of the actual Minecraft name
```txt
You've linked your account to: null
```

- [ ] Commands do not have correctly replaced placeholders
```txt
Here's some help for you to complete the command correctly, you can also use .help: 
 You are missing the argument %name% in the position %position%. 
 What is it? 
 %description%
```