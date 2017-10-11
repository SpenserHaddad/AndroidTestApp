# AndroidTestApp

A modification of the [Firebase FriendlyPix example application for Android](https://github.com/firebase/friendlypix-android).

Supports the same picture upload/comment/like system from the app, with some slight tweaks to make the code clearer
and more concise (side effect of trying to learn the code).

## Additions (in progress)

The current goal is to replace the existing functionality with a [play-by-post](https://en.wikipedia.org/wiki/Play-by-post_role-playing_game)
application, backed by Google's new Firestore database. In the end, the app will be able to:

* Users can create new games (similar to chat rooms or forum threads) with a group of players. The creator will become the "owner" of the game.
* More players can be added after the game is created.
* Players and the owner (game leader) can post messages for their current actions, and embed dice rolls into their actions that will be
computed when the message is completed. 
** Dice rolls will emulate the "d20" system. For example: "1d20 2d6 + 4" will roll one twenty-sided die, two six-sided dice, 
then and add four to the sum of their results.
* Users will be able to individually browse the games they own and the games they are players in.

Of course, several more features could be added in the futue to further enhance the experience.

## Support

This app is built for support for Android Versions 5.1 through 8 (Lollipop through Oreo), and prioritizes version 8.

This app was tested on Android version 7.0 (Nougat), using a Nexus 5X (emulated) and a Galaxy S7.
