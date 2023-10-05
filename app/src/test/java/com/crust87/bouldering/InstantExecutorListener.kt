package com.crust87.bouldering

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec

// This Class From https://gist.github.com/OliverCulleyDeLange/84aa4d2b299b2dfff3746bfdf346cd3e
class InstantExecutorListener : TestListener {
    override suspend fun afterSpec(spec: Spec) {
        super.afterSpec(spec)
        ArchTaskExecutor.getInstance().setDelegate(null)
    }

    override suspend fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        println("beforeSpec Start!!!")
        ArchTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
            override fun executeOnDiskIO(runnable: Runnable) = runnable.run()
            override fun postToMainThread(runnable: Runnable) = runnable.run()
            override fun isMainThread(): Boolean = true
        })
    }
}