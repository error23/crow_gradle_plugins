package com.crow.gradle.plugins.packaging

import LinuxPackagingExtension
import com.bmuschko.gradle.docker.tasks.container.DockerCopyFileFromContainer
import com.bmuschko.gradle.docker.tasks.container.DockerCopyFileToContainer
import com.bmuschko.gradle.docker.tasks.container.DockerCreateContainer
import com.bmuschko.gradle.docker.tasks.container.DockerLogsContainer
import com.bmuschko.gradle.docker.tasks.container.DockerStartContainer
import com.bmuschko.gradle.docker.tasks.container.DockerWaitContainer
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.crow.gradle.plugins.packaging.tasks.LinuxPackagingInitTask
import com.crow.gradle.plugins.packaging.tasks.ProcessArtifactsTask
import com.crow.gradle.plugins.packaging.tasks.ProcessDistributionSourcesTask
import com.crow.gradle.plugins.packaging.tasks.ProcessDockerSourcesTask
import com.crow.gradle.plugins.packaging.tasks.ProcessResourcesTask
import com.crow.gradle.plugins.packaging.tasks.ProcessSharedSourcesTask
import com.github.dockerjava.api.model.Frame
import com.github.dockerjava.api.model.StreamType
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.support.uppercaseFirstChar

/**
 * Linux packaging plugin.
 *
 * Builds linux packages for different distributions using dockers.
 */
class LinuxPackagingPlugin : Plugin<Project> {

	private val taskGroup = "linux packaging"

