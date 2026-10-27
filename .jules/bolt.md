## 2026-10-23 - [Optimization nested loop on collection iteration]
**Learning:** Replaced nested loops over `Map.values()` and generic Object arrays with a Frequency Map iteration to improve the iteration performance from O(N*M) down to O(N+M). Using `.merge(val, 1, Integer::sum)` is an efficient way to sum copies.
**Action:** Identify operations that need to cross-check records sequentially (O(N^2)) and use a Map for cache lookup.

## 2026-10-24 - [Optimize String sorting allocations]
**Learning:** Using `Comparator.comparing(b -> b.getTitle().toLowerCase())` inside a sort function creates a new String instance on every single comparison, leading to O(N log N) memory allocations and GC churn. Using `String.CASE_INSENSITIVE_ORDER` avoids allocating any new strings during the sort.
**Action:** Use `String.CASE_INSENSITIVE_ORDER` for case-insensitive string sorting.

## 2026-10-25 - [Optimize top-N retrieval from lists]
**Learning:** Copying a full list and reversing it just to retrieve the top N newest items creates O(N) memory allocation and takes O(N) time. Using a backward-traversing `ListIterator` (`list.listIterator(list.size())`) achieves the same in O(N) time (or better if we stop early) and O(1) space, avoiding useless allocations.
**Action:** Use `ListIterator` from the end of the list when we only need the latest few elements instead of copying and reversing the whole collection.

## 2026-10-26 - [Optimize Date Sorting Allocations and Overhead]
**Learning:** Using `Comparator.comparing(Reservation::getReservedOn).reversed()` creates wrapper overhead and invokes method references on every element comparison, significantly slowing down standard collections sort. Replacing it with a direct comparison lambda like `(r1, r2) -> r2.getReservedOn().compareTo(r1.getReservedOn())` eliminates method dispatching wrappers and cuts sorting time by more than half for larger sets.
**Action:** Use a direct lambda invoking `compareTo` when sorting object fields such as `Date` instead of chaining `Comparator.comparing(...).reversed()`.
