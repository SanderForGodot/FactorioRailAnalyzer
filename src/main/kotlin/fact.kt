import factorioBlueprint.Entity
import factorioBlueprint.Position

//The Number in entityNumber represents into which List the Rail or Signal should be added. -1 stands for left and 1 stands for right. Meaning a
val fact: Map<String, Map<Int, List<Entity>>> = mapOf(
    "straight-rail" to mapOf(
        0 to listOf(
            Entity(1, "straight-rail", Position(0.0, -2.0), 0),
            Entity(1, "curved-rail", Position(-1.0, -5.0), 0),
            Entity(1, "curved-rail", Position(1.0, -5.0), 1),
            Entity(-1, "straight-rail", Position(0.0, 2.0), 0),
            Entity(-1, "curved-rail", Position(1.0, 5.0), 4),
            Entity(-1, "curved-rail", Position(-1.0, 5.0), 5),

            Entity(-1, "signal", Position(-1.5, 0.5), 0 ,true),
            Entity(-1, "signal", Position(-1.5, -0.5), 0, false),
            Entity(1, "signal", Position(1.5, 0.5), 4,true),
            Entity(1, "signal", Position(1.5, -0.5), 4,false),
        ),
        2 to listOf(
            Entity(1, "straight-rail", Position(2.0, 0.0), 2),
            Entity(1, "curved-rail", Position(5.0, -1.0), 2),
            Entity(1, "curved-rail", Position(5.0, 1.0), 3),
            Entity(-1, "straight-rail", Position(-2.0, 0.0), 2),
            Entity(-1, "curved-rail", Position(-5.0, 1.0), 6),
            Entity(-1, "curved-rail", Position(-5.0, -1.0), 7),

            Entity(-1, "signal", Position(-0.5, -1.5), 2,true),
            Entity(-1, "signal", Position(0.5, -1.5), 2,false),
            Entity(1, "signal", Position(-0.5, 1.5), 6,false),
            Entity(1, "signal", Position(0.5, 1.5), 6,true),
        ),
        1 to listOf(
            Entity(1, "straight-rail", Position(2.0, 0.0), 5),
            Entity(1, "curved-rail", Position(3.0, 3.0), 0),
            Entity(-1, "straight-rail", Position(0.0, -2.0), 5),
            Entity(-1, "curved-rail", Position(-3.0, -3.0), 3),

            Entity(-1, "signal", Position(1.5, -1.5), 3,false),
            Entity(1, "signal", Position(-0.5, 0.5), 7,true),
        ),
        5 to listOf(
            Entity(1, "straight-rail", Position(0.0, 2.0), 1),
            Entity(1, "curved-rail", Position(3.0, 3.0), 7),
            Entity(-1, "straight-rail", Position(-2.0, 0.0), 1),
            Entity(-1, "curved-rail", Position(-3.0, -3.0), 4),

            Entity(-1, "signal", Position(0.5, -0.5), 3,true),
            Entity(1, "signal", Position(-1.5, 1.5), 7,false),

            ),
        3 to listOf(
            Entity(1, "straight-rail", Position(2.0, 0.0), 7),
            Entity(1, "curved-rail", Position(3.0, -3.0), 5),
            Entity(-1, "straight-rail", Position(0.0, 2.0), 7),
            Entity(-1, "curved-rail", Position(-3.0, 3.0), 2),

            Entity(-1, "signal", Position(-0.5, -0.5), 1,true),
            Entity(1, "signal", Position(1.5, 1.5), 5,false),
        ),
        7 to listOf(
            Entity(1, "straight-rail", Position(0.0, -2.0), 3),
            Entity(1, "curved-rail", Position(3.0, -3.0), 6),
            Entity(-1, "straight-rail", Position(-2.0, 0.0), 3),
            Entity(-1, "curved-rail", Position(-3.0, 3.0), 1),

            Entity(-1, "signal", Position(-1.5, -1.5), 1,true),
            Entity(1, "signal", Position(0.5, 0.5), 5,false),
        ),
    ),
    "curved-rail" to mapOf(
        0 to listOf(
            Entity(1, "straight-rail", Position(0.0, 2.0), 0),
            Entity(1, "curved-rail", Position(1.0, 5.0), 4),
            Entity(1, "curved-rail", Position(-1.0, 5.0), 5),
            Entity(-1, "straight-rail", Position(-3.0, -3.0), 1),
            Entity(-1, "curved-rail", Position(-4.0, -6.0), 4),

            Entity(-1, "signal", Position(-0.5, -3.5), 3,true),
            Entity(1, "signal", Position(-2.5, -1.5), 7,false),
            Entity(1, "signal", Position(2.5, 3.5), 4,false),
            Entity(-1, "signal", Position(-0.5, 3.5), 0,true),
        ), 1 to listOf(
            Entity(1, "straight-rail", Position(3.0, -3.0), 7),
            Entity(1, "curved-rail", Position(4.0, -6.0), 5),
            Entity(-1, "straight-rail", Position(1.0, 5.0), 0),
            Entity(-1, "curved-rail", Position(2.0, 8.0), 4),
            Entity(-1, "curved-rail", Position(0.0, 8.0), 5),

            Entity(-1, "signal", Position(0.5, -3.5), 1,false),
            Entity(1, "signal", Position(2.5, -2.5), 5,true),
            Entity(-1, "signal", Position(-3.5, 3.5), 0,true),
            Entity(1, "signal", Position(0.5, 3.5), 4,false),
        ), 2 to listOf(
            Entity(1, "straight-rail", Position(3.0, -3.0), 3),
            Entity(1, "curved-rail", Position(6.0, -4.0), 6),

            Entity(-1, "straight-rail", Position(-5.0, 1.0), 2),
            Entity(-1, "curved-rail", Position(-8.0, 2.0), 6),
            Entity(-1, "curved-rail", Position(-8.0, 0.0), 7),

            Entity(-1, "signal", Position(-3.5, -0.5), 2,true),
            Entity(1, "signal", Position(-3.5, 2.5), 6,false),
            Entity(-1, "signal", Position(1.5, -2.5), 1,false),
            Entity(1, "signal", Position(3.5, -0.5), 5,true),
        ), 3 to listOf(
            Entity(1, "straight-rail", Position(3.0, 1.0), 1),
            Entity(1, "curved-rail", Position(6.0, 1.0), 7),
            Entity(-1, "straight-rail", Position(-5.0, -1.0), 2),
            Entity(-1, "curved-rail", Position(-8.0, 0.0), 6),
            Entity(-1, "curved-rail", Position(-8.0, -2.0), 7),

            Entity(-1, "signal", Position(-1.5, -2.5), 2,true),
            Entity(1, "signal", Position(-3.5, 0.5), 6,false),
            Entity(1, "signal", Position(1.5, 0.5), 7,false),
            Entity(-1, "signal", Position(3.5, 2.5), 3,true),
        ), 4 to listOf(
            Entity(1, "straight-rail", Position(3.0, 3.0), 5),
            Entity(1, "curved-rail", Position(4.0, 6.0), 0),
            Entity(1, "straight-rail", Position(-1.0, -5.0), 0),
            Entity(1, "curved-rail", Position(-2.0, -8.0), 0),
            Entity(1, "curved-rail", Position(0.0, -8.0), 1),

            Entity(-1, "signal", Position(-2.5, -3.5), 0,false),
            Entity(1, "signal", Position(0.5, -3.5), 7,true),
            Entity(1, "signal", Position(0.5, 3.5), 4,true),
            Entity(-1, "signal", Position(2.5, 1.5), 3,false),
        ), 5 to listOf(
            Entity(1, "straight-rail", Position(1.0, -5.0), 0),
            Entity(1, "curved-rail", Position(0.0, -8.0), 0),
            Entity(1, "curved-rail", Position(2.0, -8.0), 1),
            Entity(-1, "straight-rail", Position(-3.0, 3.0), 3),
            Entity(-1, "curved-rail", Position(-4.0, 6.0), 1),

            Entity(-1, "signal", Position(-0.5, -3.5), 0,false),
            Entity(1, "signal", Position(2.5, -3.5), 7,true),
            Entity(-1, "signal", Position(-2.5, 1.5), 1,true),
            Entity(1, "signal", Position(-0.5, 3.5), 5,false),
        ), 6 to listOf(
            Entity(1, "straight-rail", Position(5.0, -1.0), 2),
            Entity(1, "curved-rail", Position(8.0, -2.0), 2),
            Entity(1, "curved-rail", Position(8.0, 0.0), 3),

            Entity(-1, "straight-rail", Position(3.0, 3.0), 7),
            Entity(-1, "curved-rail", Position(-6.0, 4.0), 2),

            Entity(1, "signal", Position(3.5, -2.5), 6,false),
            Entity(-1, "signal", Position(3.5, 0.5), 2,true),
            Entity(-1, "signal", Position(-3.5, 0.5), 1,true),
            Entity(1, "signal", Position(-1.5, 2.5), 5,false),
        ), 7 to listOf(
            Entity(1, "straight-rail", Position(5.0, 1.0), 2),
            Entity(1, "curved-rail", Position(8.0, 0.0), 2),
            Entity(1, "curved-rail", Position(8.0, 2.0), 3),
            Entity(-1, "straight-rail", Position(-3.0, -3.0), 5),
            Entity(-1, "curved-rail", Position(-6.0, -4.0), 3),

            Entity(-1, "signal", Position(-1.5, -2.5), 3,true),
            Entity(1, "signal", Position(-3.5, -0.5), 7,false),
            Entity(-1, "signal", Position(3.5, -0.5), 2,false),
            Entity(1, "signal", Position(3.5, 2.5), 6,true),
        )
    )
)

