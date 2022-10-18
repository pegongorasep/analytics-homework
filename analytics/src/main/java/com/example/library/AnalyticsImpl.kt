package com.example.library

data class Config(
    val version: String,
) {
    var pkg: String = ""
}

internal class AnalyticsImpl(
    private val config: Config,
    private val uploader: AnalyticsUploader,
) : Analytics {
    companion object {
        private const val PHASE_BEGIN = "phase_begin"
        private const val PHASE_END = "phase_end"
    }

    override fun track(attributes: Event.() -> Unit) {
        val event = object: Event {
            val map = HashMap<String, Any>()
            fun build(): Map<String, Any> = map
            override fun String.to(any: Any) { map[this] = any }
        }
        event.attributes()
        uploader.enqueue(event.build())
    }

    override fun phase(attributes: Phase.() -> Unit): Phase {
        val phase = object: Phase {
            val map = HashMap<String, Any>().apply { PHASE_BEGIN to System.currentTimeMillis() }
            override fun String.to(any: Any) { map[this] = any }
            override fun end() {
                map[PHASE_END] = System.currentTimeMillis()
                uploader.enqueue(map)
            }
        }
        phase.attributes()
        return phase
    }

}