	override fun apply(project: Project) {

		// Apply dependency plugins
		project.plugins.apply("base")
		project.plugins.apply("com.bmuschko.docker-remote-api")

		// Create configuration for distribution artifacts
		val configuration = project.configurations.create("distributionArtifacts")
		configuration.isCanBeResolved = true
		configuration.isCanBeConsumed = false

		// Create linux packaging extension
		val extension = project.extensions.create<LinuxPackagingExtension>("linuxPackagingConfig")
		setupExtension(extension)

		// Register tasks
		project.tasks.register<LinuxPackagingInitTask>("linuxPackagingInit") {
			group = taskGroup
			description = extension.linuxPackagingInitTask.description.get()
			packageTypes.set(extension.linuxPackagingInitTask.packageTypes)
			distributionDirectory.set(extension.linuxPackagingInitTask.distributionDirectory)
			sourceDirectoryName.set(extension.linuxPackagingInitTask.sourceDirectoryName)
			dockerSourceDirectoryName.set(extension.linuxPackagingInitTask.dockerSourceDirectoryName)
			resourcesDirectory.set(extension.linuxPackagingInitTask.resourcesDirectory)
			commonSrcDirectory.set(extension.linuxPackagingInitTask.commonSrcDirectory)
		}

		val processSharedResourcesTask = project.tasks.register<ProcessResourcesTask>("processSharedResources") {
			group = taskGroup
			description = extension.processSharedResourcesTask.description.get()
			packageTypes.set(extension.processSharedResourcesTask.packageTypes)
			copyInclude.set(extension.processSharedResourcesTask.copyInclude)
			copyExclude.set(extension.processSharedResourcesTask.copyExclude)
			filterProperties.set(extension.processSharedResourcesTask.filterProperties)
			resourcesDirectory.set(extension.processSharedResourcesTask.resourcesDirectory)
			outputDirectory.set(extension.processSharedResourcesTask.outputDirectory)
		}

		val processDockerSourcesTask = project.tasks.register<ProcessDockerSourcesTask>("processDockerSources") {
			group = taskGroup
			dependsOn(processSharedResourcesTask)
			description = extension.processDockerSourcesTask.description.get()
			packageTypes.set(extension.processDockerSourcesTask.packageTypes)
			copyInclude.set(extension.processDockerSourcesTask.copyInclude)
			copyExclude.set(extension.processDockerSourcesTask.copyExclude)
			filterProperties.set(extension.processDockerSourcesTask.filterProperties)
			distributionDirectory.set(extension.processDockerSourcesTask.distributionDirectory)
			dockerSourceDirectoryName.set(extension.processDockerSourcesTask.dockerSourceDirectoryName)
			outputDirectory.set(extension.processDockerSourcesTask.outputDirectory)
		}

		val processSharedSourcesTask = project.tasks.register<ProcessSharedSourcesTask>("processSharedSources") {
			group = taskGroup
			dependsOn(processDockerSourcesTask)
			description = extension.processSharedSourcesTask.description.get()
			packageTypes.set(extension.processSharedSourcesTask.packageTypes)
			copyInclude.set(extension.processSharedSourcesTask.copyInclude)
			copyExclude.set(extension.processSharedSourcesTask.copyExclude)
			filterProperties.set(extension.processSharedSourcesTask.filterProperties)
			commonSrcDirectory.set(extension.processSharedSourcesTask.commonSrcDirectory)
			outputDirectory.set(extension.processSharedSourcesTask.outputDirectory)
			packageName.set(extension.processSharedSourcesTask.packageName)
		}

		val processDistributionSourcesTask = project.tasks.register<ProcessDistributionSourcesTask>("processDistributionSources") {
			group = taskGroup
			dependsOn(processSharedSourcesTask)
			description = extension.processDistributionSourcesTask.description.get()
			packageTypes.set(extension.processDistributionSourcesTask.packageTypes)
			copyInclude.set(extension.processDistributionSourcesTask.copyInclude)
			copyExclude.set(extension.processDistributionSourcesTask.copyExclude)
			filterProperties.set(extension.processDistributionSourcesTask.filterProperties)
			distributionDirectory.set(extension.processDistributionSourcesTask.distributionDirectory)
			sourceDirectoryName.set(extension.processDistributionSourcesTask.sourceDirectoryName)
			outputDirectory.set(extension.processDistributionSourcesTask.outputDirectory)
			packageName.set(extension.processDistributionSourcesTask.packageName)
		}

		val processArtifactsTask = project.tasks.register<ProcessArtifactsTask>("processArtifacts") {
			group = taskGroup
			dependsOn(processDistributionSourcesTask)
			description = extension.processArtifactsTask.description.get()
			packageTypes.set(extension.processArtifactsTask.packageTypes)
			copyInclude.set(extension.processArtifactsTask.copyInclude)
			copyExclude.set(extension.processArtifactsTask.copyExclude)
			filterProperties.set(extension.processArtifactsTask.filterProperties)
			outputDirectory.set(extension.processArtifactsTask.outputDirectory)
			packageName.set(extension.processArtifactsTask.packageName)
			artifacts.from(extension.processArtifactsTask.artifacts)
			distributionPath.set(extension.processArtifactsTask.distributionPath)
			unArchive.set(extension.processArtifactsTask.unArchive)
		}

		project.afterEvaluate {

			for (packageType in extension.buildDockerImageTask.packageTypes.get()) {
				val buildDockerImageTask = project.tasks.register<DockerBuildImage>("buildDockerImage${packageType.uppercaseFirstChar()}") {
					group = taskGroup
					dependsOn(processArtifactsTask)
					description = extension.buildDockerImageTask.description.get()
					inputDir.set(extension.buildDockerImageTask.inputDirectory.dir(packageType))
					images.add("${extension.buildDockerImageTask.dockerImageNamePrefix.get().lowercase()}_${packageType.lowercase()}:${project.version}")
				}

				val createDockerContainerTask = project.tasks.register<DockerCreateContainer>("createDockerContainer${packageType.uppercaseFirstChar()}") {
					group = taskGroup
					dependsOn(buildDockerImageTask)
					description = extension.buildDockerImageTask.description.get()
					targetImageId("${extension.buildDockerImageTask.dockerImageNamePrefix.get().lowercase()}_${packageType.lowercase()}:${project.version}")
					hostConfig.autoRemove.set(true)
				}

				val copyProjectToContainerTask = project.tasks.register<DockerCopyFileToContainer>("copyProjectToContainer${packageType.uppercaseFirstChar()}") {
					group = taskGroup
					dependsOn(createDockerContainerTask)
					description = extension.buildDockerImageTask.description.get()
					val createContainerTask = project.tasks.getByName("createDockerContainer${packageType.uppercaseFirstChar()}")
					if (createContainerTask is DockerCreateContainer) {
						targetContainerId(createContainerTask.containerId)
					}
					hostPath.set(extension.buildDockerImageTask.inputDirectory.dir("${packageType}/${extension.buildDockerImageTask.packageName.get()}").get().asFile.absolutePath)
					remotePath.set("/root/build/")
				}

				val startDockerContainerTask = project.tasks.register<DockerStartContainer>("startDockerContainer${packageType.uppercaseFirstChar()}") {
					group = taskGroup
					dependsOn(copyProjectToContainerTask)
					description = extension.buildDockerImageTask.description.get()
					val createContainerTask = project.tasks.getByName("createDockerContainer${packageType.uppercaseFirstChar()}")
					if (createContainerTask is DockerCreateContainer) {
						targetContainerId(createContainerTask.containerId)
					}

					onError {
						logger.error("Failed to start docker container ${this.toString()}")
						throw this
					}
				}

				project.tasks.register<DockerLogsContainer>("logDockerContainer${packageType.uppercaseFirstChar()}") {
					group = taskGroup
					dependsOn(startDockerContainerTask)
					description = extension.buildDockerImageTask.description.get()
					targetContainerId(startDockerContainerTask.get().containerId)
					follow.set(true)
					tailAll.set(true)

					onNext {
						logger.quiet(this.toString())

						val message = this as Frame
						if (message.streamType == StreamType.STDERR) {
							throw GradleException(this.toString())
						}
					}
				}

				val dockerWaitContainerTask = project.tasks.register<DockerWaitContainer>("waitDockerContainer${packageType.uppercaseFirstChar()}") {
					group = taskGroup
					dependsOn(startDockerContainerTask)
					description = extension.buildDockerImageTask.description.get()
					val createContainerTask = project.tasks.getByName("createDockerContainer${packageType.uppercaseFirstChar()}")
					if (createContainerTask is DockerCreateContainer) {
						targetContainerId(createContainerTask.containerId)
					}
				}

				project.tasks.register<DockerCopyFileFromContainer>("copyArtifactsFromContainer${packageType.uppercaseFirstChar()}") {
					group = taskGroup
					dependsOn(dockerWaitContainerTask)
					description = extension.buildDockerImageTask.description.get()
					val createContainerTask = project.tasks.getByName("createDockerContainer${packageType.uppercaseFirstChar()}")
					if (createContainerTask is DockerCreateContainer) {
						targetContainerId(createContainerTask.containerId)
					}
					hostPath.set(extension.buildDockerImageTask.inputDirectory.dir("${packageType}/artifacts").get().asFile.absolutePath)
					remotePath.set("/root/build/artifacts")
				}
			}

			project.tasks.register("packageLinux") {
				group = "distribution"
				dependsOn(project.tasks.matching { it.name.startsWith("copyArtifactsFromContainer") })
				description = "Package linux distributions"
			}
		}
	}

