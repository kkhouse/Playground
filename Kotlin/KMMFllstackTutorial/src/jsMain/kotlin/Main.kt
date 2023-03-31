import kotlinx.browser.document
import react.dom.client.createRoot
import react.create

fun main() {
    val container = document.getElementById("root") ?: error("Couldn't find container!")
    createRoot(container).render(App.create())}
