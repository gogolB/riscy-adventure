
package memory

import chisel3._


/** 
 * The RAM for the JOCEL processor implementation.
 */
class RAM (n:Int) extends Module {
  val io = IO(new Bundle {
    val WR_DATA         = Input(Vec(4, UInt(8.W)))
    val WR              = Input(Bool())
    val MASK            = Input(UInt(4))
    val WR_ADDR         = Input(UInt(sizeof(n-2).W))
    val RD_ADDR         = Input(UInt(sizeof(n-2).W))
    val RD              = Input(Bool())

    val RD_DATA         = Output(Vec(4, UInt(8.W)))
  })

  val mem = SyncReadMem(n/4, Vec(4, UInt(8.W)))

  when(io.WR) {
    mem.write(io.WR_ADDR, io.WR_DATA, io.MASK)
    io.RD_DATA := DontCare
  }
  when(io.RD){
    io.RD_DATA := mem.read(io.RD_ADDR, io.RD)
  }
}
