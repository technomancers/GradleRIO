# GradelRIO

A simple way to build and deploy your code for FRC RoboRIOs. Very simple to use and open source. This was built and tested with FRC WPILib 2016. Will update if changes are needed for 2017.

## Table Of Contents

* [Setup](#setup)
	* [Configuration](#configuration)
* [Commands](#commands)
	* [Build](#build)
	* [Debug](#debug)
	* [Restart](#restart)
	* [Reboot](#reboot)
	* [Clean](#clean)
* [Finding the RoboRIO](#finding-the-roborio)
* [Contributing](#contributing)

## Setup

You will need to create a file named `build.gradle`. The following is an example of the file.

```groovy
//This imports the GradleRIO plugin to use for this project
plugins {
	id "io.github.technomancers.gradlerio" version "0.1.2"
}
//Includes useful tasks to allow you to create an environment that Eclipse can use.
apply plugin: 'eclipse'
//Includes useful tasks to allow you to create an environment that IntelliJ IDEA can use.
apply plugin: 'idea'
description 'Build and Deploy to the RoboRIO with WPI\'s FRC libraries'
//GradleRIO configurations go inside this closure (code block).
gradlerio{
	teamNumber '0000'
}
```

The only things that are needed for GradleRIO to run is the first three lines and the last three lines. The rest is useful for different environments. The only configuration GradleRIO needs is `teamNumber` as it is the only value that cannot be infered.

### Configuration

The following values can be placed in the `gradlerio` closure shown above. Most teams can use the defaults for all the configurations. The only needed configuration is `teamNumber`. If you need another configuration submit and issue on this project's github repository.

<dl>
	<dt><strong>teamNumber</strong> (REQUIRED)</dt>
	<dd>Your team's number</dd>
	<dt><strong>rioIP</strong> (DEFAULT 10.&lt;first_2_digits_of_team_number&gt;.&lt;last_2_digits_of_team_number&gt;.2)</dt>
	<dd>The IP address to your RoboRIO. This can be infered from the team number. Read the Finding the RoboRIO section for more information.</dd>
	<dt><strong>rioHostName</strong> (DEFAULT roboRIO-&lt;team_number&gt;-frc.local)</dt>
	<dd>The host name for your RoboRIO. This can be infered from the team number. Read the Finding the RoboRIO section for more information.</dd>
	<dt><strong>robotClass</strong> (DEFAULT org.usfirst.frc.team&lt;team_number&gt;.Robot)</dt>
	<dd>The class location for your RoboRIO. This can be infered from the team number. Change if needed.</dd>
	<dt><strong>mainClass</strong> (DEFAULT edu.wpi.first.wpilibj.RobotBase)</dt>
	<dd>The class of the RobotBase FRC uses. Don't change unless you know what this will do.</dd>
	<dt><strong>jarFileName</strong> (DEFAULT FRCUserProgram)</dt>
	<dd>This is the name of the JAR file created. Changing this will change appropriate values else where. Recommended to keep as is.</dd>
	<dt><strong>wpiVersion</strong> (DEFAULT +)</dt>
	<dd>The version of WPILib to use. "+" means to use the latest available on the maven server.</dd>
	<dt><strong>ntVersion</strong> (DEFAULT +)</dt>
	<dd>The version of Network Tables to use. "+" means to use the latest available on the maven server.</dd>
	<dt><strong>wpiBranch</strong> (DEFAULT release)</dt>
	<dd>Which branch to use in maven to find the libraries on. Recommended to use release but you are welcome to change to a supported branch found <a href="http://wpilib.screenstepslive.com/s/4485/m/wpilib_source/l/480976-maven-artifacts">here</a>.</dd>
	<dt><strong>robotUser</strong> (DEFAULT lvuser)</dt>
	<dd>The username used to log into your RoboRIO.</dd>
	<dt><strong>robotElevatedUser</strong> (DEFAULT admin)</dt>
	<dd>The username of the elevated user permission used for only certain tasks (like rebooting the RoboRIO).</dd>
	<dt><strong>robotNIGroup</strong> (DEFAULT ni)</dt>
	<dd>National Instruments user group the RoboRIO uses. Don't change unless you know what this will do.</dd>
	<dt><strong>robotPass</strong> (DEAFULT &lt;empty_string&gt;)</dt>
	<dd>The password used for robotUser.</dd>
	<dt><strong>robotPass</strong> (DEAFULT &lt;empty_string&gt;)</dt>
	<dd>The password used for robotElevatedUser.</dd>
	<dt><strong>netConsoleHostLocation</strong> (DEAFULT /usr/local/frc/bin)</dt>
	<dd>The location to find the netconsole-host command on the RoboRIO.</dd>
	<dt><strong>javaLocation</strong> (DEFAULT /usr/local/frc/JRE/bin)</dt>
	<dd>The location to find the java command on the RoboRIO.</dd>
	<dt><strong>ldLibraryPath</strong> (DEFAULT /usr/local/frc/rpath-lib/)</dt>
	<dd>Paths to include that your code needs to run. Don't change unless you know what this will do.</dd>
	<dt><strong>debugArgs</strong> (DEFAULT -XX:+UsePerfData -agentlib:jdwp=transport=dt_socket,address=5910,server=y,suspend=y)</dt>
	<dd>The debug args to include in the command file when running in debug mode.</dd>
	<dt><strong>robotCommand</strong> (DEAFULT env LD_LIBRARY_PATH=&lt;ldLibraryPath&gt; &lt;netConsoleHostLocation&gt;/netconsole-host &lt;javaLocation&gt;/java -jar &lt;deployDir&gt;&lt;jarFileName&gt;.jar)</dt>
	<dd>The command that is ran when the RoboRIO is starting to run your program.</dd>
	<dt><strong>robotDebugCommand</strong> (DEAFULT env LD_LIBRARY_PATH=&lt;ldLibraryPath&gt; &lt;netConsoleHostLocation&gt;/netconsole-host &lt;javaLocation&gt;/java &lt;debugArgs&gt; -jar &lt;deployDir&gt;&lt;jarFileName&gt;.jar)</dt>
	<dd>The command that is ran when the RoboRIO is starting to run your program in debug mode.</dd>
	<dt><strong>robotCommandFile</strong> (DEFAULT robotCommand)</dt>
	<dd>The name of the robot command file. Don't change unless you know what this will do.</dd>
	<dt><strong>robotDebugCommandFile</strong> (DEFAULT robotDebugCommand)</dt>
	<dd>The name of the robot debug command file. Don't change unless you know what this will do.</dd>
	<dt><strong>frcDebugFile</strong> (DEFAULT frcdebug)</dt>
	<dd>The name of the frc debug file. This file is used as a flag by the RoboRIO to tell it to run in debug mode. Don't change unless you know what this will do.</dd>
	<dt><strong>frcDebugDir</strong> (DEAFULT /tmp/)</dt>
	<dd>The location to put the frc debug file. Don't change unless you know what this will do.</dd>
	<dt><strong>finalCommand</strong> (DEAFULT sync)</dt>
	<dd>The command that is run after RoboRIO goes through a restart cycle (using this tool only). Recommended to keep the "sync" command. Add a simicolon to the end and add your command.</dd>
	<dt><strong>deployDir</strong> (DEAFULT /home/&lt;robotUser&gt;/)</dt>
	<dd>The directory to deploy your code to on the RoboRIO.</dd>
	<dt><strong>commandDeployDir</strong> (DEAFULT /home/&lt;robotUser&gt;/)</dt>
	<dd>The directory to deploy the command file to.</dd>
	<dt><strong>killNetConsoleCommand</strong> (DEFAULT killall -q netconsole-host || :)</dt>
	<dd>The command used to kill netconsole-host.</dd>
	<dt><strong>robotKillCommand</strong> (DEFAULT . /etc/profile.d/natinst-path.sh; /usr/local/frc/bin/frcKillRobot.sh -t -r)</dt>
	<dd>The command that will restart the RoboRIO and re-run the code. Don't change unless you know what this will do.</dd>
	<dt><strong>dryRun</strong> (DEFAULT false)</dt>
	<dd>This is used for testing purposes. This will act like it is calling the commands to communicate with the RoboRIO but not actually execute them.</dd>
	<dt><strong>timeout</strong> (DEFAULT 5)</dt>
	<dd>The timeout to use when making calls to the RoboRIO.</dd>
</dl>

## Commands

This project is built with gradle and the gradle wrapper. The gradle wrapper allows someone who does not have gradle installed still able to run the build process. It will download and use the proper version of gradle that this project uses (Currently 3.3).

To run the gradle wrapper, it is machine specific. For Windows users, simply run `.\gradlew.bat`. For Unix type operating systems (MacOS, Linux), run `./gradlew`.

> **NOTE**: You may need to run `chmod +x gradlew` on Unix operating systems to be able to run the script.

### Build

Build simply builds the appropriate jar. This does not deploy to the RoboRIO.

`gradlew build`

### Deploy

This deploys the compiled version of your code to the RoboRIO. If an update to the build is needed it will run build. It also restarts the RoboRIO to run your code.

`gradlew deploy`

### Debug

This deploys the compiled version of your code to the RoboRIO. If and update to the build is needed it will run build. It also restarts the RoboRIO in debug mode and runs your code in debug mode.

`gradlew debug`

### Restart

This will only run the commands to restart the RoboRIO to re-run the code (does not reboot the RoboRIO).

`gradlew restart`

### Reboot

This will shutdown and reboot the RoboRIO.

`gradlew reboot`

### Clean

This is used to clean you directory of any compiled code. This is usefull if you want to force a rebuild of you code. Shouldn't need to run this as gradle is smart about when it should run the build.

`gradlew clean`

## Finding the RoboRIO

This plugin trys to connect to the RoboRIO with the following values. It uses the first available in this order.

1. `rioHostName`
2. `rioIP`
3. `172.22.11.2`

## Contributing

To contribute to this code. Please fork it and make your changes then make a pull request. To eleminate wasted time it is recommended to first create a github issue with your idea. We can discuss the idea there. This also allows for other users to see what is currently being worked on.
