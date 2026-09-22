package com.devtrack.github;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GitHubCommit {

    private CommitDetails commit;

    @Getter
    @Setter
    public static class CommitDetails {
        private CommitAuthor author;
    }

    @Getter
    @Setter
    public static class CommitAuthor {
        private String date;
    }
}