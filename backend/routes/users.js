var express = require('express');
var router = express.Router();
var User = require("../models/User");
var sequelize = require('sequelize');


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
                    res.json({
                        status: 1,
                        msg: 'Success. User created.'
                    });
                })
                .catch(function (err){
                    res.json({
                        status: 0,
                        msg: 'User insert Error!'
                    });
                }); 
        }
        else{ //user exists
            res.json({
                status: 0,
                msg: 'User already exists.'
            });
        }
    });
    
});
            
/***
 *  User login
 */
router.post('/login', function(req, res, next){
    let email = req.body.email;
    let pass = req.body.pass;

    User.findOne({where:{email:email, pass:pass}})
        .then(function(result){
        res.json({
            profile:result,
            msg:'User found.',
            status:1
        });
        })
        .catch(function(err){
            console.log('DB connection error');
            res.json({
                msg: 'DB connection error',
                status:0
            });
        });
});

module.exports = router;