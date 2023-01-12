package org.example;

import org.eclipse.jgit.diff.DiffEntry;
import org.example.jdt.ClassInfo;
import org.example.jdt.Parser;
import org.example.jgit.Dumper;
import org.example.jgit.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class Composer {
    Parser parser = new Parser();
    Logger logger = LoggerFactory.getLogger(Composer.class);

    public void compose() throws IOException {
        Dumper dumper = new Dumper(".");
        String toCompareCommit = "51d5c4ef42710bb015f662d51e1b91e0a19da9bd";

        List<DiffEntry> diffEntries = dumper.dumpDiff(toCompareCommit, false);
        List<DiffEntry> validDiffEntries = new Filter(diffEntries).filter();
        List<ClassInfo> collect = validDiffEntries.stream().map(diffEntry -> {
            if (diffEntry.getChangeType().equals(DiffEntry.ChangeType.ADD)) {
                return parser.parse(Path.of(diffEntry.getNewPath()));
            }
            return null;
        }).collect(Collectors.toList());

        collect.forEach(it -> {
            logger.info(it.getBinaryName());
            logger.info("Methods' size: {}", it.getMethodsInfo().size());
        });
    }

    public static void main(String[] args) throws IOException {
        new Composer().compose();
    }
}
