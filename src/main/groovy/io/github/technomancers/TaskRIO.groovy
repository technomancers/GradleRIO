package io.github.technomancers

import org.gradle.api.*
import groovy.util.*

class TaskRIO {
	private Project _project

	TaskRIO(Project project){
		_project = project
	}

	void configureTasks(){
		configureDeployTask()
		configureDebugTask()
		configureRebootTask()
		configureRestartTask()
		configureDeployDepTask()
	}

	private void configureDeployTask(){
		def deployTask = _project.task('deploy', type: DeployTask)
		deployTask.dependsOn 'build'
		deployTask.description 'Build and Deploy code to the RoboRIO then restart the robot.'
		deployTask.group 'RoboRIO'
	}

	private void configureDebugTask(){
		def debugTask = _project.task('debug', type: DeployTask){
			type 'debug'
		}
		debugTask.dependsOn 'build'
		debugTask.description 'Build and Deploy code in debug mode to the RoboRIO then restart the robot.'
		debugTask.group 'RoboRIO'
	}

	private void configureRestartTask(){
		def restartTask = _project.task('restart', type: DeployTask){
			type 'restart'
		}
		restartTask.description 'Restart the RoboRIO\'s code base.'
		restartTask.group 'RoboRIO' 
	}

	private void configureRebootTask(){
		def rebootTask = _project.task('reboot', type: RioTask)
		rebootTask.description 'Reboot the RoboRIO.'
		rebootTask.group 'RoboRIO'
		rebootTask.doLast{
			project.logger.info('Rebooting RoboRIO.')
			ssh.run{
				session(ssh.remotes.rioElevated){
					execute 'reboot'
				}
			}
		}
	}

	private void configureDeployDepTask(){
		def deployDep = _project.task('deployDep', type: DeployTask){
			type 'deployDep'
		}
		deployDep.description 'Deploys all the needed libraries to the RoboRIO.'
		deployDep.group 'RoboRIO'
	}
}
