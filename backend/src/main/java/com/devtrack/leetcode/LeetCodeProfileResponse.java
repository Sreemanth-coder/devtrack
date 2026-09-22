package com.devtrack.leetcode;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class LeetCodeProfileResponse {

    private Data data;

    @Getter
    @Setter
    public static class Data {
        private MatchedUser matchedUser;
    }

    @Getter
    @Setter
    public static class MatchedUser {
        private SubmitStatsGlobal submitStatsGlobal;
        private String username;
        private Profile profile;
    }

    @Getter
    @Setter
    public static class SubmitStatsGlobal {
        private List<SubmissionCount> acSubmissionNum;
    }

    @Getter
    @Setter
    public static class SubmissionCount {
        private String difficulty;
        private Integer count;
    }


    @Getter
    @Setter
    public static class Profile {
        private Integer ranking;
        private String realName;
        private String aboutMe;
        private String userAvatar;
    }
}