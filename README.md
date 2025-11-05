# Restaurant Desktop App (Java Swing)

This repository contains a small Java Swing desktop application for managing a restaurant's menu, orders and basic reports. The app uses simple file-based data storage located in the `restaurant_data/` folder and is built with Maven.

## Quick overview

- Language: Java 11
- UI: Swing
- Build tool: Maven
- Main class: `restaurant.gui.MainFrame`
- Simple file-based data storage: `restaurant_data/` (contains `menu.txt`, `orders.txt`, `users.txt`, `customer_users.txt`)

## Features

- Login dialog and simple user management (file-backed)
- Create new orders and view current orders
- Menu management (add / edit / remove menu items)
- Basic reports panel

## Project structure (important files)

- `pom.xml` - Maven project file
- `src/main/java/restaurant/gui/MainFrame.java` - application entry point and UI container
- `src/main/java/restaurant/gui/*` - Swing UI panels and dialogs
- `src/main/java/restaurant/database/DatabaseManager.java` - simple file-backed persistence helpers
- `restaurant_data/` - data files used at runtime (menu, orders, users)

## Prerequisites

- Java 11 (or compatible JDK)
- Maven 3.x

Note: The project is developed and tested on Linux. It should run on macOS and Windows provided Java and Maven are installed.

## Build

From the project root (where `pom.xml` is located), run:

```bash
mvn -q -DskipTests package
```

This compiles the project and creates the artifact under `target/`.

## Run

The application is a standard Swing desktop application with `restaurant.gui.MainFrame` as the entry point. You can run it from Maven or directly with the `java` command.

Run from Maven:

```bash
mvn -q exec:java -Dexec.mainClass="restaurant.gui.MainFrame"
```

Or run the packaged jar (if you created an executable jar — this project currently does not configure the Maven assembly/plugin to create an executable jar by default):

```bash
# If you configure an executable jar, you can run it like:
java -cp target/classes:target/dependency/* restaurant.gui.MainFrame
```

If your shell is bash on Linux, use `:` as classpath separator; on Windows use `;`.

## User app vs Admin app


This repository actually contains two closely related desktop front-ends:

- User / Customer app — intended for customers (simpler UI):
  - Entry point: `restaurant.customerGUI.CustomerMain`
  - Source: `src/main/java/restaurant/customerGUI/CustomerMain.java`
  - Designed for placing orders from a customer perspective and viewing customer-specific data.

- Admin / Manager app — full management UI used by staff and administrators:
  - Entry point: `restaurant.gui.MainFrame`
  - Source: `src/main/java/restaurant/gui/MainFrame.java`
  - Contains menu management, full order view, and reports panels.

Run either app from Maven by specifying the main class. For example, to run the customer (user) app:

```bash
mvn -q exec:java -Dexec.mainClass="restaurant.customerGUI.CustomerMain"
```

And to run the admin/manager app (default documented above):

```bash
mvn -q exec:java -Dexec.mainClass="restaurant.gui.MainFrame"
```

If you prefer running directly with `java`, use the fully-qualified main class name on the classpath. Example (Linux/macOS):

```bash
java -cp target/classes:target/dependency/* restaurant.customerGUI.CustomerMain
```

Adjust the class name for the admin app if needed.

## Data files and initialization

The application reads/writes simple text files in the `restaurant_data/` folder located at the project root. When running from your IDE the working directory should be the project root so the files are found correctly.

Files of interest:

- `restaurant_data/menu.txt` - menu items
- `restaurant_data/orders.txt` - saved orders
- `restaurant_data/users.txt` - admin/user credentials
- `restaurant_data/customer_users.txt` - customer-specific users

Back up these files before making manual edits. The project does not use a database server; instead, `DatabaseManager` contains helper methods to load and save these files.

## Development notes

- The UI is implemented with Swing and programmatic layout. Look at the `src/main/java/restaurant/gui` package to change UI behavior.
- Persistence is intentionally simple — ideal for demos and learning, not production.
- If you want to create an executable jar, add the Maven Shade or Assembly plugin and specify `restaurant.gui.MainFrame` as the main class.

## Tests and linting

There are no automated tests included by default. Adding unit tests for `DatabaseManager` and key model classes is recommended.

## Troubleshooting

- If the app cannot find data files, ensure you run it with the project root as your working directory or update the file paths in `DatabaseManager`.
- If you get a Swing look-and-feel error, ensure a compatible LAF is installed; the app falls back to the default if setting the system look-and-feel fails.

## License

This repository does not include a license file. Add one (for example `LICENSE`) if you plan to publish the code.

---

If you'd like, I can:

- add a proper Maven exec plugin configuration to `pom.xml` to simplify `mvn exec:java` runs,
- create an executable shaded jar via the Maven Shade plugin,
- or add a short CONTRIBUTING.md with developer setup steps.
