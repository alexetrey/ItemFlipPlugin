# ItemFlipPlugin

A Minecraft plugin that allows players to gamble their items in a coin flip style game. Players can stake any item and challenge others to a 50/50 chance of winning both items.

## Features

- User-friendly GUI interface for item selection
- Secure item handling system
- Fair 50/50 chance system
- Command system with tab completion
- Configurable messages and settings
- Permission-based access control
- Sound and particle effects
- Anti-exploit measures

## Commands

- `/itemflip create` - Opens the GUI to create a new flip game
- `/itemflip join <player>` - Join another player's flip game
- `/itemflip list` - List all active flip games
- `/itemflip cancel` - Cancel your active game
- `/itemflip help` - Show help message

## Permissions

- `itemflip.use` - Allows players to use the basic plugin features (default: true)
- `itemflip.admin` - Allows access to administrative commands (default: op)

## Configuration

The plugin is highly configurable through the `config.yml` file. You can customize:

- Messages and prefix
- Game settings (flip delay, broadcasting)
- GUI appearance
- Sound and particle effects

## Installation

1. Download the latest release
2. Place the JAR file in your server's `plugins` folder
3. Restart your server
4. Configure the plugin in `plugins/ItemFlipPlugin/config.yml`

## Building from Source

This plugin uses Maven for dependency management and building.

```bash
git clone https://github.com/yourusername/ItemFlipPlugin.git
cd ItemFlipPlugin
mvn clean package
```

The compiled JAR will be in the `target` directory.

## Requirements

- Java 21 or higher
- Spigot/Paper 1.21+ server

## Contributing

Feel free to submit issues and pull requests to help improve the plugin.

## License

This project is licensed under the MIT License - see the LICENSE file for details. 