package memory;

import java.util.Optional;

import instruction.Tag;

public class Register {

  private final int id;
  private int value = -1;
  private RegisterFlag flag;
  private Optional<Tag> reservingTag = Optional.empty();

  public Register(int id) {
    this.id = id;
    this.flag = RegisterFlag.VALID;
  }

  public int getId() {
    return id;
  }

  public int getValue() {
    return value;
  }

  public Optional<Tag> getReservingTag() {
    return reservingTag;
  }

  public void setReservingTag(Tag reservingTag) {
    this.reservingTag = Optional.of(reservingTag);
  }

  public void clearReservingTag() {
    this.reservingTag = Optional.empty();
  }

  public void setValue(int value) {
    this.value = value;
  }

  public RegisterFlag getFlag() {
    return flag;
  }

  public void setFlag(RegisterFlag flag) {
    this.flag = flag;
  }

  public void flush() {
    flag = RegisterFlag.VALID;
    reservingTag = Optional.empty();
  }

  @Override
  public String toString() {
    return "r" + String.format("%-2d", id) + ": " + String.format("%4d", value) + " | " + String.format("%-7s", flag) + (reservingTag.isPresent() ? " | " + reservingTag.get() : "");
  }

}