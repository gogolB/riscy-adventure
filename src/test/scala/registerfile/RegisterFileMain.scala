package registerfile

import chisel3._


object RegisterFileMain extends App {
  iotesters.Driver.execute(args, () => new RegisterFile) {
    c => new RegisterFileUnitTester(c)
  }
}


object RegisterFileRepl extends App {
  iotesters.Driver.executeFirrtlRepl(args, () => new RegisterFile)
}
