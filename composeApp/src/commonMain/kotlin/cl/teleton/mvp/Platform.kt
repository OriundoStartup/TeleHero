package cl.teleton.mvp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform