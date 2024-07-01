# Auto Messages Plugin
The plugin was written for the server [Vanilla Survival](https://vanillasurv.ru) for the Spigot 1.21 version.

The main and only function is to send automatic messages at a specified interval.

When you run the plugin for the first time, the `AutoMessages` directory will be created in the `plugins` directory. In this directory there will be `config.yml` file, using it you can customize messages.

## Everything about setting up the plugin is written on our WIKI
- EN: https://wiki.shandy.dev/en/AutoMessages/introduction
- RU: https://wiki.shandy.dev/ru/AutoMessages/introduction

## Customize messages
Open the `config.yml` file, initially it will be like this:
```yml
# Available translations: en, ru
# If you want to translate yourself, create a file "{lang_code}.yml" and specify {lang_code} below
locale: "en"

# Setup instructions here: https://wiki.shandy.dev/en/AutoMessages/introduction
messages:
  message1:
    prefix: "[Сервер] "
    prefix-color: green
    message-lines:
      - line1:
          text: "Привет!"
          color: red
          style: bold
      - line2:
          text: "Это автоматическое сообщение."
          color: yellow
          style: italic
      - line3:
          text: "Ссылка на Discord"
          color: blue
          style: underlined
          link: "https://shandy.dev/discord"
    interval: 60
```
It will display this message once every 60 seconds (interval parameter):
![Automatic Chat Message](https://assets.shandy-dev.ru/u/8c4317fda96b49388e4c56adb4dd1636 "Automatic Chat Message")

The interval is configured in seconds.
## Commands

The only command in the plugin is `/am reload`. It reloads the config and immediately sends all messages from the config.

Here's what it looks like:

![Plugin reload](https://assets.shandy-dev.ru/u/a7141c08a5aa4d86aeaffe3420942341 "Plugin reload")

## Formatting
Formatting is possible:

| Object View  | Color | Text Style | Link | Command |
|--------------|-------|------------|------|---------|
| Prefix       | ✅     | ❌          | ❌    | ❌       |
| Message Line | ✅     | ✅          | ✅    | ✅       |

### Available colors:

- aqua
- black
- blue
- dark_aqua
- dark_blue
- dark_gray
- dark_green
- dark_green
- dark_purple
- dark_red
- gold
- gray
- green
- light_purple
- red
- white
- yellow

### Available styles:

- bold
- italic
- underlined
- strikethrough
- obfuscated

### Links

You can set a link for a message line, so that when you click on the text, you can follow it.

Example:
```yml
  message1:
    prefix: "[Сервер] "
    prefix-color: green
    message-lines:
      - line1:
          text: "Привет!"
          color: red
          style: bold
          link: "https://shandy.dev"
    interval: 60
```

### Commands

You can set a command by clicking on the text. When you click on the text, the user will be presented with a command in the input field.

Example:
```yml
  message1:
    prefix: "[Сервер] "
    prefix-color: green
    message-lines:
      - line1:
          text: "Привет!"
          color: red
          style: bold
          command: "/say Привет!"
    interval: 60
```
You cannot install a link and a command at the same time. If both of these properties for a line are specified in the config, the command will be applied.

**IMPORTANT! The command must start with `/`!**

## Using the plugin

Just drop `AutoMessages.jar` into the `plugins` folder and start the server.

Configure your `config.yml`. The command to reload your configuration is `/am reload`.

Enjoy!
