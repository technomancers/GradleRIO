package io.github.technomancers

import org.gradle.api.*
import groovy.util.*

class DeployLibrariesTask extends RioTask{
	private file.FileCollection libFiles = project.files()

	@tasks.SkipWhenEmpty
	@tasks.InputFiles
	@tasks.PathSensitive(tasks.PathSensitivity.NONE)
	public getLibFiles(){
		return this.libFiles
	}

	public void libDir(Task libDir){
		this.libFiles = project.files(libDir);
	}

	public void libDir(File libDir){
		this.libFiles = project.files(libDir);
	}

	@tasks.TaskAction
	void deploy(){
		ssh.run{
			session(ssh.remotes.rioElevated){
				put from: libFiles, into: "${project.gradlerio.ldLibraryPath}"
			}
		}
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
}
