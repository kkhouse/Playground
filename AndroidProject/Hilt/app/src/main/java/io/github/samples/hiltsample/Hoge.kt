package io.github.samples.hiltsample

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject


@Module
@InstallIn(ActivityRetainedComponent::class) // Activityへの依存注入を示す
abstract class HogeModule {

    @Binds
    abstract fun bindsHogeImpl(
        impl: HogeImpl
    ): HogeInterface
}

interface HogeInterface {
    fun hoge(): String
}

class HogeImpl @Inject constructor() : HogeInterface {
    override fun hoge(): String {
        val range = (1..15)
        return range.random().toString()
    }
}

