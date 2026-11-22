# Task 4 – Event-Handling Forensics (7 Marks)

## 4.1 Defect
The method is written `mouseclicked`, but the `MouseAdapter` callback is `mouseClicked(MouseEvent)`. Java is case-sensitive, so the misspelled method does not override the callback and is never invoked. The label is never updated because the intended callback body does not execute; the original code also does not call `status.setText(...)`.

## 4.2 Failing interaction
Click several times to draw dots, then minimise and restore the window or cover it with another window. Direct drawing through `getGraphics()` is temporary. A repaint clears/reconstructs the component and the old marks are not stored, so the dots disappear.

## 4.3 Repair
The code stores each click as a `Point`, updates the label and calls `repaint()`. `paint(Graphics)` redraws every stored point, so exposure and resize repaints preserve the drawing. `DrawPad.java` and `DrawPad_CompleteCode.java` both contain the completed implementation.

## 4.4 @Override
With `@Override` above `mouseclicked`, the compiler reports that the method does not override or implement a method from a supertype. This catches the capitalization error at compile time.

## Compiler-error experiment
Make a temporary copy, change `mouseClicked` to `mouseclicked`, and retain `@Override`. Compile with `javac DrawPad.java`. Record the exact compiler output from your installed JDK in your report/screenshot. Restore the correct spelling afterwards.

## BufferedImage alternative and complexity
Point-list design: storing n clicks uses O(n) point memory. Each full repaint loops over all points, O(n) drawing work. An off-screen `BufferedImage` draws each new dot once; repaint copies the image to the screen, approximately O(width × height) pixel copy (implementation/hardware dependent), and image memory is O(width × height × bytes-per-pixel), commonly four bytes per pixel for ARGB. Point-list storage is more compact for sparse drawings; a buffered image can make repeated repaints efficient for dense/complex drawings.
