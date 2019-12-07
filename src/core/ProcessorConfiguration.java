package core;

import control.BranchPredictorType;

public class ProcessorConfiguration {

  private final int width, aluCount, buCount, lsuCount,
    reorderBufferCapacity, loadStoreBufferCapacity, reservationStationCapacities,
    registerFileCapacity, memoryCapacity;
  private final BranchPredictorType branchPredictorType;

  public ProcessorConfiguration(int width, int aluCount, int buCount,
    int lsuCount, int reorderBufferCapacity, int loadStoreBufferCapacity,
    int reservationStationCapacities, int registerFileCapacity, int memoryCapacity,
    BranchPredictorType branchPredictorType) {
    this.width = width;
    this.aluCount = aluCount;
    this.buCount = buCount;
    this.lsuCount = lsuCount;
    this.reorderBufferCapacity = reorderBufferCapacity;
    this.loadStoreBufferCapacity = loadStoreBufferCapacity;
    this.reservationStationCapacities = reservationStationCapacities;
    this.registerFileCapacity = registerFileCapacity;
    this.memoryCapacity = memoryCapacity;
    this.branchPredictorType = branchPredictorType;
  }

  public int getWidth() {
    return width;
  }

  public int getALUCount() {
    return aluCount;
  }

  public int getBUCount() {
    return buCount;
  }

  public int getLSUCount() {
    return lsuCount;
  }

  public int getReorderBufferCapacity() {
    return reorderBufferCapacity;
  }

  public int getLoadStoreBufferCapacity() {
    return loadStoreBufferCapacity;
  }

  public int getReservationStationCapacities() {
    return reservationStationCapacities;
  }

  public int getRegisterFileCapacity() {
    return registerFileCapacity;
  }

  public int getMemoryCapacity() {
    return memoryCapacity;
  }

  public BranchPredictorType getBranchPredictorType() {
    return branchPredictorType;
  }

}