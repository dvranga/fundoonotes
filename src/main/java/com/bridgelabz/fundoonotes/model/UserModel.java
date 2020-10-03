package com.bridgelabz.fundoonotes.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_Id")
    private long userId;
    private String fullName;
    private String emailId;
    private String mobileNumber;
    private String password;
    private boolean isVerify;
}
