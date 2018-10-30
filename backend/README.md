# Backend routing of Foodswap

## User Login Request

1. HTTP method is `POST`

2. URL is `https://foodswapapp.herokuapp.com/users/login`

3. Content-Type is `application/json`

4. JSON should contain fields 'email' and 'pass'. Example:
   
            {"email":"example@hotmail.com", "pass":"12345" }

5. The response from the server on succesful request is a JSON object with 3 fields: 

            status: 0 - fail, 1 - success
            message: success or error message
            profile: all the fields of the user
            

## User Signup Request

1. HTTP method is `POST`

2. URL is `https://foodswapapp.herokuapp.com/users/signup`

3. Content-Type is `application/json`

4. JSON should contain:

            'fname': first name, not null
            'lname': last name, not null
            'gender': not null
                      0-male
                      1-female 
                      2-other 
                      3-prefer not to say
                      
            'nationality': ethnicity, not null
            'dietary': dietary requirements, allow null
            'pass': password, not null
            'email': email address, not null
            'dob': date of birth in 'YYYY-MM-DD', not null
   
5. The response from the server on succesful request is a JSON object with 2 fields: 

            status: 0 - fail, 1 - success
            message: success or error message

