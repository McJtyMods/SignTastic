# SignTastic for Fabric 26.3

Configurable signs with text, icons, colors, backgrounds, transparency, and large text. Requires Java 25, Fabric Loader 0.19.5 or later, Fabric API 0.164.0+26.3 or later, Forge Config API Port 26.3.1 or later, and McJtyLib 11.0.0 for Fabric 26.3. The One Probe 16.0.0 for Fabric 26.3 is optional.

Build with `./gradlew build` using the Gradle 9.6.0 wrapper. Install `build/libs/signtastic-fabric-26.3-6.0.0-fabric.jar` alongside its required dependencies.

The build uses MultiWorkSpace's published `26.3_fabric/gradletools.gradle`, or the workspace root's script when included as a subproject. No local copy is needed. Standalone builds select Jenkins artifacts `mcjtylib-fabric-26.3-11.0.0-fabric.1.jar` and `theoneprobe-26.3-16.0.0-fabric-1.jar`. Their build suffixes do not raise the minimum mod versions. A workspace uses the sibling McJtyLib and TOP projects when present.

McJtyLib supplies the blocks, items, menus, widgets, attachments, network helpers, and block entity synchronization. GUI edits update the local sign while awaiting the server, and toggling Large preserves draft text. Existing sign text, settings, configurator components, and drop data are retained. The icon renderer supports classic and improved transparency.

Run development instances with `./gradlew runClient` or `./gradlew runServer`. Regenerate resources with `./gradlew runDatagen`. In MultiWorkSpace, add `-Penable_individual_runs` to enable this project's runs.

Run the server checks with `./gradlew -PportTests runGameTestServer`. They cover all sign families and orientations, saved data, client synchronization, drops, the configurator, menu capabilities, and packet codecs.

For the optional client check, copy `runs/gameTestServer/world` to `run-fabric/saves/signtastic-port-smoke` and run `./gradlew -PportTests -PclientSmoke runClient`. Complete accessibility onboarding first. This disposable scene checks item models, sign rendering, server edits, the McJtyLib editor, icon cycling, and edits surviving closing and reopening the GUI. It writes screenshots and exits automatically. Test classes and entrypoints are excluded from normal release jars.
