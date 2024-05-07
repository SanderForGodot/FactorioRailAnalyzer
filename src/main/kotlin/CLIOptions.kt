enum class CLIFlags {
    GraphvizOutput,
    InstantShowOutput,
    ShowDebug,
    EdgesGetRandomColor,

}


val CLIOptions: MutableMap<CLIFlags, Boolean> = mutableMapOf(
//default Values
    CLIFlags.GraphvizOutput to false,
    CLIFlags.InstantShowOutput to false,
    CLIFlags.ShowDebug to false,
    CLIFlags.EdgesGetRandomColor to false,
)

fun setCLIOptions(options:List<String>){
    var i=0
    if(options.contains("-d")){
        CLIOptions[CLIFlags.ShowDebug]=true
        i++
    }
    if(options.contains("-g")){
        CLIOptions[CLIFlags.GraphvizOutput]=true
        i++
    }
    if (options.contains("-a")) {
        CLIOptions[CLIFlags.InstantShowOutput] = true
        i++
    }
    if (options.contains("-c")) {
        CLIOptions[CLIFlags.EdgesGetRandomColor] = true
        i++
    }
    if (options.contains("-h")) {
        showCLIHelp()
        i++
    }
    if (i != options.size) {
        println("unknown CLI option ignored")
    }
}

fun dbgPrintln(string: Any) {
    if (CLIOptions[CLIFlags.ShowDebug]!!) {
        println(string.toString())
    }
}
fun dbgPrintln(fn: () -> Unit) {
    if (CLIOptions[CLIFlags.ShowDebug]!!) {
        fn.invoke()
    }
}

fun showCLIHelp(){
    println("This Program takes a Blueprint and analyses it, to find Deadlocks.\n" +
            "A Blueprint-string can be given directly in the CLI or by giving a Path to a Textfile with a Blueprint-string in it.\n" +
            "Options:\n" +
            "-h : Show this help\n"+
            "-d : Show Debug Information\n" +
            "-g : Create Graphviz Output\n" +
            "-a : Open the Output automatically\n" +
            "-c : Color in the Blocks in the Output\n")
}