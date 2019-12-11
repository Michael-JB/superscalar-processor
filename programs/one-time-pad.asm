        # Load message arr into registers
st_msg: move r0  1
        move r1  0
        move r2  1
        move r3  0
        move r4  1
        move r5  0
        move r6  0
        move r7  0
        move r8  1
        move r9  1
        move r10 0
        move r11 1

        # Store the message to memory
        sai  r0  0
        sai  r1  1
        sai  r2  2
        sai  r3  3
        sai  r4  4
        sai  r5  5
        sai  r6  6
        sai  r7  7
        sai  r8  8
        sai  r9  9
        sai  r10 10
        sai  r11 11

        # Load key arr into registers
st_key: move r0  1
        move r1  1
        move r2  0
        move r3  0
        move r4  1
        move r5  1
        move r6  0
        move r7  1
        move r8  0
        move r9  1
        move r10 1
        move r11 0

        # Store the key to memory
        sai  r0  12
        sai  r1  13
        sai  r2  14
        sai  r3  15
        sai  r4  16
        sai  r5  17
        sai  r6  18
        sai  r7  19
        sai  r8  20
        sai  r9  21
        sai  r10 22
        sai  r11 23

  init: move r0 0        # Counter
        move r6 12       # Message size
        move r4 0        # Register clear value

  next: la r1 r0 0       # Load message[i]
        la r2 r0 12      # Load key[i]

   xor: cmp r3 r1 r2     # Check message[i] = key[i]
        beq r4 r3 xor_f  # If equal, xor evaluates to false

 xor_t: move r5 1
        jmp close

 xor_f: move r5 0

 close: sa r5 r0 0       # Store message[i] (xor) key[i]
        sa r4 r0 12      # Store clear key[i]
        addi r0 r0 1     # Increment counter
        blt r0 r6 next   # Next iteration