package com.example.project_sa.validators;

import com.example.project_sa.domain.User;

public class UserValidator implements ValidatorInterface<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        /*Initialize an empty String to collect the errors.*/
        String errors="";

        if(entity.getFirst_name().isEmpty()) errors+="The first name field cannot be empty!\n";

        if(entity.getLast_name().isEmpty()) errors+="The last name field cannot be empty!\n";

        if(!errors.isEmpty()) throw new ValidationException(errors);
    }
}
