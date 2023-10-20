
class Item(
    var name: String,
    var x: Double,
    var y: Double,
    var rotation: Int
) {
    var connectedTo: List<Item> = ArrayList()


    var signalOntheLeft : List<Item> = ArrayList()
    var signalOntheRight : List<Item> = ArrayList()
}