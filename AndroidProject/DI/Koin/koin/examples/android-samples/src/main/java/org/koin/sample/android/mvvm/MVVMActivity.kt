package org.koin.sample.android.mvvm

import android.os.Bundle
import kotlinx.android.synthetic.main.mvvm_activity.*
import org.junit.Assert.*
import org.koin.android.ext.android.getKoin
import org.koin.android.scope.ScopeActivity
import org.koin.android.viewmodel.ViewModelOwner.Companion.from
import org.koin.android.viewmodel.ext.android.getViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.sample.android.R
import org.koin.sample.android.components.ID
import org.koin.sample.android.components.mvvm.ExtSimpleViewModel
import org.koin.sample.android.components.mvvm.SimpleViewModel
import org.koin.sample.android.components.scope.Session
import org.koin.sample.android.scope.ScopedActivityA
import org.koin.sample.android.utils.navigateTo

class MVVMActivity : ScopeActivity() {

    val simpleViewModel: SimpleViewModel by viewModel(clazz = SimpleViewModel::class) { parametersOf(ID) }

    val vm1: SimpleViewModel by viewModel(named("vm1")) { parametersOf("vm1") }
    val vm2: SimpleViewModel by viewModel(named("vm2")) { parametersOf("vm2") }

    val scopeVm: ExtSimpleViewModel by viewModel(owner = { from(this) })
    val extScopeVm: ExtSimpleViewModel by viewModel(named("ext"), owner = { from(this) })


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        assertEquals(getViewModel<SimpleViewModel> { parametersOf(ID) }, simpleViewModel)

        assertNotEquals(vm1, vm2)

        assertNotNull(scopeVm)
        assertNotNull(extScopeVm)
        assertEquals(scopeVm.session.id, extScopeVm.session.id)

        title = "Android MVVM"
        setContentView(R.layout.mvvm_activity)

        supportFragmentManager.beginTransaction()
            .replace(R.id.mvvm_frame, MVVMFragment())
            .commit()

        getKoin().setProperty("session_id", get<Session>().id)

        mvvm_button.setOnClickListener {
            navigateTo<ScopedActivityA>(isRoot = true)
        }
    }
}