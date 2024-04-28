# Auto Messages Plugin
The plugin was written for the server [Vanilla Survival](https://vanillasurv.ru) for the Spigot 1.20.1 version.

The main and only function is to send automatic messages at a specified interval.

When you run the plugin for the first time, the `AutoMessages` directory will be created in the `plugins` directory. In this directory there will be `config.yml` file, using it you can customize messages.

## Customize messages
Open the `config.yml` file, initially it will be like this:
```yml
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
          style: underline
          link: "https://shandydev.com/dicord"
    interval: 60

```
It will display this message once every 60 seconds (interval parameter):
![Automatic Chat Message](https://assets.shandy-dev.ru/u/8c4317fda96b49388e4c56adb4dd1636 "Automatic Chat Message")

## Commands

The only command in the plugin is `/am reload`. It reloads the config and immediately sends all messages from the config.

Here's what it looks like:

![Plugin reload](https://assets.shandy-dev.ru/u/a7141c08a5aa4d86aeaffe3420942341 "Plugin reload")

## Formatting
Formatting is possible:

| Object View  | Color | Text Style | Link |
|--------------|-------|------------|------|
| Prefix       | ✅     | ❌          | ❌    |
| Message Line | ✅     | ✅          | ✅    | ✅ |

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

## Using the plugin

Just drop `AutoMessages.jar` into the `plugins` folder and start the server.

Configure your `config.yml`. The command to reload your configuration is `/am reload`.

Enjoy!

Translated with DeepL.com (free version)