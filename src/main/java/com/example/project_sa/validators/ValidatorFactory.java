package com.example.project_sa.validators;

public class ValidatorFactory implements ValidatorFactoryInterface {

    public ValidatorFactory(){};

    @Override
    public ValidatorInterface createValidator(Strategy strategy) {
        switch (strategy){
            case user -> { return new UserValidator(); }

            case friendship -> { return new FriendshipValidator(); }

            case date -> { return new DateValidator(); }

            default -> { return null; }
        }
    }
}