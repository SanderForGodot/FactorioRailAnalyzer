import factorioBlueprint.Entity
import factorioBlueprint.Position

val fact: Map<String, Map<Int, List<Entity>>> = mapOf(
    "straight-rail" to mapOf(
        0 to listOf(
            Entity(1, "straight-rail", Position(0.0, -2.0), 0),
            Entity(1, "curved-rail", Position(-1.0, -5.0), 0),
            Entity(1, "curved-rail", Position(1.0, -5.0), 1),
            Entity(-1, "straight-rail", Position(0.0, 2.0), 0),
            Entity(-1, "curved-rail", Position(1.0, 5.0), 4),
            Entity(-1, "curved-rail", Position(-1.0, 5.0), 5),

            Entity(-1, "signal", Position(-1.5, 0.5), 0),
            Entity(-1, "signal", Position(-1.5, -0.5), 0),
            Entity(-1, "signal", Position(1.5, 0.5), 4),
            Entity(-1, "signal", Position(1.5, -0.5), 4),
        ),
        2 to listOf(
            Entity(1, "straight-rail", Position(2.0, 0.0), 2),
            Entity(1, "curved-rail", Position(5.0, -1.0), 2),
            Entity(1, "curved-rail", Position(5.0, 1.0), 3),
            Entity(-1, "straight-rail", Position(-2.0, 0.0), 2),
            Entity(-1, "curved-rail", Position(-5.0, 1.0), 6),
            Entity(-1, "curved-rail", Position(-5.0, -1.0), 7),

            Entity(-1, "signal", Position(-0.5, -1.5), 2),
            Entity(-1, "signal", Position(0.5, -1.5), 2),
            Entity(-1, "signal", Position(-0.5, 1.5), 6),
            Entity(-1, "signal", Position(0.5, 1.5), 6),
        ),
        1 to listOf(
            Entity(1, "straight-rail", Position(2.0, 0.0), 5),
            Entity(1, "curved-rail", Position(3.0, 3.0), 0),
            Entity(-1, "straight-rail", Position(0.0, -2.0), 5),
            Entity(-1, "curved-rail", Position(-3.0, -3.0), 3),

            Entity(-1, "signal", Position(-1.5, -1.5), 1),
            Entity(-1, "signal", Position(0.5, 0.5), 5),
        ),
        5 to listOf(
            Entity(1, "straight-rail", Position(0.0, 2.0), 1),
            Entity(1, "curved-rail", Position(3.0, 3.0), 7),
            Entity(-1, "straight-rail", Position(-2.0, 0.0), 1),
            Entity(-1, "curved-rail", Position(-3.0, -3.0), 4),

            Entity(-1, "signal", Position(-0.5, -0.5), 1),
            Entity(-1, "signal", Position(1.5, 1.5), 5),
        ),
        3 to listOf(
            Entity(1, "straight-rail", Position(2.0, 0.0), 7),
            Entity(1, "curved-rail", Position(3.0, -3.0), 5),
            Entity(-1, "straight-rail", Position(0.0, 2.0), 7),
            Entity(-1, "curved-rail", Position(-3.0, 3.0), 2),

            Entity(-1, "signal", Position(0.5, -0.5), 3),
            Entity(-1, "signal", Position(-1.5, 1.5), 7),
        ),
        7 to listOf(
            Entity(1, "straight-rail", Position(0.0, -2.0), 3),
            Entity(1, "curved-rail", Position(3.0, -3.0), 6),
            Entity(-1, "straight-rail", Position(-2.0, 0.0), 3),
            Entity(-1, "curved-rail", Position(-3.0, 3.0), 1),

            Entity(-1, "signal", Position(-0.5, 0.5), 3),
            Entity(-1, "signal", Position(1.5, -1.5), 7),
        ),
    ),
    "curved-rail" to mapOf(
        0 to listOf(
            Entity(1, "straight-rail", Position(0.0, 2.0), 0),
            Entity(1, "curved-rail", Position(1.0, 5.0), 4),
            Entity(1, "curved-rail", Position(-1.0, 5.0), 5),
            Entity(-1, "straight-rail", Position(-3.0, -3.0), 1),
            Entity(-1, "curved-rail", Position(-4.0, -6.0), 4),


        ), 1 to listOf(
            Entity(1, "straight-rail", Position(3.0, -3.0), 7),
            Entity(1, "curved-rail", Position(4.0, -6.0), 5),
            Entity(-1, "straight-rail", Position(1.0, 5.0), 0),
            Entity(-1, "curved-rail", Position(2.0, 8.0), 4),
            Entity(-1, "curved-rail", Position(0.0, 8.0), 5),
        ), 2 to listOf(
            Entity(1, "straight-rail", Position(3.0, -3.0), 3),
            Entity(1, "curved-rail", Position(6.0, -4.0), 6),

            Entity(-1, "straight-rail", Position(-5.0, 1.0), 2),
            Entity(-1, "curved-rail", Position(-8.0, 2.0), 6),
            Entity(-1, "curved-rail", Position(-8.0, 0.0), 7),
        ), 3 to listOf(
            Entity(1, "straight-rail", Position(3.0, 1.0), 1),
            Entity(1, "curved-rail", Position(6.0, 1.0), 7),
            Entity(-1, "straight-rail", Position(-5.0, -1.0), 2),
            Entity(-1, "curved-rail", Position(-8.0, 0.0), 6),
            Entity(-1, "curved-rail", Position(-8.0, -2.0), 7),
        ), 4 to listOf(
            Entity(1, "straight-rail", Position(3.0, 3.0), 5),
            Entity(1, "curved-rail", Position(4.0, 6.0), 0),
            Entity(1, "straight-rail", Position(-1.0, -5.0), 0),
            Entity(1, "curved-rail", Position(-2.0, -8.0), 0),
            Entity(1, "curved-rail", Position(0.0, -8.0), 1),
        ), 5 to listOf(
            Entity(1, "straight-rail", Position(1.0, -5.0), 0),
            Entity(1, "curved-rail", Position(0.0, -8.0), 0),
            Entity(1, "curved-rail", Position(2.0, -8.0), 1),
            Entity(-1, "straight-rail", Position(-3.0, 3.0), 3),
            Entity(-1, "curved-rail", Position(-4.0, 6.0), 1),
        ), 6 to listOf(
            Entity(1, "straight-rail", Position(5.0, -1.0), 2),
            Entity(1, "curved-rail", Position(8.0, -2.0), 2),
            Entity(1, "curved-rail", Position(8.0, 0.0), 3),

            Entity(-1, "straight-rail", Position(3.0, 3.0), 7),
            Entity(-1, "curved-rail", Position(-6.0, 4.0), 2),
        ), 7 to listOf(
            Entity(1, "straight-rail", Position(5.0, 1.0), 2),
            Entity(1, "curved-rail", Position(8.0, 0.0), 2),
            Entity(1, "curved-rail", Position(8.0, 2.0), 3),
            Entity(-1, "straight-rail", Position(-3.0, -3.0), 5),
            Entity(-1, "curved-rail", Position(-6.0, -4.0), 3),
        )
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

