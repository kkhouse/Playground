package org.koin.sample.androidx.scope

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.scoped_activity_b.*
import org.koin.android.ext.android.getKoin
import org.koin.core.qualifier.named
import org.koin.sample.android.R
import org.koin.sample.androidx.components.ID
import org.koin.sample.androidx.components.SCOPE_ID
import org.koin.sample.androidx.components.SCOPE_SESSION
import org.koin.sample.androidx.components.scope.Session
import org.koin.sample.androidx.sdk.HostActivity
import org.koin.sample.androidx.utils.navigateTo
import org.koin.sample.androidx.workmanager.WorkManagerActivity

class ScopedActivityB : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // No scoped Session definition in current scope
        // by definition, won;t open ScopedActivity as there is no scope definition

        // Check data from ScopedActivityA
        val session = getKoin().getScope(SCOPE_ID).get<Session>(named(SCOPE_SESSION))
        assert(ID == session.id)

        title = "Scope Activity B"
        setContentView(R.layout.scoped_activity_b)

        scoped_b_button.setOnClickListener {
            //TODO Check WorkManagerActivity
//            navigateTo<WorkManagerActivity>(isRoot = true)
            navigateTo<HostActivity>(isRoot = true)
        }
    }
}