package org.example;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.diff.DiffEntry;
import org.example.error.GitOperationException;
import org.example.error.wrapper.GenericThrowingFunctionWrapper;
import org.example.jdt.ClassInfo;
import org.example.jdt.Parser;
import org.example.jdt.formator.GenericFormatter;
import org.example.jgit.DiffDumper;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VersionClassesDiffer {
    private final DiffDumper diffDumper;
    private final Parser parser;

    public VersionClassesDiffer(String repoPath) throws IOException {
        this.diffDumper = new DiffDumper(repoPath);
        this.parser = new Parser();
    }

    /**
     * extract the new classes and modified classes with methods
     *
     * @param toCompareCommit the commit id to be compared
     * @param baselineCommit  the baseline commit
     * @return list of ClassInfo
     */
    public List<ClassInfo> diff(String toCompareCommit, String baselineCommit) throws GitOperationException {
        Stream<DiffEntry> validDiffEntries = diffDumper.dump(baselineCommit, toCompareCommit, false).parallelStream()
                .filter(diffEntry -> diffEntry.getNewPath().endsWith(".java"))
                .filter(diffEntry -> diffEntry.getNewPath().contains("src/main/java"))
                .filter(diffEntry -> diffEntry.getChangeType().equals(DiffEntry.ChangeType.ADD) || diffEntry.getChangeType().equals(DiffEntry.ChangeType.MODIFY));

        return validDiffEntries.map(GenericThrowingFunctionWrapper.wrapFunc(diffEntry -> {
                    ClassInfo newClassInfo = buildClassInfo(toCompareCommit, diffEntry);
                    // Add all new files to diff list
                    if (diffEntry.getChangeType().equals(DiffEntry.ChangeType.ADD)) {
                        return newClassInfo;
                    }

                    // Add all modified files to diff list
                    ClassInfo oldClassInfo = buildClassInfo(baselineCommit, diffEntry);
                    oldClassInfo.getMethodsInfo().forEach(methodInfo -> newClassInfo.dropMethodByDigest(methodInfo.getDigest()));
                    return newClassInfo;
                }))
                .filter(classInfo -> !classInfo.getMethodsInfo().isEmpty())
                .collect(Collectors.toList());

    }

    private ClassInfo buildClassInfo(String baselineCommit, DiffEntry diffEntry) throws GitOperationException {
        char[] fileContent = diffDumper.getContent(baselineCommit, diffEntry.getNewPath()).toCharArray();
        return parser.parse(fileContent, FilenameUtils.getBaseName(diffEntry.getNewPath()));
    }

    public static void main(String[] args) throws GitOperationException, IOException {
        String repoPath = ".";
        String toCompareCommit = "4008ca1dccbadabc9681dad2abb41372ee861404";
        String baselineCommit = "da141e6efef2eb09e2dd0ea173693a2bedb2fdbb";
        List<ClassInfo> collect = new VersionClassesDiffer(repoPath).diff(toCompareCommit, baselineCommit);
        new GenericFormatter(System.out).format(collect);
    }
}
