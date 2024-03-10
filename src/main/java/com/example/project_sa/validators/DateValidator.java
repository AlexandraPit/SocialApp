package com.example.project_sa.validators;

import java.time.Year;

public class DateValidator implements ValidatorInterface<String>{
    String typeOf;
    public DateValidator(){
        this.typeOf = "date";
    }

    private void validateDate(String entity) throws ValidationException {
        /*Initialize an empty String to collect the errors.*/
        String errors="";

        if( !entity.matches("\\d{4}-\\d{2}-\\d{2}") )
            errors += "The date provided is in the incorrect format!\n";

        String[] userInputDataParts = entity.split("-");
        if(Integer.parseInt(userInputDataParts[0])<1000
                || Integer.parseInt(userInputDataParts[0])> Year.now().getValue())
            errors += "The year provided is not allowed!\n";

        if(Integer.parseInt(userInputDataParts[1])<1
                || Integer.parseInt(userInputDataParts[1])> 13)
            errors += "The month provided is not allowed!\n";

        if(Integer.parseInt(userInputDataParts[2])<1
                || Integer.parseInt(userInputDataParts[2])> 31)
            errors += "The day provided is not allowed!\n";

        if(Integer.parseInt(userInputDataParts[1])==2 && Integer.parseInt(userInputDataParts[0])%4==0 && Integer.parseInt(userInputDataParts[2])> 29)
            errors += "February cannot have more than 29 days in a leap year!\n";

        if(Integer.parseInt(userInputDataParts[1])==2 && Integer.parseInt(userInputDataParts[0])%4!=0 && Integer.parseInt(userInputDataParts[2])> 28)
            errors += "February cannot have more than 28 days in a non-leap year!\n";

        if(Integer.parseInt(userInputDataParts[1])==4 && Integer.parseInt(userInputDataParts[2])> 30)
            errors += "April cannot have more than 30 days!\n";

        if(Integer.parseInt(userInputDataParts[1])==6 && Integer.parseInt(userInputDataParts[2])> 30)
            errors += "June cannot have more than 30 days!\n";

        if(Integer.parseInt(userInputDataParts[1])==9 && Integer.parseInt(userInputDataParts[2])> 30)
            errors += "September cannot have more than 30 days!\n";

        if(Integer.parseInt(userInputDataParts[1])==11 && Integer.parseInt(userInputDataParts[2])> 30)
            errors += "November cannot have more than 30 days!\n";

        this.typeOf = "time";

        if(!errors.isEmpty()) throw new ValidationException(errors);
    }

    private void validateTime(String entity) throws ValidationException {
        /*Initialize an empty String to collect the errors.*/
        String errors="";
        if( !entity.matches("\\d{2}:\\d{2}:\\d{2}") )
            errors += "The date provided is in the incorrect format!\n";

        String[] userInputTimeParts = entity.split(":");
        if(Integer.parseInt(userInputTimeParts[0])<0
                || Integer.parseInt(userInputTimeParts[0])> 24)
            errors += "The hour provided is not allowed!\n";

        if(Integer.parseInt(userInputTimeParts[1])<0
                || Integer.parseInt(userInputTimeParts[1])> 59)
            errors += "The minutes provided are not allowed!\n";

        if(Integer.parseInt(userInputTimeParts[2])<0
                || Integer.parseInt(userInputTimeParts[2])> 59)
            errors += "The seconds provided are not allowed!\n";

        if(!errors.isEmpty()) throw new ValidationException(errors);

    }

    @Override
    public void validate(String entity) throws ValidationException {

        if(typeOf.equals("date")){
            validateDate(entity);
        }
        else if(typeOf.equals("time"))
            validateTime(entity);
        else
            throw new ValidationException("The type of validation is incorrect!");


    }
}
