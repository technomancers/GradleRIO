package io.github.technomancers

import org.gradle.api.*
import groovy.util.*

class TaskRIO {
	private Project _project


	TaskRIO(Project project){
		_project = project
	}

	void configureTasks(){
		def makeNetConsoleHostTask = _project.task('makeNetConsoleHostTask', type: MakeNetConsoleHostTask)
		def deployNetConsoleHostTask = _project.task('deployNetConsoleHostTask', type: DeployNetConsoleHostTask){
			file makeNetConsoleHostTask
		}

		def getLib = _project.task('getLib', type: GetLibrariesTask){
			zips _project.files(_project.configurations.riolibs.resolve())
		}
		getLib.description 'Get Library Dependencies.'
		getLib.group 'RoboRIO'

		def deployLib = _project.task('deployLib', type: DeployLibrariesTask){
			libDir getLib
		}
		deployLib.dependsOn getLib
		deployLib.description 'Deploys all the needed libraries to the RoboRIO.'
		deployLib.group 'RoboRIO'

		def deployTask = _project.task('deploy', type: DeployTask)
		deployTask.dependsOn 'build', deployLib, deployNetConsoleHostTask
		deployTask.description 'Build and Deploy code to the RoboRIO then restart the robot.'
		deployTask.group 'RoboRIO'

		def debugTask = _project.task('debug', type: DeployTask){
			type 'debug'
		}
		debugTask.dependsOn 'build', deployLib, deployNetConsoleHostTask
		debugTask.description 'Build and Deploy code in debug mode to the RoboRIO then restart the robot.'
		debugTask.group 'RoboRIO'

		def rebootTask = _project.task('reboot', type: RebootTask)
		rebootTask.description 'Reboot the RoboRIO.'
		rebootTask.group 'RoboRIO'

		def restartTask = _project.task('restart', type: RestartTask)
		restartTask.description 'Restart the RoboRIO\'s code base.'
		restartTask.group 'RoboRIO' 
	}
}