val collisionPoints: Map<String, Map<Int, List<Position>>> = mapOf(
    "straight-rail" to mapOf(
        0 to listOf(Position(-1.0,0.0),Position(1.0,0.0)),
        2 to listOf(Position(0.0,-1.0),Position(0.0,1.0)),
        1 to listOf(Position(0.0,-1.0),Position(1.0,0.0)),
        3 to listOf(Position(0.0,1.0),Position(1.0,0.0)),
        5 to listOf(Position(0.0,1.0),Position(-1.0,0.0)),
        7 to listOf(Position(0.0,-1.0),Position(-1.0,0.0)),
    ),
    "curved-rail" to mapOf(
        0 to listOf(Position(-2.0,-3.0), Position(1.0,4.0)),
        1 to listOf(Position(2.0,-3.0), Position(-1.0,4.0)),
        2 to listOf(Position(3.0,-2.0), Position(-4.0,1.0)),
        3 to listOf(Position(3.0,2.0), Position(-4.0,-1.0)),
        4 to listOf(Position(2.0,3.0), Position(-1.0,-4.0)),
        5 to listOf(Position(-2.0,3.0), Position(1.0,-4.0)),
        6 to listOf(Position(-3.0,2.0), Position(4.0,-1.0)),
        7 to listOf(Position(-3.0,-2.0), Position(4.0,1.0))
    )
)


