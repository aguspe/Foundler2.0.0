package com.example.foundlerv2.Matches;

public class MatchesObject {
        private String userId;
        private String name;
        private String phone;
        private String profilePictureUrl;

        public MatchesObject (String userId, String name, String profilePictureUrl){
            this.userId = userId;
            this.name = name;
            this.profilePictureUrl = profilePictureUrl;
//            this.phone = phone;
        }

        public String getUserId(){
            return userId;
        }
        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getName(){return name;}
        public void setName(String name) {
        this.name = name;
    }

//        public String getPhone(){
//        return phone;
//    }
//        public void setPhone(String phone) {
//        this.phone = phone;
//    }

        public String getProfilePictureUrl(){
        return profilePictureUrl;
    }
        public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}
