enum class EntityType(name: String) {
    Signal("rail-signal") {
        override fun isSignal(): Boolean = true;
        override fun isRail(): Boolean = false
    },
    ChainSignal("rail-chain-signal") {
        override fun isSignal(): Boolean = true;
        override fun isRail(): Boolean = false
    },
    VirtualSignal("virtual-signal") {
        override fun isSignal(): Boolean = true;
        override fun isRail(): Boolean = false
    },
    Rail("straight-rail") {
        override fun isSignal(): Boolean = false;
        override fun isRail(): Boolean = true
    },
    CurvedRail("curved-rail") {
        override fun isSignal(): Boolean = false;
        override fun isRail(): Boolean = true
    },
    Error("error") {
        override fun isSignal(): Boolean = false;
        override fun isRail(): Boolean = false
    };

    abstract fun isSignal(): Boolean
    abstract fun isRail(): Boolean
}
