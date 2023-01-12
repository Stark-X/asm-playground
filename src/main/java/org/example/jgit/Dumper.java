package org.example.jgit;

import org.eclipse.jgit.api.DiffCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class Dumper {
    private final Repository repo;

    public Dumper(String repoPath) throws IOException {
        this.repo = FileRepositoryBuilder.create(Path.of(repoPath, ".git/").toFile());
    }

    /**
     * dump diff entries between HEAD and source commit
     *
     * @param baselineCommit baseline commit id, full length id (40 char)
     * @param getDetail      get diff detail or not
     * @return list of diff entry
     */
    public List<DiffEntry> dumpDiff(String baselineCommit, boolean getDetail) throws IOException {
        ObjectId destCommitId = Optional.ofNullable(this.repo.exactRef("HEAD"))
                .orElseThrow(() -> new RuntimeException("should never throw"))
                .getObjectId();

        return dumpDiff(
                baselineCommit,
                Optional.ofNullable(destCommitId).orElseThrow(() -> new RuntimeException("should never throw")).name(),
                getDetail
        );
    }

    /**
     * dump diff entries between two commits
     *
     * @param baselineCommit  baseline commit id, full length id (40 char)
     * @param toCompareCommit to compare commit id, full length id (40 char)
     * @param getDetail       get diff detail or not
     * @return list of diff entry
     */
    public List<DiffEntry> dumpDiff(String baselineCommit, String toCompareCommit, boolean getDetail) throws IOException {
        Git gitRepo = new Git(this.repo);

        AbstractTreeIterator oldTreeIter = prepareTreeParser(baselineCommit);
        AbstractTreeIterator newTreeIter = prepareTreeParser(toCompareCommit);

        DiffCommand diffCommand = gitRepo.diff().setOldTree(oldTreeIter).setNewTree(newTreeIter);
        diffCommand.setShowNameAndStatusOnly(!getDetail);
        try {
            return diffCommand.call();
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    private AbstractTreeIterator prepareTreeParser(String commitId) throws IOException {
        ObjectId treeId;
        try (RevWalk walk = new RevWalk(this.repo)) {
            RevCommit commit = walk.parseCommit(ObjectId.fromString(commitId));
            treeId = Optional.of(commit.getTree().getId()).orElseThrow(RuntimeException::new);
        }

        try (ObjectReader reader = repo.newObjectReader()) {
            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            treeParser.reset(reader, treeId);
            return treeParser;
        }
    }

    public String getContent(String commitId, String path) throws IOException {
        return getContent(ObjectId.fromString(commitId), path);
    }

    public String getContent(ObjectId objectId, String filePath) throws IOException {
        RevCommit commit;
        try (RevWalk walk = new RevWalk(this.repo)) {
            commit = walk.parseCommit(objectId);
        }
        return getContent(commit, filePath);
    }

    private String getContent(RevCommit commit, String path) throws IOException {
        try (TreeWalk treeWalk = TreeWalk.forPath(this.repo, path, commit.getTree())) {
            ObjectId blobId = treeWalk.getObjectId(0);
            try (ObjectReader objectReader = this.repo.newObjectReader()) {
                ObjectLoader objectLoader = objectReader.open(blobId);
                byte[] bytes = objectLoader.getBytes();
                return new String(bytes, StandardCharsets.UTF_8);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Dumper dumper = new Dumper(".");
        List<DiffEntry> diffEntries = dumper.dumpDiff("fd41e1369e139d886b82a56f7f33a8eabe1f88f8", false);
        diffEntries.forEach(System.out::println);
    }
}