/*
"straight-rail",
Direction
    0
      Top (Right)
        Entity(1,"straight-rail",Position(0.0,-2.0),0)
        Entity(1,"curved-rail", Position(-1.0,-5.0),0)
        Entity(1,"curved-rail", Position(1.0,-5.0),1)
      Bottum (left)
        Entity(-1,"straight-rail",Position(0.0, 2.0),0)
        Entity(-1,"curved-rail", Position(1.0,5.0),4)
        Entity(-1,"curved-rail", Position(-1.0,5.0),5)
    2
      Right
        Entity(1,"straight-rail",Position(2.0,0.0),2)
        Entity(1,"curved-rail", Position(5.0,-1.0),2)
        Entity(1,"curved-rail", Position(5.0,1.0),3)
      Left
        Entity(-1,"straight-rail",Position(-2.0,0.0),2)
        Entity(-1,"curved-rail", Position(-5.0,1.0),6)
        Entity(-1,"curved-rail", Position(-5.0,-1.0),7)
    1
      Right
        Entity(1,"straight-rail", Position(2.0,0.0),5)
        Entity(1,"curved-rail", Position(3.0,3.0),0)
      Left
        Entity(-1,"straight-rail", Position(0.0,-2.0),5)
        Entity(-1,"curved-rail", Position(-3.0,-3.0),3)

    5
      Right
        Entity(1,"straight-rail", Position(0.0, 2.0),1)
        Entity(1,"curved-rail", Position(3.0,3.0),7)
      Left
        Entity(-1,"straight-rail", Position(-2.0,0.0),1)
        Entity(-1,"curved-rail", Position(-3.0,-3.0),4)

    3
      Right
        Entity(1,"straight-rail", Position(2.0,0.0),7)
        Entity(1,"curved-rail", Position(3.0,-3.0),5)
      Left
        Entity(-1,"straight-rail", Position(0.0,2.0),7)
        Entity(-1,"curved-rail", Position(-3.0,3.0),2)

    7
      Right
        Entity(1,"straight-rail", Position(0.0,-2.0),3)
        Entity(1,"curved-rail", Position(3.0,-3.0),6)
      Left
        Entity(-1,"straight-rail", Position(-2.0,0.0),3)
        Entity(-1,"curved-rail", Position(-3.0,3.0),1)

"rail-chain-signal",
"rail-signal",
*//*
"curved-rail"
Direction
    0
      Right
        Entity(1,"straight-rail",Position(0.0, 2.0),0)
        Entity(1,"curved-rail", Position(1.0,5.0),4)
        Entity(1,"curved-rail", Position(-1.0,5.0),5)
      Left
        Entity(-1,"straight-rail", Position(-3.0,-3.0),1)
        Entity(-1,"curved-rail", Position(-4.0,-6.0),4)
    1
      Right
        Entity(1,"straight-rail", Position(3.0,-3.0),7)
        Entity(1,"curved-rail", Position(4.0,-6.0),5)
      Left
        Entity(-1,"straight-rail",Position(1.0, 5.0),0)
        Entity(-1,"curved-rail", Position(2.0,8.0),4)
        Entity(-1,"curved-rail", Position(0.0,8.0),5)
    2
      Right
        Entity(1,"straight-rail", Position(3.0,-3.0),3)
        Entity(1,"curved-rail", Position(6.0,-4.0),6)
      Left
        Entity(-1,"straight-rail",Position(-5.0,1.0),2)
        Entity(-1,"curved-rail", Position(-8.0,2.0),6)
        Entity(-1,"curved-rail", Position(-8.0,0.0),7)
    3
      Right
        Entity(1,"straight-rail", Position(3.0, 1.0),1)
        Entity(1,"curved-rail", Position(6.0,1.0),7)
      Left
        Entity(-1,"straight-rail",Position(-5.0,-1.0),2)
        Entity(-1,"curved-rail", Position(-8.0,0.0),6)
        Entity(-1,"curved-rail", Position(-8.0,-2.0),7)
    4
      Right
        Entity(1,"straight-rail", Position(3.0,3.0),5)
        Entity(1,"curved-rail", Position(4.0,6.0),0)
      Left
        Entity(1,"straight-rail",Position(-1.0,-5.0),0)
        Entity(1,"curved-rail", Position(-2.0,-8.0),0)
        Entity(1,"curved-rail", Position(0.0,-8.0),1)
    5
      Right
        Entity(1,"straight-rail",Position(1.0,-5.0),0)
        Entity(1,"curved-rail", Position(0.0,-8.0),0)
        Entity(1,"curved-rail", Position(2.0,-8.0),1)
      Left
        Entity(-1,"straight-rail", Position(-3.0,3.0),3)
        Entity(-1,"curved-rail", Position(-4.0,6.0),1)
    6
      Right
        Entity(1,"straight-rail",Position(5.0,-1.0),2)
        Entity(1,"curved-rail", Position(8.0,-2.0),2)
        Entity(1,"curved-rail", Position(8.0,0.0),3)
      Left
        Entity(-1,"straight-rail", Position(3.0,3.0),7)
        Entity(-1,"curved-rail", Position(-6.0,4.0),2)
    7
      Right
        Entity(1,"straight-rail",Position(5.0,1.0),2)
        Entity(1,"curved-rail", Position(8.0,0.0),2)
        Entity(1,"curved-rail", Position(8.0,2.0),3)
      Left
        Entity(-1,"straight-rail", Position(-3.0,-3.0),5)
        Entity(-1,"curved-rail", Position(-6.0,-4.0),3)




* */

