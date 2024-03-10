package com.example.project_sa.validators;

public interface ValidatorInterface <T> {
    void validate(T entity) throws ValidationException;
}