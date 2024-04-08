import com.google.gson.annotations.SerializedName

enum class EntityType(name: String) {
    @SerializedName("rail-signal")
    Signal("rail-signal") {
        override fun isSignal(): Boolean = true
        override fun isRail(): Boolean = false
    },
    @SerializedName("rail-chain-signal")
    ChainSignal("rail-chain-signal") {
        override fun isSignal(): Boolean = true
        override fun isRail(): Boolean = false
    },
    AnySignal("signal") {
        override fun isSignal(): Boolean = true
        override fun isRail(): Boolean = false
    },
    VirtualSignal("virtual-signal") {
        override fun isSignal(): Boolean = true
        override fun isRail(): Boolean = false
    },
    @SerializedName("straight-rail")
    Rail("straight-rail") {
        override fun isSignal(): Boolean = false
        override fun isRail(): Boolean = true
    },
    @SerializedName("curved-rail")
    CurvedRail("curved-rail") {
        override fun isSignal(): Boolean = false
        override fun isRail(): Boolean = true
    },
    Error("error") {
        override fun isSignal(): Boolean = false
        override fun isRail(): Boolean = false
    };

    abstract fun isSignal(): Boolean
    abstract fun isRail(): Boolean

}

