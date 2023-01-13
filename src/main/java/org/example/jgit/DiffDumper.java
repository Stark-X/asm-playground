package org.example.jgit;

import org.eclipse.jgit.api.DiffCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.example.error.GitOperationException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class DiffDumper {
    private final Repository repo;

    public DiffDumper(String repoPath) throws IOException {
        this.repo = FileRepositoryBuilder.create(Path.of(repoPath, ".git/").toFile());
    }

    /**
     * dump diff entries between HEAD and source commit
     *
     * @param baselineCommit baseline commit id, full length id (40 char)
     * @param getDetail      get diff detail or not
     * @return list of diff entry
     */
    public List<DiffEntry> dump(String baselineCommit, boolean getDetail) throws GitOperationException {
        Ref head;
        try {
            head = this.repo.exactRef("HEAD");
        } catch (IOException e) {
            throw new GitOperationException("Cannot resolve current HEAD to commit id", e);
        }
        ObjectId destCommitId = Optional.ofNullable(head)
                .orElseThrow(() -> new RuntimeException("should never throw"))
                .getObjectId();

        return dump(
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
    public List<DiffEntry> dump(String baselineCommit, String toCompareCommit, boolean getDetail) throws GitOperationException {
        Git gitRepo = new Git(this.repo);

        AbstractTreeIterator oldTreeIter;
        AbstractTreeIterator newTreeIter;
        try {
            oldTreeIter = prepareTreeParser(baselineCommit);
            newTreeIter = prepareTreeParser(toCompareCommit);
        } catch (IOException e) {
            throw new GitOperationException(e);
        }

        DiffCommand diffCommand = gitRepo.diff().setOldTree(oldTreeIter).setNewTree(newTreeIter);
        diffCommand.setShowNameAndStatusOnly(!getDetail);
        try {
            return diffCommand.call();
        } catch (GitAPIException e) {
            throw new GitOperationException(e);
        }
    }

    private AbstractTreeIterator prepareTreeParser(String commitId) throws IOException, GitOperationException {
        ObjectId treeId;
        try (RevWalk walk = new RevWalk(this.repo)) {
            RevCommit commit = walk.parseCommit(ObjectId.fromString(commitId));
            treeId = Optional.of(commit.getTree().getId()).orElseThrow(() -> new GitOperationException("commit id not found in repo"));
        }

        try (ObjectReader reader = repo.newObjectReader()) {
            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            treeParser.reset(reader, treeId);
            return treeParser;
        }
    }

    public String getContent(String commitId, String path) throws GitOperationException {
        return getContent(ObjectId.fromString(commitId), path);
    }

    public String getContent(ObjectId objectId, String filePath) throws GitOperationException {
        RevCommit commit;
        try (RevWalk walk = new RevWalk(this.repo)) {
            commit = walk.parseCommit(objectId);
        } catch (IOException err) {
            throw new GitOperationException(err);
        }
        return getContent(commit, filePath);
    }

    private String getContent(RevCommit commit, String path) throws GitOperationException {
        try (TreeWalk treeWalk = TreeWalk.forPath(this.repo, path, commit.getTree())) {
            ObjectId blobId = treeWalk.getObjectId(0);
            try (ObjectReader objectReader = this.repo.newObjectReader()) {
                ObjectLoader objectLoader = objectReader.open(blobId);
                byte[] bytes = objectLoader.getBytes();
                return new String(bytes, StandardCharsets.UTF_8);
            }
        } catch (IOException err) {
            throw new GitOperationException(err);
        }
    }

    public static void main(String[] args) throws IOException, GitOperationException {
        DiffDumper dumper = new DiffDumper(".");
        List<DiffEntry> diffEntries = dumper.dump("fd41e1369e139d886b82a56f7f33a8eabe1f88f8", false);
        diffEntries.forEach(System.out::println);
    }
}
