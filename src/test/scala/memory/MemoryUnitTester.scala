package memory

import java.io.File

import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

class MemoryTester(c: RAM) extends PeekPokeTester(c) {

  private val ram = c

  // Start write testing.
  poke(ram.io.WR, true);
  step(1)

  for(i <- 0 to 31) {
    poke(ram.io.ADDR, i)
    poke(ram.io.WR_DATA, i+10)
    poke(ram.io.MASK, 15); // Write to all columns
    step(1)
  }

}
