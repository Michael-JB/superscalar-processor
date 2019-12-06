# Requires 12+ registers

# Load unsorted arr into registers
move r0 10
move r1 12
move r2 5
move r3 11
move r4 7
move r5 2
move r6 4
move r7 9
move r8 8
move r9 1
move r10 3
move r11 6

# Store the arr to memory
sai r0 0
sai r1 1
sai r2 2
sai r3 3
sai r4 4
sai r5 5
sai r6 6
sai r7 7
sai r8 8
sai r9 9
sai r10 10
sai r11 11

# Number of elements
move r0 12

# Starting index
move r6 0

# Swap counter
move r1 0

# Current index
move r2 1

# Current index - 1
subi r3 r2 1

# Load arr[i-1] into r4, arr[i] into r5
la r4 r3 0
la r5 r2 0

# Branch if arr[i-1] <= arr[i]
ble r4 r5 4

# Swap arr[i] with arr[i-1], then increment swap counter
sa r5 r3 0
sa r4 r2 0
addi r1 r1 1

# Increment current index, branching if not at end of arr
addi r2 r2 1
bne r2 r0 -8

# Branch if values were swapped in pass
bgt r1 r6 -11

# Load arr back into the registers
lai r0 0
lai r1 1
lai r2 2
lai r3 3
lai r4 4
lai r5 5
lai r6 6
lai r7 7
lai r8 8
lai r9 9
lai r10 10
lai r11 11