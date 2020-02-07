package memory

import java.io.File

import chisel3._
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

class MemoryUnitTester(c: RAM) extends PeekPokeTester(c) {

  private val ram = c

  // Start write testing.
  for(i <- 0 to 31) {


    poke(ram.io.WR, false);
    poke(ram.io.WR_ADDR, i)
    poke(ram.io.WR_DATA(0), i+10)
    poke(ram.io.WR_DATA(1), i+11)
    poke(ram.io.WR_DATA(2), i+12)
    poke(ram.io.WR_DATA(3), i+13)
    // Write to all columns
    poke(ram.io.MASK(0), true) // Write to all columns
    poke(ram.io.MASK(1), true)
    poke(ram.io.MASK(2), true)
    poke(ram.io.MASK(3), true)

    poke(ram.io.WR, true);
    step(1)
  }


  // Start Read Testing
  poke(ram.io.WR, false);
  step(1)

  for(i <- 0 to 31) {
    poke(ram.io.RD, false)
    poke(ram.io.RD_ADDR, i)
    poke(ram.io.RD, true)
    step(1)
    expect(ram.io.RD_DATA(0), i+10)
    expect(ram.io.RD_DATA(1), i+11)
    expect(ram.io.RD_DATA(2), i+12)
    expect(ram.io.RD_DATA(3), i+13)
  }

  // Now conduct syncronous testing.
  // Load the first ram cell special.
  poke(ram.io.WR, false);
  poke(ram.io.WR_ADDR, 0)
  poke(ram.io.WR_DATA(0), 11)
  poke(ram.io.WR_DATA(1), 12)
  poke(ram.io.WR_DATA(2), 13)
  poke(ram.io.WR_DATA(3), 14)

  // Write to all columns
  poke(ram.io.MASK(0), true);
  poke(ram.io.MASK(1), true);
  poke(ram.io.MASK(2), true);
  poke(ram.io.MASK(3), true);
  poke(ram.io.WR, true);
  step(1)

  // For every cell check the previous row and make sure the address lines up with what we are expecting. 
  for(i <- 1 to 31) {
    // To the i'th row add the value i + 11.
    poke(ram.io.WR_ADDR, i)
    poke(ram.io.WR_DATA(0), i+11)
    poke(ram.io.WR_DATA(1), i+12)
    poke(ram.io.WR_DATA(2), i+13)
    poke(ram.io.WR_DATA(3), i+14)

    // Write to all columns
    poke(ram.io.MASK(0), true);
    poke(ram.io.MASK(1), true);
    poke(ram.io.MASK(2), true);
    poke(ram.io.MASK(3), true);
    poke(ram.io.WR, true);

    // Check the previous row. It should have the value i+10
    poke(ram.io.RD, false)
    poke(ram.io.RD_ADDR, i-1)
    poke(ram.io.RD, true)
    step(1)
    expect(ram.io.RD_DATA(0), (i-1)+11)
    expect(ram.io.RD_DATA(1), (i-1)+12)
    expect(ram.io.RD_DATA(2), (i-1)+13)
    expect(ram.io.RD_DATA(3), (i-1)+14)
  }

}

class MemoryTester extends ChiselFlatSpec {
// Disable this until we fix isCommandAvailable to swallow stderr along with stdout
  private val backendNames = if(false && firrtl.FileUtils.isCommandAvailable(Seq("verilator", "--version"))) {
    Array("firrtl", "verilator")
  }
  else {
    Array("firrtl")
  }
  for ( backendName <- backendNames ) {
    "Memory" should s"SyncReadWrite (with $backendName)" in {
      Driver(() => new RAM(32768), backendName) {
        c => new MemoryUnitTester(c)
      } should be (true)
    }
  }

  "Basic test using Driver.execute" should "be used as an alternative way to run specification" in {
    iotesters.Driver.execute(Array(), () => new RAM(32768)) {
      c => new MemoryUnitTester(c)
    } should be (true)
  }

  "using --backend-name verilator" should "be an alternative way to run using verilator" in {
    if(backendNames.contains("verilator")) {
      iotesters.Driver.execute(Array("--backend-name", "verilator"), () => new RAM(32768)) {
        c => new MemoryUnitTester(c)
      } should be(true)
    }
  }

  "running with --is-verbose" should "show more about what's going on in your tester" in {
    iotesters.Driver.execute(Array("--is-verbose"), () => new RAM(32768)) {
      c => new MemoryUnitTester(c)
    } should be(true)
  }

  /**
    * By default verilator backend produces vcd file, and firrtl and treadle backends do not.
    * Following examples show you how to turn on vcd for firrtl and treadle and how to turn it off for verilator
    */

  "running with --generate-vcd-output on" should "create a vcd file from your test" in {
    iotesters.Driver.execute(
      Array("--generate-vcd-output", "on", "--target-dir", "test_run_dir/make_a_vcd", "--top-name", "make_a_vcd"),
      () => new RAM(32768)
    ) {

      c => new MemoryUnitTester(c)
    } should be(true)

    new File("test_run_dir/make_a_vcd/make_a_vcd.vcd").exists should be (true)
  }

  "running with --generate-vcd-output off" should "not create a vcd file from your test" in {
    iotesters.Driver.execute(
      Array("--generate-vcd-output", "off", "--target-dir", "test_run_dir/make_no_vcd", "--top-name", "make_no_vcd",
      "--backend-name", "verilator"),
      () => new RAM(32768)
    ) {

      c => new MemoryUnitTester(c)
    } should be(true)

    new File("test_run_dir/make_no_vcd/make_a_vcd.vcd").exists should be (false)

  }
}

