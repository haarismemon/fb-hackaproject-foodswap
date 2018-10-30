# Backend routing of Foodswap

## User Login Request

1. HTTP method is `POST`

2. URL is `https://foodswapapp.herokuapp.com/users/login`

3. Content-Type is `application/json`

4. JSON should contain fields 'email' and 'pass'
   
   Example
   
   ```bash
   {"email":"example@hotmail.com", "pass":"12345" }
   ```
5. Return value on succesful request is a JSON object that has 3 fields: 

            status: 0 - fail, 1 - success
            message: success or error message
            profile: all the fields of the user
            

## User Signup Request

1. HTTP method is `POST`

2. URL is `https://foodswapapp.herokuapp.com/users/signup`

3. Content-Type is `application/json`

4. JSON should contain:

            'fname' -> first name, not null
            'lname' -> last name, not null
            'gender' -> 0-male
                        1-female 
                        2-other 
                        3-prefer not to say
                        , not null
            'nationality' -> ethnicity, not null
            'dietary' -> dietary requirements, allow null
            'pass' -> password, not null
            'email'-> email address, not null
            'dob' -> date of birth in 'YYYY-MM-DD', not null
   
5. Return value on succesful request is a JSON object that has 2 fields: 

            status: 0 - fail, 1 - success
            message: success or error message


