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
    poke(ram.io.WR_ADDR, i)
    poke(ram.io.WR_DATA, i+10)
    poke(ram.io.MASK, 15); // Write to all columns
    step(1)
  }


  // Start Read Testing
  poke(ram.io.WR, false);
  poke(ram.io.RD, 1)
  step(1)

  for(i <- 0 to 31) {
    poke(ram.io.RD_ADDR, i)
    step(1)
    expect(ram.io.RD_DATA, i+10)
  }

  // Now conduct syncronous testing.
  // Load the first ram cell special.
  poke(ram.io.WR, true);
  poke(ram.io.WR_ADDR, 0)
  poke(ram.io.WR_DATA, 11)
  poke(ram.io.MASK, 15); // Write to all columns
  step(1)

  // For every cell check the previous row and make sure the address lines up with what we are expecting. 
  for(i <- 1 to 31) {
    // To the i'th row add the value i + 11.
    poke(ram.io.WR_ADDR, i)
    poke(ram.io.WR_DATA, i+11)
    poke(ram.io.MASK, 15); // Write to all columns

    // Check the previous row. It should have the value i+10
    poke(ram.io.RD_ADDR, i-1)
    step(1)
    expect(ram.io.RD_DATA, i+10); 
  }

}
