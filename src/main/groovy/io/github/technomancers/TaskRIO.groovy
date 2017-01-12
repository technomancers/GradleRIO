package io.github.technomancers

import org.gradle.api.*
import groovy.util.*

class TaskRIO {
	private Project _project


	TaskRIO(Project project){
		_project = project
	}

	void configureTasks(){
		def rebootTask = _project.task('reboot', type: RebootTask)
		rebootTask.description 'Reboot the RoboRIO.'
		rebootTask.group 'RoboRIO'

		def restartTask = _project.task('restart', type: RestartTask)
		restartTask.description 'Restart the RoboRIO\'s code base.'
		restartTask.group 'RoboRIO' 
		
		def makeNetConsoleHostTask = _project.task('makeNetConsoleHost', type: MakeNetConsoleHostTask)
		def deployNetConsoleHostTask = _project.task('deployNetConsoleHost', type: DeployNetConsoleHostTask){
			file makeNetConsoleHostTask
		}
		deployNetConsoleHostTask.dependsOn makeNetConsoleHostTask
		deployNetConsoleHostTask.description 'Deploys netconsole-host to the RoboRIO.'
		deployNetConsoleHostTask.group 'RoboRIO'

		def getLibTask = _project.task('getLib', type: GetLibrariesTask){
			zips _project.files(_project.configurations.riolibs.resolve())
		}
		getLibTask.description 'Get Library Dependencies.'
		getLibTask.group 'RoboRIO'

		def deployLibTask = _project.task('deployLib', type: DeployLibrariesTask){
			libDir getLibTask
		}
		deployLibTask.dependsOn getLibTask
		deployLibTask.description 'Deploys all the needed libraries to the RoboRIO.'
		deployLibTask.group 'RoboRIO'

		def makeDeployFileTask = _project.task('makeDeployFile', type: MakeDeployFileTask)
		def makeDebugFilesTask = _project.task('makeDebugFiles', type: MakeDebugFilesTask)

		def deployTask = _project.task('deploy', type: DeployTask){
			file makeDeployFileTask
		}
		deployTask.dependsOn 'build', deployLibTask, deployNetConsoleHostTask, makeDeployFileTask
		deployTask.description 'Build and Deploy code to the RoboRIO then restart the robot.'
		deployTask.group 'RoboRIO'
		deployTask.doLast {
			restartTask.execute()
		}

		def debugTask = _project.task('debug', type: DebugTask){
			files makeDebugFilesTask
		}
		debugTask.dependsOn 'build', deployLibTask, deployNetConsoleHostTask, makeDebugFilesTask
		debugTask.description 'Build and Deploy code in debug mode to the RoboRIO then restart the robot.'
		debugTask.group 'RoboRIO'
		debugTask.doLast {
			restartTask.execute()
		}
	}
}
