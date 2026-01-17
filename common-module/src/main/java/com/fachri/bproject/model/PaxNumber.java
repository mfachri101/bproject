package com.fachri.bproject.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaxNumber {
  private int adult;
  private int child;
  private int infant;

  @Override
  public String toString() {
    return adult + "." + child + "." + infant;
  }

  public static PaxNumber fromString(String paxString) {
    String[] parts = paxString.split("\\.");
    // validate parts length and content
    if (parts.length != 3) {
      throw new IllegalArgumentException("Invalid paxString format");
    }
    int[] intParts = new int[3];
    for (int i = 0; i < 3; i++) {
      try {
        intParts[i] = Integer.parseInt(parts[i]);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Invalid number format in paxString");
      }
    }
    PaxNumber paxNumber = new PaxNumber();
    paxNumber.setAdult(intParts[0]);
    paxNumber.setChild(intParts[1]);
    paxNumber.setInfant(intParts[2]);
    return paxNumber;
  }
}
