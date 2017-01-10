package io.github.technomancers

class GradleRIOExtension{
	private final static String robotClassTemplate = 'org.usfirst.frc.team%s.Robot'
	private final static String hostNameTemplate = 'roboRIO-%s-frc.local'
	private final static String robotCommandTemplate = '%snetconsole-host %sjava -Djava.library.path=%s -jar %s%s%s.jar'
	private final static String robotCommandFileTemplate = 'robot%sCommand'
	private final static String robotDebugCommandFilePartial = 'Debug'
	final static String rioStaticIP = '172.22.11.2'

	String teamNumber = '0000'
	String rioIP
	String rioHostName
	String robotClass
	String mainClass = 'edu.wpi.first.wpilibj.RobotBase'
	String jarFileName = 'FRCUserProgram'
	String wpiVersion = '+'
	String ntVersion = '+'
	String opencvVersion = '+'
	String cscoreVersion = '+'
	String wpiBranch = 'release'
	String robotUser = 'lvuser'
	String robotElevatedUser = 'admin'
	String robotNIGroup = 'ni'
	String robotPass
	String robotElevatedPass
	String robotCommand
	String robotDebugCommand
	String netConsoleHostLocation = '/usr/local/frc/bin/'
	String javaLocation = '/usr/local/frc/JRE/bin/'
	String ldLibraryPath = '/usr/local/frc/lib/'
	String debugArgs = '-XX:+UsePerfData -agentlib:jdwp=transport=dt_socket,address=8348,server=y,suspend=y'
	String robotCommandFile
	String robotDebugCommandFile 
	String frcDebugFile = 'frcdebug'
	String frcDebugDir = '/tmp/'
	String finalCommand = 'sync'
	String deployDir
	String commandDeployDir
	String killNetConsoleCommand = 'killall -q netconsole-host || :'
	String robotKillCommand = '. /etc/profile.d/natinst-path.sh; /usr/local/frc/bin/frcKillRobot.sh -t -r'
	boolean dryRun = false
	int timeout = 5 

	void setTeamNumber(String team){
		teamNumber = team == null ? '' : team
		def len = teamNumber == '' ? 0 : teamNumber.length()
		if (len < 4){
			for (def i = 0; i < 4 - len; i++){
				teamNumber = '0' + teamNumber
			}
		}
	}

	void setFrcDebugDir(String frc){
		frcDebugDir = dirPath(frc)
	}

	String getFinalComman(){
		if (finalCommand == null){
			return ''
		}
		return finalCommand
	}

	String getDebugArgs(){
		if (debugArgs == null){
			return ''
		}
		return debugArgs
	}

	String getRobotClass(){
		if (!isNullOrEmpty(robotClass)){
			return robotClass
		}
		return String.format(robotClassTemplate, getTeamNumber())
	}

	String getRioIP(){
		if (!isNullOrEmpty(rioIP)){
			return rioIP
		}
		return String.format('10.%s.%s.2', getTeamNumber().substring(0,2), getTeamNumber().substring(2,4))
	}

	String getRioHostName(){
		if (!isNullOrEmpty(rioHostName)){
			return rioHostName
		}
		return String.format(hostNameTemplate, getTeamNumber())
	}

	String getRobotPass(){
		if (robotPass != null){
			return robotPass
		}
		robotPass = ''
		return robotPass
	}

	String getRobotElevatedPass(){
		if (robotElevatedPass != null){
			return robotElevatedPass
		}
		robotElevatedPass = ''
		return robotElevatedPass
	}

	void setNetConsoleHostLocation(String path){
		netConsoleHostLocation = dirPath(path)
	}

	void setJavaLocation(String path){
		javaLocation = dirPath(path)
	}

	void setLdLibraryPath(String path){
		ldLibraryPath = dirPath(path)
	}

	void setJarFileName(String file){
		f = file.split('.')
		if (f.size() > 1 && f.getAt(f.size() - 1)){
			jarFileName = f.take(f.size() -1).join('.')
		}else{
			jarFileName = file
		}
	}

	String getRobotCommand(){
		if (!isNullOrEmpty(robotCommand)){
			return robotCommand
		}
		return String.format(robotCommandTemplate, netConsoleHostLocation, javaLocation, ldLibraryPath, '', getDeployDir(), jarFileName)
	}

	String getRobotDebugCommand(){
		if (!isNullOrEmpty(robotDebugCommand)){
			return robotCommand
		}
		return String.format(robotCommandTemplate, netConsoleHostLocation, javaLocation, ldLibraryPath, debugArgs + ' ', getDeployDir(), jarFileName)
	}

	String getRobotCommandFile(){
		if (!isNullOrEmpty(robotCommandFile)){
			return robotCommandFile
		}
		return String.format(robotCommandFileTemplate, '')
	}

	String getRobotDebugCommandFile(){
		if (!isNullOrEmpty(robotDebugCommandFile)){
			return robotDebugCommandFile
		}
		return String.format(robotCommandFileTemplate, robotDebugCommandFilePartial)
	}

	void setDeployDir(String dir){
		deployDir = dirPath(dir)
	}

	String getDeployDir(){
		if (!isNullOrEmpty(deployDir)){
			return deployDir
		}
		return "/home/${robotUser}/"
	}

	void setCommandDeployDir(String dir){
		deployDir = dirPath(dir)
	}

	String getCommandDeployDir(){
		if (!isNullOrEmpty(commandDeployDir)){
			return commandDeployDir
		}
		return "/home/${robotUser}/"
	}

	int getTimeout(){
		if (timeout == null || timeout == 0){
			return 1
		}
		return timeout
	}

	boolean getDryRun(){
		if (dryRun == null){
			return false
		}
		return dryRun
	}

	private static String dirPath(String path){
		def last = path.substring(path.length() - 1)
		if (last != '/' && last != '\\'){
			return "${path}/"
		}
		return path
	}

	private static Boolean isNullOrEmpty(String v){
		return (v == null || v == '')
	}
}
