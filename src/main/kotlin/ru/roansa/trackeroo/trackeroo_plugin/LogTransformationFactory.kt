package ru.roansa.trackeroo.trackeroo_plugin

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.ACC_STATIC
import org.objectweb.asm.Opcodes.INVOKESTATIC

private const val TARGET = "android/util/Log"
private const val REPLACER = "ru/roansa/trackeroo_core/logging/Logger"

abstract class LogTransformationFactory : AsmClassVisitorFactory<InstrumentationParameters.None> {

    override fun createClassVisitor(classContext: ClassContext, nextClassVisitor: ClassVisitor): ClassVisitor =
        if (isInstrumentable(classContext.currentClassData)) {
            LogClassVisitor(instrumentationContext.apiVersion.get(), nextClassVisitor)
        } else {
            nextClassVisitor
        }

    override fun isInstrumentable(classData: ClassData): Boolean {
        return !classData.className.contains("ru.roansa.trackeroo_core")
    }

}

class LogClassVisitor(api: Int, classVisitor: ClassVisitor) : ClassVisitor(api, classVisitor) {

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor = LogMethodVisitor(api, super.visitMethod(access, name, descriptor, signature, exceptions))

}

class LogMethodVisitor(api: Int, nextMethodVisitor: MethodVisitor) : MethodVisitor(api, nextMethodVisitor) {

    override fun visitMethodInsn(
        opcode: Int,
        owner: String?,
        name: String?,
        descriptor: String?,
        isInterface: Boolean
    ) {
        return when {

            owner == TARGET && name == "v" && opcode == INVOKESTATIC -> {
                mv.visitMethodInsn(opcode, REPLACER, name, descriptor, isInterface)
            }

            owner == TARGET && name == "d" && opcode == INVOKESTATIC -> {
                mv.visitMethodInsn(opcode, REPLACER, name, descriptor, isInterface)
            }

            owner == TARGET && name == "i" && opcode == INVOKESTATIC -> {
                mv.visitMethodInsn(opcode, REPLACER, name, descriptor, isInterface)
            }

            owner == TARGET && name == "w" && opcode == INVOKESTATIC -> {
                mv.visitMethodInsn(opcode, REPLACER, name, descriptor, isInterface)
            }

            owner == TARGET && name == "e" && opcode == INVOKESTATIC -> {
                mv.visitMethodInsn(opcode, REPLACER, name, descriptor, isInterface)
            }

            owner == TARGET && name == "wtf" && opcode == INVOKESTATIC -> {
                mv.visitMethodInsn(opcode, REPLACER, name, descriptor, isInterface)
            }

            owner == TARGET && name == "println" && opcode == INVOKESTATIC -> {
                mv.visitMethodInsn(opcode, REPLACER, name, descriptor, isInterface)
            }

            else -> super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
        }
    }

}