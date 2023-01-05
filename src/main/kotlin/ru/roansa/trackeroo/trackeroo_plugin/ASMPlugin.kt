package ru.roansa.trackeroo.trackeroo_plugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.DynamicFeaturePlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.AndroidBasePlugin
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class ASMPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        description = "Gradle Plugin to transform bytecode and intercept app methods for Trackaroo library"

        val isAndroid = plugins.hasPlugin(AppPlugin::class.java)
        val isLibrary = plugins.hasPlugin(LibraryPlugin::class.java)
        val isDynamicLibrary = plugins.hasPlugin(DynamicFeaturePlugin::class.java)

        //Check if project contains any android module
        if (!isAndroid && !isLibrary && !isDynamicLibrary)
            throw GradleException("'com.android.application' or 'com.android.library' plugin required.")


        plugins.withType(AndroidBasePlugin::class.java) {
            extensions.getByType(AndroidComponentsExtension::class).onVariants {

                /**
                 * This parameter COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS means
                 * that stack table will be recomputed for all modified methods in project.
                 *
                 * Recomputation may be necessary when bytecode modifications changes the stack height
                 * and/or affects count of local variables
                 */
                it.setAsmFramesComputationMode(FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS)


                /**
                 * Method transforms (oh, really?) bytecode of classes according to the rules
                 * described in AsmClassVisitorFactory implementations
                 */
                it.transformClassesWith(
                    OnClickTransformationFactory::class.java,
                    InstrumentationScope.ALL
                ) {
                    //empty block
                }

                it.transformClassesWith(
                    LogTransformationFactory::class.java,
                    InstrumentationScope.ALL
                ) {
                    //empty block
                }
            }
        }
    }

}