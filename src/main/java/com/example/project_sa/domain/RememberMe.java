package com.example.project_sa.domain;

public class RememberMe {

        private String email;
        private String password;
        private boolean checked;
        public RememberMe(String email, String password, Boolean checked)
        {
            this.email=email;
            this.password=password;
            this.checked=checked;
        }

        public String getPassword_r() {return password;}
        public String getEmail_r(){return email;}

        public boolean getChecked(){return checked;}

        public void setPassword_r(String password){this.password=password;}

        public void setEmail_r(String email){
            this.email=email;
        }

    }


