 <div align="center">
  <img src="./hNFC.svg" alt="h-NFC logo" width="180">
</div>

# h-NFC

<div align="center">

**Near Field Communication · Smart Automation**

A mobile application designed to configure NFC tags and turn simple interactions into automated actions.

</div>

---

## Overview

**h-NFC** is an Android mobile application that leverages Near Field Communication (NFC) technology to simplify everyday tasks through programmable NFC tags.

The idea is simple: configure an NFC tag to perform a specific task, then trigger that task by bringing your smartphone close to the tag.

Instead of manually navigating through applications or repeating the same operations, h-NFC aims to provide a centralized environment for configuring NFC-based interactions.

## Concept

NFC tags can act as physical triggers for digital actions. By associating a tag with a specific task, everyday interactions can become faster and more intuitive.

Some potential applications include:

- **Smart Home:** Trigger home automation routines.
- **Productivity:** Start predefined workflows or routines.
- **Connectivity:** Simplify access to network configurations and digital resources.
- **Personal Automation:** Associate physical locations or objects with specific actions.
- **Custom Tasks:** Configure tags for different personal use cases.

## How it works

The intended workflow is based on three steps:

1. **Configure:** Select the task you want to associate with an NFC tag.
2. **Program:** Bring an NFC tag close to your smartphone to associate the configuration with it.
3. **Execute:** Scan the configured tag to trigger its associated action.

## Project Structure

The project follows an Android application structure, with its implementation organized under the `app/` directory.

```text
h-NFC/
├── app/
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
├── LICENSE
└── README.md
```

## Requirements

- An Android smartphone with NFC hardware.
- NFC functionality enabled on the device.
- Compatible NFC tags.
- Android Studio for development.

## Getting Started

Clone the repository:

```bash
git clone https://github.com/titooDiaz/h-NFC.git
```

Navigate to the project directory:

```bash
cd h-NFC
```

Open the project in Android Studio, allow Gradle to synchronize the dependencies, and run the application on an NFC-compatible Android device.

## Development

h-NFC is an ongoing project focused on exploring NFC technology and its potential for everyday automation.

The objective is to build a flexible environment where users can configure different NFC tags according to their needs, without requiring specialized technical knowledge.

## License

This project is distributed under the MIT License. See the [LICENSE](./LICENSE) file for more information.

---

<div align="center">

**h-NFC — Turning physical interactions into digital actions.**

</div>