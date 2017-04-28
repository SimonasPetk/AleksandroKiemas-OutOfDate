package com.example.android.panevezioapp;

import static android.R.attr.name;

/**
 * Created by Simonas Petkevičius on 2017-04-25.
 */

public class Details {

        private String problemAddress;
        private String emailAddress;
        private String description;


        public String getEmailAddress() {
            return emailAddress;
        }

        public String getProblemAddress() {
            return problemAddress;
        }

        public String getDescription() {
            return description;
        }

        public void setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
        }

        public void setProblemAddress(String problemAddress) {
            this.problemAddress = problemAddress;
        }

        public void setDescription(String description) {
            this.description = description;
        }

}
