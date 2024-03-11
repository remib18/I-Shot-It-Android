package com.example.ishotit.BackendConnector;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Picture {

    public static void uploadPicture(String picturePath, String userId, String locationName, String prompt) {

    }

    public static List<PictureResponse> getAllForCurrentPrompt() {
        return new ArrayList<>();
    }

    public static List<PictureResponse> getAllForUser(String userId) {
        return new ArrayList<>();
    }

    public static class PictureResponse {
        public String picturePath;
        public String userId;
        public String locationName;
        public String prompt;
        public Date date;
    }

}
