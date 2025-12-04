---
title: "Logged Data"
---

:::info
Log data is stored in the same way as AdvantageKit, with string keys and
slashes used to denote subtables. **All logged data is persistent and will stay the same
until updated.**
:::

The following are the data types that Chrono supports input and output logging for:

### Simples
Chrono supports these simple types and their arrays:

 - Raw Bytes (Byte, ByteArray)
 - Booleans (Boolean, BooleanArray)
 - Integers / Longs
 - Floats / Doubles
 - Strings

### Enums
Chrono also supports logging and replaying generic enum values and enum arrays.
They are represented by string values from the enum's ``name()`` method.