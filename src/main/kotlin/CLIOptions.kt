enum class CLIFlags {
    GraphvizOutput,
    InstantShowOutput,
    ShowDebug,
    EdgesGetRandomColor,

}


val CLIOptions: MutableMap<CLIFlags, Boolean> = mutableMapOf(
//default Values
    CLIFlags.GraphvizOutput to false,
    CLIFlags.InstantShowOutput to true,
    CLIFlags.ShowDebug to false,
    CLIFlags.EdgesGetRandomColor to true,
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
    if (options.contains("-i")) {
        CLIOptions[CLIFlags.InstantShowOutput] = false
        i++
    }
    if (options.contains("-e")) {
        CLIOptions[CLIFlags.EdgesGetRandomColor] = false
        i++
    }
    if (options.contains("-h")) {
        //TODO:make help output
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
