package io.github.technomancers

import org.gradle.api.*
import groovy.util.*

class DeployJarTask extends RioTask{
	@tasks.TaskAction
	void deployJar() {
		ssh.run{
			session(ssh.remotes.rio){
				execute "${project.gradlerio.javaLocation}java -version", timeoutSec: project.gradlerio.timeout
			}
		}
		ssh.run {
			session(ssh.remotes.rio){
				put from: project.jar.archivePath.path, into: "${project.gradlerio.deployDir}${project.gradlerio.jarFileName}.jar"
			}
		}
	}
}
