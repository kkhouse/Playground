package org.koin.sample.androidx.sdk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.host_activity.*
import org.koin.android.ext.android.inject
import org.koin.sample.android.R
import org.koin.sample.androidx.components.main.SimpleService
import org.koin.sample.androidx.components.sdk.CustomSDK
import org.koin.sample.androidx.components.sdk.CustomService
import org.koin.sample.androidx.components.sdk.SDKActivity
import org.koin.sample.androidx.utils.navigateTo

class HostActivity : AppCompatActivity() {

    // Inject by Interface - default definition
    val service: SimpleService by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sdkService = CustomSDK.koinApp.koin.get<SimpleService>()
        assert(service != sdkService)
        assert(CustomService().service == sdkService)

        title = "Host Activity with SDK"
        setContentView(R.layout.host_activity)

        host_button.setOnClickListener {
            navigateTo<SDKActivity>(isRoot = true)
        }
    }
}