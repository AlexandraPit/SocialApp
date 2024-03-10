package com.example.project_sa.validators;

import com.example.project_sa.domain.Friendship;

public class FriendshipValidator  implements ValidatorInterface<Friendship> {
    @Override
    public void validate(Friendship entity) throws ValidationException {
        /*Initialize an empty String to collect the errors.*/
        String errors="";

        if( entity.getId().getLeft().intValue() <= 0 )
            errors += "The left value of the tuple ID cannot below 0!\n";

        if( entity.getId().getRight().intValue() <= 0 )
            errors += "The right value of the tuple ID cannot below 0!\n";

        if(!errors.isEmpty()) throw new ValidationException(errors);
    }
}