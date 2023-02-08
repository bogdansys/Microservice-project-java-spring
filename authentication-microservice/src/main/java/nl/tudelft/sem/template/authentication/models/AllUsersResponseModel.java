package nl.tudelft.sem.template.authentication.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllUsersResponseModel {
    private ArrayList<String> users;
}