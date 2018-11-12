var express = require('express');
var router = express.Router();
var User = require("../models/User");
const sequelize = require('../models/config');
var List = require("../models/List")



/***
 *  User signup
 *  Return status: 0 - fail, 1 - success
 *         uid: user's id
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
                        uid: result.id,
                        msg: 'Success. User created.'
                    });
                })
                .catch(function (err){
                    console.log('User insert Error! '+error);
                    res.json({
                        status: 0,
                        uid:null,
                        msg: 'User insert Error! '+error
                    });
                }); 
        }
        else{ //user exists
            console.log('User already exists.');
            res.json({
                status: 0,
                uid:null,
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
 *         event_object: {
 *              id: event id
 *              uid: id of the user who created the even
 *              food: food the user wants to cook
 *              status: 0-pending, 1-confirm, 2-done
 *              date: the datetime that the event will take place
 *              partnerid: initially null, but when paired will return the id of the paired user
 *              updatedAt: the datetime of the row being updated in the database
 *              createdAt: the datetime of the row being created in the database
 *          }
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
        //put this inside async to make sure eventA is updated
        if(result == null){
            console.log("No clash, continue to insert event into List.");
            List.create(eventA)
                .then(function(result){
                    console.log("Event created!");
                    eventA = JSON.parse(JSON.stringify(result));//doing a hard copy of the content of the object
                    res.json({
                        status: 1,
                        msg:'Created new event.',
                        event_object: eventA
                    });
                    /************** matching part starts from here ***************/
                    console.log("Matching start: event object:"+JSON.stringify(result)+" and list id: "+result.id);
                    User.findOne({where:{id:result.uid}}) //Using event uid to find owner A
                        .then(function(userA){
                        let query = 'SELECT List.id AS id, uid FROM List, User WHERE List.uid = User.id AND User.nationality !=\''+ userA.nationality+'\' AND status=0 AND date='+ JSON.stringify(result.date) +' LIMIT 1';
                        sequelize.query(query,{type: sequelize.QueryTypes.SELECT}).then(function(userB){
                            if(userB.length == 0)return;
                            else{ //update the fields
                                console.log("User B: "+userB+" list id "+userB[0].id+", userid "+userB[0].uid);
                                List.update({status:1, partnerid:userB[0].uid},{where:{id:result.id}});//update A's event info if eventA.reload() works...
                                List.update({status:1, partnerid:result.uid},{where:{id:userB[0].id}});//update B's event info
                            }
                        });
                    });
                /************** matching part ends ******************/
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
    //original place for the matching code
    });
});


/***
 *  Check event status
 *  req: id
 *  Return status: 0 - fail, 1 - success
 *         msg: success or error message
 *         event_status: the current status of the event, on false request will return null
 *         partner_info: if event is paired, returns the event object of the partner, otherwise null
 *                       {id, food, nationality, lname, fname, gender, dietary, dob}
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
                console.log("Date format: "+JSON.stringify(eventA.date));
                let query = 'SELECT uid AS id, food, nationality, lname, fname, gender, dietary, dob FROM List, User WHERE List.uid=' + eventA.partnerid +' AND List.uid = User.id AND List.date ='+JSON.stringify(eventA.date)+';';

                sequelize.query(query,{type: sequelize.QueryTypes.SELECT}).then(function(partner_info){
                    console.log("partner_info is: "+ partner_info);
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


/***
 *  Rematch event
 *  req: id (of the event)
 *  Return status: 0 - fail, 1 - success
 *         msg: success or error message
 *
 ***/ 
router.post('/rematch',function(req, res, next){
    let id = parseInt(req.body.id);
    console.log('Debug: event id is '+id);
    
    List.findOne({where:{id:id}})
        .then(function(event){
                //reset partner's event first
            List.update({status:0, partnerid:null},{where:{uid: event.partnerid, date:event.date}});
//                .then(function(result){
//                /************** matching part for partner starts from here ***************/
//                    console.log("Matching starts for partner:");
//                    //console.log("the result after update is: "+ JSON.stringify(updated));
//                    User.findOne({where:{id:event.partnerid}}) //Using partner by id
//                        .then(function(partner){
//                        let query = 'SELECT List.id AS id, uid FROM List, User WHERE List.id != '+event.id+' AND List.uid = User.id AND User.nationality !=\"'+ partner.nationality+'\" AND status=0 AND date='+ JSON.stringify(event.date) +' LIMIT 1';
//                        sequelize.query(query,{type: sequelize.QueryTypes.SELECT}).then(function(userB){
//                            if(userB.length == 0){
//                                console.log('No new match for the partner.');
//                            }
//                            else{ //update the fields
//                                console.log("UserB: "+userB+" list id "+userB[0].id+", userid "+userB[0].uid);
//                                List.update({status:1, partnerid:userB[0].uid},{where:{uid:event.partnerid, date:event.date}});
//                                List.update({status:1, partnerid:event.partnerid},{where:{id:userB[0].id}});//update userB's event info
//                            }
//                        });
//                    });
//                /************** matching part for partner ends ******************/
//                })
//                .catch(function(err){
//                    console.log("DB error: " + err);
//                    res.json({
//                        status: 0,
//                        msg:'DB error: ' + err
//                    });
//                });

            //reset user's event
            List.update({status:0, partnerid:null},{where:{id:id}})
                .then(function(result){
                /************** matching part for user starts from here ***************/
                    console.log("Matching starts for the original user:");
                    User.findOne({where:{id:event.uid}}) //Using event uid to find owner A
                        .then(function(userA){
                        let query = 'SELECT List.id AS id, uid FROM List, User WHERE List.uid != '+event.partnerid+' AND List.uid = User.id AND User.nationality !=\"'+ userA.nationality+'\" AND status=0 AND date='+ JSON.stringify(event.date) +' LIMIT 1';
                        sequelize.query(query,{type: sequelize.QueryTypes.SELECT}).then(function(userB){
                            if(userB.length == 0){console.log('No new match for the original user.');}
                            else{ //update the fields
                                console.log("User B: "+userB+" list id "+userB[0].id+", userid "+userB[0].uid);
                                List.update({status:1, partnerid:userB[0].uid},{where:{id:event.id}});
                                List.update({status:1, partnerid:event.uid},{where:{id:userB[0].id}});//update B's event info
                            }
                        });
                    });
                /************** matching part for user ends ******************/
                })
                .catch(function(err){
                    console.log("DB error: " + err);
                    res.json({
                        status: 0,
                        msg:'DB error: ' + err
                    });
                });
            console.log("Have reset 2 events.");
        
            res.json({
                status: 1,
                msg:'Success!'
            });
        })
        .catch(function(err){
            console.log("DB error: " + err);
            res.json({
                status: 0,
                msg:'DB error: ' + err
            });
        });
});


module.exports = router;