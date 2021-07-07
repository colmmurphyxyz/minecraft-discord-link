import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

var a = 0

suspend fun main() = runBlocking {
    foo()
    delay(1000)
    println(a)
}

suspend fun foo() {
    coroutineScope {
        launch {
            a++
        }
    }
}