enum class EntityType( name:String) {
    Signal("rail-signal"){  override fun isSignal():Boolean= true ; override fun isRail():Boolean= false},
    ChainSignal("rail-chain-signal"){ override  fun isSignal():Boolean= true; override fun isRail():Boolean= false},
    VirtualSignal("virtual-signal"){ override  fun isSignal():Boolean= true; override fun isRail():Boolean= false},
    Rail("straight-rail"){ override  fun isSignal():Boolean= false; override  fun isRail():Boolean= true},
    CurvedRail("curved-rail"){ override  fun isSignal():Boolean= false; override  fun isRail():Boolean= true},
    Error("error"){  override fun isSignal():Boolean= false; override  fun isRail():Boolean= false};
    abstract fun isSignal(): Boolean
    abstract fun isRail():Boolean
}

/*
interface EntityNameInterface {

}

enum class SignalNameEnum( name:String):EntityNameInterface {
    Signal("rail-signal"){
                         fun isSignal():Boolean= true
                         },
    ChainSignal("rail-chain-signal"),
    VirtualSignal("virtual-signal"),
}

enum class RailNameEnum( name:String) : EntityNameInterface {
    Rail("straight-rail"),
    CurvedRail("curved-rail"),
    Error("error")
}
*/