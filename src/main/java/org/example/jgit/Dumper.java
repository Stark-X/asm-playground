package org.example.jgit;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class Dumper {
    private final Repository repo;

    Dumper(String repoPath) throws IOException {
        this.repo = FileRepositoryBuilder.create(Path.of(repoPath, ".git/").toFile());
    }

    public List<DiffEntry> dumpDiff(String srcRef, String destRef) throws IOException, GitAPIException {
        Git gitRepo = new Git(this.repo);
        CanonicalTreeParser oldTreeIter;
        CanonicalTreeParser newTreeIter;

        try (ObjectReader reader = repo.newObjectReader()) {
            ObjectId oldObjectId = repo.resolve(srcRef);
            ObjectId newObjectId = repo.resolve(destRef);

            oldTreeIter = new CanonicalTreeParser();
            oldTreeIter.reset(reader, oldObjectId);

            newTreeIter = new CanonicalTreeParser();
            newTreeIter.reset(reader, newObjectId);
        }

        return gitRepo.diff().setOldTree(oldTreeIter).setNewTree(newTreeIter).call();
    }

    public static void main(String[] args) throws IOException, GitAPIException {
        Dumper dumper = new Dumper(".");
        List<DiffEntry> diffEntries = dumper.dumpDiff("master", "d41e136");
        diffEntries.forEach(diffEntry -> {
            System.out.println(diffEntry.getOldPath());
            System.out.println(diffEntry.getNewPath());
        });
    }
}
