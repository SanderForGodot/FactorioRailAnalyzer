import java.util.*
import javax.swing.text.html.parser.Entity

class wtf{

    var signalList : ArrayList<Entity>
    var edgeList :ArrayList<Edge>
    signalList.forEach{ signal->
        recusion(Edge(arrayListOf( signal)))
    }

}


fun recusion(edge: Edge)
{
   var  item =  edge.ItemList.last()

    var corectdirection =

    item.left.forEach{ leftNext->
        if ( !leftNext.right.contains(item))
        {
           var newEdge = Edge(edge)
            newEdge.ItemList.add(leftNext)
            recusion(newEdge)

        }
        else{
            //wenn das if einmal scheitert kann die anze liste gespipt werden
        }
    }
    item.right.forEach{ rightNext->
        if ( rightNext.left.contains(item))
        {
            //wrong way go oposite
        }
    }





}