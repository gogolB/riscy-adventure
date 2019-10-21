package memory

import chisel3._


object MemoryMain extends App {
  iotesters.Driver.execute(args, () => new RAM(32768)) {
    c => new MemoryTester(c)
  }
}


object MemoryRepl extends App {
  iotesters.Driver.executeFirrtlRepl(args, () => new RAM(32768))
}
