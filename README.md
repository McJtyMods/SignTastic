# SignTastic

Configurable signs and a sign configurator.

This branch targets Minecraft 26.3, NeoForge 26.3.0.83, and Java 25.
McJtyLib is required. The One Probe and JEI are optional integrations.

The build uses [MultiWorkspace's published 26.3 script](https://github.com/McJtyMods/MultiWorkspace/blob/26.3_neo/gradletools.gradle).
It resolves McJtyLib `26.3-11.0.0.3` and TOP `26.3_neo-16.0.0-1`;
McJtyLib's minimum mod dependency remains `26.3-11.0.0`.

Build with `./gradlew build` and regenerate resources with `./gradlew runClientData`.
Run the opt-in server port checks with `./gradlew -PportTests runGameTestServer`.
Interactive client checks are available through `./gradlew -PportTests -PclientSmoke runClient`.
The server checks cover sign save/sync, drops/configurator behavior, live edit packet sync, and packet codecs.
The client checks also cover rapid text/size changes and settings surviving closing and reopening the editor.
