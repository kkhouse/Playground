package org.koin.core

import kotlin.test.*
import kotlin.test.Test
import org.koin.Simple
import org.koin.core.error.NoScopeDefFoundException
import org.koin.core.error.ScopeAlreadyCreatedException
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeCallback
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class ScopeAPITest {

    val scopeKey = named("KEY")
    val koin = koinApplication {
        modules(
            module {
                scope(scopeKey) {
                    scoped { A() }
                }
            }
        )
    }.koin

    @Test
    fun `create a scope instance`() {
        val scopeId = "myScope"
        val scope1 = koin.createScope(scopeId, scopeKey)
        val scope2 = koin.getScope(scopeId)

        assertEquals(scope1, scope2)
    }

    @Test
    fun `refresh a scoped intance`() {
        val scopeId = "myScope"
        val scope1 = koin.createScope(scopeId, scopeKey)
        val a1 = scope1.get<A>()
        val newA = A()
        scope1.refreshScopeInstance(A::class,instance = newA)
        val a2 = scope1.get<A>()

        assertEquals(newA, a2)
        assertNotEquals(a1, a2)
    }

    @Test
    fun `can't find a non created scope instance`() {
        val scopeId = "myScope"
        try {
            koin.getScope(scopeId)
            fail()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun `create different scopes`() {
        val scope1 = koin.createScope("myScope1", scopeKey)
        val scope2 = koin.createScope("myScope2", scopeKey)

        assertNotEquals(scope1, scope2)
    }

    @Test
    fun `can create scope instance with unknown scope def`() {

        try {
            koin.createScope("myScope", named("a_scope"))
        } catch (e: NoScopeDefFoundException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `create scope instance with scope def`() {

        assertNotNull(koin.createScope("myScope", scopeKey))
    }

    @Test
    fun `can't create a new scope if not closed`() {
        koin.createScope("myScope1", scopeKey)
        try {
            koin.createScope("myScope1", scopeKey)
            fail()
        } catch (e: ScopeAlreadyCreatedException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `can't get a closed scope`() {

        val scope = koin.createScope("myScope1", scopeKey)
        scope.close()
        try {
            koin.getScope("myScope1")
            fail()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun `reuse a closed scope`() {

        val scope = koin.createScope("myScope1", scopeKey)
        scope.close()
        try {
            scope.get<Simple.ComponentA>()
            fail()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun `find a scope by id`() {
        val scopeId = "myScope"
        val scope1 = koin.createScope(scopeId, scopeKey)
        assertEquals(scope1, koin.getScope(scope1.id))
    }

    @Test
    fun `scope callback`() {
        val scopeId = "myScope"
        val scope1 = koin.createScope(scopeId, scopeKey)
        var closed = false
        scope1.registerCallback(object : ScopeCallback {
            override fun onScopeClose(scope: Scope) {
                closed = true
            }
        })
        scope1.close()
        assertTrue(closed)
    }
}
