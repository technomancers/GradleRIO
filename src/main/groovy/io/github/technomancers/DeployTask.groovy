package io.github.technomancers

import org.gradle.api.*
import groovy.util.*

class DeployTask extends RioTask{
	private final static String debug = 'debug'
	String type = 'deploy'
	
	@tasks.TaskAction
	void deploy() {
		sanityCheck()
		makeFiles()
		scpFiles()
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
}
