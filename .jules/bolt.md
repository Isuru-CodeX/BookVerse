## 2026-10-23 - [Optimization nested loop on collection iteration]
**Learning:** Replaced nested loops over `Map.values()` and generic Object arrays with a Frequency Map iteration to improve the iteration performance from O(N*M) down to O(N+M). Using `.merge(val, 1, Integer::sum)` is an efficient way to sum copies.
**Action:** Identify operations that need to cross-check records sequentially (O(N^2)) and use a Map for cache lookup.
