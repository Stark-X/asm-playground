package org.example;

import org.apache.commons.io.FilenameUtils;
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
        String toCompareCommit = "4008ca1dccbadabc9681dad2abb41372ee861404";
        String baselineCommit = "da141e6efef2eb09e2dd0ea173693a2bedb2fdbb";

        List<DiffEntry> diffEntries = dumper.dumpDiff(baselineCommit, toCompareCommit, false);
        List<DiffEntry> validDiffEntries = new Filter(diffEntries).filter();
        List<ClassInfo> collect = validDiffEntries.stream().map(diffEntry -> {
            ClassInfo newClassInfo = parser.parse(Path.of(diffEntry.getNewPath()));
            // all new file
            if (diffEntry.getChangeType().equals(DiffEntry.ChangeType.ADD)) {
                return newClassInfo;
            }

            // modified file
            char[] fileContent;
            try {
                fileContent = dumper.getContent(baselineCommit, diffEntry.getNewPath()).toCharArray();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ClassInfo oldClassInfo = parser.parse(fileContent, FilenameUtils.getBaseName(diffEntry.getNewPath()));

            oldClassInfo.getMethodsInfo().forEach(methodInfo -> newClassInfo.dropMethodByDigest(methodInfo.getDigest()));
            return newClassInfo;
        }).collect(Collectors.toList());

        collect.forEach(it -> {
            logger.info(it.getBinaryName());
            logger.info("Methods' size: {}", it.getMethodsInfo().size());
            it.getMethodsInfo().forEach(methodInfo -> {
                logger.info("Method: {}, Parameters: {}, Digest: {}", methodInfo.getName(), methodInfo.getParameters(), methodInfo.getDigest());
            });
        });
    }

    public static void main(String[] args) throws IOException {
        new Composer().compose();
    }
}
