var express = require('express');
var router = express.Router();
var User = require("../models/User");
var sequelize = require('sequelize');
var List = require("../models/List")



/***
 *  User signup
 *  Return status: 0 - fail, 1 - success
 *         message: success or error message
 */
router.post('/signup', function(req, res, next){
            
    let user = {
        fname: req.body.fname,
        lname: req.body.lname,
        gender: parseInt(req.body.gender),
        nationality: req.body.nationality,
        dietary: req.body.dietary,
        pass: req.body.pass,
        email: req.body.email,
        dob: req.body.dob//'YYYY-MM-DD'
    };
    //check if user already exists
    User.findOne({where:{email:user.email}})
        .then(function(result){
        if(result == null){
            //Insert new instance into User table in DB
            User.create(user)
                .then(function(result){
                    console.log('User created.');
                    res.json({
                        status: 1,
                        msg: 'Success. User created.'
                    });
                })
                .catch(function (err){
                    console.log('User insert Error! '+error);
                    res.json({
                        status: 0,
                        msg: 'User insert Error! '+error
                    });
                }); 
        }
        else{ //user exists
            console.log('User already exists.');
            res.json({
                status: 0,
                msg: 'User already exists.'
            });
        }
    });
    
});
            


/***
 *  User login
 *  req: email, pass
 *  Return status: 0 - fail, 1 - success
 *         message: success or error message
 *         profile: the fields of user logged in
 */
router.post('/login', function(req, res, next){
    let email = req.body.email;
    let pass = req.body.pass;

    User.findOne({where:{email:email, pass:pass}})
        .then(function(result){
        console.log("User found. Client has logged in!"+ result);
        res.json({
            profile:result,
            uid:result.id,
            msg:'User found. For gender field: 0-male, 1-female, 2-other, 3-prefer not to say.',
            status:1
        });
        })
        .catch(function(err){
            console.log('Login failed! DB connection error: '+err);
            res.json({
                msg: 'DB connection error: '+err,
                status:0
            });
        });
});



/***
 *  User's list of events
 *  req: uid
 *  Return status: 0 - fail, 1 - success
 *         message: success or error message
 *         list: a list of user's event
 */ 
router.post('/list', function(req, res, next){
    let uid = req.body.uid;
    List.findAll({where:{uid:uid}})
        .then(function(result){
        console.log("List returned!"+result);
        res.json({
            list:result,
            msg:'List is returned. For status field: 0-pending, 1-confirm, 2-done.',
            status:1
        });
    })
        .catch(function(err){
        console.log('DB connection error: '+ err);
        res.json({
            list: null,
            msg: 'DB connection error: '+err,
            status:0
        });
    });
});



/***
 *  Create new event
 *  req: uid, date, food
 *  Return status: 0 - fail, 1 - success
 *         message: success or error message
 *         event_object: 0-pending, 1-confirm, 2-done
 */ 
router.post('/newevent', function(req, res, next){
    let event = {
        uid: req.body.uid,
        food: req.body.food,
        status: 0,
        date: req.body.date,
        partnerid: null
    };
    //check if event exists on the same date
    List.findOne({where:{uid:event.uid, date:event.date}})
        .then(function(result){
        if(result == null){
            List.create(event)
                .then(function(result){
                res.json({
                    status: 1,
                    msg:'Created new event.',
                    event_object: event
                });
            })
                .catch(function(err){
                console.log('DB error: '+err);
                res.json({
                    status: 0,
                    msg:'DB error: '+err,
                    event_object: null
                });
            });
        }
        else{
            res.json({
                status:0,
                msg:'There is a clash with the selected date. You might have made a request on the same date already.',
                event_object: null
            });
        }
    });
});



/***
 *  Pairing
 *  req: uid, date, food
 *  Return status: 0 - fail, 1 - success
 *         message: success or error message
 *         event_object: 0-pending, 1-confirm, 2-done
 */ 
 


module.exports = router;