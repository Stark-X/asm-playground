package org.example;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.diff.DiffEntry;
import org.example.error.GitOperationException;
import org.example.error.wrapper.GenericThrowingFunctionWrapper;
import org.example.jdt.ClassInfo;
import org.example.jdt.Parser;
import org.example.jdt.formator.GenericFormatter;
import org.example.jgit.Dumper;
import org.example.jgit.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class VersionClassesDiffer {
    private final Dumper dumper;
    private final Parser parser;
    Logger logger = LoggerFactory.getLogger(VersionClassesDiffer.class);

    public VersionClassesDiffer(String repoPath) throws IOException {
        this.dumper = new Dumper(repoPath);
        this.parser = new Parser();
    }

    public List<ClassInfo> diff(String toCompareCommit, String baselineCommit) throws GitOperationException {
        List<DiffEntry> diffEntries = dumper.dumpDiff(baselineCommit, toCompareCommit, false);
        List<DiffEntry> validDiffEntries = new Filter(diffEntries).filter();

        return validDiffEntries.stream().map(GenericThrowingFunctionWrapper.wrapFunc(diffEntry -> {
            ClassInfo newClassInfo = buildClassInfo(toCompareCommit, diffEntry);
            logger.info("Add all new files to diff list");
            if (diffEntry.getChangeType().equals(DiffEntry.ChangeType.ADD)) {
                return newClassInfo;
            }

            logger.info("Add all modified files to diff list");
            ClassInfo oldClassInfo = buildClassInfo(baselineCommit, diffEntry);
            oldClassInfo.getMethodsInfo().forEach(methodInfo -> newClassInfo.dropMethodByDigest(methodInfo.getDigest()));
            return newClassInfo;
        })).collect(Collectors.toList());

    }

    private ClassInfo buildClassInfo(String baselineCommit, DiffEntry diffEntry) throws GitOperationException {
        char[] fileContent;
        fileContent = dumper.getContent(baselineCommit, diffEntry.getNewPath()).toCharArray();
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
