public interface ILongword
{
    bit getBit(int i); // Get bit i
    void setBit(int i, bit value); // set bit i's value
    longword and(longword other); // and two longwords, returning a third
    longword or(longword other); // or two longwords, returning a third
    longword xor(longword other);// xor two longwords, returning a third
    longword not(); // negate this longword, creating another
    longword rightShift(int amount); // rightshift this longword by amount bits, creating a new longword
    longword leftShift(int amount);// leftshift this longword by amount bits, creating a new longword
    @Override
    String toString(); // returns a comma separated string of 0's and 1's: "0,0,0,0,0 (etcetera)" for example
    long getUnsigned(); // returns the value of this longword as a long
    int getSigned(); // returns the value of this longword as an int
    void copy(longword other); // copies the values of the bits from another longword into this one
    void set(int value); // set the value of the bits of this longword (used for tests)
}