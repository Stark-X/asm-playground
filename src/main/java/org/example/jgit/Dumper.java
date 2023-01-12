package org.example.jgit;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class Dumper {
    private final Repository repo;

    Dumper(String repoPath) throws IOException {
        this.repo = FileRepositoryBuilder.create(Path.of(repoPath, ".git/").toFile());
    }

    public List<DiffEntry> dumpDiff(String srcCommitId, String destCommitId) throws IOException {
        Git gitRepo = new Git(this.repo);

        AbstractTreeIterator oldTreeIter = prepareTreeParser(srcCommitId);
        AbstractTreeIterator newTreeIter = prepareTreeParser(destCommitId);

        try {
            return gitRepo.diff().setOldTree(oldTreeIter).setNewTree(newTreeIter).call();
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

    public static void main(String[] args) throws IOException {
        Dumper dumper = new Dumper(".");
        List<DiffEntry> diffEntries = dumper.dumpDiff("refs/heads/master", "fd41e1369e139d886b82a56f7f33a8eabe1f88f8");
        diffEntries.forEach(diffEntry -> {
            System.out.println(diffEntry.getOldPath());
            System.out.println(diffEntry.getNewPath());
        });
    }
}