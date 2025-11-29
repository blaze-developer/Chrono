---
title: "Replay"
---

In Chrono's current state, all of the structure and framework is in place to run deterministic
log replay. However, the *entry point* into the FTC robot code to **run the replay** has not been
decided upon.

Currently, Chrono is a logging library with the framework to become a replay library as soon as
possible. Features like log files, RLOG streaming, output logging, and input logging are all functional.