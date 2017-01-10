package io.github.technomancers

import org.gradle.api.*
import groovy.util.*
import org.hidetake.groovy.ssh.Ssh

class DeployTask extends RioTask{
	private final static String restart = 'restart'
	private final static String deployDep = 'deployDep'
	private final static String debug = 'debug'
	String type = 'deploy'
	@tasks.TaskAction
	void deploy() {
		sanityCheck()
		switch(type){
			case restart:
				restartCommands()
				break;
			case deployDep:
				getLibraries()
				scpLibraries()
				scpNetConsoleHost()
				break;
			default:
				getLibraries()
				makeFiles()
				scpLibraries()
				scpNetConsoleHost()
				scpFiles()
				restartCommands()
				break;
			}
	}

	String getFileName(){
		switch (type) {
			case debug: 
				return project.gradlerio.robotDebugCommandFile;
				break;
			default: 
				return project.gradlerio.robotCommandFile;
				break;
		}
	}

	String getDebugFileName(){
		return project.gradlerio.frcDebugFile;
	}

	private void sanityCheck(){
		project.logger.info('Checking to see if the RoboRIO is compatable.')
		//TODO: make a post call to the RoboRIO and ask for information then assert on values
		//https://github.com/technomancers/archive2016/blob/master/java/current/ant/build.xml#L201
		ssh.run{
			session(ssh.remotes.rio){
				execute "${project.gradlerio.javaLocation}java -version", timeoutSec: project.gradlerio.timeout
			}
		}
	}

	private void makeFiles(){
		project.logger.info('Creating robot command files.')
		def commandFile = new File(temporaryDir.path + '/' + getFileName())
		switch (type) {
			case debug: 
				commandFile.text = "${project.gradlerio.robotDebugCommand}\n";
				def debugFile = new File(temporaryDir.path + '/' + getDebugFileName())
				debugFile.text = '# This file is used as a flag to determine if debugging should be used.\n# It is uploaded to the robot when launched in debug mode and should be removed automatically once used.'
				break;
			default: 
				commandFile.text = "${project.gradlerio.robotCommand}\n";
				break;
		}
	}

	private void getLibraries(){
		project.logger.info('Getting the appropriate library files.')
		project.copy {
			into "${temporaryDir.path}/lib"
			project.configurations.riolibs.asFileTree.each {
				from( project.zipTree(it).matching {
					exclude 'include/' 
					exclude '**/*.MF'
					exclude '**/*.a'
				}.files )
			}
		}
	}

	private void restartCommands(){
		project.logger.info('Restarting RoboRIO\'s code.')
		ssh.run {
			session(ssh.remotes.rioElevated){
				execute project.gradlerio.killNetConsoleCommand, ignoreError: true, timeoutSec: project.gradlerio.timeout
			}
		}
		ssh.run {
			session(ssh.remotes.rio){
				execute project.gradlerio.robotKillCommand, ignoreError: true, timeoutSec: project.gradlerio.timeout
			}
		}
		ssh.run {
			session(ssh.remotes.rio){
				execute project.gradlerio.finalCommand, timeoutSec: project.gradlerio.timeout
			}
		}
	}

	private void scpFiles(){
		project.logger.info('Sending files.')
		ssh.run {
			session(ssh.remotes.rio){
				put from: project.jar.archivePath.path, into: "${project.gradlerio.deployDir}${project.gradlerio.jarFileName}.jar"
			}
			session(ssh.remotes.rio){
				put from: temporaryDir.path + '/' + getFileName(), into: project.gradlerio.commandDeployDir + getFileName()
			}
		}
		if (type == debug){
			ssh.run {
				session(ssh.remotes.rio){
					put from: temporaryDir.path + '/' + getDebugFileName(), into: project.gradlerio.frcDebugDir + getDebugFileName()
				}
			}
		}
		filePermissions()
	}

	private void scpLibraries(){
		project.logger.info('Sending Libraries.')
		def f = project.files(project.fileTree("${temporaryDir.path}/lib/"))
		ssh.run{
			session(ssh.remotes.rioElevated){
				put from: f, into: "${project.gradlerio.ldLibraryPath}"
			}
		}
		libraryPermissions()
	}

	private void scpNetConsoleHost(){
		project.logger.info('Sending NetConsole-Host.')
		def f = new File('launch/netconsole-host')
		ssh.run{
			session(ssh.remotes.rioElevated){
				put from: f, into: "${project.gradlerio.netConsoleHostLocation}"
			}
		}
		netConsoleHostPermissions()
	}

	private void libraryPermissions(){
		project.logger.info('Changing library permissions.')
		ssh.run{
			session(ssh.remotes.rioElevated){
				execute "chmod -R +x ${project.gradlerio.ldLibraryPath}", timeoutSec: project.gradlerio.timeout
			}
		}
		ssh.run {
			session(ssh.remotes.rioElevated){
				execute "ldconfig", timeoutSec: project.gradlerio.timeout
			}
		}
	}

	private void filePermissions(){
		project.logger.info('Changing file permissions.')
		ssh.run {
			session(ssh.remotes.rioElevated){
				execute "chmod +x ${project.gradlerio.commandDeployDir}" + getFileName(), timeoutSec: project.gradlerio.timeout
			}
		}
		if (type == debug){
			ssh.run {
				session(ssh.remotes.rioElevated){
					execute "chown ${project.gradlerio.robotUser}:${project.gradlerio.robotNIGroup} ${project.gradlerio.frcDebugDir}" + getDebugFileName(), timeoutSec: project.gradlerio.timeout
				}
			}
		}
	}

	private void netConsoleHostPermissions(){
		project.logger.info('Changing netconsole-host permissions.')
		ssh.run {
			session(ssh.remotes.rioElevated){
				execute "chmod +x ${project.gradlerio.netConsoleHostLocation}netconsole-host", timeoutSec: project.gradlerio.timeout
			}
		}
	}
}
