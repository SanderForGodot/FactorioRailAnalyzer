package test

import factorioBlueprint.Position
import intersect

fun main() {
    var a  = Position(1.0,0.1)
    var b  = Position(0.0,-1.0)
    var c  = Position(0.0,1.0)
    var d  = Position(2.0  ,-1.0)

   println( intersect(a,b,c,d))

}