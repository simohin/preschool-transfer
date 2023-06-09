package com.github.simokhin.preschooltransfer.util

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

suspend fun <A, B> Iterable<A>.parallelMap(f: suspend (A) -> B): List<B> = coroutineScope {
    map { async { f(it) } }.awaitAll()
}

suspend fun Iterable<Runnable>.runParallel() = coroutineScope {
    map { async { it.run() } }.awaitAll()
}
