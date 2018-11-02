var express = require('express');
var router = express.Router();
var User = require("../models/User");
const sequelize = require('../models/config');
var List = require("../models/List")



/***
 *  User signup
 *  Return status: 0 - fail, 1 - success
 *         msg: success or error message
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
 *         msg: success or error message
 *         profile: the fields of user logged in
 */
router.post('/login', function(req, res, next){
    let email = req.body.email;
    let pass = req.body.pass;

    User.findOne({where:{email:email, pass:pass}})
        .then(function(result){
        console.log("User found. Client has logged in!"+ result);
            res.json({
                profile:{
                    fname: result.fname,
                    lname: result.lname,
                    gender: result.gender, 
                    nationality: result.nationality,
                    dietary: result.dietary, 
                    dob: result.dob
                },
                uid:result.id,
                msg:'User found. For gender field: 0-male, 1-female, 2-other, 3-prefer not to say.',
                status:1
            });
        })
        .catch(function(err){
            console.log('Login failed! DB connection error: '+err);
            res.json({
                profile:null,
                uid:null,
                msg: 'DB connection error: '+err,
                status:0
            });
        });
});



/***
 *  User's list of events
 *  req: uid
 *  Return status: 0 - fail, 1 - success
 *         msg: success or error message
 *         list: a list of user's event
 */ 
router.post('/list', function(req, res, next){
    console.log("uid: "+req.body.uid);
    let uid = parseInt(req.body.uid);
    console.log("parseInt(uid)= "+uid);
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
 *         msg: success or error message
 *         event_object: 0-pending, 1-confirm, 2-done
 */ 
router.post('/newevent', function(req, res, next){
    let eventA = {
        uid: parseInt(req.body.uid),
        food: req.body.food,
        status: 0,
        date: req.body.date,
        partnerid: null
    };
    //check if event exists on the same date
    List.findOne({where:{uid:eventA.uid, date:eventA.date}})
        .then(function(result){
        if(result == null){
            console.log("No clash, continue to insert event into List.");
            List.create(eventA)
                .then(function(result){
                    console.log("Event created!");
                    eventA = result;
                    res.json({
                        status: 1,
                        msg:'Created new event.',
                        event_object: result
                    });
                })
                .catch(function(err){
                    console.log('DB error: '+err);
                    return res.json({
                                status: 0,
                                msg:'DB error: '+err,
                                event_object: null
                            });
                });
        }
        else{
            return res.json({
                status:0,
                msg:'There is a clash with the selected date. You might have made a request on the same date already.',
                event_object: null
            });
        }
    /************** matching part starts from here ***************/
        User.findOne({where:{id:eventA.uid}}) //Using event uid to find owner A
            .then(function(userA){
            let query = 'SELECT List.id AS id, uid FROM List, User WHERE List.uid = User.id AND User.nationality !=\''+ userA.nationality+'\' AND status=0 AND date=\''+ eventA.date +'\' LIMIT 1';
            sequelize.query(query,{type: sequelize.QueryTypes.SELECT}).then(function(userB){
                if(userB == null)return;
                else{ //update the fields
                    console.log("User B: "+userB+" list id "+userB[0].id+", userid "+userB[0].uid);
                    List.update({status:1, partnerid:userB[0].uid},{where:{id:eventA.id}});//update A's event info if eventA.reload() works...
                    List.update({status:1, partnerid:eventA.uid},{where:{id:userB[0].id}});//update B's event info
                }
            });
        });
    /************** matching part ends ******************/
    });
});


/***
 *  Check event status
 *  req: id
 *  Return status: 0 - fail, 1 - success
 *         msg: success or error message
 *         event_status: the current status of the event, on false request will return null
 *         partner_info: if event is paired, returns the event object of the partner, otherwise null
 *                       {uid, food, nationality, lname, fname, gender, dietary, dob}
 *
 ***/ 
router.post('/checkevent',function(req, res, next){
    console.log("ID: "+req.body.id);
    let id = parseInt(req.body.id);
    if(isNaN(id)){
        console.log("List id sent is not a number "+id);
        return res.json({
                status:0,
                msg:'List id sent is a NaN.'+ id,
                event_status:null,
                partner_info:null
            });
    }
    else{
            List.findOne({where:{id:id}})
            .then(function(eventA){
            if(eventA.status != 0){//This event is paired already, so include also the partner's info in the response json
                //Do a raw query which is faster and less code
                let query = 'SELECT uid AS id, food, nationality, lname, fname, gender, dietary, dob FROM List, User WHERE List.uid=' + eventA.partnerid +' AND List.uid = User.id AND List.date =\''+eventA.date+'\';';

                sequelize.query(query,{type: sequelize.QueryTypes.SELECT}).then(function(partner_info){
                    return res.json({
                        status: 1,
                        msg:'Event is found and it is paired, should be able to access partner_info(an event object) from the response.',
                        event_status:eventA.status,
                        partner_info:partner_info[0]
                    });
                });
            }
            else{
                return res.json({
                        status: 1,
                        msg:'Event is found but not paired, partner_info is null.',
                        event_status:0,
                        partner_info:null
                });
            }
        }).catch(function(error){
            console.log("DB error: "+ err);
            return res.json({
                    status:0,
                    msg:'DB error: '+ err,
                    event_status:null,
                    partner_info:null
            });
        });
    }
});


module.exports = router;