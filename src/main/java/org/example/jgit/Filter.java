package org.example.jgit;

import org.eclipse.jgit.diff.DiffEntry;

import java.util.List;
import java.util.stream.Collectors;

public class Filter {
    private final List<DiffEntry> diffEntries;

    public Filter(List<DiffEntry> diffEntries) {
        this.diffEntries = diffEntries;
    }

    public List<DiffEntry> filter() {
        return diffEntries.parallelStream()
                .filter(diffEntry -> diffEntry.getNewPath().endsWith(".java"))
                .filter(diffEntry -> diffEntry.getNewPath().contains("src/main/java"))
                .filter(diffEntry -> diffEntry.getChangeType().equals(DiffEntry.ChangeType.ADD) || diffEntry.getChangeType().equals(DiffEntry.ChangeType.MODIFY))
                .collect(Collectors.toList());
    }
}
