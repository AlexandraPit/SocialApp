package com.example.project_sa.validators;

public interface ValidatorFactoryInterface <E> {

    ValidatorInterface<E> createValidator(Strategy strategy);
}