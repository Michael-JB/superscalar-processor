  init: move r0 68889        # (const) Number to calculate multiplicative persistence of
        move r1 10           # (const) Base of calculation
        move r4 9            # (const) Maximum value of digit
        move r8 0            # Iteration counter

  iter: move r7 1            # Init running multiplicative persistence total

 split: bgt r0 r4 mod        # If multiple digits, compute modulus
        mul r7 r7 r0         # Otherwise, multiply final digit
        jmp next             # Proceed to next iteration

reduce: mul r7 r7 r3         # Update running total with modulo
        div r0 r0 r1         # Divide by base (effectively shift right by digit)
        jmp split            # Process next digit

   mod: copy r2 r0           # mod: r3 = r0 % r1

 modin: sub r2 r2 r1         # Subtract base
        bge r2 r1 modin      # Loop if value >= base
        copy r3 r2           # Set result to remainder
        jmp reduce           # Return

  next: copy r0 r7           # Update working value with result of prev iteration
        addi r8 r8 1         # Increment iteration counter
        bgt r0 r4 iter       # If multiple digits remain, begin next iteration

   end: sai r8 0             # Store result to memory