	/**
	 * Setup global extension conventions.
	 *
	 * @param extension to set up
	 */
	private fun setupExtension(extension: LinuxPackagingExtension) {

		// Set packageTypes convention
		extension.linuxPackagingInitTask.packageTypes.convention(extension.packageTypes)
		extension.processSharedResourcesTask.packageTypes.convention(extension.packageTypes)
		extension.processDockerSourcesTask.packageTypes.convention(extension.packageTypes)
		extension.processSharedSourcesTask.packageTypes.convention(extension.packageTypes)
		extension.processDistributionSourcesTask.packageTypes.convention(extension.packageTypes)
		extension.processArtifactsTask.packageTypes.convention(extension.packageTypes)
		extension.buildDockerImageTask.packageTypes.convention(extension.packageTypes)

		// Set sourceDirectoryName convention
		extension.linuxPackagingInitTask.sourceDirectoryName.convention(extension.sourceDirectoryName)
		extension.processDistributionSourcesTask.sourceDirectoryName.convention(extension.sourceDirectoryName)

		// Set dockerSourceDirectoryName convention
		extension.linuxPackagingInitTask.dockerSourceDirectoryName.convention(extension.dockerSourceDirectoryName)
		extension.processDockerSourcesTask.dockerSourceDirectoryName.convention(extension.dockerSourceDirectoryName)

		// Set distroDir convention
		extension.linuxPackagingInitTask.distributionDirectory.convention(extension.distributionDirectory)
		extension.processDockerSourcesTask.distributionDirectory.convention(extension.distributionDirectory)
		extension.processDistributionSourcesTask.distributionDirectory.convention(extension.distributionDirectory)

		// Set resourcesDir convention
		extension.linuxPackagingInitTask.resourcesDirectory.convention(extension.resourcesDirectory)
		extension.processSharedResourcesTask.resourcesDirectory.convention(extension.resourcesDirectory)

		// Set commonSrcDir convention
		extension.linuxPackagingInitTask.commonSrcDirectory.convention(extension.commonSrcDirectory)
		extension.processSharedSourcesTask.commonSrcDirectory.convention(extension.commonSrcDirectory)

		// Set outputDirectory convention
		extension.processSharedResourcesTask.outputDirectory.convention(extension.outputDirectory)
		extension.processDockerSourcesTask.outputDirectory.convention(extension.outputDirectory)
		extension.processSharedSourcesTask.outputDirectory.convention(extension.outputDirectory)
		extension.processDistributionSourcesTask.outputDirectory.convention(extension.outputDirectory)
		extension.processArtifactsTask.outputDirectory.convention(extension.outputDirectory)

		// Set packageName convention
		extension.processSharedSourcesTask.packageName.convention(extension.packageName)
		extension.processDistributionSourcesTask.packageName.convention(extension.packageName)
		extension.processArtifactsTask.packageName.convention(extension.packageName)
		extension.buildDockerImageTask.packageName.convention(extension.packageName)

		// Set filter properties convention
		extension.processSharedResourcesTask.filterProperties.convention(extension.filterProperties)
		extension.processDockerSourcesTask.filterProperties.convention(extension.filterProperties)
		extension.processSharedSourcesTask.filterProperties.convention(extension.filterProperties)
		extension.processDistributionSourcesTask.filterProperties.convention(extension.filterProperties)
		extension.processArtifactsTask.filterProperties.convention(extension.filterProperties)
	}
}
