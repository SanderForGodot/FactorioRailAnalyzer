import factorioBlueprint.Entity
import factorioBlueprint.Position

//The Number in entityNumber represents into which List the Rail or Signal should be added. -1 stands for left and 1 stands for right. Meaning a
val fact: Map<EntityType, Map<Int, List<Entity>>> = mapOf(
    EntityType.Rail to mapOf(
        0 to listOf(
            Entity(1, EntityType.Rail, Position(0.0, -2.0), 0),
            Entity(1, EntityType.CurvedRail, Position(-1.0, -5.0), 0),
            Entity(1, EntityType.CurvedRail, Position(1.0, -5.0), 1),
            Entity(-1, EntityType.Rail, Position(0.0, 2.0), 0),
            Entity(-1, EntityType.CurvedRail, Position(1.0, 5.0), 4),
            Entity(-1, EntityType.CurvedRail, Position(-1.0, 5.0), 5),

            Entity(-1, EntityType.AnySignal, Position(-1.5, 0.5), 0 ,true),
            Entity(-1, EntityType.AnySignal, Position(-1.5, -0.5), 0, false),
            Entity(1, EntityType.AnySignal, Position(1.5, 0.5), 4,true),
            Entity(1, EntityType.AnySignal, Position(1.5, -0.5), 4,false),
        ),
        2 to listOf(
            Entity(1, EntityType.Rail, Position(2.0, 0.0), 2),
            Entity(1, EntityType.CurvedRail, Position(5.0, -1.0), 2),
            Entity(1, EntityType.CurvedRail, Position(5.0, 1.0), 3),
            Entity(-1, EntityType.Rail, Position(-2.0, 0.0), 2),
            Entity(-1, EntityType.CurvedRail, Position(-5.0, 1.0), 6),
            Entity(-1, EntityType.CurvedRail, Position(-5.0, -1.0), 7),

            Entity(-1, EntityType.AnySignal, Position(-0.5, -1.5), 2,true),
            Entity(-1, EntityType.AnySignal, Position(0.5, -1.5), 2,false),
            Entity(1, EntityType.AnySignal, Position(-0.5, 1.5), 6,false),
            Entity(1, EntityType.AnySignal, Position(0.5, 1.5), 6,true),
        ),
        1 to listOf(
            Entity(1, EntityType.Rail, Position(2.0, 0.0), 5),
            Entity(1, EntityType.CurvedRail, Position(3.0, 3.0), 0),
            Entity(-1, EntityType.Rail, Position(0.0, -2.0), 5),
            Entity(-1, EntityType.CurvedRail, Position(-3.0, -3.0), 3),

            Entity(-1, EntityType.AnySignal, Position(1.5, -1.5), 3,false),
            Entity(1, EntityType.AnySignal, Position(-0.5, 0.5), 7,true),
        ),
        5 to listOf(
            Entity(1, EntityType.Rail, Position(0.0, 2.0), 1),
            Entity(1, EntityType.CurvedRail, Position(3.0, 3.0), 7),
            Entity(-1, EntityType.Rail, Position(-2.0, 0.0), 1),
            Entity(-1, EntityType.CurvedRail, Position(-3.0, -3.0), 4),

            Entity(-1, EntityType.AnySignal, Position(0.5, -0.5), 3,true),
            Entity(1, EntityType.AnySignal, Position(-1.5, 1.5), 7,false),

            ),
        3 to listOf(
            Entity(1, EntityType.Rail, Position(2.0, 0.0), 7),
            Entity(1, EntityType.CurvedRail, Position(3.0, -3.0), 5),
            Entity(-1, EntityType.Rail, Position(0.0, 2.0), 7),
            Entity(-1, EntityType.CurvedRail, Position(-3.0, 3.0), 2),

            Entity(-1, EntityType.AnySignal, Position(-0.5, -0.5), 1,true),
            Entity(1, EntityType.AnySignal, Position(1.5, 1.5), 5,false),
        ),
        7 to listOf(
            Entity(1, EntityType.Rail, Position(0.0, -2.0), 3),
            Entity(1, EntityType.CurvedRail, Position(3.0, -3.0), 6),
            Entity(-1, EntityType.Rail, Position(-2.0, 0.0), 3),
            Entity(-1, EntityType.CurvedRail, Position(-3.0, 3.0), 1),

            Entity(-1, EntityType.AnySignal, Position(-1.5, -1.5), 1,true),
            Entity(1, EntityType.AnySignal, Position(0.5, 0.5), 5,false),
        ),
    ),
    /*
    To check:
    3,6,1
    * */

    EntityType.CurvedRail to mapOf(
        0 to listOf(
            Entity(1, EntityType.Rail, Position(1.0, 5.0), 0),
            Entity(1, EntityType.CurvedRail, Position(2.0, 8.0), 4),
            Entity(1, EntityType.CurvedRail, Position(0.0, 8.0), 5),
            Entity(-1, EntityType.Rail, Position(-3.0, -3.0), 1),
            Entity(-1, EntityType.CurvedRail, Position(-4.0, -6.0), 4),

            Entity(-1, EntityType.AnySignal, Position(-0.5, -3.5), 3,true),
            Entity(1, EntityType.AnySignal, Position(-2.5, -1.5), 7,false),
            Entity(1, EntityType.AnySignal, Position(2.5, 3.5), 4,false),
            Entity(-1, EntityType.AnySignal, Position(-0.5, 3.5), 0,true),
        ), 1 to listOf(
            Entity(1, EntityType.Rail, Position(3.0, -3.0), 7),
            Entity(1, EntityType.CurvedRail, Position(4.0, -6.0), 5),
            Entity(-1, EntityType.Rail, Position(-1.0, 5.0), 0),
            Entity(-1, EntityType.CurvedRail, Position(0.0, 8.0), 4),
            Entity(-1, EntityType.CurvedRail, Position(-2.0, 8.0), 5),

            Entity(-1, EntityType.AnySignal, Position(0.5, -3.5), 1,false),
            Entity(1, EntityType.AnySignal, Position(2.5, -1.5), 5,true),
            Entity(-1, EntityType.AnySignal, Position(-3.5, 3.5), 0,true),
            Entity(1, EntityType.AnySignal, Position(0.5, 3.5), 4,false),
        ), 2 to listOf(
            Entity(1, EntityType.Rail, Position(3.0, -3.0), 3),
            Entity(1, EntityType.CurvedRail, Position(6.0, -4.0), 6),
            Entity(-1, EntityType.Rail, Position(-5.0, 1.0), 2),
            Entity(-1, EntityType.CurvedRail, Position(-8.0, 2.0), 6),
            Entity(-1, EntityType.CurvedRail, Position(-8.0, 0.0), 7),

            Entity(-1, EntityType.AnySignal, Position(-3.5, -0.5), 2,true),
            Entity(1, EntityType.AnySignal, Position(-3.5, 2.5), 6,false),
            Entity(-1, EntityType.AnySignal, Position(1.5, -2.5), 1,false),
            Entity(1, EntityType.AnySignal, Position(3.5, -0.5), 5,true),
        ), 3 to listOf(
            Entity(1, EntityType.Rail, Position(3.0, 3.0), 1),
            Entity(1, EntityType.CurvedRail, Position(6.0, 4.0), 7),
            Entity(-1, EntityType.Rail, Position(-5.0, -1.0), 2),
            Entity(-1, EntityType.CurvedRail, Position(-8.0, 0.0), 6),
            Entity(-1, EntityType.CurvedRail, Position(-8.0, -2.0), 7),

            Entity(-1, EntityType.AnySignal, Position(-3.5, -2.5), 2,true),
            Entity(1, EntityType.AnySignal, Position(-3.5, 0.5), 6,false),
            Entity(1, EntityType.AnySignal, Position(1.5, 2.5), 7,false),
            Entity(-1, EntityType.AnySignal, Position(3.5, 0.5), 3,true),
        ), 4 to listOf(
            Entity(1, EntityType.Rail, Position(3.0, 3.0), 5),
            Entity(1, EntityType.CurvedRail, Position(4.0, 6.0), 0),
            Entity(-1, EntityType.Rail, Position(-1.0, -5.0), 0),
            Entity(-1, EntityType.CurvedRail, Position(-2.0, -8.0), 0),
            Entity(-1, EntityType.CurvedRail, Position(0.0, -8.0), 1),

            Entity(-1, EntityType.AnySignal, Position(-2.5, -3.5), 0,false),
            Entity(1, EntityType.AnySignal, Position(0.5, 3.5), 7,true),
            Entity(1, EntityType.AnySignal, Position(0.5, -3.5), 4,true),
            Entity(-1, EntityType.AnySignal, Position(2.5, 1.5), 3,false),
        ), 5 to listOf(
            Entity(1, EntityType.Rail, Position(1.0, -5.0), 0),
            Entity(1, EntityType.CurvedRail, Position(0.0, -8.0), 0),
            Entity(1, EntityType.CurvedRail, Position(2.0, -8.0), 1),
            Entity(-1, EntityType.Rail, Position(-3.0, 3.0), 3),
            Entity(-1, EntityType.CurvedRail, Position(-4.0, 6.0), 1),

            Entity(-1, EntityType.AnySignal, Position(-0.5, -3.5), 0,false),
            Entity(1, EntityType.AnySignal, Position(2.5, -3.5), 4,true),
            Entity(-1, EntityType.AnySignal, Position(-2.5, 1.5), 1,true),
            Entity(1, EntityType.AnySignal, Position(-0.5, 3.5), 5,false),
        ), 6 to listOf(
            Entity(1, EntityType.Rail, Position(5.0, -1.0), 2),
            Entity(1, EntityType.CurvedRail, Position(8.0, -2.0), 2),
            Entity(1, EntityType.CurvedRail, Position(8.0, 0.0), 3),
            Entity(-1, EntityType.Rail, Position(-3.0, 3.0), 7),
            Entity(-1, EntityType.CurvedRail, Position(-6.0, 4.0), 2),

            Entity(1, EntityType.AnySignal, Position(3.5, 0.5), 6,false),
            Entity(-1, EntityType.AnySignal, Position(3.5, -2.5), 2,true),
            Entity(-1, EntityType.AnySignal, Position(-3.5, 0.5), 1,true),
            Entity(1, EntityType.AnySignal, Position(-1.5, 2.5), 5,false),
        ), 7 to listOf(
            Entity(1, EntityType.Rail, Position(5.0, 1.0), 2),
            Entity(1, EntityType.CurvedRail, Position(8.0, 0.0), 2),
            Entity(1, EntityType.CurvedRail, Position(8.0, 2.0), 3),
            Entity(-1, EntityType.Rail, Position(-3.0, -3.0), 5),
            Entity(-1, EntityType.CurvedRail, Position(-6.0, -4.0), 3),

            Entity(-1, EntityType.AnySignal, Position(-1.5, -2.5), 3,true),
            Entity(1, EntityType.AnySignal, Position(-3.5, -0.5), 7,false),
            Entity(-1, EntityType.AnySignal, Position(3.5, -0.5), 2,false),
            Entity(1, EntityType.AnySignal, Position(3.5, 2.5), 6,true),
        )
    )
)

val collisionPoints: Map<EntityType, Map<Int, List<Position>>> = mapOf(
    EntityType.Rail to mapOf(
        0 to listOf(Position(-1.0,0.0),Position(1.0,0.0)),
        2 to listOf(Position(0.0,-1.0),Position(0.0,1.0)),
        1 to listOf(Position(0.0,-1.0),Position(1.0,0.0)),
        3 to listOf(Position(0.0,1.0),Position(1.0,0.0)),
        5 to listOf(Position(0.0,1.0),Position(-1.0,0.0)),
        7 to listOf(Position(0.0,-1.0),Position(-1.0,0.0)),
    ),
    EntityType.CurvedRail to mapOf(
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
