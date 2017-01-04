package io.github.technomancers

class GradleRIOExtension{
    private final static String robotClassTemplate = 'org.usfirst.frc.team%s.Robot'
    private final static String hostNameTemplate = 'roboRIO-%s-frc.local'
    private final static String robotCommandTemplate = 'env LD_LIBRARY_PATH=%s %s/netconsole-host %s/java %s -jar /home/%s/%s.jar'
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
    String wpiBranch = 'release'
    String robotUser = 'lvuser'
    String robotPass
    String robotCommand
    String robotDebugCommand
    String netConsoleHostLocation = '/usr/local/frc/bin'
    String javaLocation = '/usr/local/frc/JRE/bin'
    String ldLibraryPath = '/usr/local/frc/rpath-lib/'
    String debugArgs = '-XX:+UsePerfData -agentlib:jdwp=transport=dt_socket,address=5910,server=y,suspend=y'
    String robotCommandFile
    String robotDebugCommandFile 

    void setTeamNumber(String team){
        teamNumber = team == null ? '' : team
        def len = teamNumber == '' ? 0 : teamNumber.length()
        if (len < 4){
            for (def i = 0; i < 4 - len; i++){
                teamNumber = '0' + teamNumber
            }
        }
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

    void setNetConsoleHostLocation(String path){
        netConsoleHostLocation = cleanPath(path)
    }

    void setJavaLocation(String path){
        javaLocation = cleanPath(path)
    }

    String getRobotCommand(){
        if (!isNullOrEmpty(robotCommand)){
            return robotCommand
        }
        return String.format(robotCommandTemplate, ldLibraryPath, netConsoleHostLocation, javaLocation, '', robotUser, jarFileName)
    }

    String getRobotDebugCommand(){
        if (!isNullOrEmpty(robotDebugCommand)){
            return robotCommand
        }
        return String.format(robotCommandTemplate, ldLibraryPath, netConsoleHostLocation, javaLocation, debugArgs, robotUser, jarFileName)
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

    private static String cleanPath(String path){
        def last = path.substring(path.length() - 1)
        if (last == '/' || last == '\\'){
            return path.substring(0, path.length() - 2)
        }
        return path
    }

    private static Boolean isNullOrEmpty(String v){
        return (v == null || v == '')
    }
}