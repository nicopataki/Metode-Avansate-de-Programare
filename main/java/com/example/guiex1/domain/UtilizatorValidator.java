package com.example.guiex1.domain;

public class UtilizatorValidator implements Validator<Utilizator> {
    @Override
    public void validate(Utilizator entity) throws ValidationException {
        validateFirstName(entity.getFirstName());
        validateLastName(entity.getLastName());
        validateEmail(entity.getUsername());
    }

    private void validateFirstName(String firstName) throws ValidationException{
        if(firstName == null)
            throw new ValidationException("1FirstName must not be null!");
        else if(firstName.length() >= 100)
            throw new ValidationException("1First Name too long");
        else if(firstName.isEmpty())
            throw new ValidationException("1First Name must not be empty");
        else if(! Character.isAlphabetic(firstName.charAt(0)))
            throw new ValidationException("1First letter of the first name must be a letter");
    }

    private void validateLastName(String lastName) throws ValidationException {
        if(lastName == null)
            throw new ValidationException("2First Name must not be null!");
        else if(lastName.length() >= 100)
            throw new ValidationException("2First Name too long");
        else if(lastName.isEmpty())
            throw new ValidationException("2First Name must not be empty");
        else if(! Character.isAlphabetic(lastName.charAt(0)))
            throw new ValidationException("2First letter of the last name must be a letter");
    }

    private void validateEmail(String email) throws ValidationException {
        if(email == null)
            throw new ValidationException("Email must not be null!");
        else if(email.length() >= 100)
            throw new ValidationException("Email too long");
        else if(email.isEmpty())
            throw new ValidationException("Email must not be empty");
        else if(email.chars().filter(ch -> ch == '@').count() != 1)
            throw new ValidationException("Wrong email");
    }
}
