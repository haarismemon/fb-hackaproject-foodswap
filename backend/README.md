# Backend routing of Foodswap


## User login request

1. HTTP method is `POST`

2. URL is `https://foodswapapp.herokuapp.com/users/login`

3. Content-Type is `application/json`

4. JSON should contain fields 'email' and 'pass'. Example:
   
            {"email":"example@hotmail.com", "pass":"12345" }

5. The response from the server on succesful request is a JSON object with 3 fields: 

            status: 0 - fail, 1 - success
            msg: success or error message
            profile: Upon succesful request should return relevant fields of the user
                     {
                        fname: first name of the user,
                        lname: last name of the user,
                        gender: gender of the user, 
                        nationality: ethnicity of the user,
                        dietary: dietary requirement of the user, 
                        dob: date of birth of the user
                     }
            uid: the id of the user
     

## User signup request

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
            'dob': date of birth in the format 'YYYY-MM-DD', not null
   
5. The response from the server on succesful request is a JSON object with 2 fields: 

            status: 0 - fail, 1 - success
            msg: success or error message


## Home page list request

1. HTTP method is `POST`

2. URL is `https://foodswapapp.herokuapp.com/users/list`

3. Content-Type is `application/json`

4. JSON should contain just 'uid'
   
5. The response from the server on succesful request is a JSON object with 3 fields: 

            status: 0 - fail, 1 - success
            msg: success or error message
            list: a list of user's event (a list of List objects)
                  { id: id of the event,
                    uid: id of the owner/user of the event,
                    food: the food user wants to cook,
                    status: the status of this event, can be 0-pending, 1-confirm, 2-done,
                    date: the date to meet up in the format of 'YYYY-MM-DD',
                    partnerid: will be the partner's id after matching is done, otherwise is null }
            
            
## Create new event request          

1. HTTP method is `POST`

2. URL is `https://foodswapapp.herokuapp.com/users/newevent`

3. Content-Type is `application/json`

4. JSON should contain

            'uid': user's id, not null
            'food': food the user wants to cook, not null
            'date': in the format 'YYYY-MM-DD', not null
   
5. The response from the server on succesful request is a JSON object with 3 fields: 

            status: 0 - fail, 1 - success
            msg: success or error message
            event_object: {
               id: event id
               uid: id of the user who created the even
               food: food the user wants to cook
               status: 0-pending, 1-confirm, 2-done
               date: the datetime that the event will take place
               partnerid: initially null, but when paired will return the id of the paired user
               updatedAt: the datetime of the row being updated in the database
               createdAt: the datetime of the row being created in the database
            }
            
            
## Check event state request

1. HTTP method is `POST`

2. URL is `https://foodswapapp.herokuapp.com/users/checkevent`

3. Content-Type is `application/json`

4. Require JSON that contains just the 'id' of the event in request
   
5. The response from the server on succesful request is a JSON with 4 fields: 

            status: 0 - fail, 1 - success
            msg: success or error message
            event_status: the current status of the event (can be 0-pending, 1-confirm, 2-done), will return null upon unsuccessful request
            partner_info: if event is paired, return the event object of the partner, otherwise null
                          { id: id of the partner, 
                            food: the food partner wants to cook, 
                            nationality: partner's ethnicity, 
                            lname: last name of the partner, 
                            fname: first name of the partner, 
                            gender: gender of the partner, 
                            dietary: dietary requirement of the partner, 
                            dob: date of birth of the partner}
                            

## Rematch event request

1. HTTP method is `POST`

2. URL is `https://foodswapapp.herokuapp.com/users/rematch`

3. Content-Type is `application/json`

4. Require JSON that contains just the 'id' of the event to be unmatched
   
5. The response from the server on succesful request is a JSON with 4 fields: 

            status: 0 - fail, 1 - success
            msg: success or error message