class Block {

    lateinit var edgeList : ArrayList<Edge>



    fun doesCollide(other: Edge): Boolean {
       edgeList.forEach{
           if (it.doesCollide(other))
               return true
       }
        return false
    }
    fun doesCollide(other: Block): Boolean {
        other.edgeList.forEach{
            if (this.doesCollide(it))
                return true
        }
        return false
    }

}