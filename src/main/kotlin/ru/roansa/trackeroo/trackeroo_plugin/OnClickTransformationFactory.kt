package ru.roansa.trackeroo.trackeroo_plugin

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import org.objectweb.asm.*
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.commons.GeneratorAdapter
import org.objectweb.asm.commons.Method

abstract class OnClickTransformationFactory : AsmClassVisitorFactory<InstrumentationParameters.None> {

    override fun createClassVisitor(classContext: ClassContext, nextClassVisitor: ClassVisitor): ClassVisitor {
        return if (isInstrumentable(classContext.currentClassData)) {
            OnClickClassVisitor(instrumentationContext.apiVersion.get(), nextClassVisitor)
        } else {
            nextClassVisitor
        }

    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        return !classData.className.contains("$LIBRARY_PACKAGE_NAME_DOTS.logging.Logger")
    }
}

class OnClickClassVisitor(api: Int, classVisitor: ClassVisitor) : ClassVisitor(api, classVisitor) {

    /**
     * This variable uses for checking if current visited class
     * is a child of android.view.View.OnClickListener interface
     */
    private var shouldBeVisited: Boolean = false

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        //TODO Is there any way to decrease chance for breaking a bytecode? Any cases when current filter will not work properly?
//        shouldBeVisited = interfaces?.contains("android/view/View") ?: false
        println("$name, $signature, $superName")
        shouldBeVisited = name?.contains("android/view/View") ?: false
//        $OnClickListener
    }

    override fun visitMethod(
        access: Int, name: String?, descriptor: String?, signature: String?, exceptions: Array<out String>?
    ): MethodVisitor {
        val parentMv = super.visitMethod(access, name, descriptor, signature, exceptions)

        if (shouldBeVisited && name == "onTouchEvent" && descriptor == "(Landroid/view/MotionEvent;)Z") {
            println("method name = $name, access = $access, descriptor = $descriptor, signature = $signature")
            return OnViewTouchMethodVisitor(api, parentMv)
        }

        return parentMv
    }

}

class OnClickMethodVisitor(api: Int, mv: MethodVisitor) : MethodVisitor(api, mv) {

    private val generatorAdapter = GeneratorAdapter(
        ACC_PUBLIC + ACC_FINAL,
        Method("onClick", "(Landroid/view/View;)V"),
        mv
    )

    override fun visitCode() {
        generatorAdapter.loadArg(0)
        generatorAdapter.invokeStatic(
            Type.getObjectType("ru/roansa/trackeroo_core/hookers/ViewHooker"),
            Method("onViewClick", "(Landroid/view/View;)V")
        )
        super.visitCode()
    }
}

class OnViewTouchMethodVisitor(api: Int, mv: MethodVisitor): MethodVisitor(api, mv) {

    private val generatorAdapter = GeneratorAdapter(
        ACC_PUBLIC,
        Method("onTouchEvent", "(Landroid/view/MotionEvent;)Z"),
        mv
    )

    override fun visitCode() {
        generatorAdapter.loadArg(0)
        generatorAdapter.invokeStatic(
            Type.getObjectType("ru/roansa/trackeroo_core/hookers/ViewHooker"),
            Method("onViewClick", "(Landroid/view/View;)V")
        )
        super.visitCode()
    }
}